-- ========================================
-- 请假管理系统 - 建库建表 + 模拟数据
-- ========================================

CREATE DATABASE IF NOT EXISTS `leave_system` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `leave_system`;

-- ========== 建表 ==========

CREATE TABLE IF NOT EXISTS `sys_user` (
    `id`          VARCHAR(32)   PRIMARY KEY COMMENT '员工ID',
    `name`        VARCHAR(50)   NOT NULL    COMMENT '姓名',
    `dept`        VARCHAR(50)   NOT NULL    COMMENT '所属部门',
    `role`        VARCHAR(20)   NOT NULL    COMMENT '角色: employee-员工, manager1-部门经理, manager2-总经理',
    `level`       INT           NOT NULL    COMMENT '级别: 1-员工, 2-部门经理, 3-总经理',
    `password`    VARCHAR(100)  NOT NULL    COMMENT '登录密码',
    `created_at`  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工表';

CREATE TABLE IF NOT EXISTS `leave_request` (
    `id`              VARCHAR(32)   PRIMARY KEY COMMENT '申请编号',
    `user_id`         VARCHAR(32)   NOT NULL    COMMENT '申请人ID',
    `user_name`       VARCHAR(50)   NOT NULL    COMMENT '申请人姓名',
    `dept`            VARCHAR(50)   NOT NULL    COMMENT '申请人部门',
    `leave_type`      VARCHAR(20)   NOT NULL    COMMENT '假期类型',
    `start_date`      DATE          NOT NULL    COMMENT '开始日期',
    `end_date`        DATE          NOT NULL    COMMENT '结束日期',
    `duration`        INT           NOT NULL    COMMENT '请假天数',
    `reason`          TEXT          NOT NULL    COMMENT '请假原因',
    `status`          VARCHAR(20)   NOT NULL DEFAULT 'pending' COMMENT '状态',
    `approval_level`  INT           NOT NULL DEFAULT 1 COMMENT '审批级别',
    `created_at`      TIMESTAMP     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`      TIMESTAMP     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='请假申请表';

CREATE TABLE IF NOT EXISTS `approval_record` (
    `id`              VARCHAR(32)   PRIMARY KEY COMMENT '审批编号',
    `request_id`      VARCHAR(32)   NOT NULL    COMMENT '关联请假申请ID',
    `approver_id`     VARCHAR(32)   NOT NULL    COMMENT '审批人ID',
    `approver_name`   VARCHAR(50)   NOT NULL    COMMENT '审批人姓名',
    `action`          VARCHAR(20)   NOT NULL    COMMENT '操作: approve-批准, reject-驳回',
    `reason`          VARCHAR(500)  DEFAULT ''  COMMENT '审批意见',
    `approval_level`  INT           NOT NULL    COMMENT '审批级别',
    `timestamp`       TIMESTAMP     DEFAULT CURRENT_TIMESTAMP COMMENT '审批时间',
    KEY `idx_request_id` (`request_id`),
    KEY `idx_approver_id` (`approver_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审批记录表';

-- ========== 清空旧数据（便于重新导入） ==========
DELETE FROM `approval_record`;
DELETE FROM `leave_request`;
DELETE FROM `sys_user`;

-- ========== 用户数据 ==========
INSERT INTO `sys_user` (`id`, `name`, `dept`, `role`, `level`, `password`) VALUES
('emp001', '张三', '技术部', 'employee',  1, '123456'),
('emp002', '王五', '销售部', 'employee',  1, '123456'),
('mgr001', '李四', '技术部', 'manager1',  2, '123456'),
('mgr002', '赵六', '行政部', 'manager2',  3, '123456');

-- ========== 模拟请假数据 ==========

-- 场景覆盖: 已批准(一级/二级)、待审批(一级/二级)、已驳回
-- 时间设计: 昨天(2026-06-02)有大量的操作记录

INSERT INTO `leave_request` (`id`, `user_id`, `user_name`, `dept`, `leave_type`, `start_date`, `end_date`, `duration`, `reason`, `status`, `approval_level`, `created_at`, `updated_at`) VALUES

-- OK: 张三 年假3天 已批准（一级审批）[原始示例]
('REQ001', 'emp001', '张三', '技术部', '年假', '2024-06-10', '2024-06-12', 3,
 '回家处理私人事务',
 'approved', 1, '2024-06-08 10:00:00', '2024-06-09 10:00:00'),

-- OK: 王五 病假2天 已批准（一级审批）
('REQ002', 'emp002', '王五', '销售部', '病假', '2026-05-25', '2026-05-26', 2,
 '感冒发烧，需要休息两天',
 'approved', 1, '2026-05-24 09:30:00', '2026-05-24 15:00:00'),

-- OK: 张三 婚假5天 已批准（二级审批 李四→赵六）
('REQ003', 'emp001', '张三', '技术部', '婚假', '2026-06-10', '2026-06-14', 5,
 '本人婚礼，需回老家举办，包括来回路上时间',
 'approved', 2, '2026-05-20 08:00:00', '2026-06-01 10:00:00'),

-- PENDING-二级: 王五 年假5天（李四已初审，待赵六终审）
('REQ004', 'emp002', '王五', '销售部', '年假', '2026-06-08', '2026-06-12', 5,
 '计划带家人去云南旅游，已经订好机票和酒店',
 'pending', 2, '2026-06-02 09:00:00', '2026-06-02 10:00:00'),

-- OK: 张三 病假1天 已批准（一级审批）
('REQ005', 'emp001', '张三', '技术部', '病假', '2026-05-28', '2026-05-28', 1,
 '身体不适，需要休整一天',
 'approved', 1, '2026-05-27 22:00:00', '2026-05-28 08:00:00'),

-- PENDING-一级: 王五 事假3天（等李四审批）
('REQ006', 'emp002', '王五', '销售部', '其他', '2026-06-05', '2026-06-07', 3,
 '家里有急事需要处理，需回老家一趟',
 'pending', 1, '2026-06-02 14:30:00', '2026-06-02 14:30:00'),

-- REJECTED: 张三 丧假2天（李四驳回）
('REQ007', 'emp001', '张三', '技术部', '丧假', '2026-05-15', '2026-05-16', 2,
 '回老家参加亲戚葬礼',
 'rejected', 1, '2026-05-13 11:00:00', '2026-05-13 14:00:00'),

-- OK: 王五 年假1天 已批准（一级审批）
('REQ008', 'emp002', '王五', '销售部', '年假', '2026-05-20', '2026-05-20', 1,
 '个人年假调休',
 'approved', 1, '2026-05-18 16:00:00', '2026-05-19 09:00:00'),

-- OK: 张三 病假4天 已批准（二级审批 李四→赵六）
('REQ009', 'emp001', '张三', '技术部', '病假', '2026-06-15', '2026-06-18', 4,
 '需住院做一个小手术，医生建议休养4天',
 'approved', 2, '2026-06-01 08:30:00', '2026-06-02 08:30:00'),

-- PENDING-二级: 王五 婚假7天（李四已初审，待赵六终审）
('REQ010', 'emp002', '王五', '销售部', '婚假', '2026-07-01', '2026-07-07', 7,
 '七月举办婚礼，需请假筹备婚礼及婚假',
 'pending', 2, '2026-06-02 10:00:00', '2026-06-02 11:00:00'),

-- PENDING-一级: 张三 年假2天（刚提交，等李四审批）
('REQ011', 'emp001', '张三', '技术部', '年假', '2026-06-18', '2026-06-19', 2,
 '调休，连上周末出去放松一下',
 'pending', 1, '2026-06-03 08:15:00', '2026-06-03 08:15:00'),

-- REJECTED: 王五 病假3天（李四驳回）
('REQ012', 'emp002', '王五', '销售部', '病假', '2026-05-06', '2026-05-08', 3,
 '感觉身体不舒服，想请假检查',
 'rejected', 1, '2026-05-05 09:00:00', '2026-05-05 11:00:00');

-- ========== 审批记录 ==========

INSERT INTO `approval_record` (`id`, `request_id`, `approver_id`, `approver_name`, `action`, `reason`, `approval_level`, `timestamp`) VALUES

-- REQ001: 李四批准张三的3天年假（一级审批）
('APR001', 'REQ001', 'mgr001', '李四', 'approve', '同意', 1, '2024-06-09 10:00:00'),

-- REQ002: 李四批准王五的2天病假（一级审批）
('APR002', 'REQ002', 'mgr001', '李四', 'approve', '好好休息，早日康复', 1, '2026-05-24 15:00:00'),

-- REQ003: 李四初审张三的5天婚假（一级审批）
('APR003', 'REQ003', 'mgr001', '李四', 'approve', '恭喜！初审同意', 1, '2026-05-21 09:00:00'),
-- REQ003: 赵六终审张三的5天婚假（二级审批）
('APR004', 'REQ003', 'mgr002', '赵六', 'approve', '恭喜结婚，祝幸福美满！终审同意', 2, '2026-06-01 10:00:00'),

-- REQ004: 李四初审王五的5天年假（一级审批）
('APR005', 'REQ004', 'mgr001', '李四', 'approve', '工作已交接，同意初审', 1, '2026-06-02 10:00:00'),

-- REQ005: 李四批准张三的1天病假（一级审批）
('APR006', 'REQ005', 'mgr001', '李四', 'approve', '注意身体，好好休息', 1, '2026-05-28 08:00:00'),

-- REQ007: 李四驳回张三的丧假申请
('APR007', 'REQ007', 'mgr001', '李四', 'reject', '近期项目紧张，暂不能批准，请与主管沟通协调', 1, '2026-05-13 14:00:00'),

-- REQ008: 李四批准王五的1天年假（一级审批）
('APR008', 'REQ008', 'mgr001', '李四', 'approve', '同意', 1, '2026-05-19 09:00:00'),

-- REQ009: 李四初审张三的4天病假（一级审批）
('APR009', 'REQ009', 'mgr001', '李四', 'approve', '身体要紧，初审同意，请安心治疗', 1, '2026-06-01 16:00:00'),
-- REQ009: 赵六终审张三的4天病假（二级审批）
('APR010', 'REQ009', 'mgr002', '赵六', 'approve', '终审同意，祝早日康复回到工作岗位', 2, '2026-06-02 08:30:00'),

-- REQ010: 李四初审王五的7天婚假（一级审批）
('APR011', 'REQ010', 'mgr001', '李四', 'approve', '初审同意，恭喜恭喜！', 1, '2026-06-02 11:00:00'),

-- REQ012: 李四驳回王五的病假
('APR012', 'REQ012', 'mgr001', '李四', 'reject', '请提供医院开具的证明后再提交申请', 1, '2026-05-05 11:00:00');

-- ========== 验证 ==========
SELECT '========== 数据导入完成 ==========' AS '';
SELECT CONCAT('sys_user: ', COUNT(*), ' 条') AS `用户表` FROM sys_user;
SELECT CONCAT('leave_request: ', COUNT(*), ' 条') AS `请假表` FROM leave_request;
SELECT CONCAT('approval_record: ', COUNT(*), ' 条') AS `审批记录表` FROM approval_record;

SELECT '--- 请假单概况 ---' AS '';
SELECT `id`, `user_name`, `leave_type`, `duration`, `status`, `approval_level` FROM leave_request ORDER BY `created_at`;
