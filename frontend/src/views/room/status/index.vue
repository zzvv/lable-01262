<template>
  <div>
    <!-- 统计卡片 -->
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="4" v-for="(item, index) in statusStats" :key="index">
        <div class="status-card" :style="{ borderColor: statusColors[item.status] }">
          <div class="status-count">{{ item.count }}</div>
          <div class="status-name">{{ statusMap[item.status] }}</div>
        </div>
      </el-col>
    </el-row>

    <!-- 房态图 -->
    <div class="card">
      <div class="floor-filter">
        <span>楼层筛选：</span>
        <el-radio-group v-model="selectedFloor" @change="filterRooms">
          <el-radio-button :label="0">全部</el-radio-button>
          <el-radio-button v-for="floor in floors" :key="floor" :label="floor">{{ floor }}楼</el-radio-button>
        </el-radio-group>
      </div>

      <div class="room-grid">
        <div
          v-for="room in filteredRooms"
          :key="room.id"
          class="room-card"
          :class="'status-' + room.status"
          @click="handleRoomClick(room)"
        >
          <div class="room-number">{{ room.roomNumber }}</div>
          <div class="room-type">{{ room.roomType?.typeName }}</div>
          <div class="room-status">{{ statusMap[room.status] }}</div>
        </div>
      </div>

      <div class="legend">
        <span class="legend-item" v-for="(name, status) in statusMap" :key="status">
          <span class="legend-color" :style="{ backgroundColor: statusColors[status] }"></span>
          {{ name }}
        </span>
      </div>
    </div>

    <!-- 房间详情对话框 -->
    <el-dialog v-model="detailVisible" title="房间详情" width="400px">
      <el-descriptions :column="1" border v-if="currentRoom">
        <el-descriptions-item label="房间号">{{ currentRoom.roomNumber }}</el-descriptions-item>
        <el-descriptions-item label="房型">{{ currentRoom.roomType?.typeName }}</el-descriptions-item>
        <el-descriptions-item label="价格">¥{{ currentRoom.roomType?.price }}</el-descriptions-item>
        <el-descriptions-item label="楼层">{{ currentRoom.floor }}楼</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusType[currentRoom.status]">{{ statusMap[currentRoom.status] }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button v-if="currentRoom?.status === 0" type="primary" @click="goCheckIn">办理入住</el-button>
        <el-button v-if="currentRoom?.status === 3" type="success" @click="setFree">设为空闲</el-button>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getRoomPage, updateRoomStatus, getRoomStatusStats } from '@/api/room'

const router = useRouter()

const statusMap = { 0: '空闲', 1: '已预订', 2: '入住中', 3: '清洁中', 4: '维修中' }
const statusType = { 0: 'success', 1: 'warning', 2: 'primary', 3: 'info', 4: 'danger' }
const statusColors = { 0: '#67c23a', 1: '#e6a23c', 2: '#409eff', 3: '#909399', 4: '#f56c6c' }

const rooms = ref([])
const statusStats = ref([])
const selectedFloor = ref(0)
const detailVisible = ref(false)
const currentRoom = ref(null)

const floors = computed(() => {
  const floorSet = new Set(rooms.value.map(r => r.floor))
  return Array.from(floorSet).sort((a, b) => a - b)
})

const filteredRooms = computed(() => {
  if (selectedFloor.value === 0) return rooms.value
  return rooms.value.filter(r => r.floor === selectedFloor.value)
})

const loadData = async () => {
  const [roomRes, statsRes] = await Promise.all([
    getRoomPage({ current: 1, size: 100 }),
    getRoomStatusStats()
  ])
  rooms.value = roomRes.data.records
  statusStats.value = statsRes.data
}

const handleRoomClick = (room) => {
  currentRoom.value = room
  detailVisible.value = true
}

const goCheckIn = () => {
  detailVisible.value = false
  router.push({ path: '/checkin/create', query: { roomId: currentRoom.value.id } })
}

const setFree = async () => {
  await updateRoomStatus(currentRoom.value.id, 0)
  ElMessage.success('已设为空闲')
  detailVisible.value = false
  loadData()
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.status-card {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  text-align: center;
  border-left: 4px solid;
  
  .status-count {
    font-size: 28px;
    font-weight: bold;
  }
  
  .status-name {
    color: #909399;
    margin-top: 8px;
  }
}

.floor-filter {
  margin-bottom: 20px;
}

.room-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.room-card {
  padding: 16px;
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  transition: transform 0.2s;
  color: #fff;
  
  &:hover {
    transform: scale(1.05);
  }
  
  &.status-0 { background: #67c23a; }
  &.status-1 { background: #e6a23c; }
  &.status-2 { background: #409eff; }
  &.status-3 { background: #909399; }
  &.status-4 { background: #f56c6c; }
  
  .room-number {
    font-size: 20px;
    font-weight: bold;
  }
  
  .room-type {
    font-size: 12px;
    margin-top: 4px;
    opacity: 0.9;
  }
  
  .room-status {
    font-size: 12px;
    margin-top: 8px;
  }
}

.legend {
  display: flex;
  gap: 20px;
  justify-content: center;
  
  .legend-item {
    display: flex;
    align-items: center;
    gap: 6px;
  }
  
  .legend-color {
    width: 16px;
    height: 16px;
    border-radius: 4px;
  }
}
</style>
