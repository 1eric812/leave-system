package com.example.leave.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.leave.common.PageResult;
import com.example.leave.entity.LeaveRequest;
import java.util.List;
/**
 * 请假申请服务接口
 *
 * TODO: 定义请假申请相关业务方法
 */
public interface LeaveRequestService extends IService<LeaveRequest> {
    /**
     * 提交请假申请
     */
    boolean submitLeaveRequest(LeaveRequest leaveRequest);

    /**
     * 分页查询请假申请
     */
    PageResult<LeaveRequest> getLeaveRequestPage(Integer page, Integer pageSize, String userId, String status);

    /**
     * 更新请假申请状态
     */
    boolean updateLeaveStatus(String requestId, String status, Integer approvalLevel);

    /**
     * 查询用户所有请假记录
     */
    List<LeaveRequest> getLeaveRequestByUserId(String userId);

    /**
     * 获取待审批列表（按审批人权限过滤）
     */
    List<LeaveRequest> getPendingRequests(String approverId, Integer approverLevel);

}
