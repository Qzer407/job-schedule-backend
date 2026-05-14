package com.qzer.scheduler.modules.monitor.service;

import com.qzer.scheduler.modules.monitor.entity.ExecutorInfo;

import java.util.List;

public interface ExecutorService {

    List<ExecutorInfo> listExecutors();

    ExecutorInfo getExecutorById(Long id);

    boolean registerExecutor(String executorName, String executorAddress, String executorGroup);

    void heartbeat(Long id);

    boolean isHealthy(Long id);

    void updateExecutorStatus(Long id, Integer status);
}
