<script setup>
import {computed, onMounted, ref} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import LoginForm from './views/System/User/Login/LoginForm.vue'
import Header from './components/Header.vue'
import Sidebar from './components/Sidebar.vue'
import {ElMessage} from 'element-plus'
import './css/VUE/App.css'
import './css/VUE/Common.css'

// =================================================================
// 1. 상태 및 라우터 설정
// =================================================================
const router = useRouter()
const route = useRoute()

const isLoggedIn = ref(false)
const userInfo = ref({})
const userAcl = computed(() => parseInt(userInfo.value.user_acl || '0'))

// =================================================================
// 2. 이벤트 핸들러 및 함수
// =================================================================

// 사용자 정보 정리 및 세션에 저장 (로그인 성공 시)
function storeUserInfo(userData) {
  // 최소한의 사용자 정보만 저장
  const safeUserInfo = {
    user_name: userData.user_name,
    user_acl: userData.user_acl,
    user_id: userData.user_id
  }
  sessionStorage.setItem('user_info', JSON.stringify(safeUserInfo))
  userInfo.value = safeUserInfo
  isLoggedIn.value = true
}

// 로그인 성공 처리
function handleLoginSuccess(userData) {
  if (userData.user_acl === '0') {
    // 계정 잠금 처리
    sessionStorage.removeItem('user_info')
    isLoggedIn.value = false
    userInfo.value = {}
    ElMessage.warning('계정이 잠겨있습니다. 관리자에게 문의하세요.')
    return
  }

  // 토큰 (httpOnly 쿠키)을 제외한 사용자 정보 저장
  storeUserInfo(userData)
  router.push('/daily-management')
}

// 메뉴 선택 처리 (라우팅)
function handleMenuSelect(key) {
  console.log('App.vue handleMenuSelect 호출됨:', key)

  // 단순 매핑을 통해 코드를 간소화
  const pathMap = {
    'tag-management': '/tag-management',
    'tag-search': '/tag-management',
    'user-management': '/user-management',
    'log-management': '/log-management',
    'dept-management': '/dept-management',
    'project-search': '/project-management',
    'project-step': '/project-management',
    'project-management': '/project-management',
    'daily-list': '/daily-management',
    'daily-step': '/daily-management',
    'dailyReport-management': '/daily-management',
    'CommonMenu': '/CommonMenu',
    // tag- 서브메뉴 처리
    'tag-proc-step': '/tag-management/proc-step',
    'tag-setting': '/tag-management/setting',
    'tag-version': '/tag-management/version',
    'tag-common': '/tag-management/common',
    'tag-as': '/tag-management/as',
  }

  const targetPath = pathMap[key]
  if (targetPath) {
    router.push(targetPath)
  }
}

// 사용자 드롭다운 커맨드 처리
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

// 로그아웃 처리
function logout() {
  // 쿠키 기반 인증: 클라이언트에서는 사용자 정보만 삭제
  sessionStorage.removeItem('user_info')
  isLoggedIn.value = false
  userInfo.value = {}
  router.push('/tag-management') // 또는 '/login'
  ElMessage.success('로그아웃되었습니다.')
}


// =================================================================
// 3. 라이프사이클 훅
// =================================================================

// ⚠️ 기존의 복잡한 새로고침/새 탭 구분 로직 (localStorage)을 제거하고
// sessionStorage에 정보가 있으면 로그인 상태를 유지하도록 단순화합니다.
onMounted(() => {
  const storedUserInfo = sessionStorage.getItem('user_info')

  if (storedUserInfo) {
    try {
      const parsedInfo = JSON.parse(storedUserInfo)
      userInfo.value = parsedInfo

      if (parsedInfo.user_acl === '0') {
        // 계정 잠금 처리
        sessionStorage.removeItem('user_info')
        isLoggedIn.value = false
        userInfo.value = {}
        ElMessage.warning('계정이 잠겨있습니다. 관리자에게 문의하세요.')
        return
      }
      isLoggedIn.value = true
    } catch (error) {
      console.error('사용자 정보 파싱 오류:', error)
      sessionStorage.removeItem('user_info')
      isLoggedIn.value = false
    }
  }
})

</script>

<template>
  <LoginForm v-if="!isLoggedIn" @login-success="handleLoginSuccess"/>
  <div v-else class="app-container">
    <Header
        :active-menu="route.name"
        :user-info="userInfo"
        @menu-select="handleMenuSelect"
        @user-command="handleUserCommand"
    />

    <div class="main-content">
      <Sidebar
          :active-menu="route.name"
          :user-info="userInfo"
          @menu-select="handleMenuSelect"
      />

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