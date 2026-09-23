<!-- 审批流管理页面 -->
<template>
  <div class="approval-flow">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span class="title">审批流管理</span>
          <el-tag v-if="currentUser.role !== 'employee'" type="success">
            {{ roleLabel }}
          </el-tag>
        </div>
      </template>

      <!-- 标签页：待审批 / 已审批 -->
      <el-tabs v-model="activeTab">
        <!-- 待审批列表 -->
        <el-tab-pane label="待审批" name="pending">
          <div v-if="pendingRequests.length === 0" class="empty-state">
            <el-empty description="暂无待审批的请假申请"></el-empty>
          </div>
          <div v-else>
            <el-space fill wrap>
              <div
                v-for="request in pendingRequests"
                :key="request.id"
                class="request-card"
              >
                <el-card shadow="hover" @click="selectRequest(request)">
                  <template #header>
                    <div class="card-header-small">
                      <span class="applicant">{{ request.userName }}</span>
                      <el-tag type="info">{{ request.leaveType }}</el-tag>
                    </div>
                  </template>
                  <div class="card-content">
                    <p>
                      <strong>部门：</strong> {{ request.dept }}
                    </p>
                    <p>
                      <strong>时长：</strong>
                      <el-tag :type="request.duration > 3 ? 'warning' : 'success'">
                        {{ request.duration }} 天
                      </el-tag>
                    </p>
                    <p>
                      <strong>原因：</strong> {{ request.reason }}
                    </p>
                    <p>
                      <strong>申请时间：</strong>
                      {{ formatDate(request.createdAt) }}
                    </p>
                  </div>
                </el-card>
              </div>
            </el-space>
          </div>
        </el-tab-pane>

        <!-- 已审批列表 -->
        <el-tab-pane label="已审批" name="processed">
          <div v-if="processedRequests.length === 0" class="empty-state">
            <el-empty description="暂无已审批的请假申请"></el-empty>
          </div>
          <el-table v-else :data="processedRequests" stripe>
            <el-table-column prop="userName" label="申请人" width="120"></el-table-column>
            <el-table-column prop="dept" label="部门" width="100"></el-table-column>
            <el-table-column prop="leaveType" label="假期类型" width="80"></el-table-column>
            <el-table-column prop="duration" label="天数" width="60"></el-table-column>
            <el-table-column label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 'approved' ? 'success' : 'danger'">
                  {{ row.status === 'approved' ? '已批准' : '已驳回' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作">
              <template #default="{ row }">
                <el-button link type="primary" @click="selectRequest(row)">
                  查看详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 审批详情对话框 -->
    <el-dialog v-model="dialogVisible" :title="`审批详情 - ${selectedRequest?.userName}`" width="600px">
      <div v-if="selectedRequest" class="request-details">
        <!-- 基础信息 -->
        <div class="detail-section">
          <h4>基础信息</h4>
          <el-row :gutter="20">
            <el-col :span="12">
              <p><strong>申请人：</strong> {{ selectedRequest.userName }}</p>
            </el-col>
            <el-col :span="12">
              <p><strong>部门：</strong> {{ selectedRequest.dept }}</p>
            </el-col>
          </el-row>
        </div>

        <!-- 请假详情 -->
        <div class="detail-section">
          <h4>请假详情</h4>
          <el-row :gutter="20">
            <el-col :span="12">
              <p><strong>假期类型：</strong> {{ selectedRequest.leaveType }}</p>
            </el-col>
            <el-col :span="12">
              <p>
                <strong>时长：</strong>
                <el-tag :type="selectedRequest.duration > 3 ? 'warning' : 'success'">
                  {{ selectedRequest.duration }} 天
                </el-tag>
              </p>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <p>
                <strong>开始日期：</strong>
                {{ formatDate(selectedRequest.startDate) }}
              </p>
            </el-col>
            <el-col :span="12">
              <p>
                <strong>结束日期：</strong>
                {{ formatDate(selectedRequest.endDate) }}
              </p>
            </el-col>
          </el-row>
          <p>
            <strong>请假原因：</strong>
          </p>
          <el-alert :description="selectedRequest.reason" :closable="false"></el-alert>
        </div>

        <!-- 流程进度 -->
        <div class="detail-section">
          <h4>审批流程</h4>
          <el-steps :active="activeStep" finish-status="success" align-center>
            <el-step title="一级审批" description="部门经理"></el-step>
            <el-step
              v-if="selectedRequest.approvalLevel === 2"
              title="二级审批"
              description="总经理"
            ></el-step>
            <el-step
              :title="selectedRequest.status === 'approved' ? '已批准' : '已驳回'"
              :status="selectedRequest.status === 'approved' ? 'finish' : 'error'"
            ></el-step>
          </el-steps>
        </div>

        <!-- 审批历史 -->
        <div class="detail-section">
          <h4>审批历史</h4>
          <el-timeline>
            <el-timeline-item
              v-for="(approval, index) in selectedRequest.approvalHistory"
              :key="index"
              :timestamp="formatDateTime(approval.timestamp)"
              placement="top"
            >
              <div class="approval-item">
                <p>
                  <strong>审批者：</strong> {{ approval.approverName }}
                </p>
                <p>
                  <strong>级别：</strong> {{ approval.approvalLevel === 1 ? '一级' : '二级' }}
                </p>
                <p>
                  <strong>操作：</strong>
                  <el-tag :type="approval.action === 'approve' ? 'success' : 'danger'">
                    {{ approval.action === 'approve' ? '批准' : '驳回' }}
                  </el-tag>
                </p>
                <p>
                  <strong>备注：</strong> {{ approval.reason }}
                </p>
              </div>
            </el-timeline-item>
          </el-timeline>
        </div>

        <!-- 审批操作（仅待审时显示） -->
        <div v-if="selectedRequest.status === 'pending' && canApprove" class="detail-section">
          <h4>审批操作</h4>
          <el-form>
            <el-form-item label="审批意见" prop="approvalReason">
              <el-input
                v-model="approvalReason"
                type="textarea"
                :rows="3"
                placeholder="请输入审批意见"
              ></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="success" @click="approve" :loading="approving">
                批准
              </el-button>
              <el-button type="danger" @click="reject" :loading="approving">
                驳回
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </el-dialog>

    <div class="button-group">
      <el-button @click="goBack">返回首页</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import dayjs from 'dayjs'
import { ElMessage, ElMessageBox } from 'element-plus'

const props = defineProps({
  store: Object,
})

const emit = defineEmits(['back'])

const activeTab = ref('pending')
const dialogVisible = ref(false)
const selectedRequest = ref(null)
const approvalReason = ref('')
const approving = ref(false)

const currentUser = computed(() => props.store.currentUser)
const pendingRequests = computed(() => props.store.getPendingRequests())
const processedRequests = computed(() => {
  return props.store.leaveRequests.filter(
    (r) => r.status === 'approved' || r.status === 'rejected'
  )
})

const roleLabel = computed(() => {
  if (currentUser.value.role === 'manager1') return '一级审批者（部门经理）'
  if (currentUser.value.role === 'manager2') return '二级审批者（总经理）'
  return '员工'
})

const canApprove = computed(() => {
  if (!selectedRequest.value || selectedRequest.value.status !== 'pending') return false

  // 员工不能审批
  if (currentUser.value.role === 'employee') return false

  // 不能审批自己
  if (selectedRequest.value.userId === currentUser.value.id) return false

  // 检查是否已经审批过
  const alreadyApproved = selectedRequest.value.approvalHistory.some(
    (a) => a.approverId === currentUser.value.id && a.action === 'approve'
  )
  if (alreadyApproved) return false

  // 获取申请人信息
  const requester = props.store.allUsers.find(u => u.id === selectedRequest.value.userId)

  // 部门经理（manager1）：只能审批普通员工 (level 1)
  if (currentUser.value.role === 'manager1') {
    if (!requester || requester.level !== 1) return false
    return true
  }

  // 总经理（manager2）：审批部门经理的申请 + 需要二级审批且一级已过的员工申请
  if (currentUser.value.role === 'manager2') {
    if (requester && requester.level === 2) return true
    if (selectedRequest.value.approvalLevel === 2) {
      return selectedRequest.value.approvalHistory.some(
        (a) => a.action === 'approve' && a.approvalLevel === 1
      )
    }
    return false
  }

  return false
})

const activeStep = computed(() => {
  if (!selectedRequest.value) return 0
  if (selectedRequest.value.approvalHistory.length === 0) return 0
  if (selectedRequest.value.approvalHistory.length === 1) {
    if (selectedRequest.value.approvalLevel === 1) return 2
    return 1
  }
  return 2
})

function formatDate(date) {
  return dayjs(date).format('YYYY-MM-DD')
}

function formatDateTime(date) {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

function selectRequest(request) {
  selectedRequest.value = request
  approvalReason.value = ''
  dialogVisible.value = true
}

async function approve() {
  if (!approvalReason.value.trim()) {
    ElMessage.error('请输入审批意见')
    return
  }

  approving.value = true
  try {
    await props.store.approveLeaveRequest(selectedRequest.value.id, approvalReason.value)
    ElMessage.success('批准成功')
    dialogVisible.value = false
    activeTab.value = 'processed'
  } catch (error) {
    ElMessage.error(error.message)
  } finally {
    approving.value = false
  }
}

async function reject() {
  if (!approvalReason.value.trim()) {
    ElMessage.error('请输入驳回原因')
    return
  }

  ElMessageBox.confirm('确定要驳回该请假申请吗？驳回后申请人无法再修改。', '确认驳回', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      approving.value = true
      try {
        await props.store.rejectLeaveRequest(selectedRequest.value.id, approvalReason.value)
        ElMessage.success('驳回成功')
        dialogVisible.value = false
        activeTab.value = 'processed'
      } catch (error) {
        ElMessage.error(error.message)
      } finally {
        approving.value = false
      }
    })
    .catch(() => {
      ElMessage.info('已取消')
    })
}

function goBack() {
  emit('back')
}
</script>

<style scoped>
.approval-flow {
  width: 100%;
  animation: fadeIn 0.4s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(12px); }
  to { opacity: 1; transform: translateY(0); }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header-small {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.title {
  font-size: 18px;
  font-weight: 700;
  position: relative;
  padding-left: 14px;
}

.title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 20px;
  background: linear-gradient(180deg, var(--primary), var(--primary-light));
  border-radius: 2px;
}

.applicant {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
}

.request-card {
  width: 300px;
  cursor: pointer;
  transition: all 0.25s ease;
}

.request-card:hover {
  transform: translateY(-4px);
}

.request-card :deep(.el-card__body) {
  padding: 16px 20px !important;
}

.card-content {
  font-size: 14px;
  line-height: 1.8;
}

.card-content p {
  margin: 6px 0;
  color: var(--text-secondary);
}

.card-content p strong {
  color: var(--text-primary);
}

.empty-state {
  padding: 60px 0;
  text-align: center;
}

.detail-section {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(15, 23, 42, 0.06);
}

.detail-section:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.detail-section h4 {
  margin: 0 0 12px 0;
  font-size: 14px;
  font-weight: 700;
  color: var(--text-primary);
  position: relative;
  padding-left: 12px;
}

.detail-section h4::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 14px;
  background: var(--primary);
  border-radius: 2px;
}

.approval-item {
  font-size: 14px;
  line-height: 1.8;
}

.approval-item p {
  margin: 4px 0;
}

.button-group {
  margin-top: 20px;
  text-align: center;
}

:deep(.el-tabs__item) {
  font-weight: 600 !important;
  font-size: 14px !important;
}

:deep(.el-tabs__item.is-active) {
  color: var(--primary) !important;
}

:deep(.el-tabs__active-bar) {
  background: var(--primary) !important;
}
</style>
