package com.qzer.scheduler.modules.monitor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_executor_info")
public class ExecutorInfo {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("executor_name")
    private String executorName;

    @TableField("executor_address")
    private String executorAddress;

    @TableField("executor_group")
    private String executorGroup;

    @TableField("status")
    private Integer status;

    @TableField("last_heartbeat")
    private LocalDateTime lastHeartbeat;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
