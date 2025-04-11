package com.sunsuwedding.chat.redis;

public class ChatRedisKeyUtil {

    private static final String PREFIX = "chat";

    public static String sortedRoomKey(Long userId) {
        return PREFIX + ":rooms:" + userId;
    }

    public static String roomMetaKey(String chatRoomId) {
        return PREFIX + ":room:meta:" + chatRoomId;
    }

    public static String userRoomStatusKey(String chatRoomId, Long userId) {
        return PREFIX + ":room:user:" + chatRoomId + ":" + userId;
    }

    public static String userOnlineKey(Long userId) {
        return PREFIX + ":online:" + userId;
    }
}