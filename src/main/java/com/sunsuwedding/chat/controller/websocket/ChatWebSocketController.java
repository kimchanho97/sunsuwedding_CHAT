package com.sunsuwedding.chat.controller.websocket;


import com.sunsuwedding.chat.dto.message.ChatMessageRequest;
import com.sunsuwedding.chat.event.ChatMessageRequestEvent;
import com.sunsuwedding.chat.kafka.producer.ChatMessageProducer;
import com.sunsuwedding.chat.redis.RedisChatRoomStore;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatMessageProducer chatMessageProducer;
    private final RedisChatRoomStore redisChatRoomStore;

    @MessageMapping("/chat/rooms/{chatRoomCode}/messages")
    public void send(@DestinationVariable String chatRoomCode, @Payload @Valid ChatMessageRequest message) {
        Long messageSeqId = redisChatRoomStore.nextMessageSeq(chatRoomCode);
        chatMessageProducer.send(ChatMessageRequestEvent.from(message, chatRoomCode, messageSeqId));
    }
}
