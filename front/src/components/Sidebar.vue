<template>
  <aside class="sidebar" :class="{ collapsed: isCollapsed }">
    <div class="sidebar-header">
      <div class="toggle-btn" @click="toggleSidebar">
        <el-icon><Expand v-if="isCollapsed" /><Fold v-else /></el-icon>
      </div>
      <h3 v-show="!isCollapsed">메뉴</h3>
    </div>
    
    <nav class="sidebar-nav">
      <!-- 스마트태그 메뉴 그룹 -->
      <div class="menu-group">
        <div 
          class="menu-group-header" 
          @click="handleMenuGroupClick('smartTag')"
          :class="{ 'expanded': isSmartTagExpanded }"
        >
          <el-icon><Collection /></el-icon>
          <span v-show="!isCollapsed">스마트태그</span>
          <el-icon v-show="!isCollapsed" class="expand-icon" :class="{ 'rotated': isSmartTagExpanded }">
            <ArrowDown />
          </el-icon>
        </div>
        
        <transition name="submenu">
          <div v-show="isSmartTagExpanded" class="submenu-items">
            <div 
              class="menu-item submenu-item" 
              :class="{ active: activeMenu === 'tag-search' }"
              @click="handleMenuSelect('tag-search')"
            >
              <el-icon><Search /></el-icon>
              <span v-show="!isCollapsed">태그 검색</span>
            </div>
            
            <div 
              class="menu-item submenu-item" 
              :class="{ active: activeMenu === 'tag-proc-step' }"
              @click="handleMenuSelect('tag-proc-step')"
            >
              <el-icon><Document /></el-icon>
              <span v-show="!isCollapsed">처리단계 관리</span>
            </div>
            
            <div 
              class="menu-item submenu-item" 
              :class="{ active: activeMenu === 'tag-setting' }"
              @click="handleMenuSelect('tag-setting')"
            >
              <el-icon><Setting /></el-icon>
              <span v-show="!isCollapsed">세팅정보 관리</span>
            </div>
            
            <div 
              class="menu-item submenu-item" 
              :class="{ active: activeMenu === 'tag-version' }"
              @click="handleMenuSelect('tag-version')"
            >
              <el-icon><Histogram /></el-icon>
              <span v-show="!isCollapsed">버전 이력 관리</span>
            </div>
            
            <div 
              class="menu-item submenu-item" 
              :class="{ active: activeMenu === 'tag-common' }"
              @click="handleMenuSelect('tag-common')"
            >
              <el-icon><Collection /></el-icon>
              <span v-show="!isCollapsed">공통정보 관리</span>
            </div>
            
            <div 
              class="menu-item submenu-item" 
              :class="{ active: activeMenu === 'tag-as' }"
              @click="handleMenuSelect('tag-as')"
            >
              <el-icon><Tools /></el-icon>
              <span v-show="!isCollapsed">AS 이력 관리</span>
            </div>
          </div>
        </transition>
      </div>
      
      <!-- 시스템 관리 메뉴 그룹 -->
      <div class="menu-group">
        <div 
          class="menu-group-header" 
          @click="handleMenuGroupClick('system')"
          :class="{ 'expanded': isSystemExpanded }"
        >
          <el-icon><Collection /></el-icon>
          <span v-show="!isCollapsed">시스템 관리</span>
          <el-icon v-show="!isCollapsed" class="expand-icon" :class="{ 'rotated': isSystemExpanded }">
            <ArrowDown />
          </el-icon>
        </div>
        
        <transition name="submenu">
          <div v-show="isSystemExpanded" class="submenu-items">
            <div 
              v-if="userAcl >= 3"
              class="menu-item submenu-item" 
              :class="{ active: activeMenu === 'user-management' }"
              @click="handleMenuSelect('user-management')"
            >
              <el-icon><User /></el-icon>
              <span v-show="!isCollapsed">사용자 관리</span>
            </div>
            
            <div 
              v-if="userAcl >= 3"
              class="menu-item submenu-item" 
              :class="{ active: activeMenu === 'dept-management' }"
              @click="handleMenuSelect('dept-management')"
            >
              <el-icon><OfficeBuilding /></el-icon>
              <span v-show="!isCollapsed">부서 관리</span>
            </div>
            
            <div 
              class="menu-item submenu-item" 
              :class="{ active: activeMenu === 'log-management' }"
              @click="handleMenuSelect('log-management')"
            >
              <el-icon><DocumentCopy /></el-icon>
              <span v-show="!isCollapsed">로그 관리</span>
            </div>
          </div>
        </transition>
      </div>
    </nav>
  </aside>
</template>

