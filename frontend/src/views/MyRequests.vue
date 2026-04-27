<template>
  <div class="page-container">
    <h2 class="page-title">我的申请</h2>

    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading"><Loading /></el-icon>
    </div>

    <div v-else-if="requests.length === 0" class="empty-state">
      <el-icon class="empty-icon"><Document /></el-icon>
      <p class="empty-text">暂无领养申请记录</p>
    </div>

    <div v-else class="request-list">
      <el-card v-for="item in requests" :key="item.id" class="request-card" shadow="hover">
        <div class="request-content" @click="goToPetDetail(item.petId)">
          <img
            v-if="item.petThumbnail"
            :src="item.petThumbnail"
            :alt="item.petName"
            class="pet-thumbnail"
          />
          <div v-else class="pet-thumbnail-placeholder">
            <el-icon><Picture /></el-icon>
          </div>
          <div class="request-info">
            <div class="pet-name">{{ item.petName }}</div>
            <div class="rescuer-info">
              <el-icon><User /></el-icon>
              <span>发布人：{{ item.rescuerNickname }}</span>
            </div>
            <div class="create-time">
              <el-icon><Clock /></el-icon>
              <span>申请时间：{{ item.createTime }}</span>
            </div>
          </div>
          <div class="status-area">
            <el-tag :type="getStatusType(item.status)" size="large">
              {{ getStatusLabel(item.status) }}
            </el-tag>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { adoptionApi } from '@/api'
import { ElMessage } from 'element-plus'

const router = useRouter()

const loading = ref(true)
const requests = ref([])

const getStatusLabel = (status) => {
  const labels = { PENDING: '待审核', APPROVED: '已通过', REJECTED: '已拒绝' }
  return labels[status] || '未知'
}

const getStatusType = (status) => {
  const types = { PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger' }
  return types[status] || ''
}

const fetchRequests = async () => {
  loading.value = true
  try {
    const res = await adoptionApi.getMyRequests()
    requests.value = res.data
  } catch (error) {
    ElMessage.error('获取申请列表失败')
  } finally {
    loading.value = false
  }
}

const goToPetDetail = (petId) => {
  router.push(`/pet/${petId}`)
}

onMounted(() => {
  fetchRequests()
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

.request-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.request-card {
  cursor: pointer;

  .request-content {
    display: flex;
    align-items: center;
    gap: 20px;
  }

  .pet-thumbnail {
    width: 120px;
    height: 120px;
    object-fit: cover;
    border-radius: 8px;
    flex-shrink: 0;
  }

  .pet-thumbnail-placeholder {
    width: 120px;
    height: 120px;
    background-color: #f5f7fa;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 8px;
    flex-shrink: 0;

    .el-icon {
      font-size: 40px;
      color: #c0c4cc;
    }
  }

  .request-info {
    flex: 1;

    .pet-name {
      font-size: 18px;
      font-weight: bold;
      margin-bottom: 12px;
    }

    .rescuer-info,
    .create-time {
      display: flex;
      align-items: center;
      gap: 6px;
      color: #909399;
      font-size: 14px;
      margin-bottom: 8px;
    }
  }

  .status-area {
    flex-shrink: 0;
  }
}
</style>
