<template>
  <div class="tag-common-page">
    <h2>공통정보 관리</h2>
    <p>태그번호를 입력하여 공통정보를 관리하세요.</p>

    <div class="direct-search-section">
      <h3>🔍 태그번호 검색</h3>
      <el-form :inline="true" class="direct-search-form">
        <el-form-item label="태그번호">
          <el-input v-model="searchTagNo" placeholder="태그번호 일부 입력 (예: AABB)" clearable style="width: 350px;" @keyup.enter="searchCommonHistory" />
        </el-form-item>
        <el-form-item>
          <el-button type="success" @click="searchCommonHistory">검색</el-button>
          <el-button @click="clearTagSearch">초기화</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div v-if="commonHistoryData && commonHistoryData.length > 0" class="result-section">
      <div class="result-header">
        <h3>공통정보</h3>
        <div class="action-buttons" v-if="userAcl >= 2">
          <el-button type="primary" @click="addNewCommon">새 공통정보 등록</el-button>
        </div>
      </div>
      <el-table :data="Array.isArray(commonHistoryData) ? commonHistoryData : [commonHistoryData]" style="width: 100%" border resizable>
        <el-table-column prop="mac_ADDR" label="MAC주소" width="180" resizable />
        <el-table-column prop="fac_CD" label="공장코드" width="100" resizable />
        <el-table-column prop="fac_NO" label="시리얼번호" width="100" resizable />
        <el-table-column prop="create_DT" label="생성일" width="140" resizable>
          <template #default="{ row }">{{ formatDate(row.create_DT) }}</template>
        </el-table-column>
        <el-table-column prop="create_ID" label="생성자" width="80" resizable />
        <el-table-column prop="update_DT" label="수정일" width="140" resizable>
          <template #default="{ row }">{{ formatDate(row.update_DT) }}</template>
        </el-table-column>
        <el-table-column prop="update_ID" label="수정자" width="80" resizable />
      </el-table>
    </div>

    <div v-else-if="commonHistoryData && commonHistoryData.length === 0" class="result-section">
      <h3>공통정보</h3>
      <el-alert title="공통정보가 없습니다" description="해당 태그의 공통정보가 없습니다." type="info" :closable="false" show-icon />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const userAcl = 3
const searchTagNo = ref('')
const commonHistoryData = ref([])

function clearTagSearch() { searchTagNo.value=''; commonHistoryData.value=[] }

function formatDate(dateString) { if (!dateString) return '-'; try { const d=new Date(dateString); return d.toLocaleDateString('ko-KR',{year:'numeric',month:'2-digit',day:'2-digit'}) } catch { return dateString } }

async function searchCommonHistory() {
  if (!searchTagNo.value.trim()) { ElMessage.warning('태그번호를 입력해주세요.'); return }
  try {
    const res = await axios.get(`/tags/common_history_${searchTagNo.value.trim()}`)
    let data = res.data.body || res.data
    if (data && !Array.isArray(data)) data = [data]
    commonHistoryData.value = data || []
    if (!commonHistoryData.value || commonHistoryData.value.length === 0) ElMessage.warning('해당 태그의 공통정보가 없습니다.')
  } catch (e) { console.error('공통정보 조회 오류:', e); ElMessage.error('공통정보를 불러오는 중 오류가 발생했습니다.'); commonHistoryData.value=[] }
}

function addNewCommon() { ElMessage.info('공통정보 등록 기능은 개발 중입니다.') }
</script>

<style scoped>
.tag-common-page { padding: 16px; }
.result-header { display: flex; align-items: center; justify-content: space-between; margin: 12px 0; }
.action-buttons { display: flex; gap: 8px; }
</style>


