package com.sunsuwedding.chat.kafka.consumer;

import com.sunsuwedding.chat.event.ChatMessageRequestEvent;
import com.sunsuwedding.chat.event.ChatMessageSavedEvent;
import com.sunsuwedding.chat.kafka.producer.ChatMessageSavedEventProducer;
import com.sunsuwedding.chat.model.ChatMessageDocument;
import com.sunsuwedding.chat.redis.RedisChatRoomStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessagePersistenceConsumer {

    private final MongoTemplate mongoTemplate;
    private final RedisChatRoomStore redisChatRoomStore;
    private final ChatMessageSavedEventProducer chatMessageSavedEventProducer;

    @KafkaListener(
            topics = "chat-message",
            groupId = "chat-message-persistence-group",
            containerFactory = "transactionalFactory"
    )
    public void consume(ChatMessageRequestEvent event) {
        // 1. Redis에서 시퀀스 증가
        Long messageSeqId = redisChatRoomStore.nextMessageSeq(event.getChatRoomCode());

        // 2. MongoDB 저장
        ChatMessageDocument savedDocument = upsertAndGet(event.toDocument(messageSeqId));

        // 3. 후속 이벤트 발행
        ChatMessageSavedEvent savedEvent = ChatMessageSavedEvent.from(savedDocument);
        chatMessageSavedEventProducer.sendTransactional(savedEvent);
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
