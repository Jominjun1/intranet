<template>
  <div class="login-bg-bizon" :style="{ backgroundImage: `linear-gradient(rgba(0, 0, 0, 0.3), rgba(0, 0, 0, 0.3)), url(${loginBg})` }">
    <div class="login-text-bizon">
      <h1>기업의 지속가능한 성장을 위해<br>디지털 혁신을 완성한다</h1>
      <p>새로운 방식의 업무문화 혁신은 효율적인 시간활용으로<br>낭비되는 시간비용은 감소시키고<br>업무 효율성과 생산성은 증가되도록 도와줍니다.</p>
    </div>
    <div class="login-card-bizon">
      <div class="login-header-bizon">
        <!-- <img src="/logo.svg" alt="로고" class="login-logo-bizon" /> -->
        <h2>TEIA INTRANET</h2>
      </div>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" class="login-form-bizon">
        <el-form-item prop="login_id">
          <el-input
            v-model="loginForm.login_id"
            placeholder="아이디를 입력하세요"
            prefix-icon="el-icon-user"
            size="large"
            clearable
            class="input-bizon"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="비밀번호를 입력하세요"
            prefix-icon="el-icon-lock"
            size="large"
            show-password
            clearable
            class="input-bizon"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            @click="handleLogin"
            :loading="loading"
            class="login-btn-bizon"
            style="width: 100%"
          >
            로그인
          </el-button>
        </el-form-item>
        <div class="login-options-row">
          <el-checkbox 
            v-model="rememberId" 
            class="remember-id-checkbox"
            @change="handleRememberIdChange"
          >
            아이디 저장
          </el-checkbox>
          <div class="login-links-bizon">
            <el-button type="link" @click="showFindId = true">아이디 찾기</el-button>
            <el-button type="link" @click="showFindPassword = true">비밀번호 찾기</el-button>
          </div>
        </div>
      </el-form>
      <div class="login-footer-bizon">
        <span>ⓒ 2025 by mmin0307</span>
      </div>
    </div>
    <!-- 아이디 찾기 모달 -->
    <el-dialog v-model="showFindId" title="아이디 찾기" width="400px">
      <el-form :model="findIdForm" label-width="100">
        <el-form-item label="이름">
          <el-input v-model="findIdForm.user_name" placeholder="이름을 입력하세요" />
        </el-form-item>
        <el-form-item label="이메일">
          <el-input v-model="findIdForm.user_email" placeholder="이메일을 입력하세요" />
        </el-form-item>
        <el-form-item label="전화번호">
          <el-input v-model="findIdForm.user_phone_num" placeholder="전화번호를 입력하세요" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showFindId = false">취소</el-button>
          <el-button type="primary" @click="findId">아이디 찾기</el-button>
        </span>
      </template>
    </el-dialog>
    <!-- 비밀번호 찾기 모달 -->
    <el-dialog v-model="showFindPassword" title="비밀번호 찾기" width="400px">
      <el-form :model="findPasswordForm" label-width="100">
        <el-form-item label="이름">
          <el-input v-model="findPasswordForm.user_name" placeholder="이름을 입력하세요" />
        </el-form-item>
        <el-form-item label="아이디">
          <el-input v-model="findPasswordForm.login_id" placeholder="아이디를 입력하세요" />
        </el-form-item>
        <el-form-item label="새 비밀번호">
          <el-input v-model="findPasswordForm.password" type="password" placeholder="새 비밀번호를 입력하세요" show-password />
        </el-form-item>
        <el-form-item label="이메일">
          <el-input v-model="findPasswordForm.user_email" placeholder="이메일을 입력하세요" />
        </el-form-item>
        <el-form-item label="전화번호">
          <el-input v-model="findPasswordForm.user_phone_num" placeholder="전화번호를 입력하세요" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showFindPassword = false">취소</el-button>
          <el-button type="primary" @click="findPassword">비밀번호 찾기</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {onMounted, reactive, ref} from 'vue'
