
package com.qzer.scheduler.modules.monitor.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.common.dto.response.ApiResponse;
import com.qzer.scheduler.modules.monitor.entity.ScheduleLog;
import com.qzer.scheduler.modules.monitor.service.ScheduleLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/schedule-log")
@RequiredArgsConstructor
public class ScheduleLogController {

    private final ScheduleLogService scheduleLogService;

    @GetMapping("/{id}")
    public ApiResponse&lt;ScheduleLog&gt; getScheduleLog(@PathVariable Long id) {
        return ApiResponse.success(scheduleLogService.getById(id));
    }

    @GetMapping
    public ApiResponse&lt;IPage&lt;ScheduleLog&gt;&gt; listScheduleLogs(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long taskId,
            @RequestParam(required = false) Integer status) {
        Page&lt;ScheduleLog&gt; page = new Page&lt;&gt;(current, size);
        return ApiResponse.success(scheduleLogService.listScheduleLogs(page, taskId, status));
    }
}

