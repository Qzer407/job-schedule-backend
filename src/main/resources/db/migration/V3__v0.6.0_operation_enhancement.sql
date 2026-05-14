
-- v0.6.0 运维增强 - 数据库脚本

-- 执行器分组表
CREATE TABLE IF NOT EXISTS t_executor_group (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    group_name VARCHAR(100) NOT NULL COMMENT '分组名称',
    group_code VARCHAR(50) NOT NULL COMMENT '分组编码',
    description VARCHAR(500) COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_group_code (group_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='执行器分组表';

-- 任务调度日志表
CREATE TABLE IF NOT EXISTS t_schedule_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    task_id BIGINT NOT NULL COMMENT '任务ID',
    task_name VARCHAR(200) COMMENT '任务名称',
    schedule_time DATETIME NOT NULL COMMENT '调度时间',
    trigger_type VARCHAR(50) COMMENT '触发类型：CRON-定时，MANUAL-手动，API-接口',
    status TINYINT COMMENT '状态：0-失败，1-成功',
    error_message TEXT COMMENT '错误信息',
    cost_time BIGINT COMMENT '耗时(毫秒)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_task_id (task_id),
    INDEX idx_schedule_time (schedule_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务调度日志表';

-- 审计日志表
CREATE TABLE IF NOT EXISTS t_audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT COMMENT '用户ID',
    username VARCHAR(100) COMMENT '用户名',
    operation VARCHAR(100) NOT NULL COMMENT '操作',
    module VARCHAR(100) COMMENT '模块',
    description VARCHAR(500) COMMENT '描述',
    request_method VARCHAR(20) COMMENT '请求方法',
    request_url VARCHAR(500) COMMENT '请求URL',
    request_params TEXT COMMENT '请求参数',
    response_result TEXT COMMENT '响应结果',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    status TINYINT DEFAULT 1 COMMENT '状态：0-失败，1-成功',
    cost_time BIGINT COMMENT '耗时(毫秒)',
    error_message TEXT COMMENT '错误信息',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作审计日志表';

-- 插入示例数据
INSERT INTO t_executor_group (group_name, group_code, description, status) VALUES
('默认分组', 'DEFAULT', '默认执行器分组', 1),
('测试分组', 'TEST', '测试执行器分组', 1);

