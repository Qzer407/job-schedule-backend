package com.qzer.scheduler.modules.system.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiKeyCreateRequest {
    private Long userId;
    private String keyName;
    private String permissions;
    private Integer rateLimit;
    private LocalDateTime expireTime;
}
