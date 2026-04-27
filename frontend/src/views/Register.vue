<template>
  <div class="register-container">
    <el-card class="register-card">
      <template #header>
        <div class="register-header">
          <el-icon class="logo-icon"><Pets /></el-icon>
          <span>注册</span>
        </div>
      </template>
      <el-form ref="registerFormRef" :model="registerForm" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="registerForm.nickname" placeholder="请输入昵称" prefix-icon="UserFilled" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-radio-group v-model="registerForm.role">
            <el-radio value="RESCUER">
              <el-icon><EditPen /></el-icon>
              救助人
            </el-radio>
            <el-radio value="ADOPTER">
              <el-icon><Star /></el-icon>
              领养人
            </el-radio>
          </el-radio-group>
          <div class="role-desc">
            <el-text v-if="registerForm.role === 'RESCUER'" type="info">
              救助人：可以发布待领养宠物信息，审核领养申请
            </el-text>
            <el-text v-else-if="registerForm.role === 'ADOPTER'" type="info">
              领养人：可以浏览宠物信息，提交领养申请，收藏宠物
            </el-text>
          </div>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="registerForm.phone" placeholder="请输入手机号（选填）" prefix-icon="Phone" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="registerForm.email" placeholder="请输入邮箱（选填）" prefix-icon="Message" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleRegister" style="width: 100%">
            注册
          </el-button>
        </el-form-item>
      </el-form>
      <div class="register-footer">
        <span>已有账号？</span>
        <router-link to="/login" class="login-link">立即登录</router-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const registerFormRef = ref(null)
const loading = ref(false)

const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  role: '',
  phone: '',
  email: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validatePass, trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

const handleRegister = async () => {
  const valid = await registerFormRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const { confirmPassword, ...registerData } = registerForm
    await userStore.register(registerData)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 140px);
  padding: 40px 20px;
}

.register-card {
  width: 100%;
  max-width: 500px;

  .register-header {
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

  .role-desc {
    margin-top: 8px;
    margin-left: 80px;
    font-size: 13px;
  }

  .register-footer {
    text-align: center;
    margin-top: 10px;
    color: #909399;
    font-size: 14px;

    .login-link {
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
