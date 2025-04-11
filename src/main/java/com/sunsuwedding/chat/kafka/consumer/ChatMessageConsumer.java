package com.sunsuwedding.chat.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunsuwedding.chat.domain.ChatMessageDocument;
import com.sunsuwedding.chat.dto.message.ChatMessage;
import com.sunsuwedding.chat.repository.ChatMessageMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageConsumer {

    private final ChatMessageMongoRepository mongoRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "chat-message", groupId = "chat-consumer")
    public void consume(String payload, Acknowledgment ack) {
        try {
            log.info("[Kafka Consumer] 메시지 수신: {}", payload);
            ChatMessage message = objectMapper.readValue(payload, ChatMessage.class);

            ChatMessageDocument document = ChatMessageDocument.builder()
                    .chatRoomId(message.getChatRoomId())
                    .senderId(message.getSenderId())
                    .senderName(message.getSenderName())
                    .content(message.getContent())
                    .messageType(message.getMessageType())
                    .createdAt(LocalDateTime.now()) // or message.getCreatedAt()
                    .build();

            mongoRepository.save(document);
            log.info("[MongoDB] 메시지 저장 완료: {}", document);
            ack.acknowledge(); // 수동으로 ack 처리
        } catch (Exception e) {
            log.error("❌ 메시지 파싱 또는 저장 실패", e);
        }
    }
}
