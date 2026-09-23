# 请假审批流系统（Leave Approval System）

> 大学课程设计：一个基于 **Vue 3 + Spring Boot + MySQL** 的前后端分离企业级请假审批管理系统。

## ✨ 项目简介

本项目实现了一套完整的请假审批流程：员工提交请假申请，系统根据请假时长**自动判定审批级别**（≤3 天一级审批、>3 天二级审批），部门经理初审、总经理终审，全程留痕。覆盖请假申请、两级审批、审批记录、角色权限、数据可视化等完整业务闭环。

## 🛠 技术栈

| 端 | 技术 | 版本 | 用途 |
|----|------|------|------|
| 前端 | Vue | 3.5.34 | 前端框架 |
| 前端 | Element Plus | 2.4.4 | UI 组件库 |
| 前端 | Vite | 8.0.12 | 构建工具 |
| 前端 | Pinia | 2.1.6 | 状态管理 |
| 后端 | Spring Boot | 3.4.4 | 后端框架 |
| 后端 | MyBatis-Plus | 3.5.9 | ORM / 数据访问 |
| 数据库 | MySQL | 8.0 | 数据存储 |

## 📁 项目结构

```
请假程序设计/
├── src/                       # 前端源码（Vue3）
│   ├── components/
│   │   ├── LeaveApplication.vue   # 请假申请表单
│   │   ├── ApprovalFlow.vue       # 审批流程页面
│   │   └── LeaveRecords.vue       # 请假记录页面
│   ├── api/                   # 前端接口封装
│   ├── stores/                # Pinia 状态管理
│   ├── App.vue                # 主应用
│   └── main.js                # 前端入口
├── backend/                   # 后端源码（Spring Boot）
│   ├── src/main/java/com/example/leave/
│   │   ├── controller/        # 控制器层
│   │   ├── service/           # 业务层
│   │   ├── mapper/            # 数据访问层
│   │   ├── entity/            # 实体类
│   │   └── common/            # 通用封装
│   └── src/main/resources/
│       ├── application.yml    # 后端配置
│       ├── schema.sql         # 建表 DDL（启动自动执行）
│       ├── init_data.sql      # 初始化 + 模拟数据
│       └── mapper/            # MyBatis XML
├── .env.example               # 环境变量模板
├── .gitignore                 # git 忽略文件
└── README.md                  # 项目说明
```

## 🚀 快速启动

### 1. 环境依赖

- Node.js ≥ 18
- JDK 17
- Maven ≥ 3.6
- MySQL 8.0（本地需建库，启动会自动建表）

### 2. 配置数据库

复制 `.env.example` 为 `.env`，填入你的 MySQL 账号密码（或直接修改 `backend/src/main/resources/application.yml`）：

```bash
# 在项目根目录
cp .env.example .env
```

> 未设置环境变量时，后端默认使用本地默认值 `root/root` 连接 `localhost:3306/leave_system`。

### 3. 启动后端

```bash
cd backend
mvn spring-boot:run
```

启动后自动执行 `schema.sql` 建表、`init_data.sql` 导入模拟数据，接口默认运行在 `http://localhost:8080`。

### 4. 启动前端

```bash
# 项目根目录
npm install
npm run dev
```

浏览器访问 `http://localhost:5173`。前端已配置代理，`/api` 请求会转发到 `http://localhost:8080`。

## 👥 预置测试账号

系统内置 4 个测试账号（密码均为 `123456`），详见 [账号密码说明.md](./账号密码说明.md)：

| 角色 | 账号 | 权限 |
|------|------|------|
| 员工 | emp001 / emp002 | 提交申请、查看记录 |
| 部门经理 | mgr001 | 一级审批 |
| 总经理 | mgr002 | 二级审批 |

## 📊 业务规则

### 两级审批机制

```
请假天数 ≤ 3 天  →  部门经理批准 → 直接通过
请假天数  > 3 天  →  部门经理批准 → 总经理复审 → 通过
```

### 编辑规则
- ✅ 提交前 / 待审批：可修改
- ❌ 已批准 / 已驳回：不可修改，需重新申请

## 🎬 演示场景

- **一级审批**：员工提交 3 天年假 → 经理批准 → 自动"已批准"
- **二级审批**：员工提交 5 天病假 → 经理初审 → 总经理终审 → 完成
- **驳回处理**：经理填写驳回原因 → 状态变"已驳回" → 员工重新申请

## 📚 更多文档

- **[QUICKSTART.md](./QUICKSTART.md)** - 快速上手
- **[SYSTEM_GUIDE.md](./SYSTEM_GUIDE.md)** - 完整系统使用指南
- **[ASSIGNMENT_CHECKLIST.md](./ASSIGNMENT_CHECKLIST.md)** - 课程需求实现对照
- **[backend/API设计说明.md](./backend/API设计说明.md)** - 后端接口文档
- **[backend/数据库设计.md](./backend/数据库设计.md)** - 数据库设计说明

## ⚠️ 注意事项

- 本项目为课程学习项目，**仅供学习参考**，请勿用于生产环境。
- 模拟数据中的姓名、请假原因均为虚构，用于演示审批流程。
- 数据库账号密码、环境变量等敏感信息**请自行配置，不要提交到代码仓库**（`.env` 已被 gitignore 忽略）。

## 📄 License

本项目为大学课程设计作品，仅用于学习交流。
