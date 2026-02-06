import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

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
        meta: { title: '用户管理' }
      },
      {
        path: 'system/role',
        name: 'SystemRole',
        component: () => import('@/views/system/role/index.vue'),
        meta: { title: '角色管理' }
      },
      {
        path: 'system/menu',
        name: 'SystemMenu',
        component: () => import('@/views/system/menu/index.vue'),
        meta: { title: '菜单管理' }
      },
      // 房间管理
      {
        path: 'room/type',
        name: 'RoomType',
        component: () => import('@/views/room/type/index.vue'),
        meta: { title: '房型管理' }
      },
      {
        path: 'room/list',
        name: 'RoomList',
        component: () => import('@/views/room/list/index.vue'),
        meta: { title: '房间列表' }
      },
      {
        path: 'room/status',
        name: 'RoomStatus',
        component: () => import('@/views/room/status/index.vue'),
        meta: { title: '房态总览' }
      },
      // 预订管理
      {
        path: 'reservation/list',
        name: 'ReservationList',
        component: () => import('@/views/reservation/list/index.vue'),
        meta: { title: '预订列表' }
      },
      {
        path: 'reservation/create',
        name: 'ReservationCreate',
        component: () => import('@/views/reservation/create/index.vue'),
        meta: { title: '新建预订' }
      },
      // 入住管理
      {
        path: 'checkin/list',
        name: 'CheckInList',
        component: () => import('@/views/checkin/list/index.vue'),
        meta: { title: '入住列表' }
      },
      {
        path: 'checkin/create',
        name: 'CheckInCreate',
        component: () => import('@/views/checkin/create/index.vue'),
        meta: { title: '办理入住' }
      },
      // 客户管理
      {
        path: 'customer/list',
        name: 'CustomerList',
        component: () => import('@/views/customer/list/index.vue'),
        meta: { title: '客户列表' }
      },
      // 财务管理
      {
        path: 'finance/bill',
        name: 'FinanceBill',
        component: () => import('@/views/finance/bill/index.vue'),
        meta: { title: '账单管理' }
      },
      {
        path: 'finance/report',
        name: 'FinanceReport',
        component: () => import('@/views/finance/report/index.vue'),
        meta: { title: '统计报表' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 酒店管理系统` : '酒店管理系统'
  
  const userStore = useUserStore()
  
  if (to.meta.public) {
    next()
  } else if (!userStore.isLoggedIn) {
    next('/login')
  } else {
    next()
  }
})

export default router
