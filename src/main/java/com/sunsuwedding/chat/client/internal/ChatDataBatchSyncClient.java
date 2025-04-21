package com.sunsuwedding.chat.client.internal;

import com.sunsuwedding.chat.common.exception.CustomException;
import com.sunsuwedding.chat.dto.sync.ChatReadSeqSyncRequest;
import com.sunsuwedding.chat.dto.sync.ChatRoomMetaSyncRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.sunsuwedding.chat.common.exception.ChatErrorCode.CHAT_ROOM_META_SYNC_FAILED;
import static com.sunsuwedding.chat.common.exception.ChatErrorCode.LAST_READ_SYNC_FAILED;

@Component
@RequiredArgsConstructor
public class ChatDataBatchSyncClient {

    private final RestTemplate restTemplate;

    private static final String BATCH_PATH = "/internal/batch";

    @Value("${backend.api.base-url}")
    private String baseUrl;

    public void syncChatRoomMetaBatch(List<ChatRoomMetaSyncRequest> requests) {
        String url = baseUrl + BATCH_PATH + "/chat-room-meta";
        try {
            restTemplate.postForEntity(url, requests, Void.class);
        } catch (RestClientException e) {
            throw new CustomException(CHAT_ROOM_META_SYNC_FAILED);
        }
    }

    public void syncLastReadSequences(List<ChatReadSeqSyncRequest> requests) {
        String url = baseUrl + BATCH_PATH + "/last-read-sequences";
        try {
            restTemplate.postForEntity(url, requests, Void.class);
        } catch (RestClientException e) {
            throw new CustomException(LAST_READ_SYNC_FAILED);
        }
    }
}
