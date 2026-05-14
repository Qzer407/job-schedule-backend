package com.qzer.scheduler.modules.task.service;

public interface XxlJobAdminService {

    boolean triggerJob(Long jobId, String executorParam);

    boolean startJob(Long jobId);

    boolean stopJob(Long jobId);
}
