package com.qzer.scheduler.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.modules.system.dto.PermissionCreateRequest;
import com.qzer.scheduler.modules.system.dto.PermissionUpdateRequest;
import com.qzer.scheduler.modules.system.entity.Permission;
import com.qzer.scheduler.modules.system.entity.RolePermission;
import com.qzer.scheduler.modules.system.entity.UserRole;
import com.qzer.scheduler.modules.system.mapper.PermissionMapper;
import com.qzer.scheduler.modules.system.mapper.RolePermissionMapper;
import com.qzer.scheduler.modules.system.mapper.UserRoleMapper;
import com.qzer.scheduler.modules.system.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionMapper permissionMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final UserRoleMapper userRoleMapper;

    @Override
    @Transactional
    public Permission createPermission(PermissionCreateRequest request) {
        Permission permission = new Permission();
        permission.setPermissionName(request.getPermissionName());
        permission.setPermissionCode(request.getPermissionCode());
        permission.setPermissionType(request.getPermissionType());
        permission.setParentId(request.getParentId() != null ? request.getParentId() : 0L);
        permission.setPath(request.getPath());
        permission.setIcon(request.getIcon());
        permission.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        permission.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        permission.setCreateTime(LocalDateTime.now());
        permission.setUpdateTime(LocalDateTime.now());
        permissionMapper.insert(permission);
        log.info("创建权限成功: {}", permission.getId());
        return permission;
    }

    @Override
    @Transactional
    public Permission updatePermission(Long id, PermissionUpdateRequest request) {
        Permission permission = permissionMapper.selectById(id);
        if (permission == null) {
            throw new RuntimeException("权限不存在");
        }
        if (StringUtils.hasText(request.getPermissionName())) {
            permission.setPermissionName(request.getPermissionName());
        }
        if (StringUtils.hasText(request.getPermissionCode())) {
            permission.setPermissionCode(request.getPermissionCode());
        }
        if (StringUtils.hasText(request.getPermissionType())) {
            permission.setPermissionType(request.getPermissionType());
        }
        if (request.getParentId() != null) {
            permission.setParentId(request.getParentId());
        }
        if (StringUtils.hasText(request.getPath())) {
            permission.setPath(request.getPath());
        }
        if (StringUtils.hasText(request.getIcon())) {
            permission.setIcon(request.getIcon());
        }
        if (request.getSortOrder() != null) {
            permission.setSortOrder(request.getSortOrder());
        }
        if (request.getStatus() != null) {
            permission.setStatus(request.getStatus());
        }
        permission.setUpdateTime(LocalDateTime.now());
        permissionMapper.updateById(permission);
        log.info("更新权限成功: {}", id);
        return permission;
    }

    @Override
    @Transactional
    public void deletePermission(Long id) {
        permissionMapper.deleteById(id);
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getPermissionId, id);
        rolePermissionMapper.delete(wrapper);
        log.info("删除权限成功: {}", id);
    }

    @Override
    public Permission getPermissionById(Long id) {
        return permissionMapper.selectById(id);
    }

    @Override
    public IPage<Permission> listPermissions(Page<Permission> page, String permissionName, Integer status) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(permissionName)) {
            wrapper.like(Permission::getPermissionName, permissionName);
        }
        if (status != null) {
            wrapper.eq(Permission::getStatus, status);
        }
        wrapper.orderByAsc(Permission::getSortOrder).orderByDesc(Permission::getCreateTime);
        return permissionMapper.selectPage(page, wrapper);
    }

    @Override
    public List<Permission> getAllPermissions() {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getStatus, 1);
        wrapper.orderByAsc(Permission::getSortOrder).orderByDesc(Permission::getCreateTime);
        return permissionMapper.selectList(wrapper);
    }

    @Override
    public List<Permission> getPermissionTree() {
        List<Permission> allPermissions = getAllPermissions();
        return buildPermissionTree(allPermissions, 0L);
    }

    private List<Permission> buildPermissionTree(List<Permission> permissions, Long parentId) {
        List<Permission> tree = new ArrayList<>();
        for (Permission permission : permissions) {
            if (parentId.equals(permission.getParentId())) {
                permission.setChildren(buildPermissionTree(permissions, permission.getId()));
                tree.add(permission);
            }
        }
        return tree;
    }

    @Override
    public List<Permission> getPermissionsByRoleId(Long roleId) {
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, roleId);
        List<RolePermission> rps = rolePermissionMapper.selectList(wrapper);
        List<Long> permissionIds = rps.stream().map(RolePermission::getPermissionId).collect(Collectors.toList());
        if (permissionIds.isEmpty()) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<Permission> permWrapper = new LambdaQueryWrapper<>();
        permWrapper.in(Permission::getId, permissionIds);
        return permissionMapper.selectList(permWrapper);
    }

    @Override
    public List<Permission> getPermissionsByUserId(Long userId) {
        LambdaQueryWrapper<UserRole> urWrapper = new LambdaQueryWrapper<>();
        urWrapper.eq(UserRole::getUserId, userId);
        List<UserRole> urs = userRoleMapper.selectList(urWrapper);
        List<Long> roleIds = urs.stream().map(UserRole::getRoleId).collect(Collectors.toList());
        if (roleIds.isEmpty()) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<RolePermission> rpWrapper = new LambdaQueryWrapper<>();
        rpWrapper.in(RolePermission::getRoleId, roleIds);
        List<RolePermission> rps = rolePermissionMapper.selectList(rpWrapper);
        List<Long> permissionIds = rps.stream().map(RolePermission::getPermissionId).distinct().collect(Collectors.toList());
        if (permissionIds.isEmpty()) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<Permission> permWrapper = new LambdaQueryWrapper<>();
        permWrapper.in(Permission::getId, permissionIds);
        permWrapper.eq(Permission::getStatus, 1);
        return permissionMapper.selectList(permWrapper);
    }
}
