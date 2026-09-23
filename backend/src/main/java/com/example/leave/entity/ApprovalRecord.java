package com.example.leave.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 审批记录实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "approval_record")
public class ApprovalRecord {

    /** 审批记录ID（如 APR001） */
    @Id
    @Column(length = 32, nullable = false)
    private String id;

    /** 关联的请假申请ID */
    @Column(name = "request_id", length = 32, nullable = false)
    private String requestId;

    /** 审批人ID */
    @Column(name = "approver_id", length = 32, nullable = false)
    private String approverId;

    /** 审批人姓名 */
    @Column(name = "approver_name", length = 50, nullable = false)
    private String approverName;

    /** 审批动作（approve / reject） */
    @Column(length = 20, nullable = false)
    private String action;

    /** 审批意见 */
    @Column(length = 500)
    private String reason;

    /** 审批级别 */
    @Column(name = "approval_level", nullable = false)
    private Integer approvalLevel;

    /** 审批时间 */
    @Column(nullable = false)
    private LocalDateTime timestamp;
}
