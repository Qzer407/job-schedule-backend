package com.qzer.scheduler.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TaskStatus {
    
    DISABLED(0, "停用"),
    ENABLED(1, "启用"),
    PAUSED(2, "暂停"),
    TERMINATED(3, "终止");

    private final int code;
    private final String description;
    
    public static TaskStatus fromCode(Integer code) {
        if (code == null) return null;
        for (TaskStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
