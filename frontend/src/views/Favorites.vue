<template>
  <div class="page-container">
    <h2 class="page-title">我的收藏</h2>

    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading"><Loading /></el-icon>
    </div>

    <div v-else-if="favorites.length === 0" class="empty-state">
      <el-icon class="empty-icon"><Star /></el-icon>
      <p class="empty-text">暂无收藏的宠物</p>
      <el-button type="primary" @click="goHome">去逛逛</el-button>
    </div>

    <div v-else class="card-grid">
      <el-card
        v-for="item in favorites"
        :key="item.id"
        class="pet-card"
        shadow="hover"
      >
        <div class="image-wrapper" @click="goToDetail(item.petId)">
          <img
            v-if="item.thumbnail"
            :src="item.thumbnail"
            :alt="item.petName"
            class="pet-image"
          />
          <div v-else class="pet-image-placeholder">
            <el-icon><Picture /></el-icon>
          </div>
          <el-button
            type="danger"
            text
            class="remove-btn"
            @click.stop="removeFavorite(item)"
          >
            <el-icon><Delete /></el-icon>
          </el-button>
        </div>
        <div class="pet-info" @click="goToDetail(item.petId)">
          <div class="pet-name">{{ item.petName }}</div>
          <div class="pet-tags">
            <el-tag size="small" type="primary">{{ getTypeLabel(item.petType) }}</el-tag>
            <el-tag v-if="item.age !== null" size="small">{{ item.age }}岁</el-tag>
            <el-tag :type="getStatusType(item.petStatus)" size="small">
              {{ getStatusLabel(item.petStatus) }}
            </el-tag>
          </div>
          <div class="pet-location">
            <el-icon><Location /></el-icon>
            <span>{{ item.city }}</span>
          </div>
          <div class="rescuer-info">
            <el-icon><User /></el-icon>
            <span>发布人：{{ item.rescuerNickname }}</span>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { favoriteApi } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()

const loading = ref(true)
const favorites = ref([])

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

const fetchFavorites = async () => {
  loading.value = true
  try {
    const res = await favoriteApi.getMyFavorites()
    favorites.value = res.data
  } catch (error) {
    ElMessage.error('获取收藏列表失败')
  } finally {
    loading.value = false
  }
}

const goToDetail = (petId) => {
  router.push(`/pet/${petId}`)
}

const goHome = () => {
  router.push('/')
}

const removeFavorite = async (item) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏该宠物吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await favoriteApi.remove(item.petId)
    ElMessage.success('已取消收藏')
    fetchFavorites()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

onMounted(() => {
  fetchFavorites()
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

.pet-card {
  .image-wrapper {
    position: relative;
    cursor: pointer;

    .pet-image {
      width: 100%;
      height: 200px;
      object-fit: cover;
      border-radius: 8px 8px 0 0;
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

    .remove-btn {
      position: absolute;
      top: 8px;
      right: 8px;
      background-color: rgba(255, 255, 255, 0.9);
      border-radius: 4px;
      padding: 4px 8px;
    }
  }

  .pet-info {
    padding: 16px;
    cursor: pointer;

    .pet-name {
      font-size: 18px;
      font-weight: bold;
      margin-bottom: 8px;
    }

    .pet-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 6px;
      margin-bottom: 8px;
    }

    .pet-location,
    .rescuer-info {
      display: flex;
      align-items: center;
      color: #909399;
      font-size: 13px;
      margin-bottom: 4px;

      .el-icon {
        margin-right: 4px;
      }
    }
  }
}
</style>
