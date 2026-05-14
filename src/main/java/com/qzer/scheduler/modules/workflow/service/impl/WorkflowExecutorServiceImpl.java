package com.qzer.scheduler.modules.workflow.service.impl;

import com.qzer.scheduler.common.util.DagTopologySort;
import com.qzer.scheduler.modules.task.entity.TaskInfo;
import com.qzer.scheduler.modules.task.mapper.TaskInfoMapper;
import com.qzer.scheduler.modules.workflow.entity.TaskDependency;
import com.qzer.scheduler.modules.workflow.entity.Workflow;
import com.qzer.scheduler.modules.workflow.entity.WorkflowExecution;
import com.qzer.scheduler.modules.workflow.mapper.TaskDependencyMapper;
import com.qzer.scheduler.modules.workflow.mapper.WorkflowExecutionMapper;
import com.qzer.scheduler.modules.workflow.mapper.WorkflowMapper;
import com.qzer.scheduler.modules.workflow.service.WorkflowExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowExecutorServiceImpl implements WorkflowExecutorService {

    private final WorkflowMapper workflowMapper;
    private final TaskDependencyMapper taskDependencyMapper;
    private final TaskInfoMapper taskInfoMapper;
    private final WorkflowExecutionMapper workflowExecutionMapper;

    private final Map<Long, WorkflowExecutionContext> executionContexts = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public void executeWorkflow(Long workflowId) {
        Workflow workflow = workflowMapper.selectById(workflowId);
        if (workflow == null) {
            throw new RuntimeException("工作流不存在");
        }

        if (executionContexts.containsKey(workflowId)) {
            throw new RuntimeException("工作流正在执行中");
        }

        List<TaskDependency> dependencies = taskDependencyMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<TaskDependency>()
                .eq(TaskDependency::getWorkflowId, workflowId)
        );

        List<TaskInfo> tasks = taskInfoMapper.selectList(null);
        List<Long> taskIds = tasks.stream().map(TaskInfo::getId).toList();

        List<Long> sortedTaskIds = DagTopologySort.sort(dependencies, taskIds);

        WorkflowExecution execution = new WorkflowExecution();
        execution.setWorkflowId(workflowId);
        execution.setExecutionStatus(1);
        execution.setStartTime(LocalDateTime.now());
        execution.setTriggerType("MANUAL");
        execution.setCreateTime(LocalDateTime.now());
        workflowExecutionMapper.insert(execution);

        WorkflowExecutionContext context = new WorkflowExecutionContext();
        context.setExecutionId(execution.getId());
        context.setWorkflowId(workflowId);
        context.setTaskIds(sortedTaskIds);
        context.setCurrentIndex(0);
        context.setStatus(1);
        executionContexts.put(workflowId, context);

        executeNextTask(context);
    }

    private void executeNextTask(WorkflowExecutionContext context) {
        if (context.getStatus() != 1) {
            return;
        }

        if (context.getCurrentIndex() >= context.getTaskIds().size()) {
            completeWorkflow(context);
            return;
        }

        Long taskId = context.getTaskIds().get(context.getCurrentIndex());
        TaskInfo task = taskInfoMapper.selectById(taskId);

        if (task != null) {
            try {
                log.info("执行工作流任务: workflowId={}, taskId={}, taskName={}",
                    context.getWorkflowId(), taskId, task.getTaskName());
                context.setCurrentIndex(context.getCurrentIndex() + 1);
                executeNextTask(context);
            } catch (Exception e) {
                log.error("执行工作流任务失败: {}", e.getMessage());
                failWorkflow(context, e.getMessage());
            }
        } else {
            context.setCurrentIndex(context.getCurrentIndex() + 1);
            executeNextTask(context);
        }
    }

    private void completeWorkflow(WorkflowExecutionContext context) {
        WorkflowExecution execution = workflowExecutionMapper.selectById(context.getExecutionId());
        if (execution != null) {
            execution.setExecutionStatus(2);
            execution.setEndTime(LocalDateTime.now());
            execution.setExecutionLog("工作流执行完成");
            workflowExecutionMapper.updateById(execution);
        }
        executionContexts.remove(context.getWorkflowId());
        log.info("工作流执行完成: workflowId={}", context.getWorkflowId());
    }

    private void failWorkflow(WorkflowExecutionContext context, String errorMessage) {
        WorkflowExecution execution = workflowExecutionMapper.selectById(context.getExecutionId());
        if (execution != null) {
            execution.setExecutionStatus(3);
            execution.setEndTime(LocalDateTime.now());
            execution.setExecutionLog("工作流执行失败: " + errorMessage);
            workflowExecutionMapper.updateById(execution);
        }
        executionContexts.remove(context.getWorkflowId());
        log.error("工作流执行失败: workflowId={}, error={}", context.getWorkflowId(), errorMessage);
    }

    @Override
    public void pauseWorkflow(Long workflowId) {
        WorkflowExecutionContext context = executionContexts.get(workflowId);
        if (context != null) {
            context.setStatus(2);
            log.info("工作流已暂停: {}", workflowId);
        }
    }

    @Override
    public void resumeWorkflow(Long workflowId) {
        WorkflowExecutionContext context = executionContexts.get(workflowId);
        if (context != null && context.getStatus() == 2) {
            context.setStatus(1);
            executeNextTask(context);
            log.info("工作流已恢复: {}", workflowId);
        }
    }

    @Override
    public void terminateWorkflow(Long workflowId) {
        WorkflowExecutionContext context = executionContexts.remove(workflowId);
        if (context != null) {
            WorkflowExecution execution = workflowExecutionMapper.selectById(context.getExecutionId());
            if (execution != null) {
                execution.setExecutionStatus(4);
                execution.setEndTime(LocalDateTime.now());
                execution.setExecutionLog("工作流已终止");
                workflowExecutionMapper.updateById(execution);
            }
            log.info("工作流已终止: {}", workflowId);
        }
    }

    @Override
    public Integer getWorkflowStatus(Long workflowId) {
        WorkflowExecutionContext context = executionContexts.get(workflowId);
        if (context != null) {
            return context.getStatus();
        }
        return 0;
    }

    private static class WorkflowExecutionContext {
        private Long executionId;
        private Long workflowId;
        private List<Long> taskIds;
        private int currentIndex;
        private int status;

        public Long getExecutionId() {
            return executionId;
        }

        public void setExecutionId(Long executionId) {
            this.executionId = executionId;
        }

        public Long getWorkflowId() {
            return workflowId;
        }

        public void setWorkflowId(Long workflowId) {
            this.workflowId = workflowId;
        }

        public List<Long> getTaskIds() {
            return taskIds;
        }

        public void setTaskIds(List<Long> taskIds) {
            this.taskIds = taskIds;
        }

        public int getCurrentIndex() {
            return currentIndex;
        }

        public void setCurrentIndex(int currentIndex) {
            this.currentIndex = currentIndex;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
