package com.qzer.scheduler.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.modules.system.dto.RoleCreateRequest;
import com.qzer.scheduler.modules.system.dto.RoleUpdateRequest;
import com.qzer.scheduler.modules.system.entity.Role;

import java.util.List;

public interface RoleService {
    Role createRole(RoleCreateRequest request);
    Role updateRole(Long id, RoleUpdateRequest request);
    void deleteRole(Long id);
    Role getRoleById(Long id);
    IPage<Role> listRoles(Page<Role> page, String roleName, Integer status);
    List<Role> getAllRoles();
    void assignPermissions(Long roleId, List<Long> permissionIds);
    List<Long> getRolePermissions(Long roleId);
}
