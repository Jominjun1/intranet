<template>
  <div>
    <div class="search-header">
      <h3>🔍 공통 메뉴 관리</h3>
      <el-button type="info" :icon="QuestionFilled" circle size="small" @click="showSearchHelp" title="프로젝트 관리 도움말"/>
    </div>

  <div style="display: flex; justify-content: flex-end;">
    <el-button type="primary" @click="showAddDeptForm" v-if="userAcl >= 3">
      <el-icon><Plus /></el-icon>
      부서 등록
    </el-button>

    <el-button type="primary" @click="showAddProject" v-if="userAcl >= 3">
      <el-icon><Plus /></el-icon>
        프로젝트 등록
    </el-button>

    <el-button type="primary" @click="showAddUser" v-if="userAcl >= 3">
      <el-icon><Plus /></el-icon>
      사용자 등록
    </el-button>
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

import {QuestionFilled} from "@element-plus/icons-vue";
import {computed, ref} from "vue";
import DeptManagement from "../Dept/DeptManagement.js";
import {ElMessage} from "element-plus";

const isEditMode = ref(false)
const showDeptForm = ref(false)
const parentOptions = ref([])
const saving = ref(false)
const loading = ref(false)
const depts = ref([])

const userAcl = computed(() => parseInt(props.userInfo.user_acl || '0'))

const deptForm = ref({
  deptCode: '', dept: '', parentDeptCode: ''
})

const props = defineProps({
  userInfo: {
    type: Object, default: () => ({})
  }
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

function showAddDeptForm() {
  isEditMode.value = false
  deptForm.value = {
    deptCode: '', dept: '', parentDeptCode: ''
  }
  showDeptForm.value = true
}

function cancelDeptForm() {
  showDeptForm.value = false
  deptForm.value = {
    deptCode: '', dept: ''
  }
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
</script>