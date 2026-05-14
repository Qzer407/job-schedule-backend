
package com.qzer.scheduler.modules.message.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("t_consume_log")
public class ConsumeLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String messageId;

    private String queueName;

    private String consumer;

    private LocalDateTime consumeTime;

    private Integer status;

    private Long costTime;

    private String errorMessage;

    private Integer retryCount;

    private LocalDateTime createTime;
}

