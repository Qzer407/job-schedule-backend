package com.qzer.scheduler.modules.alarm.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.common.dto.response.ApiResponse;
import com.qzer.scheduler.modules.alarm.entity.AlarmConfig;
import com.qzer.scheduler.modules.alarm.entity.AlarmRecord;
import com.qzer.scheduler.modules.alarm.service.AlarmService;
import com.qzer.scheduler.modules.monitor.service.MonitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/alarms")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;
    private final MonitorService monitorService;

    @GetMapping("/config")
    public ApiResponse<List<AlarmConfig>> listConfigs() {
        List<AlarmConfig> configs = alarmService.listAllConfigs();
        return ApiResponse.success(configs);
    }

    @GetMapping("/config/{channelType}")
    public ApiResponse<AlarmConfig> getConfigByChannelType(@PathVariable String channelType) {
        AlarmConfig config = alarmService.getConfigByChannelType(channelType);
        return ApiResponse.success(config);
    }

    @PostMapping("/config")
    public ApiResponse<Void> saveConfig(@RequestBody AlarmConfig config) {
        alarmService.saveConfig(config);
        return ApiResponse.success(null);
    }

    @PutMapping("/config/{channelType}")
    public ApiResponse<Void> updateConfig(@PathVariable String channelType, @RequestBody AlarmConfig config) {
        config.setChannelType(channelType);
        alarmService.updateConfig(config);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/config/{channelType}")
    public ApiResponse<Void> deleteConfig(@PathVariable String channelType) {
        AlarmConfig config = alarmService.getConfigByChannelType(channelType);
        if (config != null) {
            alarmService.deleteConfig(config.getId());
        }
        return ApiResponse.success(null);
    }

    @PostMapping("/test/{channelType}")
    public ApiResponse<Void> testAlarmChannel(@PathVariable String channelType) {
        AlarmConfig config = alarmService.getConfigByChannelType(channelType);
        if (config == null) {
            return ApiResponse.error(404, "告警配置不存在");
        }
        alarmService.sendAlarm(0L, "测试任务", "TEST", "这是一条测试告警消息");
        return ApiResponse.success(null);
    }

    @GetMapping("/records")
    public ApiResponse<IPage<AlarmRecord>> getAlarmRecords(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) Long taskId) {
        Page<AlarmRecord> page = new Page<>(pageNum, pageSize);
        IPage<AlarmRecord> result = monitorService.getAlarmRecords(page, taskId, null);
        return ApiResponse.success(result);
    }
}
