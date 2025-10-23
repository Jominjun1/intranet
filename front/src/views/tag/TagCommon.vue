<template>
  <div class="tag-common-page">
    <h2>공통정보 관리</h2>
    <p>태그번호를 입력하여 공통정보를 관리하세요.</p>

    <!-- 태그번호 검색 -->
    <SearchTag
        v-model="searchTagNo"
    />

    <div v-if="commonHistoryData && commonHistoryData.length > 0" class="result-section">
      <div class="result-header">
        <h3>공통정보</h3>
        <div class="action-buttons" v-if="userAcl >= 2">
          <el-button type="primary" @click="addNewCommon">새 공통정보 등록</el-button>
        </div>
      </div>
      <el-table class="common-table" :data="Array.isArray(commonHistoryData) ? commonHistoryData : [commonHistoryData]" style="width: 100%" border stripe resizable :table-layout="'auto'">
        <el-table-column prop="mac_ADDR" label="MAC주소" width="180" align="center" resizable />
        <el-table-column prop="fac_CD" label="공장코드" align="center" resizable />
        <el-table-column prop="fac_NO" label="시리얼번호" align="center" resizable />
        <el-table-column prop="create_DT" label="생성일" align="center" resizable>
          <template #default="{ row }">{{ formatDate(row.create_DT) }}</template>
        </el-table-column>
        <el-table-column prop="create_ID" label="생성자" align="center" resizable />
        <el-table-column prop="update_DT" label="수정일" align="center" resizable>
          <template #default="{ row }">{{ formatDate(row.update_DT) }}</template>
        </el-table-column>
        <el-table-column prop="update_ID" label="수정자" align="center" resizable />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import {ref} from 'vue'
import axios from 'axios'
import {ElMessage} from 'element-plus'
import '../../css/Tag/TagCommon.css'
import '../../css/VUE/Common.css'
import SearchTag from "../Common/SearchDept.vue";

const userAcl = 3
const searchTagNo = ref('')
const commonHistoryData = ref([])

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

