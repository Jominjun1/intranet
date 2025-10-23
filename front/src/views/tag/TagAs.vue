<template>
  <div class="tag-as-page">
    <h2>AS 이력 관리</h2>
    <p>태그번호를 입력하여 AS 이력을 관리하세요.</p>

    <!-- 태그번호 검색 -->
    <SearchTag
        v-model="searchTagNo"
    />

    <!-- 태그번호 검색 결과 목록 -->
    <div v-if="tagNumberList.length > 0" class="tag-search-results">
      <h3>검색된 태그번호 목록 (총 {{ totalTagCount }}개)</h3>
      <el-table
          :data="paginatedTagList"
          style="width: 100%"
          @row-click="selectTagNumber"
          border
          stripe
          resizable
          :table-layout="'auto'"
          :cell-style="{ 'white-space': 'nowrap', 'text-align': 'center' }"
          :header-cell-style="{ 'white-space': 'nowrap', 'text-align': 'center', 'background-color': '#f5f7fa', 'font-weight': 'bold' }"
      >
        <el-table-column prop="tag_No" label="태그번호" width="300" align="center" resizable />
        <el-table-column prop="mac_Addr" label="MAC주소" align="center" resizable />
        <el-table-column prop="fac_Cd" label="공장코드" align="center" resizable />
        <el-table-column prop="fac_No" label="시리얼번호" align="center" resizable />
        <el-table-column label="작업" align="center" resizable>
          <template #default="{ row }">
            <el-button size="small" type="primary" @click.stop="selectTagNumber(row)">선택</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 페이지네이션 -->
      <Pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="tagNumberList.length"
      />
    </div>

    <!-- AS 이력 테이블 -->
    <div v-if="asInfoData && asInfoData.length > 0" class="result-section">
      <div class="result-header">
        <h3>AS 이력</h3>
        <div class="as-actions" v-if="userAcl >= 2">
          <el-button type="primary" @click="addNewAs">
            <el-icon><Plus /></el-icon>
            새 AS 등록
          </el-button>
        </div>
      </div>

      <el-table class="common-table" :data="asInfoData" style="width: 100%" border stripe resizable :table-layout="'auto'">
        <el-table-column prop="as_Cnt" label="AS 횟수" align="center" resizable />
        <el-table-column prop="mac_ADDR" label="MAC주소" width="150" align="center" resizable />
        <el-table-column prop="as_Doc" label="AS 문서번호" align="center" resizable />
        <el-table-column prop="occr_Dt" label="AS 발생일" align="center" resizable>
          <template #default="{ row }">{{ formatDate(row.occr_Dt) }}</template>
        </el-table-column>
        <el-table-column prop="occr_RSN" label="AS 발생사유" width="150" align="center" resizable />
        <el-table-column prop="close_Dt" label="AS 종결일" align="center" resizable>
          <template #default="{ row }">{{ formatDate(row.close_Dt) }}</template>
        </el-table-column>
        <el-table-column prop="close_RSLT" label="AS 처리결과" width="150" align="center" resizable />
        <el-table-column prop="delivery_DT" label="납품일" align="center" resizable>
          <template #default="{ row }">{{ formatDate(row.delivery_DT) }}</template>
        </el-table-column>
        <el-table-column prop="create_DT" label="생성일" align="center" resizable>
          <template #default="{ row }">{{ formatDate(row.create_DT) }}</template>
        </el-table-column>
        <el-table-column prop="create_ID" label="생성자" align="center" resizable />
        <el-table-column prop="update_DT" label="수정일" align="center" resizable>
          <template #default="{ row }">{{ formatDate(row.update_DT) }}</template>
        </el-table-column>
        <el-table-column prop="update_ID" label="수정자" align="center" resizable />
        <el-table-column prop="del_YN" label="삭제여부" align="center" resizable>
          <template #default="{ row }">
            <el-tag :type="row.del_YN === 'Y' ? 'danger' : 'success'">
              {{ row.del_YN === 'Y' ? '삭제됨' : '사용중' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="작업" align="center" v-if="userAcl >= 2" resizable>
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button size="small" @click="editAs(row)"><el-icon><Edit /></el-icon>수정</el-button>
              <el-button v-if="row.del_YN !== 'Y'" size="small" type="danger" @click="deleteAs(row)">
                <el-icon><Delete /></el-icon>삭제
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- AS 등록/수정 폼 모달 -->
    <el-dialog v-model="showAsForm" :title="isEditMode ? 'AS 수정' : 'AS 등록'" width="600px">
      <el-form :model="asForm" label-width="120px">
        <el-form-item label="AS 문서번호"><el-input v-model="asForm.asDoc" placeholder="AS 문서번호 입력" /></el-form-item>
        <el-form-item label="AS 발생일">
          <el-date-picker v-model="asForm.occrDt" type="datetime" placeholder="AS 발생일 선택" format="YYYY-MM-DD HH:mm" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="AS 발생사유"><el-input v-model="asForm.occrRsn" type="textarea" placeholder="AS 발생사유 입력" /></el-form-item>
        <el-form-item label="AS 종결일">
          <el-date-picker v-model="asForm.closeDt" type="datetime" placeholder="AS 종결일 선택" format="YYYY-MM-DD HH:mm" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="AS 처리결과"><el-input v-model="asForm.closeRslt" type="textarea" placeholder="AS 처리결과 입력" /></el-form-item>
        <el-form-item label="납품일">
          <el-date-picker v-model="asForm.deliveryDt" type="datetime" placeholder="납품일 선택" format="YYYY-MM-DD HH:mm" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="수정자"><el-input v-model="asForm.updateId" placeholder="수정자 사번 입력" /></el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAsForm = false">취소</el-button>
          <el-button type="primary" @click="saveAs">저장</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {computed, ref} from 'vue'
import axios from 'axios'
import {ElMessage, ElMessageBox} from 'element-plus'
import {Delete, Edit, Plus} from '@element-plus/icons-vue'
import SearchTag from "../Common/SearchDept.vue";
import Pagination from "../Common/Pagination.vue";

const userAcl = 3 // 권한 표시용(필요시 상위에서 주입하도록 변경 가능)

const currentPage = ref(1)
const pageSize = ref(10)

const asInfoData = ref([])
const currentTagNo = ref(null)

const showAsForm = ref(false)
const isEditMode = ref(false)
const asForm = ref({ id: null, asDoc: '', occrDt: '', occrRsn: '', closeDt: '', closeRslt: '', deliveryDt: '', updateId: '' })

function handleSizeChange(size) { pageSize.value = size; currentPage.value = 1 }
function handleCurrentChange(page) { currentPage.value = page }

const paginatedTagList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return tagNumberList.value.slice(start, end)
})

