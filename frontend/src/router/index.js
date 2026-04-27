import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/Profile.vue'),
    meta: { title: '个人中心', requiresAuth: true }
  },
  {
    path: '/pet/:id',
    name: 'PetDetail',
    component: () => import('@/views/PetDetail.vue'),
    meta: { title: '宠物详情' }
  },
  {
    path: '/my-pets',
    name: 'MyPets',
    component: () => import('@/views/MyPets.vue'),
    meta: { title: '我的宠物', requiresAuth: true, role: 'RESCUER' }
  },
  {
    path: '/pet-publish',
    name: 'PetPublish',
    component: () => import('@/views/PetPublish.vue'),
    meta: { title: '发布宠物', requiresAuth: true, role: 'RESCUER' }
  },
  {
    path: '/pet-edit/:id',
    name: 'PetEdit',
    component: () => import('@/views/PetEdit.vue'),
    meta: { title: '编辑宠物', requiresAuth: true, role: 'RESCUER' }
  },
  {
    path: '/my-requests',
    name: 'MyRequests',
    component: () => import('@/views/MyRequests.vue'),
    meta: { title: '我的申请', requiresAuth: true, role: 'ADOPTER' }
  },
  {
    path: '/received-requests',
    name: 'ReceivedRequests',
    component: () => import('@/views/ReceivedRequests.vue'),
    meta: { title: '领养申请', requiresAuth: true, role: 'RESCUER' }
  },
  {
    path: '/favorites',
    name: 'Favorites',
    component: () => import('@/views/Favorites.vue'),
    meta: { title: '我的收藏', requiresAuth: true, role: 'ADOPTER' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title || '宠物领养平台'

  const userStore = useUserStore()

  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
  } else if (to.meta.role && userStore.user.role !== to.meta.role) {
    next({ name: 'Home' })
  } else {
    next()
  }
})

export default router
