package com.sunsuwedding.chat.service;

public interface PresenceService {
    void handleConnect(Long userId, Long chatPartnerId, String chatRoomCode, String sessionId);

    void handlePing(Long userId, String chatRoomCode, String sessionId);

    void handleDisconnect(String sessionId);
}
