<template>
  <div>
    <!-- 统计卡片 -->
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :xs="12" :sm="6">
        <div class="stat-card">
          <div class="stat-title">今日营收</div>
          <div class="stat-value">¥{{ todayRevenue }}</div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card green">
          <div class="stat-title">本月营收</div>
          <div class="stat-value">¥{{ monthRevenue }}</div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card orange">
          <div class="stat-title">今日入住</div>
          <div class="stat-value">{{ stats.todayCheckIn || 0 }}</div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card blue">
          <div class="stat-title">入住率</div>
          <div class="stat-value">{{ stats.occupancyRate || 0 }}%</div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表 -->
    <el-row :gutter="20">
      <el-col :xs="24" :md="12">
        <div class="card">
          <h3 style="margin-bottom: 16px;">营收趋势（近30天）</h3>
          <div ref="revenueChart" style="height: 350px;"></div>
        </div>
      </el-col>
      <el-col :xs="24" :md="12">
        <div class="card">
          <h3 style="margin-bottom: 16px;">收入构成</h3>
          <div ref="billTypeChart" style="height: 350px;"></div>
        </div>
      </el-col>
    </el-row>

    <!-- 历史报表 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <div class="card">
          <div class="table-toolbar">
            <h3 style="margin: 0;">历史报表</h3>
            <div class="toolbar-buttons">
              <el-button type="primary" @click="showGenerateDialog">
                <el-icon><Timer /></el-icon> 手动生成报表
              </el-button>
            </div>
          </div>
          
          <el-tabs v-model="activeTab" @tab-change="loadReportData">
            <el-tab-pane label="日报表" name="daily">
              <div class="table-toolbar" style="margin-bottom: 12px;">
                <el-form :inline="true" :model="dailyQuery">
                  <el-form-item>
                    <el-date-picker v-model="dailyQuery.dateRange" type="daterange" 
                      start-placeholder="开始日期" end-placeholder="结束日期" 
                      value-format="YYYY-MM-DD" />
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" @click="loadDailyReport">查询</el-button>
                  </el-form-item>
                </el-form>
              </div>
              <el-table :data="dailyReports" v-loading="loading" stripe>
                <el-table-column prop="reportDate" label="日期" width="120" />
                <el-table-column label="总营收" width="120">
                  <template #default="{ row }">¥{{ row.totalRevenue }}</template>
                </el-table-column>
                <el-table-column label="房费" width="100">
                  <template #default="{ row }">¥{{ row.roomRevenue }}</template>
                </el-table-column>
                <el-table-column label="消费" width="100">
                  <template #default="{ row }">¥{{ row.consumeRevenue }}</template>
                </el-table-column>
                <el-table-column prop="checkInCount" label="入住数" width="80" />
                <el-table-column prop="checkOutCount" label="退房数" width="80" />
                <el-table-column prop="reservationCount" label="预订数" width="80" />
                <el-table-column prop="newCustomerCount" label="新客户" width="80" />
                <el-table-column label="入住率" width="80">
                  <template #default="{ row }">{{ row.occupancyRate }}%</template>
                </el-table-column>
                <el-table-column prop="createTime" label="生成时间" width="160" />
              </el-table>
              <el-pagination
                v-model:current-page="dailyPagination.current"
                v-model:page-size="dailyPagination.size"
                :total="dailyPagination.total"
                :page-sizes="[10, 20, 50]"
                layout="total, sizes, prev, pager, next"
                @change="loadDailyReport"
                style="margin-top: 16px; justify-content: flex-end;"
              />
            </el-tab-pane>
            
            <el-tab-pane label="月报表" name="monthly">
              <div class="table-toolbar" style="margin-bottom: 12px;">
                <el-form :inline="true" :model="monthlyQuery">
                  <el-form-item>
                    <el-date-picker v-model="monthlyQuery.year" type="year" 
                      placeholder="选择年份" value-format="YYYY" />
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" @click="loadMonthlyReport">查询</el-button>
                  </el-form-item>
                </el-form>
              </div>
              <el-table :data="monthlyReports" v-loading="loading" stripe>
                <el-table-column label="月份" width="100">
                  <template #default="{ row }">{{ row.reportYear }}-{{ String(row.reportMonth).padStart(2, '0') }}</template>
                </el-table-column>
                <el-table-column label="总营收" width="120">
                  <template #default="{ row }">¥{{ row.totalRevenue }}</template>
                </el-table-column>
                <el-table-column label="房费" width="100">
                  <template #default="{ row }">¥{{ row.roomRevenue }}</template>
                </el-table-column>
                <el-table-column label="消费" width="100">
                  <template #default="{ row }">¥{{ row.consumeRevenue }}</template>
                </el-table-column>
                <el-table-column prop="checkInCount" label="入住总数" width="90" />
                <el-table-column prop="checkOutCount" label="退房总数" width="90" />
                <el-table-column prop="newCustomerCount" label="新客户" width="80" />
                <el-table-column label="平均入住率" width="100">
                  <template #default="{ row }">{{ row.avgOccupancyRate }}%</template>
                </el-table-column>
                <el-table-column label="日均营收" width="100">
                  <template #default="{ row }">¥{{ row.avgDailyRevenue }}</template>
                </el-table-column>
                <el-table-column prop="createTime" label="生成时间" width="160" />
              </el-table>
              <el-pagination
                v-model:current-page="monthlyPagination.current"
                v-model:page-size="monthlyPagination.size"
                :total="monthlyPagination.total"
                :page-sizes="[10, 20, 50]"
                layout="total, sizes, prev, pager, next"
                @change="loadMonthlyReport"
                style="margin-top: 16px; justify-content: flex-end;"
              />
            </el-tab-pane>
          </el-tabs>
        </div>
      </el-col>
    </el-row>

    <!-- 生成报表对话框 -->
    <el-dialog v-model="generateVisible" title="手动生成报表" width="400px">
      <el-form label-width="100px">
        <el-form-item label="报表类型">
          <el-radio-group v-model="generateForm.type">
            <el-radio label="daily">日报表</el-radio>
            <el-radio label="monthly">月报表</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="generateForm.type === 'daily'" label="日期">
          <el-date-picker v-model="generateForm.date" type="date" 
            placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%;" />
        </el-form-item>
        <el-form-item v-else label="月份">
          <el-date-picker v-model="generateForm.month" type="month" 
            placeholder="选择月份" value-format="YYYY-MM" style="width: 100%;" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="generateVisible = false">取消</el-button>
        <el-button type="primary" @click="handleGenerate" :loading="generating">生成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { getStatistics, getChartData } from '@/api/dashboard'
