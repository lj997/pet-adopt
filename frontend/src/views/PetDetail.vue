<template>
  <div class="detail-container">
    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading"><Loading /></el-icon>
    </div>

    <template v-else>
      <div class="detail-header">
        <div class="detail-images">
          <img
            v-if="currentImage"
            :src="currentImage"
            :alt="pet.name"
            class="main-image"
          />
          <div v-else class="main-image-placeholder">
            <el-icon><Picture /></el-icon>
          </div>
          <div v-if="pet.photos && pet.photos.length > 1" class="thumbnails">
            <img
              v-for="(photo, index) in pet.photos"
              :key="index"
              :src="photo"
              :class="['thumbnail', { active: currentImage === photo }]"
              @click="currentImage = photo"
            />
          </div>
        </div>
        <div class="detail-info">
          <div class="pet-title">
            <h1>{{ pet.name }}</h1>
            <el-tag :type="getStatusType(pet.status)" size="large">
              {{ getStatusLabel(pet.status) }}
            </el-tag>
          </div>

          <div class="info-item">
            <span class="label">种类：</span>
            <span class="value">
              <el-tag type="primary">{{ getTypeLabel(pet.type) }}</el-tag>
            </span>
          </div>

          <div v-if="pet.breed" class="info-item">
            <span class="label">品种：</span>
            <span class="value">{{ pet.breed }}</span>
          </div>

          <div v-if="pet.age !== null" class="info-item">
            <span class="label">年龄：</span>
            <span class="value">{{ pet.age }}岁</span>
          </div>

          <div v-if="pet.gender" class="info-item">
            <span class="label">性别：</span>
            <span class="value">{{ getGenderLabel(pet.gender) }}</span>
          </div>

          <div class="info-item">
            <span class="label">绝育：</span>
            <span class="value">
              <el-tag :type="pet.sterilized ? 'success' : 'info'">
                {{ pet.sterilized ? '已绝育' : '未绝育' }}
              </el-tag>
            </span>
          </div>

          <div class="info-item">
            <span class="label">疫苗：</span>
            <span class="value">
              <el-tag :type="pet.vaccinated ? 'success' : 'info'">
                {{ pet.vaccinated ? '已接种' : '未接种' }}
              </el-tag>
            </span>
          </div>

          <div class="info-item">
            <span class="label">所在城市：</span>
            <span class="value">
              <el-icon><Location /></el-icon>
              {{ pet.city }}
            </span>
          </div>

          <div v-if="pet.rescuerNickname" class="info-item">
            <span class="label">发布人：</span>
            <span class="value">
              <el-icon><User /></el-icon>
              {{ pet.rescuerNickname }}
            </span>
          </div>

          <div v-if="pet.rescuerPhone" class="info-item">
            <span class="label">联系方式：</span>
            <span class="value">
              <el-icon><Phone /></el-icon>
              {{ pet.rescuerPhone }}
            </span>
          </div>

          <div class="action-buttons">
            <template v-if="userStore.isLoggedIn && userStore.user.role === 'ADOPTER'">
              <el-button type="primary" size="large" :disabled="pet.status !== 'AVAILABLE'" @click="showAdoptDialog">
                <el-icon><Edit /></el-icon>
                申请领养
              </el-button>
              <el-button :type="isFavorite ? 'warning' : 'default'" size="large" @click="toggleFavorite">
                <el-icon><Star /></el-icon>
                {{ isFavorite ? '已收藏' : '收藏' }}
              </el-button>
            </template>
            <template v-else-if="!userStore.isLoggedIn">
              <el-button type="primary" size="large" @click="goLogin">
                <el-icon><User /></el-icon>
                登录后领养
              </el-button>
            </template>
          </div>
        </div>
      </div>

      <div v-if="pet.healthCondition" class="detail-section">
        <div class="section-title">健康状况</div>
        <div class="section-content">{{ pet.healthCondition }}</div>
      </div>

      <div v-if="pet.personality" class="detail-section">
        <div class="section-title">性格描述</div>
        <div class="section-content">{{ pet.personality }}</div>
      </div>
    </template>

    <el-dialog
      v-model="adoptDialogVisible"
      title="领养申请"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form ref="adoptFormRef" :model="adoptForm" :rules="adoptRules" label-width="100px">
        <el-form-item label="个人介绍" prop="personalIntro">
          <el-input
            v-model="adoptForm.personalIntro"
            type="textarea"
            :rows="3"
            placeholder="请简单介绍一下您自己"
          />
        </el-form-item>
        <el-form-item label="养宠经验" prop="petExperience">
          <el-input
            v-model="adoptForm.petExperience"
            type="textarea"
            :rows="3"
            placeholder="请描述您的养宠经验（如果有）"
          />
        </el-form-item>
        <el-form-item label="住房情况" prop="housingCondition">
          <el-input
            v-model="adoptForm.housingCondition"
            type="textarea"
            :rows="2"
            placeholder="请描述您的住房情况，如是否自有住房、是否有阳台等"
          />
        </el-form-item>
        <el-form-item label="联系方式" prop="contactInfo">
          <el-input
            v-model="adoptForm.contactInfo"
            placeholder="请输入您的手机号或其他联系方式"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="adoptDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitAdoptRequest">提交申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { petApi, adoptionApi, favoriteApi } from '@/api'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const petId = computed(() => route.params.id)
