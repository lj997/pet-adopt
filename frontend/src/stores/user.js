import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { userApi } from '@/api'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  const isLoggedIn = computed(() => !!token.value && !!user.value)
  const userRole = computed(() => user.value?.role || '')

  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const setUser = (newUser) => {
    user.value = newUser
    localStorage.setItem('user', JSON.stringify(newUser))
  }

  const login = async (loginForm) => {
    const res = await userApi.login(loginForm)
    setToken(res.data.token)
    setUser(res.data.user)
    return res
  }

  const register = async (registerForm) => {
    return await userApi.register(registerForm)
  }

  const logout = () => {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  const fetchCurrentUser = async () => {
    const res = await userApi.getCurrentUser()
    setUser(res.data)
    return res
  }

  const updateProfile = async (form) => {
    const res = await userApi.updateProfile(form)
    if (res.data) {
      setUser(res.data)
    }
    return res
  }

  return {
    token,
    user,
    isLoggedIn,
    userRole,
    setToken,
    setUser,
    login,
    register,
    logout,
    fetchCurrentUser,
    updateProfile
  }
})
