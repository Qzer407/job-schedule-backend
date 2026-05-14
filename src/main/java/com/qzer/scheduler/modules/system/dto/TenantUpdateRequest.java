package com.qzer.scheduler.modules.system.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TenantUpdateRequest {
    private String tenantName;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private Integer status;
    private LocalDateTime expireTime;
    private Integer maxUsers;
    private Integer maxTasks;
}
