package com.sunsuwedding.chat.redis;

public class RedisKeyUtil {

    private static final String PREFIX = "chat";

    // 세션 ID → 유저 ID 매핑
    // chat:session:{sessionId} → userId
    public static String sessionToUserKey(String sessionId) {
        return PREFIX + ":session:" + sessionId;
    }

    // 세션 ID → 상대방 유저 ID 매핑 (1:1 채팅에서만 사용)
    // chat:session:partner:{sessionId} → partnerUserId
    public static String sessionToPartnerKey(String sessionId) {
        return PREFIX + ":session:partner:" + sessionId;
    }

    // 유저 접속 서버 ID (유니캐스트 라우팅용)
    // chat:presence:{userId} → chat-server-1
    public static String userPresenceKey(Long userId) {
        return PREFIX + ":presence:" + userId;
    }

    // 유저별 채팅방 목록 (ZSET, score = lastMessageAt)
    // chat:rooms:{userId}
    public static String userChatRoomsKey(Long userId) {
        return PREFIX + ":rooms:" + userId;
    }

    // 채팅방 메타정보 (lastMessage, lastMessageAt, lastMessageSeqId)
    // chat:room:meta:{chatRoomCode}
    public static String roomMetaKey(String chatRoomCode) {
        return PREFIX + ":room:meta:" + chatRoomCode;
    }

    // 채팅방 메시지 시퀀스 ID (Redis INCR로 사용)
    // chat:room:seq:{chatRoomCode} → Long (1부터 시작)
    public static String roomMessageSeqKey(String chatRoomCode) {
        return PREFIX + ":room:seq:" + chatRoomCode;
    }

    // 유저가 마지막으로 읽은 메시지 seq ID (옵션)
    // chat:room:last-read:{chatRoomCode}:{userId}
    public static String lastReadSeqKey(String chatRoomCode, Long userId) {
        return PREFIX + ":room:last-read:" + chatRoomCode + ":" + userId;
    }
}