<template>
  <div class="page-container">
    <div class="search-bar">
      <div class="search-item">
        <span>种类：</span>
        <el-select v-model="searchForm.type" placeholder="全部" clearable style="width: 120px">
          <el-option label="猫" value="CAT" />
          <el-option label="狗" value="DOG" />
          <el-option label="其他" value="OTHER" />
        </el-select>
      </div>
      <div class="search-item">
        <span>城市：</span>
        <el-input v-model="searchForm.city" placeholder="输入城市" clearable style="width: 150px" />
      </div>
      <div class="search-item">
        <span>年龄：</span>
        <el-input-number v-model="searchForm.minAge" :min="0" :max="30" placeholder="最小" style="width: 100px" />
        <span>至</span>
        <el-input-number v-model="searchForm.maxAge" :min="0" :max="30" placeholder="最大" style="width: 100px" />
      </div>
      <div class="search-item">
        <span>关键词：</span>
        <el-input v-model="searchForm.keyword" placeholder="名称/品种" clearable style="width: 150px" />
      </div>
      <div class="search-actions">
        <el-button type="primary" @click="searchPets">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>
    </div>

    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading"><Loading /></el-icon>
    </div>

    <div v-else-if="pets.length === 0" class="empty-state">
      <el-icon class="empty-icon"><Search /></el-icon>
      <p class="empty-text">暂无待领养宠物</p>
    </div>

    <div v-else class="card-grid">
      <el-card
        v-for="pet in pets"
        :key="pet.id"
        class="pet-card"
        shadow="hover"
        @click="goToDetail(pet.id)"
      >
        <img
          v-if="pet.thumbnail"
          :src="pet.thumbnail"
          :alt="pet.name"
          class="pet-image"
        />
        <div v-else class="pet-image-placeholder">
          <el-icon><Picture /></el-icon>
        </div>
        <div class="pet-info">
          <div class="pet-name">{{ pet.name }}</div>
          <div class="pet-tags">
            <el-tag size="small" type="primary">{{ getTypeLabel(pet.type) }}</el-tag>
            <el-tag v-if="pet.age !== null" size="small">{{ pet.age }}岁</el-tag>
            <el-tag v-if="pet.gender" size="small" type="info">
              {{ getGenderLabel(pet.gender) }}
            </el-tag>
            <el-tag :type="getStatusType(pet.status)" size="small">
              {{ getStatusLabel(pet.status) }}
            </el-tag>
          </div>
          <div class="pet-location">
            <el-icon><Location /></el-icon>
            <span>{{ pet.city }}</span>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { petApi } from '@/api'
import { ElMessage } from 'element-plus'

const router = useRouter()

const loading = ref(true)
const pets = ref([])

const searchForm = ref({
  type: null,
  city: '',
  minAge: null,
  maxAge: null,
  keyword: ''
})

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

const fetchPets = async () => {
  loading.value = true
  try {
    const hasFilter = searchForm.value.type || searchForm.value.city || 
                       searchForm.value.minAge !== null || searchForm.value.maxAge !== null ||
                       searchForm.value.keyword

    if (hasFilter) {
      const res = await petApi.search(searchForm.value)
      pets.value = res.data
    } else {
      const res = await petApi.list()
      pets.value = res.data
    }
  } catch (error) {
    ElMessage.error('获取宠物列表失败')
  } finally {
    loading.value = false
  }
}

const searchPets = () => {
  fetchPets()
}

const resetSearch = () => {
  searchForm.value = {
    type: null,
    city: '',
    minAge: null,
    maxAge: null,
    keyword: ''
  }
  fetchPets()
}

const goToDetail = (id) => {
  router.push(`/pet/${id}`)
}

onMounted(() => {
  fetchPets()
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

.pet-image-placeholder {
  width: 100%;
  height: 200px;
  background-color: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px 8px 0 0;

  .el-icon {
    font-size: 60px;
    color: #c0c4cc;
  }
}
</style>
