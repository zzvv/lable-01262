<template>
  <div class="card">
    <div class="table-toolbar">
      <el-form :inline="true" :model="queryForm">
        <el-form-item>
          <el-input v-model="queryForm.reservationNo" placeholder="预订单号" clearable />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryForm.customerName" placeholder="客户姓名" clearable />
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryForm.status" placeholder="状态" clearable>
            <el-option label="待确认" :value="0" />
            <el-option label="已确认" :value="1" />
            <el-option label="已入住" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已取消" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" @click="$router.push('/reservation/create')">
        <el-icon><Plus /></el-icon> 新建预订
      </el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="reservationNo" label="预订单号" width="180" />
      <el-table-column label="客户">
        <template #default="{ row }">
          <div>{{ row.customer?.name }}</div>
          <div style="color: #909399; font-size: 12px;">{{ row.customer?.phone }}</div>
        </template>
      </el-table-column>
      <el-table-column label="房型">
        <template #default="{ row }">{{ row.roomType?.typeName }}</template>
      </el-table-column>
      <el-table-column label="房间">
        <template #default="{ row }">{{ row.room?.roomNumber || '-' }}</template>
      </el-table-column>
      <el-table-column label="入住日期">
        <template #default="{ row }">{{ row.checkInDate }}</template>
      </el-table-column>
      <el-table-column label="离店日期">
        <template #default="{ row }">{{ row.checkOutDate }}</template>
      </el-table-column>
      <el-table-column prop="nights" label="天数" width="60" />
      <el-table-column label="总价">
        <template #default="{ row }">¥{{ row.totalPrice }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="statusType[row.status]">{{ statusMap[row.status] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button v-if="row.status === 0" type="success" link @click="handleConfirm(row)">确认</el-button>
          <el-button v-if="row.status === 1" type="primary" link @click="handleCheckIn(row)">入住</el-button>
          <el-button v-if="row.status < 2" type="warning" link @click="handleCancel(row)">取消</el-button>
          <el-button type="info" link @click="handleDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="pagination.current"
      v-model:page-size="pagination.size"
      :total="pagination.total"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next"
      @change="loadData"
      style="margin-top: 16px; justify-content: flex-end;"
    />

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="预订详情" width="600px">
      <el-descriptions :column="2" border v-if="currentRow">
        <el-descriptions-item label="预订单号">{{ currentRow.reservationNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusType[currentRow.status]">{{ statusMap[currentRow.status] }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="客户姓名">{{ currentRow.customer?.name }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentRow.customer?.phone }}</el-descriptions-item>
        <el-descriptions-item label="房型">{{ currentRow.roomType?.typeName }}</el-descriptions-item>
        <el-descriptions-item label="房间号">{{ currentRow.room?.roomNumber || '-' }}</el-descriptions-item>
        <el-descriptions-item label="入住日期">{{ currentRow.checkInDate }}</el-descriptions-item>
        <el-descriptions-item label="离店日期">{{ currentRow.checkOutDate }}</el-descriptions-item>
        <el-descriptions-item label="入住天数">{{ currentRow.nights }}晚</el-descriptions-item>
        <el-descriptions-item label="总价">¥{{ currentRow.totalPrice }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentRow.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getReservationPage, confirmReservation, cancelReservation } from '@/api/reservation'

const router = useRouter()

const statusMap = { 0: '待确认', 1: '已确认', 2: '已入住', 3: '已完成', 4: '已取消' }
const statusType = { 0: 'info', 1: 'warning', 2: 'primary', 3: 'success', 4: 'danger' }

const loading = ref(false)
const tableData = ref([])
const detailVisible = ref(false)
const currentRow = ref(null)

const queryForm = reactive({ reservationNo: '', customerName: '', status: null })
const pagination = reactive({ current: 1, size: 10, total: 0 })

const loadData = async () => {
  loading.value = true
  try {
    const res = await getReservationPage({ current: pagination.current, size: pagination.size, ...queryForm })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  Object.assign(queryForm, { reservationNo: '', customerName: '', status: null })
  pagination.current = 1
  loadData()
}

const handleConfirm = async (row) => {
  await ElMessageBox.confirm('确认该预订并分配房间？', '提示')
  await confirmReservation(row.id)
  ElMessage.success('确认成功')
  loadData()
}

const handleCheckIn = (row) => {
  router.push({ path: '/checkin/create', query: { reservationId: row.id } })
}

const handleCancel = async (row) => {
  const { value } = await ElMessageBox.prompt('请输入取消原因', '取消预订', { inputType: 'textarea' })
  await cancelReservation(row.id, value)
  ElMessage.success('取消成功')
  loadData()
}

const handleDetail = (row) => {
  currentRow.value = row
  detailVisible.value = true
}

loadData()
</script>
