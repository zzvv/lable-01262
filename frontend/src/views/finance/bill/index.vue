<template>
  <div class="card">
    <div class="table-toolbar">
      <el-form :inline="true" :model="queryForm">
        <el-form-item>
          <el-input v-model="queryForm.billNo" placeholder="账单号" clearable />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryForm.customerName" placeholder="客户姓名" clearable />
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryForm.billType" placeholder="账单类型" clearable>
            <el-option label="房费" :value="0" />
            <el-option label="消费" :value="1" />
            <el-option label="押金" :value="2" />
            <el-option label="退款" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryForm.paymentStatus" placeholder="支付状态" clearable>
            <el-option label="未支付" :value="0" />
            <el-option label="已支付" :value="1" />
            <el-option label="已退款" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      <el-button type="success" @click="handleExport" :loading="exporting">
        <el-icon><Download /></el-icon> 导出Excel
      </el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="billNo" label="账单号" width="180" />
      <el-table-column label="客户">
        <template #default="{ row }">{{ row.customer?.name }}</template>
      </el-table-column>
      <el-table-column prop="billType" label="类型" width="80">
        <template #default="{ row }">
          <el-tag :type="typeTagType[row.billType]">{{ typeMap[row.billType] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="itemName" label="项目" />
      <el-table-column label="金额" width="100">
        <template #default="{ row }">¥{{ row.amount }}</template>
      </el-table-column>
      <el-table-column prop="paymentStatus" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="statusTagType[row.paymentStatus]">{{ statusMap[row.paymentStatus] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="paymentMethod" label="支付方式" width="80">
        <template #default="{ row }">{{ methodMap[row.paymentMethod] || '-' }}</template>
      </el-table-column>
      <el-table-column label="支付时间" width="160">
        <template #default="{ row }">{{ row.paymentTime || '-' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button v-if="row.paymentStatus === 0" type="primary" link @click="handlePay(row)">收款</el-button>
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

    <!-- 收款对话框 -->
    <el-dialog v-model="payVisible" title="收款" width="400px">
      <el-form label-width="80px">
        <el-form-item label="账单号">{{ currentRow?.billNo }}</el-form-item>
        <el-form-item label="金额">¥{{ currentRow?.amount }}</el-form-item>
        <el-form-item label="支付方式">
          <el-select v-model="paymentMethod" style="width: 100%;">
            <el-option label="现金" :value="0" />
            <el-option label="微信" :value="1" />
            <el-option label="支付宝" :value="2" />
            <el-option label="银行卡" :value="3" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="payVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPay">确认收款</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { getBillPage, payBill, exportBills } from '@/api/bill'

const typeMap = { 0: '房费', 1: '消费', 2: '押金', 3: '退款' }
const typeTagType = { 0: 'primary', 1: 'warning', 2: 'success', 3: 'danger' }
const statusMap = { 0: '未支付', 1: '已支付', 2: '已退款' }
const statusTagType = { 0: 'danger', 1: 'success', 2: 'info' }
const methodMap = { 0: '现金', 1: '微信', 2: '支付宝', 3: '银行卡', 4: '挂账' }

const loading = ref(false)
const exporting = ref(false)
const tableData = ref([])
const payVisible = ref(false)
const currentRow = ref(null)
const paymentMethod = ref(1)

const queryForm = reactive({ billNo: '', customerName: '', billType: null, paymentStatus: null })
const pagination = reactive({ current: 1, size: 10, total: 0 })

const loadData = async () => {
  loading.value = true
  try {
    const res = await getBillPage({ current: pagination.current, size: pagination.size, ...queryForm })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  Object.assign(queryForm, { billNo: '', customerName: '', billType: null, paymentStatus: null })
  pagination.current = 1
  loadData()
}

const handlePay = (row) => {
  currentRow.value = row
  paymentMethod.value = 1
  payVisible.value = true
}

const submitPay = async () => {
  await payBill(currentRow.value.id, paymentMethod.value)
  ElMessage.success('收款成功')
  payVisible.value = false
  loadData()
}

const handleExport = async () => {
  exporting.value = true
  try {
    const res = await exportBills(queryForm)
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '账单数据.xlsx'
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  } finally {
    exporting.value = false
  }
}

loadData()
</script>
