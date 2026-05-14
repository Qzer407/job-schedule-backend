package com.qzer.scheduler.modules.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.modules.task.entity.TaskLog;
import com.qzer.scheduler.modules.task.mapper.TaskLogMapper;
import com.qzer.scheduler.modules.task.service.TaskLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskLogServiceImpl implements TaskLogService {

    private final TaskLogMapper taskLogMapper;

    @Override
    @Transactional
    public Long saveLog(TaskLog taskLog) {
        taskLog.setCreateTime(LocalDateTime.now());
        taskLogMapper.insert(taskLog);
        log.debug("保存任务日志: {}", taskLog.getId());
        return taskLog.getId();
    }

    @Override
    public TaskLog getLogById(Long id) {
        return taskLogMapper.selectById(id);
    }

    @Override
    public IPage<TaskLog> listLogs(Page<TaskLog> page, Long taskId, Integer status) {
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
    @Transactional
    public void updateLogStatus(Long logId, Integer status, String errorMsg, Long duration) {
        TaskLog taskLog = taskLogMapper.selectById(logId);
        if (taskLog != null) {
            taskLog.setExecutionStatus(status);
            taskLog.setEndTime(LocalDateTime.now());
            if (errorMsg != null) {
                taskLog.setErrorMsg(errorMsg);
            }
            if (duration != null) {
                taskLog.setDuration(duration);
            }
            taskLogMapper.updateById(taskLog);
            log.debug("更新任务日志状态: logId={}, status={}", logId, status);
        }
    }
}
