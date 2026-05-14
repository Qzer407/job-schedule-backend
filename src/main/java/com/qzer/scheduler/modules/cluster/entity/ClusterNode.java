
package com.qzer.scheduler.modules.cluster.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_cluster_node")
public class ClusterNode {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("node_id")
    private String nodeId;

    @TableField("node_name")
    private String nodeName;

    @TableField("node_ip")
    private String nodeIp;

    @TableField("node_port")
    private Integer nodePort;

    @TableField("status")
    private Integer status;

    @TableField("role")
    private String role;

    @TableField("last_heartbeat")
    private LocalDateTime lastHeartbeat;

    @TableField("cpu_load")
    private BigDecimal cpuLoad;

    @TableField("memory_usage")
    private BigDecimal memoryUsage;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
