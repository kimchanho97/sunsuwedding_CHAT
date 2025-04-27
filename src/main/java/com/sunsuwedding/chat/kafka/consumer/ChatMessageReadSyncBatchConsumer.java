package com.sunsuwedding.chat.kafka.consumer;

import com.sunsuwedding.chat.event.ChatMessageReadSyncBatchEvent;
import com.sunsuwedding.chat.redis.RedisChatReadStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageReadSyncBatchConsumer {

    private final RedisChatReadStore redisChatReadStore;

    @KafkaListener(
            topics = "chat-message-read-sync",
            groupId = "chat-message-read-sync-group"
    )
    public void consume(ChatMessageReadSyncBatchEvent event, Acknowledgment ack) {
        event.userIds().forEach(userId -> {
            // 1. Redis에 읽음 시퀀스 저장
            redisChatReadStore.markMessageAsRead(event.chatRoomCode(), userId, event.messageSequenceId());

            // 2. Redis에 읽음 시퀀스 dirty 저장소에 키 추가
            redisChatReadStore.markLastReadSequenceAsDirty(event.chatRoomCode(), userId);
        });

        ack.acknowledge();
    }
}
