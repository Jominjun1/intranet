<template>
  <header class="app-header">
    <div class="header-content">
      <div class="logo">
        <h1>TEIA 인트라넷</h1>
      </div>
      
      <div class="user-section">
        <el-dropdown @command="handleUserCommand">
          <span class="user-info">
            <el-avatar :size="32">
              {{ userInfo.user_name ? userInfo.user_name.charAt(0) : 'U' }}
            </el-avatar>
            <span class="username">{{ userInfo.user_name || '사용자' }}</span>
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">프로필</el-dropdown-item>
              <el-dropdown-item command="settings">설정</el-dropdown-item>
              <el-dropdown-item divided command="logout">로그아웃</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </header>
</template>

<script setup>
import {computed} from 'vue'
import {ArrowDown} from '@element-plus/icons-vue'
import '../css/VUE/Header.css'

const props = defineProps({
  activeMenu: {
    type: String,
    default: ''
  },
  userInfo: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['menu-select', 'user-command'])

const userAcl = computed(() => parseInt(props.userInfo.user_acl || '0'))

function handleUserCommand(command) {
  emit('user-command', command)
}

function handleMenuSelect(key) {
  emit('menu-select', key)
}
</script>