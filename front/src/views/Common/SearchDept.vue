<template>
  <div class="search-section">
    <div class="search-header">
      <h3>🔍 부서 목록</h3>
      <el-button type="info" :icon="QuestionFilled" circle size="small" @click="showSearchHelp" title="프로젝트 관리 도움말"/>
    </div>
    <div class="search-controls">
      <div class="search-group">
        <label>부서명</label>
        <el-input v-model="localForm.dept" placeholder="부서명을 입력하세요" clearable @keyup.enter="emitSearch"/>
      </div>
      <div class="search-group">
        <label>부서코드</label>
        <el-input v-model="localForm.deptCode" placeholder="부서코드를 입력하세요" clearable @keyup.enter="emitSearch"/>
      </div>
      <div class="search-group">
        <label>상태</label>
        <el-select v-model="localForm.status" placeholder="상태 선택" clearable>
          <el-option label="전체" :value="'all'" />
          <el-option label="사용중" :value="'Y'" />
          <el-option label="삭제됨" :value="'N'" />
        </el-select>
      </div>
      <div class="search-actions">
        <el-button type="primary" @click="emitSearch" :loading="loading">
          <el-icon><Search /></el-icon> 검색
        </el-button>
        <el-button @click="emitReset">
          <el-icon><Refresh /></el-icon> 초기화
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import {reactive, watch} from "vue"
import {QuestionFilled, Search} from "@element-plus/icons-vue";

const props = defineProps({
  modelValue: Object, loading: Boolean
})

const emit = defineEmits(["update:modelValue", "search", "reset"])

const localForm = reactive({ ...props.modelValue })

// 내부 → 부모
watch(localForm, (val) => {
  emit("update:modelValue", val)
}, { deep: true })

// 부모 → 내부
watch(() => props.modelValue, (val) => {
  Object.assign(localForm, val || {})
}, { deep: true })

const emitSearch = () => emit("search", localForm)
const emitReset = () => {
  Object.assign(localForm, { dept: "", deptCode: "", status: "" })
  emit("reset")
}
</script>
