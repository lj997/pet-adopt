<template>
  <div id="app">
    <el-container class="main-container">
      <el-header class="header">
        <div class="logo" @click="goHome">
          <el-icon><Pets /></el-icon>
          <span>宠物领养平台</span>
        </div>
        <el-menu
          mode="horizontal"
          :default-active="activeMenu"
          class="nav-menu"
          background-color="transparent"
          text-color="#333"
          active-text-color="#409EFF"
        >
          <el-menu-item index="/" @click="$router.push('/')">
            <el-icon><House /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <template v-if="userStore.isLoggedIn">
            <el-menu-item v-if="userStore.user.role === 'RESCUER'" index="/my-pets" @click="$router.push('/my-pets')">
              <el-icon><EditPen /></el-icon>
              <span>我的宠物</span>
            </el-menu-item>
            <el-menu-item v-if="userStore.user.role === 'RESCUER'" index="/received-requests" @click="$router.push('/received-requests')">
              <el-icon><DocumentChecked /></el-icon>
              <span>领养申请</span>
            </el-menu-item>
            <el-menu-item v-if="userStore.user.role === 'ADOPTER'" index="/my-requests" @click="$router.push('/my-requests')">
              <el-icon><List /></el-icon>
              <span>我的申请</span>
            </el-menu-item>
            <el-menu-item v-if="userStore.user.role === 'ADOPTER'" index="/favorites" @click="$router.push('/favorites')">
              <el-icon><Star /></el-icon>
              <span>我的收藏</span>
            </el-menu-item>
          </template>
        </el-menu>
        <div class="user-area">
          <template v-if="userStore.isLoggedIn">
            <el-dropdown>
              <span class="user-info">
                <el-avatar :size="32" class="avatar">
                  <el-icon><User /></el-icon>
                </el-avatar>
                <span class="nickname">{{ userStore.user.nickname }}</span>
                <el-tag v-if="userStore.user.role === 'RESCUER'" type="warning" size="small">救助人</el-tag>
                <el-tag v-else type="success" size="small">领养人</el-tag>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="goProfile">
                    <el-icon><User /></el-icon>
                    个人中心
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="logout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button type="primary" text @click="goLogin">登录</el-button>
            <el-button type="primary" text @click="goRegister">注册</el-button>
          </template>
        </div>
      </el-header>
      <el-main class="main-content">
        <router-view />
      </el-main>
      <el-footer class="footer">
        <div class="footer-content">
          <p>© 2024 宠物领养平台 - 让每一个生命都有一个家</p>
        </div>
      </el-footer>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const goHome = () => router.push('/')
const goLogin = () => router.push('/login')
const goRegister = () => router.push('/register')
const goProfile = () => router.push('/profile')

const logout = () => {
  userStore.logout()
  router.push('/')
}
</script>

<style lang="scss" scoped>
.main-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0 20px;
  height: 60px;
  position: sticky;
  top: 0;
  z-index: 1000;
}

.logo {
  display: flex;
  align-items: center;
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
  cursor: pointer;

  .el-icon {
    font-size: 24px;
    margin-right: 8px;
  }
}

.nav-menu {
  flex: 1;
  margin: 0 20px;
  border: none;

  .el-menu-item {
    border-bottom: none !important;
    height: 60px;
    line-height: 60px;
  }
}

.user-area {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;

  .nickname {
    margin: 0 8px;
    font-size: 14px;
  }

  .el-tag {
    margin-left: 4px;
  }
}

.main-content {
  flex: 1;
  background-color: #f5f7fa;
  padding: 20px;
}

.footer {
  background-color: #fff;
  border-top: 1px solid #e6e6e6;
  padding: 20px 0;

  .footer-content {
    text-align: center;
    color: #999;
    font-size: 14px;

    p {
      margin: 0;
    }
  }
}
</style>
