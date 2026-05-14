package com.qzer.scheduler.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qzer.scheduler.entity.TaskInfo;
import com.qzer.scheduler.mapper.TaskInfoMapper;
import com.qzer.scheduler.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskInfoMapper taskInfoMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    private TaskInfo taskInfo;

    @BeforeEach
    void setUp() {
        taskInfo = new TaskInfo();
        taskInfo.setId(1L);
        taskInfo.setTaskName("测试任务");
        taskInfo.setTaskGroup("DEFAULT_GROUP");
        taskInfo.setCronExpression("0 0 * * * ?");
        taskInfo.setJobHandler("testJobHandler");
        taskInfo.setStatus(0);
        taskInfo.setCreateTime(LocalDateTime.now());
    }

    @Test
    void testCreateTask() {
        when(taskInfoMapper.insert(any(TaskInfo.class))).thenReturn(1);
        
        TaskInfo result = taskService.createTask(taskInfo);
        
        assertNotNull(result);
        assertEquals("测试任务", result.getTaskName());
        verify(taskInfoMapper, times(1)).insert(any(TaskInfo.class));
    }

    @Test
    void testGetTaskById() {
        when(taskInfoMapper.selectById(1L)).thenReturn(taskInfo);
        
        TaskInfo result = taskService.getTaskById(1L);
        
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("测试任务", result.getTaskName());
    }

    @Test
    void testGetTaskByIdNotFound() {
        when(taskInfoMapper.selectById(999L)).thenReturn(null);
        
        assertThrows(RuntimeException.class, () -> taskService.getTaskById(999L));
    }

    @Test
    void testUpdateTask() {
        when(taskInfoMapper.selectById(1L)).thenReturn(taskInfo);
        when(taskInfoMapper.updateById(any(TaskInfo.class))).thenReturn(1);
        
        TaskInfo updatedTask = new TaskInfo();
        updatedTask.setTaskName("更新后的任务");
        
        TaskInfo result = taskService.updateTask(1L, updatedTask);
        
        assertNotNull(result);
        assertEquals("更新后的任务", result.getTaskName());
        verify(taskInfoMapper, times(1)).updateById(any(TaskInfo.class));
    }

    @Test
    void testDeleteTask() {
        when(taskInfoMapper.selectById(1L)).thenReturn(taskInfo);
        when(taskInfoMapper.deleteById(1L)).thenReturn(1);
        
        assertDoesNotThrow(() -> taskService.deleteTask(1L));
        verify(taskInfoMapper, times(1)).deleteById(1L);
    }

    @Test
    void testStartTask() {
        when(taskInfoMapper.selectById(1L)).thenReturn(taskInfo);
        when(taskInfoMapper.updateById(any(TaskInfo.class))).thenReturn(1);
        
        assertDoesNotThrow(() -> taskService.startTask(1L));
        verify(taskInfoMapper, times(1)).updateById(any(TaskInfo.class));
    }

    @Test
    void testStopTask() {
        taskInfo.setStatus(1);
        when(taskInfoMapper.selectById(1L)).thenReturn(taskInfo);
        when(taskInfoMapper.updateById(any(TaskInfo.class))).thenReturn(1);
        
        assertDoesNotThrow(() -> taskService.stopTask(1L));
        verify(taskInfoMapper, times(1)).updateById(any(TaskInfo.class));
    }
}
