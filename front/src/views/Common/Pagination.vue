<template>
  <div class="pagination-wrapper">
    <el-pagination
        :current-page="modelCurrentPage"
        :page-size="modelPageSize"
        :page-sizes="pageSizes"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="onCurrentChange"
        @size-change="onSizeChange"
    />
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  currentPage: Number,
  pageSize: Number,
  total: Number,
  pageSizes: {
    type: Array,
    default: () => [5, 10, 20],
  },
})

const emit = defineEmits(['update:currentPage', 'update:pageSize'])

// computed로 “양방향 v-model”처럼 작동하도록 래핑
const modelCurrentPage = computed({
  get: () => props.currentPage,
  set: (val) => emit('update:currentPage', val),
})

const modelPageSize = computed({
  get: () => props.pageSize,
  set: (val) => emit('update:pageSize', val),
})

function onCurrentChange(val) {
  modelCurrentPage.value = val
}

function onSizeChange(val) {
  modelPageSize.value = val
}
</script>

<style scoped>
.pagination-wrapper {
  display: flex;
  justify-content: center;
}
</style>
