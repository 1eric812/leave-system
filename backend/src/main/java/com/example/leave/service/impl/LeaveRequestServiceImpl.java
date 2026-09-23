package com.example.leave.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.leave.common.PageResult;
import com.example.leave.entity.ApprovalRecord;
import com.example.leave.entity.LeaveRequest;
import com.example.leave.entity.SysUser;
import com.example.leave.mapper.ApprovalRecordMapper;
import com.example.leave.mapper.LeaveRequestMapper;
import com.example.leave.service.LeaveRequestService;
import com.example.leave.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 请假申请服务实现
 */
@Service
public class LeaveRequestServiceImpl extends ServiceImpl<LeaveRequestMapper, LeaveRequest> implements LeaveRequestService {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private ApprovalRecordMapper approvalRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitLeaveRequest(LeaveRequest leaveRequest) {
        // 1. 生成唯一申请ID
        if (leaveRequest.getId() == null) {
            leaveRequest.setId("REQ" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        // 2. 补全申请人信息
        SysUser user = sysUserService.getUserById(leaveRequest.getUserId());
        if (user == null) {
            return false;
        }
        leaveRequest.setUserName(user.getName());
        leaveRequest.setDept(user.getDept());
        leaveRequest.setCreatedAt(LocalDateTime.now());
        leaveRequest.setUpdatedAt(LocalDateTime.now());

        int duration = leaveRequest.getDuration() != null ? leaveRequest.getDuration() : 0;

        // 3. 根据用户级别和请假天数确定审批流程
        if (user.getLevel() == 3) {
            // 总经理：自动批准
            leaveRequest.setStatus("approved");
            leaveRequest.setApprovalLevel(1);
        } else if (user.getLevel() == 2) {
            // 部门经理：无论天数，必须总经理审批
            leaveRequest.setStatus("pending");
            leaveRequest.setApprovalLevel(1);
        } else {
            // 普通员工：>3天需二级审批
            leaveRequest.setStatus("pending");
            leaveRequest.setApprovalLevel(duration > 3 ? 2 : 1);
        }

        // 4. 保存申请
        boolean saved = save(leaveRequest);

        // 5. 总经理自动审批自己的申请
        if (user.getLevel() == 3) {
            ApprovalRecord record = new ApprovalRecord();
            record.setId("APR" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            record.setRequestId(leaveRequest.getId());
            record.setApproverId(user.getId());
            record.setApproverName(user.getName());
            record.setAction("approve");
            record.setReason("总经理自动审批通过");
            record.setApprovalLevel(1);
            record.setTimestamp(LocalDateTime.now());
            approvalRecordMapper.insert(record);
        }

        return saved;
    }

    @Override
    public PageResult<LeaveRequest> getLeaveRequestPage(Integer page, Integer pageSize, String userId, String status) {
        Page<LeaveRequest> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<LeaveRequest> wrapper = new LambdaQueryWrapper<>();
        // 按用户ID过滤
        if (userId != null && !userId.isEmpty()) {
            wrapper.eq(LeaveRequest::getUserId, userId);
        }
        // 按状态过滤
        if (status != null && !status.isEmpty()) {
            wrapper.eq(LeaveRequest::getStatus, status);
        }
        // 按创建时间倒序
        wrapper.orderByDesc(LeaveRequest::getCreatedAt);

        IPage<LeaveRequest> iPage = baseMapper.selectPage(pageParam, wrapper);
        return PageResult.of(page, pageSize, iPage.getTotal(), iPage.getRecords());
    }

    @Override
    public boolean updateLeaveStatus(String requestId, String status, Integer approvalLevel) {
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setId(requestId);
        leaveRequest.setStatus(status);
        leaveRequest.setApprovalLevel(approvalLevel);
        leaveRequest.setUpdatedAt(LocalDateTime.now());
        return updateById(leaveRequest);
    }

    @Override
    public List<LeaveRequest> getLeaveRequestByUserId(String userId) {
        LambdaQueryWrapper<LeaveRequest> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LeaveRequest::getUserId, userId)
                .orderByDesc(LeaveRequest::getCreatedAt);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<LeaveRequest> getPendingRequests(String approverId, Integer approverLevel) {
        LambdaQueryWrapper<LeaveRequest> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LeaveRequest::getStatus, "pending")
                .orderByDesc(LeaveRequest::getCreatedAt);
        List<LeaveRequest> allPending = baseMapper.selectList(wrapper);

        // 如果没有审批人信息，返回所有待审批（用于兼容）
        if (approverId == null || approverLevel == null) {
            return allPending;
        }

        // 根据审批人级别过滤
        return allPending.stream().filter(req -> {
            SysUser requester = sysUserService.getUserById(req.getUserId());
            if (requester == null) return false;

            if (approverLevel == 3) {
                // 总经理：能看到部门经理的申请 + 需要二级审批且一级已过的员工申请
                if (requester.getLevel() == 2) return true;
                if (req.getApprovalLevel() == 2) {
                    // 检查是否一级已审批通过
                    return approvalRecordMapper.selectList(
                        new LambdaQueryWrapper<ApprovalRecord>()
                            .eq(ApprovalRecord::getRequestId, req.getId())
                            .eq(ApprovalRecord::getAction, "approve")
                            .eq(ApprovalRecord::getApprovalLevel, 1)
                    ).size() > 0;
                }
                return false;
            } else if (approverLevel == 2) {
                // 部门经理：只能审批员工（level 1）的申请
                return requester.getLevel() == 1;
            }
            return false;
        }).toList();
    }
}