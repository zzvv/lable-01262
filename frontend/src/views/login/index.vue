<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
    
    <div class="login-wrapper">
      <!-- 左侧品牌区域 -->
      <div class="brand-section">
        <div class="brand-content">
          <div class="logo-icon">
            <el-icon :size="48"><House /></el-icon>
          </div>
          <h1 class="brand-title">酒店管理系统</h1>
          <p class="brand-desc">Hotel Management System</p>
          <div class="feature-list">
            <div class="feature-item">
              <el-icon><Check /></el-icon>
              <span>智能房态管理</span>
            </div>
            <div class="feature-item">
              <el-icon><Check /></el-icon>
              <span>便捷预订入住</span>
            </div>
            <div class="feature-item">
              <el-icon><Check /></el-icon>
              <span>财务报表分析</span>
            </div>
            <div class="feature-item">
              <el-icon><Check /></el-icon>
              <span>多角色权限控制</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 右侧登录表单 -->
      <div class="login-section">
        <div class="login-box">
          <div class="login-header">
            <h2 class="login-title">欢迎登录</h2>
            <p class="login-subtitle">请输入您的账号信息</p>
          </div>
          
          <el-form ref="formRef" :model="form" :rules="rules" class="login-form">
            <el-form-item prop="username">
              <el-input 
                v-model="form.username" 
                placeholder="请输入用户名" 
                prefix-icon="User" 
                size="large"
                clearable
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input 
                v-model="form.password" 
                type="password" 
                placeholder="请输入密码" 
                prefix-icon="Lock" 
                size="large" 
                show-password 
                @keyup.enter="handleLogin" 
              />
            </el-form-item>
            
            <el-form-item>
              <el-button 
                type="primary" 
                size="large" 
                :loading="loading" 
                @click="handleLogin" 
                class="login-btn"
              >
                <span v-if="!loading">登 录</span>
                <span v-else>登录中...</span>
              </el-button>
            </el-form-item>
          </el-form>
          
          <div class="demo-accounts">
            <div class="demo-title">
              <el-icon><InfoFilled /></el-icon>
              <span>演示账号</span>
            </div>
            <div class="account-list">
              <div class="account-item" @click="fillAccount('admin', 'admin123')">
                <span class="role">管理员</span>
                <span class="info">admin / admin123</span>
              </div>
              <div class="account-item" @click="fillAccount('reception', 'admin123')">
                <span class="role">前台</span>
                <span class="info">reception / admin123</span>
              </div>
              <div class="account-item" @click="fillAccount('user', 'admin123')">
                <span class="role">用户</span>
                <span class="info">user / admin123</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 底部版权 -->
    <div class="footer">
      <span>© 2026 酒店管理系统 · 基于 Vue3 + SpringBoot</span>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'

const router = useRouter()
const store = useStore()

const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: 'admin',
  password: 'admin123'
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const fillAccount = (username, password) => {
  form.username = username
  form.password = password
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  try {
    await store.dispatch('login', form)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #304156 0%, #1a252f 100%);
  position: relative;
  overflow: hidden;
}

// 背景装饰圆圈
.bg-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  pointer-events: none;
  
  .circle {
    position: absolute;
    border-radius: 50%;
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  }
  
  .circle-1 {
    width: 400px;
    height: 400px;
    top: -100px;
    right: -100px;
  }
  
  .circle-2 {
    width: 300px;
    height: 300px;
    bottom: -50px;
    left: -50px;
  }
  
  .circle-3 {
    width: 200px;
    height: 200px;
    top: 50%;
    left: 10%;
    background: linear-gradient(135deg, rgba(17, 153, 142, 0.1) 0%, rgba(56, 239, 125, 0.1) 100%);
  }
}

.login-wrapper {
  display: flex;
  width: 900px;
  min-height: 520px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  overflow: hidden;
  z-index: 1;
}

// 左侧品牌区域
.brand-section {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  
  .brand-content {
    color: #fff;
    text-align: center;
  }
  
  .logo-icon {
    width: 80px;
    height: 80px;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 20px;
  }
  
  .brand-title {
    font-size: 28px;
    font-weight: bold;
    margin-bottom: 8px;
  }
  
  .brand-desc {
    font-size: 14px;
    opacity: 0.8;
    margin-bottom: 40px;
  }
  
  .feature-list {
    text-align: left;
    
    .feature-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 10px 0;
      font-size: 14px;
      
      .el-icon {
        width: 24px;
        height: 24px;
        background: rgba(255, 255, 255, 0.2);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 12px;
      }
    }
  }
}

// 右侧登录区域
.login-section {
  flex: 1;
  padding: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-box {
  width: 100%;
  max-width: 320px;
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
  
  .login-title {
    font-size: 24px;
    color: #303133;
    margin-bottom: 8px;
  }
  
  .login-subtitle {
    font-size: 14px;
    color: #909399;
  }
}

.login-form {
  .el-form-item {
    margin-bottom: 24px;
  }
  
  .el-input {
    :deep(.el-input__wrapper) {
      border-radius: 8px;
      box-shadow: 0 0 0 1px #dcdfe6 inset;
      
      &:hover {
        box-shadow: 0 0 0 1px #c0c4cc inset;
      }
      
      &.is-focus {
        box-shadow: 0 0 0 1px #667eea inset;
      }
    }
  }
}

.login-btn {
  width: 100%;
  height: 44px;
  border-radius: 8px;
  font-size: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  
  &:hover {
    background: linear-gradient(135deg, #5a6fd6 0%, #6a4190 100%);
  }
}

// 演示账号
.demo-accounts {
  margin-top: 24px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  
  .demo-title {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 12px;
    color: #909399;
    margin-bottom: 12px;
  }
  
  .account-list {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }
  
  .account-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px 12px;
    background: #fff;
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.2s;
    
    &:hover {
      background: #ecf5ff;
      transform: translateX(4px);
    }
    
    .role {
      font-size: 12px;
      color: #667eea;
      font-weight: 500;
    }
    
    .info {
      font-size: 12px;
      color: #909399;
    }
  }
}

// 底部版权
.footer {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
}

// 响应式适配
@media screen and (max-width: 768px) {
  .login-wrapper {
    flex-direction: column;
    width: 90%;
    max-width: 400px;
    min-height: auto;
  }
  
  .brand-section {
    padding: 30px 20px;
    
    .logo-icon {
      width: 60px;
      height: 60px;
      border-radius: 16px;
      
      .el-icon {
        font-size: 32px;
      }
    }
    
    .brand-title {
      font-size: 22px;
    }
    
    .brand-desc {
      margin-bottom: 20px;
    }
    
    .feature-list {
      display: none;
    }
  }
  
  .login-section {
    padding: 30px 20px;
  }
  
  .login-header {
    margin-bottom: 24px;
    
    .login-title {
      font-size: 20px;
    }
  }
  
  .footer {
    position: relative;
    bottom: auto;
    left: auto;
    transform: none;
    text-align: center;
    padding: 20px;
  }
}
</style>
