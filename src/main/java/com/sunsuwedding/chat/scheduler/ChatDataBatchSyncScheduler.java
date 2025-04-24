package com.sunsuwedding.chat.scheduler;

import com.sunsuwedding.chat.client.internal.ChatDataBatchSyncClient;
import com.sunsuwedding.chat.dto.sync.ChatReadSeqSyncRequest;
import com.sunsuwedding.chat.dto.sync.ChatRoomMetaSyncRequest;
import com.sunsuwedding.chat.redis.RedisChatReadStore;
import com.sunsuwedding.chat.redis.RedisChatRoomStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatDataBatchSyncScheduler {

    private final RedisChatRoomStore redisChatRoomStore;
    private final RedisChatReadStore redisChatReadStore;
    private final ChatDataBatchSyncClient chatDataBatchSyncClient;

    private static final int CHUNK_SIZE = 100;

    @Scheduled(fixedDelay = 3 * 60 * 1000) // 3분마다 실행
    public void syncDirtyData() {
        syncChatRoomMeta();
        syncLastReadSequences();
    }

    private void syncChatRoomMeta() {
        Set<String> chatRoomCodes = redisChatRoomStore.getDirtyChatRoomMetaCodes();
        if (chatRoomCodes == null || chatRoomCodes.isEmpty()) return;

        List<String> chunk = chatRoomCodes.stream().limit(CHUNK_SIZE).toList();

        List<ChatRoomMetaSyncRequest> requests = chunk.stream()
                .map(chatRoomCode -> {
                    Map<String, String> meta = redisChatRoomStore.getChatRoomMeta(chatRoomCode);

                    // 1. UTC 문자열 → LocalDateTime
                    LocalDateTime utcDateTime = LocalDateTime.parse(meta.get("lastMessageAt"));

                    // 2. UTC → KST 변환
                    LocalDateTime kstDateTime = utcDateTime
                            .atZone(ZoneOffset.UTC)
                            .withZoneSameInstant(ZoneId.of("Asia/Seoul"))
                            .toLocalDateTime();

                    // 3. ChatRoomMetaSyncRequest에 KST 문자열로 담기
                    return new ChatRoomMetaSyncRequest(
                            chatRoomCode,
                            meta.get("lastMessage"),
                            kstDateTime.toString(),
                            meta.get("lastMessageSeqId")
                    );
                })
                .toList();

        chatDataBatchSyncClient.syncChatRoomMetaBatch(requests);
        redisChatRoomStore.removeDirtyChatRoomMetaCodes(chunk);
    }

    private void syncLastReadSequences() {
        Set<String> dirtyKeys = redisChatReadStore.getDirtyLastReadKeys();
        if (dirtyKeys == null || dirtyKeys.isEmpty()) return;

        List<ChatReadSeqSyncRequest> requests = dirtyKeys.stream()
                .limit(CHUNK_SIZE)
                .map(key -> {
                    String[] parts = key.split(":");
                    String chatRoomCode = parts[0];
                    Long userId = Long.parseLong(parts[1]);
                    Long seqId = redisChatReadStore.getLastReadSequence(chatRoomCode, userId);
                    return new ChatReadSeqSyncRequest(chatRoomCode, userId, seqId);
                })
                .toList();

        chatDataBatchSyncClient.syncLastReadSequences(requests);
        redisChatReadStore.removeDirtyLastReadKeys(requests);
    }

}
