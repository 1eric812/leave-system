<!-- 请假申请表单组件 -->
<template>
  <div class="leave-application">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span class="title">请假申请</span>
        </div>
      </template>

      <el-form :model="formData" label-width="100px" @submit.prevent="submitForm">
        <!-- 基础信息（只读） -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="申请人">
              <el-input v-model="currentUser.name" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="部门">
              <el-input v-model="currentUser.dept" disabled></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 请假类型 -->
        <el-form-item label="假期类型" prop="leaveType">
          <el-select v-model="formData.leaveType" placeholder="请选择假期类型">
            <el-option label="年假" value="年假"></el-option>
            <el-option label="病假" value="病假"></el-option>
            <el-option label="婚假" value="婚假"></el-option>
            <el-option label="丧假" value="丧假"></el-option>
            <el-option label="其他" value="其他"></el-option>
          </el-select>
        </el-form-item>

        <!-- 日期选择 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker
                v-model="formData.startDate"
                type="date"
                placeholder="选择开始日期"
                @change="onDateChange"
              ></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期" prop="endDate">
              <el-date-picker
                v-model="formData.endDate"
                type="date"
                placeholder="选择结束日期"
                @change="onDateChange"
              ></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 显示计算结果 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="请假天数">
              <el-input v-model.number="duration" type="number" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="审批级别">
              <el-tag :type="approvalLevelTag">
                {{ approvalLevelLabel }}
              </el-tag>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 请假原因 -->
        <el-form-item label="请假原因" prop="reason">
          <el-input
            v-model="formData.reason"
            type="textarea"
            :rows="4"
            placeholder="请详细说明请假原因"
          ></el-input>
        </el-form-item>

        <!-- 操作按钮 -->
        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="submitting">
            提交申请
          </el-button>
          <el-button @click="resetForm">重置</el-button>
          <el-button @click="goBack">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref, computed } from 'vue'
import dayjs from 'dayjs'
import { ElMessage, ElMessageBox } from 'element-plus'

const props = defineProps({
  store: Object,
  request: Object,
})

const emit = defineEmits(['submit', 'back'])

const submitting = ref(false)
const duration = ref(0)
const currentUser = computed(() => props.store.currentUser)

const formData = reactive({
  leaveType: props.request?.leaveType || '',
  startDate: props.request?.startDate ? new Date(props.request.startDate) : null,
  endDate: props.request?.endDate ? new Date(props.request.endDate) : null,
  reason: props.request?.reason || '',
})

const approvalLevelTag = computed(() => {
  if (props.store.currentUser.level === 3) return ''
  if (props.store.currentUser.level === 2) return 'warning'
  return duration.value > 3 ? 'warning' : 'success'
})

const approvalLevelLabel = computed(() => {
  if (props.store.currentUser.level === 3) return '自动批准'
  if (props.store.currentUser.level === 2) return '需总经理审批'
  return duration.value > 3 ? '二级审批（部门经理+总经理）' : '一级审批（部门经理）'
})

function onDateChange() {
  if (formData.startDate && formData.endDate) {
    const start = dayjs(formData.startDate)
    const end = dayjs(formData.endDate)
    if (end.isBefore(start)) {
      ElMessage.error('结束日期不能早于开始日期')
      formData.endDate = null
      duration.value = 0
    } else {
      duration.value = end.diff(start, 'day') + 1
    }
  }
}

function validateForm() {
  if (!formData.leaveType) {
    ElMessage.error('请选择假期类型')
    return false
  }
  if (!formData.startDate || !formData.endDate) {
    ElMessage.error('请选择请假日期')
    return false
  }
  if (!formData.reason.trim()) {
    ElMessage.error('请填写请假原因')
    return false
  }
  return true
}

async function submitForm() {
  if (!validateForm()) return

  submitting.value = true
  try {
    emit('submit', {
      ...formData,
      duration: duration.value,
    })
  } finally {
    submitting.value = false
  }
}

function resetForm() {
  formData.leaveType = ''
  formData.startDate = null
  formData.endDate = null
  formData.reason = ''
  duration.value = 0
}

function goBack() {
  emit('back')
}
</script>

<style scoped>
.leave-application {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
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

:deep(.el-input__wrapper) {
  background-color: #f8faff !important;
}

:deep(.el-textarea__inner) {
  background-color: #f8faff !important;
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}

:deep(.el-form-item__label) {
  font-weight: 600 !important;
  color: var(--text-secondary) !important;
}

:deep(.el-select),
:deep(.el-date-editor) {
  width: 100% !important;
}
</style>
