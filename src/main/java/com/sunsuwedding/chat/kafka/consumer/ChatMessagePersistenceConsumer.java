package com.sunsuwedding.chat.kafka.consumer;

import com.sunsuwedding.chat.event.ChatMessageRequestEvent;
import com.sunsuwedding.chat.event.ChatMessageSavedEvent;
import com.sunsuwedding.chat.kafka.producer.ChatMessageSavedEventProducer;
import com.sunsuwedding.chat.model.ChatMessageDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessagePersistenceConsumer {

    private final MongoTemplate mongoTemplate;
    private final ChatMessageSavedEventProducer chatMessageSavedEventProducer;

    @KafkaListener(
            topics = "chat-message",
            groupId = "chat-message-persistence-group",
            containerFactory = "transactionalFactory"
    )
    public void consume(ChatMessageRequestEvent event) {
        // MongoDB 저장
        ChatMessageDocument savedDocument = upsertWithFindAndModify(event.toDocument());

        // 후속 이벤트 발행
        ChatMessageSavedEvent savedEvent = ChatMessageSavedEvent.from(savedDocument);
        chatMessageSavedEventProducer.sendTransactional(savedEvent);
    }

    private ChatMessageDocument upsertWithFindAndModify(ChatMessageDocument doc) {
        Query query = new Query(Criteria.where("_id").is(doc.getId()));

        Update update = new Update()
                .setOnInsert("_id", doc.getId())
                .setOnInsert("chatRoomCode", doc.getChatRoomCode())
                .setOnInsert("senderId", doc.getSenderId())
                .setOnInsert("senderName", doc.getSenderName())
                .setOnInsert("content", doc.getContent())
                .setOnInsert("messageType", doc.getMessageType())
                .setOnInsert("createdAt", doc.getCreatedAt())
                .setOnInsert("messageSeqId", doc.getMessageSeqId())
                .setOnInsert("fileName", doc.getFileName())
                .setOnInsert("fileUrl", doc.getFileUrl());

        // FindAndModifyOptions를 사용하여 단일 연산으로 upsert 및 결과 조회
        return mongoTemplate.findAndModify(
                query,
                update,
                FindAndModifyOptions.options().upsert(true).returnNew(true),
                ChatMessageDocument.class);
    }
}
