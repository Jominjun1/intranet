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
      <SearchProject
          v-model="doSearch"
          :loading="loading"
          @search="loadProject"
          @reset="resetSearch"
      />
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
import {computed, ref,} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import axios from 'axios'
import '../../css/Tag/TagManagement.css'
import {ElMessage} from 'element-plus'
import {
  Close,
  QuestionFilled
} from '@element-plus/icons-vue'
import SearchProject from "../Common/SearchProject.vue";
const showSearchHelp = ref(false)

// Emits
const emit = defineEmits(['menu-select', 'user-command'])

// 사용자 정보
const userInfo = computed(() => props.userInfo)
const userAcl = computed(() => parseInt(userInfo.value.user_acl || 0))

// 테이블 데이터
const tableData = ref([])
const loading = ref(false)

// 페이지네이션 상태
const currentPage = ref(1)
const pageSize = ref(10)

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
async function doSearch(form) {
  if (!form.searchProject && !form.searchProjectPM && !form.searchDelFilter) {
    ElMessage.warning('검색 조건을 하나 이상 입력해주세요.')
    return
  }

  loading.value = true
  try {
    const res = await axios.get('/project/searchAll', { params: form })
    tableData.value = res.data.body ?? []
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


</script>