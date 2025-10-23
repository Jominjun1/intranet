<template>
  <div class="search-section">
    <div class="search-header">
      <h3>🔍 프로젝트 목록</h3>
      <el-button type="info" :icon="QuestionFilled" circle size="small" @click="showSearchHelp" title="프로젝트 관리 도움말"/>
    </div>
    <el-form :inline="true" class="search-form">
      <SearchProject v-model="doSearch" :loading="loading" @search="loadProject" @reset="resetSearch"/>
    </el-form>

  </div>
  <div v-if="tableData.length >0">
    <h3>검색된 프로젝트 목록</h3>
    <el-table class ="projectList" :data="paginatedData" style="width:100%" v-loading="loading" border stripe resizable :table-layout="'auto'">
    </el-table>
    <el-table-column prop="project_seq" label="번호" width="70" align="center" resizable />
    <el-table-column prop="projectCode" label="프로젝트 코드" width="150" align="center" resizable />
    <el-table-column prop="project_name" label="프로젝트 명" width="150" align="center" resizable />
    <el-table-column prop="project_leader" label="프로젝트 PM" width="150" align="center" resizable />
    <el-table-column prop="project_category" label="프로젝트 유형" width="150" align="center" resizable />
    <el-table-column prop="project_status" label="삭제여부" width="150" align="center" resizable />
    <el-table-column prop="customer" label="고객사" width="150" align="center" resizable />
    <el-table-column prop="region" label="지역" width="150" align="center" resizable />
    <el-table-column prop="deptCd" label="담당 부서" width="150" align="center" resizable />
    <el-table-column prop="project_ing" label="프로젝트 진행 상황" width="150" align="center" resizable />
    <el-table-column prop="createDt" label="생성일" width="150" align="center" resizable />
    <el-table-column prop="create_id" label="생성자" width="150" align="center" resizable />
    <el-table-column prop="updateDt" label="수정일" width="150" align="center" resizable />
    <el-table-column prop="update_id" label="수정자" width="150" align="center" resizable />
    <el-table-column prop="startDt" label="계약날짜" width="150" align="center" resizable />
    <el-table-column prop="endDt" label="종료날짜" width="150" align="center" resizable />

  </div>

  <!-- 검색 도움말 사이드바 -->
  <div class="search-help-sidebar" :class="{ 'show': showSearchHelp }">
    <div class="sidebar-header">
      <h3>🔍 검색 도움말</h3>
      <el-button type="text" :icon="Close" @click="showHelp = false" class="close-btn"/>
    </div>
    <div class="search-help-content">
      <p>다음 조건 중 <strong>하나 이상</strong>을 입력하여 검색할 수 있습니다:</p>

      <div class="help-item">
        <h5>📱 프로젝트 명</h5>
        <p>프로젝트 이름을 입력하세요. (예: 테이아 인트라넷 컨버젼)</p>
      </div>

      <div class="help-item">
        <h5>🔢 프로젝트 PM</h5>
        <p>프로젝트 PM을 입력하세요. (예: 조민준)</p>
      </div>

      <div class="help-item">
        <h5>🗑️ 삭제여부</h5>
        <p>전체/사용중/삭제됨 중 선택하여 필터링할 수 있습니다.</p>
      </div>

      <el-alert title="💡 팁" description="검색 조건을 입력하지 않으면 데이터가 표시되지 않습니다. 정확한 검색을 위해 가능한 많은 정보를 입력해주세요."
          type="success" :closable="false" show-icon/>
    </div>
  </div>
  <!-- 페이지네이션 -->
  <Pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :total="tableData.length"
      @current-change="handleCurrentChange"
      @size-change="handleSizeChange"
  />

  <el-dialog v-model="showAddProjectForm" :title="isEditMode ? '프로젝트 수정' : '프로젝트 등록'" width="600px">
    <el-form :model="projectForm" ref="projectFormRef" label-width="120">
      <el-form-item label="프로젝트 코드" prop="projectCode">
        <el-input v-model="projectForm.projectCode" placeholder="프로젝트 코드를 입력하세요." />
      </el-form-item>
      <el-form-item label="프로젝트 명" prop="projectName">
        <el-input v-model="projectForm.projectName" placeholder="프로젝트 명을 입력하세요" />
      </el-form-item>
      <el-form-item label="고객사" prop="customer">
        <el-input v-model="projectForm.customer" placeholder="고객사를 선택하세요."/>
      </el-form-item>
      <el-form-item label="부서" prop="deptCd">
        <div class="dept-input-group">
          <el-input v-model="projectForm.deptCd" placeholder="부서명을 선택하세요" readonly />
          <el-button type="primary" @click="openDeptModal">
            <el-icon><Search /></el-icon>
            부서 선택
          </el-button>
        </div>
      </el-form-item>
      <el-form-item label="직책" prop="user_job">
        <el-input v-model="projectForm.user_job" placeholder="직책을 입력하세요" />
      </el-form-item>
      <el-form-item label="상태" prop="user_stat">
        <el-select v-model="projectForm.user_stat" placeholder="상태를 선택하세요">
          <el-option label="활성" value="ACTIVE" />
          <el-option label="대기" value="PENDING" />
          <el-option label="잠금" value="LOCK" />
          <el-option label="비활성" value="INACTIVE" />
          <el-option label="삭제" value="N" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button type="primary" @click="saveUser">저장</el-button>
        <el-button @click="showAddUserForm = false">취소</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import {computed, ref,} from 'vue'
