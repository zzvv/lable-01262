<template>
  <div class="card">
    <div class="table-toolbar">
      <el-form :inline="true" :model="queryForm">
        <el-form-item>
          <el-input v-model="queryForm.roomNumber" placeholder="房间号" clearable />
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryForm.status" placeholder="状态" clearable>
            <el-option label="有效" :value="0" />
            <el-option label="已作废" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryForm.isBilled" placeholder="对账状态" clearable>
            <el-option label="未对账" :value="0" />
            <el-option label="已对账" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon> 登记消费
      </el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="consumptionNo" label="消费记录号" width="180" />
      <el-table-column prop="roomNumber" label="房间号" width="100" />
      <el-table-column prop="itemName" label="商品名称" />
      <el-table-column prop="quantity" label="数量" width="80" />
      <el-table-column prop="unitPrice" label="单价" width="100">
        <template #default="{ row }">¥{{ row.unitPrice }}</template>
      </el-table-column>
      <el-table-column prop="amount" label="金额" width="100">
        <template #default="{ row }">¥{{ row.amount }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 0 ? 'success' : 'info'">{{ row.status === 0 ? '有效' : '已作废' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="isBilled" label="对账状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.isBilled === 1 ? 'success' : 'warning'">{{ row.isBilled === 1 ? '已对账' : '未对账' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160" />
      <el-table-column label="操作" width="250" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleView(row)">查看</el-button>
          <el-button 
            v-if="row.status === 0" 
            type="danger" 
            link 
            @click="handleCancel(row)"
          >
            作废
          </el-button>
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

    <!-- 登记消费对话框 -->
    <el-dialog v-model="addVisible" title="登记消费" width="600px">
      <el-form :model="consumptionForm" :rules="rules" ref="consumptionFormRef" label-width="100px">
        <el-form-item label="入住单" prop="checkInId">
          <el-select 
            v-model="consumptionForm.checkInId" 
            placeholder="请选择入住单" 
            style="width: 100%"
            filterable
            @focus="loadCheckInList"
          >
            <el-option
              v-for="item in checkInList"
              :key="item.id"
              :label="`${item.roomNumber} - ${item.customer?.name}`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="商品名称" prop="itemName">
          <el-input v-model="consumptionForm.itemName" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input-number v-model="consumptionForm.quantity" :min="1" />
        </el-form-item>
        <el-form-item label="单价" prop="unitPrice">
          <el-input-number 
            v-model="consumptionForm.unitPrice" 
            :min="0.01" 
            :precision="2" 
            :step="0.1"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="金额" prop="amount">
          <el-input-number 
            v-model="consumptionForm.amount" 
            :min="0.01" 
            :precision="2" 
            :step="0.1"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="consumptionForm.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" @click="submitConsumption" :loading="submitting">确认登记</el-button>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="viewVisible" title="消费记录详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="消费记录号">{{ currentDetail?.consumptionNo }}</el-descriptions-item>
        <el-descriptions-item label="房间号">{{ currentDetail?.roomNumber }}</el-descriptions-item>
        <el-descriptions-item label="商品名称">{{ currentDetail?.itemName }}</el-descriptions-item>
        <el-descriptions-item label="数量">{{ currentDetail?.quantity }}</el-descriptions-item>
        <el-descriptions-item label="单价">¥{{ currentDetail?.unitPrice }}</el-descriptions-item>
        <el-descriptions-item label="金额">¥{{ currentDetail?.amount }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentDetail?.status === 0 ? 'success' : 'info'">
            {{ currentDetail?.status === 0 ? '有效' : '已作废' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="对账状态">
          <el-tag :type="currentDetail?.isBilled === 1 ? 'success' : 'warning'">
            {{ currentDetail?.isBilled === 1 ? '已对账' : '未对账' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentDetail?.createTime }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentDetail?.remark || '-' }}</el-descriptions-item>
        <el-descriptions-item v-if="currentDetail?.status === 1" label="作废时间" :span="2">
          {{ currentDetail?.cancelTime }}
        </el-descriptions-item>
        <el-descriptions-item v-if="currentDetail?.status === 1" label="作废备注" :span="2">
          {{ currentDetail?.cancelRemark || '-' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 作废对话框 -->
    <el-dialog v-model="cancelVisible" title="作废消费记录" width="500px">
      <el-alert type="warning" title="作废操作不可恢复，请确认" style="margin-bottom: 16px;" />
      <el-form label-width="100px">
        <el-form-item label="消费记录号">{{ currentCancel?.consumptionNo }}</el-form-item>
        <el-form-item label="商品名称">{{ currentCancel?.itemName }}</el-form-item>
        <el-form-item label="金额">¥{{ currentCancel?.amount }}</el-form-item>
        <el-form-item label="作废原因">
          <el-input v-model="cancelRemark" type="textarea" :rows="3" placeholder="请输入作废原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="cancelVisible = false">取消</el-button>
        <el-button type="danger" @click="submitCancel" :loading="cancelling">确认作废</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { 
  getConsumptionPage, 
  createConsumption, 
  getConsumptionById, 
  cancelConsumption 
} from '@/api/consumption'
import { getCheckInPage } from '@/api/checkin'

const loading = ref(false)
const submitting = ref(false)
const cancelling = ref(false)
const tableData = ref([])
const addVisible = ref(false)
const viewVisible = ref(false)
const cancelVisible = ref(false)
const consumptionFormRef = ref(null)
const currentDetail = ref(null)
const currentCancel = ref(null)
const cancelRemark = ref('')
const checkInList = ref([])

const queryForm = reactive({ roomNumber: '', status: null, isBilled: null })
const pagination = reactive({ current: 1, size: 10, total: 0 })

const consumptionForm = reactive({
  checkInId: null,
  itemName: '',
  quantity: 1,
  unitPrice: 0,
  amount: 0,
  remark: ''
})

const rules = {
  checkInId: [{ required: true, message: '请选择入住单', trigger: 'change' }],
  itemName: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  unitPrice: [
    { required: true, message: '请输入单价', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '单价必须大于0', trigger: 'blur' }
  ],
  amount: [
    { required: true, message: '请输入金额', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '金额必须大于0', trigger: 'blur' }
  ]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getConsumptionPage({ current: pagination.current, size: pagination.size, ...queryForm })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  Object.assign(queryForm, { roomNumber: '', status: null, isBilled: null })
  pagination.current = 1
  loadData()
}

const loadCheckInList = async () => {
  try {
    const res = await getCheckInPage({ current: 1, size: 100, status: 0 })
    checkInList.value = res.data.records
  } catch (error) {
    ElMessage.error('加载入住单列表失败')
  }
}

const handleAdd = () => {
  Object.assign(consumptionForm, {
    checkInId: null,
    itemName: '',
    quantity: 1,
    unitPrice: 0,
    amount: 0,
    remark: ''
  })
  addVisible.value = true
}

const submitConsumption = async () => {
  if (!consumptionFormRef.value) return
  
  await consumptionFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        await createConsumption(consumptionForm)
        ElMessage.success('消费登记成功')
        addVisible.value = false
        loadData()
      } finally {
        submitting.value = false
      }
    }
  })
}

const handleView = async (row) => {
  try {
    const res = await getConsumptionById(row.id)
    currentDetail.value = res.data
    viewVisible.value = true
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}

const handleCancel = (row) => {
  currentCancel.value = row
  cancelRemark.value = ''
  cancelVisible.value = true
}

const submitCancel = async () => {
  cancelling.value = true
  try {
    await cancelConsumption(currentCancel.value.id, cancelRemark.value)
    ElMessage.success('作废成功')
    cancelVisible.value = false
    loadData()
  } finally {
    cancelling.value = false
  }
}

loadData()
</script>
