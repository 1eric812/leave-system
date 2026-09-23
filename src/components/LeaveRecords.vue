<!-- 请假记录页面 -->
<template>
  <div class="leave-records">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span class="title">请假记录</span>
        </div>
      </template>

      <!-- 筛选面板 -->
      <div class="filter-panel">
        <el-form :model="filterData" inline>
          <el-form-item label="状态">
            <el-select v-model="filterData.status" placeholder="全部状态" clearable>
              <el-option label="待审批" value="pending"></el-option>
              <el-option label="已批准" value="approved"></el-option>
              <el-option label="已驳回" value="rejected"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="假期类型">
            <el-select v-model="filterData.leaveType" placeholder="全部类型" clearable>
              <el-option label="年假" value="年假"></el-option>
              <el-option label="病假" value="病假"></el-option>
              <el-option label="婚假" value="婚假"></el-option>
              <el-option label="丧假" value="丧假"></el-option>
              <el-option label="其他" value="其他"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="resetFilter">重置筛选</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 统计信息 -->
      <div class="stats-panel">
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="stat-card">
              <div class="stat-value">{{ totalRequests }}</div>
              <div class="stat-label">总请假数</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-card success">
              <div class="stat-value">{{ approvedCount }}</div>
              <div class="stat-label">已批准</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-card warning">
              <div class="stat-value">{{ pendingCount }}</div>
              <div class="stat-label">待审批</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-card danger">
              <div class="stat-value">{{ rejectedCount }}</div>
              <div class="stat-label">已驳回</div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 请假记录表格 -->
      <div v-if="filteredRequests.length === 0" class="empty-state">
        <el-empty description="暂无请假记录"></el-empty>
      </div>
      <el-table v-else :data="filteredRequests" stripe>
        <el-table-column prop="id" label="申请编号" width="120"></el-table-column>
        <el-table-column prop="leaveType" label="假期类型" width="80"></el-table-column>
        <el-table-column label="请假日期" width="180">
          <template #default="{ row }">
            {{ formatDate(row.startDate) }} ~ {{ formatDate(row.endDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="天数" width="60" align="center">
          <template #default="{ row }">
            <el-tag :type="row.duration > 3 ? 'warning' : 'success'">
              {{ row.duration }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag
              :type="
                row.status === 'pending'
                  ? 'info'
                  : row.status === 'approved'
                    ? 'success'
                    : 'danger'
              "
            >
              {{
                row.status === 'pending'
                  ? '待审批'
                  : row.status === 'approved'
                    ? '已批准'
                    : '已驳回'
              }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'pending' && row.userId === currentUser.id"
              link
              type="primary"
              @click="editRequest(row)"
            >
              编辑
            </el-button>
            <el-button link type="primary" @click="viewDetails(row)">
              详情
            </el-button>
            <el-button
              v-if="row.status === 'pending' && row.userId === currentUser.id"
              link
              type="danger"
              @click="deleteRequest(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailsVisible" title="请假详情" width="600px">
      <div v-if="selectedForDetail" class="request-details">
        <!-- 基础信息 -->
        <div class="detail-section">
          <h4>基础信息</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="申请编号">
              {{ selectedForDetail.id }}
            </el-descriptions-item>
            <el-descriptions-item label="申请人">
              {{ selectedForDetail.userName }}
            </el-descriptions-item>
            <el-descriptions-item label="部门">
              {{ selectedForDetail.dept }}
            </el-descriptions-item>
            <el-descriptions-item label="假期类型">
              {{ selectedForDetail.leaveType }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 请假详情 -->
        <div class="detail-section">
          <h4>请假详情</h4>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="开始日期">
              {{ formatDate(selectedForDetail.startDate) }}
            </el-descriptions-item>
            <el-descriptions-item label="结束日期">
              {{ formatDate(selectedForDetail.endDate) }}
            </el-descriptions-item>
            <el-descriptions-item label="请假天数">
              <el-tag :type="selectedForDetail.duration > 3 ? 'warning' : 'success'">
                {{ selectedForDetail.duration }} 天
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="审批级别">
              {{ selectedForDetail.approvalLevel === 1 ? '一级' : '二级' }}
            </el-descriptions-item>
            <el-descriptions-item label="请假原因">
              {{ selectedForDetail.reason }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 审批状态 -->
        <div class="detail-section">
          <h4>审批状态</h4>
          <div class="status-display">
            <el-tag
              :type="
                selectedForDetail.status === 'pending'
                  ? 'info'
                  : selectedForDetail.status === 'approved'
                    ? 'success'
                    : 'danger'
              "
              size="large"
            >
              {{
                selectedForDetail.status === 'pending'
                  ? '待审批'
                  : selectedForDetail.status === 'approved'
                    ? '已批准'
                    : '已驳回'
              }}
            </el-tag>
          </div>
        </div>

        <!-- 审批历史 -->
        <div v-if="selectedForDetail.approvalHistory.length > 0" class="detail-section">
          <h4>审批历史</h4>
          <el-timeline>
            <el-timeline-item
              v-for="(approval, index) in selectedForDetail.approvalHistory"
              :key="index"
              :timestamp="formatDateTime(approval.timestamp)"
              placement="top"
            >
              <div class="approval-item">
                <p>
                  <strong>审批者：</strong> {{ approval.approverName }}
                </p>
                <p>
                  <strong>审批级别：</strong> {{ approval.approvalLevel === 1 ? '一级' : '二级' }}
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

const emit = defineEmits(['back', 'edit'])

const filterData = ref({
  status: '',
  leaveType: '',
})

const detailsVisible = ref(false)
const selectedForDetail = ref(null)

const currentUser = computed(() => props.store.currentUser)

const myRequests = computed(() => props.store.getMyLeaveRequests())

const filteredRequests = computed(() => {
  return myRequests.value.filter((req) => {
    if (filterData.value.status && req.status !== filterData.value.status) {
      return false
    }
    if (filterData.value.leaveType && req.leaveType !== filterData.value.leaveType) {
      return false
    }
    return true
  })
})

const totalRequests = computed(() => myRequests.value.length)
const approvedCount = computed(() => myRequests.value.filter((r) => r.status === 'approved').length)
const pendingCount = computed(() => myRequests.value.filter((r) => r.status === 'pending').length)
const rejectedCount = computed(() => myRequests.value.filter((r) => r.status === 'rejected').length)

function formatDate(date) {
  return dayjs(date).format('YYYY-MM-DD')
}

function formatDateTime(date) {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

function resetFilter() {
  filterData.value.status = ''
  filterData.value.leaveType = ''
}

function editRequest(row) {
  if (row.status !== 'pending') {
    ElMessage.error('只能编辑待审批的请假申请')
    return
  }
  emit('edit', row)
}

function viewDetails(row) {
  selectedForDetail.value = row
  detailsVisible.value = true
}

async function deleteRequest(row) {
  try {
    await ElMessageBox.confirm('确定要删除该请假申请吗？', '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await props.store.deleteLeaveRequest(row.id)
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

function goBack() {
  emit('back')
}
</script>

<style scoped>
.leave-records {
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

.filter-panel {
  margin-bottom: 24px;
  padding: 16px 20px;
  background: rgba(248, 250, 255, 0.8);
  border-radius: var(--radius-md);
  border: 1px solid rgba(11, 95, 255, 0.06);
}

.stats-panel {
  margin-bottom: 24px;
}

.stat-card {
  padding: 20px;
  background: rgba(240, 249, 255, 0.8);
  border: 1px solid rgba(179, 216, 255, 0.3);
  border-radius: var(--radius-md);
  text-align: center;
  transition: all var(--transition-normal);
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-sm);
}

.stat-card.success {
  background: rgba(240, 253, 244, 0.8);
  border-color: rgba(133, 206, 97, 0.3);
}

.stat-card.warning {
  background: rgba(253, 246, 236, 0.8);
  border-color: rgba(230, 162, 60, 0.3);
}

.stat-card.danger {
  background: rgba(254, 240, 240, 0.8);
  border-color: rgba(245, 108, 108, 0.3);
}

.stat-value {
  font-size: 28px;
  font-weight: 800;
  color: var(--text-primary);
  margin-bottom: 6px;
  letter-spacing: -0.5px;
}

.stat-label {
  font-size: 14px;
  color: var(--text-muted);
  font-weight: 500;
}

.empty-state {
  padding: 60px 0;
  text-align: center;
}

.detail-section {
  margin-bottom: 24px;
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

.status-display {
  text-align: center;
  padding: 20px;
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
</style>
