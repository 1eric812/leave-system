# 请假管理系统 - API 接口设计

> 基础路径：`http://localhost:8080/api`

---

## 1. 认证模块

### POST `/api/auth/login`

用户登录。

**请求体：**
```json
{
  "userId": "emp001",
  "password": "123456"
}
```

**响应（成功）：**
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "id": "emp001",
    "name": "张三",
    "dept": "技术部",
    "role": "employee",
    "level": 1
  }
}
```

**响应（失败）：**
```json
{
  "code": 401,
  "message": "账号或密码错误"
}
```

---

## 2. 员工管理

### GET `/api/users`

获取当前用户可见的员工列表（仅返回比自己级别低的用户）。

**请求头：** `Authorization: Bearer <token>`

**响应：**
```json
{
  "code": 200,
  "data": [
    {
      "id": "emp001",
      "name": "张三",
      "dept": "技术部",
      "role": "employee",
      "level": 1
    }
  ]
}
```

### POST `/api/users`

添加新员工。

**请求体：**
```json
{
  "name": "新员工",
  "dept": "技术部",
  "role": "employee"
}
```

**响应：**
```json
{
  "code": 200,
  "message": "添加成功",
  "data": {
    "id": "emp003",
    "name": "新员工",
    "dept": "技术部",
    "role": "employee",
    "level": 1,
    "password": "123456"
  }
}
```

### DELETE `/api/users/{userId}`

删除员工。

**响应：**
```json
{
  "code": 200,
  "message": "删除成功"
}
```

---

## 3. 请假申请

### GET `/api/leave-requests/my`

获取当前用户的请假记录。

**响应：**
```json
{
  "code": 200,
  "data": [
    {
      "id": "REQ001",
      "userId": "emp001",
      "userName": "张三",
      "dept": "技术部",
      "leaveType": "年假",
      "startDate": "2024-06-10",
      "endDate": "2024-06-12",
      "duration": 3,
      "reason": "回家处理私人事务",
      "status": "approved",
      "approvalLevel": 1,
      "createdAt": "2024-06-08T10:00:00",
      "updatedAt": "2024-06-09T10:00:00"
    }
  ]
}
```

### POST `/api/leave-requests`

提交请假申请。

**请求体：**
```json
{
  "leaveType": "年假",
  "startDate": "2024-07-01",
  "endDate": "2024-07-03",
  "reason": "家庭旅行"
}
```

### PUT `/api/leave-requests/{id}`

修改待审批的请假申请。

**请求体：** 同 POST

### DELETE `/api/leave-requests/{id}`

删除自己的待审请假单。

---

## 4. 审批模块

### GET `/api/approval/pending`

获取当前用户的待审批列表。

**响应：**
```json
{
  "code": 200,
  "data": [
    {
      "id": "REQ002",
      "userName": "张三",
      "leaveType": "年假",
      "duration": 5,
      "reason": "...",
      "status": "pending",
      "approvalLevel": 2,
      "approvalHistory": []
    }
  ]
}
```

### POST `/api/approval/{requestId}/approve`

批准申请。

**请求体：**
```json
{
  "reason": "同意请假"
}
```

### POST `/api/approval/{requestId}/reject`

驳回申请。

**请求体：**
```json
{
  "reason": "工作繁忙，暂不同意"
}
```

---

## 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

| code | 说明 |
|------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未登录/登录过期 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |
