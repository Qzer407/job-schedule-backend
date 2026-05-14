package com.qzer.scheduler.modules.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.modules.workflow.entity.TaskDependency;
import com.qzer.scheduler.modules.workflow.entity.Workflow;
import com.qzer.scheduler.modules.workflow.entity.WorkflowExecution;
import com.qzer.scheduler.modules.workflow.mapper.TaskDependencyMapper;
import com.qzer.scheduler.modules.workflow.mapper.WorkflowExecutionMapper;
import com.qzer.scheduler.modules.workflow.mapper.WorkflowMapper;
import com.qzer.scheduler.modules.workflow.service.WorkflowService;
import com.qzer.scheduler.modules.workflow.service.WorkflowExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowServiceImpl implements WorkflowService {

    private final WorkflowMapper workflowMapper;
    private final TaskDependencyMapper taskDependencyMapper;
    private final WorkflowExecutionMapper workflowExecutionMapper;
    private final WorkflowExecutorService workflowExecutorService;

    @Override
    @Transactional
    public Workflow createWorkflow(Workflow workflow) {
        workflow.setStatus(0);
        workflow.setCreateTime(LocalDateTime.now());
        workflow.setUpdateTime(LocalDateTime.now());
        workflowMapper.insert(workflow);
        log.info("创建工作流成功: {}", workflow.getId());
        return workflow;
    }

    @Override
    @Transactional
    public Workflow updateWorkflow(Long id, Workflow workflow) {
        Workflow existing = workflowMapper.selectById(id);
        if (existing == null) {
            throw new RuntimeException("工作流不存在");
        }
        workflow.setId(id);
        workflow.setUpdateTime(LocalDateTime.now());
        workflowMapper.updateById(workflow);
        log.info("更新工作流成功: {}", id);
        return workflow;
    }

    @Override
    @Transactional
    public void deleteWorkflow(Long id) {
        Workflow workflow = workflowMapper.selectById(id);
        if (workflow == null) {
            throw new RuntimeException("工作流不存在");
        }
        workflowMapper.deleteById(id);
        log.info("删除工作流成功: {}", id);
    }

    @Override
    public Workflow getWorkflowById(Long id) {
        return workflowMapper.selectById(id);
    }

    @Override
    public IPage<Workflow> listWorkflows(Page<Workflow> page, String keyword, Integer status) {
        LambdaQueryWrapper<Workflow> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Workflow::getWorkflowName, keyword);
        }
        if (status != null) {
            wrapper.eq(Workflow::getStatus, status);
        }
        wrapper.orderByDesc(Workflow::getCreateTime);
        return workflowMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional
    public void executeWorkflow(Long id) {
        Workflow workflow = workflowMapper.selectById(id);
        if (workflow == null) {
            throw new RuntimeException("工作流不存在");
        }
        workflowExecutorService.executeWorkflow(id);
        log.info("启动工作流执行: {}", id);
    }

    @Override
    @Transactional
    public void pauseWorkflow(Long id) {
        Workflow workflow = workflowMapper.selectById(id);
        if (workflow == null) {
            throw new RuntimeException("工作流不存在");
        }
        workflow.setStatus(2);
        workflow.setUpdateTime(LocalDateTime.now());
        workflowMapper.updateById(workflow);
        log.info("暂停工作流: {}", id);
    }

    @Override
    @Transactional
    public void terminateWorkflow(Long id) {
        Workflow workflow = workflowMapper.selectById(id);
        if (workflow == null) {
            throw new RuntimeException("工作流不存在");
        }
        workflow.setStatus(3);
        workflow.setUpdateTime(LocalDateTime.now());
        workflowMapper.updateById(workflow);

        LambdaQueryWrapper<WorkflowExecution> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkflowExecution::getWorkflowId, id);
        wrapper.eq(WorkflowExecution::getExecutionStatus, 1);
        List<WorkflowExecution> runningExecutions = workflowExecutionMapper.selectList(wrapper);
        for (WorkflowExecution execution : runningExecutions) {
            execution.setExecutionStatus(3);
            execution.setEndTime(LocalDateTime.now());
            workflowExecutionMapper.updateById(execution);
        }

        log.info("终止工作流: {}", id);
    }

    @Override
    public List<Workflow> getWorkflowTemplates() {
        LambdaQueryWrapper<Workflow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Workflow::getStatus, 1);
        wrapper.like(Workflow::getWorkflowName, "模板");
        return workflowMapper.selectList(wrapper);
    }
}
