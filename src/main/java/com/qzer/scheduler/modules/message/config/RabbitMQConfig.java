
package com.qzer.scheduler.modules.message.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    // 队列名称
    public static final String TASK_QUEUE = "task.queue";
    public static final String TASK_DLQ_QUEUE = "task.queue.dlq";
    public static final String DELAY_QUEUE = "delay.queue";

    // 交换机名称
    public static final String TASK_EXCHANGE = "task.exchange";
    public static final String TASK_DLX_EXCHANGE = "task.dlx.exchange";
    public static final String DELAY_EXCHANGE = "delay.exchange";

    // 路由键
    public static final String TASK_ROUTING_KEY = "task.routing.key";
    public static final String TASK_DLX_ROUTING_KEY = "task.dlx.routing.key";
    public static final String DELAY_ROUTING_KEY = "delay.routing.key";

    // 1. 消息转换器 - 使用 Jackson 进行 JSON 序列化
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // 2. 配置 RabbitTemplate
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    // ========== 普通任务队列相关 ==========

    // 任务交换机 (direct)
    @Bean
    public DirectExchange taskExchange() {
        return new DirectExchange(TASK_EXCHANGE, true, false);
    }

    // 任务队列（配置死信交换机）
    @Bean
    public Queue taskQueue() {
        Map&lt;String, Object&gt; args = new HashMap&lt;&gt;();
        args.put("x-dead-letter-exchange", TASK_DLX_EXCHANGE);
        args.put("x-dead-letter-routing-key", TASK_DLX_ROUTING_KEY);
        return new Queue(TASK_QUEUE, true, false, false, args);
    }

    // 任务队列绑定
    @Bean
    public Binding taskBinding() {
        return BindingBuilder.bind(taskQueue()).to(taskExchange()).with(TASK_ROUTING_KEY);
    }

    // ========== 死信队列相关 ==========

    // 死信交换机
    @Bean
    public DirectExchange taskDlxExchange() {
        return new DirectExchange(TASK_DLX_EXCHANGE, true, false);
    }

    // 死信队列
    @Bean
    public Queue taskDlqQueue() {
        return new Queue(TASK_DLQ_QUEUE, true);
    }

    // 死信队列绑定
    @Bean
    public Binding taskDlqBinding() {
        return BindingBuilder.bind(taskDlqQueue()).to(taskDlxExchange()).with(TASK_DLX_ROUTING_KEY);
    }

    // ========== 延迟队列相关 ==========

    // 延迟交换机
    @Bean
    public DirectExchange delayExchange() {
        return new DirectExchange(DELAY_EXCHANGE, true, false);
    }

    // 延迟队列（TTL + 死信转发）
    @Bean
    public Queue delayQueue() {
        Map&lt;String, Object&gt; args = new HashMap&lt;&gt;();
        args.put("x-dead-letter-exchange", TASK_EXCHANGE);
        args.put("x-dead-letter-routing-key", TASK_ROUTING_KEY);
        return new Queue(DELAY_QUEUE, true, false, false, args);
    }

    // 延迟队列绑定
    @Bean
    public Binding delayBinding() {
        return BindingBuilder.bind(delayQueue()).to(delayExchange()).with(DELAY_ROUTING_KEY);
    }
}

