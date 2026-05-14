
package com.qzer.scheduler.modules.message.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("t_message_task")
public class MessageTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long taskId;

    private String taskName;

    private String messageId;

    private String queueName;

    private String messageType;

    private String payload;

    private Integer status;

    private Long delayTime;

    private LocalDateTime executeTime;

    private Integer retryCount;

    private Integer maxRetry;

    private String errorMessage;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

