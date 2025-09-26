<template>
  <div class="report-calendar">
    <h3 class="text-2xl font-bold mb-4">일일 업무 보고</h3>

    <!-- 달력 -->
    <el-calendar v-model="currentDate" >
      <template #date-cell="{ data }"  @mousedown.prevent="onDateClick(toDate(data.day))">
       <div
            class="calendar-day"
            :class="[
        isHoliday(data.day) ? 'Holiday' : '',
        isSunday(data.day) ? 'isSunday' : '',
        isSaturday(data.day) ? 'isSaturday' : ''
      ]"  @mousedown.prevent="onDateClick(toDate(data.day))"
        >
          {{ toDate(data.day).getDate() }}
        </div>
      </template>
    </el-calendar>

    <!-- 선택된 날짜 모달 -->
    <el-dialog
        v-model="dialogVisible"
        :title="formatDate(selectedDate) + ' 일일보고'"
        width="600px"
        append-to-body
        class="custom-dialog"
    >
      <div v-if="reports[selectedDate]?.length" class="mb-4">
        <el-card
            v-for="(report, index) in reports[selectedDate]"
            :key="report.dailyReportInfoId"
            class="mb-3"
            shadow="hover"
        >
          <div class="flex justify-between items-center">
            <div>
              <strong>{{ report.title }}</strong>
              <p class="text-sm text-gray-500">{{ report.time }}</p>
              <p>{{ report.content }}</p>
            </div>
            <div class="flex gap-2">
              <el-button type="primary" size="small" @click="editReport(index)">수정</el-button>
              <el-button type="danger" size="small" @click="deleteReport(index)">삭제</el-button>
            </div>
          </div>
        </el-card>
      </div>
      <div v-else class="text-gray-500 text-center py-5">
        등록된 보고가 없습니다.
      </div>

      <el-form :model="newReport" label-width="80px" class="mt-4">
        <el-form-item label="제목">
          <el-input v-model="newReport.title" placeholder="제목을 입력하세요" />
        </el-form-item>

        <el-form-item label="시간">
          <el-time-picker
              v-model="newReport.time"
              is-range
              range-separator="~"
              start-placeholder="시작 시간"
              end-placeholder="종료 시간"
              format="HH:mm"
              value-format="HH:mm"
              style="width: 100%;"
          />
        </el-form-item>

        <el-form-item label="부서" prop="dept_cd">
          <div class="dept-input-group">
            <el-input v-model="newReport.deptCode" placeholder="부서명을 선택하세요" readonly />
            <el-button type="primary" @click="openDeptModal">
              <el-icon><Search /></el-icon>
              부서 선택
            </el-button>
          </div>
        </el-form-item>

        <el-form-item label="내용">
          <el-input
              type="textarea"
              v-model="newReport.content"
              placeholder="보고 내용을 입력하세요"
              rows="4"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">닫기</el-button>
        <el-button type="primary" @click="saveReport">저장</el-button>
      </template>
    </el-dialog>

    <!-- 부서 선택 모달 -->
    <el-dialog
        v-model="showDeptModal"
        title="부서 선택"
        width="800px"
        append-to-body
        :close-on-click-modal="false"
        class="dept-dialog"
    >
      <div class="dept-modal-content">
        <el-table
            :data="deptList"
            style="width: 100%"
            border
            resizable
            @row-click="selectDept"
            highlight-current-row
        >
          <el-table-column prop="deptCode" label="부서코드" width="120" resizable />
          <el-table-column prop="dept" label="부서명" min-width="200" resizable />
          <el-table-column prop="status" label="상태" width="100" resizable>
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
          <el-button @click="showDeptModal = false">취소</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import api from 'axios'
import { Search } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import DeptManagement from "../System/Dept/DeptManagement.js";
import DailyReportManagement from "./DailyReportManagement.js";


const currentDate = ref(new Date())
const dialogVisible = ref(false)
const selectedDate = ref('')
const reports = ref({})
const showDeptModal = ref(false)
const deptList = ref([])
const holidays = ref([])

const newReport = ref({
  dailyReportInfoId: null,
  deptCode: "",
  title: "",
  time: [],
  content: ""
})

// ------------------ 날짜 관련 ------------------
const formatDate = (date) => {
  const d = new Date(date)
  return `${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2,'0')}-${d.getDate().toString().padStart(2,'0')}`
}
function toDate(dateObj) {
  // dateObj가 Date면 그대로, 아니면 new Date()
  return dateObj instanceof Date ? dateObj : new Date(dateObj)
}

function isSunday(dateObj) {
  return toDate(dateObj).getDay() === 0
}

function isSaturday(dateObj) {
  return toDate(dateObj).getDay() === 6
}

function isHoliday(dateObj) {
  const d = toDate(dateObj)
  const str = formatDate(d)
  return holidays.value.includes(str)
}

// ------------------ 달력 클릭 ------------------
const onDateClick = async (date) => {
  selectedDate.value = formatDate(date)
  dialogVisible.value = true
  newReport.value = { dailyReportInfoId: null, title: "", time: [], deptCode: "", content: "" }
  await fetchReports(selectedDate.value)
}

// ------------------ 보고 CRUD ------------------

const fetchReports = async (date) => {
  try {
    const data = DailyReportManagement.fetchReportsByDate(date)
    reports.value[date] = data.map(r => ({
      dailyReportInfoId: r.dailyReportInfoId,
      title: r.txt.slice(0,10),
      time: [`${r.hour.toString().padStart(2,'0')}:${r.minute.toString().padStart(2,'0')}`],
      content: r.txt,
      deptCode: r.deptCode
    }))
  } catch (err) {
    console.error(err)
  }
}

const saveReport = async () => {
  try {
    const payload = {
      txt: newReport.value.content,
      hour: newReport.value.time[0]?.split(':')[0] || '',
      minute: newReport.value.time[0]?.split(':')[1] || '',
      deptCode: newReport.value.deptCode
    }
    if (newReport.value.dailyReportInfoId) {
      await DailyReportManagement.updateReport(newReport.value.dailyReportInfoId, payload)
    } else {
      await DailyReportManagement.createReport(payload)
    }
    await fetchReports(selectedDate.value)
    dialogVisible.value = false
  } catch (err) {
    console.error(err)
  }
}

const deleteReportByIndex = async (index) => {
  const report = reports.value[selectedDate.value][index]
  try {
    await DailyReportManagement.deleteReport(report.dailyReportInfoId)
    await DailyReportManagement.fetchReportsByDate(selectedDate.value)
  } catch (err) {
    console.error(err)
  }
}

const editReport = (index) => {
  newReport.value = { ...reports.value[selectedDate.value][index] }
  reports.value[selectedDate.value].splice(index, 1)
  dialogVisible.value = true
}

// ------------------ 공휴일 ------------------
async function fetchHolidays(year) {
  try {
    const response = await api.get(`/sys/holiday/${year}`)
    const data = typeof response.data === 'string' ? JSON.parse(response.data) : response.data
    const items = data.response?.body?.items?.item || []
    holidays.value = items
        .filter(i => i.isHoliday === 'Y')
        .map(i => {
          const y = i.locdate.toString().slice(0,4)
          const m = i.locdate.toString().slice(4,6)
          const d = i.locdate.toString().slice(6,8)
          return `${y}-${m}-${d}`
        })
  } catch (err) {
    console.error('공휴일 불러오기 실패:', err)
    holidays.value = []
  }
}

// ------------------ 부서 모달 ------------------
async function openDeptModal() {
  showDeptModal.value = true
  await loadDeptList()
}

function selectDept(dept) {
  if (dept.status === 'Y') {
    newReport.value.deptCode = dept.dept
    showDeptModal.value = false
    ElMessage.success(`${dept.dept} 부서가 선택되었습니다.`)
  } else {
    ElMessage.warning('사용중인 부서를 선택해주세요.')
  }
}

async function loadDeptList() {
  try {
    const response = await api.get('/user/getDeptList')
    deptList.value = response.data?.body || []
  } catch (err) {
    console.error(err)
    deptList.value = []
    ElMessage.error('부서 목록을 불러오는데 실패했습니다.')
  }
}

// ------------------ 생명주기 ------------------
onMounted(() => {
  const year = new Date().getFullYear()
  fetchHolidays(year)
  fetchReports(formatDate(currentDate.value))
})

watch(currentDate, (newDate) => {
  const year = new Date(newDate).getFullYear()
  fetchHolidays(year)
})
</script>
