package com.sunsuwedding.chat.event.listener;

import com.sunsuwedding.chat.event.message.PresenceStatusEvent;
import com.sunsuwedding.chat.kafka.producer.PresenceStatusProducer;
import com.sunsuwedding.chat.redis.RedisKeyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompDisconnectListener implements ApplicationListener<SessionDisconnectEvent> {

    private final RedisTemplate<String, String> redisTemplate;
    private final PresenceStatusProducer presenceStatusProducer;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();

        if (sessionId == null) {
            log.warn("⚠️ 세션 ID 없음, Disconnect 처리 스킵");
            return;
        }

        String sessionKey = RedisKeyUtil.sessionToUserKey(sessionId);
        String userIdStr = redisTemplate.opsForValue().get(sessionKey);

        if (userIdStr == null) {
            log.warn("⚠️ 해당 세션에 대한 유저 정보 없음: sessionId={}", sessionId);
            return;
        }

        Long userId = Long.valueOf(userIdStr);

        // Redis에서 session → userId 매핑 제거
        redisTemplate.delete(sessionKey);

        // Redis에서 userId → serverId 연결도 제거
        String presenceKey = RedisKeyUtil.userPresenceKey(userId);
        String serverId = redisTemplate.opsForValue().get(presenceKey);
        redisTemplate.delete(presenceKey);

        // Kafka로 전파
        presenceStatusProducer.send(new PresenceStatusEvent(userId, "offline", serverId));
        log.info("🧹 Disconnect 완료: userId={}, sessionId={}", userId, sessionId);
    }
}
