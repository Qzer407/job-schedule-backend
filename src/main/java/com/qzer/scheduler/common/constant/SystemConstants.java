package com.qzer.scheduler.common.constant;

public class SystemConstants {

    public static final String DEFAULT_TASK_GROUP = "DEFAULT_GROUP";
    
    public static final String JWT_SECRET = "qzer-scheduler-jwt-secret-key-2026";
    public static final long JWT_EXPIRATION = 86400000L;
    public static final String TOKEN_PREFIX = "Bearer ";
    
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    
    public static final int DEFAULT_RETRY_COUNT = 3;
    public static final int DEFAULT_SHARDING_TOTAL = 1;
    
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    
    public static final String ALARM_SUPPRESSION_KEY_PREFIX = "alarm:suppression:";
    public static final int DEFAULT_SUPPRESSION_MINUTES = 5;
    
    private SystemConstants() {}
}
