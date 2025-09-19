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
          @click="toggleHelp"
          title="로그 관리 도움말"
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
        :key="tableKey"
      >
        <!-- 사용자 로그 -->
        <template v-if="selectedLogType === 'user'">
          <el-table-column prop="logId" label="로그 ID" width="80" />
          <el-table-column prop="loginId" label="로그인 ID" width="120" />
          <el-table-column prop="status" label="상태" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ getStatusDisplay(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="ip_addr" label="IP 주소" width="140" />
          <el-table-column prop="http_refr" label="HTTP 레퍼런스" width="200" />
          <el-table-column prop="reg_dt" label="로그인 일시" width="160">
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
          <el-table-column prop="log_seq" label="로그 ID" width="80" />
          <el-table-column prop="ordNo" label="태그 번호" width="200" />
          <el-table-column prop="tagType" label="태그 타입" width="120" />
          <el-table-column prop="ERP_CD" label="ERP 코드" width="120" />
          <el-table-column prop="MNG_CTG" label="관리 카테고리" width="120" />
          <el-table-column prop="LOT" label="LOT 번호" width="120" />
          <el-table-column prop="PROD_ODR" label="생산 지시" width="120" />
          <el-table-column prop="PJT_CD" label="프로젝트 코드" width="120" />
          <el-table-column prop="PJT_MNGR" label="프로젝트 매니저" width="120" />
          <el-table-column prop="MAC_DUP_YN" label="MAC 중복 여부" width="120" />
          <el-table-column prop="Status" label="상태" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.Status)">
                {{ getStatusDisplay(scope.row.Status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createDt" label="등록일시" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.createDt) }}
            </template>
          </el-table-column>
          <el-table-column prop="UPDATE_DT" label="수정일시" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.UPDATE_DT) }}
            </template>
          </el-table-column>
          <el-table-column prop="UPDATE_ID" label="수정자" width="100" />
        </template>

        <!-- 태그 공통정보 로그 -->
        <template v-else-if="selectedLogType === 'common'">
          <el-table-column prop="logseq" label="로그 ID" width="80" />
          <el-table-column prop="macAddr" label="MAC 주소" width="160" />
          <el-table-column prop="facCd" label="공장 코드" width="120" />
          <el-table-column prop="facNo" label="공장 번호" width="120" />
          <el-table-column prop="Status" label="상태" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.Status)">
                {{ getStatusDisplay(scope.row.Status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createDt" label="등록일시" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.createDt) }}
            </template>
          </el-table-column>
          <el-table-column prop="UPDATE_DT" label="수정일시" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.UPDATE_DT) }}
            </template>
          </el-table-column>
          <el-table-column prop="UPDATE_ID" label="수정자" width="100" />
        </template>

        <!-- 태그 설정정보 로그 -->
        <template v-else-if="selectedLogType === 'setting'">
          <el-table-column prop="logId" label="로그 ID" width="80" />
          <el-table-column prop="ordNo" label="태그 번호" width="200" />
          <el-table-column prop="hwVer" label="하드웨어 버전" width="120" />
          <el-table-column prop="fwVer" label="펌웨어 버전" width="120" />
          <el-table-column prop="ledSec" label="LED ON 주기" width="120" />
          <el-table-column prop="riMs" label="송신주기" width="120" />
          <el-table-column prop="txPower" label="신호 강도" width="120" />
          <el-table-column prop="randomDv" label="랜덤 디바이스" width="120" />
          <el-table-column prop="rfProfile" label="RF 프로파일" width="120" />
          <el-table-column prop="channel" label="채널" width="100" />
          <el-table-column prop="sleepMode" label="슬립 모드" width="100" />
          <el-table-column prop="sleepThHold" label="슬립 임계값" width="120" />
          <el-table-column prop="sleepInterval" label="슬립 간격" width="120" />
          <el-table-column prop="sleepPeriod" label="슬립 주기" width="120" />
          <el-table-column prop="bcVer" label="BC 버전" width="100" />
          <el-table-column prop="bcPeriod" label="BC 주기" width="100" />
          <el-table-column prop="bcSleep" label="BC 슬립" width="100" />
          <el-table-column prop="deviceIp" label="디바이스 IP" width="140" />
          <el-table-column prop="serverIp" label="서버 IP" width="140" />
          <el-table-column prop="gateway" label="게이트웨이" width="140" />
          <el-table-column prop="subMask" label="서브넷마스크" width="140" />
          <el-table-column prop="tdma" label="TDMA" width="100" />
          <el-table-column prop="port" label="포트" width="80" />
          <el-table-column prop="createDt" label="등록일시" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.createDt) }}
            </template>
          </el-table-column>
          <el-table-column prop="updateDt" label="수정일시" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.updateDt) }}
            </template>
          </el-table-column>
          <el-table-column prop="updateId" label="수정자" width="100" />
        </template>

        <!-- 태그 프로세스 로그 -->
        <template v-else-if="selectedLogType === 'proc'">
          <el-table-column prop="logseq" label="로그 ID" width="80" />
          <el-table-column prop="ordNo" label="태그 번호" width="200" />
          <el-table-column prop="RECEIPT_DT" label="입고일" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.RECEIPT_DT) }}
            </template>
          </el-table-column>
          <el-table-column prop="DELIVERY_DT" label="납품일" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.DELIVERY_DT) }}
            </template>
          </el-table-column>
          <el-table-column prop="LAB_INSP_DT" label="연구소 검수일" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.LAB_INSP_DT) }}
            </template>
          </el-table-column>
          <el-table-column prop="LAB_INSP_DESC" label="연구소 검수소견" width="200" />
          <el-table-column prop="Status" label="상태" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.Status)">
                {{ getStatusDisplay(scope.row.Status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createDt" label="등록일시" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.createDt) }}
            </template>
          </el-table-column>
          <el-table-column prop="UPDATE_DT" label="수정일시" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.UPDATE_DT) }}
            </template>
          </el-table-column>
          <el-table-column prop="UPDATE_ID" label="수정자" width="100" />
        </template>

        <!-- 태그 AS 로그 -->
        <template v-else-if="selectedLogType === 'prod'">
          <el-table-column prop="AS_LOG_SEQ" label="로그 ID" width="80" />
          <el-table-column prop="ordNo" label="태그 번호" width="200" />
          <el-table-column prop="AS_CNT" label="AS 횟수" width="100" />
          <el-table-column prop="AS_DOC" label="AS 문서" width="200" />
          <el-table-column prop="OCCR_DT" label="발생일시" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.OCCR_DT) }}
            </template>
          </el-table-column>
          <el-table-column prop="OCCR_RSN" label="발생 사유" width="200" />
          <el-table-column prop="DELIVERY_DT" label="배송일시" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.DELIVERY_DT) }}
            </template>
          </el-table-column>
          <el-table-column prop="CLOSE_DT" label="종료일시" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.CLOSE_DT) }}
            </template>
          </el-table-column>
          <el-table-column prop="CLOSE_RSLT" label="종료 결과" width="120" />
          <el-table-column prop="Status" label="상태" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.Status)">
                {{ getStatusDisplay(scope.row.Status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createDt" label="등록일시" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.createDt) }}
            </template>
          </el-table-column>
          <el-table-column prop="UPDATE_DT" label="수정일시" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.UPDATE_DT) }}
            </template>
          </el-table-column>
          <el-table-column prop="UPDATE_ID" label="수정자" width="100" />
        </template>

        <!-- 태그 버전 로그 -->
        <template v-else-if="selectedLogType === 'version'">
          <el-table-column prop="logId" label="로그 ID" width="80" />
          <el-table-column prop="ordNo" label="태그 번호" width="200" />
          <el-table-column prop="tagVer" label="태그 버전" width="120" />
          <el-table-column prop="delRsn" label="상태" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.delRsn)">
                {{ getStatusDisplay(scope.row.delRsn) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createDt" label="등록일시" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.createDt) }}
            </template>
          </el-table-column>
          <el-table-column prop="updateDt" label="수정일시" width="160">
            <template #default="scope">
              {{ formatDate(scope.row.updateDt) }}
            </template>
          </el-table-column>
          <el-table-column prop="updateId" label="수정자" width="100" />
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
    <div class="help-sidebar" :class="{ show: showHelp }">
      <div class="sidebar-header">
        <h3>로그 관리 도움말</h3>
        <el-button type="link"
          :icon="Close"
          @click="toggleHelp"
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
import {computed, onMounted, ref} from 'vue'
import {ElMessage} from 'element-plus'
import {Close, Download, QuestionFilled} from '@element-plus/icons-vue'
import axios from 'axios'

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
const tableKey = ref(0)

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

function toggleHelp() {
  showHelp.value = !showHelp.value
}

function getStatusType(status) {
  if (!status) return 'info'
  
  const statusStr = String(status).toLowerCase()
  
  switch (statusStr) {
    case '성공':
    case 'success':
    case '로그인':
    case 'login':
    case '로그아웃':
    case 'logout':
    case '정상':
    case 'active':
      return 'success'
    case '실패':
    case 'fail':
    case 'error':
    case '오류':
      return 'danger'
    case '잠김':
    case 'lock':
    case 'blocked':
    case '경고':
    case 'warning':
      return 'warning'
    default:
      return 'info'
  }
}

function getStatusDisplay(status) {
  if (!status) return '미정'
  
  const statusStr = String(status).toLowerCase()
  
  // 간단한 상태 표시로 변환
  switch (statusStr) {
    case '성공':
    case 'success':
    case '로그인':
    case 'login':
    case '로그아웃':
    case 'logout':
    case '정상':
    case 'active':
      return '성공'
    case '실패':
    case 'fail':
    case 'error':
    case '오류':
      return '실패'
    case '잠김':
    case 'lock':
    case 'blocked':
      return '잠김'
    default:
      return statusStr
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
    tableKey.value++ // 테이블 키 업데이트로 강제 리렌더링

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
}

function handleCurrentChange(val) {
  currentPage.value = val
}

// 초기 로드
onMounted(() => {
  // 오늘 날짜 범위 설정 (00:00:00 ~ 23:59:59)
  const today = new Date()
  const startOfDay = new Date(today.getFullYear(), today.getMonth(), today.getDate())
  const endOfDay = new Date(today.getFullYear(), today.getMonth(), today.getDate(), 23, 59, 59)
  
  dateRange.value = [
    startOfDay.toISOString().split('T')[0], // YYYY-MM-DD 형식
    endOfDay.toISOString().split('T')[0]    // YYYY-MM-DD 형식
  ]
  
  loadLogs()
})
</script>