import {createRouter, createWebHistory} from 'vue-router'
import UserManagement from '../views/System/User/UserManagement.vue'
import TagManagement from '../views/tag/TagManagement.vue'
import LogManagement from '../views/System/Log/LogManagement.vue'
import DeptManagement from '../views/System/Dept/DeptManagement.vue'
import LoginFrom from '../views/System/User/Login/LoginForm.vue'
import DailyReportManagement from "../views/DailyReport/dailyReportManagement.vue";
import ProjectManagement from "../views/Project/ProjectManagement.vue";

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: LoginFrom
  },
  {
    path: '/',
    redirect: '/user-management',
    meta: { requiresAuth: true }
  },
  {
    path: '/user-management',
    name: 'UserManagement',
    component: UserManagement,
    meta: { requiresAuth: true }
  },
  {
    path: '/tag-management',
    name: 'TagManagement',
    component: TagManagement
  },
  // 태그번호 없이 접근할 수 있는 서브메뉴 경로들
  {
    path: '/tag-management/proc-step',
    name: 'TagProcStepSearch',
    component: TagManagement,
    props: { subMenu: 'tag-proc-step' },
    meta: { requiresAuth: true }
  },
  {
    path: '/tag-management/setting',
    name: 'TagSettingSearch',
    component: TagManagement,
    props: { subMenu: 'tag-setting' },
    meta: { requiresAuth: true }
  },
  {
    path: '/tag-management/version',
    name: 'TagVersionSearch',
    component: TagManagement,
    props: { subMenu: 'tag-version' },
    meta: { requiresAuth: true }
  },
  {
    path: '/tag-management/common',
    name: 'TagCommonSearch',
    component: TagManagement,
    props: { subMenu: 'tag-common' },
    meta: { requiresAuth: true }
  },
  {
    path: '/tag-management/as',
    name: 'TagAsSearch',
    component: TagManagement,
    props: { subMenu: 'tag-as' },
    meta: { requiresAuth: true }
  },
  // 기존 태그번호가 있는 경로들
  {
    path: '/tag-management/proc-step/:ordNo',
    name: 'TagProcStep',
    component: TagManagement,
    props: true,
    meta: { requiresAuth: true }
  },
  {
    path: '/tag-management/setting/:ordNo',
    name: 'TagSetting',
    component: TagManagement,
    props: true,
    meta: { requiresAuth: true }
  },
  {
    path: '/tag-management/version/:ordNo',
    name: 'TagVersion',
    component: TagManagement,
    props: true,
    meta: { requiresAuth: true }
  },
  {
    path: '/tag-management/common/:ordNo',
    name: 'TagCommon',
    component: TagManagement,
    props: true,
    meta: { requiresAuth: true }
  },
  {
    path: '/tag-management/as/:ordNo',
    name: 'TagAs',
    component: TagManagement,
    props: true,
    meta: { requiresAuth: true }
  },
  {
    path: '/log-management',
    name: 'LogManagement',
    component: LogManagement,
    meta: { requiresAuth: true }
  },
  {
    path: '/dept-management',
    name: 'DeptManagement',
    component: DeptManagement,
    meta: { requiresAuth: true }
  },
  {
    path: '/daily-management',
    name: 'DailyManagement',
    component: DailyReportManagement,
    meta: {requiresAuth: true}
  },
  {
    path: '/project-management',
    name: 'ProjectManagement',
    component: ProjectManagement,
    meta: {requiresAuth: true}
  }
]

const router = createRouter({
  history: createWebHistory('/'),
  routes
})

router.beforeEach((to, from, next) => {
  // 보안을 위해 토큰은 httpOnly 쿠키로 관리되므로
  // 클라이언트에서는 사용자 정보만으로 로그인 상태 판단
  const userInfo = sessionStorage.getItem('user_info')
  console.log('[Router] userInfo:', userInfo)
  console.log('[Router] to.path:', to.path, 'requiresAuth:', to.meta.requiresAuth)

  if (to.meta.requiresAuth && !userInfo) {
    console.warn('[Router] 로그인 안됨. /login으로 리디렉션')
    next('/login')
  } else if (to.path === '/login' && userInfo) {
    console.warn('[Router] 이미 로그인됨. 홈(/)으로 리디렉션')
    next('/')
  } else {
    next()
  }
})

export default router 