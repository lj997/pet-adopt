<template>
  <div class="page-container">
    <h2 class="page-title">领养申请</h2>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="全部" name="all" />
      <el-tab-pane label="待审核" name="PENDING" />
      <el-tab-pane label="已通过" name="APPROVED" />
      <el-tab-pane label="已拒绝" name="REJECTED" />
    </el-tabs>

    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading"><Loading /></el-icon>
    </div>

    <div v-else-if="requests.length === 0" class="empty-state">
      <el-icon class="empty-icon"><Document /></el-icon>
      <p class="empty-text">暂无领养申请</p>
    </div>

    <div v-else class="request-list">
      <el-card v-for="item in requests" :key="item.id" class="request-card" shadow="hover">
        <div class="request-content">
          <div class="pet-section" @click="goToPetDetail(item.petId)">
            <img
              v-if="item.petThumbnail"
              :src="item.petThumbnail"
              :alt="item.petName"
              class="pet-thumbnail"
            />
            <div v-else class="pet-thumbnail-placeholder">
              <el-icon><Picture /></el-icon>
            </div>
            <div class="pet-info">
              <div class="pet-name">{{ item.petName }}</div>
              <el-tag :type="getStatusType(item.status)" size="small">
                {{ getStatusLabel(item.status) }}
              </el-tag>
            </div>
          </div>

          <el-divider />

          <div class="adopter-section">
            <div class="section-title">申请信息</div>
            <div class="info-grid">
              <div class="info-item">
                <span class="label">申请人：</span>
                <span class="value">{{ item.adopterNickname }}</span>
              </div>
              <div class="info-item">
                <span class="label">联系方式：</span>
                <span class="value">{{ item.contactInfo }}</span>
              </div>
              <div class="info-item">
                <span class="label">申请时间：</span>
                <span class="value">{{ item.createTime }}</span>
              </div>
              <div v-if="item.reviewTime" class="info-item">
                <span class="label">审核时间：</span>
                <span class="value">{{ item.reviewTime }}</span>
              </div>
            </div>

            <el-divider />

            <div class="info-item-full">
              <span class="label">个人介绍：</span>
              <span class="value">{{ item.personalIntro || '无' }}</span>
            </div>
            <div class="info-item-full">
              <span class="label">养宠经验：</span>
              <span class="value">{{ item.petExperience || '无' }}</span>
            </div>
            <div class="info-item-full">
              <span class="label">住房情况：</span>
              <span class="value">{{ item.housingCondition || '无' }}</span>
            </div>

            <el-divider v-if="item.reviewNote" />

            <div v-if="item.reviewNote" class="info-item-full">
              <span class="label">审核备注：</span>
              <span class="value">{{ item.reviewNote }}</span>
            </div>
          </div>

          <div v-if="item.status === 'PENDING'" class="action-section">
            <el-input
              v-model="reviewNotes[item.id]"
              type="textarea"
              :rows="2"
              placeholder="请输入回访备注（选填）"
              style="margin-bottom: 12px;"
            />
            <div class="action-buttons">
              <el-button type="success" @click="approveRequest(item)">
                <el-icon><Check /></el-icon>
                通过
              </el-button>
              <el-button type="danger" @click="rejectRequest(item)">
                <el-icon><Close /></el-icon>
                拒绝
              </el-button>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { adoptionApi } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()

const activeTab = ref('all')
const loading = ref(true)
const requests = ref([])
const reviewNotes = reactive({})

const getStatusLabel = (status) => {
  const labels = { PENDING: '待审核', APPROVED: '已通过', REJECTED: '已拒绝' }
  return labels[status] || '未知'
}

const getStatusType = (status) => {
  const types = { PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger' }
  return types[status] || ''
}

const fetchRequests = async (status) => {
  loading.value = true
  try {
    let res
    if (status === 'all') {
      res = await adoptionApi.getReceivedRequests()
    } else {
      res = await adoptionApi.getReceivedRequestsByStatus(status)
    }
    requests.value = res.data
  } catch (error) {
    ElMessage.error('获取申请列表失败')
  } finally {
    loading.value = false
  }
}

watch(activeTab, (newTab) => {
  fetchRequests(newTab)
}, { immediate: true })

const goToPetDetail = (petId) => {
  router.push(`/pet/${petId}`)
}

const approveRequest = async (item) => {
  try {
    await ElMessageBox.confirm('确定通过该领养申请吗？通过后宠物将被标记为已领养。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await adoptionApi.review({
      requestId: item.id,
      status: 'APPROVED',
      reviewNote: reviewNotes[item.id] || ''
    })
    ElMessage.success('审核通过')
    fetchRequests(activeTab.value)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const rejectRequest = async (item) => {
  try {
    await ElMessageBox.confirm('确定拒绝该领养申请吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await adoptionApi.review({
      requestId: item.id,
      status: 'REJECTED',
      reviewNote: reviewNotes[item.id] || ''
    })
    ElMessage.success('已拒绝')
    fetchRequests(activeTab.value)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}
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
  gap: 20px;
}

.request-card {
  .request-content {
    display: flex;
    flex-direction: column;
  }

  .pet-section {
    display: flex;
    align-items: center;
    gap: 16px;
    cursor: pointer;

    .pet-thumbnail {
      width: 100px;
      height: 100px;
      object-fit: cover;
      border-radius: 8px;
      flex-shrink: 0;
    }

    .pet-thumbnail-placeholder {
      width: 100px;
      height: 100px;
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

    .pet-info {
      .pet-name {
        font-size: 18px;
        font-weight: bold;
        margin-bottom: 8px;
      }
    }
  }

  .adopter-section {
    .section-title {
      font-size: 16px;
      font-weight: bold;
      margin-bottom: 16px;
      color: #303133;
    }

    .info-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 12px;
    }

    .info-item {
      display: flex;
      align-items: center;
      font-size: 14px;

      .label {
        color: #909399;
        width: 80px;
        flex-shrink: 0;
      }

      .value {
        color: #303133;
      }
    }

    .info-item-full {
      display: flex;
      margin-bottom: 12px;
      font-size: 14px;

      .label {
        color: #909399;
        width: 80px;
        flex-shrink: 0;
      }

      .value {
        color: #303133;
        flex: 1;
      }
    }
  }

  .action-section {
    margin-top: 12px;

    .action-buttons {
      display: flex;
      gap: 12px;
    }
  }
}
</style>
