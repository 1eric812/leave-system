<template>
  <!-- 登录页面 -->
  <div v-if="!isLoggedIn" class="login-page">
    <!-- 装饰浮动圆 -->
    <div class="deco-circle"></div>
    <div class="deco-circle"></div>
    <div class="deco-circle"></div>

    <div class="login-wrapper">
      <el-card class="login-card" shadow="never">
        <div class="login-header">
          <div class="login-logo-icon">
            <el-icon><Calendar /></el-icon>
          </div>
          <h2>请假管理系统</h2>
          <p class="login-subtitle">企业级请假审批管理平台</p>
        </div>
        <el-form :model="loginForm" @keyup.enter="handleLogin">
          <el-form-item label="员工账号">
            <el-input
              v-model="loginForm.userId"
              placeholder="请输入员工ID"
              clearable
            >
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="密码">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              show-password
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              @click="handleLogin"
              :loading="loggingIn"
              style="width: 100%"
            >
              登 录
            </el-button>
          </el-form-item>
        </el-form>
        <div class="login-tips">
          <p class="tips-label">— 演示账号 —</p>
          <p>员工：emp001（张三） / emp002（王五）</p>
          <p>部门经理：mgr001（李四） / 总经理：mgr002（赵六）</p>
          <p style="margin-top: 6px; color: rgba(255,255,255,0.25)">默认密码：123456</p>
        </div>
      </el-card>
    </div>
  </div>

  <!-- 主应用 -->
  <el-container v-else class="app-container">
    <!-- 侧边栏 -->
    <el-aside width="200px" class="sidebar">
      <div class="logo">
        <h1>请假管理系统</h1>
      </div>

      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical-demo"
        @select="handleMenuSelect"
      >
        <el-menu-item index="home" @click="goHome">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="apply" @click="goApply">
          <el-icon><EditPen /></el-icon>
          <span>提交申请</span>
        </el-menu-item>
        <el-menu-item v-if="currentUser.role !== 'employee'" index="approval">
          <el-icon><Select /></el-icon>
          <span>审批流程</span>
          <el-badge
            :value="pendingCount"
            :max="99"
            @click="goApproval"
          ></el-badge>
        </el-menu-item>
        <el-menu-item index="records" @click="goRecords">
          <el-icon><Document /></el-icon>
          <span>我的请假</span>
        </el-menu-item>
        <el-menu-item v-if="currentUser.role !== 'employee'" index="employees" @click="openEmployeeMgmt">
          <el-icon><UserFilled /></el-icon>
          <span>员工管理</span>
        </el-menu-item>
      </el-menu>

      <div class="sidebar-footer">
        <p>LeaveFlow v1.0 · 请假审批系统</p>
      </div>
    </el-aside>

    <!-- 主内容区 -->
    <el-container class="main-container">
      <!-- 顶部栏 -->
      <el-header class="app-header">
        <div class="header-left">
          <span>{{ pageTitle }}</span>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <span class="user-avatar">{{ currentUser.name.charAt(0) }}</span>
              {{ currentUser.name }}
              <span class="user-role">({{ roleLabel }})</span>
              <el-icon class="el-icon--right">
                <arrow-down />
              </el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">
                  <el-icon style="margin-right: 6px"><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 页面内容 -->
      <el-main class="app-main">
        <!-- 首页 -->
        <div v-if="currentPage === 'home'" class="home-page">
          <!-- Hero / Dashboard banner -->
          <section class="hero-card">
            <div class="hero-content">
              <h1>欢迎，{{ currentUser.name }}，可视化请假管理</h1>
              <p class="hero-sub">智能审批、流程追踪与统计一目了然，提升团队协作效率。</p>
              <div class="hero-actions">
                <el-button type="primary" @click="goApply">提交请假</el-button>
              <el-button plain @click="goRecords">查看记录</el-button>
              <el-button v-if="currentUser.role !== 'employee'" type="warning" @click="goApproval">去审批 ({{ pendingCount }})</el-button>
                <el-button plain @click="goRecords">查看记录</el-button>
                <el-button v-if="currentUser.role !== 'employee'" type="warning" @click="goApproval">去审批 ({{ pendingCount }})</el-button>
              </div>
            </div>
            <div class="hero-visual" aria-hidden>
              <!-- simple decorative shapes -->
              <svg width="380" height="160" viewBox="0 0 380 160" fill="none" xmlns="http://www.w3.org/2000/svg">
                <defs>
                  <linearGradient id="g1" x1="0" x2="1">
                    <stop offset="0" stop-color="#7CBBFF" stop-opacity="0.9"/>
                    <stop offset="1" stop-color="#0B5FFF" stop-opacity="0.8"/>
                  </linearGradient>
                </defs>
                <rect x="0" y="0" width="380" height="160" rx="16" fill="url(#g1)" opacity="0.15"/>
              </svg>
            </div>
          </section>

          <el-row :gutter="20" style="margin-top: 18px">
            <el-col :span="8">
              <el-card class="box-card stat-card" @click="goRecords" style="cursor: pointer">
                <div class="stat-accent blue"></div>
                <div class="stat-item">
                  <div class="stat-number">{{ totalRequests }}</div>
                  <div class="stat-label">总请假次数</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card class="box-card stat-card" @click="goRecords" style="cursor: pointer">
                <div class="stat-accent orange"></div>
                <div class="stat-item">
                  <div class="stat-number" style="color:#f97316">{{ pendingCount }}</div>
                  <div class="stat-label">待审批</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card class="box-card stat-card" @click="goRecords" style="cursor: pointer">
                <div class="stat-accent purple"></div>
                <div class="stat-item">
                  <div class="stat-number">{{ leaveStore.leaveRequests.length }}</div>
                  <div class="stat-label">历史请假单</div>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>

        <!-- 请假申请页 -->
        <LeaveApplication
          v-if="currentPage === 'apply'"
          :store="leaveStore"
          :request="editingRequest"
          @submit="submitApplication"
          @back="goHome"
        />

        <!-- 审批流页 -->
        <ApprovalFlow
          v-if="currentPage === 'approval'"
          :store="leaveStore"
          @back="goHome"
        />

        <!-- 请假记录页 -->
        <LeaveRecords
          v-if="currentPage === 'records'"
          :store="leaveStore"
          @back="goHome"
          @edit="editRequest"
        />
      </el-main>
    </el-container>

    <!-- 员工管理对话框 -->
    <el-dialog v-model="employeeMgmtDialogVisible" title="员工管理" width="750px">
      <!-- 添加新员工 -->
      <div class="mgmt-section">
        <h4 class="section-title">添加新员工</h4>
        <el-form :model="newEmployeeForm" inline>
          <el-form-item label="姓名">
            <el-input
              v-model="newEmployeeForm.name"
              placeholder="请输入员工姓名"
              clearable
            ></el-input>
          </el-form-item>
          <el-form-item label="部门">
            <el-input
              v-model="newEmployeeForm.dept"
              placeholder="请输入所属部门"
              clearable
            ></el-input>
          </el-form-item>
          <el-form-item label="角色">
            <el-select v-model="newEmployeeForm.role">
              <el-option label="员工" value="employee"></el-option>
              <el-option v-if="currentUser.level > 2" label="部门经理" value="manager1"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="addNewEmployee">添加</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 员工列表 -->
      <div class="mgmt-section">
        <h4 class="section-title">员工列表（{{ visibleUsers.length }}人）</h4>
        <el-table :data="visibleUsers" stripe size="small">
          <el-table-column prop="name" label="姓名" width="120"></el-table-column>
          <el-table-column prop="dept" label="部门" width="150"></el-table-column>
          <el-table-column label="角色" width="120">
            <template #default="{ row }">
              <el-tag :type="row.role === 'employee' ? 'info' : row.role === 'manager1' ? 'warning' : 'danger'">
                {{ getRoleLabel(row.role) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="登录账号" width="160">
            <template #default="{ row }">
              <code>{{ row.id }}</code>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button
                link
                type="danger"
                :disabled="row.id === leaveStore.currentUser.id"
                @click="deleteEmployee(row.id)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>

    <!-- 页脚 -->
    <el-footer class="app-footer">
      <div class="footer-inner">
        <div class="footer-left">© 2026 请假管理系统 · 高端大气版</div>
        <div class="footer-right">设计灵感来自现代企业级后台 · v1.0</div>
      </div>
    </el-footer>
  </el-container>
</template>

<script setup>
import { ref, computed, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import LeaveApplication from './components/LeaveApplication.vue'
import ApprovalFlow from './components/ApprovalFlow.vue'
import LeaveRecords from './components/LeaveRecords.vue'
import { ArrowDown, User, Lock, Calendar, SwitchButton, HomeFilled, EditPen, Select, Document, UserFilled } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { loginApi, getUserListApi, getVisibleUsersApi, addUserApi, deleteUserApi } from './api/userApi'
import { getLeaveListApi, getMyLeaveListApi, getPendingLeaveListApi, createLeaveApi, updateLeaveApi, deleteLeaveApi } from './api/leaveApi'
import { approveLeaveApi, rejectLeaveApi } from './api/approvalApi'

// ==================== Mock 回退数据 ====================
const MOCK_USERS = [
  { id: 'emp001', name: '张三', dept: '技术部', role: 'employee', level: 1, password: '123456' },
  { id: 'emp002', name: '王五', dept: '销售部', role: 'employee', level: 1, password: '123456' },
  { id: 'mgr001', name: '李四', dept: '技术部', role: 'manager1', level: 2, password: '123456' },
  { id: 'mgr002', name: '赵六', dept: '行政部', role: 'manager2', level: 3, password: '123456' },
]

const MOCK_REQUESTS = [
  {
    id: 'REQ001', userId: 'emp001', userName: '张三', dept: '技术部', leaveType: '年假',
    startDate: new Date('2024-06-10'), endDate: new Date('2024-06-12'), duration: 3,
    reason: '回家处理私人事务', status: 'approved', approvalLevel: 1,
    approvalHistory: [{ id: 'APR001', requestId: 'REQ001', approverId: 'mgr001', approverName: '李四', action: 'approve', reason: '同意', approvalLevel: 1, timestamp: new Date('2024-06-09') }],
    createdAt: new Date('2024-06-08'), updatedAt: new Date('2024-06-09'),
  },
]

// ==================== API 可用性检测 ====================
let apiAvailable = true

async function checkApi() {
  try {
    const res = await getUserListApi()
    return res?.code === 200
  } catch {
    console.warn('[离职管理系统] 后端未启动，使用 Mock 数据')
    return false
  }
}

// ==================== Store ====================
function createLeaveStore() {
  const currentUser = reactive({
    id: 'emp001', name: '张三', dept: '技术部', role: 'employee', level: 1,
  })

  const leaveRequests = reactive([])
  const allUsers = reactive([])

  // ---------- 初始化：尝试加载后端数据，失败用 Mock ----------
  async function init() {
    apiAvailable = await checkApi()

    if (apiAvailable) {
      try {
        const userRes = await getUserListApi()
        if (userRes?.code === 200) allUsers.push(...(userRes.data || []))

        const leaveRes = await getLeaveListApi()
        if (leaveRes?.code === 200) {
          leaveRequests.push(...((leaveRes.data || []).map(normalizeRequest)))
        }
      } catch {
        fallbackToMock()
      }
    } else {
      fallbackToMock()
    }
  }

  function fallbackToMock() {
    apiAvailable = false
    allUsers.length = 0
    allUsers.push(...MOCK_USERS.map(u => ({ ...u })))
    leaveRequests.length = 0
    leaveRequests.push(...MOCK_REQUESTS.map(r => ({ ...r, approvalHistory: r.approvalHistory ? r.approvalHistory.map(a => ({ ...a })) : [] })))
  }

  // 后端 Date 字符串 → Date 对象
  function normalizeRequest(r) {
    return {
      ...r,
      startDate: r.startDate ? new Date(r.startDate) : null,
      endDate: r.endDate ? new Date(r.endDate) : null,
      createdAt: r.createdAt ? new Date(r.createdAt) : null,
      updatedAt: r.updatedAt ? new Date(r.updatedAt) : null,
      approvalHistory: (r.approvalHistory || []).map(a => ({
        ...a,
        timestamp: a.timestamp ? new Date(a.timestamp) : null,
      })),
    }
  }

  // ---------- 登录 ----------
  async function authenticate(userId, password) {
    if (apiAvailable) {
      try {
        const res = await loginApi(userId, password)
        if (res?.code === 200 && res.data) {
          Object.assign(currentUser, res.data)
          return true
        }
      } catch {}
    }
    // Mock 回退
    const user = allUsers.find(u => u.id === userId)
    if (!user || user.password !== password) return false
    Object.assign(currentUser, user)
    return true
  }

  // ---------- 请假 CRUD ----------
  async function createLeaveRequest(data) {
    const duration = calculateDays(data.startDate, data.endDate)

    if (apiAvailable) {
      try {
        const res = await createLeaveApi({
          userId: currentUser.id,
          leaveType: data.leaveType,
          startDate: formatDate(data.startDate),
          endDate: formatDate(data.endDate),
          duration,
          reason: data.reason,
        })
        if (res?.code === 200 && res.data) {
          const request = normalizeRequest(res.data)
          leaveRequests.push(request)
          return request
        }
      } catch (e) { ElMessage.warning('后端暂不可用，数据仅保存在本地: ' + e.message) }
    }

    // Mock 回退
    // GM 自己提交 → 自动批准
    let status = 'pending'
    if (currentUser.level === 3) {
      status = 'approved'
    }
    const approvalLevel = duration > 3 ? 2 : 1

    const request = {
      id: 'REQ' + Date.now(), userId: currentUser.id, userName: currentUser.name,
      dept: currentUser.dept, leaveType: data.leaveType,
      startDate: data.startDate, endDate: data.endDate, duration,
      reason: data.reason, status,
      approvalLevel, approvalHistory: [],
      createdAt: new Date(), updatedAt: new Date(),
    }

    if (currentUser.level === 3) {
      request.approvalHistory.push({
        id: 'APR' + Date.now(), requestId: request.id,
        approverId: currentUser.id, approverName: currentUser.name,
        action: 'approve', reason: '总经理自动审批通过', approvalLevel: 1,
        timestamp: new Date(),
      })
    }

    leaveRequests.push(request)
    return request
  }

  async function updateLeaveRequest(id, data) {
    const duration = calculateDays(data.startDate, data.endDate)

    if (apiAvailable) {
      try {
        const res = await updateLeaveApi(id, {
          leaveType: data.leaveType,
          startDate: formatDate(data.startDate),
          endDate: formatDate(data.endDate),
          duration,
          reason: data.reason,
        })
        if (res?.code === 200 && res.data) {
          const idx = leaveRequests.findIndex(r => r.id === id)
          if (idx >= 0) leaveRequests[idx] = normalizeRequest(res.data)
          return res.data
        }
      } catch (e) {
        if (e.message.includes('只有待审批') || e.message.includes('只能修改自己')) {
          throw e
        }
        ElMessage.warning('后端暂不可用，数据仅保存在本地')
      }
    }

    // Mock 回退
    const request = leaveRequests.find(r => r.id === id)
    if (!request) return null
    if (request.status !== 'pending') throw new Error('只有待审批的请假单才能修改')
    if (request.userId !== currentUser.id) throw new Error('只能修改自己的请假单')
    Object.assign(request, {
      leaveType: data.leaveType, startDate: data.startDate, endDate: data.endDate,
      duration, reason: data.reason,
      approvalLevel: (currentUser.level === 2) ? 1 : (duration > 3 ? 2 : 1),
      updatedAt: new Date(),
    })
    return request
  }

  async function deleteLeaveRequest(id) {
    if (apiAvailable) {
      try {
        const res = await deleteLeaveApi(id)
        if (res?.code === 200) {
          const idx = leaveRequests.findIndex(r => r.id === id)
          if (idx >= 0) leaveRequests.splice(idx, 1)
          return true
        }
      } catch (e) {
        if (e.message.includes('只能删除')) throw e
        ElMessage.warning('后端暂不可用')
      }
    }
    // Mock 回退
    const idx = leaveRequests.findIndex(r => r.id === id)
    if (idx === -1) return false
    const request = leaveRequests[idx]
    if (request.status !== 'pending' || request.userId !== currentUser.id) {
      throw new Error('只能删除自己的待审请假单')
    }
    leaveRequests.splice(idx, 1)
    return true
  }

  // ---------- 审批 ----------
  async function approveLeaveRequest(requestId, reason = '同意') {
    if (currentUser.role === 'employee') throw new Error('员工无权批准请假申请')

    if (apiAvailable) {
      try {
        const res = await approveLeaveApi(requestId, reason, currentUser.id, currentUser.level - 1)
        if (res?.code === 200) {
          // API 审批成功后本地状态同步更新（全量刷新会丢失审批历史）
          const request = leaveRequests.find(r => r.id === requestId)
          if (request) {
            const approverLevel = currentUser.level - 1
            const requester = allUsers.find(u => u.id === request.userId)
            request.approvalHistory.push({
              id: 'APR' + Date.now(), requestId,
              approverId: currentUser.id, approverName: currentUser.name,
              action: 'approve', reason, approvalLevel: approverLevel,
              timestamp: new Date(),
            })
            if (requester && requester.level === 2) {
              request.status = 'approved'
            } else if (request.approvalLevel === 2 && approverLevel === 1) {
              request.status = 'pending'
            } else {
              request.status = 'approved'
            }
            request.updatedAt = new Date()
          }
          return true
        } else {
          throw new Error(res?.message || '审批失败')
        }
      } catch (e) {
        throw e
      }
    }

    // Mock 回退
    return mockApprove(requestId, reason)
  }

  async function rejectLeaveRequest(requestId, reason) {
    if (currentUser.role === 'employee') throw new Error('员工无权驳回请假申请')

    if (apiAvailable) {
      try {
        const res = await rejectLeaveApi(requestId, reason, currentUser.id, currentUser.level - 1)
        if (res?.code === 200) {
          // API 驳回成功后本地状态同步更新
          const request = leaveRequests.find(r => r.id === requestId)
          if (request) {
            request.approvalHistory.push({
              id: 'APR' + Date.now(), requestId,
              approverId: currentUser.id, approverName: currentUser.name,
              action: 'reject', reason, approvalLevel: currentUser.level - 1,
              timestamp: new Date(),
            })
            request.status = 'rejected'
            request.updatedAt = new Date()
          }
          return true
        } else {
          throw new Error(res?.message || '驳回失败')
        }
      } catch (e) {
        throw e
      }
    }

    // Mock 回退
    return mockReject(requestId, reason)
  }

  // Mock 审批（与后端逻辑一致）
  function mockApprove(requestId, reason) {
    const request = leaveRequests.find(r => r.id === requestId)
    if (!request || request.status !== 'pending') throw new Error('该请假单已处理')
    if (request.approvalHistory.some(a => a.approverId === currentUser.id && a.action === 'approve')) {
      throw new Error('您已审批过该申请')
    }
    const approverLevel = currentUser.level - 1 // 审批级别
    const requester = allUsers.find(u => u.id === request.userId)

    // 权限检查
    if (currentUser.level === 2 && requester && requester.level !== 1) {
      throw new Error('部门经理只能审批普通员工的请假申请')
    }

    const approval = {
      id: 'APR' + Date.now(), requestId,
      approverId: currentUser.id, approverName: currentUser.name,
      action: 'approve', reason, approvalLevel: approverLevel,
      timestamp: new Date(),
    }
    request.approvalHistory.push(approval)

    if (requester && requester.level === 2) {
      // 部门经理的申请 → 总经理批准后直接通过
      request.status = 'approved'
    } else if (request.approvalLevel === 2 && approverLevel === 1) {
      // 员工二级审批：一级通过，等待二级
      request.status = 'pending'
    } else {
      request.status = 'approved'
    }
    request.updatedAt = new Date()
    return request
  }

  function mockReject(requestId, reason) {
    const request = leaveRequests.find(r => r.id === requestId)
    if (!request || request.status !== 'pending') throw new Error('该请假单已处理')
    const approval = {
      id: 'APR' + Date.now(), requestId,
      approverId: currentUser.id, approverName: currentUser.name,
      action: 'reject', reason, approvalLevel: currentUser.level - 1,
      timestamp: new Date(),
    }
    request.approvalHistory.push(approval)
    request.status = 'rejected'
    request.updatedAt = new Date()
    return request
  }

  // ---------- 用户管理 ----------
  async function addUser(userData) {
    if (apiAvailable) {
      try {
        const res = await addUserApi({
          name: userData.name, dept: userData.dept, role: userData.role || 'employee',
        })
        if (res?.code === 200 && res.data) {
          allUsers.push(res.data)
          return res.data
        }
      } catch { ElMessage.warning('后端暂不可用，数据仅保存在本地') }
    }
    const newLevel = userData.role === 'manager1' ? 2 : userData.role === 'manager2' ? 3 : 1
    if (newLevel >= currentUser.level) throw new Error('无权添加级别不低于自己的员工')
    const newUser = { id: 'emp' + Date.now(), name: userData.name, dept: userData.dept, role: userData.role || 'employee', level: newLevel, password: '123456' }
    allUsers.push(newUser)
    return newUser
  }

  async function deleteUser(userId) {
    if (apiAvailable) {
      try {
        const res = await deleteUserApi(userId)
        if (res?.code === 200) {
          const idx = allUsers.findIndex(u => u.id === userId)
          if (idx >= 0) allUsers.splice(idx, 1)
          return true
        }
      } catch { ElMessage.warning('后端暂不可用') }
    }
    const idx = allUsers.findIndex(u => u.id === userId)
    if (idx === -1 || currentUser.id === userId) return false
    if (allUsers[idx].level >= currentUser.level) return false
    allUsers.splice(idx, 1)
    return true
  }

  // ---------- 查询（同步，从本地数据读取） ----------
  function getPendingRequests() {
    if (currentUser.role === 'employee') return []
    return leaveRequests.filter(r => {
      if (r.status !== 'pending') return false
      const requester = allUsers.find(u => u.id === r.userId)
      if (!requester) return false
      // 不能审批自己
      if (r.userId === currentUser.id) return false
      // 已经审批过的排除
      if (r.approvalHistory.some(a => a.approverId === currentUser.id && a.action === 'approve')) return false

      if (currentUser.level === 2) {
        // 部门经理：只能审批普通员工的申请
        return requester.level === 1
      }
      if (currentUser.level === 3) {
        // 总经理：审批部门经理的申请 + 需要二级审批且一级已过的员工申请
        if (requester.level === 2) return true
        if (r.approvalLevel === 2) {
          return r.approvalHistory.some(a => a.action === 'approve' && a.approvalLevel === 1)
        }
        return false
      }
      return false
    })
  }

  function getMyLeaveRequests() {
    return leaveRequests.filter(r => r.userId === currentUser.id)
  }

  function getVisibleUsers() {
    return allUsers.filter(u => u.level < currentUser.level)
  }

  function calculateDays(startDate, endDate) {
    return dayjs(endDate).diff(dayjs(startDate), 'day') + 1
  }

  return {
    currentUser, leaveRequests, allUsers,
    init, authenticate,
    createLeaveRequest, updateLeaveRequest, deleteLeaveRequest,
    approveLeaveRequest, rejectLeaveRequest,
    getPendingRequests, getMyLeaveRequests, getVisibleUsers, calculateDays,
    addUser, deleteUser,
  }
}

function formatDate(d) {
  return dayjs(d).format('YYYY-MM-DD')
}

const leaveStore = createLeaveStore()
const currentPage = ref('home')
const activeMenu = ref('home')
const editingRequest = ref(null)
const isLoggedIn = ref(false)
const loggingIn = ref(false)
const loginForm = reactive({
  userId: '',
  password: '',
})
const employeeMgmtDialogVisible = ref(false)
const newEmployeeForm = reactive({
  name: '',
  dept: '',
  role: 'employee',
})

const currentUser = computed(() => leaveStore.currentUser)
const myRequests = computed(() => leaveStore.getMyLeaveRequests())
const totalRequests = computed(() => myRequests.value.length)
const pendingCount = computed(() => leaveStore.getPendingRequests().length)
const visibleUsers = computed(() => leaveStore.getVisibleUsers())

const roleLabel = computed(() => {
  return getRoleLabel(currentUser.value.role)
})

const pageTitle = computed(() => {
  const titles = {
    home: '首页',
    apply: editingRequest.value ? '编辑请假申请' : '提交请假申请',
    approval: '审批流程',
    records: '我的请假记录',
  }
  return titles[currentPage.value] || '请假管理系统'
})

function getRoleLabel(role) {
  const labels = { employee: '员工', manager1: '部门经理', manager2: '总经理' }
  return labels[role] || '未知'
}

function handleMenuSelect() {}
function goHome() {
  currentPage.value = 'home'
  activeMenu.value = 'home'
  editingRequest.value = null
}
function goApply() {
  currentPage.value = 'apply'
  activeMenu.value = 'apply'
  editingRequest.value = null
}
function goApproval() {
  currentPage.value = 'approval'
  activeMenu.value = 'approval'
}
function goRecords() {
  currentPage.value = 'records'
  activeMenu.value = 'records'
}
function submitApplication(data) {
  if (editingRequest.value) {
    leaveStore.updateLeaveRequest(editingRequest.value.id, data).then(() => {
      ElMessage.success('请假申请已更新')
      editingRequest.value = null
      goHome()
    }).catch((error) => {
      ElMessage.error(error.message || '更新失败')
    })
  } else {
    leaveStore.createLeaveRequest(data).then(() => {
      ElMessage.success('请假申请已提交，请等待审批')
      editingRequest.value = null
      goHome()
    }).catch((error) => {
      ElMessage.error(error.message || '提交失败')
    })
  }
}
function editRequest(request) {
  editingRequest.value = request
  currentPage.value = 'apply'
  activeMenu.value = 'apply'
}
function handleCommand(command) {
  if (command === 'logout') {
    isLoggedIn.value = false
    loginForm.userId = ''
    loginForm.password = ''
    goHome()
    ElMessage.success('已登出')
  }
}
async function handleLogin() {
  if (!loginForm.userId.trim()) {
    ElMessage.error('请输入员工账号')
    return
  }
  if (!loginForm.password.trim()) {
    ElMessage.error('请输入密码')
    return
  }
  loggingIn.value = true
  try {
    // 初始化：尝试连接后端，失败则用 Mock
    await leaveStore.init()
    const success = await leaveStore.authenticate(loginForm.userId.trim(), loginForm.password)
    if (success) {
      isLoggedIn.value = true
      goHome()
      ElMessage.success(`欢迎回来，${leaveStore.currentUser.name}！`)
    } else {
      ElMessage.error('账号或密码错误，请重试')
    }
  } catch {
    ElMessage.error('登录失败，请检查后端是否启动')
  } finally {
    loggingIn.value = false
  }
}
function openEmployeeMgmt() {
  employeeMgmtDialogVisible.value = true
  resetNewEmployeeForm()
}
function resetNewEmployeeForm() {
  newEmployeeForm.name = ''
  newEmployeeForm.dept = ''
  newEmployeeForm.role = 'employee'
}
async function addNewEmployee() {
  if (!newEmployeeForm.name.trim()) {
    ElMessage.error('请输入员工姓名')
    return
  }
  if (!newEmployeeForm.dept.trim()) {
    ElMessage.error('请输入所属部门')
    return
  }
  try {
    await leaveStore.addUser({
      name: newEmployeeForm.name.trim(),
      dept: newEmployeeForm.dept.trim(),
      role: newEmployeeForm.role,
    })
    ElMessage.success(`已添加员工：${newEmployeeForm.name}`)
    resetNewEmployeeForm()
  } catch (error) {
    ElMessage.error(error.message || '添加失败')
  }
}
async function deleteEmployee(userId) {
  const user = leaveStore.allUsers.find((u) => u.id === userId)
  if (!user) return
  try {
    await ElMessageBox.confirm(
      `确定要删除员工「${user.name}」吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    const result = await leaveStore.deleteUser(userId)
    if (result) {
      ElMessage.success('员工已删除')
    } else {
      ElMessage.error('无法删除当前登录用户')
    }
  } catch { /* 取消确认不做任何事 */ }
}
</script>

<style scoped>
.app-container {
  height: 100vh;
  display: flex;
}
.sidebar {
  background-color: #545c64;
  border-right: 1px solid #ebeef5;
}
.logo {
  padding: 20px;
  text-align: center;
  color: white;
  border-bottom: 1px solid #ebeef5;
}
.logo h1 {
  margin: 0;
  font-size: 18px;
  font-weight: bold;
}
:deep(.el-menu-vertical-demo) {
  background-color: #545c64;
  border: none;
}
:deep(.el-menu-vertical-demo .el-menu-item) {
  color: #ccc;
}
:deep(.el-menu-vertical-demo .el-menu-item:hover) {
  color: white;
  background-color: #626670 !important;
}
:deep(.el-menu-vertical-demo .el-menu-item.is-active) {
  color: white;
  background-color: #409eff !important;
}
.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.app-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  border-bottom: 1px solid #ebeef5;
  padding: 0 20px;
  font-size: 16px;
  font-weight: bold;
}
.header-left {
  flex: 1;
}
.header-right {
  display: flex;
  align-items: center;
}
.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}
.user-role {
  margin: 0 8px;
  color: #909399;
  font-size: 14px;
}
.app-main {
  flex: 1;
  background-color: #f5f7fa;
  overflow-y: auto;
  padding: 20px;
}
.home-page .welcome-content {
  font-size: 14px;
  line-height: 1.8;
}
.quick-link {
  text-align: center;
  padding: 20px;
}
.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}
.stat-label {
  font-size: 14px;
  color: #606266;
}
.box-card {
  margin-bottom: 20px;
}
.card-header {
  font-size: 16px;
  font-weight: bold;
}
.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
.mgmt-section {
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}
.mgmt-section:last-child {
  border-bottom: none;
}
.section-title {
  margin: 0 0 12px 0;
  font-size: 15px;
  font-weight: bold;
  color: #303133;
}

/* 登录页面样式 */
.login-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-wrapper {
  width: 400px;
}
.login-card {
  border-radius: 8px;
}
.login-header {
  text-align: center;
}
.login-header h2 {
  margin: 0 0 8px 0;
  font-size: 22px;
  color: #303133;
}
.login-subtitle {
  margin: 0;
  font-size: 14px;
  color: #909399;
}
.login-tips {
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
  text-align: center;
}
.login-tips p {
  margin: 4px 0;
  font-size: 12px;
  color: #909399;
}
</style>
