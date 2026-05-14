package com.qzer.scheduler.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("t_permission")
public class Permission {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("permission_name")
    private String permissionName;

    @TableField("permission_code")
    private String permissionCode;

    @TableField("permission_type")
    private String permissionType;

    @TableField("parent_id")
    private Long parentId;

    @TableField("path")
    private String path;

    @TableField("icon")
    private String icon;

    @TableField("sort_order")
    private Integer sortOrder;

    @TableField("status")
    private Integer status;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private List<Permission> children;
}
