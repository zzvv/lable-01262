import { defineStore } from 'pinia'
import { login as loginApi, logout as logoutApi, getUserMenus } from '@/api/auth'
import router from '@/router'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userId: null,
    username: '',
    nickname: '',
    roles: [],
    permissions: [],
    menus: []
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: (state) => state.roles.includes('admin')
  },

  actions: {
    async login(loginForm) {
      const res = await loginApi(loginForm)
      this.token = res.data.token
      this.userId = res.data.userId
      this.username = res.data.username
      this.nickname = res.data.nickname
      this.roles = Array.from(res.data.roles || [])
      this.permissions = Array.from(res.data.permissions || [])
      
      localStorage.setItem('token', this.token)
      
      // 获取菜单（捕获错误，不影响登录流程）
      try {
        await this.fetchMenus()
      } catch (e) {
        console.warn('获取菜单失败:', e)
      }
      
      return res
    },

    async fetchMenus() {
      const res = await getUserMenus()
      this.menus = res.data || []
    },

    async logout() {
      try {
        await logoutApi()
      } catch (e) {
        // ignore
      }
      this.resetState()
      router.push('/login')
    },

    resetState() {
      this.token = ''
      this.userId = null
      this.username = ''
      this.nickname = ''
      this.roles = []
      this.permissions = []
      this.menus = []
      localStorage.removeItem('token')
    },

    hasPermission(permission) {
      if (this.permissions.includes('*:*:*')) {
        return true
      }
      return this.permissions.includes(permission)
    }
  }
})
