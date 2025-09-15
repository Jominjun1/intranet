<template>
  <header class="app-header">
    <div class="header-content">
      <div class="logo">
        <h1>스마트태그 인벤토리 관리</h1>
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
</script>

<style scoped>
.app-header {
  background: #ffffff;
  border-bottom: 1px solid #e5e7eb;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06);
  position: relative;
  z-index: 1000;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 70px;
  max-width: 100%;
}

.logo h1 {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 700;
  color: #1e293b;
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.user-section {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  color: #374151;
}

.user-info:hover {
  background: #f3f4f6;
  color: #1f2937;
}

.username {
  font-weight: 500;
  font-size: 0.9rem;
}

/* Element Plus dropdown menu styling */
:deep(.el-dropdown-menu) {
  border-radius: 8px;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
  border: 1px solid #e5e7eb;
}

:deep(.el-dropdown-menu__item) {
  padding: 12px 16px;
  font-size: 0.9rem;
  color: #374151;
  transition: all 0.2s ease;
}

:deep(.el-dropdown-menu__item:hover) {
  background: #f3f4f6;
  color: #1f2937;
}

:deep(.el-dropdown-menu__item--divided) {
  border-top: 1px solid #e5e7eb;
  margin-top: 4px;
  padding-top: 16px;
}

/* Responsive */
@media (max-width: 768px) {
  .header-content {
    padding: 0 16px;
  }
  
  .logo h1 {
    font-size: 1.2rem;
  }
  
  .username {
    display: none;
  }
}
</style> 