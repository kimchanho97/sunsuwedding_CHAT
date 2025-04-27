package com.sunsuwedding.chat.kafka.consumer;

import com.sunsuwedding.chat.event.ChatRoomResortBatchEvent;
import com.sunsuwedding.chat.redis.RedisChatRoomStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatRoomResortBatchEventConsumer {

    private final RedisChatRoomStore redisChatRoomStore;

    @KafkaListener(
            topics = "chat-room-resort",
            groupId = "chat-room-resort-group"
    )
    public void consume(ChatRoomResortBatchEvent event, Acknowledgment ack) {
        event.userIds().forEach(userId ->
                redisChatRoomStore.resortChatRoom(userId, event.chatRoomCode(), event.lastMessageAtMillis())
        );

        ack.acknowledge();
    }

}