import { getTodayRevenue, getMonthRevenue } from '@/api/bill'
import { getDailyReportPage, getMonthlyReportPage, generateDailyReport, generateMonthlyReport } from '@/api/report'

const stats = ref({})
const todayRevenue = ref(0)
const monthRevenue = ref(0)
const loading = ref(false)
const generating = ref(false)

const revenueChart = ref()
const billTypeChart = ref()
let charts = []

const typeMap = { 0: '房费', 1: '消费', 2: '押金', 3: '退款' }

// 报表数据
const activeTab = ref('daily')
const dailyReports = ref([])
const monthlyReports = ref([])
const dailyQuery = reactive({ dateRange: null })
const monthlyQuery = reactive({ year: null })
const dailyPagination = reactive({ current: 1, size: 10, total: 0 })
const monthlyPagination = reactive({ current: 1, size: 10, total: 0 })

// 生成报表
const generateVisible = ref(false)
const generateForm = reactive({ type: 'daily', date: null, month: null })

const loadData = async () => {
  const [statsRes, chartRes, todayRes, monthRes] = await Promise.all([
    getStatistics(),
    getChartData(),
    getTodayRevenue(),
    getMonthRevenue()
  ])
  
  stats.value = statsRes.data
  todayRevenue.value = todayRes.data || 0
  monthRevenue.value = monthRes.data || 0
  
  renderCharts(chartRes.data)
}

const renderCharts = (data) => {
  if (revenueChart.value) {
    const chart = echarts.init(revenueChart.value)
    charts.push(chart)
    const revenueData = data.dailyRevenueData || []
    chart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: revenueData.map(i => i.date) },
      yAxis: { type: 'value', axisLabel: { formatter: '¥{value}' } },
      series: [{
        type: 'bar',
        data: revenueData.map(i => i.amount || 0),
        itemStyle: { color: '#409eff' }
      }]
    })
  }

  if (billTypeChart.value) {
    const chart = echarts.init(billTypeChart.value)
    charts.push(chart)
    const billData = (data.billTypeData || []).map(i => ({
      name: typeMap[i.bill_type] || '其他',
      value: i.amount || 0
    }))
    chart.setOption({
      tooltip: { trigger: 'item', formatter: '{b}: ¥{c} ({d}%)' },
      legend: { bottom: 0 },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        data: billData
      }]
    })
  }
}

const loadReportData = () => {
  if (activeTab.value === 'daily') {
    loadDailyReport()
  } else {
    loadMonthlyReport()
  }
}

const loadDailyReport = async () => {
  loading.value = true
  try {
    const params = {
      current: dailyPagination.current,
      size: dailyPagination.size
    }
    if (dailyQuery.dateRange) {
      params.startDate = dailyQuery.dateRange[0]
      params.endDate = dailyQuery.dateRange[1]
    }
    const res = await getDailyReportPage(params)
    dailyReports.value = res.data.records
    dailyPagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

const loadMonthlyReport = async () => {
  loading.value = true
  try {
    const params = {
      current: monthlyPagination.current,
      size: monthlyPagination.size
    }
    if (monthlyQuery.year) {
      params.year = parseInt(monthlyQuery.year)
    }
    const res = await getMonthlyReportPage(params)
    monthlyReports.value = res.data.records
    monthlyPagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

const showGenerateDialog = () => {
  generateForm.type = 'daily'
  generateForm.date = null
  generateForm.month = null
  generateVisible.value = true
}

const handleGenerate = async () => {
  generating.value = true
  try {
    if (generateForm.type === 'daily') {
      if (!generateForm.date) {
        ElMessage.warning('请选择日期')
        return
      }
      await generateDailyReport(generateForm.date)
      ElMessage.success('日报表生成成功')
      loadDailyReport()
    } else {
      if (!generateForm.month) {
        ElMessage.warning('请选择月份')
        return
      }
      const [year, month] = generateForm.month.split('-')
      await generateMonthlyReport(parseInt(year), parseInt(month))
      ElMessage.success('月报表生成成功')
      loadMonthlyReport()
    }
    generateVisible.value = false
  } catch (error) {
    ElMessage.error('生成失败')
  } finally {
    generating.value = false
  }
}

const handleResize = () => {
  charts.forEach(c => c.resize())
}

onMounted(() => {
  loadData()
  loadDailyReport()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  charts.forEach(c => c.dispose())
})
</script>
