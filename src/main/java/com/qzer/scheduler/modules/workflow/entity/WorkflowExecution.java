package com.qzer.scheduler.modules.workflow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_workflow_execution")
public class WorkflowExecution {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("workflow_id")
    private Long workflowId;

    @TableField("execution_status")
    private Integer executionStatus;

    @TableField("start_time")
    private LocalDateTime startTime;

    @TableField("end_time")
    private LocalDateTime endTime;

    @TableField("trigger_type")
    private String triggerType;

    @TableField("execution_log")
    private String executionLog;

    @TableField("create_time")
    private LocalDateTime createTime;
}
