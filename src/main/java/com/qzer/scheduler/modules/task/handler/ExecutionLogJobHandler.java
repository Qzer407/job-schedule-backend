package com.qzer.scheduler.modules.task.handler;

import com.qzer.scheduler.modules.task.entity.TaskInfo;
import com.qzer.scheduler.modules.task.entity.TaskLog;
import com.qzer.scheduler.modules.task.mapper.TaskInfoMapper;
import com.qzer.scheduler.modules.task.mapper.TaskLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class ExecutionLogJobHandler {

    @Autowired
    private TaskLogMapper taskLogMapper;

    @Autowired
    private TaskInfoMapper taskInfoMapper;

    public Long startExecution(Long taskId, String executorHandler, String executorParam) {
        TaskInfo taskInfo = taskInfoMapper.selectById(taskId);
        String taskName = taskInfo != null ? taskInfo.getTaskName() : executorHandler;

        TaskLog taskLog = new TaskLog();
        taskLog.setTaskId(taskId);
        taskLog.setTaskName(taskName);
        taskLog.setExecutorAddress("local");
        taskLog.setShardingIndex(0);
        taskLog.setShardingParam(executorParam == null ? "" : executorParam);
        taskLog.setTriggerTime(LocalDateTime.now());
        taskLog.setStartTime(LocalDateTime.now());
        taskLog.setExecutionStatus(0);

        taskLogMapper.insert(taskLog);
        log.info("任务执行开始，日志ID: {}", taskLog.getId());

        return taskLog.getId();
    }

    public void onSuccess(Long logId, String result) {
        TaskLog taskLog = taskLogMapper.selectById(logId);
        if (taskLog != null) {
            taskLog.setExecutionStatus(1);
            taskLog.setEndTime(LocalDateTime.now());
            long duration = java.time.Duration.between(taskLog.getStartTime(), taskLog.getEndTime()).toMillis();
            taskLog.setDuration(duration);
            taskLogMapper.updateById(taskLog);
            log.info("任务执行成功，日志ID: {}, 耗时: {}ms", logId, duration);
        }
    }

    public void onFailure(Long logId, Throwable e) {
        TaskLog taskLog = taskLogMapper.selectById(logId);
        if (taskLog != null) {
            taskLog.setExecutionStatus(2);
            taskLog.setEndTime(LocalDateTime.now());
            long duration = java.time.Duration.between(taskLog.getStartTime(), taskLog.getEndTime()).toMillis();
            taskLog.setDuration(duration);
            taskLog.setErrorMsg(e.getMessage());
            taskLogMapper.updateById(taskLog);
            log.error("任务执行失败，日志ID: {}, 错误: {}", logId, e.getMessage());
        }
    }
}
