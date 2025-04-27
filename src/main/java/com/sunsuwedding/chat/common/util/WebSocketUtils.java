package com.sunsuwedding.chat.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Slf4j
public class WebSocketUtils {

    /**
     * 웹소켓으로 메시지 전송
     *
     * @param messagingTemplate 웹소켓 메시징 템플릿
     * @param destination       목적지 토픽
     * @param payload           전송할 메시지
     * @param logContext        로깅용 컨텍스트
     */
    public static void sendMessage(
            SimpMessagingTemplate messagingTemplate,
            String destination,
            Object payload,
            String logContext
    ) {
        try {
            messagingTemplate.convertAndSend(destination, payload);
        } catch (MessageDeliveryException e) {
            log.error("{} 웹소켓 메시지 전송 실패", logContext);
        }
    }
}
