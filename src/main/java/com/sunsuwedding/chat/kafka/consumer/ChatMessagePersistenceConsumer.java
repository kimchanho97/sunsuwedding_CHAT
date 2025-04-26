package com.sunsuwedding.chat.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunsuwedding.chat.event.ChatMessageRequestEvent;
import com.sunsuwedding.chat.event.ChatMessageSavedEvent;
import com.sunsuwedding.chat.kafka.producer.ChatMessageSavedEventProducer;
import com.sunsuwedding.chat.model.ChatMessageDocument;
import com.sunsuwedding.chat.redis.RedisChatRoomStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChatMessagePersistenceConsumer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final ChatMessageSavedEventProducer chatMessageSavedEventProducer;
    private final RedisChatRoomStore redisChatRoomStore;
    private final MongoTemplate mongoTemplate;

    public ChatMessagePersistenceConsumer(
            @Qualifier("transactionalKafkaTemplate") KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            ChatMessageSavedEventProducer chatMessageSavedEventProducer,
            RedisChatRoomStore redisChatRoomStore,
            MongoTemplate mongoTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.chatMessageSavedEventProducer = chatMessageSavedEventProducer;
        this.redisChatRoomStore = redisChatRoomStore;
        this.mongoTemplate = mongoTemplate;
    }

    @KafkaListener(topics = "chat-message", groupId = "chat-persistence-group")
    public void consume(String payload, Acknowledgment ack) {
        try {
            ChatMessageRequestEvent request = objectMapper.readValue(payload, ChatMessageRequestEvent.class);
            // 1. Redis에서 시퀀스 증가
            Long messageSeqId = redisChatRoomStore.nextMessageSeq(request.getChatRoomCode());
            // 2. MongoDB 저장
            ChatMessageDocument savedDocument = upsertAndGet(request.toDocument(messageSeqId));

            kafkaTemplate.executeInTransaction(template -> {
                // 3. 후속 이벤트 발행
                ChatMessageSavedEvent event = ChatMessageSavedEvent.from(savedDocument);
                chatMessageSavedEventProducer.sendTransactional(template, event);
                // 4. Kafka Consumer offset 커밋
                ack.acknowledge();
                return true;
            });

        } catch (Exception e) {
            log.error("❌ ChatMessageRequestEvent 처리 실패", e);
        }
    }

    private ChatMessageDocument upsertAndGet(ChatMessageDocument doc) {
        Query query = new Query();
        query.addCriteria(Criteria.where("chatRoomCode").is(doc.getChatRoomCode())
                .and("messageSeqId").is(doc.getMessageSeqId()));

        Update update = new Update()
                .setOnInsert("chatRoomCode", doc.getChatRoomCode())
                .setOnInsert("senderId", doc.getSenderId())
                .setOnInsert("senderName", doc.getSenderName())
                .setOnInsert("content", doc.getContent())
                .setOnInsert("messageType", doc.getMessageType())
                .setOnInsert("createdAt", doc.getCreatedAt())
                .setOnInsert("messageSeqId", doc.getMessageSeqId())
                .setOnInsert("fileName", doc.getFileName())
                .setOnInsert("fileUrl", doc.getFileUrl());

        mongoTemplate.upsert(query, update, ChatMessageDocument.class);
        return mongoTemplate.findOne(query, ChatMessageDocument.class);
    }

}
