package com.sunsuwedding.chat.redis;

public class RedisKeyUtil {

    private static final String PREFIX = "chat";

    /**
     * 세션 → 유저 ID 매핑
     * ex) chat:session:3fa85f64 → 32
     */
    public static String sessionToUserKey(String sessionId) {
        return PREFIX + ":session:" + sessionId;
    }

    /**
     * 유저 ID → 서버 ID 매핑 (유니캐스트 라우팅용), 접속 중인 서버 ID
     * ex) chat:presence:32 → chat-server-1
     */
    public static String userPresenceKey(Long userId) {
        return PREFIX + ":presence:" + userId;
    }

    /**
     * 유저가 참여 중인 채팅방 목록 (zset)
     */
    public static String sortedRoomKey(Long userId) {
        return PREFIX + ":rooms:" + userId;
    }

    /**
     * 채팅방 메타 정보 (마지막 메시지, 시간 등)
     */
    public static String roomMetaKey(String chatRoomCode) {
        return PREFIX + ":room:meta:" + chatRoomCode;
    }

    /**
     * 유저의 특정 채팅방에서의 상태 정보 (읽음 여부 등)
     */
    public static String userRoomStatusKey(String chatRoomCode, Long userId) {
        return PREFIX + ":room:user:" + chatRoomCode + ":" + userId;
    }
}