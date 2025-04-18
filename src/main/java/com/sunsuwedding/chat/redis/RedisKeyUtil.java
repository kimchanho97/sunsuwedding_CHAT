package com.sunsuwedding.chat.redis;

public class RedisKeyUtil {

    private static final String PREFIX = "chat";

    // 채팅방 참여자 목록
    // chat:room:members:{chatRoomCode} → Set<Long> (userId1, userId2, ...)
    public static String chatRoomMembersKey(String chatRoomCode) {
        return PREFIX + ":room:members:" + chatRoomCode;
    }

    // 유저별 채팅방 목록 (ZSET, score = lastMessageAt)
    // chat:rooms:{userId}
    public static String userChatRoomsKey(Long userId) {
        return PREFIX + ":rooms:" + userId;
    }

    // 채팅방 메타정보 (lastMessage, lastMessageAt, lastMessageSeqId)
    // chat:room:meta:{chatRoomCode}
    public static String chatRoomMetaKey(String chatRoomCode) {
        return PREFIX + ":room:meta:" + chatRoomCode;
    }

    // 유저가 마지막으로 읽은 메시지 seq ID (옵션)
    // chat:room:last-read:{chatRoomCode}:{userId}
    public static String lastReadSeqKey(String chatRoomCode, Long userId) {
        return PREFIX + ":room:last-read:" + chatRoomCode + ":" + userId;
    }

    // 세션 정보 (userId, chatRoomCode, partnerUserId)
    // chat:session:{sessionId} → userId 31 chatRoomCode abc123 partnerUserId 32
    public static String sessionKey(String sessionId) {
        return PREFIX + ":session:" + sessionId;
    }

    // 유저 접속 서버 ID (유니캐스트 라우팅용)
    // chat:presence:{chatRoomCode}:{userId} = {serverUrl}
    public static String userPresenceKey(String chatRoomCode, Long userId) {
        return PREFIX + ":presence:" + chatRoomCode + ":" + userId;
    }

    // 채팅방 메시지 시퀀스 ID (Redis INCR로 사용)
    // chat:room:seq:{chatRoomCode} → Long (1부터 시작)
    public static String chatRoomMessageSeqKey(String chatRoomCode) {
        return PREFIX + ":room:seq:" + chatRoomCode;
    }
}