package com.qzer.scheduler.modules.task.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.common.dto.response.ApiResponse;
import com.qzer.scheduler.modules.task.entity.TaskLog;
import com.qzer.scheduler.modules.task.service.TaskLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/logs")
@RequiredArgsConstructor
public class TaskLogController {

    private final TaskLogService taskLogService;

    @GetMapping
    public ApiResponse<IPage<TaskLog>> listLogs(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) Long taskId,
            @RequestParam(required = false) Integer status) {
        Page<TaskLog> page = new Page<>(pageNum, pageSize);
        IPage<TaskLog> result = taskLogService.listLogs(page, taskId, status);
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<TaskLog> getLogById(@PathVariable Long id) {
        TaskLog log = taskLogService.getLogById(id);
        return ApiResponse.success(log);
    }
}
