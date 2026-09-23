import request from './index'

/** 用户登录 */
export function loginApi(userId, password) {
  return request.post('/users/login', { userId, password })
}

/** 获取当前用户信息 */
export function getCurrentUserApi() {
  return request.get('/users/current')
}

/** 获取用户列表（管理员可见） */
export function getUserListApi() {
  return request.get('/users')
}

/** 获取可见用户列表（级别更低的下属） */
export function getVisibleUsersApi() {
  return request.get('/users/visible')
}

/** 添加新员工 */
export function addUserApi(data) {
  return request.post('/users', data)
}

/** 删除员工 */
export function deleteUserApi(userId) {
  return request.delete(`/users/${userId}`)
}
