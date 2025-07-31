<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import LoginForm from './components/LoginForm.vue'
import Header from './components/Header.vue'
import { ElMessage } from 'element-plus'
import TagManagement from './components/TagManagement.vue'
import UserManagement from './components/UserManagement.vue'
import LogManagement from './components/LogManagement.vue'
import './css/App.css'

// 상태 관리
const isLoggedIn = ref(false)
const activeMenu = ref('tag-management')
const userInfo = ref({})
const userAcl = computed(() => parseInt(userInfo.value.user_acl || '0'))

// 창 이벤트 핸들러
function handleBeforeUnload() {
  // 새로고침 감지를 위해 localStorage에 플래그 설정
  localStorage.setItem('isReloading', 'true')
}

// 로그인 성공 처리
function handleLoginSuccess(userData) {
  if (userData.user_acl === '0') {
    sessionStorage.removeItem('jwt_token')
    sessionStorage.removeItem('user_info')
    isLoggedIn.value = false
    userInfo.value = {}
    ElMessage.warning('계정이 잠겨있습니다. 관리자에게 문의하세요.')
    return
  }
  userInfo.value = userData
  isLoggedIn.value = true
  activeMenu.value = 'tag-management'
}

function handleMenuSelect(key) {
  console.log('App.vue handleMenuSelect 호출됨:', key)
  // 태그 관리 관련 메뉴들은 모두 TagManagement 컴포넌트로 라우팅
  if (key.startsWith('tag-')) {
    console.log('태그 서브메뉴 선택됨:', key)
    activeMenu.value = 'tag-management'
    // TagManagement 컴포넌트에 서브메뉴 정보 전달
    sessionStorage.setItem('tagSubMenu', key)
    console.log('sessionStorage에 저장됨:', key)
  } else {
    activeMenu.value = key
  }
}

function handleUserCommand(command) {
  switch (command) {
    case 'profile':
      ElMessage.info('프로필 메뉴를 선택했습니다.')
      break
    case 'settings':
      ElMessage.info('사용자 설정 메뉴를 선택했습니다.')
      break
    case 'logout':
      logout()
      break
  }
}

function logout() {
  sessionStorage.removeItem('jwt_token')
  sessionStorage.removeItem('user_info')
  isLoggedIn.value = false
  userInfo.value = {}
  activeMenu.value = 'tag-management'
  ElMessage.success('로그아웃되었습니다.')
}

onMounted(() => {
  // 이벤트 리스너 등록
  window.addEventListener('beforeunload', handleBeforeUnload)
  
  // 새로고침 여부 확인
  const isReloading = localStorage.getItem('isReloading')
  if (isReloading) {
    // 새로고침인 경우 토큰 유지
    localStorage.removeItem('isReloading')
  } else {
    // 새로고침이 아닌 경우 (새 탭, 직접 URL 접근 등) 토큰 삭제
    // 단, 이미 토큰이 있는 경우는 유지 (이미 로그인된 상태)
    const existingToken = sessionStorage.getItem('jwt_token')
    if (!existingToken) {
      sessionStorage.removeItem('jwt_token')
      sessionStorage.removeItem('user_info')
    }
  }
  
  const token = sessionStorage.getItem('jwt_token')
  const storedUserInfo = sessionStorage.getItem('user_info')
  
  if (token && storedUserInfo) {
    try {
      userInfo.value = JSON.parse(storedUserInfo)
      
      if (userInfo.value.user_acl === '0') {
        sessionStorage.removeItem('jwt_token')
        sessionStorage.removeItem('user_info')
        isLoggedIn.value = false
        userInfo.value = {}
        ElMessage.warning('계정이 잠겨있습니다. 관리자에게 문의하세요.')
        return
      }
      isLoggedIn.value = true
    } catch (error) {
      console.error('사용자 정보 파싱 오류:', error)
      sessionStorage.removeItem('jwt_token')
      sessionStorage.removeItem('user_info')
    }
  }
})

onUnmounted(() => {
  // 이벤트 리스너 제거
  window.removeEventListener('beforeunload', handleBeforeUnload)
})
</script>

<template>
  <LoginForm v-if="!isLoggedIn" @login-success="handleLoginSuccess"/>
  <div v-else class="app-container">
    <TagManagement v-if="activeMenu === 'tag-management'" 
      :user-info="userInfo" 
      :active-menu="activeMenu"
      @menu-select="handleMenuSelect"
      @user-command="handleUserCommand"
    />
    <UserManagement v-else-if="activeMenu === 'user-management'" 
      :user-info="userInfo" 
      :active-menu="activeMenu"
      @menu-select="handleMenuSelect"
      @user-command="handleUserCommand"
    />
    <LogManagement v-else-if="activeMenu === 'log-management'" 
      :user-info="userInfo" 
      :active-menu="activeMenu"
      @menu-select="handleMenuSelect"
      @user-command="handleUserCommand"
    />
    <div v-else-if="activeMenu === 'admin'" class="admin-page">
      <Header 
        :active-menu="activeMenu" 
        :user-info="userInfo"
        @menu-select="handleMenuSelect"
        @user-command="handleUserCommand"
      />
      <div class="admin-content">
        <h2>관리자 페이지</h2>
        <p>관리자 기능은 추후 구현 예정입니다.</p>
      </div>
    </div>
  </div>
</template>
