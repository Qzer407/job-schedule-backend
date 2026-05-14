
package com.qzer.scheduler.modules.message.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qzer.scheduler.modules.message.config.RabbitMQConfig;
import com.qzer.scheduler.modules.message.entity.ConsumeLog;
import com.qzer.scheduler.modules.message.entity.MessageTask;
import com.qzer.scheduler.modules.message.mapper.ConsumeLogMapper;
import com.qzer.scheduler.modules.message.mapper.MessageTaskMapper;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskConsumer {

    private final MessageTaskMapper messageTaskMapper;
    private final ConsumeLogMapper consumeLogMapper;

    @RabbitListener(queues = RabbitMQConfig.TASK_QUEUE)
    public void consumeTask(Message message, Channel channel) throws IOException {
        long start = System.currentTimeMillis();
        String messageId = message.getMessageProperties().getMessageId();
        log.info("开始消费消息: messageId={}", messageId);

        try {
            // 1. 查询任务
            LambdaQueryWrapper&lt;MessageTask&gt; wrapper = new LambdaQueryWrapper&lt;&gt;();
            wrapper.eq(MessageTask::getMessageId, messageId);
            MessageTask task = messageTaskMapper.selectOne(wrapper);

            if (task == null) {
                log.warn("任务不存在: messageId={}", messageId);
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }

            // 检查是否已取消
            if (task.getStatus() == 5) {
                log.info("任务已取消，跳过消费: messageId={}", messageId);
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }

            // 2. 更新状态为消费中
            task.setStatus(2);
            task.setUpdateTime(LocalDateTime.now());
            messageTaskMapper.updateById(task);

            // 3. 执行业务逻辑（这里模拟执行）
            // TODO: 这里应该调用实际的任务执行逻辑
            log.info("执行任务: messageId={}, payload={}", messageId, new String(message.getBody()));
            Thread.sleep(100); // 模拟执行时间

            // 4. 更新状态为成功
            task.setStatus(3);
            task.setUpdateTime(LocalDateTime.now());
            messageTaskMapper.updateById(task);

            // 5. 记录消费日志
            saveConsumeLog(messageId, RabbitMQConfig.TASK_QUEUE, 1, System.currentTimeMillis() - start, null, task.getRetryCount());

            // 6. 确认消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info("消息消费成功: messageId={}", messageId);

        } catch (Exception e) {
            log.error("消息消费失败: messageId={}", messageId, e);
            handleConsumeFailure(message, channel, messageId, e);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.TASK_DLQ_QUEUE)
    public void consumeDeadLetter(Message message, Channel channel) throws IOException {
        String messageId = message.getMessageProperties().getMessageId();
        log.warn("收到死信消息: messageId={}", messageId);

        try {
            // 记录死信
            saveConsumeLog(messageId, RabbitMQConfig.TASK_DLQ_QUEUE, 0, 0L, "死信消息", 0);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            log.info("死信消息处理完成: messageId={}", messageId);
        } catch (Exception e) {
            log.error("死信消息处理失败: messageId={}", messageId, e);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }

    private void handleConsumeFailure(Message message, Channel channel, String messageId, Exception e) throws IOException {
        // 查询任务
        LambdaQueryWrapper&lt;MessageTask&gt; wrapper = new LambdaQueryWrapper&lt;&gt;();
        wrapper.eq(MessageTask::getMessageId, messageId);
        MessageTask task = messageTaskMapper.selectOne(wrapper);

        if (task != null) {
            int retryCount = task.getRetryCount() + 1;
            
            if (retryCount &lt;= task.getMaxRetry()) {
                // 重试
                task.setRetryCount(retryCount);
                task.setErrorMessage(e.getMessage());
                task.setUpdateTime(LocalDateTime.now());
                messageTaskMapper.updateById(task);
                
                log.warn("消息消费失败，准备重试: messageId={}, retryCount={}/{}", 
                        messageId, retryCount, task.getMaxRetry());
                
                // 记录失败日志
                saveConsumeLog(messageId, RabbitMQConfig.TASK_QUEUE, 0, 0L, e.getMessage(), retryCount);
                
                // 重新入队
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            } else {
                // 超过重试次数，标记为失败
                task.setStatus(4);
                task.setErrorMessage(e.getMessage());
                task.setUpdateTime(LocalDateTime.now());
                messageTaskMapper.updateById(task);
                
                log.error("消息消费失败，超过重试次数: messageId={}", messageId);
                
                // 记录失败日志
                saveConsumeLog(messageId, RabbitMQConfig.TASK_QUEUE, 0, 0L, e.getMessage(), retryCount);
                
                // 确认消息（不重新入队，进入死信队列）
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            }
        } else {
            // 任务不存在，直接丢弃
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }

    private void saveConsumeLog(String messageId, String queueName, Integer status, Long costTime, String errorMessage, Integer retryCount) {
        ConsumeLog consumeLog = new ConsumeLog();
        consumeLog.setMessageId(messageId);
        consumeLog.setQueueName(queueName);
        consumeLog.setConsumer(Thread.currentThread().getName());
        consumeLog.setConsumeTime(LocalDateTime.now());
        consumeLog.setStatus(status);
        consumeLog.setCostTime(costTime);
        consumeLog.setErrorMessage(errorMessage);
        consumeLog.setRetryCount(retryCount);
        consumeLog.setCreateTime(LocalDateTime.now());
        consumeLogMapper.insert(consumeLog);
    }
}

