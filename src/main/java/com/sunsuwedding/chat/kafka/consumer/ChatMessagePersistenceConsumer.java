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
                // TODO: 저장은 성공하고, 후속 이벤트 발행(Kafka 전송)이 실패할 수 있어 중복 처리를 방지해야 함
                //  messageSeqId에 unique 인덱스를 추가하고, save → upsert 또는 DuplicateKeyException 처리로 멱등성 보장

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
