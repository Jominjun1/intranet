import {createRouter, createWebHistory} from 'vue-router'
import UserManagement from '../components/UserManagement.vue'
import TagManagement from '../components/TagManagement.vue'
import LogManagement from '../components/LogManagement.vue'

const routes = [
  {
    path: '/',
    redirect: '/user-management'
  },
  {
    path: '/user-management',
    name: 'UserManagement',
    component: UserManagement
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
    props: { subMenu: 'tag-proc-step' }
  },
  {
    path: '/tag-management/setting',
    name: 'TagSettingSearch',
    component: TagManagement,
    props: { subMenu: 'tag-setting' }
  },
  {
    path: '/tag-management/version',
    name: 'TagVersionSearch',
    component: TagManagement,
    props: { subMenu: 'tag-version' }
  },
  {
    path: '/tag-management/common',
    name: 'TagCommonSearch',
    component: TagManagement,
    props: { subMenu: 'tag-common' }
  },
  {
    path: '/tag-management/as',
    name: 'TagAsSearch',
    component: TagManagement,
    props: { subMenu: 'tag-as' }
  },
  // 기존 태그번호가 있는 경로들
  {
    path: '/tag-management/proc-step/:ordNo',
    name: 'TagProcStep',
    component: TagManagement,
    props: true
  },
  {
    path: '/tag-management/setting/:ordNo',
    name: 'TagSetting',
    component: TagManagement,
    props: true
  },
  {
    path: '/tag-management/version/:ordNo',
    name: 'TagVersion',
    component: TagManagement,
    props: true
  },
  {
    path: '/tag-management/common/:ordNo',
    name: 'TagCommon',
    component: TagManagement,
    props: true
  },
  {
    path: '/tag-management/as/:ordNo',
    name: 'TagAs',
    component: TagManagement,
    props: true
  },
  {
    path: '/log-management',
    name: 'LogManagement',
    component: LogManagement
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router 