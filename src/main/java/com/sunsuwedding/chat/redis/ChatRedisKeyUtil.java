package com.sunsuwedding.chat.redis;

public class ChatRedisKeyUtil {

    private static final String PREFIX = "chat";

    public static String sortedRoomKey(Long userId) {
        return PREFIX + ":rooms:" + userId;
    }

    public static String roomMetaKey(String chatRoomCode) {
        return PREFIX + ":room:meta:" + chatRoomCode;
    }

    public static String userRoomStatusKey(String chatRoomCode, Long userId) {
        return PREFIX + ":room:user:" + chatRoomCode + ":" + userId;
    }

    public static String userOnlineKey(Long userId) {
        return PREFIX + ":online:" + userId;
    }
}