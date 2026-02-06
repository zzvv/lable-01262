<template>
  <div class="card">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="预订入住" name="reservation">
        <el-form label-width="100px" style="max-width: 600px;">
          <el-form-item label="选择预订">
            <el-select v-model="selectedReservation" style="width: 100%;" filterable placeholder="搜索预订单号或客户姓名">
              <el-option
                v-for="item in reservations"
                :key="item.id"
                :label="`${item.reservationNo} - ${item.customer?.name} - ${item.roomType?.typeName}`"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="押金">
            <el-input-number v-model="deposit" :min="0" :precision="2" style="width: 100%;" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleReservationCheckIn" :loading="submitting">办理入住</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <el-tab-pane label="散客入住" name="walkin">
        <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="max-width: 800px;">
          <el-divider content-position="left">客户信息</el-divider>
          
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="客户姓名" prop="customer.name">
                <el-input v-model="form.customer.name" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="联系电话" prop="customer.phone">
                <el-input v-model="form.customer.phone" maxlength="11" show-word-limit placeholder="请输入11位手机号" @blur="searchCustomer" />
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="身份证号" prop="customer.idCard">
                <el-input v-model="form.customer.idCard" maxlength="18" show-word-limit placeholder="请输入18位身份证号" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="性别">
                <el-radio-group v-model="form.customer.gender">
                  <el-radio :label="1">男</el-radio>
                  <el-radio :label="2">女</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>

          <el-divider content-position="left">入住信息</el-divider>
          
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="房型" prop="roomTypeId">
                <el-select v-model="form.roomTypeId" style="width: 100%;" @change="loadAvailableRooms">
                  <el-option v-for="item in roomTypes" :key="item.id" :label="`${item.typeName} (¥${item.price}/晚)`" :value="item.id" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="选择房间" prop="roomId">
                <el-select v-model="form.roomId" style="width: 100%;">
                  <el-option v-for="item in availableRooms" :key="item.id" :label="item.roomNumber" :value="item.id" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="预计退房" prop="checkIn.expectedCheckOut">
                <el-date-picker v-model="form.checkIn.expectedCheckOut" type="date" value-format="YYYY-MM-DD" style="width: 100%;" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="押金">
                <el-input-number v-model="form.deposit" :min="0" :precision="2" style="width: 100%;" />
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-form-item label="备注">
            <el-input v-model="form.checkIn.remark" type="textarea" :rows="3" />
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="handleWalkInCheckIn" :loading="submitting">办理入住</el-button>
            <el-button @click="$router.back()">返回</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getRoomTypeList, getAvailableRooms } from '@/api/room'
import { getReservationPage } from '@/api/reservation'
import { getCustomerByPhone } from '@/api/customer'
import { checkInFromReservation, walkInCheckIn } from '@/api/checkin'

const route = useRoute()
const router = useRouter()
const formRef = ref()

const activeTab = ref('reservation')
const roomTypes = ref([])
const availableRooms = ref([])
const reservations = ref([])
const selectedReservation = ref(null)
const deposit = ref(200)
const submitting = ref(false)

const form = reactive({
  customer: { name: '', phone: '', idCard: '', gender: 1 },
  checkIn: { expectedCheckOut: '', remark: '' },
  roomTypeId: null,
  roomId: null,
  deposit: 200
})

const rules = {
  'customer.name': [
    { required: true, message: '请输入客户姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度为2-20个字符', trigger: 'blur' }
  ],
  'customer.phone': [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  'customer.idCard': [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dXx]$/, message: '请输入正确的身份证号', trigger: 'blur' }
  ],
  roomTypeId: [{ required: true, message: '请选择房型', trigger: 'change' }],
  roomId: [{ required: true, message: '请选择房间', trigger: 'change' }],
  'checkIn.expectedCheckOut': [{ required: true, message: '请选择预计退房日期', trigger: 'change' }]
}

const loadRoomTypes = async () => {
  const res = await getRoomTypeList()
  roomTypes.value = res.data
}

const loadReservations = async () => {
  const res = await getReservationPage({ current: 1, size: 100, status: 1 })
  reservations.value = res.data.records
}

const loadAvailableRooms = async () => {
  if (!form.roomTypeId) return
  const res = await getAvailableRooms(form.roomTypeId)
  availableRooms.value = res.data
  form.roomId = null
}

const searchCustomer = async () => {
  if (!form.customer.phone) return
  try {
    const res = await getCustomerByPhone(form.customer.phone)
    if (res.data) {
      Object.assign(form.customer, res.data)
      ElMessage.success('已找到客户信息')
    }
  } catch (e) {
    // ignore
  }
}

const handleReservationCheckIn = async () => {
  if (!selectedReservation.value) {
    ElMessage.warning('请选择预订')
    return
  }
  submitting.value = true
  try {
    await checkInFromReservation({ reservationId: selectedReservation.value, deposit: deposit.value })
    ElMessage.success('入住成功')
    router.push('/checkin/list')
  } finally {
    submitting.value = false
  }
}

const handleWalkInCheckIn = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await walkInCheckIn({
      customer: form.customer,
      checkIn: form.checkIn,
      roomId: form.roomId,
      deposit: form.deposit
    })
    ElMessage.success('入住成功')
    router.push('/checkin/list')
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  await loadRoomTypes()
  loadReservations()
  
  // 从路由参数初始化
  if (route.query.reservationId) {
    selectedReservation.value = Number(route.query.reservationId)
    activeTab.value = 'reservation'
  } else if (route.query.roomId) {
    activeTab.value = 'walkin'
    // 如果有房型ID，先选中房型再加载可用房间
    if (route.query.roomTypeId) {
      form.roomTypeId = Number(route.query.roomTypeId)
      await loadAvailableRooms()
      form.roomId = Number(route.query.roomId)
    }
  }
})
</script>
