package com.sunsuwedding.chat.kafka.consumer;

import com.sunsuwedding.chat.event.ChatMessageReadSyncBatchEvent;
import com.sunsuwedding.chat.event.ChatMessageSavedEvent;
import com.sunsuwedding.chat.event.ChatMessageUnicastEvent;
import com.sunsuwedding.chat.event.ChatRoomResortBatchEvent;
import com.sunsuwedding.chat.kafka.producer.ChatMessageReadSyncBatchEventProducer;
import com.sunsuwedding.chat.kafka.producer.ChatMessageUnicastEventProducer;
import com.sunsuwedding.chat.kafka.producer.ChatRoomResortBatchEventProducer;
import com.sunsuwedding.chat.redis.RedisPresenceStore;
import com.sunsuwedding.chat.service.ChatRoomParticipantQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageSavedEventConsumer {

    private final RedisPresenceStore redisPresenceStore;
    private final ChatRoomParticipantQueryService chatRoomParticipantQueryService;
    private final ChatRoomResortBatchEventProducer chatRoomResortBatchEventProducer;
    private final ChatMessageUnicastEventProducer chatMessageUnicastEventProducer;
    private final ChatMessageReadSyncBatchEventProducer chatMessageReadSyncBatchEventProducer;

    @KafkaListener(
            topics = "chat-message-saved",
            groupId = "chat-message-saved-dispatcher-group",
            containerFactory = "readCommittedFactory"
    )
    public void consume(ChatMessageSavedEvent event, Acknowledgment ack) {
        String chatRoomCode = event.getChatRoomCode();
        List<Long> participantUserIds = chatRoomParticipantQueryService.getParticipantUserIds(chatRoomCode);

        // 1. 참여자 기준 채팅방 정렬 이벤트 발행
        chatRoomResortBatchEventProducer.send(ChatRoomResortBatchEvent.from(event, participantUserIds));

        // 2. 온라인 유저 기준 메시지 유니캐스트 이벤트 발행
        Map<Long, String> onlineUsers = redisPresenceStore.findOnlineUsersWithServerUrl(chatRoomCode);
        List<Long> onlineUserIds = new ArrayList<>(onlineUsers.keySet());
        onlineUsers.values().stream()
                .distinct() // 서버 URL 중복 제거
                .map(serverUrl -> ChatMessageUnicastEvent.from(event, serverUrl, onlineUserIds))
                .forEach(chatMessageUnicastEventProducer::send);

        // 3. 온라인 유저 기준 읽음 동기화 이벤트 발행
        chatMessageReadSyncBatchEventProducer.send(ChatMessageReadSyncBatchEvent.from(event, onlineUserIds));

        ack.acknowledge();
    }
}