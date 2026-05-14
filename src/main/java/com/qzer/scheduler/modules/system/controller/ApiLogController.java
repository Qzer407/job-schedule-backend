package com.qzer.scheduler.modules.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.common.dto.response.ApiResponse;
import com.qzer.scheduler.modules.system.entity.ApiLog;
import com.qzer.scheduler.modules.system.service.ApiLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/v1/system/apilog")
@RequiredArgsConstructor
public class ApiLogController {

    private final ApiLogService apiLogService;

    @GetMapping("/{id}")
    public ApiResponse<ApiLog> getApiLogById(@PathVariable Long id) {
        return ApiResponse.success(apiLogService.getApiLogById(id));
    }

    @GetMapping("/page")
    public ApiResponse<IPage<ApiLog>> listApiLogs(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long apiKeyId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String endpoint,
            @RequestParam(required = false) String method,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        Page<ApiLog> page = new Page<>(current, size);
        return ApiResponse.success(apiLogService.listApiLogs(page, apiKeyId, userId, endpoint, method, startTime, endTime));
    }

    @DeleteMapping("/old")
    public ApiResponse<Void> deleteOldLogs(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime before) {
        apiLogService.deleteOldLogs(before);
        return ApiResponse.success(null);
    }
}
