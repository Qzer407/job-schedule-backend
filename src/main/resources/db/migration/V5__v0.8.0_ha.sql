
-- v0.8.0 - 高可用数据库迁移脚本
-- 创建时间：2026-05-14

-- 集群节点表
CREATE TABLE IF NOT EXISTS t_cluster_node (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    node_id VARCHAR(64) NOT NULL COMMENT '节点唯一标识',
    node_name VARCHAR(100) NOT NULL COMMENT '节点名称',
    node_ip VARCHAR(45) NOT NULL COMMENT '节点IP地址',
    node_port INT NOT NULL COMMENT '节点端口',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-离线,1-在线,2-故障',
    role VARCHAR(20) NOT NULL DEFAULT 'worker' COMMENT '角色：master,worker',
    last_heartbeat DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后心跳时间',
    cpu_load DECIMAL(5,2) NULL COMMENT 'CPU负载',
    memory_usage DECIMAL(5,2) NULL COMMENT '内存使用率',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_node_id (node_id),
    INDEX idx_status (status),
    INDEX idx_last_heartbeat (last_heartbeat)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='集群节点表';

-- 任务分片表
CREATE TABLE IF NOT EXISTS t_task_shard (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    task_id BIGINT NOT NULL COMMENT '任务ID',
    shard_key VARCHAR(100) NULL COMMENT '分片键',
    assigned_node_id VARCHAR(64) NULL COMMENT '分配节点ID',
    shard_status TINYINT NOT NULL DEFAULT 0 COMMENT '分片状态：0-待分配,1-已分配,2-迁移中',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_task_id (task_id),
    INDEX idx_assigned_node (assigned_node_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务分片表';

-- 集群事件表
CREATE TABLE IF NOT EXISTS t_cluster_event (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    event_type VARCHAR(50) NOT NULL COMMENT '事件类型：node_join,node_leave,failover,task_migrate',
    event_data TEXT NULL COMMENT '事件数据(JSON)',
    event_time DATETIME NOT NULL COMMENT '事件时间',
    source_node VARCHAR(64) NULL COMMENT '源节点',
    target_node VARCHAR(64) NULL COMMENT '目标节点',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_event_type (event_type),
    INDEX idx_event_time (event_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='集群事件表';

-- 故障记录表
CREATE TABLE IF NOT EXISTS t_failure_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    node_id VARCHAR(64) NOT NULL COMMENT '故障节点ID',
    failure_type VARCHAR(50) NOT NULL COMMENT '故障类型',
    failure_detail TEXT NULL COMMENT '故障详情',
    recovery_status TINYINT NOT NULL DEFAULT 0 COMMENT '恢复状态：0-未恢复,1-已恢复,2-恢复中',
    failure_time DATETIME NOT NULL COMMENT '故障时间',
    recovery_time DATETIME NULL COMMENT '恢复时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_node_id (node_id),
    INDEX idx_failure_time (failure_time),
    INDEX idx_recovery_status (recovery_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='故障记录表';
