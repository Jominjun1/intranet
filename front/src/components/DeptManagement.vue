<template>
  <div class="dept-management">
    <div class="page-header">
      <h1>부서 관리</h1>
      <p class="page-description">부서 정보를 등록, 수정, 삭제할 수 있습니다.</p>
    </div>

    <div class="search-section">
      <div class="search-controls">
        <div class="search-group">
          <label>부서명</label>
          <el-input
            v-model="searchForm.dept"
            placeholder="부서명을 입력하세요"
            clearable
            @keyup.enter="searchDepts"
          />
        </div>
        <div class="search-group">
          <label>부서코드</label>
          <el-input
            v-model="searchForm.deptCode"
            placeholder="부서코드를 입력하세요"
            clearable
            @keyup.enter="searchDepts"
          />
        </div>
        <div class="search-group">
          <label>상태</label>
          <el-select v-model="searchForm.status" placeholder="상태 선택" clearable>
            <el-option label="전체" :value="'all'" />
            <el-option label="사용중" :value="'Y'" />
            <el-option label="삭제됨" :value="'N'" />
          </el-select>
        </div>
        <div class="search-actions">
          <el-button type="primary" @click="searchDepts" :loading="loading">
            <el-icon><Search /></el-icon>
            검색
          </el-button>
          <el-button @click="resetSearch">
            <el-icon><Refresh /></el-icon>
            초기화
          </el-button>
        </div>
      </div>
    </div>

    <div class="action-section">
      <el-button type="primary" @click="showAddDeptForm" v-if="userAcl >= 3">
        <el-icon><Plus /></el-icon>
        부서 등록
      </el-button>
    </div>

    <div class="table-section">
      <el-table
        :data="displayedDepts"
        v-loading="loading"
        stripe
        border
        resizable
        style="width: 100%"
        empty-text="등록된 부서가 없습니다."
      >
        <el-table-column prop="deptCode" label="부서코드" width="120" align="center" resizable />
        <el-table-column prop="dept" label="부서명" min-width="200" align="center" resizable />
        <el-table-column prop="status" label="상태" width="100" align="center" resizable>
          <template #default="scope">
            <el-tag :type="scope.row.status === 'Y' ? 'success' : 'danger'">
              {{ scope.row.status === 'Y' ? '사용중' : '삭제됨' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="등록자" width="120" align="center" resizable />
        <el-table-column prop="regDt" label="등록일" width="180" align="center" resizable>
          <template #default="scope">
            {{ formatDate(scope.row.regDt) }}
          </template>
        </el-table-column>
        <el-table-column prop="updateDt" label="수정일" width="180" align="center" resizable>
          <template #default="scope">
            {{ formatDate(scope.row.updateDt) }}
          </template>
        </el-table-column>
        <el-table-column label="관리" width="180" fixed="right" v-if="userAcl >= 3" align="center" resizable>
          <template #default="scope">
            <div class="action-buttons">
              <el-button
                type="primary"
                size="small"
                @click="editDept(scope.row)"
              >
                <el-icon><Edit /></el-icon>
                수정
              </el-button>
              <el-button
                type="danger"
                size="small"
                @click="deleteDept(scope.row)"
              >
                <el-icon><Delete /></el-icon>
                삭제
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog
      v-model="showDeptForm"
      :title="isEditMode ? '부서 수정' : '부서 등록'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="deptFormRef"
        :model="deptForm"
        :rules="deptFormRules"
        label-width="100px"
      >
        <el-form-item label="부서코드" prop="deptCode">
          <el-input
            v-model="deptForm.deptCode"
            placeholder="부서코드를 입력하세요"
            :disabled="isEditMode"
          />
        </el-form-item>
        <el-form-item label="부서명" prop="dept">
          <el-input
            v-model="deptForm.dept"
            placeholder="부서명을 입력하세요"
          />
        </el-form-item>
        <el-form-item label="상위부서">
          <el-select
            v-model="deptForm.parentDeptCode"
            placeholder="상위부서를 선택하세요"
            clearable
            filterable
          >
            <el-option
              v-for="d in parentOptions"
              :key="d.deptCode"
              :label="`${d.dept} (${d.deptCode})`"
              :value="d.deptCode"
            />
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancelDeptForm">취소</el-button>
          <el-button type="primary" @click="saveDept" :loading="saving">
            {{ isEditMode ? '수정' : '등록' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {computed, onMounted, ref} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {Delete, Edit, Plus, Refresh, Search} from '@element-plus/icons-vue'
import axios from 'axios'

const props = defineProps({
  userInfo: {
    type: Object,
    default: () => ({})
  }
})

const userAcl = computed(() => parseInt(props.userInfo.user_acl || '0'))

const loading = ref(false)
const saving = ref(false)
const depts = ref([])
const parentOptions = ref([])
const showDeptForm = ref(false)
const isEditMode = ref(false)
const deptFormRef = ref()

const searchForm = ref({
  dept: '',
  deptCode: '',
  status: 'all'
})
const displayedDepts = computed(() => {
  const name = (searchForm.value.dept || '').trim()
  const code = (searchForm.value.deptCode || '').trim()
  const stat = searchForm.value.status || 'all'

  return depts.value.filter(row => {
    const nameOk = name ? (row.dept || '').includes(name) : true
    const codeOk = code ? (row.deptCode || '').includes(code) : true
    const statusOk = stat === 'all' ? true : ((row.status || '').toUpperCase() === stat)
    return nameOk && codeOk && statusOk
  })
})


const deptForm = ref({
  deptCode: '',
  dept: '',
  parentDeptCode: ''
})

const deptFormRules = {
  deptCode: [
    { required: true, message: '부서코드를 입력하세요', trigger: 'blur' },
    { min: 2, max: 10, message: '부서코드는 2-10자 사이여야 합니다', trigger: 'blur' }
  ],
  dept: [
    { required: true, message: '부서명을 입력하세요', trigger: 'blur' },
    { min: 2, max: 50, message: '부서명은 2-50자 사이여야 합니다', trigger: 'blur' }
  ]
}

async function loadDepts() {
  loading.value = true
  try {
    console.log('부서 목록 조회 시작...')
    const response = await axios.get('/user/getDeptList')
    console.log('부서 목록 응답:', response)
    
    let responseData = response.data
    
    if (responseData && typeof responseData === 'object' && responseData.body !== undefined) {
      responseData = responseData.body
    }
    
    if (Array.isArray(responseData)) {
      depts.value = responseData
      parentOptions.value = responseData
    } else {
      console.warn('부서 데이터가 배열이 아닙니다:', responseData)
      depts.value = []
    }
    
    console.log('부서 목록 데이터:', depts.value)
    
    if (depts.value.length === 0) {
      ElMessage.info('등록된 부서가 없습니다.')
    }
  } catch (error) {
    console.error('부서 목록 조회 오류:', error)
    console.error('오류 응답:', error.response)
    ElMessage.error(`부서 목록을 불러오는데 실패했습니다: ${error.response?.data || error.message}`)
    depts.value = []
  } finally {
    loading.value = false
  }
}

async function searchDepts() {
  loading.value = true
  try {
    // TODO: 부서 검색 API 구현 필요
    // const response = await axios.get('/Admin/getDept', {
    //   params: {
    //     dept: searchForm.value.dept,
    //     deptCode: searchForm.value.deptCode
    //   }
    // })
    
    ElMessage.success('검색 기능은 준비 중입니다.')
    await loadDepts()
  } catch (error) {
    console.error('부서 검색 오류:', error)
    ElMessage.error('부서 검색에 실패했습니다.')
  } finally {
    loading.value = false
  }
}

function resetSearch() {
  searchForm.value = {
    dept: '',
    deptCode: '',
    status: 'all'
  }
  loadDepts()
}

function showAddDeptForm() {
  isEditMode.value = false
  deptForm.value = {
    deptCode: '',
    dept: '',
    parentDeptCode: ''
  }
  showDeptForm.value = true
}

function editDept(dept) {
  isEditMode.value = true
  deptForm.value = {
    deptCode: dept.deptCode,
    dept: dept.dept,
    parentDeptCode: dept.parentDept?.deptCode || ''
  }
  showDeptForm.value = true
}

async function saveDept() {
  if (!deptFormRef.value) return
  
  try {
    await deptFormRef.value.validate()
    saving.value = true
    
    if (isEditMode.value) {
      // 부서 수정
      await axios.put(`/Admin/changeDept/${deptForm.value.deptCode}`, {
        dept: deptForm.value.dept,
        status: 'Y',
        parentDeptCode: deptForm.value.parentDeptCode || ''
      })
      ElMessage.success('부서 정보가 수정되었습니다.')
    } else {
      // 부서 등록
      await axios.post('/Admin/createDept', {
        deptCode: deptForm.value.deptCode,
        dept: deptForm.value.dept,
        regDt: new Date(),
        parentDeptCode: deptForm.value.parentDeptCode || ''
      })
      ElMessage.success('부서가 등록되었습니다.')
    }
    
    showDeptForm.value = false
    await loadDepts()
  } catch (error) {
    console.error('부서 저장 오류:', error)
    ElMessage.error('부서 저장에 실패했습니다.')
  } finally {
    saving.value = false
  }
}

function cancelDeptForm() {
  showDeptForm.value = false
  deptForm.value = {
    deptCode: '',
    dept: ''
  }
}

async function deleteDept(dept) {
  try {
    await ElMessageBox.confirm(
      `'${dept.dept}' 부서를 삭제하시겠습니까?`,
      '부서 삭제 확인',
      {
        confirmButtonText: '삭제',
        cancelButtonText: '취소',
        type: 'warning'
      }
    )
    
    await axios.put(`/Admin/changeDept/${dept.deptCode}`, {
      dept: dept.dept,
      status: 'N'
    })
    
    ElMessage.success('부서가 삭제되었습니다.')
    await loadDepts()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('부서 삭제 오류:', error)
      ElMessage.error('부서 삭제에 실패했습니다.')
    }
  }
}

function formatDate(dateString) {
  if (!dateString) return '-'
  try {
    const date = new Date(dateString)
    return date.toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    })
  } catch (e) {
    return dateString
  }
}

onMounted(() => {
  loadDepts()
})
</script>

<style scoped>
.dept-management {
  padding: 0;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  margin: 0 0 8px 0;
  font-size: 1.5rem;
  font-weight: 600;
  color: #1f2937;
}

.page-description {
  margin: 0;
  color: #6b7280;
  font-size: 0.9rem;
}

.search-section {
  background: #ffffff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.search-controls {
  display: flex;
  gap: 16px;
  align-items: end;
  flex-wrap: wrap;
}

.search-group {
  flex: 1;
  min-width: 200px;
}

.search-group label {
  display: block;
  margin-bottom: 4px;
  font-weight: 500;
  color: #374151;
  font-size: 0.9rem;
}

.search-actions {
  display: flex;
  gap: 8px;
}

.action-section {
  margin-bottom: 20px;
}

.table-section {
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.action-buttons {
  display: flex;
  gap: 8px;
  align-items: center;
  justify-content: center;
  flex-wrap: nowrap;
}

:deep(.el-table .cell) {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  padding: 8px 12px;
}

:deep(.el-table .el-table__cell) {
  padding: 0;
}

:deep(.el-table .cell:hover) {
  white-space: normal;
  word-break: break-all;
  background-color: #f5f7fa;
  z-index: 10;
  position: relative;
}

/* 테이블 통합 스타일 */
:deep(.el-table) {
  table-layout: auto !important;
  font-size: 14px;
  width: 100% !important;
}

/* 테이블 컨테이너 전체 너비 사용 */
:deep(.el-table__header-wrapper),
:deep(.el-table__body-wrapper) {
  width: 100% !important;
}

/* 테이블 헤더와 바디 너비 맞춤 */
:deep(.el-table__header table),
:deep(.el-table__body table) {
  width: 100% !important;
  table-layout: auto !important;
}

/* 컬럼 너비 자동 조정 */
:deep(.el-table__header th),
:deep(.el-table__body td) {
  min-width: auto !important;
  max-width: none !important;
}

/* 셀 스타일 통일 */
:deep(.el-table .cell) {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  padding: 8px 12px;
  text-align: center;
}

:deep(.el-table .el-table__header .cell) {
  white-space: nowrap;
  font-weight: bold;
  color: #303133;
  background-color: #f5f7fa;
  text-align: center;
}

:deep(.el-table .el-table__body .cell) {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  text-align: center;
}

/* 테이블 행 호버 효과 */
:deep(.el-table__row:hover > td) {
  background-color: #f0f9ff !important;
}

/* 테이블 경계선 스타일 */
:deep(.el-table--border) {
  border: 1px solid #dcdfe6;
}

:deep(.el-table--border td, .el-table--border th) {
  border-right: 1px solid #dcdfe6;
}

/* 스트라이프 행 색상 */
:deep(.el-table--striped .el-table__body tr.el-table__row--striped td) {
  background-color: #fafafa;
}

@media (max-width: 768px) {
  .search-controls {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-group {
    min-width: auto;
  }
  
  .search-actions {
    justify-content: stretch;
  }
  
  .search-actions .el-button {
    flex: 1;
  }
}
</style>
