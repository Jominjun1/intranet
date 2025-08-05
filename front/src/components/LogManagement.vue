<template>
  <div class="log-management-page">
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
        <h3>로그 검색</h3>
        <el-button 
          type="info" 
          :icon="QuestionFilled" 
          circle 
          size="small"
          @click="showHelp = true"
          title="검색 도움말"
        />
      </div>
      <el-form :inline="true" class="search-form">
        <el-form-item label="로그 타입">
          <el-select v-model="selectedLogType" placeholder="로그 타입 선택" @change="loadLogs" style="width: 200px;">
            <el-option label="사용자 로그" value="user" />
            <el-option label="태그 기본정보 로그" value="basic" />
            <el-option label="태그 공통정보 로그" value="common" />
            <el-option label="태그 설정정보 로그" value="setting" />
            <el-option label="태그 프로세스 로그" value="proc" />
            <el-option label="태그 AS 로그" value="prod" />
            <el-option label="태그 버전 로그" value="version" />
          </el-select>
        </el-form-item>
        <el-form-item label="기간">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="~"
            start-placeholder="시작일"
            end-placeholder="종료일"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            @change="loadLogs"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadLogs" :loading="loading">검색</el-button>
          <el-button @click="resetSearch">초기화</el-button>
          <el-button type="success" @click="exportLogs" :disabled="!logs.length">
            <el-icon><Download /></el-icon>
            내보내기
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 테이블 영역 -->
    <div class="table-section">
      <el-table 
        :data="paginatedData" 
        style="width: 100%" 
        v-loading="loading"
        stripe
        border
      >
        <!-- 사용자 로그 -->
        <template v-if="selectedLogType === 'user'">
          <el-table-column prop="logId" label="로그 ID" width="80" />
          <el-table-column prop="loginId" label="로그인 ID" width="120" />
          <el-table-column prop="status" label="상태" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="ip_addr" label="IP 주소" width="140" />
          <el-table-column prop="http_refr" label="HTTP 레퍼런스" width="200" />
          <el-table-column prop="reg_dt" label="등록일시" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.reg_dt) }}
            </template>
          </el-table-column>
          <el-table-column prop="update_dt" label="수정일시" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.update_dt) }}
            </template>
          </el-table-column>
          <el-table-column prop="update_id" label="수정자" width="100" />
        </template>

        <!-- 태그 기본정보 로그 -->
        <template v-else-if="selectedLogType === 'basic'">
          <el-table-column prop="logId" label="로그 ID" width="80" />
          <el-table-column prop="ordNo" label="태그 번호" width="200" />
          <el-table-column prop="tagType" label="태그 타입" width="120" />
          <el-table-column prop="erpCd" label="ERP 코드" width="120" />
          <el-table-column prop="mngCtg" label="관리 카테고리" width="120" />
          <el-table-column prop="lot" label="LOT 번호" width="120" />
          <el-table-column prop="prodOdr" label="생산 지시" width="120" />
          <el-table-column prop="pjtCd" label="프로젝트 코드" width="120" />
          <el-table-column prop="pjtMngr" label="프로젝트 매니저" width="120" />
          <el-table-column prop="macDupYn" label="MAC 중복 여부" width="120" />
          <el-table-column prop="reg_dt" label="등록일시" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.reg_dt) }}
            </template>
          </el-table-column>
          <el-table-column prop="update_dt" label="수정일시" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.update_dt) }}
            </template>
          </el-table-column>
          <el-table-column prop="update_id" label="수정자" width="100" />
        </template>

        <!-- 태그 공통정보 로그 -->
        <template v-else-if="selectedLogType === 'common'">
          <el-table-column prop="logId" label="로그 ID" width="80" />
          <el-table-column prop="ordNo" label="태그 번호" width="200" />
          <el-table-column prop="macAddr" label="MAC 주소" width="160" />
          <el-table-column prop="facCd" label="공장 코드" width="120" />
          <el-table-column prop="facNo" label="공장 번호" width="120" />
          <el-table-column prop="reg_dt" label="등록일시" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.reg_dt) }}
            </template>
          </el-table-column>
          <el-table-column prop="update_dt" label="수정일시" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.update_dt) }}
            </template>
          </el-table-column>
          <el-table-column prop="update_id" label="수정자" width="100" />
        </template>

        <!-- 태그 설정정보 로그 -->
        <template v-else-if="selectedLogType === 'setting'">
          <el-table-column prop="logId" label="로그 ID" width="80" />
          <el-table-column prop="ordNo" label="태그 번호" width="200" />
          <el-table-column prop="settingType" label="설정 타입" width="120" />
          <el-table-column prop="settingValue" label="설정 값" width="200" />
          <el-table-column prop="reg_dt" label="등록일시" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.reg_dt) }}
            </template>
          </el-table-column>
          <el-table-column prop="update_dt" label="수정일시" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.update_dt) }}
            </template>
          </el-table-column>
          <el-table-column prop="update_id" label="수정자" width="100" />
        </template>

        <!-- 태그 프로세스 로그 -->
        <template v-else-if="selectedLogType === 'proc'">
          <el-table-column prop="logId" label="로그 ID" width="80" />
          <el-table-column prop="ordNo" label="태그 번호" width="200" />
          <el-table-column prop="procStep" label="프로세스 단계" width="120" />
          <el-table-column prop="procStatus" label="프로세스 상태" width="120" />
          <el-table-column prop="procResult" label="프로세스 결과" width="200" />
          <el-table-column prop="reg_dt" label="등록일시" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.reg_dt) }}
            </template>
          </el-table-column>
          <el-table-column prop="update_dt" label="수정일시" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.update_dt) }}
            </template>
          </el-table-column>
          <el-table-column prop="update_id" label="수정자" width="100" />
        </template>

        <!-- 태그 AS 로그 -->
        <template v-else-if="selectedLogType === 'prod'">
          <el-table-column prop="logId" label="로그 ID" width="80" />
          <el-table-column prop="ordNo" label="태그 번호" width="200" />
          <el-table-column prop="asCnt" label="AS 횟수" width="100" />
          <el-table-column prop="asDoc" label="AS 문서" width="200" />
          <el-table-column prop="occrDt" label="발생일시" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.occrDt) }}
            </template>
          </el-table-column>
          <el-table-column prop="occrRsn" label="발생 사유" width="200" />
          <el-table-column prop="deliveryDt" label="배송일시" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.deliveryDt) }}
            </template>
          </el-table-column>
          <el-table-column prop="closeDt" label="종료일시" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.closeDt) }}
            </template>
          </el-table-column>
          <el-table-column prop="closeRslt" label="종료 결과" width="120" />
          <el-table-column prop="reg_dt" label="등록일시" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.reg_dt) }}
            </template>
          </el-table-column>
          <el-table-column prop="update_dt" label="수정일시" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.update_dt) }}
            </template>
          </el-table-column>
          <el-table-column prop="update_id" label="수정자" width="100" />
        </template>

        <!-- 태그 버전 로그 -->
        <template v-else-if="selectedLogType === 'version'">
          <el-table-column prop="logId" label="로그 ID" width="80" />
          <el-table-column prop="ordNo" label="태그 번호" width="200" />
          <el-table-column prop="tagVer" label="태그 버전" width="120" />
          <el-table-column prop="reg_dt" label="등록일시" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.reg_dt) }}
            </template>
          </el-table-column>
          <el-table-column prop="update_dt" label="수정일시" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.update_dt) }}
            </template>
          </el-table-column>
          <el-table-column prop="update_id" label="수정자" width="100" />
        </template>
      </el-table>
      
      <!-- 페이지네이션 -->
      <div class="pagination-section" v-if="logs.length > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="logs.length"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 도움말 사이드바 -->
    <div class="help-sidebar" v-if="showHelp">
      <div class="sidebar-header">
        <h3>로그 관리 도움말</h3>
        <el-button type="link"
          :icon="Close" 
          @click="showHelp = false"
          class="close-btn"
        />
      </div>
      <div class="help-content">
        <div class="help-section">
          <h4>📊 로그 타입</h4>
          <ul>
            <li><strong>사용자 로그:</strong> 사용자 로그인/로그아웃 활동 기록</li>
            <li><strong>태그 기본정보 로그:</strong> 태그 기본 정보 변경 이력</li>
            <li><strong>태그 공통정보 로그:</strong> 태그 공통 정보 변경 이력</li>
            <li><strong>태그 설정정보 로그:</strong> 태그 설정 정보 변경 이력</li>
            <li><strong>태그 프로세스 로그:</strong> 태그 처리 단계별 이력</li>
            <li><strong>태그 AS 로그:</strong> 태그 AS(After Service) 이력</li>
            <li><strong>태그 버전 로그:</strong> 태그 버전 변경 이력</li>
          </ul>
        </div>
        
        <div class="help-section">
          <h4>🔍 검색 기능</h4>
          <ul>
            <li><strong>로그 타입:</strong> 조회할 로그의 종류를 선택합니다.</li>
            <li><strong>기간:</strong> 조회할 기간을 설정합니다.</li>
            <li><strong>검색:</strong> 선택한 조건으로 로그를 조회합니다.</li>
            <li><strong>초기화:</strong> 검색 조건을 초기화합니다.</li>
            <li><strong>내보내기:</strong> 조회된 로그를 CSV 파일로 다운로드합니다.</li>
          </ul>
        </div>
        
        <div class="help-section">
          <h4>📈 로그 상태</h4>
          <ul>
            <li><strong>로그 ID:</strong> 각 로그의 고유 식별 번호</li>
            <li><strong>등록일시:</strong> 로그가 생성된 시간</li>
            <li><strong>수정일시:</strong> 로그가 마지막으로 수정된 시간</li>
            <li><strong>수정자:</strong> 로그를 수정한 사용자</li>
          </ul>
        </div>
        
        <div class="help-section">
          <h4>💡 사용 팁</h4>
          <ul>
            <li>로그 타입을 선택하면 해당하는 컬럼들이 표시됩니다.</li>
            <li>기간을 설정하지 않으면 최근 30일간의 로그가 조회됩니다.</li>
            <li>대용량 로그는 페이지네이션을 통해 효율적으로 확인할 수 있습니다.</li>
            <li>내보내기 기능으로 로그 데이터를 백업하거나 분석할 수 있습니다.</li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { QuestionFilled, Close, Download } from '@element-plus/icons-vue'
