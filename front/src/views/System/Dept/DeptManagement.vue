<template>
  <div class="dept-management">
    <SearchDept v-model="searchForm" :loading="loading" @search="loadDepts" @reset="resetSearch"/>

    <div class="table-section">
      <el-table :data="displayedDepts" v-loading="loading" stripe border resizable style="width: 100%" empty-text="등록된 부서가 없습니다.">
        <el-table-column prop="deptCode" label="부서코드" align="center" resizable />
        <el-table-column prop="dept" label="부서명" min-align="center" resizable />
        <el-table-column prop="status" label="상태" align="center" resizable>
          <template #default="scope">
            <el-tag :type="scope.row.status === 'Y' ? 'success' : 'danger'">
              {{ scope.row.status === 'Y' ? '사용중' : '삭제됨' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="등록자" align="center" resizable />
        <el-table-column prop="regDt" label="등록일" width="180" align="center" resizable>
          <template #default="scope">
            {{ formatDate(scope.row.regDt) }}
          </template>
        </el-table-column>
        <el-table-column prop="updateDt" label="수정일" width="180" align="center" resizable>
          <template #default="scope">
            {{ formatDate(scope.row.updateDt) }}
          </template>
        </el-table-column>
        <el-table-column label="관리" width="180" fixed="right" v-if="userAcl >= 3" align="center" resizable>
          <template #default="scope">
            <div class="action-buttons">
              <el-button type="primary" size="small" @click="editDept(scope.row)">
                <el-icon><Edit /></el-icon>
                수정
              </el-button>
              <el-button type="danger" size="small" @click="deleteDept(scope.row)">
                <el-icon><Delete /></el-icon>
                삭제
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="showDeptForm" :title="isEditMode ? '부서 수정' : '부서 등록'" width="500px" :close-on-click-modal="false">
      <el-form ref="deptFormRef" :model="deptForm" :rules="deptFormRules" label-width="100px">
        <el-form-item label="부서코드" prop="deptCode">
          <el-input v-model="deptForm.deptCode" placeholder="부서코드를 입력하세요" :disabled="isEditMode"/>
        </el-form-item>
        <el-form-item label="부서명" prop="dept">
          <el-input v-model="deptForm.dept" placeholder="부서명을 입력하세요"/>
        </el-form-item>
        <el-form-item label="상위부서">
          <el-select v-model="deptForm.parentDeptCode" placeholder="상위부서를 선택하세요" clearable filterable>
            <el-option v-for="d in parentOptions" :key="d.deptCode" :label="`${d.dept} (${d.deptCode})`" :value="d.deptCode"/>
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="saveDept" :loading="saving">
            {{ isEditMode ? '수정' : '등록' }}
          </el-button>
          <el-button @click="cancelDeptForm">취소</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {computed, onMounted, ref} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {Delete, Edit} from '@element-plus/icons-vue'
import '../../../css/System/Dept/DeptManagement.css'
import DeptManagement from '../Dept/DeptManagement.js'
import SearchDept from "../../Common/SearchDept.vue";


const props = defineProps({
  userInfo: {
    type: Object, default: () => ({})
  }
})

const userAcl = computed(() => parseInt(props.userInfo.user_acl || '0'))

const loading = ref(false)
const saving = ref(false)
const depts = ref([])
const parentOptions = ref([])
const showDeptForm = ref(false)
const isEditMode = ref(false)
const deptFormRef = ref()

const searchForm = ref({
  dept: '', deptCode: '', status: 'all'
})
const displayedDepts = computed(() => {
  const name = (searchForm.value.dept || '').trim()
  const code = (searchForm.value.deptCode || '').trim()
  const stat = searchForm.value.status || 'all'

  return depts.value.filter(row => {
    const nameOk = name ? (row.dept || '').includes(name) : true
    const codeOk = code ? (row.deptCode || '').includes(code) : true
    const statusOk = stat === 'all' ? true : ((row.status || '').toUpperCase() === stat)
    return nameOk && codeOk && statusOk
  })
})


const deptForm = ref({
  deptCode: '', dept: '', parentDeptCode: ''
})

const deptFormRules = {
  deptCode: [
    { required: true, message: '부서코드를 입력하세요', trigger: 'blur' },
    { min: 2, max: 10, message: '부서코드는 2-10자 사이여야 합니다', trigger: 'blur' }
  ],
  dept: [
    { required: true, message: '부서명을 입력하세요', trigger: 'blur' },
    { min: 2, max: 50, message: '부서명은 2-50자 사이여야 합니다', trigger: 'blur' }
  ]
}

async function loadDepts() {
  loading.value = true
  try {
    const data = await DeptManagement.getDepts()
    depts.value = data
    parentOptions.value = data
    if (!data.length) ElMessage.info('등록된 부서가 없습니다.')
  } catch (error) {
    console.error('부서 목록 조회 오류:', error)
    ElMessage.error('부서 목록을 불러오는데 실패했습니다.')
  } finally {
    loading.value = false
  }
}

async function saveDept() {
  if (!deptFormRef.value) return
  try {
    await deptFormRef.value.validate()
    saving.value = true

    if (isEditMode.value) {
      await DeptManagement.updateDept(deptForm.value.deptCode, {
        dept: deptForm.value.dept, status: 'Y', parentDeptCode: deptForm.value.parentDeptCode || ''
      })
      ElMessage.success('부서 정보가 수정되었습니다.')
    } else {
      await DeptManagement.createDept({
        deptCode: deptForm.value.deptCode, dept: deptForm.value.dept, regDt: new Date(), parentDeptCode: deptForm.value.parentDeptCode || ''
      })
      ElMessage.success('부서가 등록되었습니다.')
    }

    showDeptForm.value = false
    await loadDepts()
  } catch (error) {
    console.error('부서 저장 오류:', error)
    ElMessage.error('부서 저장에 실패했습니다.')
  } finally {
    saving.value = false
  }
}

async function deleteDept(dept) {
  try {
    await ElMessageBox.confirm(
        `'${dept.dept}' 부서를 삭제하시겠습니까?`, '부서 삭제 확인',
        { confirmButtonText: '삭제', cancelButtonText: '취소', type: 'warning' }
    )
    await DeptManagement.deleteDept(dept.deptCode, dept.dept)
    ElMessage.success('부서가 삭제되었습니다.')
    await loadDepts()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('부서 삭제 오류:', error)
      ElMessage.error('부서 삭제에 실패했습니다.')
    }
  }
}

function resetSearch() {
  searchForm.value = {
    dept: '', deptCode: '', status: 'all'
  }
  loadDepts()
}

function showAddDeptForm() {
  isEditMode.value = false
  deptForm.value = {
    deptCode: '', dept: '', parentDeptCode: ''
  }
  showDeptForm.value = true
}

function editDept(dept) {
  isEditMode.value = true
  deptForm.value = {
    deptCode: dept.deptCode, dept: dept.dept, parentDeptCode: dept.parentDept?.deptCode || ''
  }
  showDeptForm.value = true
}

function cancelDeptForm() {
  showDeptForm.value = false
  deptForm.value = {
    deptCode: '', dept: ''
  }
}

function formatDate(dateString) {
  if (!dateString) return '-'
  try {
    const date = new Date(dateString)
    return date.toLocaleDateString('ko-KR', {
      year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit'
    })
  } catch (e) {
    return dateString
  }
}
onMounted(() => {
  loadDepts()
})
</script>
