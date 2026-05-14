
package com.qzer.scheduler.modules.cluster.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_failure_record")
public class FailureRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("node_id")
    private String nodeId;

    @TableField("failure_type")
    private String failureType;

    @TableField("failure_detail")
    private String failureDetail;

    @TableField("recovery_status")
    private Integer recoveryStatus;

    @TableField("failure_time")
    private LocalDateTime failureTime;

    @TableField("recovery_time")
    private LocalDateTime recoveryTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
