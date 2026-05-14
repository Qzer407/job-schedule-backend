
package com.qzer.scheduler.modules.monitor.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_executor_group")
public class ExecutorGroup {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("group_name")
    private String groupName;

    @TableField("group_code")
    private String groupCode;

    @TableField("description")
    private String description;

    @TableField("status")
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

