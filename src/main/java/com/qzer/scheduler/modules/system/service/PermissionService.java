package com.qzer.scheduler.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.modules.system.dto.PermissionCreateRequest;
import com.qzer.scheduler.modules.system.dto.PermissionUpdateRequest;
import com.qzer.scheduler.modules.system.entity.Permission;

import java.util.List;

public interface PermissionService {
    Permission createPermission(PermissionCreateRequest request);
    Permission updatePermission(Long id, PermissionUpdateRequest request);
    void deletePermission(Long id);
    Permission getPermissionById(Long id);
    IPage<Permission> listPermissions(Page<Permission> page, String permissionName, Integer status);
    List<Permission> getAllPermissions();
    List<Permission> getPermissionTree();
    List<Permission> getPermissionsByRoleId(Long roleId);
    List<Permission> getPermissionsByUserId(Long userId);
}
