package com.qzer.scheduler.modules.system.dto;

import lombok.Data;

@Data
public class PermissionUpdateRequest {
    private String permissionName;
    private String permissionCode;
    private String permissionType;
    private Long parentId;
    private String path;
    private String icon;
    private Integer sortOrder;
    private Integer status;
}
