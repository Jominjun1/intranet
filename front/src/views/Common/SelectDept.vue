<template>
  <el-dialog
      v-model="visible"
      title="부서 선택"
      width="800px"
      append-to-body
      :close-on-click-modal="false"
  >
    <div class="table-section">
      <el-table
          :data="filteredDepts"
          v-loading="loading"
          stripe
          border
          resizable
          style="width: 100%"
          empty-text="등록된 부서가 없습니다."
          @row-click="onSelectDept"
      >
        <el-table-column prop="deptCode" label="부서코드" align="center" resizable />
        <el-table-column prop="dept" label="부서명" min-align="center" resizable />
        <el-table-column prop="status" label="상태" align="center" resizable>
          <template #default="scope">
            <el-tag :type="scope.row.status === 'Y' ? 'success' : 'danger'">
              {{ scope.row.status === 'Y' ? '사용중' : '삭제됨' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="visible = false">취소</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import {ref, watch, computed, onMounted} from 'vue'
import DeptManagement from '../System/Dept/DeptManagement.js'
import {ElMessage} from 'element-plus'

const props = defineProps({
  modelValue: { type: Boolean, required: true },
  searchForm: { type: Object, default: () => ({dept:'', deptCode:'', status:'all'}) }
})

const emit = defineEmits(['update:modelValue', 'select'])

const visible = ref(props.modelValue)
watch(() => props.modelValue, val => visible.value = val)
watch(visible, val => emit('update:modelValue', val))

const depts = ref([])
const loading = ref(false)

const filteredDepts = computed(() => {
  const name = (props.searchForm.dept || '').trim()
  const code = (props.searchForm.deptCode || '').trim()
  const stat = props.searchForm.status || 'all'
  return depts.value.filter(row => {
    const nameOk = name ? row.dept.includes(name) : true
    const codeOk = code ? row.deptCode.includes(code) : true
    const statusOk = stat === 'all' ? true : (row.status.toUpperCase() === stat)
    return nameOk && codeOk && statusOk
  })
})

async function loadDepts() {
  loading.value = true
  try {
    const data = await DeptManagement.getDepts()
    depts.value = data || []
  } catch (err) {
    console.error(err)
    ElMessage.error('부서 목록 조회 실패')
    depts.value = []
  } finally {
    loading.value = false
  }
}

function onSelectDept(dept) {
  if (dept.status === 'Y') {
    emit('select', dept)
    visible.value = false
    ElMessage.success(`${dept.dept} 부서가 선택되었습니다.`)
  } else {
    ElMessage.warning('사용중인 부서를 선택해주세요.')
  }
}

onMounted(loadDepts)
</script>
