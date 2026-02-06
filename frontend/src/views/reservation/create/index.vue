<template>
  <div class="card">
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
            <el-input v-model="form.customer.phone" @blur="searchCustomer" />
          </el-form-item>
        </el-col>
      </el-row>
      
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="身份证号" prop="customer.idCard">
            <el-input v-model="form.customer.idCard" />
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

      <el-divider content-position="left">预订信息</el-divider>
      
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="房型" prop="reservation.roomTypeId">
            <el-select v-model="form.reservation.roomTypeId" style="width: 100%;" @change="onRoomTypeChange">
              <el-option v-for="item in roomTypes" :key="item.id" :label="`${item.typeName} (¥${item.price}/晚, 可用${item.availableCount}间)`" :value="item.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="房间数量" prop="reservation.roomCount">
            <el-input-number v-model="form.reservation.roomCount" :min="1" :max="10" style="width: 100%;" @change="calcPrice" />
          </el-form-item>
        </el-col>
      </el-row>
      
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="入住日期" prop="reservation.checkInDate">
            <el-date-picker v-model="form.reservation.checkInDate" type="date" value-format="YYYY-MM-DD" style="width: 100%;" @change="calcPrice" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="离店日期" prop="reservation.checkOutDate">
            <el-date-picker v-model="form.reservation.checkOutDate" type="date" value-format="YYYY-MM-DD" style="width: 100%;" @change="calcPrice" />
          </el-form-item>
        </el-col>
      </el-row>
      
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="入住人数">
            <el-input-number v-model="form.reservation.guestCount" :min="1" style="width: 100%;" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="预计总价">
            <el-input v-model="totalPrice" readonly>
              <template #prepend>¥</template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      
      <el-form-item label="备注">
        <el-input v-model="form.reservation.remark" type="textarea" :rows="3" />
      </el-form-item>
      
      <el-form-item>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">提交预订</el-button>
        <el-button @click="$router.back()">返回</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getRoomTypeList } from '@/api/room'
import { getCustomerByPhone } from '@/api/customer'
import { createReservation } from '@/api/reservation'
import dayjs from 'dayjs'

const router = useRouter()
const formRef = ref()
const roomTypes = ref([])
const totalPrice = ref(0)
const submitting = ref(false)
const selectedRoomType = ref(null)

const form = reactive({
  customer: { name: '', phone: '', idCard: '', gender: 1 },
  reservation: { roomTypeId: null, roomCount: 1, checkInDate: '', checkOutDate: '', guestCount: 1, remark: '' }
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
    { pattern: /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dXx]$/, message: '请输入正确的身份证号', trigger: 'blur' }
  ],
  'reservation.roomTypeId': [{ required: true, message: '请选择房型', trigger: 'change' }],
  'reservation.checkInDate': [{ required: true, message: '请选择入住日期', trigger: 'change' }],
  'reservation.checkOutDate': [{ required: true, message: '请选择离店日期', trigger: 'change' }]
}

const loadRoomTypes = async () => {
  const res = await getRoomTypeList()
  roomTypes.value = res.data
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
    // 客户不存在，忽略
  }
}

const onRoomTypeChange = () => {
  selectedRoomType.value = roomTypes.value.find(r => r.id === form.reservation.roomTypeId)
  calcPrice()
}

const calcPrice = () => {
  if (!selectedRoomType.value || !form.reservation.checkInDate || !form.reservation.checkOutDate) {
    totalPrice.value = 0
    return
  }
  const nights = dayjs(form.reservation.checkOutDate).diff(dayjs(form.reservation.checkInDate), 'day')
  if (nights <= 0) {
    totalPrice.value = 0
    return
  }
  totalPrice.value = (selectedRoomType.value.price * nights * form.reservation.roomCount).toFixed(2)
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await createReservation(form)
    ElMessage.success('预订成功')
    router.push('/reservation/list')
  } finally {
    submitting.value = false
  }
}

onMounted(loadRoomTypes)
</script>
