
package com.qzer.scheduler.modules.cluster.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_task_shard")
public class TaskShard {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("task_id")
    private Long taskId;

    @TableField("shard_key")
    private String shardKey;

    @TableField("assigned_node_id")
    private String assignedNodeId;

    @TableField("shard_status")
    private Integer shardStatus;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
