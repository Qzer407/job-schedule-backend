package com.qzer.scheduler.modules.monitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qzer.scheduler.modules.monitor.entity.ExecutorInfo;
import com.qzer.scheduler.modules.monitor.mapper.ExecutorInfoMapper;
import com.qzer.scheduler.modules.monitor.service.ExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExecutorServiceImpl implements ExecutorService {

    private final ExecutorInfoMapper executorInfoMapper;
    private static final int HEARTBEAT_TIMEOUT_SECONDS = 60;

    @Override
    public List<ExecutorInfo> listExecutors() {
        return executorInfoMapper.selectList(new LambdaQueryWrapper<ExecutorInfo>()
                .orderByDesc(ExecutorInfo::getCreateTime));
    }

    @Override
    public ExecutorInfo getExecutorById(Long id) {
        return executorInfoMapper.selectById(id);
    }

    @Override
    public boolean registerExecutor(String executorName, String executorAddress, String executorGroup) {
        LambdaQueryWrapper<ExecutorInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExecutorInfo::getExecutorAddress, executorAddress);

        ExecutorInfo existing = executorInfoMapper.selectOne(wrapper);
        if (existing != null) {
            log.info("执行器已存在: {}", executorAddress);
            return false;
        }

        ExecutorInfo executor = new ExecutorInfo();
        executor.setExecutorName(executorName);
        executor.setExecutorAddress(executorAddress);
        executor.setExecutorGroup(executorGroup);
        executor.setStatus(1);
        executor.setLastHeartbeat(LocalDateTime.now());
        executor.setCreateTime(LocalDateTime.now());
        executor.setUpdateTime(LocalDateTime.now());

        executorInfoMapper.insert(executor);
        log.info("执行器注册成功: {}", executorAddress);
        return true;
    }

    @Override
    public void heartbeat(Long id) {
        ExecutorInfo executor = executorInfoMapper.selectById(id);
        if (executor != null) {
            executor.setLastHeartbeat(LocalDateTime.now());
            executor.setStatus(1);
            executor.setUpdateTime(LocalDateTime.now());
            executorInfoMapper.updateById(executor);
            log.debug("执行器心跳更新: {}", id);
        }
    }

    @Override
    public boolean isHealthy(Long id) {
        ExecutorInfo executor = executorInfoMapper.selectById(id);
        if (executor == null) {
            return false;
        }

        LocalDateTime lastHeartbeat = executor.getLastHeartbeat();
        if (lastHeartbeat == null) {
            return false;
        }

        long secondsSinceHeartbeat = java.time.Duration.between(lastHeartbeat, LocalDateTime.now()).getSeconds();
        return secondsSinceHeartbeat <= HEARTBEAT_TIMEOUT_SECONDS && executor.getStatus() == 1;
    }

    @Override
    public void updateExecutorStatus(Long id, Integer status) {
        ExecutorInfo executor = executorInfoMapper.selectById(id);
        if (executor != null) {
            executor.setStatus(status);
            executor.setUpdateTime(LocalDateTime.now());
            executorInfoMapper.updateById(executor);
        }
    }

    @Scheduled(fixedRate = 30000)
    public void checkExecutorHealth() {
        List<ExecutorInfo> executors = listExecutors();
        for (ExecutorInfo executor : executors) {
            if (!isHealthy(executor.getId())) {
                updateExecutorStatus(executor.getId(), 0);
                log.warn("执行器不健康: {}, 上次心跳: {}", executor.getExecutorAddress(), executor.getLastHeartbeat());
            }
        }
    }
}
