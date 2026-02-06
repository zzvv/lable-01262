<template>
  <div class="layout">
    <!-- 移动端遮罩 -->
    <div class="sidebar-mask" v-if="isMobile && !isCollapsed" @click="isCollapsed = true"></div>
    
    <div class="sidebar" :class="{ collapsed: isCollapsed, 'mobile-open': isMobile && !isCollapsed }">
      <div class="logo">
        <el-icon size="24"><House /></el-icon>
        <span v-show="!isCollapsed">酒店管理系统</span>
      </div>
      <div class="menu-wrapper">
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapsed && !isMobile"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
          router
          @select="handleMenuSelect"
        >
          <!-- 首页 - 所有人可见 -->
          <el-menu-item index="/dashboard">
            <el-icon><House /></el-icon>
            <span>首页</span>
          </el-menu-item>
          
          <!-- 房间管理 -->
          <el-sub-menu index="room" v-if="hasPermission('room')">
            <template #title>
              <el-icon><OfficeBuilding /></el-icon>
              <span>房间管理</span>
            </template>
            <el-menu-item index="/room/type" v-if="hasPermission('room:type')">房型管理</el-menu-item>
            <el-menu-item index="/room/list" v-if="hasPermission('room:list')">房间列表</el-menu-item>
            <el-menu-item index="/room/status" v-if="hasPermission('room:status')">房态总览</el-menu-item>
          </el-sub-menu>
          
          <!-- 预订管理 -->
          <el-sub-menu index="reservation" v-if="hasPermission('reservation')">
            <template #title>
              <el-icon><Calendar /></el-icon>
              <span>预订管理</span>
            </template>
            <el-menu-item index="/reservation/list" v-if="hasPermission('reservation:list')">预订列表</el-menu-item>
            <el-menu-item index="/reservation/create" v-if="hasPermission('reservation:create')">新建预订</el-menu-item>
          </el-sub-menu>
          
          <!-- 入住管理 -->
          <el-sub-menu index="checkin" v-if="hasPermission('checkin')">
            <template #title>
              <el-icon><Key /></el-icon>
              <span>入住管理</span>
            </template>
            <el-menu-item index="/checkin/list" v-if="hasPermission('checkin:list')">入住列表</el-menu-item>
            <el-menu-item index="/checkin/create" v-if="hasPermission('checkin:create')">办理入住</el-menu-item>
          </el-sub-menu>
          
          <!-- 客户管理 -->
          <el-menu-item index="/customer/list" v-if="hasPermission('customer')">
            <el-icon><User /></el-icon>
            <span>客户管理</span>
          </el-menu-item>
          
          <!-- 财务管理 -->
          <el-sub-menu index="finance" v-if="hasPermission('finance')">
            <template #title>
              <el-icon><Money /></el-icon>
              <span>财务管理</span>
            </template>
            <el-menu-item index="/finance/bill" v-if="hasPermission('finance:bill')">账单管理</el-menu-item>
            <el-menu-item index="/finance/report" v-if="hasPermission('finance:report')">统计报表</el-menu-item>
          </el-sub-menu>
          
          <!-- 系统管理 - 仅管理员可见 -->
          <el-sub-menu index="system" v-if="hasPermission('system')">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统管理</span>
            </template>
            <el-menu-item index="/system/user" v-if="hasPermission('system:user')">用户管理</el-menu-item>
            <el-menu-item index="/system/role" v-if="hasPermission('system:role')">角色管理</el-menu-item>
            <el-menu-item index="/system/menu" v-if="hasPermission('system:menu')">菜单管理</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </div>
    </div>
    
    <div class="main-container">
      <div class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="toggleCollapse">
            <Fold v-if="!isCollapsed" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="$route.meta.title">{{ $route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" icon="User" />
              <span class="username">{{ nickname || username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
      
      <div class="content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { useStore } from 'vuex'

const route = useRoute()
const store = useStore()

const isCollapsed = ref(false)
const isMobile = ref(false)

// 从Vuex获取状态
const nickname = computed(() => store.state.nickname)
const username = computed(() => store.state.username)
const permissions = computed(() => store.state.permissions || [])
const roles = computed(() => store.state.roles || [])

const activeMenu = computed(() => route.path)

/**
 * 检查是否有权限
 * @param {string} perm - 权限标识，如 'system' 或 'system:user'
 */
const hasPermission = (perm) => {
  // 管理员拥有所有权限
  if (permissions.value.includes('*:*:*') || roles.value.includes('admin')) {
    return true
  }
  
  // 检查是否有该模块的任意权限
  // 例如：检查 'room' 时，如果有 'room:type:list' 也算有权限
  return permissions.value.some(p => p.startsWith(perm))
}

const checkMobile = () => {
  isMobile.value = window.innerWidth < 768
  if (isMobile.value) {
    isCollapsed.value = true
  }
}

const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
}

const handleMenuSelect = () => {
  // 移动端选择菜单后自动收起
  if (isMobile.value) {
    isCollapsed.value = true
  }
}

const handleCommand = (command) => {
  if (command === 'logout') {
    store.dispatch('logout')
  }
}

onMounted(() => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
})

onUnmounted(() => {
  window.removeEventListener('resize', checkMobile)
})
</script>

<style lang="scss" scoped>
.sidebar-mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 998;
}

.logo {
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  background-color: #263445;
  flex-shrink: 0;
  
  .el-icon {
    margin-right: 8px;
  }
}

.menu-wrapper {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  
  &::-webkit-scrollbar {
    width: 4px;
  }
  
  &::-webkit-scrollbar-thumb {
    background-color: rgba(255, 255, 255, 0.2);
    border-radius: 2px;
  }
  
  &::-webkit-scrollbar-track {
    background-color: transparent;
  }
}

.el-menu {
  border-right: none;
}

.header-left {
  display: flex;
  align-items: center;
  
  .collapse-btn {
    font-size: 20px;
    cursor: pointer;
    margin-right: 16px;
  }
}

.header-right {
  .user-info {
    display: flex;
    align-items: center;
    cursor: pointer;
    
    .username {
      margin-left: 8px;
    }
  }
}

// 响应式
@media screen and (max-width: 768px) {
  .sidebar {
    position: fixed;
    left: -220px;
    top: 0;
    z-index: 999;
    transition: left 0.3s;
    
    &.mobile-open {
      left: 0;
    }
  }
  
  .breadcrumb {
    display: none;
  }
  
  .username {
    display: none;
  }
}
</style>
