package com.qzer.scheduler.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlarmChannel {
    
    EMAIL("EMAIL", "邮件", "email"),
    WECHAT("WECHAT", "企业微信", "wechat"),
    DINGTALK("DINGTALK", "钉钉", "dingtalk"),
    SMS("SMS", "短信", "sms"),
    WEBHOOK("WEBHOOK", "Webhook", "webhook");

    private final String code;
    private final String name;
    private final String handler;
    
    public static AlarmChannel fromCode(String code) {
        if (code == null) return null;
        for (AlarmChannel channel : values()) {
            if (channel.code.equals(code)) {
                return channel;
            }
        }
        return null;
    }
}
