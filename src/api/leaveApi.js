import request from './index'

/** 获取所有请假申请（管理员/员工按权限过滤） */
export function getLeaveListApi() {
  return request.get('/leave-requests')
}

/** 获取我的请假申请 */
export function getMyLeaveListApi() {
  return request.get('/leave-requests/my')
}

/** 获取待审批列表（按审批人权限过滤） */
export function getPendingLeaveListApi(approverId, approverLevel) {
  const params = {}
  if (approverId) params.approverId = approverId
  if (approverLevel) params.approverLevel = approverLevel
  return request.get('/leave-requests/pending', { params })
}

/** 根据ID获取请假详情 */
export function getLeaveByIdApi(id) {
  return request.get(`/leave-requests/${id}`)
}

/** 新增请假申请 */
export function createLeaveApi(data) {
  return request.post('/leave-requests', data)
}

/** 修改请假申请（仅待审状态可改） */
export function updateLeaveApi(id, data) {
  return request.put(`/leave-requests/${id}`, data)
}

/** 删除请假申请 */
export function deleteLeaveApi(id) {
  return request.delete(`/leave-requests/${id}`)
}
