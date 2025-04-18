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
public class ChatWebSocketPublisherConsumer {

    private final ObjectMapper objectMapper;
    private final RedisPresenceStore redisPresenceStore;
    private final ChatRoomParticipantService chatRoomParticipantService;
    private final ChatRoomResortEventPublisher chatRoomResortEventPublisher;
    private final ChatMessageUnicastEventPublisher chatMessageUnicastEventPublisher;

    @KafkaListener(topics = "chat-message-saved", groupId = "chat-unicast-group")
    public void consume(String payload) {
        try {
            ChatMessageSavedEvent savedEvent = objectMapper.readValue(payload, ChatMessageSavedEvent.class);
            String chatRoomCode = savedEvent.getChatRoomCode();

            List<Long> participantUserIds = chatRoomParticipantService.getParticipantUserIds(chatRoomCode);
            // 유저별 채팅방 목록 재정렬 이벤트 발행
            for (Long otherUserId : participantUserIds) {
                ChatRoomResortEvent resortEvent = ChatRoomResortEvent.from(savedEvent, otherUserId);
                chatRoomResortEventPublisher.publish(resortEvent);
            }

            Map<Long, String> onlineUsers = redisPresenceStore.findOnlineUsersWithServerUrl(chatRoomCode);
            List<Long> onlineUserIds = new ArrayList<>(onlineUsers.keySet());

            for (String serverUrl : onlineUsers.values()) {
                ChatMessageUnicastEvent unicastEvent = ChatMessageUnicastEvent.from(savedEvent, serverUrl, onlineUserIds);
                chatMessageUnicastEventPublisher.publish(unicastEvent);
            }


        } catch (Exception e) {
        }
    }

}
