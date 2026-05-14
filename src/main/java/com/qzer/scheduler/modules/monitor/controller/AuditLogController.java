
package com.qzer.scheduler.modules.monitor.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.common.dto.response.ApiResponse;
import com.qzer.scheduler.modules.monitor.entity.AuditLog;
import com.qzer.scheduler.modules.monitor.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/audit-log")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping("/{id}")
    public ApiResponse&lt;AuditLog&gt; getAuditLog(@PathVariable Long id) {
        return ApiResponse.success(auditLogService.getById(id));
    }

    @GetMapping
    public ApiResponse&lt;IPage&lt;AuditLog&gt;&gt; listAuditLogs(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) Integer status) {
        Page&lt;AuditLog&gt; page = new Page&lt;&gt;(current, size);
        return ApiResponse.success(auditLogService.listAuditLogs(page, module, status));
    }
}

