import request from './index'

/** 获取某请假单的审批记录 */
export function getApprovalListApi(requestId) {
  return request.get(`/approval-records/${requestId}`)
}

/** 批准请假 */
export function approveLeaveApi(requestId, reason, approverId, approvalLevel) {
  return request.post('/approval-records/approve', { requestId, reason, approverId, approvalLevel })
}

/** 驳回请假 */
export function rejectLeaveApi(requestId, reason, approverId, approvalLevel) {
  return request.post('/approval-records/reject', { requestId, reason, approverId, approvalLevel })
}
