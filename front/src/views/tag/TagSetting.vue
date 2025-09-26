<template>
  <div class="tag-setting-page">
    <h2>세팅정보 관리</h2>
    <p>태그번호를 입력하여 세팅정보를 관리하세요.</p>

    <!-- 태그번호 검색 -->
    <div class="direct-search-section">
      <h3>🔍 태그번호 검색</h3>
      <el-form :inline="true" class="direct-search-form">
        <el-form-item label="태그번호">
          <el-input
              v-model="searchTagNo"
              placeholder="태그번호 일부 입력 (예: AABB)"
              clearable
              style="width: 350px;"
              @keyup.enter="searchTagNumbers"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="success" @click="searchSettingInfo">검색</el-button>
          <el-button @click="clearTagSearch">초기화</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div v-if="tagNumberList.length > 0" class="tag-search-results">
      <h3>검색된 태그번호 목록 (총 {{ totalTagCount }}개)</h3>
      <el-table :data="paginatedTagList" style="width: 100%" @row-click="selectTagNumber" border resizable>
        <el-table-column prop="tag_No" label="태그번호" width="300" align="center" resizable />
        <el-table-column prop="mac_Addr" label="MAC주소" width="200" align="center" resizable />
        <el-table-column prop="fac_Cd" label="공장코드" width="120" align="center" resizable />
        <el-table-column prop="fac_No" label="시리얼번호" width="120" align="center" resizable />
        <el-table-column label="작업" width="120" align="center" resizable>
          <template #default="{ row }">
            <el-button size="small" type="primary" @click.stop="selectTagNumber(row)">선택</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[5, 10, 20]"
            :total="tagNumberList.length"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 세팅정보 결과 -->
    <div v-if="settingInfoData" class="result-section">
      <div class="result-header">
        <h3>세팅정보</h3>
        <div class="action-buttons" v-if="userAcl >= 2">
          <el-button :type="isEditMode ? 'success' : 'primary'" @click="toggleEditMode">
            {{ isEditMode ? '저장' : '수정모드' }}
          </el-button>
          <el-button v-if="isEditMode" type="warning" @click="cancelEdit">취소</el-button>
          <el-button type="danger" @click="deleteSettingInfo">삭제</el-button>
        </div>
      </div>

      <div v-if="isEditMode" class="edit-mode-notice">편집 모드가 활성화되었습니다. 값을 수정한 후 저장 버튼을 클릭하세요.</div>

      <el-descriptions :column="2" border :class="{ 'editable-descriptions': isEditMode }">
        <el-descriptions-item label="시퀀스"><span>{{ settingInfoData[0]?.setting_info_seq }}</span></el-descriptions-item>
        <el-descriptions-item label="태그번호"><span>{{ settingInfoData[0]?.ordNo }}</span></el-descriptions-item>
        <el-descriptions-item label="하드웨어버전">
          <el-input v-if="isEditMode" v-model="settingInfoData[0].hw_version" size="small" placeholder="하드웨어 버전" />
          <span v-else>{{ getVersionDisplay(settingInfoData[0]?.hw_version) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="펌웨어버전">
          <el-input v-if="isEditMode" v-model="settingInfoData[0].fw_version" size="small" placeholder="펌웨어 버전" />
          <span v-else>{{ getVersionDisplay(settingInfoData[0]?.fw_version) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="LED ON 주기">
          <el-input v-if="isEditMode" v-model="settingInfoData[0].led_SEC" size="small" placeholder="LED 주기" />
          <span v-else>{{ getDisplayValue(settingInfoData[0]?.led_SEC) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="송신주기">
          <el-input v-if="isEditMode" v-model="settingInfoData[0].ri_MS" size="small" placeholder="송신주기" />
          <span v-else>{{ getDisplayValue(settingInfoData[0]?.ri_MS) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="신호 강도">
          <el-input v-if="isEditMode" v-model="settingInfoData[0].tx_POWER" size="small" placeholder="신호 강도" />
          <span v-else>{{ getDisplayValue(settingInfoData[0]?.tx_POWER) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="송신 방식">
          <el-input v-if="isEditMode" v-model="settingInfoData[0].random_DV" size="small" placeholder="송신 방식" />
          <span v-else>{{ getDisplayValue(settingInfoData[0]?.random_DV) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="RF 프로파일">
          <el-input v-if="isEditMode" v-model="settingInfoData[0].rf_PROFILE" size="small" placeholder="RF 프로파일" />
          <span v-else>{{ getDisplayValue(settingInfoData[0]?.rf_PROFILE) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="채널">
          <el-input v-if="isEditMode" v-model="settingInfoData[0].channel" size="small" placeholder="채널" />
          <span v-else>{{ getDisplayValue(settingInfoData[0]?.channel) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="서버 IP">
          <el-input v-if="isEditMode" v-model="settingInfoData[0].server_IP" size="small" placeholder="서버 IP" />
          <span v-else>{{ getDisplayValue(settingInfoData[0]?.server_IP) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="디바이스 IP">
          <el-input v-if="isEditMode" v-model="settingInfoData[0].device_IP" size="small" placeholder="디바이스 IP" />
          <span v-else>{{ getDisplayValue(settingInfoData[0]?.device_IP) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="게이트웨이">
          <el-input v-if="isEditMode" v-model="settingInfoData[0].gateway" size="small" placeholder="게이트웨이" />
          <span v-else>{{ getDisplayValue(settingInfoData[0]?.gateway) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="서브넷 마스크">
          <el-input v-if="isEditMode" v-model="settingInfoData[0].sub_MASK" size="small" placeholder="서브넷 마스크" />
          <span v-else>{{ getDisplayValue(settingInfoData[0]?.sub_MASK) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="포트">
          <el-input v-if="isEditMode" v-model="settingInfoData[0].port" size="small" placeholder="포트" />
          <span v-else>{{ getDisplayValue(settingInfoData[0]?.port) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="BC 버전">
          <el-input v-if="isEditMode" v-model="settingInfoData[0].bc_VER" size="small" placeholder="BC 버전" />
          <span v-else>{{ getVersionDisplay(settingInfoData[0]?.bc_VER) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="BC 주기">
          <el-input v-if="isEditMode" v-model="settingInfoData[0].bc_PERIOD" size="small" placeholder="BC 주기" />
          <span v-else>{{ getDisplayValue(settingInfoData[0]?.bc_PERIOD) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="BC 슬립">
          <el-input v-if="isEditMode" v-model="settingInfoData[0].bc_SLEEP" size="small" placeholder="BC 슬립" />
          <span v-else>{{ getDisplayValue(settingInfoData[0]?.bc_SLEEP) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="슬립 모드">
          <el-input v-if="isEditMode" v-model="settingInfoData[0].sleep_MODE" size="small" placeholder="슬립 모드" />
          <span v-else>{{ getDisplayValue(settingInfoData[0]?.sleep_MODE) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="슬립 주기">
          <el-input v-if="isEditMode" v-model="settingInfoData[0].sleep_PERIOD" size="small" placeholder="슬립 주기" />
          <span v-else>{{ getDisplayValue(settingInfoData[0]?.sleep_PERIOD) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="슬립 간격">
          <el-input v-if="isEditMode" v-model="settingInfoData[0].sleep_INTERVAL" size="small" placeholder="슬립 간격" />
          <span v-else>{{ getDisplayValue(settingInfoData[0]?.sleep_INTERVAL) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="슬립 임계값">
          <el-input v-if="isEditMode" v-model="settingInfoData[0].sleep_TH_HOLD" size="small" placeholder="슬립 임계값" />
          <span v-else>{{ getDisplayValue(settingInfoData[0]?.sleep_TH_HOLD) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="TDMA">
          <el-input v-if="isEditMode" v-model="settingInfoData[0].tdma" size="small" placeholder="TDMA" />
          <span v-else>{{ getDisplayValue(settingInfoData[0]?.tdma) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="상태">
          <el-input v-if="isEditMode" v-model="settingInfoData[0].status" size="small" placeholder="상태" />
          <span v-else>{{ getDisplayValue(settingInfoData[0]?.status) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="생성일">
          <el-input v-if="isEditMode" v-model="settingInfoData[0].create_DT" size="small" placeholder="생성일" />
          <span v-else>{{ formatDate(settingInfoData[0]?.create_DT) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="생성자">
          <el-input v-if="isEditMode" v-model="settingInfoData[0].create_ID" size="small" placeholder="생성자" />
          <span v-else>{{ getDisplayValue(settingInfoData[0]?.create_ID) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="수정일">
          <el-input v-if="isEditMode" v-model="settingInfoData[0].update_DT" size="small" placeholder="수정일" />
          <span v-else>{{ formatDate(settingInfoData[0]?.update_DT) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="수정자">
          <el-input v-if="isEditMode" v-model="settingInfoData[0].update_ID" size="small" placeholder="수정자" />
          <span v-else>{{ getDisplayValue(settingInfoData[0]?.update_ID) }}</span>
        </el-descriptions-item>
      </el-descriptions>
    </div>
  </div>
</template>

<script setup>
import {ref} from 'vue'
import axios from 'axios'
import {ElMessage} from 'element-plus'
import '../../css/Tag/TagSetting.css'


const userAcl = 3

const searchTagNo = ref('')
const tagNumberList = ref([])
const totalTagCount = ref(0)

const currentPage = ref(1)
const pageSize = ref(10)

const settingInfoData = ref(null)
const isEditMode = ref(false)
const originalSettingData = ref(null)
const currentTagNo = ref(null)


function handleSizeChange(size) { pageSize.value = size; currentPage.value = 1 }
function handleCurrentChange(page) { currentPage.value = page }
function startEditMode() { originalSettingData.value = JSON.parse(JSON.stringify(settingInfoData.value)); isEditMode.value = true }
function cancelEdit() { if (originalSettingData.value) settingInfoData.value = JSON.parse(JSON.stringify(originalSettingData.value)); isEditMode.value = false; originalSettingData.value = null }
function toggleEditMode() { isEditMode.value ? saveSettingInfo() : startEditMode() }

async function searchTagNumbers() {
  if (!searchTagNo.value.trim()) { ElMessage.warning('태그번호를 입력해주세요.'); return }
  try {
    const res = await axios.get('/tags/tag-numbers', { params: { query: searchTagNo.value.trim() } })
    const tagNumbers = res.data.body || res.data || []
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

function clearTagSearch() {
  searchTagNo.value = ''
  tagNumberList.value = []
  totalTagCount.value = 0
}

function selectTagNumber(row) {
  searchTagNo.value = row.tag_No
  tagNumberList.value = []
  totalTagCount.value = 0
  loadAsData(row.tag_No)
}

async function searchSettingInfo() {
  if (!searchTagNo.value.trim()) { ElMessage.warning('태그번호를 입력해주세요.'); return }
  try {
    const res = await axios.get(`/tags/setting_info_${searchTagNo.value.trim()}`)
    const data = res.data.body || res.data
    settingInfoData.value = data && !Array.isArray(data) ? [data] : (data || [])
    if (!settingInfoData.value || settingInfoData.value.length === 0) ElMessage.warning('해당 태그의 세팅정보가 없습니다.')
    isEditMode.value = false
    originalSettingData.value = null
  } catch (e) { console.error('세팅정보 조회 오류:', e); ElMessage.error('세팅정보를 불러오는 중 오류가 발생했습니다.'); settingInfoData.value = [] }
}

async function saveSettingInfo() {
  try {
    const convert = v => (v === null || v === undefined ? '' : v)
    const req = {
      HW_VER: convert(settingInfoData.value[0]?.hw_version),
      FW_VER: convert(settingInfoData.value[0]?.fw_version),
      LED_SEC: convert(settingInfoData.value[0]?.led_SEC),
      RI_MS: convert(settingInfoData.value[0]?.ri_MS),
      TX_POWER: convert(settingInfoData.value[0]?.tx_POWER),
      RANDOM_DV: convert(settingInfoData.value[0]?.random_DV),
      RF_PROFILE: convert(settingInfoData.value[0]?.rf_PROFILE),
      CHANNEL: convert(settingInfoData.value[0]?.channel),
      SLEEP_MODE: convert(settingInfoData.value[0]?.sleep_MODE),
      SLEEP_TH_HOLD: convert(settingInfoData.value[0]?.sleep_TH_HOLD),
      SLEEP_INTERVAL: convert(settingInfoData.value[0]?.sleep_INTERVAL),
      SLEEP_PERIOD: convert(settingInfoData.value[0]?.sleep_PERIOD),
      BC_VER: convert(settingInfoData.value[0]?.bc_VER),
      BC_PERIOD: convert(settingInfoData.value[0]?.bc_PERIOD),
      BC_SLEEP: convert(settingInfoData.value[0]?.bc_SLEEP),
      DEVICE_IP: convert(settingInfoData.value[0]?.device_IP),
      SERVER_IP: convert(settingInfoData.value[0]?.server_IP),
      GATEWAY: convert(settingInfoData.value[0]?.gateway),
      SUB_MASK: convert(settingInfoData.value[0]?.sub_MASK),
      TDMA: convert(settingInfoData.value[0]?.tdma),
      PORT: convert(settingInfoData.value[0]?.port)
    }
    const res = await axios.put(`/tags/update_setting_${settingInfoData.value[0]?.ordNo}`, req)
    if (res.data.statusCode === 'OK' || res.data.statusCodeValue === 200 || res.status === 200) {
      ElMessage.success('세팅정보가 성공적으로 수정되었습니다.')
      isEditMode.value = false
      originalSettingData.value = null
      await searchSettingInfo()
    } else { ElMessage.error('세팅정보 수정에 실패했습니다.') }
  } catch (e) { console.error('세팅정보 수정 오류:', e); ElMessage.error('세팅정보 수정 중 오류가 발생했습니다.') }
}

async function deleteSettingInfo() {
  try {
    await axios.put(`/tags/update_setting_${settingInfoData.value[0]?.ordNo}`, { status: 'Y', updateId: null })
    ElMessage.success('세팅정보가 삭제되었습니다.')
    await searchSettingInfo()
  } catch (e) { console.error('세팅정보 삭제 오류:', e); ElMessage.error('세팅정보 삭제 중 오류가 발생했습니다.') }
}

function getDisplayValue(value) { return value || '-' }
function getVersionDisplay(value) { if (!value || value === '-') return '-'; return value.startsWith('v') ? value : `v${value}` }

function formatDate(dateString) { if (!dateString) return '-'; try { const d = new Date(dateString); return d.toLocaleDateString('ko-KR', {year:'numeric',month:'2-digit',day:'2-digit',hour:'2-digit',minute:'2-digit'}) } catch { return dateString } }
</script>

