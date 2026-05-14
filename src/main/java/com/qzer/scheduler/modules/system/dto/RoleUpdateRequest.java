package com.qzer.scheduler.modules.system.dto;

import lombok.Data;

@Data
public class RoleUpdateRequest {
    private String roleName;
    private String roleCode;
    private String description;
    private Integer status;
}
