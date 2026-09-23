package com.example.leave.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.leave.entity.ApprovalRecord;

import java.util.List;

/**
 * 审批记录服务接口
 */
public interface ApprovalRecordService extends IService<ApprovalRecord> {
    /**
     * 新增审批记录
     */
    boolean addApprovalRecord(ApprovalRecord approvalRecord);

    /**
     * 根据请假申请ID查询审批历史
     */
    List<ApprovalRecord> getApprovalRecordByRequestId(String requestId);

    /**
     * 处理审批操作（批准/驳回）
     */
    boolean handleApproval(String requestId, String approverId, String action, String reason, Integer approvalLevel);
}