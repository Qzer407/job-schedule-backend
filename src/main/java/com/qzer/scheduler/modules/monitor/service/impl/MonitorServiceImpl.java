package com.qzer.scheduler.modules.monitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qzer.scheduler.common.utils.RedisUtil;
import com.qzer.scheduler.modules.alarm.entity.AlarmRecord;
import com.qzer.scheduler.modules.alarm.mapper.AlarmRecordMapper;
import com.qzer.scheduler.modules.monitor.service.MonitorService;
import com.qzer.scheduler.modules.task.entity.TaskInfo;
import com.qzer.scheduler.modules.task.entity.TaskLog;
import com.qzer.scheduler.modules.task.mapper.TaskInfoMapper;
import com.qzer.scheduler.modules.task.mapper.TaskLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MonitorServiceImpl implements MonitorService {

    private final TaskLogMapper taskLogMapper;
    private final AlarmRecordMapper alarmRecordMapper;
    private final TaskInfoMapper taskInfoMapper;
    private final RedisUtil redisUtil;
    private final ObjectMapper objectMapper;

    private static final String STATISTICS_CACHE_KEY = "monitor:statistics";
    private static final long CACHE_EXPIRE_TIME = 5;

    @Override
    public Map<String, Object> getStatistics() {
        Object cached = redisUtil.get(STATISTICS_CACHE_KEY);
        if (cached != null) {
            return objectMapper.convertValue(cached, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
        }
        Map<String, Object> statistics = new HashMap<>();

        long totalTaskCount = taskInfoMapper.selectCount(null);

        LambdaQueryWrapper<TaskInfo> runningWrapper = new LambdaQueryWrapper<>();
        runningWrapper.eq(TaskInfo::getStatus, 1);
        long runningTaskCount = taskInfoMapper.selectCount(runningWrapper);

        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        LambdaQueryWrapper<TaskLog> logWrapper = new LambdaQueryWrapper<>();
        logWrapper.ge(TaskLog::getTriggerTime, todayStart);
        logWrapper.le(TaskLog::getTriggerTime, todayEnd);
        long todayExecuteCount = taskLogMapper.selectCount(logWrapper);

        logWrapper.clear();
        logWrapper.ge(TaskLog::getTriggerTime, todayStart);
        logWrapper.le(TaskLog::getTriggerTime, todayEnd);
        logWrapper.eq(TaskLog::getExecutionStatus, 0);
        long todayFailCount = taskLogMapper.selectCount(logWrapper);

        logWrapper.clear();
        logWrapper.ge(TaskLog::getTriggerTime, todayStart);
        logWrapper.le(TaskLog::getTriggerTime, todayEnd);
        logWrapper.eq(TaskLog::getExecutionStatus, 1);
        Double avgDuration = taskLogMapper.selectList(logWrapper).stream()
                .mapToLong(TaskLog::getDuration)
                .average()
                .orElse(0);

        double successRate = todayExecuteCount > 0 ?
                ((todayExecuteCount - todayFailCount) * 100.0 / todayExecuteCount) : 100;

        statistics.put("totalTaskCount", totalTaskCount);
        statistics.put("runningTaskCount", runningTaskCount);
        statistics.put("successRate", Math.round(successRate * 100.0) / 100.0);
        statistics.put("avgDuration", Math.round(avgDuration));
        statistics.put("todayExecuteCount", todayExecuteCount);
        statistics.put("todayFailCount", todayFailCount);

        redisUtil.set(STATISTICS_CACHE_KEY, statistics, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        return statistics;
    }

    @Override
    public IPage<TaskLog> getTaskLogs(Page<TaskLog> page, Long taskId, Integer status) {
        LambdaQueryWrapper<TaskLog> wrapper = new LambdaQueryWrapper<>();
        if (taskId != null) {
            wrapper.eq(TaskLog::getTaskId, taskId);
        }
        if (status != null) {
            wrapper.eq(TaskLog::getExecutionStatus, status);
        }
        wrapper.orderByDesc(TaskLog::getTriggerTime);
        return taskLogMapper.selectPage(page, wrapper);
    }

    @Override
    public IPage<AlarmRecord> getAlarmRecords(Page<AlarmRecord> page, Long taskId, Integer status) {
        LambdaQueryWrapper<AlarmRecord> wrapper = new LambdaQueryWrapper<>();
        if (taskId != null) {
            wrapper.eq(AlarmRecord::getTaskId, taskId);
        }
        if (status != null) {
            wrapper.eq(AlarmRecord::getSendStatus, status);
        }
        wrapper.orderByDesc(AlarmRecord::getAlarmTime);
        return alarmRecordMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional
    public void handleAlarm(Long id) {
        AlarmRecord alarmRecord = alarmRecordMapper.selectById(id);
        if (alarmRecord == null) {
            throw new RuntimeException("告警记录不存在");
        }
        alarmRecord.setSendStatus(1);
        alarmRecordMapper.updateById(alarmRecord);
        log.info("处理告警成功: {}", id);
    }
}
