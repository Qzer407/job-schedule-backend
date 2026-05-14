
package com.qzer.scheduler.modules.cluster.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_cluster_event")
public class ClusterEvent {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("event_type")
    private String eventType;

    @TableField("event_data")
    private String eventData;

    @TableField("event_time")
    private LocalDateTime eventTime;

    @TableField("source_node")
    private String sourceNode;

    @TableField("target_node")
    private String targetNode;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
