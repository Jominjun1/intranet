<template>
  <div class="tag-version-page">
    <h2>버전 이력 관리</h2>
    <p>태그번호를 입력하여 버전 이력을 관리하세요.</p>

    <div class="direct-search-section">
      <h3>🔍 태그번호 검색</h3>
      <el-form :inline="true" class="direct-search-form">
        <el-form-item label="태그번호">
          <el-input v-model="searchTagNo" placeholder="태그번호 일부 입력 (예: AABB)" clearable style="width: 350px;" @keyup.enter="searchVersionHistory" />
        </el-form-item>
        <el-form-item>
          <el-button type="success" @click="searchVersionHistory">검색</el-button>
          <el-button @click="clearTagSearch">초기화</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div v-if="versionHistoryData && versionHistoryData.length > 0" class="result-section">
      <div class="result-header">
        <h3>버전 이력</h3>
        <div class="action-buttons" v-if="userAcl >= 2">
          <el-button type="primary" @click="addNewVersion">새 버전 등록</el-button>
        </div>
      </div>
      <el-table :data="versionHistoryData || []" style="width: 100%" border resizable>
        <el-table-column prop="tag_version" label="버전" width="100" resizable />
        <el-table-column prop="HW_VERSION" label="하드웨어버전" width="120" resizable />
        <el-table-column prop="FW_VERSION" label="펌웨어버전" width="120" resizable />
        <el-table-column prop="create_Dt" label="생성일" width="120" resizable>
          <template #default="{ row }">{{ formatDate(row.create_Dt) }}</template>
        </el-table-column>
        <el-table-column prop="create_Id" label="생성자" width="100" resizable />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import {ref} from 'vue'
import axios from 'axios'
import {ElMessage} from 'element-plus'
import '../../css/Tag/TagVersion.css'

const userAcl = 3
const searchTagNo = ref('')
const versionHistoryData = ref([])

function clearTagSearch() { searchTagNo.value = ''; versionHistoryData.value = [] }

function formatDate(dateString) { if (!dateString) return '-'; try { const d = new Date(dateString); return d.toLocaleDateString('ko-KR', {year:'numeric',month:'2-digit',day:'2-digit'}) } catch { return dateString } }

async function searchVersionHistory() {
  if (!searchTagNo.value.trim()) { ElMessage.warning('태그번호를 입력해주세요.'); return }
  try {
    const res = await axios.get(`/tags/version-history/${searchTagNo.value.trim()}`)
    versionHistoryData.value = res.data.body || res.data || []
    if (!versionHistoryData.value || versionHistoryData.value.length === 0) ElMessage.warning('해당 태그의 버전 이력이 없습니다.')
  } catch (e) { console.error('버전 이력 조회 오류:', e); ElMessage.error('버전 이력을 불러오는 중 오류가 발생했습니다.') }
}

function addNewVersion() { ElMessage.info('버전 등록 기능은 개발 중입니다.') }
</script>


