package com.qzer.scheduler.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.modules.system.dto.RoleCreateRequest;
import com.qzer.scheduler.modules.system.dto.RoleUpdateRequest;
import com.qzer.scheduler.modules.system.entity.Role;
import com.qzer.scheduler.modules.system.entity.RolePermission;
import com.qzer.scheduler.modules.system.mapper.RoleMapper;
import com.qzer.scheduler.modules.system.mapper.RolePermissionMapper;
import com.qzer.scheduler.modules.system.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final RolePermissionMapper rolePermissionMapper;

    @Override
    @Transactional
    public Role createRole(RoleCreateRequest request) {
        Role role = new Role();
        role.setRoleName(request.getRoleName());
        role.setRoleCode(request.getRoleCode());
        role.setDescription(request.getDescription());
        role.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        role.setCreateTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        roleMapper.insert(role);
        log.info("创建角色成功: {}", role.getId());
        return role;
    }

    @Override
    @Transactional
    public Role updateRole(Long id, RoleUpdateRequest request) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        if (StringUtils.hasText(request.getRoleName())) {
            role.setRoleName(request.getRoleName());
        }
        if (StringUtils.hasText(request.getRoleCode())) {
            role.setRoleCode(request.getRoleCode());
        }
        if (StringUtils.hasText(request.getDescription())) {
            role.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            role.setStatus(request.getStatus());
        }
        role.setUpdateTime(LocalDateTime.now());
        roleMapper.updateById(role);
        log.info("更新角色成功: {}", id);
        return role;
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        roleMapper.deleteById(id);
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, id);
        rolePermissionMapper.delete(wrapper);
        log.info("删除角色成功: {}", id);
    }

    @Override
    public Role getRoleById(Long id) {
        return roleMapper.selectById(id);
    }

    @Override
    public IPage<Role> listRoles(Page<Role> page, String roleName, Integer status) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(roleName)) {
            wrapper.like(Role::getRoleName, roleName);
        }
        if (status != null) {
            wrapper.eq(Role::getStatus, status);
        }
        wrapper.orderByDesc(Role::getCreateTime);
        return roleMapper.selectPage(page, wrapper);
    }

    @Override
    public List<Role> getAllRoles() {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getStatus, 1);
        wrapper.orderByDesc(Role::getCreateTime);
        return roleMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, roleId);
        rolePermissionMapper.delete(wrapper);
        
        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (Long permissionId : permissionIds) {
                RolePermission rp = new RolePermission();
                rp.setRoleId(roleId);
                rp.setPermissionId(permissionId);
                rp.setCreateTime(LocalDateTime.now());
                rolePermissionMapper.insert(rp);
            }
        }
        log.info("分配权限成功，角色ID: {}, 权限数量: {}", roleId, permissionIds != null ? permissionIds.size() : 0);
    }

    @Override
    public List<Long> getRolePermissions(Long roleId) {
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, roleId);
        List<RolePermission> rps = rolePermissionMapper.selectList(wrapper);
        return rps.stream().map(RolePermission::getPermissionId).collect(Collectors.toList());
    }
}
