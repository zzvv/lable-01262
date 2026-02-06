<template>
  <div class="card">
    <div class="table-toolbar">
      <el-form :inline="true" :model="queryForm">
        <el-form-item>
          <el-input v-model="queryForm.roomNumber" placeholder="房间号" clearable />
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryForm.roomTypeId" placeholder="房型" clearable>
            <el-option v-for="item in roomTypes" :key="item.id" :label="item.typeName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryForm.status" placeholder="状态" clearable>
            <el-option label="空闲" :value="0" />
            <el-option label="已预订" :value="1" />
            <el-option label="入住中" :value="2" />
            <el-option label="清洁中" :value="3" />
            <el-option label="维修中" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon> 新增房间
      </el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="roomNumber" label="房间号" width="100" />
      <el-table-column label="房型">
        <template #default="{ row }">{{ row.roomType?.typeName }}</template>
      </el-table-column>
      <el-table-column label="价格">
        <template #default="{ row }">¥{{ row.roomType?.price }}</template>
      </el-table-column>
      <el-table-column prop="floor" label="楼层" width="80" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType[row.status]">{{ statusMap[row.status] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="warning" link @click="handleChangeStatus(row)">改状态</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
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

    <!-- 编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="房间号" prop="roomNumber">
          <el-input v-model="form.roomNumber" />
        </el-form-item>
        <el-form-item label="房型" prop="roomTypeId">
          <el-select v-model="form.roomTypeId" style="width: 100%;">
            <el-option v-for="item in roomTypes" :key="item.id" :label="item.typeName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="楼层" prop="floor">
          <el-input-number v-model="form.floor" :min="1" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 状态修改对话框 -->
    <el-dialog v-model="statusDialogVisible" title="修改房间状态" width="400px">
      <el-form label-width="80px">
        <el-form-item label="房间号">{{ currentRoom?.roomNumber }}</el-form-item>
        <el-form-item label="新状态">
          <el-select v-model="newStatus" style="width: 100%;">
            <el-option label="空闲" :value="0" />
            <el-option label="已预订" :value="1" />
            <el-option label="入住中" :value="2" />
            <el-option label="清洁中" :value="3" />
            <el-option label="维修中" :value="4" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="statusDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitStatus">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRoomPage, createRoom, updateRoom, updateRoomStatus, deleteRoom, getRoomTypeList } from '@/api/room'

const statusMap = { 0: '空闲', 1: '已预订', 2: '入住中', 3: '清洁中', 4: '维修中' }
const statusType = { 0: 'success', 1: 'warning', 2: 'primary', 3: 'info', 4: 'danger' }

const loading = ref(false)
const tableData = ref([])
const roomTypes = ref([])
const dialogVisible = ref(false)
const statusDialogVisible = ref(false)
const formRef = ref()
const currentRoom = ref(null)
const newStatus = ref(0)

const queryForm = reactive({ roomNumber: '', roomTypeId: null, status: null })
const pagination = reactive({ current: 1, size: 10, total: 0 })
const form = reactive({ id: null, roomNumber: '', roomTypeId: null, floor: 1, remark: '' })

const rules = {
  roomNumber: [{ required: true, message: '请输入房间号', trigger: 'blur' }],
  roomTypeId: [{ required: true, message: '请选择房型', trigger: 'change' }]
}

const dialogTitle = computed(() => form.id ? '编辑房间' : '新增房间')

const loadRoomTypes = async () => {
  const res = await getRoomTypeList()
  roomTypes.value = res.data
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getRoomPage({ current: pagination.current, size: pagination.size, ...queryForm })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  Object.assign(queryForm, { roomNumber: '', roomTypeId: null, status: null })
  pagination.current = 1
  loadData()
}

const handleAdd = () => {
  Object.assign(form, { id: null, roomNumber: '', roomTypeId: null, floor: 1, remark: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    if (form.id) {
      await updateRoom(form)
      ElMessage.success('更新成功')
    } else {
      await createRoom(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const handleChangeStatus = (row) => {
  currentRoom.value = row
  newStatus.value = row.status
  statusDialogVisible.value = true
}

const submitStatus = async () => {
  await updateRoomStatus(currentRoom.value.id, newStatus.value)
  ElMessage.success('状态更新成功')
  statusDialogVisible.value = false
  loadData()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定删除该房间吗？', '提示', { type: 'warning' })
  await deleteRoom(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(() => {
  loadRoomTypes()
  loadData()
})
</script>
