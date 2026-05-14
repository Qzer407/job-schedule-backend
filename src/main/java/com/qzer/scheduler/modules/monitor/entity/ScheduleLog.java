
package com.qzer.scheduler.modules.monitor.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_schedule_log")
public class ScheduleLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("task_id")
    private Long taskId;

    @TableField("task_name")
    private String taskName;

    @TableField("schedule_time")
    private LocalDateTime scheduleTime;

    @TableField("trigger_type")
    private String triggerType;

    @TableField("status")
    private Integer status;

    @TableField("error_message")
    private String errorMessage;

    @TableField("cost_time")
    private Long costTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

