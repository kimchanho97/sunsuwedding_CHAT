package com.sunsuwedding.chat.repository;

import com.sunsuwedding.chat.domain.ChatMessageDocument;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageMongoRepository extends MongoRepository<ChatMessageDocument, String> {

    Slice<ChatMessageDocument> findByChatRoomCodeOrderByCreatedAtDesc(String chatRoomCode, Pageable pageable);
}
