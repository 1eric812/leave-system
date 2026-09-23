-- ========================================
-- 请假管理系统 - MySQL 建表 DDL
-- 数据库: 请假系统
-- ========================================

-- 员工表
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id`          VARCHAR(32)   PRIMARY KEY COMMENT '员工ID（如 emp001, mgr001）',
    `name`        VARCHAR(50)   NOT NULL    COMMENT '姓名',
    `dept`        VARCHAR(50)   NOT NULL    COMMENT '所属部门',
    `role`        VARCHAR(20)   NOT NULL    COMMENT '角色: employee-员工, manager1-部门经理, manager2-总经理',
    `level`       INT           NOT NULL    COMMENT '级别: 1-员工, 2-部门经理, 3-总经理',
    `password`    VARCHAR(100)  NOT NULL    COMMENT '登录密码',
    `created_at`  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工表';

-- 请假申请表
CREATE TABLE IF NOT EXISTS `leave_request` (
    `id`              VARCHAR(32)   PRIMARY KEY COMMENT '申请编号（如 REQ001）',
    `user_id`         VARCHAR(32)   NOT NULL    COMMENT '申请人ID',
    `user_name`       VARCHAR(50)   NOT NULL    COMMENT '申请人姓名',
    `dept`            VARCHAR(50)   NOT NULL    COMMENT '申请人部门',
    `leave_type`      VARCHAR(20)   NOT NULL    COMMENT '假期类型: 年假/病假/婚假/丧假/其他',
    `start_date`      DATE          NOT NULL    COMMENT '开始日期',
    `end_date`        DATE          NOT NULL    COMMENT '结束日期',
    `duration`        INT           NOT NULL    COMMENT '请假天数',
    `reason`          TEXT          NOT NULL    COMMENT '请假原因',
    `status`          VARCHAR(20)   NOT NULL DEFAULT 'pending' COMMENT '状态: pending-待审批, approved-已批准, rejected-已驳回',
    `approval_level`  INT           NOT NULL DEFAULT 1 COMMENT '审批级别: 1-一级审批, 2-二级审批',
    `created_at`      TIMESTAMP     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`      TIMESTAMP     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='请假申请表';

-- 审批记录表
CREATE TABLE IF NOT EXISTS `approval_record` (
    `id`              VARCHAR(32)   PRIMARY KEY COMMENT '审批编号（如 APR001）',
    `request_id`      VARCHAR(32)   NOT NULL    COMMENT '关联请假申请ID',
    `approver_id`     VARCHAR(32)   NOT NULL    COMMENT '审批人ID',
    `approver_name`   VARCHAR(50)   NOT NULL    COMMENT '审批人姓名',
    `action`          VARCHAR(20)   NOT NULL    COMMENT '操作: approve-批准, reject-驳回',
    `reason`          VARCHAR(500)  DEFAULT ''  COMMENT '审批意见',
    `approval_level`  INT           NOT NULL    COMMENT '审批级别: 1-一级, 2-二级',
    `timestamp`       TIMESTAMP     DEFAULT CURRENT_TIMESTAMP COMMENT '审批时间',
    KEY `idx_request_id` (`request_id`),
    KEY `idx_approver_id` (`approver_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审批记录表';

-- ========================================
-- 初始数据
-- ========================================
INSERT IGNORE INTO `sys_user` (`id`, `name`, `dept`, `role`, `level`, `password`) VALUES
('emp001', '张三', '技术部', 'employee', 1, '123456'),
('emp002', '王五', '销售部', 'employee', 1, '123456'),
('mgr001', '李四', '技术部', 'manager1', 2, '123456'),
('mgr002', '赵六', '行政部', 'manager2', 3, '123456');

-- 示例请假申请
INSERT IGNORE INTO `leave_request` (`id`, `user_id`, `user_name`, `dept`, `leave_type`, `start_date`, `end_date`, `duration`, `reason`, `status`, `approval_level`, `created_at`, `updated_at`) VALUES
('REQ001', 'emp001', '张三', '技术部', '年假', '2024-06-10', '2024-06-12', 3, '回家处理私人事务', 'approved', 1, '2024-06-08 10:00:00', '2024-06-09 10:00:00');

-- 示例审批记录
INSERT IGNORE INTO `approval_record` (`id`, `request_id`, `approver_id`, `approver_name`, `action`, `reason`, `approval_level`, `timestamp`) VALUES
('APR001', 'REQ001', 'mgr001', '李四', 'approve', '同意', 1, '2024-06-09 10:00:00');
