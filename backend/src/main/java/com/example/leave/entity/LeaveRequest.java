package com.example.leave.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 请假申请表实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "leave_request")
public class LeaveRequest {

    /** 请假单ID（如 REQ001） */
    @Id
    @Column(length = 32, nullable = false)
    private String id;

    /** 申请人ID（关联 sys_user.id） */
    @Column(name = "user_id", length = 32, nullable = false)
    private String userId;

    /** 申请人姓名 */
    @Column(name = "user_name", length = 50, nullable = false)
    private String userName;

    /** 所属部门 */
    @Column(length = 50, nullable = false)
    private String dept;

    /** 请假类型（年假/病假/婚假/丧假/事假/其他） */
    @Column(name = "leave_type", length = 20, nullable = false)
    private String leaveType;

    /** 开始日期 */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /** 结束日期 */
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    /** 请假天数 */
    @Column(nullable = false)
    private Integer duration;

    /** 请假原因 */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String reason;

    /** 状态（pending / approved / rejected） */
    @Column(length = 20, nullable = false)
    private String status;

    /** 当前审批级别 */
    @Column(name = "approval_level", nullable = false)
    private Integer approvalLevel;

    /** 创建时间 */
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
