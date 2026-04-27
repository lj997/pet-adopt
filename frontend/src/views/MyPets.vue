<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">我的宠物</h2>
      <el-button type="primary" @click="goPublish">
        <el-icon><Plus /></el-icon>
        发布宠物
      </el-button>
    </div>

    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading"><Loading /></el-icon>
    </div>

    <div v-else-if="pets.length === 0" class="empty-state">
      <el-icon class="empty-icon"><FolderOpened /></el-icon>
      <p class="empty-text">暂无宠物信息</p>
      <el-button type="primary" @click="goPublish">去发布</el-button>
    </div>

    <div v-else class="card-grid">
      <el-card
        v-for="pet in pets"
        :key="pet.id"
        class="pet-card"
        shadow="hover"
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
            <el-tag :type="getStatusType(pet.status)" size="small">
              {{ getStatusLabel(pet.status) }}
            </el-tag>
          </div>
          <div class="pet-actions">
            <el-button type="primary" text size="small" @click="goEdit(pet.id)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button
              v-if="pet.status === 'AVAILABLE'"
              type="success"
              text
              size="small"
              @click="markAdopted(pet)"
            >
              <el-icon><Check /></el-icon>
              标记已领养
            </el-button>
            <el-button
              v-else-if="pet.status === 'ADOPTED'"
              type="warning"
              text
              size="small"
              @click="markAvailable(pet)"
            >
              <el-icon><Refresh /></el-icon>
              重新上架
            </el-button>
            <el-button type="danger" text size="small" @click="handleDelete(pet)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
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
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()

const loading = ref(true)
const pets = ref([])

const getTypeLabel = (type) => {
  const labels = { CAT: '猫', DOG: '狗', OTHER: '其他' }
  return labels[type] || '未知'
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
    const res = await petApi.getMyPets()
    pets.value = res.data
  } catch (error) {
    ElMessage.error('获取宠物列表失败')
  } finally {
    loading.value = false
  }
}

const goPublish = () => {
  router.push('/pet-publish')
}

const goEdit = (id) => {
  router.push(`/pet-edit/${id}`)
}

const markAdopted = async (pet) => {
  try {
    await ElMessageBox.confirm('确定要将该宠物标记为已领养吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await petApi.updateStatus(pet.id, 'ADOPTED')
    ElMessage.success('已标记为已领养')
    fetchPets()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const markAvailable = async (pet) => {
  try {
    await ElMessageBox.confirm('确定要将该宠物重新上架吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await petApi.updateStatus(pet.id, 'AVAILABLE')
    ElMessage.success('已重新上架')
    fetchPets()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const handleDelete = async (pet) => {
  try {
    await ElMessageBox.confirm('确定要删除该宠物信息吗？此操作不可恢复。', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await petApi.delete(pet.id)
    ElMessage.success('删除成功')
    fetchPets()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  fetchPets()
})
</script>

<style lang="scss" scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

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

.pet-actions {
  display: flex;
  gap: 8px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #EBEEF5;
}
</style>
