
-- 创建消息任务表
CREATE TABLE IF NOT EXISTS t_message_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    task_id BIGINT COMMENT '关联任务ID',
    task_name VARCHAR(200) COMMENT '任务名称',
    message_id VARCHAR(100) NOT NULL COMMENT '消息ID',
    queue_name VARCHAR(100) NOT NULL COMMENT '队列名称',
    message_type VARCHAR(50) NOT NULL COMMENT '消息类型 ASYNC/DELAY',
    payload TEXT COMMENT '消息载荷',
    status TINYINT DEFAULT 0 COMMENT '状态 0-待发送 1-已发送 2-消费中 3-成功 4-失败 5-已取消',
    delay_time BIGINT COMMENT '延迟时间(ms)',
    execute_time DATETIME COMMENT '执行时间',
    retry_count INT DEFAULT 0 COMMENT '重试次数',
    max_retry INT DEFAULT 3 COMMENT '最大重试次数',
    error_message TEXT COMMENT '错误信息',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_task_id (task_id),
    INDEX idx_message_id (message_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息任务表';

-- 创建消费日志表
CREATE TABLE IF NOT EXISTS t_consume_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    message_id VARCHAR(100) NOT NULL COMMENT '消息ID',
    queue_name VARCHAR(100) NOT NULL COMMENT '队列名称',
    consumer VARCHAR(100) COMMENT '消费者',
    consume_time DATETIME NOT NULL COMMENT '消费时间',
    status TINYINT COMMENT '状态 0-失败 1-成功',
    cost_time BIGINT COMMENT '耗时(ms)',
    error_message TEXT COMMENT '错误信息',
    retry_count INT COMMENT '当前重试次数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_message_id (message_id),
    INDEX idx_consume_time (consume_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消费日志表';

-- 创建死信消息表
CREATE TABLE IF NOT EXISTS t_dead_letter (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    original_message_id VARCHAR(100) NOT NULL COMMENT '原消息ID',
    queue_name VARCHAR(100) NOT NULL COMMENT '原队列',
    dead_letter_queue VARCHAR(100) NOT NULL COMMENT '死信队列',
    payload TEXT COMMENT '消息载荷',
    reason VARCHAR(200) COMMENT '死信原因',
    error_message TEXT COMMENT '错误信息',
    is_reprocessed TINYINT DEFAULT 0 COMMENT '是否已重处理 0-否 1-是',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_original_message_id (original_message_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='死信消息表';

