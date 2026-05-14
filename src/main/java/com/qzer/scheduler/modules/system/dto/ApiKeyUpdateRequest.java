package com.qzer.scheduler.modules.system.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiKeyUpdateRequest {
    private String keyName;
    private String permissions;
    private Integer rateLimit;
    private LocalDateTime expireTime;
    private Integer status;
}
