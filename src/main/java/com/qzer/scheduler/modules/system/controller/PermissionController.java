package com.qzer.scheduler.modules.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.common.dto.response.ApiResponse;
import com.qzer.scheduler.modules.system.dto.PermissionCreateRequest;
import com.qzer.scheduler.modules.system.dto.PermissionUpdateRequest;
import com.qzer.scheduler.modules.system.entity.Permission;
import com.qzer.scheduler.modules.system.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/system/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    public ApiResponse<Permission> createPermission(@RequestBody PermissionCreateRequest request) {
        return ApiResponse.success(permissionService.createPermission(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Permission> updatePermission(@PathVariable Long id, @RequestBody PermissionUpdateRequest request) {
        return ApiResponse.success(permissionService.updatePermission(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}")
    public ApiResponse<Permission> getPermissionById(@PathVariable Long id) {
        return ApiResponse.success(permissionService.getPermissionById(id));
    }

    @GetMapping("/page")
    public ApiResponse<IPage<Permission>> listPermissions(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String permissionName,
            @RequestParam(required = false) Integer status) {
        Page<Permission> page = new Page<>(current, size);
        return ApiResponse.success(permissionService.listPermissions(page, permissionName, status));
    }

    @GetMapping("/all")
    public ApiResponse<List<Permission>> getAllPermissions() {
        return ApiResponse.success(permissionService.getAllPermissions());
    }

    @GetMapping("/tree")
    public ApiResponse<List<Permission>> getPermissionTree() {
        return ApiResponse.success(permissionService.getPermissionTree());
    }

    @GetMapping("/role/{roleId}")
    public ApiResponse<List<Permission>> getPermissionsByRoleId(@PathVariable Long roleId) {
        return ApiResponse.success(permissionService.getPermissionsByRoleId(roleId));
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<Permission>> getPermissionsByUserId(@PathVariable Long userId) {
        return ApiResponse.success(permissionService.getPermissionsByUserId(userId));
    }
}
