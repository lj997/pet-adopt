<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="login-header">
          <el-icon class="logo-icon"><Pets /></el-icon>
          <span>登录</span>
        </div>
      </template>
      <el-form ref="loginFormRef" :model="loginForm" :rules="rules" label-width="0">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" @click="handleLogin" style="width: 100%">
            登录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="login-footer">
        <span>还没有账号？</span>
        <router-link to="/register" class="register-link">立即注册</router-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  const valid = await loginFormRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await userStore.login(loginForm)
    ElMessage.success('登录成功')
    const redirect = route.query.redirect || '/'
    router.push(redirect)
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 140px);
  padding: 40px 20px;
}

.login-card {
  width: 100%;
  max-width: 400px;

  .login-header {
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 20px;
    font-weight: bold;
    color: #303133;

    .logo-icon {
      font-size: 28px;
      margin-right: 10px;
      color: #409EFF;
    }
  }

  .login-footer {
    text-align: center;
    margin-top: 10px;
    color: #909399;
    font-size: 14px;

    .register-link {
      color: #409EFF;
      margin-left: 5px;
      text-decoration: none;

      &:hover {
        text-decoration: underline;
      }
    }
  }
}
</style>
