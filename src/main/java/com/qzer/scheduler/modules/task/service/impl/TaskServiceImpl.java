package com.qzer.scheduler.modules.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qzer.scheduler.common.utils.RedisUtil;
import com.qzer.scheduler.modules.task.dto.TaskCreateRequest;
import com.qzer.scheduler.modules.task.dto.TaskUpdateRequest;
import com.qzer.scheduler.modules.task.entity.TaskInfo;
import com.qzer.scheduler.modules.task.mapper.TaskInfoMapper;
import com.qzer.scheduler.modules.task.service.TaskService;
import com.qzer.scheduler.modules.task.service.XxlJobAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskInfoMapper taskInfoMapper;
    private final XxlJobAdminService xxlJobAdminService;
    private final RedisUtil redisUtil;
    private final ObjectMapper objectMapper;

    private static final String TASK_LIST_CACHE_KEY = "task:list:";
    private static final String TASK_DETAIL_CACHE_KEY = "task:detail:";
    private static final long CACHE_EXPIRE_TIME = 30;

    @Override
    @Transactional
    public TaskInfo createTask(TaskCreateRequest request) {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setTaskName(request.getTaskName());
        taskInfo.setTaskGroup(request.getTaskGroup());
        taskInfo.setCronExpression(request.getCronExpression());
        taskInfo.setExecutorHandler(request.getExecutorHandler());
        taskInfo.setExecutorParam(request.getExecutorParam());
        taskInfo.setShardingTotal(request.getShardingTotal());
        taskInfo.setShardingParam(request.getShardingParam());
        taskInfo.setRetryCount(request.getRetryCount());
        taskInfo.setAlarmStatus(request.getAlarmStatus());
        taskInfo.setDescription(request.getDescription());
        taskInfo.setStatus(0);
        taskInfo.setCreateTime(LocalDateTime.now());
        taskInfo.setUpdateTime(LocalDateTime.now());

        taskInfoMapper.insert(taskInfo);
        clearTaskCache();
        log.info("创建任务成功: {}", taskInfo.getId());
        return taskInfo;
    }

    @Override
    @Transactional
    public TaskInfo updateTask(Long id, TaskUpdateRequest request) {
        TaskInfo taskInfo = taskInfoMapper.selectById(id);
        if (taskInfo == null) {
            throw new RuntimeException("任务不存在");
        }

        if (StringUtils.hasText(request.getTaskName())) {
            taskInfo.setTaskName(request.getTaskName());
        }
        if (StringUtils.hasText(request.getTaskGroup())) {
            taskInfo.setTaskGroup(request.getTaskGroup());
        }
        if (StringUtils.hasText(request.getCronExpression())) {
            taskInfo.setCronExpression(request.getCronExpression());
        }
        if (StringUtils.hasText(request.getExecutorHandler())) {
            taskInfo.setExecutorHandler(request.getExecutorHandler());
        }
        if (request.getExecutorParam() != null) {
            taskInfo.setExecutorParam(request.getExecutorParam());
        }
        if (request.getShardingTotal() != null) {
            taskInfo.setShardingTotal(request.getShardingTotal());
        }
        if (request.getShardingParam() != null) {
            taskInfo.setShardingParam(request.getShardingParam());
        }
        if (request.getRetryCount() != null) {
            taskInfo.setRetryCount(request.getRetryCount());
        }
        if (request.getAlarmStatus() != null) {
            taskInfo.setAlarmStatus(request.getAlarmStatus());
        }
        if (request.getStatus() != null) {
            taskInfo.setStatus(request.getStatus());
        }
        if (request.getDescription() != null) {
            taskInfo.setDescription(request.getDescription());
        }
        taskInfo.setUpdateTime(LocalDateTime.now());

        taskInfoMapper.updateById(taskInfo);
        clearTaskCache();
        log.info("更新任务成功: {}", id);
        return taskInfo;
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {
        TaskInfo taskInfo = taskInfoMapper.selectById(id);
        if (taskInfo == null) {
            throw new RuntimeException("任务不存在");
        }
        taskInfoMapper.deleteById(id);
        clearTaskCache();
        log.info("删除任务成功: {}", id);
    }

    @Override
    public TaskInfo getTaskById(Long id) {
        String cacheKey = TASK_DETAIL_CACHE_KEY + id;
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            return objectMapper.convertValue(cached, TaskInfo.class);
        }
        TaskInfo taskInfo = taskInfoMapper.selectById(id);
        if (taskInfo != null) {
            redisUtil.set(cacheKey, taskInfo, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        }
        return taskInfo;
    }

    @Override
    public IPage<TaskInfo> listTasks(Page<TaskInfo> page, String taskName, String taskGroup, Integer status) {
        String cacheKey = TASK_LIST_CACHE_KEY + page.getCurrent() + ":" + page.getSize() + ":" + taskName + ":" + taskGroup + ":" + status;
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            return objectMapper.convertValue(cached, new TypeReference<IPage<TaskInfo>>() {});
        }
        LambdaQueryWrapper<TaskInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(taskName)) {
            wrapper.like(TaskInfo::getTaskName, taskName);
        }
        if (StringUtils.hasText(taskGroup)) {
            wrapper.eq(TaskInfo::getTaskGroup, taskGroup);
        }
        if (status != null) {
            wrapper.eq(TaskInfo::getStatus, status);
        }
        wrapper.orderByDesc(TaskInfo::getCreateTime);
        IPage<TaskInfo> result = taskInfoMapper.selectPage(page, wrapper);
        redisUtil.set(cacheKey, result, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        return result;
    }

    @Override
    public void startTask(Long id) {
        TaskInfo taskInfo = taskInfoMapper.selectById(id);
        if (taskInfo == null) {
            throw new RuntimeException("任务不存在");
        }
        taskInfo.setStatus(1);
        taskInfo.setUpdateTime(LocalDateTime.now());
        taskInfoMapper.updateById(taskInfo);
        xxlJobAdminService.startJob(id);
        clearTaskCache();
        log.info("启动任务成功: {}", id);
    }

    @Override
    public void stopTask(Long id) {
        TaskInfo taskInfo = taskInfoMapper.selectById(id);
        if (taskInfo == null) {
            throw new RuntimeException("任务不存在");
        }
        taskInfo.setStatus(0);
        taskInfo.setUpdateTime(LocalDateTime.now());
        taskInfoMapper.updateById(taskInfo);
        xxlJobAdminService.stopJob(id);
        clearTaskCache();
        log.info("停止任务成功: {}", id);
    }

    private void clearTaskCache() {
        Set<String> keys = redisUtil.keys(TASK_LIST_CACHE_KEY + "*");
        if (keys != null && !keys.isEmpty()) {
            redisUtil.delete(keys);
        }
        keys = redisUtil.keys(TASK_DETAIL_CACHE_KEY + "*");
        if (keys != null && !keys.isEmpty()) {
            redisUtil.delete(keys);
        }
    }

    @Override
    public void triggerTask(Long id, String executorParam) {
        TaskInfo taskInfo = taskInfoMapper.selectById(id);
        if (taskInfo == null) {
            throw new RuntimeException("任务不存在");
        }
        boolean result = xxlJobAdminService.triggerJob(id, executorParam);
        if (!result) {
            throw new RuntimeException("触发XXL-JOB任务失败");
        }
        log.info("触发任务成功: {}", id);
    }
}