import '../../../css/Tag/TagManagement.css'
import '../../../css/Project/ProjectManagement.css'
import {ElMessage} from 'element-plus'
import {Close, QuestionFilled, Search} from '@element-plus/icons-vue'
import SearchProject from "../../Common/SearchProject.vue";
import ProjectManagement from './ProjectManagement.js'
import Pagination from "../../Common/Pagination.vue";

const showSearchHelp = ref(false)
const showAddProjectForm = ref(false)
const showDeptModal = ref(false)
const isEditMode = ref(false)
const tableData = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
// Emits
const emit = defineEmits(['menu-select', 'user-command'])

// 사용자 정보
const userInfo = computed(() => props.userInfo)
const userAcl = computed(() => parseInt(userInfo.value.user_acl || 0))

const paginatedData = computed(() => {
  const startIndex = (currentPage.value - 1) * pageSize.value
  const endIndex = startIndex + pageSize.value
  const result = tableData.value.slice(startIndex, endIndex)
  return result
})

// 프로젝트 폼
const projectForm = ref({
  projectCode: '', project_name: '', project_leader: '', project_category: '',
  project_status: '', customer: '', region: '', deptCd: '', project_ing: '', createDt: null,
  create_id: 'ACTIVE', updateDt: null, update_id: 'ACTIVE', startDt: null, endDt: null,
})


// 페이지네이션 이벤트 핸들러
function handleSizeChange(size) {
  pageSize.value = size
  currentPage.value = 1 // 페이지 크기가 변경되면 첫 페이지로 이동
}

function handleCurrentChange(page) {
  currentPage.value = page
}
async function doSearch(form) {
  if (!form.searchProject && !form.searchProjectPM && !form.searchDelFilter) {
    ElMessage.warning('검색 조건을 하나 이상 입력해주세요.')
    return
  }

  loading.value = true
  try {
    const data = await ProjectManagement.searchProjects(form)
    tableData.value = data
  } catch (error) {
    console.error(error)
    ElMessage.error('검색 중 오류가 발생했습니다.')
  } finally {
    loading.value = false
  }
}

function resetSearch() {
  searchForm.value = { searchProject: '', searchProjectPM: '', searchDelFilter: 'all' }
  tableData.value = []
}

function addProject(){
  isEditMode.value = false
  resetProjectForm()
  showAddProjectForm.value = true;
}

// 사용자 폼 초기화
function resetProjectForm() {
  projectForm.value = {
    projectCode: '', project_name: '', project_leader: '', project_category: '',
    project_status: '', customer: '', region: '', deptCd: '', project_ing: '', createDt: null,
    create_id: 'ACTIVE', updateDt: null, update_id: 'ACTIVE', startDt: null, endDt: null,
  }
  isEditMode.value = false
}
// 부서 모달 열기
async function openDeptModal() {
  showDeptModal.value = true
  await loadDeptList()
}

// 부서 목록 조회
async function loadDeptList() {
  try {
    const data = await ProjectManagement.getDeptList()
    deptList.value = data
  } catch (error) {
    console.error('부서 목록 조회 오류:', error)
    ElMessage.error('부서 목록을 불러오는데 실패했습니다.')
    deptList.value = []
  }
}
</script>