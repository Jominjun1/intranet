<template>
  <div class="user-management-page">
    <!-- 헤더 -->
    <Header 
      :active-menu="activeMenu" 
      :user-info="userInfo"
      @menu-select="handleMenuSelect"
      @user-command="handleUserCommand"
    />
    
    <!-- 검색 영역 -->
    <div class="search-section">
      <div class="search-header">
        <h3>사용자 검색</h3>
        <el-button 
          type="info" 
          :icon="QuestionFilled" 
          circle 
          size="small"
          @click="toggleHelp"
          title="사용자 관리 도움말"
        />
      </div>
      <el-form :inline="true" class="search-form">
        <el-form-item label="이름">
          <el-input v-model="searchName" placeholder="이름 입력" clearable />
        </el-form-item>
        <el-form-item label="로그인ID">
          <el-input v-model="searchLoginId" placeholder="로그인ID 입력" clearable />
        </el-form-item>
        <el-form-item label="부서코드">
          <el-input v-model="searchDeptCd" placeholder="부서코드 입력" clearable />
        </el-form-item>
        <el-form-item label="권한">
          <el-select v-model="searchAcl" placeholder="권한 선택" clearable style="width: 150px;">
            <el-option label="전체" :value="''" />
            <el-option label="운영자" :value="'4'" />
            <el-option label="시스템관리자" :value="'3'" />
            <el-option label="관리자" :value="'2'" />
            <el-option label="일반사용자" :value="'1'" />
            <el-option label="잠금" :value="'0'" />
          </el-select>
        </el-form-item>
        <el-form-item label="상태">
          <el-select v-model="searchStat" placeholder="상태 선택" clearable style="width: 150px;">
            <el-option label="전체" value="" />
            <el-option label="활성" value="ACTIVE" />
            <el-option label="대기" value="PENDING" />
            <el-option label="잠금" value="LOCK" />
            <el-option label="비활성" value="INACTIVE" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="doSearch">검색</el-button>
          <el-button @click="resetSearch">초기화</el-button>
          <el-button type="success" @click="addUser">
            <el-icon><Plus /></el-icon>
            사용자 추가
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 테이블 영역 -->
    <div class="table-section">
      <el-table :data="paginatedData" style="width:100%" v-loading="loading" :key="tableKey">
        <el-table-column prop="userId" label="사용자ID" width="80" />
        <el-table-column prop="userName" label="이름" width="100" />
        <el-table-column prop="loginId" label="로그인ID" width="100" />
        <el-table-column prop="userEmail" label="이메일" width="180" />
        <el-table-column prop="userPhoneNum" label="전화번호" width="120" />
        <el-table-column prop="dept_cd" label="부서코드" width="80" />
        <el-table-column prop="user_job" label="직책" width="80" />
        <el-table-column prop="user_acl" label="권한" width="80">
          <template #default="{ row }">
            <el-tag :type="getAclType(row.user_acl)">
              {{ getAclText(row.user_acl) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="user_stat" label="상태" width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.user_stat)">
              {{ getStatusLabel(row.user_stat) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="hire_dt" label="입사일" width="100">
          <template #default="{ row }">
            {{ formatDate(row.hire_dt) }}
          </template>
        </el-table-column>
        <el-table-column prop="change_password_dt" label="비밀번호 변경일" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.change_password_dt) }}
          </template>
        </el-table-column>
        <el-table-column prop="login_dt" label="로그인일시" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.login_dt) }}
          </template>
        </el-table-column>
        <el-table-column prop="update_dt" label="수정일시" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.update_dt) }}
          </template>
        </el-table-column>
        <el-table-column prop="update_id" label="수정한사람" width="100">
          <template #default="{ row }">
            {{ row.update_id || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="작업" width="120">
          <template #default="{ row }">
            <el-button size="small" @click="editUser(row)">수정</el-button>
            <el-button size="small" type="danger" @click="deleteUser(row)">삭제</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 페이지네이션 -->
      <div class="pagination-section" v-if="users.length > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="users.length"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 도움말 사이드바 -->
    <div class="help-sidebar" :class="{ 'show': showHelp }">
      <div class="sidebar-header">
        <h3>사용자 관리 도움말</h3>
        <el-button type="link"
          :icon="Close" 
          @click="showHelp = false"
          class="close-btn"
        />
      </div>
      <div class="help-content">
        <div class="help-section">
          <h4>🔍 검색 기능</h4>
          <ul>
            <li><strong>이름:</strong> 사용자의 이름으로 검색할 수 있습니다.</li>
            <li><strong>로그인ID:</strong> 사용자의 로그인 ID로 검색할 수 있습니다.</li>
            <li><strong>부서코드:</strong> 사용자의 부서 코드로 검색할 수 있습니다.</li>
            <li><strong>권한:</strong> 관리자, 일반사용자, 잠금 상태로 필터링할 수 있습니다.</li>
            <li><strong>상태:</strong> 활성, 비활성 상태로 필터링할 수 있습니다.</li>
          </ul>
        </div>
        
        <div class="help-section">
          <h4>👥 권한 레벨</h4>
          <ul>
            <li><strong>운영자 (4):</strong> 최고 관리자 권한</li>
            <li><strong>시스템관리자 (3):</strong> 시스템 개발 및 관리 권한</li>
            <li><strong>관리자 (2):</strong> 관리자 기능 일부 사용 가능</li>
            <li><strong>일반사용자 (1):</strong> 기본 기능 사용 가능한 일반 권한</li>
            <li><strong>잠금 (0):</strong> 로그인이 차단된 상태</li>
          </ul>
        </div>
        
        <div class="help-section">
          <h4>📊 사용자 상태</h4>
          <ul>
            <li><strong>활성 (ACTIVE/PENDING):</strong> 정상적으로 사용 가능한 상태</li>
            <li><strong>잠금 (LOCK):</strong> 로그인 실패로 인한 자동 잠금</li>
            <li><strong>비활성 (INACTIVE):</strong> 사용이 제한된 상태</li>
          </ul>
        </div>
        
        <div class="help-section">
          <h4>💡 사용 팁</h4>
          <ul>
            <li>검색 조건을 입력하지 않으면 모든 사용자가 표시됩니다.</li>
            <li>여러 조건을 조합하여 정확한 검색이 가능합니다.</li>
            <li>사용자 추가 버튼으로 새로운 사용자를 등록할 수 있습니다.</li>
            <li>수정 버튼으로 사용자 정보, 권한, 상태, 비밀번호를 모두 변경할 수 있습니다.</li>
            <li>삭제 버튼으로 사용자를 영구적으로 삭제할 수 있습니다.</li>
          </ul>
        </div>
      </div>
    </div>

    <!-- 사용자 등록/수정 모달 -->
    <el-dialog v-model="showAddUserForm" :title="isEditMode ? '사용자 수정' : '사용자 등록'" width="600px">
      <el-form :model="userForm" ref="userFormRef" label-width="120">
        <el-form-item label="이름" prop="user_name">
          <el-input v-model="userForm.user_name" placeholder="이름을 입력하세요" />
        </el-form-item>
        <el-form-item label="영문이름" prop="user_en_name">
          <el-input v-model="userForm.user_en_name" placeholder="영문이름을 입력하세요" />
        </el-form-item>
        <el-form-item label="로그인ID" prop="login_id">
          <el-input v-model="userForm.login_id" placeholder="로그인ID를 입력하세요"/>
        </el-form-item>
        <el-form-item label="비밀번호" prop="password" v-if="!isEditMode">
          <el-input v-model="userForm.password" type="password" placeholder="비밀번호를 입력하세요" />
        </el-form-item>
        <el-form-item label="비밀번호 변경" v-if="isEditMode">
          <el-input 
            v-model="userForm.password" 
            type="password" 
            placeholder="변경할 비밀번호를 입력하세요 (변경하지 않으려면 비워두세요)"
            show-password
          />
        </el-form-item>
        <el-form-item label="이메일" prop="user_email">
          <el-input v-model="userForm.user_email" placeholder="이메일을 입력하세요" />
        </el-form-item>
        <el-form-item label="전화번호" prop="user_phone_num">
          <el-input v-model="userForm.user_phone_num" placeholder="전화번호를 입력하세요" />
        </el-form-item>
        <el-form-item label="부서코드" prop="dept_cd">
          <el-input v-model="userForm.dept_cd" placeholder="부서코드를 입력하세요" />
        </el-form-item>
        <el-form-item label="직책" prop="user_job">
          <el-input v-model="userForm.user_job" placeholder="직책을 입력하세요" />
        </el-form-item>
        <el-form-item label="권한" prop="user_acl">
          <el-select v-model="userForm.user_acl" placeholder="권한을 선택하세요">
            <el-option label="운영자" :value="4" />
            <el-option label="시스템관리자" :value="3" />
            <el-option label="관리자" :value="2" />
            <el-option label="일반사용자" :value="1" />
            <el-option label="잠금" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="상태" prop="user_stat">
          <el-select v-model="userForm.user_stat" placeholder="상태를 선택하세요">
            <el-option label="활성" value="ACTIVE" />
            <el-option label="대기" value="PENDING" />
            <el-option label="잠금" value="LOCK" />
            <el-option label="비활성" value="INACTIVE" />
          </el-select>
        </el-form-item>
        <el-form-item label="입사일" prop="hire_dt">
          <el-date-picker v-model="userForm.hire_dt" type="date" placeholder="입사일을 선택하세요" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showAddUserForm = false">취소</el-button>
          <el-button type="primary" @click="saveUser">저장</el-button>
        </div>
      </template>
    </el-dialog>


  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { QuestionFilled, Close, Plus } from '@element-plus/icons-vue'

// Props
const props = defineProps({
  userInfo: {
    type: Object,
    default: () => ({})
  },
  activeMenu: {
    type: String,
    default: 'user-management'
  }
})

// Emits
const emit = defineEmits(['menu-select', 'user-command'])

// 상태 관리
const loading = ref(false)
const users = ref([])
const showAddUserForm = ref(false)
const isEditMode = ref(false)
const userFormRef = ref()
const showHelp = ref(false)
const tableKey = ref(0)

// 검색 및 페이지네이션 상태
const searchName = ref('')
const searchLoginId = ref('')
const searchDeptCd = ref('')
const searchAcl = ref('')
const searchStat = ref('')
const currentPage = ref(1)
const pageSize = ref(20)

// 페이지네이션된 데이터
const paginatedData = computed(() => {
  const startIndex = (currentPage.value - 1) * pageSize.value
  const endIndex = startIndex + pageSize.value
  return filteredData.value.slice(startIndex, endIndex)
})

// 필터링된 데이터
const filteredData = computed(() => {
  return users.value.filter(user => {
    const matchesName = searchName.value ? user.userName?.includes(searchName.value) : true
    const matchesLoginId = searchLoginId.value ? user.loginId?.includes(searchLoginId.value) : true
    const matchesDeptCd = searchDeptCd.value ? user.dept_cd?.includes(searchDeptCd.value) : true
    const matchesAcl = searchAcl.value ? user.user_acl === searchAcl.value : true
    const matchesStat = searchStat.value ? user.user_stat === searchStat.value : true
    return matchesName && matchesLoginId && matchesDeptCd && matchesAcl && matchesStat
  })
})

// 사용자 폼
const userForm = ref({
  user_name: '',
  user_en_name: '',
  login_id: '',
  password: '',
  user_email: '',
  user_phone_num: '',
  dept_cd: '',
  user_job: '',
  user_acl: 1,
  user_stat: 'ACTIVE',
  hire_dt: null
})

// 권한별 라벨과 타입
const getAclText = (acl) => {
  // 문자열을 숫자로 변환
  const aclNum = parseInt(acl)
  const labels = { 
    0: '잠금', 
    1: '일반사용자', 
    2: '관리자', 
    3: '시스템관리자',
    4: '운영자'
  }
  return labels[aclNum] || '알수없음'
}

const getAclType = (acl) => {
  // 문자열을 숫자로 변환
  const aclNum = parseInt(acl)
  const types = { 
    0: 'danger', 
    1: 'info', 
    2: 'warning', 
    3: 'success',
    4: 'primary'
  }
  return types[aclNum] || 'info'
}

// 상태별 라벨과 타입
const getStatusLabel = (status) => {
  const labels = { 
    'ACTIVE': '활성', 
    'PENDING': '활성', 
    'LOCK': '잠금',
    'INACTIVE': '비활성'
  }
  return labels[status] || '알수없음'
}

const getStatusType = (status) => {
  const types = { 
    'ACTIVE': 'success', 
    'PENDING': 'success', 
    'LOCK': 'danger',
    'INACTIVE': 'info'
  }
  return types[status] || 'info'
}

// 날짜 포맷팅
const formatDate = (dateString) => {
  if (!dateString) return '-'
  try {
    const date = new Date(dateString)
    return date.toLocaleDateString('ko-KR')
  } catch (e) {
    return dateString
  }
}

// 날짜시간 포맷팅
const formatDateTime = (dateString) => {
  if (!dateString) return '-'
  try {
    const date = new Date(dateString)
    return date.toLocaleString('ko-KR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    })
  } catch (e) {
    return dateString
  }
}

// 사용자 목록 조회
async function loadUsers() {
  loading.value = true
  try {
    const token = sessionStorage.getItem("jwt_token"); 
    if (!token) {
      console.error("JWT 토큰이 없습니다.");
      return;
    }
    const response = await axios.get('/Admin/all-user' , { headers: { Authorization: `Bearer ${token}`}})
    console.log("서버 응답 데이터:", response.data)
    
    // 원본 데이터 그대로 사용 (매핑 제거)
    users.value = response.data.body || []
    
    // 사용자 데이터 디버깅
    if (users.value.length > 0) {
      console.log("첫 번째 사용자 데이터:", users.value[0])
      console.log("hire_dt 값:", users.value[0].hire_dt, "타입:", typeof users.value[0].hire_dt)
      console.log("모든 키:", Object.keys(users.value[0]))
    }
    
    // 테이블 키 업데이트로 강제 재렌더링
    tableKey.value++
    

  } catch (error) {
    console.error('사용자 목록 조회 오류:', error)
    ElMessage.error('사용자 목록을 불러오는 중 오류가 발생했습니다.')
  } finally {
    loading.value = false
  }
}

// 권한 선택 변경 이벤트
function onAclChange(value) {
  console.log('권한 선택 변경:', value, '타입:', typeof value)
}

// 검색 및 페이지네이션 로직
function doSearch() {
  currentPage.value = 1 // 페이지 초기화
  ElMessage.success('검색이 완료되었습니다.')
}

function resetSearch() {
  searchName.value = ''
  searchLoginId.value = ''
  searchDeptCd.value = ''
  searchAcl.value = ''
  searchStat.value = ''
  currentPage.value = 1
  ElMessage.info('검색 조건이 초기화되었습니다.')
}

function handleSizeChange(val) {
  pageSize.value = val
  currentPage.value = 1
}

function handleCurrentChange(val) {
  currentPage.value = val
}

// 사용자 추가
function addUser() {
  isEditMode.value = false
  resetUserForm()
  showAddUserForm.value = true
}

// 사용자 삭제
async function deleteUser(user) {
  try {
    // 디버깅을 위한 로그
    console.log('삭제할 사용자 데이터:', user)
    console.log('loginId 값:', user.loginId, '타입:', typeof user.loginId)
    console.log('login_id 값:', user.login_id, '타입:', typeof user.login_id)
    
    // login_id 값 사용
    const loginId = user.login_id
    
    // loginId가 null이거나 undefined인 경우 처리
    if (!loginId) {
      ElMessage.error('로그인 ID가 없어 삭제할 수 없습니다.')
      return
    }
    
    await ElMessageBox.confirm(
      `정말 사용자 "${user.user_name}" (${loginId})을(를) 삭제하시겠습니까?`,
      '사용자 삭제 확인',
      {
        confirmButtonText: '삭제',
        cancelButtonText: '취소',
        type: 'warning'
      }
    )
    
    const token = sessionStorage.getItem('jwt_token')
    await axios.delete(`/Admin/deleteUser/${loginId}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    
    ElMessage.success('사용자가 삭제되었습니다.')
    loadUsers() // 사용자 목록 새로고침
  } catch (error) {
    if (error !== 'cancel') {
      console.error('사용자 삭제 오류:', error)
      ElMessage.error('사용자 삭제 중 오류가 발생했습니다.')
    }
  }
}

// 사용자 수정
function editUser(user) {
  isEditMode.value = true
  userForm.value = {
    user_id: user.user_id,
    user_name: user.user_name || '',  user_en_name: user.user_en_name || '',
    login_id: user.login_id || '',    password: '', // 비밀번호는 빈 값으로 초기화
    user_email: user.user_email || '',    user_phone_num: user.user_phone_num || '',
    dept_cd: user.dept_cd || '',    user_job: user.user_job || '',
    user_acl: user.user_acl || 1,    user_stat: user.user_stat || 'ACTIVE',
    hire_dt: user.hire_dt || null
  }
  showAddUserForm.value = true
}

// 사용자 저장
async function saveUser() {
  if (!userFormRef.value) return
  
  try {
    await userFormRef.value.validate()
    
    if (isEditMode.value) {
      // 수정
      const token = sessionStorage.getItem('jwt_token')
      
      // 비밀번호가 입력된 경우 비밀번호 변경 API 호출
      if (userForm.value.password && userForm.value.password.trim() !== '') {
        await axios.put(`/Admin/changePassword/${userForm.value.login_id}`, {
          password: userForm.value.password
        }, {
          headers: { Authorization: `Bearer ${token}` }
        })
        ElMessage.success('비밀번호가 변경되었습니다.')
      }
      
      // 사용자 정보 수정 (비밀번호 제외)
      const userDataForUpdate = { ...userForm.value }
      delete userDataForUpdate.password // 비밀번호는 별도로 처리했으므로 제거
      
      await axios.put(`/Admin/update/${userForm.value.user_id}`, userDataForUpdate, {
        headers: { Authorization: `Bearer ${token}` }
      })
      ElMessage.success('사용자 정보가 수정되었습니다.')
    } else {
      // 등록
      const token = sessionStorage.getItem('jwt_token')
      await axios.post('/Admin/createUser', userForm.value, {
        headers: { Authorization: `Bearer ${token}` }
      })
      ElMessage.success('사용자가 등록되었습니다.')
    }
    
    showAddUserForm.value = false
    isEditMode.value = false
    resetUserForm()
    loadUsers()
  } catch (error) {
    console.error('사용자 저장 오류:', error)
    ElMessage.error('사용자 저장 중 오류가 발생했습니다.')
  }
}

// 사용자 폼 초기화
function resetUserForm() {
  userForm.value = {
    user_name: '',  user_en_name: '',
    login_id: '',    password: '',
    user_email: '',    user_phone_num: '',
    dept_cd: '',    user_job: '',
    user_acl: 1,    user_stat: 'ACTIVE',
    hire_dt: null
  }
  isEditMode.value = false
}





// 도움말 토글
function toggleHelp() {
  console.log('도움말 버튼 클릭됨')
  console.log('현재 showHelp 값:', showHelp.value)
  showHelp.value = !showHelp.value
  console.log('변경된 showHelp 값:', showHelp.value)
}

// 헤더 이벤트 핸들러
function handleMenuSelect(key) {
  emit('menu-select', key)
}

function handleUserCommand(command) {
  emit('user-command', command)
}

// 컴포넌트 마운트 시 사용자 목록 로드
onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.user-management-page {
  width: 100%;
  height: 100%;
  padding: 0;
  background: #f8f9fa;
  overflow: auto;
  margin: 0;
  box-sizing: border-box;
}

.search-section {
  margin-bottom: 20px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.search-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.search-header h3 {
  margin: 0;
  color: #2c3e50;
  font-size: 1.2rem;
  font-weight: 600;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  align-items: flex-end;
}

.search-form .el-form-item {
  margin-bottom: 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 0;
}

/* 테이블 스타일 개선 */
.table-section {
  margin-top: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

/* 테이블 헤더 중앙 정렬 */
:deep(.el-table th) {
  text-align: center !important;
}

:deep(.el-table th .cell) {
  text-align: center !important;
}

:deep(.el-table__header-wrapper .el-table__header th) {
  text-align: center !important;
}

:deep(.el-table__header-wrapper .el-table__header th .cell) {
  text-align: center !important;
}

:deep(.el-table) {
  table-layout: fixed !important;
  width: 100% !important;
}

:deep(.el-table__header-wrapper) {
  width: 100% !important;
}

:deep(.el-table__header) {
  width: 100% !important;
}

:deep(.el-table__body-wrapper) {
  width: 100% !important;
}

:deep(.el-table__row) {
  width: 100% !important;
}

:deep(.el-scrollbar__view) {
  width: 100% !important;
}

:deep(.el-table__body) {
  width: 100% !important;
}

:deep(.el-table__inner-wrapper) {
  width: 100% !important;
}

:deep(.el-table__fixed) {
  width: 100% !important;
}

:deep(.el-table__fixed-right) {
  width: 100% !important;
}

:deep(.el-table__fixed-header-wrapper) {
  width: 100% !important;
}

:deep(.el-table__fixed-body-wrapper) {
  width: 100% !important;
}

:deep(.el-table__body-wrapper) {
  text-align: center !important;
}

:deep(.el-table__header-wrapper) {
  text-align: center !important;
}

:deep(.el-table th) {
  text-align: center !important;
  padding: 8px 0 !important;
}

:deep(.el-table td) {
  text-align: center !important;
  padding: 8px 0 !important;
}

:deep(.el-table .cell) {
  text-align: center !important;
  padding: 8px 0 !important;
}

:deep(.el-table td) {
  text-align: center !important;
}

:deep(.el-table td .cell) {
  text-align: center !important;
}

/* 테이블 컨테이너 반응형 */
.table-section {
  margin-top: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow-x: auto; /* 가로 스크롤 추가 */
  width: 100%;
}

/* 테이블 최소 너비 설정 */
:deep(.el-table) {
  min-width: 1200px; /* 테이블 최소 너비 */
}

.pagination-section {
  display: flex;
  justify-content: center;
  padding: 20px;
  background: white;
  border-top: 1px solid #eee;
}

:deep(.el-button) {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

:deep(.el-button:hover) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

:deep(.el-tag) {
  border-radius: 6px;
  font-weight: 500;
  border: none;
}

:deep(.el-input) {
  border-radius: 8px;
}

:deep(.el-select) {
  border-radius: 8px;
}

/* 도움말 사이드바 스타일 */
.help-sidebar {
  position: fixed;
  top: 0;
  right: -400px;
  width: 400px;
  height: 100vh;
  background: white;
  box-shadow: -4px 0 20px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  transition: right 0.3s ease;
  overflow-y: auto;
  padding: 20px;
}

.help-sidebar.show {
  right: 0;
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.sidebar-header h3 {
  margin: 0;
  color: #2c3e50;
  font-size: 1.3rem;
  font-weight: 600;
}

.close-btn {
  font-size: 18px;
  color: #666;
}

.close-btn:hover {
  color: #409eff;
}

.help-content {
  padding: 0;
}

.help-section {
  margin-bottom: 25px;
}

.help-section h4 {
  color: #2c3e50;
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 2px solid #f0f0f0;
}

.help-section ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.help-section li {
  padding: 8px 0;
  color: #555;
  line-height: 1.5;
  border-bottom: 1px solid #f9f9f9;
}

.help-section li:last-child {
  border-bottom: none;
}

.help-section strong {
  color: #2c3e50;
  font-weight: 600;
}
</style> 