function formatDate(dateString) {
  if (!dateString) return '-'
  try {
    const date = new Date(dateString)
    return date.toLocaleDateString('ko-KR', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
  } catch { return dateString }
}

function selectTagNumber(row) {
  searchTagNo.value = row.tag_No
  tagNumberList.value = []
  totalTagCount.value = 0
  loadAsData(row.tag_No)
}

async function loadAsData(ordNo) {
  if (!ordNo) return
  currentTagNo.value = ordNo
  try {
    const res = await axios.get(`/tags/prod_as_${ordNo}`)
    const data = res.data.body || res.data
    asInfoData.value = Array.isArray(data) ? data : [data]
  } catch (e) {
    console.error('AS 이력 조회 오류:', e)
    ElMessage.error('AS 이력을 불러오는 중 오류가 발생했습니다.')
  }
}

function addNewAs() {
  isEditMode.value = false
  asForm.value = { id: null, asDoc: '', occrDt: '', occrRsn: '', closeDt: '', closeRslt: '', deliveryDt: '', updateId: '' }
  showAsForm.value = true
}

function editAs(row) {
  isEditMode.value = true
  asForm.value = {
    id: row.id,
    asDoc: row.as_Doc || '',
    occrDt: row.occr_Dt ? formatDateForInput(row.occr_Dt) : '',
    occrRsn: row.occr_RSN || '',
    closeDt: row.close_Dt ? formatDateForInput(row.close_Dt) : '',
    closeRslt: row.close_RSLT || '',
    deliveryDt: row.delivery_DT ? formatDateForInput(row.delivery_DT) : '',
    updateId: row.update_ID || ''
  }
  showAsForm.value = true
}

function formatDateForInput(dateString) {
  if (!dateString) return ''
  try {
    const date = new Date(dateString)
    return date.toISOString().slice(0, 16).replace('T', ' ')
  } catch { return dateString }
}

async function saveAs() {
  try {
    const formData = {
      ordNo: currentTagNo.value,
      asDoc: asForm.value.asDoc,
      occrDt: asForm.value.occrDt,
      occrRsn: asForm.value.occrRsn,
      closeDt: asForm.value.closeDt,
      closeRslt: asForm.value.closeRslt,
      deliveryDt: asForm.value.deliveryDt,
      updateId: asForm.value.updateId
    }
    if (isEditMode.value) {
      formData.id = asForm.value.id
      await axios.put(`/tags/update_${currentTagNo.value}_as`, formData)
    } else {
      await axios.post(`/tags/${currentTagNo.value}_as`, formData)
    }
    await loadAsData(currentTagNo.value)
    showAsForm.value = false
    isEditMode.value = false
    ElMessage.success(isEditMode.value ? 'AS 정보가 수정되었습니다.' : 'AS 정보가 등록되었습니다.')
  } catch (e) {
    console.error('AS 저장 오류:', e)
    ElMessage.error('AS 정보 저장 중 오류가 발생했습니다.')
  }
}

async function deleteAs(row) {
  try {
    await ElMessageBox.confirm('정말 삭제하시겠습니까?', '확인', { confirmButtonText: '삭제', cancelButtonText: '취소', type: 'warning' })
    await axios.delete(`/tags/delete_as/${row.id}`)
    await loadAsData(currentTagNo.value)
    ElMessage.success('AS 정보가 삭제되었습니다.')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('AS 삭제 오류:', error)
      ElMessage.error('AS 정보 삭제 중 오류가 발생했습니다.')
    }
  }
}
</script>

