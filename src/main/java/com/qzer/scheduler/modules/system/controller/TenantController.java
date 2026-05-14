package com.qzer.scheduler.modules.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.common.dto.response.ApiResponse;
import com.qzer.scheduler.common.utils.ExcelExportUtil;
import com.qzer.scheduler.modules.system.dto.TenantCreateRequest;
import com.qzer.scheduler.modules.system.dto.TenantUpdateRequest;
import com.qzer.scheduler.modules.system.entity.Tenant;
import com.qzer.scheduler.modules.system.service.TenantService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/system/tenant")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;
    private final ExcelExportUtil excelExportUtil;

    @PostMapping
    public ApiResponse<Tenant> createTenant(@RequestBody TenantCreateRequest request) {
        return ApiResponse.success(tenantService.createTenant(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Tenant> updateTenant(@PathVariable Long id, @RequestBody TenantUpdateRequest request) {
        return ApiResponse.success(tenantService.updateTenant(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTenant(@PathVariable Long id) {
        tenantService.deleteTenant(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}")
    public ApiResponse<Tenant> getTenantById(@PathVariable Long id) {
        return ApiResponse.success(tenantService.getTenantById(id));
    }

    @GetMapping("/page")
    public ApiResponse<IPage<Tenant>> listTenants(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String tenantName,
            @RequestParam(required = false) Integer status) {
        Page<Tenant> page = new Page<>(current, size);
        return ApiResponse.success(tenantService.listTenants(page, tenantName, status));
    }

    @GetMapping("/all")
    public ApiResponse<List<Tenant>> getAllTenants() {
        return ApiResponse.success(tenantService.getAllTenants());
    }

    @GetMapping("/export")
    public void exportTenants(HttpServletResponse response) throws IOException {
        List<Tenant> tenants = tenantService.getAllTenants();
        String[] headers = {"ID", "租户编码", "租户名称", "联系人", "联系电话", "联系邮箱", 
                          "状态", "过期时间", "最大用户数", "最大任务数", "创建时间", "更新时间"};
        String[] fieldNames = {"id", "tenantCode", "tenantName", "contactName", "contactPhone", 
                            "contactEmail", "status", "expireTime", "maxUsers", "maxTasks", 
                            "createTime", "updateTime"};
        excelExportUtil.exportExcel(response, tenants, "tenants", "租户列表", headers, fieldNames);
    }
}
