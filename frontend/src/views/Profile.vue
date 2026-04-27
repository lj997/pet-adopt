<template>
  <div class="page-container" style="max-width: 600px;">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>个人中心</span>
        </div>
      </template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名">
          <el-input :value="userStore.user.username" disabled />
        </el-form-item>
        <el-form-item label="角色">
          <el-tag :type="userStore.user.role === 'RESCUER' ? 'warning' : 'success'">
            {{ userStore.user.role === 'RESCUER' ? '救助人' : '领养人' }}
          </el-tag>
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model="form.city" placeholder="请输入所在城市" />
        </el-form-item>
        <el-form-item label="个人简介" prop="introduction">
          <el-input
            v-model="form.introduction"
            type="textarea"
            :rows="3"
            placeholder="请输入个人简介"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleUpdate">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()

const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  nickname: '',
  phone: '',
  email: '',
  city: '',
  introduction: ''
})

const rules = {}

const loadUserInfo = () => {
  const user = userStore.user
  form.nickname = user.nickname || ''
  form.phone = user.phone || ''
  form.email = user.email || ''
  form.city = user.city || ''
  form.introduction = user.introduction || ''
}

const handleUpdate = async () => {
  loading.value = true
  try {
    await userStore.updateProfile(form)
    ElMessage.success('修改成功')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style lang="scss" scoped>
.card-header {
  font-size: 18px;
  font-weight: bold;
}
</style>
