package com.sunsuwedding.chat.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunsuwedding.chat.event.ChatMessageSavedEvent;
import com.sunsuwedding.chat.redis.RedisPresenceStore;
import com.sunsuwedding.chat.service.ChatMessageDispatcher;
import com.sunsuwedding.chat.service.ChatRoomParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketPublisherConsumer {

    private final ObjectMapper objectMapper;
    private final RedisPresenceStore redisPresenceStore;
    private final ChatMessageDispatcher chatMessageDispatcher;
    private final ChatRoomParticipantService chatRoomParticipantService;
    private final RedisPresenceStore redisPresenceStore;

    @KafkaListener(topics = "chat-message-saved", groupId = "chat-unicast-group")
    public void consume(String payload) {
        try {
            ChatMessageSavedEvent savedEvent = objectMapper.readValue(payload, ChatMessageSavedEvent.class);
            String chatRoomCode = savedEvent.getChatRoomCode();

            List<Long> participantUserIds = chatRoomParticipantService.getParticipantUserIds(chatRoomCode);
            // 유저별 채팅방 목록 재정렬 이벤트 발행
            for (Long otherUserId : participantUserIds) {

            }

            for (Long otherUserId : participantUserIds) {
                String targetServerUrl = redisPresenceStore.findPresenceServerUrl(otherUserId, chatRoomCode);
                // 유니캐스트 이벤트 발행
            }
        } catch (Exception e) {
        }
    }

}
