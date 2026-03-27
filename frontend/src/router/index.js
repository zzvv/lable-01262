import { createRouter, createWebHistory } from 'vue-router'
import store from '@/store'  // Vuex store

// 路由权限配置
const routePermissions = {
  '/room/type': 'room:type',
  '/room/list': 'room:list',
  '/room/status': 'room:status',
  '/reservation/list': 'reservation:list',
  '/reservation/create': 'reservation:create',
  '/checkin/list': 'checkin:list',
  '/checkin/create': 'checkin:create',
  '/customer/list': 'customer:list',
  '/finance/bill': 'finance:bill',
  '/finance/consumption': 'finance:consumption',
  '/finance/report': 'finance:report',
  '/system/user': 'system:user',
  '/system/role': 'system:role',
  '/system/menu': 'system:menu'
}

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', public: true }
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页' }
      },
      // 系统管理
      {
        path: 'system/user',
        name: 'SystemUser',
        component: () => import('@/views/system/user/index.vue'),
        meta: { title: '用户管理', permission: 'system:user' }
      },
      {
        path: 'system/role',
        name: 'SystemRole',
        component: () => import('@/views/system/role/index.vue'),
        meta: { title: '角色管理', permission: 'system:role' }
      },
      {
        path: 'system/menu',
        name: 'SystemMenu',
        component: () => import('@/views/system/menu/index.vue'),
        meta: { title: '菜单管理', permission: 'system:menu' }
      },
      // 房间管理
      {
        path: 'room/type',
        name: 'RoomType',
        component: () => import('@/views/room/type/index.vue'),
        meta: { title: '房型管理', permission: 'room:type' }
      },
      {
        path: 'room/list',
        name: 'RoomList',
        component: () => import('@/views/room/list/index.vue'),
        meta: { title: '房间列表', permission: 'room:list' }
      },
      {
        path: 'room/status',
        name: 'RoomStatus',
        component: () => import('@/views/room/status/index.vue'),
        meta: { title: '房态总览', permission: 'room:status' }
      },
      // 预订管理
      {
        path: 'reservation/list',
        name: 'ReservationList',
        component: () => import('@/views/reservation/list/index.vue'),
        meta: { title: '预订列表', permission: 'reservation:list' }
      },
      {
        path: 'reservation/create',
        name: 'ReservationCreate',
        component: () => import('@/views/reservation/create/index.vue'),
        meta: { title: '新建预订', permission: 'reservation:create' }
      },
      // 入住管理
      {
        path: 'checkin/list',
        name: 'CheckInList',
        component: () => import('@/views/checkin/list/index.vue'),
        meta: { title: '入住列表', permission: 'checkin:list' }
      },
      {
        path: 'checkin/create',
        name: 'CheckInCreate',
        component: () => import('@/views/checkin/create/index.vue'),
        meta: { title: '办理入住', permission: 'checkin:create' }
      },
      // 客户管理
      {
        path: 'customer/list',
        name: 'CustomerList',
        component: () => import('@/views/customer/list/index.vue'),
        meta: { title: '客户列表', permission: 'customer:list' }
      },
      // 财务管理
      {
        path: 'finance/bill',
        name: 'FinanceBill',
        component: () => import('@/views/finance/bill/index.vue'),
        meta: { title: '账单管理', permission: 'finance:bill' }
      },
      {
        path: 'finance/consumption',
        name: 'FinanceConsumption',
        component: () => import('@/views/finance/consumption/index.vue'),
        meta: { title: '消费记录', permission: 'finance:consumption' }
      },
      {
        path: 'finance/report',
        name: 'FinanceReport',
        component: () => import('@/views/finance/report/index.vue'),
        meta: { title: '统计报表', permission: 'finance:report' }
      }
    ]
  },
  // 403 无权限页面
  {
    path: '/403',
    name: 'Forbidden',
    component: () => import('@/views/error/403.vue'),
    meta: { title: '无权限', public: true }
  },
  // 404 页面
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    redirect: '/dashboard'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

/**
 * 检查是否有权限
 */
const hasPermission = (permission) => {
  const permissions = store.state.permissions || []
  const roles = store.state.roles || []
  
  // 管理员拥有所有权限
  if (permissions.includes('*:*:*') || roles.includes('admin')) {
    return true
  }
  
  // 检查是否有该权限或其父级权限
  return permissions.some(p => p.startsWith(permission))
}

// 路由守卫
router.beforeEach(async (to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 酒店管理系统` : '酒店管理系统'
  
  // 公开页面直接放行
  if (to.meta.public) {
    next()
    return
  }
  
  // 检查登录状态
  const token = store.state.token
  if (!token) {
    next('/login')
    return
  }
  
  // 如果有token但用户信息未加载（页面刷新的情况），先获取用户信息
  if (!store.getters.isUserInfoLoaded) {
    try {
      await store.dispatch('fetchUserInfo')
    } catch (error) {
      console.error('获取用户信息失败:', error)
      next('/login')
      return
    }
  }
  
  // 检查页面权限
  const permission = to.meta.permission
  if (permission && !hasPermission(permission)) {
    next('/403')
    return
  }
  
  next()
})

export default router
