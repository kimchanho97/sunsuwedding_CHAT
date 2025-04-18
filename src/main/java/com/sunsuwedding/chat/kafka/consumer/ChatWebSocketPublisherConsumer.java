package com.sunsuwedding.chat.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunsuwedding.chat.dto.message.ChatMessageUnicastDto;
import com.sunsuwedding.chat.event.message.ChatMessageSavedEvent;
import com.sunsuwedding.chat.redis.RedisPresenceStore;
import com.sunsuwedding.chat.service.ChatMessageDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketPublisherConsumer {

    private final ObjectMapper objectMapper;
    private final RedisPresenceStore redisPresenceStore;
    private final ChatMessageDispatcher chatMessageDispatcher;

    @KafkaListener(topics = "chat-message-saved", groupId = "chat-unicast-group")
    public void consume(String payload) {
        try {
            ChatMessageSavedEvent savedEvent = objectMapper.readValue(payload, ChatMessageSavedEvent.class);
            String chatRoomCode = savedEvent.getChatRoomCode();

            // 1. 접속 중인 유저 → 서버 매핑 (sender 포함)
            Map<Long, String> onlineUsers = redisPresenceStore.findOnlineUsersWithServerId(chatRoomCode);

            // 2. 전송용 DTO 생성
            ChatMessageUnicastDto unicastDto = ChatMessageUnicastDto.from(savedEvent, new ArrayList<>(onlineUsers.keySet()));

            // 3. 유니캐스트로 전체 서버 전송
            chatMessageDispatcher.dispatch(onlineUsers, unicastDto);
        } catch (Exception e) {
            log.error("❌ WebSocket 유니캐스트 전송 실패", e);
        }
    }

}