<script setup>
import {computed, ref} from 'vue'
import {
  ArrowDown,
  Collection,
  Document,
  DocumentCopy,
  Expand,
  Fold,
  Histogram,
  OfficeBuilding,
  Search,
  Setting,
  Tools,
  User
} from '@element-plus/icons-vue'

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
const emit = defineEmits(['menu-select'])

// Local state
const isCollapsed = ref(false)
const isSmartTagExpanded = ref(false)
const isSystemExpanded = ref(false)

// Computed
const userAcl = computed(() => parseInt(props.userInfo.user_acl || '0'))

// Methods
function toggleSidebar() {
  isCollapsed.value = !isCollapsed.value
}

function toggleSmartTagMenu() {
  isSmartTagExpanded.value = !isSmartTagExpanded.value
}

function toggleSystemMenu() {
  isSystemExpanded.value = !isSystemExpanded.value
}

function handleMenuGroupClick(groupName) {
  // 사이드바가 접혀있으면 먼저 펴기
  if (isCollapsed.value) {
    isCollapsed.value = false
  }
  
  // 해당 메뉴 그룹 토글
  if (groupName === 'smartTag') {
    toggleSmartTagMenu()
  } else if (groupName === 'system') {
    toggleSystemMenu()
  }
}

function handleMenuSelect(key) {
  emit('menu-select', key)
}
</script>

<style scoped>
.sidebar {
  width: 280px;
  height: 100%;
  background: #ffffff;
  border-right: 1px solid #e5e7eb;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
}

.sidebar.collapsed {
  width: 64px;
}

.sidebar-header {
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding: 0 20px;
  border-bottom: 1px solid #e5e7eb;
  background: #f8fafc;
}

.toggle-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.2s ease;
  color: #6b7280;
}

.toggle-btn:hover {
  background: #e5e7eb;
  color: #374151;
}

.sidebar-header h3 {
  margin: 0 0 0 16px;
  font-size: 1.1rem;
  font-weight: 600;
  color: #374151;
}

.sidebar-nav {
  flex: 1;
  padding: 20px 0;
  overflow-y: auto;
}

.menu-group {
  margin-bottom: 32px;
}

.menu-group-header {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 12px;
  padding: 0 20px 16px;
  color: #6b7280;
  font-size: 0.875rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  cursor: pointer;
  transition: all 0.3s ease;
}

.menu-group-header.expanded {
  color: #3b82f6;
}

.menu-group-header .expand-icon {
  transition: transform 0.3s ease;
  font-size: 16px;
}

.menu-group-header .expand-icon.rotated {
  transform: rotate(180deg);
}

.submenu-items {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding-left: 20px;
  margin-top: 8px;
  border-left: 1px solid #e5e7eb;
}

.menu-item {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 16px;
  padding: 12px 20px;
  cursor: pointer;
  transition: all 0.2s ease;
  color: #6b7280;
  border-radius: 0 8px 8px 0;
  margin-right: 16px;
}

.menu-item:hover {
  background: #f3f4f6;
  color: #3b82f6;
}

.menu-item.active {
  background: #eff6ff;
  color: #3b82f6;
  font-weight: 600;
  border-right: 3px solid #3b82f6;
}

.menu-item .el-icon {
  font-size: 18px;
  min-width: 20px;
  text-align: center;
}

.menu-item span {
  font-size: 0.9rem;
  white-space: nowrap;
}

/* 스크롤바 스타일링 */
.sidebar-nav::-webkit-scrollbar {
  width: 4px;
}

.sidebar-nav::-webkit-scrollbar-track {
  background: transparent;
}

.sidebar-nav::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 2px;
}

.sidebar-nav::-webkit-scrollbar-thumb:hover {
  background: #9ca3af;
}

/* 반응형 */
@media (max-width: 768px) {
  .sidebar {
    position: fixed;
    left: 0;
    top: 0;
    z-index: 1001;
    transform: translateX(-100%);
  }
  
  .sidebar.show {
    transform: translateX(0);
  }
}

/* 서브메뉴 애니메이션 */
.submenu-enter-active,
.submenu-leave-active {
  transition: all 0.3s ease;
  overflow: hidden;
}

.submenu-enter-from {
  opacity: 0;
  transform: translateY(-10px);
  max-height: 0;
}

.submenu-enter-to {
  opacity: 1;
  transform: translateY(0);
  max-height: 300px;
}

.submenu-leave-from {
  opacity: 1;
  transform: translateY(0);
  max-height: 300px;
}

.submenu-leave-to {
  opacity: 0;
  transform: translateY(-10px);
  max-height: 0;
}

/* 서브메뉴 아이템 스타일 */
.submenu-item {
  margin-left: 20px;
  padding-left: 16px;
  border-left: 2px solid #e5e7eb;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 16px;
}

.submenu-item:hover {
  border-left-color: #3b82f6;
}

.submenu-item.active {
  border-left-color: #3b82f6;
}
</style>

