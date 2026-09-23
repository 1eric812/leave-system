package com.example.leave.controller;

import com.example.leave.common.Result;
import com.example.leave.entity.LeaveRequest;
import com.example.leave.service.LeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 请假申请控制器
 */
@RestController
@RequestMapping("/api/leave-requests")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    /** 获取所有请假申请 */
    @GetMapping
    public Result<List<LeaveRequest>> getAllLeaveRequests() {
        List<LeaveRequest> list = leaveRequestService.list();
        return Result.success(list);
    }

    /** 获取我的请假申请（需传 userId 参数） */
    @GetMapping("/my")
    public Result<List<LeaveRequest>> getMyLeaveRequests(@RequestParam String userId) {
        List<LeaveRequest> list = leaveRequestService.getLeaveRequestByUserId(userId);
        return Result.success(list);
    }

    /** 获取待审批列表（按当前用户权限过滤） */
    @GetMapping("/pending")
    public Result<List<LeaveRequest>> getPendingLeaveRequests(
            @RequestParam(required = false) String approverId,
            @RequestParam(required = false) Integer approverLevel) {
        List<LeaveRequest> list = leaveRequestService.getPendingRequests(approverId, approverLevel);
        return Result.success(list);
    }

    /** 根据ID获取请假详情 */
    @GetMapping("/{id}")
    public Result<LeaveRequest> getLeaveRequestById(@PathVariable String id) {
        LeaveRequest leaveRequest = leaveRequestService.getById(id);
        if (leaveRequest == null) {
            return Result.notFound("请假申请不存在");
        }
        return Result.success(leaveRequest);
    }

    /** 提交请假申请 */
    @PostMapping
    public Result<LeaveRequest> submitLeaveRequest(@RequestBody LeaveRequest leaveRequest) {
        if (leaveRequest.getUserId() == null) {
            return Result.badRequest("用户ID不能为空");
        }

        boolean success = leaveRequestService.submitLeaveRequest(leaveRequest);
        if (!success) {
            return Result.error("提交失败，用户不存在");
        }

        // 返回完整的请假单
        LeaveRequest saved = leaveRequestService.getById(leaveRequest.getId());
        return Result.success(saved);
    }

    /** 修改请假申请 */
    @PutMapping("/{id}")
    public Result<LeaveRequest> updateLeaveRequest(@PathVariable String id, @RequestBody Map<String, Object> data) {
        LeaveRequest existing = leaveRequestService.getById(id);
        if (existing == null) {
            return Result.notFound("请假申请不存在");
        }
        if (!"pending".equals(existing.getStatus())) {
            return Result.error(400, "只有待审批的请假单才能修改");
        }

        // 更新字段
        if (data.containsKey("leaveType")) {
            existing.setLeaveType((String) data.get("leaveType"));
        }

        if (data.containsKey("startDate")) {
            existing.setStartDate(LocalDate.parse((String) data.get("startDate")));
        }
        if (data.containsKey("endDate")) {
            existing.setEndDate(LocalDate.parse((String) data.get("endDate")));
        }
        if (data.containsKey("duration")) {
            existing.setDuration((Integer) data.get("duration"));
        }
        if (data.containsKey("reason")) {
            existing.setReason((String) data.get("reason"));
        }
        // 重新计算审批级别（经理申请始终一级）
        int duration = existing.getDuration() != null ? existing.getDuration() : 0;
        existing.setApprovalLevel(duration > 3 ? 2 : 1);
        existing.setUpdatedAt(LocalDateTime.now());

        leaveRequestService.updateById(existing);
        return Result.success(existing);
    }

    /** 删除请假申请 */
    @DeleteMapping("/{id}")
    public Result<Void> deleteLeaveRequest(@PathVariable String id) {
        LeaveRequest existing = leaveRequestService.getById(id);
        if (existing == null) {
            return Result.notFound("请假申请不存在");
        }
        if (!"pending".equals(existing.getStatus())) {
            return Result.error(400, "只能删除待审请假单");
        }
        leaveRequestService.removeById(id);
        return Result.success();
    }

    /** 分页查询（保留原有接口） */
    @RequestMapping("/page")
    public Result<?> getLeaveRequestPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String status) {
        return Result.success(leaveRequestService.getLeaveRequestPage(page, pageSize, userId, status));
    }
}
