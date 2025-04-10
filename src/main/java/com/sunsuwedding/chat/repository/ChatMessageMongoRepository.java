package com.sunsuwedding.chat.repository;

import com.sunsuwedding.chat.domain.ChatMessageDocument;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageMongoRepository extends MongoRepository<ChatMessageDocument, String> {
    
    List<ChatMessageDocument> findByChatRoomIdOrderByCreatedAtAsc(Long chatRoomId, Pageable pageable);
}