import axios from 'axios'
import Header from './Header.vue'

// Props
const props = defineProps({
  userInfo: {
    type: Object,
    default: () => ({})
  },
  activeMenu: {
    type: String,
    default: 'log-management'
  }
})

// Emits
const emit = defineEmits(['menu-select', 'user-command'])

// 상태 관리
const selectedLogType = ref('user')
const dateRange = ref([])
const logs = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)
const showHelp = ref(false)

// 페이지네이션된 데이터
const paginatedData = computed(() => {
  const startIndex = (currentPage.value - 1) * pageSize.value
  const endIndex = startIndex + pageSize.value
  return logs.value.slice(startIndex, endIndex)
})

// 메서드
function handleMenuSelect(key) {
  emit('menu-select', key)
}

function handleUserCommand(command) {
  emit('user-command', command)
}

function getStatusType(status) {
  switch (status) {
    case '성공':
    case '로그인':
    case '로그아웃':
      return 'success'
    case '실패':
      return 'danger'
    case '잠김':
      return 'warning'
    case '회원가입':
    case '정보수정':
    case '권한수정':
    case '비밀번호 변경':
      return 'info'
    default:
      return ''
  }
}

function formatDate(dateString) {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleString('ko-KR')
}

async function loadLogs() {
  if (!selectedLogType.value) {
    ElMessage.warning('로그 타입을 선택해주세요.')
    return
  }

  loading.value = true
  try {
    currentPage.value = 1 // 페이지 초기화
    const params = {
      type: selectedLogType.value
    }

    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }

    const response = await axios.get('/Log/getLog', { params })
    
    // 백엔드 응답 구조에 맞게 데이터 추출
    let responseData = response.data
    
    // 응답이 래핑된 경우 body에서 추출
    if (responseData && typeof responseData === 'object' && responseData.body !== undefined) {
      responseData = responseData.body
    }
    
    logs.value = Array.isArray(responseData) ? responseData : []

    ElMessage.success('로그를 성공적으로 조회했습니다.')
  } catch (error) {
    console.error('로그 조회 오류:', error)
    ElMessage.error('로그 조회 중 오류가 발생했습니다.')
    logs.value = []
  } finally {
    loading.value = false
  }
}



