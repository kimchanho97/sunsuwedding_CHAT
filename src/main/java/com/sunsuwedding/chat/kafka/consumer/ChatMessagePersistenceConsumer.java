package com.sunsuwedding.chat.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunsuwedding.chat.event.ChatMessageRequestEvent;
import com.sunsuwedding.chat.event.ChatMessageSavedEvent;
import com.sunsuwedding.chat.kafka.producer.ChatMessageSavedEventProducer;
import com.sunsuwedding.chat.model.ChatMessageDocument;
import com.sunsuwedding.chat.redis.RedisChatRoomStore;
import com.sunsuwedding.chat.repository.ChatMessageMongoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChatMessagePersistenceConsumer {

    private final ChatMessageMongoRepository mongoRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final ChatMessageSavedEventProducer chatMessageSavedEventProducer;
    private final RedisChatRoomStore redisChatRoomStore;

    public ChatMessagePersistenceConsumer(
            ChatMessageMongoRepository mongoRepository,
            @Qualifier("transactionalKafkaTemplate") KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            ChatMessageSavedEventProducer chatMessageSavedEventProducer,
            RedisChatRoomStore redisChatRoomStore
    ) {
        this.mongoRepository = mongoRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.chatMessageSavedEventProducer = chatMessageSavedEventProducer;
        this.redisChatRoomStore = redisChatRoomStore;
    }

    @KafkaListener(topics = "chat-message", groupId = "chat-persistence-group")
    public void consume(String payload, Acknowledgment ack) {
        try {
            ChatMessageRequestEvent request = objectMapper.readValue(payload, ChatMessageRequestEvent.class);
            kafkaTemplate.executeInTransaction(template -> {
                // 1. Redis에서 시퀀스 증가
                Long messageSeqId = redisChatRoomStore.nextMessageSeq(request.getChatRoomCode());

                // 2. MongoDB 저장
                ChatMessageDocument savedDocument = mongoRepository.save(request.toDocument(messageSeqId));

                // 3. 후속 이벤트 발행
                ChatMessageSavedEvent event = ChatMessageSavedEvent.from(savedDocument);
                chatMessageSavedEventProducer.sendTransactional(template, event);
                return true;
            });

            ack.acknowledge();
        } catch (Exception e) {
            log.error("❌ chat-response 컨슘 실패", e);
        }
    }
}
