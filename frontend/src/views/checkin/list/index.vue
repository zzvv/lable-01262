<template>
  <div class="card">
    <div class="table-toolbar">
      <el-form :inline="true" :model="queryForm">
        <el-form-item>
          <el-input v-model="queryForm.checkInNo" placeholder="入住单号" clearable />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryForm.customerName" placeholder="客户姓名" clearable />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryForm.roomNumber" placeholder="房间号" clearable />
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryForm.status" placeholder="状态" clearable>
            <el-option label="入住中" :value="0" />
            <el-option label="已退房" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" @click="$router.push('/checkin/create')">
        <el-icon><Plus /></el-icon> 办理入住
      </el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="checkInNo" label="入住单号" width="220" />
      <el-table-column label="客户">
        <template #default="{ row }">
          <div>{{ row.customer?.name }}</div>
          <div style="color: #909399; font-size: 12px;">{{ row.customer?.phone }}</div>
        </template>
      </el-table-column>
      <el-table-column label="房间">
        <template #default="{ row }">{{ row.room?.roomNumber }}</template>
      </el-table-column>
      <el-table-column label="入住时间">
        <template #default="{ row }">{{ row.checkInTime?.substring(0, 16) }}</template>
      </el-table-column>
      <el-table-column label="预计退房">
        <template #default="{ row }">{{ row.expectedCheckOut }}</template>
      </el-table-column>
      <el-table-column prop="nights" label="天数" width="60" />
      <el-table-column label="房费">
        <template #default="{ row }">¥{{ row.totalPrice }}</template>
      </el-table-column>
      <el-table-column label="押金">
        <template #default="{ row }">¥{{ row.deposit }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 0 ? 'primary' : 'success'">
            {{ row.status === 0 ? '入住中' : '已退房' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button v-if="row.status === 0" type="warning" link @click="handleCheckOut(row)">退房</el-button>
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

    <!-- 退房对话框 -->
    <el-dialog v-model="checkOutVisible" title="退房结算" width="500px">
      <el-form :model="checkOutForm" label-width="100px">
        <el-form-item label="房费">
          <el-input :value="'¥' + currentRow?.totalPrice" readonly />
        </el-form-item>
        <el-form-item label="已付押金">
          <el-input :value="'¥' + currentRow?.deposit" readonly />
        </el-form-item>
        <el-form-item label="额外消费">
          <el-input-number v-model="checkOutForm.extraCharges" :min="0" :precision="2" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="折扣">
          <el-input-number v-model="checkOutForm.discount" :min="0" :precision="2" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="支付方式">
          <el-select v-model="checkOutForm.paymentMethod" style="width: 100%;">
            <el-option label="现金" :value="0" />
            <el-option label="微信" :value="1" />
            <el-option label="支付宝" :value="2" />
            <el-option label="银行卡" :value="3" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="checkOutVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCheckOut">确认退房</el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="入住详情" width="600px">
      <el-descriptions :column="2" border v-if="currentRow">
        <el-descriptions-item label="入住单号">{{ currentRow.checkInNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentRow.status === 0 ? 'primary' : 'success'">
            {{ currentRow.status === 0 ? '入住中' : '已退房' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="客户姓名">{{ currentRow.customer?.name }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentRow.customer?.phone }}</el-descriptions-item>
        <el-descriptions-item label="房间号">{{ currentRow.room?.roomNumber }}</el-descriptions-item>
        <el-descriptions-item label="楼层">{{ currentRow.room?.floor }}楼</el-descriptions-item>
        <el-descriptions-item label="入住时间">{{ currentRow.checkInTime }}</el-descriptions-item>
        <el-descriptions-item label="退房时间">{{ currentRow.checkOutTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="房费">¥{{ currentRow.totalPrice }}</el-descriptions-item>
        <el-descriptions-item label="押金">¥{{ currentRow.deposit }}</el-descriptions-item>
        <el-descriptions-item label="额外消费">¥{{ currentRow.extraCharges }}</el-descriptions-item>
        <el-descriptions-item label="实际金额">¥{{ currentRow.actualAmount }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { getCheckInPage, checkOut } from '@/api/checkin'

const loading = ref(false)
const tableData = ref([])
const checkOutVisible = ref(false)
const detailVisible = ref(false)
const currentRow = ref(null)

const queryForm = reactive({ checkInNo: '', customerName: '', roomNumber: '', status: null })
const pagination = reactive({ current: 1, size: 10, total: 0 })
const checkOutForm = reactive({ extraCharges: 0, discount: 0, paymentMethod: 1 })

const loadData = async () => {
  loading.value = true
  try {
    const res = await getCheckInPage({ current: pagination.current, size: pagination.size, ...queryForm })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  Object.assign(queryForm, { checkInNo: '', customerName: '', roomNumber: '', status: null })
  pagination.current = 1
  loadData()
}

const handleCheckOut = (row) => {
  currentRow.value = row
  Object.assign(checkOutForm, { extraCharges: 0, discount: 0, paymentMethod: 1 })
  checkOutVisible.value = true
}

const submitCheckOut = async () => {
  await checkOut(currentRow.value.id, checkOutForm)
  ElMessage.success('退房成功')
  checkOutVisible.value = false
  loadData()
}

const handleDetail = (row) => {
  currentRow.value = row
  detailVisible.value = true
}

loadData()
</script>
