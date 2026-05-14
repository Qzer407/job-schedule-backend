package com.qzer.scheduler.modules.alarm.handler;

public interface AlarmChannelHandler {

    String getChannelType();

    void send(String title, String content, String config);

    default boolean isEnabled(String config) {
        return config != null && !config.isEmpty();
    }
}