function exportLogs() {
  if (!logs.value.length) {
    ElMessage.warning('내보낼 로그가 없습니다.')
    return
  }

  // CSV 내보내기 로직
  const headers = Object.keys(logs.value[0])
  const csvContent = [
    headers.join(','),
    ...logs.value.map(row => 
      headers.map(header => {
        const value = row[header]
        if (value instanceof Date) {
          return formatDate(value)
        }
        return value || ''
      }).join(',')
    )
  ].join('\n')

  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  const url = URL.createObjectURL(blob)
  link.setAttribute('href', url)
  link.setAttribute('download', `${selectedLogType.value}_logs_${new Date().toISOString().split('T')[0]}.csv`)
  link.style.visibility = 'hidden'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)

  ElMessage.success('로그가 성공적으로 내보내졌습니다.')
}

function resetSearch() {
  selectedLogType.value = 'user'
  dateRange.value = []
  currentPage.value = 1
  loadLogs()
  ElMessage.info('검색 조건이 초기화되었습니다.')
}

function handleSizeChange(val) {
  pageSize.value = val
  currentPage.value = 1
  loadLogs()
}

function handleCurrentChange(val) {
  currentPage.value = val
  loadLogs()
}

// 초기 로드
onMounted(() => {
  loadLogs()
})
</script>

<style scoped>
.log-management-page {
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

.table-section {
  margin-top: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.pagination-section {
  display: flex;
  justify-content: center;
  padding: 20px;
  background: white;
  border-top: 1px solid #eee;
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

:deep(.el-table td .cell) {
  text-align: center !important;
}

@media (max-width: 768px) {
  .search-form {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-form .el-form-item {
    width: 100%;
  }

  .search-form .el-button {
    width: 100%;
  }

  .search-section, .table-section {
    padding: 16px;
  }
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