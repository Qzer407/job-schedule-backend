package com.qzer.scheduler.modules.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.modules.task.dto.SubTaskCreateRequest;
import com.qzer.scheduler.modules.task.dto.SubTaskUpdateRequest;
import com.qzer.scheduler.modules.task.entity.SubTask;
import com.qzer.scheduler.modules.task.entity.TaskInfo;
import com.qzer.scheduler.modules.task.mapper.SubTaskMapper;
import com.qzer.scheduler.modules.task.mapper.TaskInfoMapper;
import com.qzer.scheduler.modules.task.service.SubTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubTaskServiceImpl implements SubTaskService {

    private final SubTaskMapper subTaskMapper;
    private final TaskInfoMapper taskInfoMapper;

    @Override
    @Transactional
    public SubTask createSubTask(Long parentTaskId, SubTaskCreateRequest request) {
        TaskInfo parentTask = taskInfoMapper.selectById(parentTaskId);
        if (parentTask == null) {
            throw new RuntimeException("父任务不存在");
        }

        SubTask subTask = new SubTask();
        BeanUtils.copyProperties(request, subTask);
        subTask.setParentTaskId(parentTaskId);
        subTask.setCreateTime(LocalDateTime.now());
        subTask.setUpdateTime(LocalDateTime.now());

        subTaskMapper.insert(subTask);
        return subTask;
    }

    @Override
    public SubTask getSubTaskById(Long id) {
        return subTaskMapper.selectById(id);
    }

    @Override
    public List<SubTask> getSubTasksByParentId(Long parentTaskId) {
        return subTaskMapper.selectByParentTaskId(parentTaskId);
    }

    @Override
    public Page<SubTask> getSubTaskPage(Long parentTaskId, Integer pageNum, Integer pageSize) {
        Page<SubTask> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SubTask> wrapper = new LambdaQueryWrapper<>();
        if (parentTaskId != null) {
            wrapper.eq(SubTask::getParentTaskId, parentTaskId);
        }
        wrapper.orderByAsc(SubTask::getOrderIndex);
        return subTaskMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional
    public SubTask updateSubTask(Long id, SubTaskUpdateRequest request) {
        SubTask subTask = subTaskMapper.selectById(id);
        if (subTask == null) {
            throw new RuntimeException("子任务不存在");
        }

        if (request.getSubTaskName() != null) {
            subTask.setSubTaskName(request.getSubTaskName());
        }
        if (request.getExecutorHandler() != null) {
            subTask.setExecutorHandler(request.getExecutorHandler());
        }
        if (request.getExecutorParam() != null) {
            subTask.setExecutorParam(request.getExecutorParam());
        }
        if (request.getRetryCount() != null) {
            subTask.setRetryCount(request.getRetryCount());
        }
        if (request.getOrderIndex() != null) {
            subTask.setOrderIndex(request.getOrderIndex());
        }
        if (request.getStatus() != null) {
            subTask.setStatus(request.getStatus());
        }
        if (request.getDescription() != null) {
            subTask.setDescription(request.getDescription());
        }
        subTask.setUpdateTime(LocalDateTime.now());

        subTaskMapper.updateById(subTask);
        return subTask;
    }

    @Override
    @Transactional
    public void deleteSubTask(Long id) {
        SubTask subTask = subTaskMapper.selectById(id);
        if (subTask == null) {
            throw new RuntimeException("子任务不存在");
        }
        subTaskMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByParentTaskId(Long parentTaskId) {
        subTaskMapper.deleteByParentTaskId(parentTaskId);
    }

    @Override
    @Transactional
    public List<SubTask> batchCreate(Long parentTaskId, List<SubTaskCreateRequest> requests) {
        TaskInfo parentTask = taskInfoMapper.selectById(parentTaskId);
        if (parentTask == null) {
            throw new RuntimeException("父任务不存在");
        }

        return requests.stream()
                .map(request -> {
                    SubTask subTask = new SubTask();
                    BeanUtils.copyProperties(request, subTask);
                    subTask.setParentTaskId(parentTaskId);
                    subTask.setCreateTime(LocalDateTime.now());
                    subTask.setUpdateTime(LocalDateTime.now());
                    subTaskMapper.insert(subTask);
                    return subTask;
                })
                .toList();
    }

    @Override
    public void executeSubTasks(Long parentTaskId) {
        List<SubTask> subTasks = getSubTasksByParentId(parentTaskId).stream()
                .filter(st -> st.getStatus() != null && st.getStatus() == 1)
                .sorted((a, b) -> {
                    int orderA = a.getOrderIndex() != null ? a.getOrderIndex() : 0;
                    int orderB = b.getOrderIndex() != null ? b.getOrderIndex() : 0;
                    return Integer.compare(orderA, orderB);
                })
                .toList();

        for (SubTask subTask : subTasks) {
            executeSubTask(subTask);
        }
    }

    private void executeSubTask(SubTask subTask) {
    }
}
