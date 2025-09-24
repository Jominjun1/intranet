<template>
  <div class="search-section">
    <div class="search-header">
      <h3>🔍 프로젝트 목록</h3>
      <el-button type="text" @click="showSearchHelp = !showSearchHelp">
        <el-icon><QuestionFilled /></el-icon>
        검색 도움말
      </el-button>
    </div>
    <el-form :inline="true" class="search-form">
      <el-form-item label="프로젝트">
        <el-input v-model="searchProject" placeholder="프로젝트 명 입력(예: 테이아 인트라넷)" clearable style="width: 300px;" />
      </el-form-item>
      <el-form-item label="PM">
        <el-input v-model="searchProjectPM" placeholder="PM 입력" clearable />
      </el-form-item>
      <el-form-item label="삭제여부">
        <el-select
            v-model="searchDelFilter"
            placeholder="삭제여부 선택"
            style="width: 150px;"
            clearable
        >
          <el-option
              v-for="option in delFilterOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="doSearch">검색</el-button>
        <el-button @click="resetSearch">초기화</el-button>
      </el-form-item>
    </el-form>
  </div>
  <div v-if="tableData.length >0">
    <h3>검색된 프로젝트 목록</h3>
    <el-table
        :data="paginatedData"
        style="width:100%"
        v-loading="loading"
        border
        stripe
        resizable
        :table-layout="'auto'"
        :cell-style="{ 'white-space': 'nowrap', 'text-align': 'center' }"
        :header-cell-style="{ 'white-space': 'nowrap', 'text-align': 'center', 'background-color': '#f5f7fa', 'font-weight': 'bold' }"
    >
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
      <el-button type="text"
                 :icon="Close"
                 @click="showSearchHelp = false"
                 class="close-btn"
      />
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

      <el-alert
          title="💡 팁"
          description="검색 조건을 입력하지 않으면 데이터가 표시되지 않습니다. 정확한 검색을 위해 가능한 많은 정보를 입력해주세요."
          type="success"
          :closable="false"
          show-icon
      />
    </div>
  </div>
  <div class="pagination-section" v-if="tableData.length > 0">
    <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="tableData.length"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
    />
  </div>
</template>

<script setup>
import {computed, onMounted, ref, watch} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import axios from 'axios'
import '../css/TagManagement.css'
import {ElMessage, ElMessageBox} from 'element-plus'
import {
  Close,
  Delete,
  Document,
  Edit,
  InfoFilled,
  Plus,
  QuestionFilled,
  Setting,
  Tools,
  Warning
} from '@element-plus/icons-vue'
const showSearchHelp = ref(false)

// Emits
const emit = defineEmits(['menu-select', 'user-command'])

// 라우터 설정
const router = useRouter()
const route = useRoute()

// 사용자 정보
const userInfo = computed(() => props.userInfo)
const userAcl = computed(() => parseInt(userInfo.value.user_acl || 0))

// 검색 조건
const searchProject = ref('')
const searchProjectPM = ref('')
const searchDelFilter = ref('')

// 삭제여부 옵션
const delFilterOptions = [
  { label: '전체', value: 'all' },
  { label: '사용중', value: 'active' },
  { label: '삭제됨', value: 'deleted' }
]

// 테이블 데이터
const tableData = ref([])
const loading = ref(false)

// 페이지네이션 상태
const currentPage = ref(1)
const pageSize = ref(10)
const totalTagCount = ref(0)

// 서브메뉴 관련 상태
const currentSubMenu = ref(null)

const paginatedData = computed(() => {
  const startIndex = (currentPage.value - 1) * pageSize.value
  const endIndex = startIndex + pageSize.value
  const result = tableData.value.slice(startIndex, endIndex)
  console.log('페이징 계산:', {
    currentPage: currentPage.value,
    pageSize: pageSize.value,
    totalData: tableData.value.length,
    startIndex,
    endIndex,
    resultLength: result.length
  })
  return result
})

// 페이지네이션 이벤트 핸들러
function handleSizeChange(size) {
  pageSize.value = size
  currentPage.value = 1 // 페이지 크기가 변경되면 첫 페이지로 이동
}

function handleCurrentChange(page) {
  currentPage.value = page
}
async function doSearch(){
  // 검색 조건이 없으면 경고 메시지 표시
  const hasSearchCondition = (searchProject.value && searchProject.value.trim()) ||
      (searchProjectPM.value && searchProjectPM.trim()) ||
      (searchDelFilter.value && searchDelFilter.trim())

  if (!hasSearchCondition) {
    ElMessage.warning('검색 조건을 하나 이상 입력해주세요.')
    return
  }
  loading.value= true
  try {
    const params = {}
    if (searchProject.value && searchProject.value.trim()) {
      params.project_name = searchProject.value.trim()
    }
    if (searchProjectPM.value && searchProjectPM.value.trim()) {
      params.project_leader = searchProjectPM.value.trim()
    }
    if (searchDelFilter.value && searchDelFilter.value !== 'all') {
      params.delFilter = searchDelFilter.value
    }

    const res = await axios.get('/project/searchAll', {params})

    let responseData = res.data

    // 응답이 래핑된 경우 body에서 추출
    if (responseData && typeof responseData === 'object' && responseData.body !== undefined) {
      responseData = responseData.body
    }

    // 배열이 아닌 경우 빈 배열로 설정
    tableData.value = Array.isArray(responseData) ? responseData : []

    // 검색 후 첫 페이지로 이동
    currentPage.value = 1
  }catch{
    console.error('검색 오류:', error)
    console.error('오류 응답:', error.response?.data)
    ElMessage.error('검색 중 오류가 발생했습니다.')
  } finally {
    loading.value = false
  }
}

function resetSearch() {
  searchProject.value = ''
  searchProjectPM.value = ''
  searchDelFilter.value = 'all'
  tableData.value = []
  currentPage.value = 1 // 초기화 시 첫 페이지로 이동
}

// 헤더 이벤트 핸들러
function handleMenuSelect(key) {
  emit('menu-select', key)
}

function handleUserCommand(command) {
  emit('user-command', command)
}
// 날짜 포맷팅 함수
const formatDate = (dateString) => {
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

</script>