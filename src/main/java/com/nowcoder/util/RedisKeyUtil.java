package com.nowcoder.util;

public class RedisKeyUtil {
    public static String SPILT=":";
    public  static String BIZ_LIKE="LIKE";
    public  static String BIZ_DISLIKE="DISLIKE";
    private static String BIZ_EVENT = "EVENT";

    public static String getEventQueueKey() {
        return BIZ_EVENT;
    }

    public static String getLikeKey(int entityType,int entityId){
        return BIZ_LIKE+SPILT+String.valueOf(entityType)+SPILT+String.valueOf(entityId);
    }
    public static String getDisLikeKey(int entityType,int entityId ){
        return BIZ_DISLIKE+SPILT+String.valueOf(entityType)+SPILT+String.valueOf(entityId);
    }

}
