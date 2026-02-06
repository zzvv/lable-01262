/**
 * Vuex Store 配置
 * @description 全局状态管理，使用Vuex 4.x
 */
import { createStore } from 'vuex'
import { login as loginApi, logout as logoutApi, getUserMenus, getUserInfo } from '@/api/auth'
import router from '@/router'

export default createStore({
  // ==================== State ====================
  state: {
    /** 用户Token */
    token: localStorage.getItem('token') || '',
    /** 用户ID */
    userId: null,
    /** 用户名 */
    username: '',
    /** 昵称 */
    nickname: '',
    /** 角色列表 */
    roles: [],
    /** 权限列表 */
    permissions: [],
    /** 菜单列表 */
    menus: [],
    /** 是否已加载用户信息 */
    userInfoLoaded: false
  },

  // ==================== Getters ====================
  getters: {
    /**
     * 是否已登录
     */
    isLoggedIn: (state) => !!state.token,
    
    /**
     * 是否是管理员
     */
    isAdmin: (state) => state.roles.includes('admin'),
    
    /**
     * 获取Token
     */
    getToken: (state) => state.token,
    
    /**
     * 获取用户信息
     */
    getUserInfo: (state) => ({
      userId: state.userId,
      username: state.username,
      nickname: state.nickname,
      roles: state.roles
    }),
    
    /**
     * 是否已加载用户信息
     */
    isUserInfoLoaded: (state) => state.userInfoLoaded
  },

  // ==================== Mutations ====================
  mutations: {
    /**
     * 设置Token
     */
    SET_TOKEN(state, token) {
      state.token = token
      localStorage.setItem('token', token)
    },
    
    /**
     * 设置用户信息
     */
    SET_USER_INFO(state, userInfo) {
      state.userId = userInfo.userId
      state.username = userInfo.username
      state.nickname = userInfo.nickname
      state.roles = Array.from(userInfo.roles || [])
      state.permissions = Array.from(userInfo.permissions || [])
      state.userInfoLoaded = true
    },
    
    /**
     * 设置菜单
     */
    SET_MENUS(state, menus) {
      state.menus = menus
    },
    
    /**
     * 重置状态
     */
    RESET_STATE(state) {
      state.token = ''
      state.userId = null
      state.username = ''
      state.nickname = ''
      state.roles = []
      state.permissions = []
      state.menus = []
      state.userInfoLoaded = false
      localStorage.removeItem('token')
    }
  },

  // ==================== Actions ====================
  actions: {
    /**
     * 用户登录
     * @param {Object} loginForm - 登录表单 { username, password }
     */
    async login({ commit, dispatch }, loginForm) {
      const res = await loginApi(loginForm)
      
      // 保存Token
      commit('SET_TOKEN', res.data.token)
      
      // 保存用户信息
      commit('SET_USER_INFO', {
        userId: res.data.userId,
        username: res.data.username,
        nickname: res.data.nickname,
        roles: res.data.roles,
        permissions: res.data.permissions
      })
      
      // 获取菜单（捕获错误，不影响登录流程）
      try {
        await dispatch('fetchMenus')
      } catch (e) {
        console.warn('获取菜单失败:', e)
      }
      
      return res
    },
    
    /**
     * 获取用户信息（用于页面刷新后恢复状态）
     */
    async fetchUserInfo({ commit, state }) {
      // 如果没有token，直接返回
      if (!state.token) {
        return Promise.reject(new Error('No token'))
      }
      
      try {
        const res = await getUserInfo()
        commit('SET_USER_INFO', {
          userId: res.data.userId,
          username: res.data.username,
          nickname: res.data.nickname,
          roles: res.data.roles,
          permissions: res.data.permissions
        })
        return res
      } catch (error) {
        // 获取用户信息失败，清除token
        commit('RESET_STATE')
        throw error
      }
    },
    
    /**
     * 获取用户菜单
     */
    async fetchMenus({ commit }) {
      const res = await getUserMenus()
      commit('SET_MENUS', res.data || [])
    },
    
    /**
     * 用户登出
     */
    async logout({ commit }) {
      try {
        await logoutApi()
      } catch (e) {
        // ignore logout api error
      }
      commit('RESET_STATE')
      router.push('/login')
    },
    
    /**
     * 重置Token（用于Token过期等场景）
     */
    resetToken({ commit }) {
      commit('RESET_STATE')
    },
    
    /**
     * 检查权限
     * @param {string} permission - 权限标识
     * @returns {boolean}
     */
    hasPermission({ state }, permission) {
      if (state.permissions.includes('*:*:*')) {
        return true
      }
      return state.permissions.includes(permission)
    }
  }
})
