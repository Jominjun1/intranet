<template>
  <aside class="sidebar" :class="{ collapsed: isCollapsed }">
    <div class="sidebar-header">
      <div class="toggle-btn" @click="toggleSidebar">
        <el-icon>
          <Expand v-if="isCollapsed"/>
          <Fold v-else/>
        </el-icon>
      </div>
      <h3 v-show="!isCollapsed">메뉴</h3>
    </div>
    <!-- 일일보고 메뉴 그룹 -->
    <nav class="sidebar-nav">
      <div class="menu-group">
        <div
            class="menu-group-header"
            @click="handleMenuGroupClick('daily')"
            :class="{ 'expanded': isDailyExpanded }"
        >
          <el-icon>
            <Collection/>
          </el-icon>
          <span v-show="!isCollapsed">일일보고</span>
          <el-icon v-show="!isCollapsed" class="expand-icon" :class="{'rotated': isDailyExpanded}">
            <ArrowDown/>
          </el-icon>
        </div>

        <transition name="submenu">
          <div v-show="isDailyExpanded" class="submenu-items">
            <div
                class="menu-item submenu-item"
                :class="{ active: activeMenu === 'daily-list' }"
                @click="handleMenuSelect('daily-list')"
            >
              <el-icon>
                <Search/>
              </el-icon>
              <span v-show="!isCollapsed">일일보고 목록</span>
            </div>

            <div
                class="menu-item submenu-item"
                :class="{ active: activeMenu === 'daily-step' }"
                @click="handleMenuSelect('daily-step')"
            >
              <el-icon>
                <Document/>
              </el-icon>
              <span v-show="!isCollapsed">일일보고 관리</span>
            </div>
          </div>
        </transition>
      </div>
      <!-- 프로젝트 메뉴 그룹 -->
      <div class="menu-group">
        <div
            class="menu-group-header"
            @click="handleMenuGroupClick('project')"
            :class="{ 'expanded': isProjectExpanded }"
        >
          <el-icon>
            <Collection/>
          </el-icon>
          <span v-show="!isCollapsed">프로젝트</span>
          <el-icon v-show="!isCollapsed" class="expand-icon" :class="{'rotated': isProjectExpanded}">
            <ArrowDown/>
          </el-icon>
        </div>

        <transition name="submenu">
          <div v-show="isProjectExpanded" class="submenu-items">
            <div
                class="menu-item submenu-item"
                :class="{ active: activeMenu === 'project-step' }"
                @click="handleMenuSelect('project-step')"
            >
              <el-icon>
                <Document/>
              </el-icon>
              <span v-show="!isCollapsed">프로젝트 관리</span>
            </div>
          </div>
        </transition>
      </div>
      <!-- 스마트태그 메뉴 그룹 -->
      <div class="menu-group">
        <div
            class="menu-group-header"
            @click="handleMenuGroupClick('smartTag')"
            :class="{ 'expanded': isSmartTagExpanded }"
        >
          <el-icon>
            <Collection/>
          </el-icon>
          <span v-show="!isCollapsed">스마트태그</span>
          <el-icon v-show="!isCollapsed" class="expand-icon" :class="{ 'rotated': isSmartTagExpanded }">
            <ArrowDown/>
          </el-icon>
        </div>

        <transition name="submenu">
          <div v-show="isSmartTagExpanded" class="submenu-items">
            <div
                class="menu-item submenu-item"
                :class="{ active: activeMenu === 'tag-search' }"
                @click="handleMenuSelect('tag-search')"
            >
              <el-icon>
                <Search/>
              </el-icon>
              <span v-show="!isCollapsed">태그 검색</span>
            </div>

            <div
                class="menu-item submenu-item"
                :class="{ active: activeMenu === 'tag-proc-step' }"
                @click="handleMenuSelect('tag-proc-step')"
            >
              <el-icon>
                <Document/>
              </el-icon>
              <span v-show="!isCollapsed">처리단계 관리</span>
            </div>

            <div
                class="menu-item submenu-item"
                :class="{ active: activeMenu === 'tag-setting' }"
                @click="handleMenuSelect('tag-setting')"
            >
              <el-icon>
                <Setting/>
              </el-icon>
              <span v-show="!isCollapsed">세팅정보 관리</span>
            </div>

            <div
                class="menu-item submenu-item"
                :class="{ active: activeMenu === 'tag-version' }"
                @click="handleMenuSelect('tag-version')"
            >
              <el-icon>
                <Histogram/>
              </el-icon>
              <span v-show="!isCollapsed">버전 이력 관리</span>
            </div>

            <div
                class="menu-item submenu-item"
                :class="{ active: activeMenu === 'tag-common' }"
                @click="handleMenuSelect('tag-common')"
            >
              <el-icon>
                <Collection/>
              </el-icon>
              <span v-show="!isCollapsed">공통정보 관리</span>
            </div>

            <div
                class="menu-item submenu-item"
                :class="{ active: activeMenu === 'tag-as' }"
                @click="handleMenuSelect('tag-as')"
            >
              <el-icon>
                <Tools/>
              </el-icon>
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
          <el-icon>
            <Collection/>
          </el-icon>
          <span v-show="!isCollapsed">시스템</span>
          <el-icon v-show="!isCollapsed" class="expand-icon" :class="{ 'rotated': isSystemExpanded }">
            <ArrowDown/>
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
              <el-icon>
                <User/>
              </el-icon>
              <span v-show="!isCollapsed">사용자 관리</span>
            </div>

            <div
                v-if="userAcl >= 3"
                class="menu-item submenu-item"
                :class="{ active: activeMenu === 'dept-management' }"
                @click="handleMenuSelect('dept-management')"
            >
              <el-icon>
                <OfficeBuilding/>
              </el-icon>
              <span v-show="!isCollapsed">부서 관리</span>
            </div>

            <div
                class="menu-item submenu-item"
                :class="{ active: activeMenu === 'log-management' }"
                @click="handleMenuSelect('log-management')"
            >
              <el-icon>
                <DocumentCopy/>
              </el-icon>
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
import '../css/VUE/Sidebar.css'
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
const isProjectExpanded = ref(false)
const isDailyExpanded = ref(false)
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
function toggleProjectMenu(){
  isProjectExpanded.value = !isProjectExpanded.value
}
function toggleDailyMenu(){
  isDailyExpanded.value = !isDailyExpanded.value
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
  } else if(groupName === 'project'){
    toggleProjectMenu()
  } else if(groupName === 'daily'){
    toggleDailyMenu()
  }
}

function handleMenuSelect(key) {
  emit('menu-select', key)
}
</script>


