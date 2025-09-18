<script setup>
import {computed, onMounted, onUnmounted, ref} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import LoginForm from './components/LoginForm.vue'
import Header from './components/Header.vue'
import Sidebar from './components/Sidebar.vue'
import {ElMessage} from 'element-plus'
import './css/App.css'

// 라우터 설정
const router = useRouter()
const route = useRoute()

// 상태 관리
const isLoggedIn = ref(false)
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
  // 보안을 위해 최소한의 사용자 정보만 저장
  const { accessToken, refreshToken, ...pureUserData } = userData
  userInfo.value = pureUserData
  
  // 사용자 정보를 암호화하거나 최소한으로만 저장
  const safeUserInfo = {
    user_name: pureUserData.user_name,
    user_acl: pureUserData.user_acl,
    user_id: pureUserData.user_id
  }
  sessionStorage.setItem('user_info', JSON.stringify(safeUserInfo))
  isLoggedIn.value = true
  router.push('/tag-management')
}

function handleMenuSelect(key) {
  console.log('App.vue handleMenuSelect 호출됨:', key)
  // 라우터를 사용해서 페이지 이동
  switch (key) {
    case 'tag-management':
    case 'tag-search':
      router.push('/tag-management')
      break
    case 'user-management':
      router.push('/user-management')
      break
    case 'log-management':
      router.push('/log-management')
      break
    case 'dept-management':
      router.push('/dept-management')
      break
    default:
      if (key.startsWith('tag-')) {
        // 태그 서브메뉴는 태그 검색처럼 바로 해당 페이지로 이동
        const subMenuMap = {
          'tag-proc-step': 'proc-step',
          'tag-setting': 'setting',
          'tag-version': 'version',
          'tag-common': 'common',
          'tag-as': 'as'
        }
        const subPath = subMenuMap[key]
        if (subPath) {
          const targetUrl = `/tag-management/${subPath}`
          console.log('라우터 이동:', targetUrl)
          router.push(targetUrl)
        }
      }
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
  // 쿠키 기반 인증 - 사용자 정보만 삭제, 토큰은 백엔드에서 처리
  sessionStorage.removeItem('user_info')
  isLoggedIn.value = false
  userInfo.value = {}
  router.push('/tag-management')
  ElMessage.success('로그아웃되었습니다.')
}

onMounted(() => {
  // 이벤트 리스너 등록
  window.addEventListener('beforeunload', handleBeforeUnload)
  
  // 쿠키 기반 인증으로 변경 - 토큰은 httpOnly 쿠키로 관리됨
  
  // 새로고침 여부 확인
  const isReloading = localStorage.getItem('isReloading')
  if (isReloading) {
    // 새로고침인 경우 사용자 정보 유지
    localStorage.removeItem('isReloading')
  } else {
    // 새로고침이 아닌 경우 (새 탭, 직접 URL 접근 등) 사용자 정보 삭제
    const existingUserInfo = sessionStorage.getItem('user_info')
    if (!existingUserInfo) {
      sessionStorage.removeItem('user_info')
    }
  }
  
  // 보안을 위해 토큰은 httpOnly 쿠키로 관리되므로
  // 클라이언트에서는 사용자 정보만으로 로그인 상태 판단
  const storedUserInfo = sessionStorage.getItem('user_info')
  
  if (storedUserInfo) {
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
    <!-- Header -->
    <Header
      :active-menu="route.name"
      :user-info="userInfo"
      @menu-select="handleMenuSelect"
      @user-command="handleUserCommand"
    />

    <!-- Main content area -->
    <div class="main-content">
      <!-- Sidebar -->
      <Sidebar
        :active-menu="route.name"
        :user-info="userInfo"
        @menu-select="handleMenuSelect"
      />

      <!-- Content area -->
      <div class="content-area">
        <router-view
          :user-info="userInfo"
          :active-menu="route.name"
          @menu-select="handleMenuSelect"
          @user-command="handleUserCommand"
        />
      </div>
    </div>
  </div>
</template>
