<template>
  <div class="page-container" style="max-width: 800px;">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>编辑宠物</span>
        </div>
      </template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="宠物名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入宠物名称" />
        </el-form-item>
        <el-form-item label="品种">
          <el-input v-model="form.breed" placeholder="请输入品种（选填）" />
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number v-model="form.age" :min="0" :max="30" placeholder="年龄（岁）" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio value="MALE">公</el-radio>
            <el-radio value="FEMALE">母</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="健康状况">
          <el-input
            v-model="form.healthCondition"
            type="textarea"
            :rows="3"
            placeholder="请描述宠物的健康状况"
          />
        </el-form-item>
        <el-form-item label="性格描述">
          <el-input
            v-model="form.personality"
            type="textarea"
            :rows="3"
            placeholder="请描述宠物的性格特点"
          />
        </el-form-item>
        <el-form-item label="是否绝育">
          <el-switch v-model="form.sterilized" active-text="是" inactive-text="否" />
        </el-form-item>
        <el-form-item label="是否接种疫苗">
          <el-switch v-model="form.vaccinated" active-text="是" inactive-text="否" />
        </el-form-item>
        <el-form-item label="所在城市" prop="city">
          <el-input v-model="form.city" placeholder="请输入所在城市" />
        </el-form-item>
        <el-form-item label="照片">
          <el-upload
            v-model:file-list="fileList"
            :action="uploadUrl"
            :headers="uploadHeaders"
            list-type="picture-card"
            :on-success="handleUploadSuccess"
            :on-remove="handleRemove"
            :limit="9"
            accept="image/*"
          >
            <el-icon><Plus /></el-icon>
            <template #tip>
              <div class="el-upload__tip">
                最多上传9张图片，支持 jpg、png 格式
              </div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSubmit">保存</el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { petApi } from '@/api'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const formRef = ref(null)
const loading = ref(false)
const fileList = ref([])

const uploadUrl = computed(() => '/api/files/upload')
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${userStore.token}`
}))

const form = reactive({
  name: '',
  breed: '',
  age: null,
  gender: null,
  healthCondition: '',
  personality: '',
  sterilized: false,
  vaccinated: false,
  city: '',
  photos: []
})

const rules = {
  name: [{ required: true, message: '请输入宠物名称', trigger: 'blur' }],
  city: [{ required: true, message: '请输入所在城市', trigger: 'blur' }]
}

const fetchPet = async () => {
  loading.value = true
  try {
    const res = await petApi.getById(route.params.id)
    const pet = res.data
    form.name = pet.name || ''
    form.breed = pet.breed || ''
    form.age = pet.age
    form.gender = pet.gender
    form.healthCondition = pet.healthCondition || ''
    form.personality = pet.personality || ''
    form.sterilized = pet.sterilized
    form.vaccinated = pet.vaccinated
    form.city = pet.city || ''
    form.photos = pet.photos || []

    fileList.value = form.photos.map((url, index) => ({
      name: `image-${index}`,
      url: url
    }))
  } catch (error) {
    ElMessage.error('获取宠物信息失败')
  } finally {
    loading.value = false
  }
}

const handleUploadSuccess = (response, file, fileListData) => {
  if (response.code === 200) {
    form.photos.push(response.data.url)
  } else {
    ElMessage.error('上传失败')
  }
}

const handleRemove = (file, fileListData) => {
  const index = form.photos.findIndex(url => url === file.response?.data?.url || file.url)
  if (index > -1) {
    form.photos.splice(index, 1)
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await petApi.update(route.params.id, form)
    ElMessage.success('保存成功')
    router.push('/my-pets')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.push('/my-pets')
}

onMounted(() => {
  fetchPet()
})
</script>

<style lang="scss" scoped>
.card-header {
  font-size: 18px;
  font-weight: bold;
}
</style>
