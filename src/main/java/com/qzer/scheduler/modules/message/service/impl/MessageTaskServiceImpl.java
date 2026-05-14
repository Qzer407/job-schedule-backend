
package com.qzer.scheduler.modules.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzer.scheduler.modules.message.config.RabbitMQConfig;
import com.qzer.scheduler.modules.message.dto.AsyncTaskRequest;
import com.qzer.scheduler.modules.message.dto.DelayTaskRequest;
import com.qzer.scheduler.modules.message.entity.MessageTask;
import com.qzer.scheduler.modules.message.mapper.MessageTaskMapper;
import com.qzer.scheduler.modules.message.service.MessageTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageTaskServiceImpl implements MessageTaskService {

    private final RabbitTemplate rabbitTemplate;
    private final MessageTaskMapper messageTaskMapper;

    @Override
    public String submitAsyncTask(AsyncTaskRequest request) {
        String messageId = UUID.randomUUID().toString();
        
        // 1. 保存数据库
        MessageTask task = new MessageTask();
        task.setTaskId(request.getTaskId());
        task.setTaskName(request.getTaskName());
        task.setMessageId(messageId);
        task.setQueueName(RabbitMQConfig.TASK_QUEUE);
        task.setMessageType("ASYNC");
        task.setPayload(request.getPayload());
        task.setStatus(0);
        task.setMaxRetry(request.getMaxRetry());
        task.setRetryCount(0);
        task.setCreateTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());
        messageTaskMapper.insert(task);
        
        // 2. 发送到 RabbitMQ
        try {
            Message message = MessageBuilder
                    .withBody(request.getPayload().getBytes(StandardCharsets.UTF_8))
                    .setMessageId(messageId)
                    .setHeader("taskId", request.getTaskId())
                    .setHeader("taskName", request.getTaskName())
                    .build();
            
            rabbitTemplate.send(RabbitMQConfig.TASK_EXCHANGE, RabbitMQConfig.TASK_ROUTING_KEY, message);
            
            // 更新状态为已发送
            task.setStatus(1);
            task.setUpdateTime(LocalDateTime.now());
            messageTaskMapper.updateById(task);
            
            log.info("异步任务提交成功: messageId={}", messageId);
        } catch (Exception e) {
            log.error("异步任务发送失败: messageId={}", messageId, e);
            task.setStatus(4);
            task.setErrorMessage(e.getMessage());
            task.setUpdateTime(LocalDateTime.now());
            messageTaskMapper.updateById(task);
            throw new RuntimeException("发送消息失败", e);
        }
        
        return messageId;
    }

    @Override
    public String submitDelayTask(DelayTaskRequest request) {
        String messageId = UUID.randomUUID().toString();
        
        // 1. 保存数据库
        MessageTask task = new MessageTask();
        task.setTaskId(request.getTaskId());
        task.setTaskName(request.getTaskName());
        task.setMessageId(messageId);
        task.setQueueName(RabbitMQConfig.DELAY_QUEUE);
        task.setMessageType("DELAY");
        task.setPayload(request.getPayload());
        task.setStatus(0);
        task.setDelayTime(request.getDelayTime());
        task.setExecuteTime(LocalDateTime.now().plusNanos(request.getDelayTime() * 1_000_000));
        task.setMaxRetry(request.getMaxRetry());
        task.setRetryCount(0);
        task.setCreateTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());
        messageTaskMapper.insert(task);
        
        // 2. 发送到延迟队列
        try {
            Message message = MessageBuilder
                    .withBody(request.getPayload().getBytes(StandardCharsets.UTF_8))
                    .setMessageId(messageId)
                    .setHeader("taskId", request.getTaskId())
                    .setHeader("taskName", request.getTaskName())
                    .setHeader("x-delay", request.getDelayTime())
                    .build();
            
            // 设置消息过期时间
            message.getMessageProperties().setExpiration(String.valueOf(request.getDelayTime()));
            
            rabbitTemplate.send(RabbitMQConfig.DELAY_EXCHANGE, RabbitMQConfig.DELAY_ROUTING_KEY, message);
            
            // 更新状态为已发送
            task.setStatus(1);
            task.setUpdateTime(LocalDateTime.now());
            messageTaskMapper.updateById(task);
            
            log.info("延迟任务提交成功: messageId={}, delay={}ms", messageId, request.getDelayTime());
        } catch (Exception e) {
            log.error("延迟任务发送失败: messageId={}", messageId, e);
            task.setStatus(4);
            task.setErrorMessage(e.getMessage());
            task.setUpdateTime(LocalDateTime.now());
            messageTaskMapper.updateById(task);
            throw new RuntimeException("发送延迟消息失败", e);
        }
        
        return messageId;
    }

    @Override
    public Page&lt;MessageTask&gt; getTaskList(Integer current, Integer size, Integer status) {
        Page&lt;MessageTask&gt; page = new Page&lt;&gt;(current, size);
        LambdaQueryWrapper&lt;MessageTask&gt; wrapper = new LambdaQueryWrapper&lt;&gt;();
        
        if (status != null) {
            wrapper.eq(MessageTask::getStatus, status);
        }
        
        wrapper.orderByDesc(MessageTask::getCreateTime);
        return messageTaskMapper.selectPage(page, wrapper);
    }

    @Override
    public MessageTask getTaskById(Long id) {
        return messageTaskMapper.selectById(id);
    }

    @Override
    public void cancelTask(Long id) {
        MessageTask task = messageTaskMapper.selectById(id);
        if (task != null &amp;&amp; task.getStatus() &lt;= 1) {
            task.setStatus(5); // 已取消
            task.setUpdateTime(LocalDateTime.now());
            messageTaskMapper.updateById(task);
            log.info("任务已取消: id={}", id);
        }
    }

    @Override
    public void retryTask(Long id) {
        MessageTask task = messageTaskMapper.selectById(id);
        if (task != null &amp;&amp; task.getStatus() == 4) {
            AsyncTaskRequest request = new AsyncTaskRequest();
            request.setTaskId(task.getTaskId());
            request.setTaskName(task.getTaskName());
            request.setPayload(task.getPayload());
            request.setMaxRetry(task.getMaxRetry());
            submitAsyncTask(request);
        }
    }
}

