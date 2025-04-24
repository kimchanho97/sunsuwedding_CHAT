package com.sunsuwedding.chat.init;

import com.sunsuwedding.chat.client.internal.ChatRoomMetaClient;
import com.sunsuwedding.chat.model.ChatRoomMeta;
import com.sunsuwedding.chat.redis.RedisChatRoomStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatRoomSeqInitializer {

    private final ChatRoomMetaClient chatRoomMetaClient;
    private final RedisChatRoomStore redisChatRoomStore;

    @EventListener(ApplicationReadyEvent.class)
    public void restoreSeqIdsOnStartup() {
        Map<String, ChatRoomMeta> chatRooms = chatRoomMetaClient.getAllChatRoomMetas();

        chatRooms.forEach((chatRoomCode, meta) -> {
            Long lastSeq = meta.lastMessageSeqId() != null ? meta.lastMessageSeqId() : 0L;
            redisChatRoomStore.setMessageSeq(chatRoomCode, lastSeq);
        });

        log.info("✅ 채팅방 seqId 복구 완료. 총 {}건", chatRooms.size());
    }
}
