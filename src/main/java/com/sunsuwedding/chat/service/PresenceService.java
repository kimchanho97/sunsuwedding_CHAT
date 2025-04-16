package com.sunsuwedding.chat.service;

public interface PresenceService {
    void handleConnect(Long userId, Long chatPartnerId, String sessionId);

    void handlePing(Long userId, String sessionId);
}
