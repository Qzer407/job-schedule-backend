package com.qzer.scheduler.modules.workflow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_task_dependency")
public class TaskDependency {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("workflow_id")
    private Long workflowId;

    @TableField("task_id")
    private Long taskId;

    @TableField("parent_task_id")
    private Long parentTaskId;

    @TableField("dependency_type")
    private String dependencyType;

    @TableField("create_time")
    private LocalDateTime createTime;
}