const loading = ref(true)
const pet = ref({})
const currentImage = ref('')
const isFavorite = ref(false)

const adoptDialogVisible = ref(false)
const adoptFormRef = ref(null)
const submitLoading = ref(false)
const adoptForm = ref({
  personalIntro: '',
  petExperience: '',
  housingCondition: '',
  contactInfo: ''
})

const adoptRules = {
  contactInfo: [{ required: true, message: '请输入联系方式', trigger: 'blur' }]
}

const getTypeLabel = (type) => {
  const labels = { CAT: '猫', DOG: '狗', OTHER: '其他' }
  return labels[type] || '未知'
}

const getGenderLabel = (gender) => {
  const labels = { MALE: '公', FEMALE: '母' }
  return labels[gender] || '未知'
}

const getStatusLabel = (status) => {
  const labels = { AVAILABLE: '待领养', ADOPTED: '已领养', UNAVAILABLE: '不可领养' }
  return labels[status] || '未知'
}

const getStatusType = (status) => {
  const types = { AVAILABLE: 'success', ADOPTED: 'info', UNAVAILABLE: 'danger' }
  return types[status] || ''
}

const fetchPet = async () => {
  loading.value = true
  try {
    const res = await petApi.getById(petId.value)
    pet.value = res.data
    if (res.data.photos && res.data.photos.length > 0) {
      currentImage.value = res.data.photos[0]
    }
  } catch (error) {
    ElMessage.error('获取宠物详情失败')
  } finally {
    loading.value = false
  }
}

const checkFavorite = async () => {
  if (!userStore.isLoggedIn || userStore.user.role !== 'ADOPTER') return
  try {
    const res = await favoriteApi.check(petId.value)
    isFavorite.value = res.data.isFavorite
  } catch (error) {
    console.error('检查收藏状态失败', error)
  }
}

const toggleFavorite = async () => {
  try {
    await favoriteApi.toggle(petId.value)
    isFavorite.value = !isFavorite.value
    ElMessage.success(isFavorite.value ? '收藏成功' : '已取消收藏')
  } catch (error) {
    console.error(error)
  }
}

const showAdoptDialog = () => {
  adoptDialogVisible.value = true
  adoptForm.value = {
    personalIntro: '',
    petExperience: '',
    housingCondition: '',
    contactInfo: ''
  }
}

const submitAdoptRequest = async () => {
  const valid = await adoptFormRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    const data = {
      petId: petId.value,
      ...adoptForm.value
    }
    await adoptionApi.create(data)
    ElMessage.success('申请提交成功！')
    adoptDialogVisible.value = false
  } catch (error) {
    console.error(error)
  } finally {
    submitLoading.value = false
  }
}

const goLogin = () => {
  router.push({ path: '/login', query: { redirect: route.fullPath } })
}

onMounted(() => {
  fetchPet()
  checkFavorite()
})
</script>

<style lang="scss" scoped>
.loading-container {
  text-align: center;
  padding: 60px 20px;

  .el-icon {
    font-size: 40px;
    color: #409EFF;
  }
}

.main-image-placeholder {
  width: 400px;
  height: 400px;
  background-color: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  margin-bottom: 12px;

  .el-icon {
    font-size: 80px;
    color: #c0c4cc;
  }
}
</style>
