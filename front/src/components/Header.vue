<template>
  <header class="app-header">
    <div class="header-content">
      <div class="logo">
        <h1>스마트태그 인벤토리 관리</h1>
      </div>
      <nav class="main-nav">
        <el-dropdown 
          class="nav-item" 
          :class="{ active: activeMenu.startsWith('tag-') }"
          @command="handleSubMenuSelect"
          trigger="hover"
        >
          <span class="dropdown-trigger">
            태그 관리
            <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="tag-search">🔍 태그 검색</el-dropdown-item>
              <el-dropdown-item command="tag-proc-step">📋 처리단계 조회</el-dropdown-item>
              <el-dropdown-item command="tag-setting">⚙️ 세팅정보 조회</el-dropdown-item>
              <el-dropdown-item command="tag-version">📈 버전 이력 조회</el-dropdown-item>
              <el-dropdown-item command="tag-common">🏷️ 공통정보 조회</el-dropdown-item>
              <el-dropdown-item command="tag-as">🔧 AS 이력 조회</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <div class="nav-item" 
             v-if="userAcl >= 3"
             :class="{ active: activeMenu === 'user-management' }"
             @click="handleMenuSelect('user-management')">
          <span>사용자 관리</span>
        </div>
        <div class="nav-item" 
             :class="{ active: activeMenu === 'log-management' }"
             @click="handleMenuSelect('log-management')">
          <span>로그 관리</span>
        </div>
      </nav>
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
import { computed } from 'vue'
import { ElMessage } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'

// Props
const props = defineProps({
  activeMenu: {
    type: String,
    default: 'tag-management'
  },
  userInfo: {
    type: Object,
    default: () => ({})
  }
})

// Emits
const emit = defineEmits(['menu-select', 'user-command'])

// Computed
const userAcl = computed(() => parseInt(props.userInfo.user_acl || '0'))

// Methods
function handleMenuSelect(key) {
  emit('menu-select', key)
}

function handleSubMenuSelect(command) {
  console.log('서브메뉴 선택:', command)
  emit('menu-select', command)
}

function handleUserCommand(command) {
  emit('user-command', command)
}
</script>

<style scoped>
.app-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-bottom: none;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
  flex-shrink: 0;
  z-index: 1000;
  position: relative;
}

.app-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
  height: 70px;
  width: 100%;
  position: relative;
  z-index: 1;
}

.logo h1 {
  margin: 0;
  font-size: 1.6rem;
  color: white;
  font-weight: 700;
  text-shadow: 0 2px 4px rgba(0,0,0,0.2);
  letter-spacing: -0.5px;
}

.main-nav {
  flex: 1;
  display: flex;
  justify-content: center;
  gap: 0;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 0 24px;
  height: 70px;
  cursor: pointer;
  transition: all 0.3s ease;
  color: rgba(255, 255, 255, 0.8);
  font-weight: 500;
  font-size: 0.95rem;
  position: relative;
  border-bottom: 3px solid transparent;
}

.nav-item:hover {
  color: white;
  background: rgba(255, 255, 255, 0.1);
}

.nav-item.active {
  color: white;
  background: rgba(255, 255, 255, 0.15);
  border-bottom: 3px solid white;
  font-weight: 600;
}

.nav-item span {
  position: relative;
  z-index: 1;
}

.dropdown-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
}

.dropdown-icon {
  font-size: 12px;
  transition: transform 0.3s ease;
}

.nav-item:hover .dropdown-icon {
  transform: rotate(180deg);
}

:deep(.el-dropdown-menu) {
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.15);
  border: 1px solid rgba(255,255,255,0.1);
  backdrop-filter: blur(10px);
  background: rgba(255,255,255,0.95);
}

:deep(.el-dropdown-menu__item) {
  padding: 12px 20px;
  font-size: 0.9rem;
  color: #333;
  transition: all 0.3s ease;
}

:deep(.el-dropdown-menu__item:hover) {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
}

:deep(.el-dropdown-menu__item.is-disabled) {
  color: #999;
  background: transparent;
}

.user-section {
  display: flex;
  align-items: center;
  margin-left: auto;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 8px 16px;
  border-radius: 8px;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.user-info:hover {
  background: rgba(255, 255, 255, 0.2);
}

.username {
  font-weight: 600;
  color: white;
  font-size: 0.9rem;
}

:deep(.el-avatar) {
  border: 2px solid rgba(255, 255, 255, 0.3);
  background: rgba(255, 255, 255, 0.2);
  color: white;
  font-weight: 600;
}
</style> 