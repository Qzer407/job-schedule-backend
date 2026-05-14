
-- 创建角色表
CREATE TABLE IF NOT EXISTS t_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL COMMENT '角色编码',
    description VARCHAR(200) COMMENT '角色描述',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_role_code (role_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 创建权限表
CREATE TABLE IF NOT EXISTS t_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    permission_name VARCHAR(100) NOT NULL COMMENT '权限名称',
    permission_code VARCHAR(100) NOT NULL COMMENT '权限编码',
    permission_type VARCHAR(20) NOT NULL COMMENT '权限类型 MENU-菜单 OPERATION-操作',
    parent_id BIGINT DEFAULT 0 COMMENT '父权限ID',
    path VARCHAR(200) COMMENT '路由路径',
    icon VARCHAR(50) COMMENT '图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_permission_code (permission_code),
    INDEX idx_parent_id (parent_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 创建用户角色关联表
CREATE TABLE IF NOT EXISTS t_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 创建角色权限关联表
CREATE TABLE IF NOT EXISTS t_role_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    INDEX idx_role_id (role_id),
    INDEX idx_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 创建租户表
CREATE TABLE IF NOT EXISTS t_tenant (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    tenant_code VARCHAR(50) NOT NULL COMMENT '租户编码',
    tenant_name VARCHAR(100) NOT NULL COMMENT '租户名称',
    contact_name VARCHAR(50) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    contact_email VARCHAR(100) COMMENT '联系邮箱',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    expire_time DATETIME COMMENT '过期时间',
    max_users INT DEFAULT 10 COMMENT '最大用户数',
    max_tasks INT DEFAULT 100 COMMENT '最大任务数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_tenant_code (tenant_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户表';

-- 修改用户表，添加租户ID字段
ALTER TABLE t_user ADD COLUMN IF NOT EXISTS tenant_id BIGINT COMMENT '租户ID' AFTER id;
ALTER TABLE t_user ADD INDEX IF NOT EXISTS idx_tenant_id (tenant_id);

-- 创建OAuth2客户端表
CREATE TABLE IF NOT EXISTS t_oauth_client (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    client_id VARCHAR(100) NOT NULL COMMENT '客户端ID',
    client_name VARCHAR(100) NOT NULL COMMENT '客户端名称',
    provider VARCHAR(50) NOT NULL COMMENT '提供商 GITHUB/GITLAB/GOOGLE',
    client_secret VARCHAR(200) NOT NULL COMMENT '客户端密钥',
    redirect_uri VARCHAR(500) COMMENT '回调地址',
    scope VARCHAR(200) COMMENT '权限范围',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_client_id (client_id),
    INDEX idx_provider (provider)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OAuth2客户端表';

-- 创建OAuth2用户绑定表
CREATE TABLE IF NOT EXISTS t_oauth_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    provider VARCHAR(50) NOT NULL COMMENT '提供商',
    openid VARCHAR(100) NOT NULL COMMENT '第三方用户ID',
    nickname VARCHAR(100) COMMENT '昵称',
    avatar VARCHAR(500) COMMENT '头像',
    email VARCHAR(100) COMMENT '邮箱',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_provider_openid (provider, openid),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OAuth2用户绑定表';

-- 创建API密钥表
CREATE TABLE IF NOT EXISTS t_api_key (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    api_key VARCHAR(100) NOT NULL COMMENT 'API密钥',
    api_secret VARCHAR(200) NOT NULL COMMENT 'API密钥',
    key_name VARCHAR(100) NOT NULL COMMENT '密钥名称',
    permissions TEXT COMMENT '权限列表',
    rate_limit INT DEFAULT 1000 COMMENT '限流次数/分钟',
    expire_time DATETIME COMMENT '过期时间',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_api_key (api_key),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='API密钥表';

-- 创建API调用日志表
CREATE TABLE IF NOT EXISTS t_api_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    api_key_id BIGINT COMMENT 'API密钥ID',
    user_id BIGINT COMMENT '用户ID',
    endpoint VARCHAR(200) NOT NULL COMMENT '接口端点',
    method VARCHAR(10) NOT NULL COMMENT '请求方法',
    request_params TEXT COMMENT '请求参数',
    response_status INT COMMENT '响应状态码',
    cost_time BIGINT COMMENT '耗时(ms)',
    ip VARCHAR(50) COMMENT 'IP地址',
    user_agent VARCHAR(500) COMMENT 'User-Agent',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_api_key_id (api_key_id),
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time),
    INDEX idx_endpoint (endpoint)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='API调用日志表';

-- 初始化默认角色
INSERT IGNORE INTO t_role (role_name, role_code, description, status) VALUES
('超级管理员', 'SUPER_ADMIN', '拥有所有权限', 1),
('租户管理员', 'TENANT_ADMIN', '管理本租户的所有功能', 1),
('运维人员', 'OPERATOR', '监控、告警、运维功能', 1),
('开发人员', 'DEVELOPER', '任务管理、工作流', 1),
('普通用户', 'USER', '只读权限', 1);

-- 初始化默认权限
INSERT IGNORE INTO t_permission (permission_name, permission_code, permission_type, parent_id, path, icon, sort_order, status) VALUES
('任务管理', 'task', 'MENU', 0, '/task', 'Document', 1, 1),
('任务列表', 'task:list', 'MENU', 1, '/task', '', 1, 1),
('新增任务', 'task:add', 'OPERATION', 1, '', '', 1, 1),
('编辑任务', 'task:edit', 'OPERATION', 1, '', '', 2, 1),
('删除任务', 'task:delete', 'OPERATION', 1, '', '', 3, 1),
('子任务', 'task:sub', 'MENU', 1, '/task/sub', '', 2, 1),
('工作流', 'workflow', 'MENU', 0, '/workflow', 'List', 2, 1),
('工作流列表', 'workflow:list', 'MENU', 7, '/workflow', '', 1, 1),
('工作流模板', 'workflow:template', 'MENU', 7, '/workflow/template', '', 2, 1),
('监控', 'monitor', 'MENU', 0, '/dashboard', 'View', 3, 1),
('仪表盘', 'monitor:dashboard', 'MENU', 10, '/dashboard', '', 1, 1),
('调度日志', 'monitor:schedule', 'MENU', 10, '/operation/schedule', '', 2, 1),
('健康检查', 'monitor:health', 'MENU', 10, '/operation/health', '', 3, 1),
('报表', 'monitor:report', 'MENU', 10, '/operation/report', '', 4, 1),
('告警', 'alarm', 'MENU', 0, '/alarm', 'Connection', 4, 1),
('告警配置', 'alarm:config', 'MENU', 15, '/alarm', '', 1, 1),
('消息队列', 'message', 'MENU', 0, '/message', 'Clock', 5, 1),
('消息队列管理', 'message:manage', 'MENU', 17, '/message', '', 1, 1),
('集群', 'cluster', 'MENU', 0, '/cluster', 'Monitor', 6, 1),
('集群管理', 'cluster:manage', 'MENU', 19, '/cluster', '', 1, 1),
('运维', 'operation', 'MENU', 0, '/operation/executor', 'ChartLine', 7, 1),
('执行器分组', 'operation:executor', 'MENU', 21, '/operation/executor', '', 1, 1),
('审计日志', 'operation:audit', 'MENU', 21, '/operation/audit', '', 2, 1),
('系统管理', 'system', 'MENU', 0, '/system/user', 'Setting', 8, 1),
('用户管理', 'system:user', 'MENU', 24, '/system/user', '', 1, 1),
('角色管理', 'system:role', 'MENU', 24, '/system/role', '', 2, 1),
('权限管理', 'system:permission', 'MENU', 24, '/system/permission', '', 3, 1),
('租户管理', 'system:tenant', 'MENU', 24, '/system/tenant', '', 4, 1),
('API管理', 'api', 'MENU', 0, '/api/key', 'Key', 9, 1),
('API密钥', 'api:key', 'MENU', 29, '/api/key', '', 1, 1),
('API日志', 'api:log', 'MENU', 29, '/api/log', '', 2, 1),
('个人中心', 'profile', 'MENU', 0, '/profile', 'User', 10, 1),
('个人信息', 'profile:info', 'MENU', 32, '/profile', '', 1, 1);

-- 初始化默认租户
INSERT IGNORE INTO t_tenant (tenant_code, tenant_name, status, max_users, max_tasks) VALUES
('default', '默认租户', 1, 100, 1000);

