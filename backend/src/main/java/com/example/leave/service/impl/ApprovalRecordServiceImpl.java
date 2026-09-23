package com.example.leave.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.leave.entity.ApprovalRecord;
import com.example.leave.entity.LeaveRequest;
import com.example.leave.entity.SysUser;
import com.example.leave.mapper.ApprovalRecordMapper;
import com.example.leave.service.ApprovalRecordService;
import com.example.leave.service.LeaveRequestService;
import com.example.leave.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 审批记录服务实现
 */
@Service
public class ApprovalRecordServiceImpl extends ServiceImpl<ApprovalRecordMapper, ApprovalRecord> implements ApprovalRecordService {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Autowired
    private SysUserService sysUserService;

    @Override
    public boolean addApprovalRecord(ApprovalRecord approvalRecord) {
        return save(approvalRecord);
    }

    @Override
    public List<ApprovalRecord> getApprovalRecordByRequestId(String requestId) {
        LambdaQueryWrapper<ApprovalRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApprovalRecord::getRequestId, requestId)
                .orderByAsc(ApprovalRecord::getTimestamp);
        return baseMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleApproval(String requestId, String approverId, String action, String reason, Integer approvalLevel) {
        // 1. 获取请假申请
        LeaveRequest leaveRequest = leaveRequestService.getById(requestId);
        if (leaveRequest == null) {
            throw new RuntimeException("请假申请不存在");
        }
        if (!"pending".equals(leaveRequest.getStatus())) {
            throw new RuntimeException("该请假单已处理");
        }

        // 2. 获取审批人信息
        SysUser approver = sysUserService.getById(approverId);
        if (approver == null) {
            throw new RuntimeException("审批人不存在");
        }
        if (approver.getLevel() < 2) {
            throw new RuntimeException("普通员工无审批权限");
        }

        // 3. 获取申请人信息
        SysUser requester = sysUserService.getById(leaveRequest.getUserId());
        if (requester == null) {
            throw new RuntimeException("申请人不存在");
        }

        // 4. 权限校验
        if (approver.getLevel() == 2) {
            // 部门经理只能审批普通员工的申请
            if (requester.getLevel() != 1) {
                throw new RuntimeException("部门经理只能审批普通员工的请假申请");
            }
        } else if (approver.getLevel() == 3) {
            // 总经理可审批部门经理申请，以及需要二级审批的员工申请
            if (requester.getLevel() == 2) {
                // 部门经理的申请 → 总经理直接审批
                approvalLevel = 1;
            } else if (leaveRequest.getApprovalLevel() == 2) {
                // 员工二级审批：确保一级已经通过
                LambdaQueryWrapper<ApprovalRecord> checkWrapper = new LambdaQueryWrapper<>();
                checkWrapper.eq(ApprovalRecord::getRequestId, requestId)
                        .eq(ApprovalRecord::getAction, "approve")
                        .eq(ApprovalRecord::getApprovalLevel, 1);
                if (baseMapper.selectList(checkWrapper).isEmpty()) {
                    throw new RuntimeException("需先完成一级审批");
                }
            } else {
                throw new RuntimeException("总经理无权审批此申请");
            }
        }

        // 5. 检查是否已审批过
        LambdaQueryWrapper<ApprovalRecord> dupWrapper = new LambdaQueryWrapper<>();
        dupWrapper.eq(ApprovalRecord::getRequestId, requestId)
                .eq(ApprovalRecord::getApproverId, approverId)
                .eq(ApprovalRecord::getAction, "approve");
        if (!baseMapper.selectList(dupWrapper).isEmpty()) {
            throw new RuntimeException("您已审批过该申请，请勿重复操作");
        }

        // 6. 创建审批记录
        ApprovalRecord record = new ApprovalRecord();
        record.setId("APR" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        record.setRequestId(requestId);
        record.setApproverId(approverId);
        record.setApproverName(approver.getName());
        record.setAction(action);
        record.setReason(reason);
        record.setApprovalLevel(approvalLevel);
        record.setTimestamp(LocalDateTime.now());
        save(record);

        // 7. 更新请假申请状态
        if ("reject".equals(action)) {
            // 驳回：直接结束
            leaveRequestService.updateLeaveStatus(requestId, "rejected", leaveRequest.getApprovalLevel());
        } else if ("approve".equals(action)) {
            if (requester.getLevel() == 2) {
                // 部门经理申请：总经理批准后直接通过
                leaveRequestService.updateLeaveStatus(requestId, "approved", 1);
            } else if (leaveRequest.getApprovalLevel() == 2 && approvalLevel == 1) {
                // 员工二级审批：一级通过，等待二级
                leaveRequestService.updateLeaveStatus(requestId, "pending", 2);
            } else {
                // 一级审批完成
                leaveRequestService.updateLeaveStatus(requestId, "approved", leaveRequest.getApprovalLevel());
            }
        }

        return true;
    }
}
