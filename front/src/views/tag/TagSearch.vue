<template>
  <div class="tag-search-page">
    <h2>태그 검색</h2>
    <p>태그 정보를 검색하여 상세 정보를 조회하세요.</p>

    <div class="search-section">
      <div class="search-header">
        <h3>🔍 태그 검색</h3>
        <el-button type="text" @click="showSearchHelp = !showSearchHelp">
          <el-icon><QuestionFilled /></el-icon>
          검색 도움말
        </el-button>
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
          <el-button type="primary" @click="doSearch">태그 검색</el-button>
          <el-button @click="resetSearch">초기화</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div v-if="tableData.length > 0">
      <h3>검색된 태그 목록</h3>
      <el-table :data="paginatedData" style="width:100%" v-loading="loading" border resizable>
        <el-table-column prop="tag_No" label="태그번호" width="150" align="center" resizable />
        <el-table-column prop="mac_Addr" label="MAC주소" width="150" align="center" resizable />
        <el-table-column prop="fac_Cd" label="공장코드" width="100" align="center" resizable />
        <el-table-column prop="fac_No" label="시리얼번호" width="120" align="center" resizable />
        <el-table-column prop="tag_Version" label="제품버전" width="100" align="center" resizable />
        <el-table-column prop="tag_Type" label="태그타입" width="100" align="center" resizable />
        <el-table-column prop="erp_Code" label="ERP코드" width="120" align="center" resizable />
        <el-table-column prop="Mng_Category" label="관리카테고리" width="120" align="center" resizable />
        <el-table-column prop="Lot" label="LOT번호" width="120" align="center" resizable />
        <el-table-column prop="Prod_order" label="생산지시" width="120" align="center" resizable />
        <el-table-column prop="Project_code" label="프로젝트코드" width="120" align="center" resizable />
        <el-table-column prop="Project_manager" label="프로젝트매니저" width="120" align="center" resizable />
        <el-table-column prop="Mac_duple_yn" label="MAC중복여부" width="120" align="center" resizable />
        <el-table-column prop="as_Cnt" label="AS횟수" width="80" align="center" resizable />
        <el-table-column prop="Status" label="삭제여부" width="100" align="center" resizable>
          <template #default="{ row }">
            <el-tag :type="row.Status === 'Y' ? 'danger' : 'success'">
              {{ row.Status === 'Y' ? '삭제됨' : '사용중' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="작업" width="320" align="center" resizable>
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button size="small" @click="showProcStep(row.tag_No)">
                <el-icon><Setting /></el-icon>
                처리단계
              </el-button>
              <el-button size="small" @click="showSettingInfo(row.tag_No)">
                <el-icon><Tools /></el-icon>
                세팅정보
              </el-button>
              <el-button size="small" @click="showVersionHistory(row.tag_No)">
                <el-icon><Document /></el-icon>
                버전이력
              </el-button>
              <el-button size="small" @click="showCommonHistory(row.tag_No)">
                <el-icon><InfoFilled /></el-icon>
                공통정보
              </el-button>
              <el-button size="small" @click="showAsInfo(row.tag_No)">
                <el-icon><Warning /></el-icon>
                AS이력
              </el-button>
            </div>
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
          <h5>📱 MAC주소</h5>
          <p>태그의 MAC 주소를 입력하세요. (예: 00:11:22:33:44:55)</p>
        </div>
        <div class="help-item">
          <h5>🏭 공장코드</h5>
          <p>태그가 설치된 공장의 코드를 입력하세요.</p>
        </div>
        <div class="help-item">
          <h5>🔢 시리얼번호</h5>
          <p>태그의 시리얼 번호를 입력하세요.</p>
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
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { QuestionFilled, Close, InfoFilled, Setting, Tools, Document, Warning } from '@element-plus/icons-vue'

const router = useRouter()

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
const showSearchHelp = ref(false)

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

function handleSizeChange(size) {
  pageSize.value = size
  currentPage.value = 1
}

function handleCurrentChange(page) {
  currentPage.value = page
}

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
    } else {
      macAddr = ordNo
    }
  }

  const hasSearchCondition = (searchTagNo.value && searchTagNo.value.trim()) || 
                           (macAddr && macAddr.trim()) || 
                           (facCd && facCd.trim()) || 
                           (facNo && facNo.trim())
  if (!hasSearchCondition) {
    ElMessage.warning('검색 조건을 하나 이상 입력해주세요.')
    return
  }

  loading.value = true
  try {
    const params = {}
    if (macAddr && macAddr.trim()) params.macAddr = macAddr.trim()
    if (facCd && facCd.trim()) params.facCd = facCd.trim()
    if (facNo && facNo.trim()) params.facNo = facNo.trim()
    if (searchDelFilter.value && searchDelFilter.value !== 'all') params.delFilter = searchDelFilter.value

    const res = await axios.get('/tags/getTagList', { params })
    let responseData = res.data
    if (responseData && typeof responseData === 'object' && responseData.body !== undefined) {
      responseData = responseData.body
    }
    tableData.value = Array.isArray(responseData) ? responseData : []
    currentPage.value = 1
    if (tableData.value.length === 0) ElMessage.info('검색 결과가 없습니다.')
  } catch (error) {
    console.error('검색 오류:', error)
    ElMessage.error('검색 중 오류가 발생했습니다.')
  } finally {
    loading.value = false
  }
}

function resetSearch() {
  searchTagNo.value = ''
  searchMac.value = ''
  searchSn.value = ''
  searchFacCd.value = ''
  searchDelFilter.value = 'all'
  tableData.value = []
  currentPage.value = 1
}

function showProcStep(ordNo) { router.push(`/tag-management/proc-step/${ordNo}`) }
function showSettingInfo(ordNo) { router.push(`/tag-management/setting/${ordNo}`) }
function showVersionHistory(ordNo) { router.push(`/tag-management/version/${ordNo}`) }
function showCommonHistory(ordNo) { router.push(`/tag-management/common/${ordNo}`) }
function showAsInfo(ordNo) { router.push(`/tag-management/as/${ordNo}`) }
</script>

<style scoped>
.tag-search-page { padding: 16px; }
.search-section { margin-top: 12px; }
.search-header { display: flex; align-items: center; justify-content: space-between; }
.action-buttons { display: flex; gap: 6px; justify-content: center; }
.pagination-section { margin-top: 12px; display: flex; justify-content: center; }
.search-help-sidebar { position: fixed; top: 64px; right: -380px; width: 360px; transition: right .2s; background: #fff; border-left: 1px solid #eee; height: calc(100% - 64px); padding: 12px; overflow: auto; }
.search-help-sidebar.show { right: 0; }
.sidebar-header { display: flex; align-items: center; justify-content: space-between; }
.help-item { margin: 8px 0; }
</style>