import {ElMessage} from 'element-plus'
import axios from 'axios'
import loginBg from '../image/login_img.jpg'
import '../css/LoginForm.css'

const emit = defineEmits(['login-success'])
const loginForm = reactive({ login_id: '', password: '' })
const findIdForm = reactive({ user_name: '', user_email: '', user_phone_num: '' })
const findPasswordForm = reactive({ user_name: '', login_id: '', password: '', user_email: '', user_phone_num: '' })
const loading = ref(false)
const showFindId = ref(false)
const showFindPassword = ref(false)
const loginFormRef = ref()
const rememberId = ref(false)

const rules = {
  login_id: [{ required: true, message: '아이디를 입력하세요', trigger: 'blur' }],
  password: [{ required: true, message: '비밀번호를 입력하세요', trigger: 'blur' }]
}

// 저장된 아이디 불러오기
onMounted(() => {
  const savedId = localStorage.getItem('saved_login_id')
  const savedRememberId = localStorage.getItem('remember_id')
  
  if (savedId && savedRememberId === 'true') {
    loginForm.login_id = savedId
    rememberId.value = true
  }
})

// 아이디 저장 처리
function handleRememberIdChange(checked) {
  if (checked) {
    // 체크박스가 체크되면 현재 입력된 아이디를 저장
    if (loginForm.login_id.trim()) {
      localStorage.setItem('saved_login_id', loginForm.login_id.trim())
      localStorage.setItem('remember_id', 'true')
    }
  } else {
    // 체크박스가 해제되면 저장된 아이디 삭제
    localStorage.removeItem('saved_login_id')
    localStorage.removeItem('remember_id')
  }
}

async function handleLogin() {
  if (!loginFormRef.value) return
  try {
    await loginFormRef.value.validate()
    loading.value = true
    
    const response = await axios.post('/user/login', loginForm)
    
    if (response.data) {
      // 로그인 성공 시 아이디 저장 처리
      if (rememberId.value && loginForm.login_id.trim()) {
        localStorage.setItem('saved_login_id', loginForm.login_id.trim())
        localStorage.setItem('remember_id', 'true')
      } else if (!rememberId.value) {
        localStorage.removeItem('saved_login_id')
        localStorage.removeItem('remember_id')
      }
      
      sessionStorage.setItem('jwt_token', response.data.token)
      sessionStorage.setItem('user_info', JSON.stringify({
        user_id: response.data.user_id,
        user_name: response.data.user_name,
        login_id: response.data.login_id,
        user_acl: response.data.user_acl,
        user_email: response.data.user_email
      }))
      
      ElMessage.success('로그인 성공')
      emit('login-success', response.data)
    }
  } catch (error) {
    console.error('로그인 오류:', error)
    console.error('오류 응답:', error.response?.data)
    if (error.response?.status === 401) {
      ElMessage.error('아이디 또는 비밀번호가 일치하지 않습니다.')
    } else if (error.response?.status === 403) {
      ElMessage.error('계정이 잠겨있습니다. 관리자에게 문의하세요.')
    } else {
      ElMessage.error('로그인 중 오류가 발생했습니다.')
    }
  } finally {
    loading.value = false
  }
}

async function findId() {
  try {
    const response = await axios.get('/user/findID', { 
      params: findIdForm 
    })
    if (response.data) {
      ElMessage.success(`찾은 아이디: ${response.data.loginId}`)
    } else {
      ElMessage.warning('일치하는 정보가 없습니다.')
    }
    showFindId.value = false
  } catch (error) {
    ElMessage.error('아이디 찾기 중 오류가 발생했습니다.')
  }
}

async function findPassword() {
  try {
    const response = await axios.put('/user/findPassword', findPasswordForm)
    if (response.data) {
      ElMessage.success(`찾은 비밀번호: ${response.data.password}`)
    } else {
      ElMessage.warning('일치하는 정보가 없습니다.')
    }
    showFindPassword.value = false
  } catch (error) {
    ElMessage.error('비밀번호 찾기 중 오류가 발생했습니다.')
  }
}
</script> 