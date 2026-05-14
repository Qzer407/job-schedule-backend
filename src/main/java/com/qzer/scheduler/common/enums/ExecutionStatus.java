package com.qzer.scheduler.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExecutionStatus {
    
    PENDING(0, "待执行"),
    RUNNING(1, "执行中"),
    SUCCESS(2, "成功"),
    FAILED(3, "失败");

    private final int code;
    private final String description;
    
    public static ExecutionStatus fromCode(Integer code) {
        if (code == null) return null;
        for (ExecutionStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
