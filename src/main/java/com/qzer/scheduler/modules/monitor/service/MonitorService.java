package com.qzer.scheduler.modules.monitor.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.modules.alarm.entity.AlarmRecord;
import com.qzer.scheduler.modules.task.entity.TaskLog;

import java.util.Map;

public interface MonitorService {

    Map<String, Object> getStatistics();

    IPage<TaskLog> getTaskLogs(Page<TaskLog> page, Long taskId, Integer status);

    IPage<AlarmRecord> getAlarmRecords(Page<AlarmRecord> page, Long taskId, Integer status);

    void handleAlarm(Long id);
}
