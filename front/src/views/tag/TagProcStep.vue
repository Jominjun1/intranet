<template>
  <h2>처리단계 관리</h2>
  <p>태그 정보를 검색하여 처리단계 정보를 관리하세용.</p>

  <!-- 태그 검색 폼 -->
  <SearchTag
      v-model="searchTagNo"
  />

  <!-- 태그 검색 결과 테이블 -->
  <div v-if="tableData.length > 0">
    <h3>검색된 태그 목록</h3>
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
      <el-table-column prop="tag_No" label="태그번호" width="150" align="center" resizable/>
      <el-table-column prop="mac_Addr" label="MAC주소" width="150" align="center" resizable/>
      <el-table-column prop="fac_Cd" label="공장코드" align="center" resizable/>
      <el-table-column prop="fac_No" label="시리얼번호" align="center" resizable/>
      <el-table-column prop="Status" label="삭제여부" resizable>
        <template #default="{ row }">
          <el-tag :type="row.Status === 'Y' ? 'danger' : 'success'">
            {{ row.Status === 'Y' ? '삭제됨' : '사용중' }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>

    <!-- 검색 결과가 여러 개일 때 안내 메시지 -->
    <div v-if="tableData.length > 1" class="info-message">
      <el-alert
          title="검색 결과가 여러 개입니다"
          description="더 구체적인 검색 조건을 입력하여 하나의 태그만 검색되도록 해주세요."
          type="info"
          :closable="false"
          show-icon
      />
    </div>
  </div>

  <!-- 처리단계 조회 결과 -->
  <div v-if="procStepData" class="result-section">
    <div class="result-header">
      <h3>처리단계 정보</h3>
      <div class="action-buttons" v-if="userAcl >= 2">
        <el-button type="isEditMode ? 'success' : 'primary'" @click="editProcStep">
          {{ isEditMode ? '저장' : '수정모드'}}
          <el-icon><Edit/></el-icon>수정
        </el-button>
        <el-button v-if="isEditMode" type="warning" @click="cancelEdit">취소</el-button>
        <el-button type="danger" @click="deleteProcStep">
          <el-icon><Delete/></el-icon>삭제
        </el-button>
      </div>
    </div>
    <el-descriptions :column="2" border>
      <el-descriptions-item label="태그번호">{{ procStepData.ordNo }}</el-descriptions-item>
      <el-descriptions-item label="발주사">{{ procStepData.order_cp}}</el-descriptions-item>
      <el-descriptions-item label="받은사람">{{ procStepData.order_re}}</el-descriptions-item>
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
</template>

<script setup>
import {Delete, Edit, QuestionFilled} from "@element-plus/icons-vue";
import {ref} from "vue";
import SearchTag from "../Common/SearchDept.vue";

const searchTagNo = ref('')
const showSearchHelp = ref(false)
const searchMac = ref('')
const searchSn = ref('')
const searchFacCd = ref('')
const searchDelFilter = ref('')
const isEditMode = ref(false)
const procStepInfoData = ref(null)
const originalProcStepData = ref(null)

function startEditMode() { originalProcStepData.value = JSON.parse(JSON.stringify(procStepInfoData.value)); isEditMode.value = true }
function cancelEdit() { if (originalProcStepData.value) procStepInfoData.value = JSON.parse(JSON.stringify(originalProcStepData.value)); isEditMode.value = false; originalProcStepData.value = null }
function toggleEditMode() { isEditMode.value ? savaProcStepInfo() : startstartEditMode()}
// 삭제여부 옵션
const delFilterOptions = [
  { label: '전체', value: 'all' },
  { label: '사용중', value: 'active' },
  { label: '삭제됨', value: 'deleted' }
]

</script>