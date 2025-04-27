package com.sunsuwedding.chat.kafka.consumer;

import com.sunsuwedding.chat.event.ChatMessageSavedEvent;
import com.sunsuwedding.chat.redis.RedisChatRoomStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatRoomMetaUpdaterConsumer {

    private final RedisChatRoomStore redisChatRoomStore;

    @KafkaListener(
            topics = "chat-message-saved",
            groupId = "chat-room-meta-updater-group"
    )
    public void consume(ChatMessageSavedEvent event, Acknowledgment ack) {
        // 1. 메타 정보 업데이트
        redisChatRoomStore.updateChatRoomMeta(
                event.getChatRoomCode(),
                event.getContent(),
                event.getCreatedAt(),
                event.getSequenceId()
        );

        // 2. dirty 저장소에 키 추가
        redisChatRoomStore.markChatRoomMetaAsDirty(event.getChatRoomCode());

        ack.acknowledge();
    }

}
