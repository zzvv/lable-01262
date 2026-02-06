<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="16">
      <el-col :xs="12" :sm="12" :md="6">
        <div class="stat-card">
          <div class="stat-title">总房间数</div>
          <div class="stat-value">{{ stats.totalRooms || 0 }}</div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6">
        <div class="stat-card green">
          <div class="stat-title">空闲房间</div>
          <div class="stat-value">{{ stats.freeRooms || 0 }}</div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6">
        <div class="stat-card orange">
          <div class="stat-title">入住率</div>
          <div class="stat-value">{{ stats.occupancyRate || 0 }}%</div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6">
        <div class="stat-card blue">
          <div class="stat-title">今日营收</div>
          <div class="stat-value">¥{{ stats.todayRevenue || 0 }}</div>
        </div>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <div class="card" style="margin-top: 16px;">
      <h3 style="margin-bottom: 16px;">快捷操作</h3>
      <el-row :gutter="12">
        <el-col :xs="12" :sm="12" :md="6">
          <el-button type="primary" @click="$router.push('/reservation/create')" style="width: 100%; margin-bottom: 8px;">
            <el-icon><Plus /></el-icon> 新建预订
          </el-button>
        </el-col>
        <el-col :xs="12" :sm="12" :md="6">
          <el-button type="success" @click="$router.push('/checkin/create')" style="width: 100%; margin-bottom: 8px;">
            <el-icon><Key /></el-icon> 办理入住
          </el-button>
        </el-col>
        <el-col :xs="12" :sm="12" :md="6">
          <el-button type="warning" @click="$router.push('/room/status')" style="width: 100%; margin-bottom: 8px;">
            <el-icon><Monitor /></el-icon> 房态总览
          </el-button>
        </el-col>
        <el-col :xs="12" :sm="12" :md="6">
          <el-button type="info" @click="$router.push('/finance/report')" style="width: 100%; margin-bottom: 8px;">
            <el-icon><DataAnalysis /></el-icon> 统计报表
          </el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 图表 -->
    <el-row :gutter="16" style="margin-top: 16px;">
      <el-col :xs="24" :sm="24" :md="12">
        <div class="card">
          <h3 style="margin-bottom: 16px;">房间状态分布</h3>
          <div ref="roomStatusChart" style="height: 280px;"></div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12">
        <div class="card chart-card">
          <h3 style="margin-bottom: 16px;">近30天入住趋势</h3>
          <div ref="checkInChart" style="height: 280px;"></div>
        </div>
      </el-col>
    </el-row>

    <!-- 今日概览 -->
    <el-row :gutter="16" style="margin-top: 16px;">
      <el-col :xs="24" :sm="8" :md="8">
        <div class="card">
          <h3 style="margin-bottom: 12px;">今日预计入住</h3>
          <div class="overview-value">{{ stats.todayCheckIn || 0 }} 间</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="8" :md="8">
        <div class="card">
          <h3 style="margin-bottom: 12px;">今日预计离店</h3>
          <div class="overview-value">{{ stats.todayCheckOut || 0 }} 间</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="8" :md="8">
        <div class="card">
          <h3 style="margin-bottom: 12px;">当前在住</h3>
          <div class="overview-value">{{ stats.currentGuests || 0 }} 人</div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { getStatistics, getChartData } from '@/api/dashboard'

const stats = ref({})
const chartData = ref({})

const roomStatusChart = ref()
const checkInChart = ref()

let roomStatusChartInstance = null
let checkInChartInstance = null

const statusMap = {
  0: '空闲',
  1: '已预订',
  2: '入住中',
  3: '清洁中',
  4: '维修中'
}

const loadData = async () => {
  try {
    const [statsRes, chartRes] = await Promise.all([
      getStatistics(),
      getChartData()
    ])
    stats.value = statsRes.data
    chartData.value = chartRes.data
    
    renderCharts()
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

const renderCharts = () => {
  // 房间状态分布图
  if (roomStatusChart.value) {
    roomStatusChartInstance = echarts.init(roomStatusChart.value)
    const roomStatusData = (chartData.value.roomStatusData || []).map(item => ({
      name: statusMap[item.status] || '未知',
      value: item.count
    }))
    
    roomStatusChartInstance.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: 0 },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        data: roomStatusData,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }]
    })
  }

  // 入住趋势图
  if (checkInChart.value) {
    checkInChartInstance = echarts.init(checkInChart.value)
    const dailyData = chartData.value.dailyCheckInData || []
    
    checkInChartInstance.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: {
        type: 'category',
        data: dailyData.map(item => item.date)
      },
      yAxis: { type: 'value' },
      series: [{
        type: 'line',
        data: dailyData.map(item => item.count),
        smooth: true,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.5)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
          ])
        }
      }]
    })
  }
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  roomStatusChartInstance?.dispose()
  checkInChartInstance?.dispose()
})

const handleResize = () => {
  roomStatusChartInstance?.resize()
  checkInChartInstance?.resize()
}
</script>

<style lang="scss" scoped>
.overview-value {
  font-size: 32px;
  font-weight: bold;
  color: #409EFF;
  text-align: center;
  padding: 20px 0;
}

.chart-card {
  margin-top: 0;
}

@media screen and (max-width: 768px) {
  .overview-value {
    font-size: 24px;
    padding: 12px 0;
  }
  
  .chart-card {
    margin-top: 16px;
  }
  
  .stat-card {
    margin-bottom: 12px;
  }
}
</style>
