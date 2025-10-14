<template>
  <el-form :inline="true" class="search-form">
    <el-form-item label="프로젝트">
      <el-input
          v-model="localForm.searchProject"
          placeholder="프로젝트 명 입력"
          clearable
          style="width: 300px"
      />
    </el-form-item>
    <el-form-item label="PM">
      <el-input v-model="localForm.searchProjectPM" placeholder="PM 입력" clearable />
    </el-form-item>
    <el-form-item label="삭제여부">
      <el-select v-model="localForm.searchDelFilter" placeholder="삭제여부 선택" style="width: 150px;">
        <el-option v-for="option in delFilterOptions" :key="option.value" :label="option.label" :value="option.value" />
      </el-select>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="() => emit('search', localForm)"><el-icon><Search /></el-icon> 검색</el-button>
      <el-button @click="resetSearch"><el-icon><Refresh /></el-icon> 초기화</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup>
import { reactive, watch } from 'vue'
import {Search} from "@element-plus/icons-vue";

const props = defineProps({
  modelValue: Object,
  loading: Boolean
})
const emit = defineEmits(['update:modelValue', 'search', 'reset'])

const localForm = reactive({ ...props.modelValue })

// 양방향 바인딩 동기화
watch(
    () => props.modelValue,
    (val) => Object.assign(localForm, val),
    { deep: true }
)
watch(localForm, (val) => emit('update:modelValue', val), { deep: true })

const delFilterOptions = [
  { label: '전체', value: 'all' },
  { label: '사용중', value: 'active' },
  { label: '삭제됨', value: 'deleted' }
]

function resetSearch() {
  Object.assign(localForm, { searchProject: '', searchProjectPM: '', searchDelFilter: 'all' })
  emit('reset')
}
</script>
