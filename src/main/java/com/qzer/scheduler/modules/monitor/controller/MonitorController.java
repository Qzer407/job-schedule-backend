package com.qzer.scheduler.modules.monitor.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.common.dto.response.ApiResponse;
import com.qzer.scheduler.modules.alarm.entity.AlarmRecord;
import com.qzer.scheduler.modules.monitor.service.MonitorService;
import com.qzer.scheduler.modules.task.entity.TaskLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/monitor")
@RequiredArgsConstructor
public class MonitorController {

    private final MonitorService monitorService;

    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getStatistics() {
        Map<String, Object> statistics = monitorService.getStatistics();
        return ApiResponse.success(statistics);
    }

    @GetMapping("/task/{id}/logs")
    public ApiResponse<IPage<TaskLog>> getTaskLogs(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        Page<TaskLog> page = new Page<>(pageNum, pageSize);
        IPage<TaskLog> result = monitorService.getTaskLogs(page, id, status);
        return ApiResponse.success(result);
    }

    @GetMapping("/alarm")
    public ApiResponse<IPage<AlarmRecord>> getAlarmRecords(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) Long taskId,
            @RequestParam(required = false) Integer status) {
        Page<AlarmRecord> page = new Page<>(pageNum, pageSize);
        IPage<AlarmRecord> result = monitorService.getAlarmRecords(page, taskId, status);
        return ApiResponse.success(result);
    }

    @PutMapping("/alarm/{id}")
    public ApiResponse<Void> handleAlarm(@PathVariable Long id) {
        monitorService.handleAlarm(id);
        return ApiResponse.success(null);
    }
}
