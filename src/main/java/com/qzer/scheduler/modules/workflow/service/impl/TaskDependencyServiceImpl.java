package com.qzer.scheduler.modules.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qzer.scheduler.modules.workflow.entity.TaskDependency;
import com.qzer.scheduler.modules.workflow.mapper.TaskDependencyMapper;
import com.qzer.scheduler.modules.workflow.service.TaskDependencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskDependencyServiceImpl implements TaskDependencyService {

    private final TaskDependencyMapper taskDependencyMapper;

    @Override
    @Transactional
    public TaskDependency addDependency(Long workflowId, Long taskId, Long parentTaskId, String dependencyType) {
        TaskDependency dependency = new TaskDependency();
        dependency.setWorkflowId(workflowId);
        dependency.setTaskId(taskId);
        dependency.setParentTaskId(parentTaskId);
        dependency.setDependencyType(dependencyType);
        dependency.setCreateTime(LocalDateTime.now());
        taskDependencyMapper.insert(dependency);
        log.info("添加任务依赖成功: workflowId={}, taskId={}, parentTaskId={}", workflowId, taskId, parentTaskId);
        return dependency;
    }

    @Override
    @Transactional
    public void removeDependency(Long id) {
        taskDependencyMapper.deleteById(id);
        log.info("删除任务依赖成功: {}", id);
    }

    @Override
    public List<TaskDependency> getDependenciesByWorkflowId(Long workflowId) {
        LambdaQueryWrapper<TaskDependency> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TaskDependency::getWorkflowId, workflowId);
        return taskDependencyMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public void batchAddDependencies(Long workflowId, List<TaskDependency> dependencies) {
        LambdaQueryWrapper<TaskDependency> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TaskDependency::getWorkflowId, workflowId);
        taskDependencyMapper.delete(wrapper);

        for (TaskDependency dependency : dependencies) {
            dependency.setWorkflowId(workflowId);
            dependency.setCreateTime(LocalDateTime.now());
            taskDependencyMapper.insert(dependency);
        }
        log.info("批量更新任务依赖成功: {}", workflowId);
    }
}
