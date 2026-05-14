package com.qzer.scheduler.modules.task.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_sub_task")
public class SubTask {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("parent_task_id")
    private Long parentTaskId;

    @TableField("sub_task_name")
    private String subTaskName;

    @TableField("executor_handler")
    private String executorHandler;

    @TableField("executor_param")
    private String executorParam;

    @TableField("retry_count")
    private Integer retryCount;

    @TableField("order_index")
    private Integer orderIndex;

    @TableField("status")
    private Integer status;

    @TableField("description")
    private String description;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
