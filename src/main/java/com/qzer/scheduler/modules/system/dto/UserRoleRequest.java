package com.qzer.scheduler.modules.system.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserRoleRequest {
    private Long userId;
    private List<Long> roleIds;
}
