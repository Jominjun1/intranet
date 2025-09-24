<template>
  <div class="tag-proc-step-page">
    <h2>처리단계 관리</h2>
    <p>태그 정보를 검색하여 처리단계 정보를 관리하세요.</p>

    <!-- 태그 검색 폼 -->
    <div class="search-section">
      <div class="search-header">
        <h3>🔍 태그 목록</h3>
      </div>
      <el-form :inline="true" class="search-form">
        <el-form-item label="태그번호">
          <el-input v-model="searchTagNo" placeholder="전체 태그번호 입력 (예: AABBCCDDEE01FAC001001)" clearable style="width: 300px;" />
        </el-form-item>
        <el-form-item label="MAC주소">
          <el-input v-model="searchMac" placeholder="MAC주소 입력" clearable />
        </el-form-item>
        <el-form-item label="시리얼번호">
          <el-input v-model="searchSn" placeholder="시리얼번호 입력" clearable />
        </el-form-item>
        <el-form-item label="공장코드">
          <el-input v-model="searchFacCd" placeholder="공장코드 입력" clearable />
        </el-form-item>
        <el-form-item label="삭제여부">
          <el-select v-model="searchDelFilter" placeholder="삭제여부 선택" style="width: 150px;" clearable>
            <el-option v-for="option in delFilterOptions" :key="option.value" :label="option.label" :value="option.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="doSearch">태그 검색</el-button>
          <el-button @click="resetSearch">초기화</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 태그 검색 결과 -->
    <div v-if="tableData.length > 0">
      <h3>검색된 태그 목록</h3>
      <el-table 
        :data="paginatedData" 
        style="width:100%" 
        v-loading="loading" 
        border 
        stripe
        resizable 
        @row-click="onSelectRow"
        :table-layout="'auto'"
        :cell-style="{ 'white-space': 'nowrap', 'text-align': 'center' }"
        :header-cell-style="{ 'white-space': 'nowrap', 'text-align': 'center', 'background-color': '#f5f7fa', 'font-weight': 'bold' }"
      >
        <el-table-column prop="tag_No" label="태그번호" width="150" align="center" resizable />
        <el-table-column prop="mac_Addr" label="MAC주소" width="150" align="center" resizable />
        <el-table-column prop="fac_Cd" label="공장코드" width="100" align="center" resizable />
        <el-table-column prop="fac_No" label="시리얼번호" width="120" align="center" resizable />
        <el-table-column prop="Status" label="삭제여부" width="100" align="center" resizable>
          <template #default="{ row }">
            <el-tag :type="row.Status === 'Y' ? 'danger' : 'success'">{{ row.Status === 'Y' ? '삭제됨' : '사용중' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
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
    </div>

    <!-- 처리단계 조회 결과 -->
    <div v-if="procStepData" class="result-section">
      <div class="result-header">
        <h3>처리단계 정보</h3>
      </div>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="태그번호">{{ procStepData.ordNo }}</el-descriptions-item>
        <el-descriptions-item label="입고일">{{ formatDate(procStepData.receipt_dt) }}</el-descriptions-item>
        <el-descriptions-item label="납품일">{{ formatDate(procStepData.delivery_dt) }}</el-descriptions-item>
        <el-descriptions-item label="연구소 검수일">{{ formatDate(procStepData.lab_insp_dt) }}</el-descriptions-item>
        <el-descriptions-item label="연구소 검수소견">{{ procStepData.lab_insp_desc }}</el-descriptions-item>
        <el-descriptions-item label="융합기술팀 검수일">{{ formatDate(procStepData.tech_inst_dt) }}</el-descriptions-item>
        <el-descriptions-item label="융합기술팀 검수소견">{{ procStepData.tech_inst_desc }}</el-descriptions-item>
        <el-descriptions-item label="생성일">{{ formatDate(procStepData.create_dt) }}</el-descriptions-item>
        <el-descriptions-item label="생성자">{{ procStepData.create_id }}</el-descriptions-item>
        <el-descriptions-item label="수정일">{{ formatDate(procStepData.update_dt) }}</el-descriptions-item>
        <el-descriptions-item label="수정자">{{ procStepData.update_id }}</el-descriptions-item>
      </el-descriptions>
    </div>
  </div>
</template>

<script setup>
import {computed, ref} from 'vue'
import axios from 'axios'
import {ElMessage} from 'element-plus'

const searchMac = ref('')
const searchSn = ref('')
const searchFacCd = ref('')
const searchDelFilter = ref('all')
const searchTagNo = ref('')

const delFilterOptions = [
  { label: '전체', value: 'all' },
  { label: '사용중', value: 'active' },
  { label: '삭제됨', value: 'deleted' }
]

const tableData = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const procStepData = ref(null)

const filteredData = computed(() => {
  if (!Array.isArray(tableData.value)) return []
  return tableData.value.filter(row => {
    const macMatch = searchMac.value ? row.mac_Addr?.includes(searchMac.value) : true
    const snMatch = searchSn.value ? row.fac_No?.includes(searchSn.value) : true
    const facCdMatch = searchFacCd.value ? row.fac_Cd?.includes(searchFacCd.value) : true
    return macMatch && snMatch && facCdMatch
  })
})

const paginatedData = computed(() => {
  const startIndex = (currentPage.value - 1) * pageSize.value
  const endIndex = startIndex + pageSize.value
  return filteredData.value.slice(startIndex, endIndex)
})

function handleSizeChange(size) { pageSize.value = size; currentPage.value = 1 }
function handleCurrentChange(page) { currentPage.value = page }

async function doSearch() {
  let macAddr = searchMac.value.trim()
  let facCd = searchFacCd.value.trim()
  let facNo = searchSn.value.trim()
  if (searchTagNo.value && searchTagNo.value.trim()) {
    const ordNo = searchTagNo.value.trim()
    if (ordNo.length >= 20) {
      macAddr = ordNo.substring(0, 12)
      facCd = ordNo.substring(12, 18)
      facNo = ordNo.substring(18)
    } else { macAddr = ordNo }
  }
  const has = (searchTagNo.value && searchTagNo.value.trim()) || macAddr || facCd || facNo
  if (!has) { ElMessage.warning('검색 조건을 하나 이상 입력해주세요.'); return }
  loading.value = true
  try {
    const params = {}
    if (macAddr) params.macAddr = macAddr
    if (facCd) params.facCd = facCd
    if (facNo) params.facNo = facNo
    if (searchDelFilter.value && searchDelFilter.value !== 'all') params.delFilter = searchDelFilter.value
    const res = await axios.get('/tags/getTagList', { params })
    let data = res.data
    if (data && typeof data === 'object' && data.body !== undefined) data = data.body
    tableData.value = Array.isArray(data) ? data : []
    currentPage.value = 1
    if (tableData.value.length === 0) ElMessage.info('검색 결과가 없습니다.')
  } catch (e) {
    console.error('검색 오류:', e)
    ElMessage.error('검색 중 오류가 발생했습니다.')
  } finally { loading.value = false }
}

async function onSelectRow(row) {
  try {
    const ordNo = row.ordNo || row.tag_No
    if (!ordNo) return
    const res = await axios.get(`/tags/proc_step_${ordNo}`)
    procStepData.value = res.data.body || res.data
  } catch (e) {
    console.error('처리단계 조회 오류:', e)
    ElMessage.error('처리단계 정보를 불러오는 중 오류가 발생했습니다.')
  }
}

function formatDate(dateString) {
  if (!dateString) return '-'
  try {
    const date = new Date(dateString)
    return date.toLocaleDateString('ko-KR', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
  } catch { return dateString }
}
</script>

<style scoped>
.tag-proc-step-page { padding: 16px; }
.search-section { margin-top: 12px; }
.search-header { display: flex; align-items: center; justify-content: space-between; }
.pagination-section { margin-top: 12px; display: flex; justify-content: center; }
.result-header { margin: 12px 0; }

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
</style>


