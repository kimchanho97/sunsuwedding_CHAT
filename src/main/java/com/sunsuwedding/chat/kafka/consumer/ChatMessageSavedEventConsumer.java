package com.sunsuwedding.chat.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunsuwedding.chat.event.ChatMessageSavedEvent;
import com.sunsuwedding.chat.event.ChatMessageUnicastEvent;
import com.sunsuwedding.chat.event.ChatRoomResortEvent;
import com.sunsuwedding.chat.kafka.producer.ChatMessageUnicastEventPublisher;
import com.sunsuwedding.chat.kafka.producer.ChatRoomResortEventPublisher;
import com.sunsuwedding.chat.redis.RedisPresenceStore;
import com.sunsuwedding.chat.service.ChatRoomParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageSavedEventConsumer {

    private final ObjectMapper objectMapper;
    private final RedisPresenceStore redisPresenceStore;
    private final ChatRoomParticipantService chatRoomParticipantService;
    private final ChatRoomResortEventPublisher chatRoomResortEventPublisher;
    private final ChatMessageUnicastEventPublisher chatMessageUnicastEventPublisher;

    @KafkaListener(topics = "chat-message-saved", groupId = "chat-message-unicast-publisher-group")
    public void handle(String payload) {
        try {
            ChatMessageSavedEvent savedEvent = objectMapper.readValue(payload, ChatMessageSavedEvent.class);
            String chatRoomCode = savedEvent.getChatRoomCode();

            // 1. 참여자 기준 정렬 이벤트 발행
            chatRoomParticipantService.getParticipantUserIds(chatRoomCode).stream()
                    .map(userId -> ChatRoomResortEvent.from(savedEvent, userId))
                    .forEach(chatRoomResortEventPublisher::publish);

            // 2. 온라인 유저 기준 메시지 유니캐스트 이벤트 발행
            Map<Long, String> onlineUsers = redisPresenceStore.findOnlineUsersWithServerUrl(chatRoomCode);
            List<Long> onlineUserIds = new ArrayList<>(onlineUsers.keySet());

            onlineUsers.values().stream()
                    .distinct() // 서버 중복 제거
                    .map(serverUrl -> ChatMessageUnicastEvent.from(savedEvent, serverUrl, onlineUserIds))
                    .forEach(chatMessageUnicastEventPublisher::publish);

        } catch (Exception e) {
            log.error("❌ chat-message-saved 후속 이벤트 처리 실패", e);
        }
    }
}