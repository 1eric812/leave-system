package com.example.leave.controller;

import com.example.leave.common.Result;
import com.example.leave.entity.ApprovalRecord;
import com.example.leave.service.ApprovalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 审批记录控制器
 */
@RestController
@RequestMapping("/api/approval-records")
public class ApprovalRecordController {

    @Autowired
    private ApprovalRecordService approvalRecordService;

    /** 获取某请假单的审批记录 */
    @GetMapping("/{requestId}")
    public Result<List<ApprovalRecord>> getApprovalRecords(@PathVariable String requestId) {
        List<ApprovalRecord> records = approvalRecordService.getApprovalRecordByRequestId(requestId);
        return Result.success(records);
    }

    /** 批准请假 */
    @PostMapping("/approve")
    public Result<Void> approveLeave(@RequestBody Map<String, Object> params) {
        String requestId = (String) params.get("requestId");
        String reason = (String) params.get("reason");
        if (reason == null || reason.isEmpty()) {
            reason = "同意";
        }

        try {
            // 从请求中获取审批人ID，前端应在后续传过来
            String approverId = (String) params.get("approverId");
            if (approverId == null) {
                return Result.badRequest("审批人ID不能为空");
            }
            Integer approvalLevel = (Integer) params.get("approvalLevel");
            if (approvalLevel == null) {
                approvalLevel = 1;
            }
            approvalRecordService.handleApproval(requestId, approverId, "approve", reason, approvalLevel);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /** 驳回请假 */
    @PostMapping("/reject")
    public Result<Void> rejectLeave(@RequestBody Map<String, Object> params) {
        String requestId = (String) params.get("requestId");
        String reason = (String) params.get("reason");

        try {
            String approverId = (String) params.get("approverId");
            if (approverId == null) {
                return Result.badRequest("审批人ID不能为空");
            }
            Integer approvalLevel = (Integer) params.get("approvalLevel");
            if (approvalLevel == null) {
                approvalLevel = 1;
            }
            approvalRecordService.handleApproval(requestId, approverId, "reject", reason != null ? reason : "驳回", approvalLevel);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }
}
