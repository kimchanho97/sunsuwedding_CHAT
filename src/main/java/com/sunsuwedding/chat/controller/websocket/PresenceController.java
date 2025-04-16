package com.sunsuwedding.chat.controller.websocket;

import com.sunsuwedding.chat.dto.presece.PresenceMessage;
import com.sunsuwedding.chat.dto.presece.PresencePingMessage;
import com.sunsuwedding.chat.event.message.PresenceStatusEvent;
import com.sunsuwedding.chat.kafka.producer.PresenceStatusProducer;
import com.sunsuwedding.chat.redis.RedisKeyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.time.Duration;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PresenceController {

    private final RedisTemplate<String, String> redisTemplate;
    private final PresenceStatusProducer presenceStatusProducer;

    private static final Duration TTL = Duration.ofSeconds(180);

    @Value("${chat.server-id}")
    private String serverId;

    @MessageMapping("/presence")
    public void handlePresence(@Payload PresenceMessage message, @Header("simpSessionId") String sessionId) {
        // 1. 세션 정보 저장 (sessionId → userId)
        String sessionKey = RedisKeyUtil.sessionToUserKey(sessionId);
        log.info("[Session] sessionId={} → userId={}", sessionKey, message.getUserId());
        redisTemplate.opsForValue().set(sessionKey, String.valueOf(message.getUserId()), TTL);

        // 2. 접속 상태 저장 (userId → serverId)
        String presenceKey = RedisKeyUtil.userPresenceKey(message.getUserId());
        redisTemplate.opsForValue().set(presenceKey, serverId, TTL);

        // 3. 상대방의 접속 상태 전파
        sendInitialPresenceStatusToUser(message.getChatPartnerId());

        // 4. 상대방에게 online 전파
        PresenceStatusEvent status = new PresenceStatusEvent(message.getUserId(), "online", serverId);
        presenceStatusProducer.send(status);
    }

    private void sendInitialPresenceStatusToUser(Long chatPartnerId) {
        String presenceKey = RedisKeyUtil.userPresenceKey(chatPartnerId);
        String serverId = redisTemplate.opsForValue().get(presenceKey);
        if (serverId != null) {
            log.info("[Initial Presence] chatPartnerId={} → serverId={}", chatPartnerId, serverId);
            presenceStatusProducer.send(new PresenceStatusEvent(chatPartnerId, "online", this.serverId));
        }
    }

    @MessageMapping("/presence/ping")
    public void handlePing(@Payload PresencePingMessage message, @Header("simpSessionId") String sessionId) {
        // 1. 유저 접속 상태 TTL 연장
        String presenceKey = RedisKeyUtil.userPresenceKey(message.getUserId());
        redisTemplate.expire(presenceKey, TTL);

        // 2. 세션 정보 TTL도 함께 연장
        String sessionKey = RedisKeyUtil.sessionToUserKey(sessionId);
        redisTemplate.expire(sessionKey, TTL);
    }

}
