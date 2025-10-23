<template>
  <div class="direct-search-section">
    <h3>🔍 태그번호 검색</h3>
    <el-form :inline="true" class="direct-search-form">
      <el-form-item label="태그번호">
        <el-input v-model="searchTagNo" placeholder="태그번호 일부 입력 (예: AABB)" clearable style="width: 350px;" @keyup.enter="searchTagNumbers"/>
      </el-form-item>
      <el-form-item>
        <el-button type="success" @click="searchTagNumbers">검색</el-button>
        <el-button @click="clearTagSearch">초기화</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script setup>

import {ref} from "vue";
import {ElMessage} from "element-plus";
import SearchTag from "./SearchTag.js";

const searchTagNo = ref('')
const tagNumberList = ref([])
const totalTagCount = ref(0)

function clearTagSearch() {
  searchTagNo.value = ''
  tagNumberList.value = []
  totalTagCount.value = 0
}

async function searchTagNumbers() {
  if (!searchTagNo.value.trim()) { ElMessage.warning('태그번호를 입력해주세요.'); return }
  try {
    const tagNumbers = await SearchTag.searchTagNumbers(searchTagNo.value.trim())
    const detailedList = []
    for (const ordNo of tagNumbers) {
      let macAddr = '', facCd = '', facNo = ''
      if (ordNo && ordNo.length >= 20) {
        macAddr = ordNo.substring(0, 12)
        facCd = ordNo.substring(12, 18)
        facNo = ordNo.substring(18)
      } else {
        macAddr = ordNo || ''
      }
      detailedList.push({ tag_No: ordNo, mac_Addr: macAddr, fac_Cd: facCd, fac_No: facNo })
    }
    tagNumberList.value = detailedList
    totalTagCount.value = detailedList.length
    if (tagNumberList.value.length === 0) ElMessage.info('검색 결과가 없습니다.')
  } catch (e) {
    console.error('태그번호 검색 오류:', e)
    ElMessage.error('검색 중 오류가 발생했습니다.')
  }
}
</script>

