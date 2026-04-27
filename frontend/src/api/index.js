import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 30000
})

request.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code === 200) {
      return res
    } else {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
  },
  (error) => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          ElMessage.error('登录已过期，请重新登录')
          const userStore = useUserStore()
          userStore.logout()
          router.push('/login')
          break
        case 403:
          ElMessage.error('权限不足')
          break
        case 500:
          ElMessage.error('服务器错误')
          break
        default:
          ElMessage.error(error.response.data?.message || '请求失败')
      }
    } else {
      ElMessage.error('网络错误，请稍后重试')
    }
    return Promise.reject(error)
  }
)

export const userApi = {
  login: (data) => request.post('/auth/login', data),
  register: (data) => request.post('/auth/register', data),
  getCurrentUser: () => request.get('/auth/me'),
  updateProfile: (data) => request.put('/users/profile', data)
}

export const petApi = {
  list: () => request.get('/pets/list'),
  search: (data) => request.post('/pets/search', data),
  getById: (id) => request.get(`/pets/${id}`),
  create: (data) => request.post('/pets', data),
  update: (id, data) => request.put(`/pets/${id}`, data),
  updateStatus: (id, status) => request.put(`/pets/${id}/status?status=${status}`),
  delete: (id) => request.delete(`/pets/${id}`),
  getMyPets: () => request.get('/pets/my')
}

export const adoptionApi = {
  create: (data) => request.post('/adoptions', data),
  getMyRequests: () => request.get('/adoptions/my'),
  getReceivedRequests: () => request.get('/adoptions/received'),
  getReceivedRequestsByStatus: (status) => request.get(`/adoptions/received/status/${status}`),
  review: (data) => request.post('/adoptions/review', data),
  getById: (id) => request.get(`/adoptions/${id}`)
}

export const favoriteApi = {
  toggle: (petId) => request.post(`/favorites/toggle/${petId}`),
  add: (petId) => request.post(`/favorites/add/${petId}`),
  remove: (petId) => request.delete(`/favorites/remove/${petId}`),
  check: (petId) => request.get(`/favorites/check/${petId}`),
  getMyFavorites: () => request.get('/favorites/my')
}

export const fileApi = {
  upload: (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/files/upload', formData)
  },
  uploadMultiple: (files) => {
    const formData = new FormData()
    files.forEach((file, index) => {
      formData.append('files', file)
    })
    return request.post('/files/uploads', formData)
  }
}

export default request
