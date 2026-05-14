package com.qzer.scheduler.modules.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.common.dto.response.ApiResponse;
import com.qzer.scheduler.common.utils.ExcelExportUtil;
import com.qzer.scheduler.modules.system.dto.RoleCreateRequest;
import com.qzer.scheduler.modules.system.dto.RoleUpdateRequest;
import com.qzer.scheduler.modules.system.entity.Role;
import com.qzer.scheduler.modules.system.service.RoleService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/system/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final ExcelExportUtil excelExportUtil;

    @PostMapping
    public ApiResponse<Role> createRole(@RequestBody RoleCreateRequest request) {
        return ApiResponse.success(roleService.createRole(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Role> updateRole(@PathVariable Long id, @RequestBody RoleUpdateRequest request) {
        return ApiResponse.success(roleService.updateRole(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}")
    public ApiResponse<Role> getRoleById(@PathVariable Long id) {
        return ApiResponse.success(roleService.getRoleById(id));
    }

    @GetMapping("/page")
    public ApiResponse<IPage<Role>> listRoles(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) Integer status) {
        Page<Role> page = new Page<>(current, size);
        return ApiResponse.success(roleService.listRoles(page, roleName, status));
    }

    @GetMapping("/all")
    public ApiResponse<List<Role>> getAllRoles() {
        return ApiResponse.success(roleService.getAllRoles());
    }

    @PostMapping("/{roleId}/permissions")
    public ApiResponse<Void> assignPermissions(
            @PathVariable Long roleId,
            @RequestBody List<Long> permissionIds) {
        roleService.assignPermissions(roleId, permissionIds);
        return ApiResponse.success(null);
    }

    @GetMapping("/{roleId}/permissions")
    public ApiResponse<List<Long>> getRolePermissions(@PathVariable Long roleId) {
        return ApiResponse.success(roleService.getRolePermissions(roleId));
    }

    @GetMapping("/export")
    public void exportRoles(HttpServletResponse response,
                            @RequestParam(required = false) String roleName,
                            @RequestParam(required = false) Integer status) throws IOException {
        List<Role> roles = roleService.getAllRoles();
        String[] headers = {"ID", "角色名称", "角色编码", "描述", "状态", "创建时间", "更新时间"};
        String[] fieldNames = {"id", "roleName", "roleCode", "description", "status", "createTime", "updateTime"};
        excelExportUtil.exportExcel(response, roles, "roles", "角色列表", headers, fieldNames);
    }
}
