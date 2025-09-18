import {createApp} from 'vue'
import './style.css'
import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import axios from 'axios'
import router from './router'


axios.defaults.baseURL = 'http://localhost:8080'
axios.interceptors.request.use(
  (config) => {
    // 쿠키 기반 인증 - withCredentials로 쿠키 자동 전송
    config.withCredentials = true
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

axios.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    if (error.response?.status === 401) {
      // 쿠키 기반 인증 - 사용자 정보만 삭제, 토큰은 백엔드에서 처리
      sessionStorage.removeItem('user_info')
      window.location.reload()
    }
    return Promise.reject(error)
  }
)

const app = createApp(App)
app.use(ElementPlus)
app.use(router)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.mount('#app')
