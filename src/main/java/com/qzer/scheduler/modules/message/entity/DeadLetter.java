
package com.qzer.scheduler.modules.message.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("t_dead_letter")
public class DeadLetter implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String originalMessageId;

    private String queueName;

    private String deadLetterQueue;

    private String payload;

    private String reason;

    private String errorMessage;

    private Integer isReprocessed;

    private LocalDateTime createTime;
}

