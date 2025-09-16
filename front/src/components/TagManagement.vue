<template>
  <div class="tag-management-page">
    <!-- 서브메뉴별 컨텐츠 -->
    <div v-if="currentSubMenu === 'tag-proc-step'" class="submenu-content">
      <h2>처리단계 관리</h2>
      <p>태그 정보를 검색하여 처리단계 정보를 관리하세요.</p>
      
      <!-- 태그 검색 폼 -->
      <div class="search-section">
        <div class="search-header">
          <h3>🔍 태그 목록</h3>
          <el-button type="text" @click="showSearchHelp = !showSearchHelp">
            <el-icon><QuestionFilled /></el-icon>
            검색 도움말
          </el-button>
        </div>
        
        <el-form :inline="true" class="search-form">
          <el-form-item label="태그번호">
            <el-input v-model="searchTagNo" placeholder="전체 태그번호 입력 (예: AABBCCDDEE01FAC001001)" clearable style="width: 300px;" />
          </el-form-item>
          <el-form-item label="MAC주소">
            <el-input v-model="searchMac" placeholder="MAC주소 입력" clearable />
          </el-form-item>
          <el-form-item label="시리얼번호">
            <el-input v-model="searchSn" placeholder="시리얼번호 입력" clearable />
          </el-form-item>
          <el-form-item label="공장코드">
            <el-input v-model="searchFacCd" placeholder="공장코드 입력" clearable />
          </el-form-item>
          <el-form-item label="삭제여부">
            <el-select 
              v-model="searchDelFilter" 
              placeholder="삭제여부 선택"
              style="width: 150px;"
              clearable
            >
              <el-option 
                v-for="option in delFilterOptions" 
                :key="option.value"
                :label="option.label" 
                :value="option.value" 
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="doSearch">태그 검색</el-button>
            <el-button @click="resetSearch">초기화</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 태그 검색 결과 테이블 -->
      <div v-if="tableData.length > 0">
        <h3>검색된 태그 목록</h3>
        <el-table :data="tableData" style="width:100%" v-loading="loading">
          <el-table-column prop="tag_No" label="태그번호" width="150" />
          <el-table-column prop="mac_Addr" label="MAC주소" width="150" />
          <el-table-column prop="fac_Cd" label="공장코드" width="100" />
          <el-table-column prop="fac_No" label="시리얼번호" width="120" />
          <el-table-column prop="Status" label="삭제여부" width="100">
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
            <el-button type="primary" @click="editProcStep">수정</el-button>
            <el-button type="danger" @click="deleteProcStep">삭제</el-button>
          </div>
        </div>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="태그번호">{{ procStepData.ordNo }}</el-descriptions-item>
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
    </div>
    
    <div v-else-if="currentSubMenu === 'tag-setting'" class="submenu-content">
      <h2>세팅정보 관리</h2>
      <p>태그번호를 입력하여 세팅정보를 관리하세요.</p>
      
      <!-- 태그번호 검색 섹션 -->
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
            <el-button type="success" @click="searchTagNumbers">검색</el-button>
            <el-button @click="clearTagSearch">초기화</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 태그번호 검색 결과 -->
      <div v-if="tagNumberList.length > 0" class="tag-search-results">
        <h3>검색된 태그번호 목록 (총 {{ totalTagCount }}개)</h3>
        <el-table :data="paginatedTagList" style="width: 100%" @row-click="selectTagNumber">
          <el-table-column prop="tag_No" label="태그번호" width="300" align="center" />
          <el-table-column prop="mac_Addr" label="MAC주소" width="200" align="center" />
          <el-table-column prop="fac_Cd" label="공장코드" width="120" align="center" />
          <el-table-column prop="fac_No" label="시리얼번호" width="120" align="center" />
          <el-table-column label="작업" width="120" align="center">
            <template #default="{ row }">
              <el-button size="small" type="primary" @click.stop="selectTagNumber(row)">선택</el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 페이지네이션 -->
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
      
      <!-- 세팅정보 조회 결과 -->
      <div v-if="settingInfoData" class="result-section">
        <div class="result-header">
          <h3>세팅정보</h3>
          <div class="action-buttons" v-if="userAcl >= 2">
            <el-button 
              :type="isEditMode ? 'success' : 'primary'" 
              @click="toggleEditMode"
            >
              {{ isEditMode ? '저장' : '수정모드' }}
            </el-button>
            <el-button 
              v-if="isEditMode" 
              type="warning" 
              @click="cancelEdit"
            >
              취소
            </el-button>
            <el-button type="danger" @click="deleteSettingInfo">삭제</el-button>
          </div>
        </div>
        
        <!-- 편집 모드 안내 메시지 -->
        <div v-if="isEditMode" class="edit-mode-notice">
          <el-icon><InfoFilled /></el-icon>
          편집 모드가 활성화되었습니다. 값을 수정한 후 저장 버튼을 클릭하세요.
        </div>
        
        <!-- 편집 가능한 전표형식 -->
        <el-descriptions 
          :column="2" 
          border 
          :class="{ 'editable-descriptions': isEditMode }"
        >
          <el-descriptions-item label="시퀀스">
            <span>{{ settingInfoData[0]?.setting_info_seq }}</span>
          </el-descriptions-item>
          
          <el-descriptions-item label="태그번호">
            <span>{{ settingInfoData[0]?.ordNo }}</span>
          </el-descriptions-item>
          
          <el-descriptions-item label="하드웨어버전">
            <el-input 
              v-if="isEditMode" 
              v-model="settingInfoData[0].hw_version" 
              size="small"
              placeholder="하드웨어 버전 (예: 1.2)"
            />
            <span v-else>{{ getVersionDisplay(settingInfoData[0]?.hw_version) }}</span>
          </el-descriptions-item>
          
          <el-descriptions-item label="펌웨어버전">
            <el-input 
              v-if="isEditMode" 
              v-model="settingInfoData[0].fw_version" 
              size="small"
              placeholder="펌웨어 버전 (예: 2.1)"
            />
            <span v-else>{{ getVersionDisplay(settingInfoData[0]?.fw_version) }}</span>
          </el-descriptions-item>
          
          <el-descriptions-item label="LED ON 주기">
            <el-input 
              v-if="isEditMode" 
              v-model="settingInfoData[0].led_SEC" 
              size="small"
              placeholder="LED 주기"
            />
            <span v-else>{{ getDisplayValue(settingInfoData[0]?.led_SEC) }}</span>
          </el-descriptions-item>
          
          <el-descriptions-item label="송신주기">
            <el-input 
              v-if="isEditMode" 
              v-model="settingInfoData[0].ri_MS" 
              size="small"
              placeholder="송신주기"
            />
            <span v-else>{{ getDisplayValue(settingInfoData[0]?.ri_MS) }}</span>
          </el-descriptions-item>
          
          <el-descriptions-item label="신호 강도">
            <el-input 
              v-if="isEditMode" 
              v-model="settingInfoData[0].tx_POWER" 
              size="small"
              placeholder="신호 강도"
            />
            <span v-else>{{ getDisplayValue(settingInfoData[0]?.tx_POWER) }}</span>
          </el-descriptions-item>
          
          <el-descriptions-item label="송신 방식">
            <el-input 
              v-if="isEditMode" 
              v-model="settingInfoData[0].random_DV" 
              size="small"
              placeholder="송신 방식"
            />
            <span v-else>{{ getDisplayValue(settingInfoData[0]?.random_DV) }}</span>
          </el-descriptions-item>
          
          <el-descriptions-item label="RF 프로파일">
            <el-input 
              v-if="isEditMode" 
              v-model="settingInfoData[0].rf_PROFILE" 
              size="small"
              placeholder="RF 프로파일"
            />
            <span v-else>{{ getDisplayValue(settingInfoData[0]?.rf_PROFILE) }}</span>
          </el-descriptions-item>
          
          <el-descriptions-item label="채널">
            <el-input 
              v-if="isEditMode" 
              v-model="settingInfoData[0].channel" 
              size="small"
              placeholder="채널"
            />
            <span v-else>{{ getDisplayValue(settingInfoData[0]?.channel) }}</span>
          </el-descriptions-item>
          
          <el-descriptions-item label="서버 IP">
            <el-input 
              v-if="isEditMode" 
              v-model="settingInfoData[0].server_IP" 
              size="small"
              placeholder="서버 IP"
            />
            <span v-else>{{ getDisplayValue(settingInfoData[0]?.server_IP) }}</span>
          </el-descriptions-item>
          
          <el-descriptions-item label="디바이스 IP">
            <el-input 
              v-if="isEditMode" 
              v-model="settingInfoData[0].device_IP" 
              size="small"
              placeholder="디바이스 IP"
            />
            <span v-else>{{ getDisplayValue(settingInfoData[0]?.device_IP) }}</span>
          </el-descriptions-item>
          
          <el-descriptions-item label="게이트웨이">
            <el-input 
              v-if="isEditMode" 
              v-model="settingInfoData[0].gateway"
              size="small"
              placeholder="게이트웨이"
            />
            <span v-else>{{ getDisplayValue(settingInfoData[0]?.gateway) }}</span>
          </el-descriptions-item>
          
          <el-descriptions-item label="서브넷 마스크">
            <el-input 
              v-if="isEditMode" 
              v-model="settingInfoData[0].sub_MASK"
              size="small"
              placeholder="서브넷 마스크"
            />
            <span v-else>{{ getDisplayValue(settingInfoData[0]?.sub_MASK) }}</span>
          </el-descriptions-item>
          
          <el-descriptions-item label="포트">
            <el-input 
              v-if="isEditMode" 
              v-model="settingInfoData[0].port"
              size="small"
              placeholder="포트"
            />
            <span v-else>{{ getDisplayValue(settingInfoData[0]?.port) }}</span>
          </el-descriptions-item>
          
          <el-descriptions-item label="BC 버전">
            <el-input 
              v-if="isEditMode" 
              v-model="settingInfoData[0].bc_VER"
              size="small"
              placeholder="BC 버전 (예: 1.0)"
            />
            <span v-else>{{ getVersionDisplay(settingInfoData[0]?.bc_VER) }}</span>
          </el-descriptions-item>
          
          <el-descriptions-item label="BC 주기">
            <el-input 
              v-if="isEditMode" 
              v-model="settingInfoData[0].bc_PERIOD"
              size="small"
              placeholder="BC 주기"
            />
            <span v-else>{{ getDisplayValue(settingInfoData[0]?.bc_PERIOD) }}</span>
          </el-descriptions-item>
          
          <el-descriptions-item label="BC 슬립">
            <el-input 
              v-if="isEditMode" 
              v-model="settingInfoData[0].bc_SLEEP"
              size="small"
              placeholder="BC 슬립"
            />
            <span v-else>{{ getDisplayValue(settingInfoData[0]?.bc_SLEEP) }}</span>
          </el-descriptions-item>
          
          <el-descriptions-item label="슬립 모드">
            <el-input 
              v-if="isEditMode" 
              v-model="settingInfoData[0].sleep_MODE"
              size="small"
              placeholder="슬립 모드"
            />
            <span v-else>{{ getDisplayValue(settingInfoData[0]?.sleep_MODE) }}</span>
          </el-descriptions-item>
          
          <el-descriptions-item label="슬립 주기">
            <el-input 
              v-if="isEditMode" 
              v-model="settingInfoData[0].sleep_PERIOD"
              size="small"
              placeholder="슬립 주기"
            />
            <span v-else>{{ getDisplayValue(settingInfoData[0]?.sleep_PERIOD) }}</span>
          </el-descriptions-item>
          
          <el-descriptions-item label="슬립 간격">
            <el-input 
              v-if="isEditMode" 
              v-model="settingInfoData[0].sleep_INTERVAL"
              size="small"
              placeholder="슬립 간격"
            />
            <span v-else>{{ getDisplayValue(settingInfoData[0]?.sleep_INTERVAL) }}</span>
          </el-descriptions-item>
          
          <el-descriptions-item label="슬립 임계값">
            <el-input 
              v-if="isEditMode" 
              v-model="settingInfoData[0].sleep_TH_HOLD"
              size="small"
              placeholder="슬립 임계값"
            />
            <span v-else>{{ getDisplayValue(settingInfoData[0]?.sleep_TH_HOLD) }}</span>
          </el-descriptions-item>
          
          <el-descriptions-item label="TDMA">
            <el-input 
              v-if="isEditMode" 
              v-model="settingInfoData[0].tdma"
              size="small"
              placeholder="TDMA"
            />
            <span v-else>{{ getDisplayValue(settingInfoData[0]?.tdma) }}</span>
          </el-descriptions-item>
          
          <el-descriptions-item label="상태">
            <el-input 
              v-if="isEditMode" 
              v-model="settingInfoData[0].status"
              size="small"
              placeholder="상태"
            />
            <span v-else>{{ getDisplayValue(settingInfoData[0]?.status) }}</span>
          </el-descriptions-item>
          
          <el-descriptions-item label="생성일">
            <el-input
                v-if="isEditMode"
                v-model="settingInfoData[0].create_DT"
                size="small"
                placeholder="생성일"
            />
            <span v-else>{{ formatDate(settingInfoData[0]?.create_DT) }}</span>
          </el-descriptions-item>
          
          <el-descriptions-item label="생성자">
            <el-input
            v-if="isEditMode"
            v-model="settingInfoData[0].create_ID"
            size="small"
            placeholder="생성자"
            />
            <span v-else>{{ getDisplayValue(settingInfoData[0]?.create_ID) }}</span>
          </el-descriptions-item>
          
          <el-descriptions-item label="수정일">
            <el-input
                v-if="isEditMode"
                v-model="settingInfoData[0].update_DT"
                size="small"
                placeholder="수정일"
            />
            <span v-else>{{ formatDate(settingInfoData[0].update_DT) }}</span>
          </el-descriptions-item>

          <el-descriptions-item label="수정자">
            <el-input
                v-if="isEditMode"
                v-model="settingInfoData[0].update_ID"
                size="small"
                placeholder="수정자"
            />
            <span v-else>{{ getDisplayValue(settingInfoData[0]?.update_ID) }}</span>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </div>
    
    <div v-else-if="currentSubMenu === 'tag-version'" class="submenu-content">
              <h2>버전 이력 관리</h2>
        <p>태그번호를 입력하여 버전 이력을 관리하세요.</p>
      
      <!-- 태그번호 검색 섹션 -->
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
            <el-button type="success" @click="searchTagNumbers">검색</el-button>
            <el-button @click="clearTagSearch">초기화</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 태그번호 검색 결과 -->
      <div v-if="tagNumberList.length > 0" class="tag-search-results">
        <h3>검색된 태그번호 목록 (총 {{ totalTagCount }}개)</h3>
        <el-table :data="paginatedTagList" style="width: 100%" @row-click="selectTagNumber">
          <el-table-column prop="tag_No" label="태그번호" width="300" align="center" />
          <el-table-column prop="mac_Addr" label="MAC주소" width="200" align="center" />
          <el-table-column prop="fac_Cd" label="공장코드" width="120" align="center" />
          <el-table-column prop="fac_No" label="시리얼번호" width="120" align="center" />
          <el-table-column label="작업" width="120" align="center">
            <template #default="{ row }">
              <el-button size="small" type="primary" @click.stop="selectTagNumber(row)">선택</el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 페이지네이션 -->
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
      
      <!-- 버전 이력 조회 결과 -->
      <div v-if="versionHistoryData && versionHistoryData.length > 0" class="result-section">
        <div class="result-header">
          <h3>버전 이력</h3>
          <div class="action-buttons" v-if="userAcl >= 2">
            <el-button type="primary" @click="addNewVersion">새 버전 등록</el-button>
          </div>
        </div>
        <el-table :data="versionHistoryData || []" style="width: 100%">
          <el-table-column prop="tag_version" label="버전" width="100" />
          <el-table-column prop="HW_VERSION" label="하드웨어버전" width="120" />
          <el-table-column prop="FW_VERSION" label="펌웨어버전" width="120" />
          <el-table-column prop="create_Dt" label="생성일" width="120">
            <template #default="{ row }">
              {{ formatDate(row.create_Dt) }}
            </template>
          </el-table-column>
          <el-table-column prop="create_Id" label="생성자" width="100" />
        </el-table>
      </div>
    </div>
    
    <div v-else-if="currentSubMenu === 'tag-common'" class="submenu-content">
              <h2>공통정보 관리</h2>
        <p>태그번호를 입력하여 공통정보를 관리하세요.</p>
      
      <!-- 태그번호 검색 섹션 -->
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
            <el-button type="success" @click="searchTagNumbers">검색</el-button>
            <el-button @click="clearTagSearch">초기화</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 태그번호 검색 결과 -->
      <div v-if="tagNumberList.length > 0" class="tag-search-results">
        <h3>검색된 태그번호 목록 (총 {{ totalTagCount }}개)</h3>
        <el-table :data="paginatedTagList" style="width: 100%" @row-click="selectTagNumber">
          <el-table-column prop="tag_No" label="태그번호" width="300" align="center" />
          <el-table-column prop="mac_Addr" label="MAC주소" width="200" align="center" />
          <el-table-column prop="fac_Cd" label="공장코드" width="120" align="center" />
          <el-table-column prop="fac_No" label="시리얼번호" width="120" align="center" />
          <el-table-column label="작업" width="120" align="center">
            <template #default="{ row }">
              <el-button size="small" type="primary" @click.stop="selectTagNumber(row)">선택</el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 페이지네이션 -->
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
      

      
      <!-- 공통정보 조회 결과 -->
      <div v-if="commonHistoryData && commonHistoryData.length > 0" class="result-section">
        <div class="result-header">
          <h3>공통정보</h3>
          <div class="action-buttons" v-if="userAcl >= 2">
            <el-button type="primary" @click="addNewCommon">새 공통정보 등록</el-button>
          </div>
        </div>
        <el-table :data="Array.isArray(commonHistoryData) ? commonHistoryData : [commonHistoryData]" style="width: 100%">
          <el-table-column prop="mac_ADDR" label="MAC주소" width="180" />
          <el-table-column prop="fac_CD" label="공장코드" width="100" />
          <el-table-column prop="fac_NO" label="시리얼번호" width="100" />
          <el-table-column prop="create_DT" label="생성일" width="140">
            <template #default="{ row }">
              {{ formatDate(row.create_DT) }}
            </template>
          </el-table-column>
          <el-table-column prop="create_ID" label="생성자" width="80" />
          <el-table-column prop="update_DT" label="수정일" width="140">
            <template #default="{ row }">
              {{ formatDate(row.update_DT) }}
            </template>
          </el-table-column>
          <el-table-column prop="update_ID" label="수정자" width="80" />
        </el-table>
      </div>
      
      <!-- 공통정보가 없을 때 메시지 -->
      <div v-else-if="commonHistoryData && commonHistoryData.length === 0" class="result-section">
        <h3>공통정보</h3>
        <el-alert
          title="공통정보가 없습니다"
          description="해당 태그의 공통정보가 없습니다."
          type="info"
          :closable="false"
          show-icon
        />
      </div>
    </div>
    
    <div v-else-if="currentSubMenu === 'tag-as'" class="submenu-content">
              <h2>AS 이력 관리</h2>
        <p>태그번호를 입력하여 AS 이력을 관리하세요.</p>
      
      <!-- 태그번호 검색 섹션 -->
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
            <el-button type="success" @click="searchTagNumbers">검색</el-button>
            <el-button @click="clearTagSearch">초기화</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 태그번호 검색 결과 -->
      <div v-if="tagNumberList.length > 0" class="tag-search-results">
        <h3>검색된 태그번호 목록 (총 {{ totalTagCount }}개)</h3>
        <el-table :data="paginatedTagList" style="width: 100%" @row-click="selectTagNumber">
          <el-table-column prop="tag_No" label="태그번호" width="300" align="center" />
          <el-table-column prop="mac_Addr" label="MAC주소" width="200" align="center" />
          <el-table-column prop="fac_Cd" label="공장코드" width="120" align="center" />
          <el-table-column prop="fac_No" label="시리얼번호" width="120" align="center" />
          <el-table-column label="작업" width="120" align="center">
            <template #default="{ row }">
              <el-button size="small" type="primary" @click.stop="selectTagNumber(row)">선택</el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 페이지네이션 -->
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
      

      
      <!-- AS이력 조회 결과 -->
      <div v-if="asInfoData && asInfoData.length > 0" class="result-section">
        <h3>AS 이력</h3>
        <el-table :data="asInfoData" style="width: 100%">
          <el-table-column prop="as_Cnt" label="AS 횟수" width="80" align="center" />
          <el-table-column prop="mac_ADDR" label="MAC주소" width="150" align="center" />
          <el-table-column prop="as_Doc" label="AS 문서번호" width="120" align="center" />
          <el-table-column prop="occr_Dt" label="AS 발생일" width="120" align="center">
            <template #default="{ row }">
              {{ formatDate(row.occr_Dt) }}
            </template>
          </el-table-column>
          <el-table-column prop="occr_RSN" label="AS 발생사유" width="150" align="center" />
          <el-table-column prop="close_Dt" label="AS 종결일" width="120" align="center">
            <template #default="{ row }">
              {{ formatDate(row.close_Dt) }}
            </template>
          </el-table-column>
          <el-table-column prop="close_RSLT" label="AS 처리 결과" width="150" align="center" />
          <el-table-column prop="delivery_DT" label="납품일" width="120" align="center">
            <template #default="{ row }">
              {{ formatDate(row.delivery_DT) }}
            </template>
          </el-table-column>
        </el-table>
      </div>
      
      <!-- AS 이력이 없을 때 메시지 -->
      <div v-else-if="tableData.length > 0 && (!asInfoData || asInfoData.length === 0)" class="result-section">
        <h3>AS 이력</h3>
        <el-alert
          title="AS 이력이 없습니다"
          description="해당 태그의 AS 이력이 없습니다."
          type="info"
          :closable="false"
          show-icon
        />
      </div>
    </div>
    
    <!-- 메인 검색 영역 (기본 화면) -->
    <div v-else-if="!currentSubMenu || currentSubMenu === null" class="submenu-content">
      <h2>태그 검색</h2>
      <p>태그 정보를 검색하여 상세 정보를 조회하세요.</p>
      
      <div class="search-section">
        <div class="search-header">
          <h3>🔍 태그 검색</h3>
          <el-button type="text" @click="showSearchHelp = !showSearchHelp">
            <el-icon><QuestionFilled /></el-icon>
            검색 도움말
          </el-button>
        </div>
        <el-form :inline="true" class="search-form">
          <el-form-item label="태그번호">
            <el-input v-model="searchTagNo" placeholder="전체 태그번호 입력 (예: AABBCCDDEE01FAC001001)" clearable style="width: 300px;" />
          </el-form-item>
          <el-form-item label="MAC주소">
            <el-input v-model="searchMac" placeholder="MAC주소 입력" clearable />
          </el-form-item>
          <el-form-item label="시리얼번호">
            <el-input v-model="searchSn" placeholder="시리얼번호 입력" clearable />
          </el-form-item>
          <el-form-item label="공장코드">
            <el-input v-model="searchFacCd" placeholder="공장코드 입력" clearable />
          </el-form-item>
          <el-form-item label="삭제여부">
            <el-select 
              v-model="searchDelFilter" 
              placeholder="삭제여부 선택"
              style="width: 150px;"
              clearable
            >
              <el-option 
                v-for="option in delFilterOptions" 
                :key="option.value"
                :label="option.label" 
                :value="option.value" 
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="doSearch">태그 검색</el-button>
            <el-button @click="resetSearch">초기화</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 태그 검색 결과 테이블 -->
      <div v-if="tableData.length > 0">
        <h3>검색된 태그 목록</h3>
        <el-table :data="tableData" style="width:100%" v-loading="loading">
          <el-table-column prop="tag_No" label="태그번호" width="150" align="center" />
          <el-table-column prop="mac_Addr" label="MAC주소" width="150" align="center" />
          <el-table-column prop="fac_Cd" label="공장코드" width="100" align="center" />
          <el-table-column prop="fac_No" label="시리얼번호" width="120" align="center" />
          <el-table-column prop="tag_Version" label="제품버전" width="100" align="center" />
          <el-table-column prop="tag_Type" label="태그타입" width="100" align="center" />
          <el-table-column prop="erp_Code" label="ERP코드" width="120" align="center" />
          <el-table-column prop="Mng_Category" label="관리카테고리" width="120" align="center" />
          <el-table-column prop="Lot" label="LOT번호" width="120" align="center" />
          <el-table-column prop="Prod_order" label="생산지시" width="120" align="center" />
          <el-table-column prop="Project_code" label="프로젝트코드" width="120" align="center" />
          <el-table-column prop="Project_manager" label="프로젝트매니저" width="120" align="center" />
          <el-table-column prop="Mac_duple_yn" label="MAC중복여부" width="120" align="center" />
          <el-table-column prop="as_Cnt" label="AS횟수" width="80" align="center" />
          <el-table-column prop="Status" label="삭제여부" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="row.Status === 'Y' ? 'danger' : 'success'">
                {{ row.Status === 'Y' ? '삭제됨' : '사용중' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="작업" width="250" align="center">
            <template #default="{ row }">
              <el-button size="small" @click="showProcStep(row.tag_No)">처리단계</el-button>
              <el-button size="small" @click="showSettingInfo(row.tag_No)">세팅정보</el-button>
              <el-button size="small" @click="showVersionHistory(row.tag_No)">버전이력</el-button>
              <el-button size="small" @click="showCommonHistory(row.tag_No)">공통정보</el-button>
              <el-button size="small" @click="showAsInfo(row.tag_No)">AS이력</el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 페이지네이션 -->
        <div class="pagination-section" v-if="tableData.length > 0">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="tableData.length"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>

   
    <!-- 모달들 -->
    <!-- 처리단계 모달 -->
    <el-dialog v-model="procStepVisible" title="처리단계 정보" width="600">
      <div v-if="procStepData">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="태그번호">{{ procStepData.ordNo }}</el-descriptions-item>
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
      <div v-else>
        <p>처리단계 정보가 없습니다.</p>
      </div>
    </el-dialog>

    <!-- 세팅정보 모달 -->
    <el-dialog v-model="settingInfoVisible" title="세팅정보 (제품버전)" width="1000">
      <div v-if="settingInfoData">
        <div class="setting-actions" v-if="userAcl >= 2">
          <el-button type="primary" @click="editSettingInfo">세팅정보 수정</el-button>
        </div>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="태그번호">{{ settingInfoData.ordNo }}</el-descriptions-item>
          <el-descriptions-item label="하드웨어버전">{{ settingInfoData.hw_VER }}</el-descriptions-item>
          <el-descriptions-item label="펌웨어버전">{{ settingInfoData.fw_VER }}</el-descriptions-item>
          <el-descriptions-item label="LED ON 주기">{{ settingInfoData.led_SEC }}</el-descriptions-item>
          <el-descriptions-item label="송신주기">{{ settingInfoData.ri_MS }}</el-descriptions-item>
          <el-descriptions-item label="신호 강도">{{ settingInfoData.tx_POWER }}</el-descriptions-item>
          <el-descriptions-item label="송신 방식">{{ settingInfoData.random_DV }}</el-descriptions-item>
          <el-descriptions-item label="RF 프로파일">{{ settingInfoData.rf_PROFILE }}</el-descriptions-item>
          <el-descriptions-item label="통신 채널">{{ settingInfoData.channel }}</el-descriptions-item>
          <el-descriptions-item label="슬립모드">{{ settingInfoData.sleep_MODE }}</el-descriptions-item>
          <el-descriptions-item label="슬립모드 강도">{{ settingInfoData.sleep_TH_HOLD }}</el-descriptions-item>
          <el-descriptions-item label="RISM">{{ settingInfoData.sleep_INTERVAL }}</el-descriptions-item>
          <el-descriptions-item label="슬립모드 진입시간">{{ settingInfoData.sleep_PERIOD }}</el-descriptions-item>
          <el-descriptions-item label="Back Channel 버전">{{ settingInfoData.bc_VER }}</el-descriptions-item>
          <el-descriptions-item label="Back Channel 주기">{{ settingInfoData.bc_PERIOD }}</el-descriptions-item>
          <el-descriptions-item label="BC Sleep 주기">{{ settingInfoData.bc_SLEEP }}</el-descriptions-item>
          <el-descriptions-item label="디바이스 IP">{{ settingInfoData.device_IP }}</el-descriptions-item>
          <el-descriptions-item label="서버 IP">{{ settingInfoData.server_IP }}</el-descriptions-item>
          <el-descriptions-item label="게이트웨이">{{ settingInfoData.gateway }}</el-descriptions-item>
          <el-descriptions-item label="서브넷 마스크">{{ settingInfoData.sub_MASK }}</el-descriptions-item>
          <el-descriptions-item label="TDMA">{{ settingInfoData.tdma }}</el-descriptions-item>
          <el-descriptions-item label="포트 번호">{{ settingInfoData.port }}</el-descriptions-item>
          <el-descriptions-item label="생성일">{{ formatDate(settingInfoData.create_DT) }}</el-descriptions-item>
          <el-descriptions-item label="생성자">{{ settingInfoData.create_ID }}</el-descriptions-item>
          <el-descriptions-item label="수정일">{{ formatDate(settingInfoData.update_DT) }}</el-descriptions-item>
          <el-descriptions-item label="수정자">{{ settingInfoData.update_ID }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <div v-else>
        <p>세팅정보가 없습니다.</p>
      </div>
    </el-dialog>

    <!-- 버전이력 모달 -->
    <el-dialog v-model="versionHistoryVisible" title="제품버전 이력" width="800">
      <div class="version-actions" v-if="userAcl >= 2">
        <el-button type="primary" @click="addNewVersion">새 버전 등록</el-button>
      </div>
      <el-table :data="versionHistoryData || []" style="width: 100%">
        <el-table-column prop="TAG_VER" label="버전" width="100" />
        <el-table-column prop="HW_VER" label="하드웨어버전" width="120" />
        <el-table-column prop="FW_VER" label="펌웨어버전" width="120" />
        <el-table-column prop="CREATE_DT" label="생성일" width="120">
          <template #default="{ row }">
            {{ formatDate(row.CREATE_DT) }}
          </template>
        </el-table-column>
        <el-table-column prop="CREATE_ID" label="생성자" width="100" />
        <el-table-column label="작업" width="150" v-if="userAcl >= 2">
          <template #default="{ row }">
            <el-button size="small" @click="editVersion(row)">수정</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 공통정보 이력 모달 -->
    <el-dialog v-model="commonHistoryVisible" title="공통정보 이력" width="800">
      <div class="common-actions" v-if="userAcl >= 2">
        <el-button type="primary" @click="addNewCommon">새 공통정보 등록</el-button>
      </div>
      <el-table :data="commonHistoryData" style="width: 100%">
        <el-table-column prop="mac_ADDR" label="MAC주소" width="150" />
        <el-table-column prop="fac_CD" label="공장코드" width="100" />
        <el-table-column prop="fac_NO" label="시리얼번호" width="120" />
        <el-table-column prop="CREATE_DT" label="생성일" width="120">
          <template #default="{ row }">
            {{ formatDate(row.CREATE_DT) }}
          </template>
        </el-table-column>
        <el-table-column prop="CREATE_ID" label="생성자" width="100" />
        <el-table-column label="작업" width="150" v-if="userAcl >= 2">
          <template #default="{ row }">
            <el-button size="small" @click="editCommon(row)">수정</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- AS이력 모달 -->
    <el-dialog v-model="asInfoVisible" title="AS 이력" width="1200">
      <div class="as-actions" v-if="userAcl >= 2">
        <el-button type="primary" @click="addNewAs">새 AS 등록</el-button>
      </div>
      <el-table :data="asInfoData" style="width: 100%; margin: 0 auto;">
        <el-table-column prop="as_Cnt" label="AS 횟수" width="80" align="center" />
        <el-table-column prop="mac_ADDR" label="MAC주소" width="150" align="center" />
        <el-table-column prop="as_Doc" label="AS 문서번호" width="120" align="center" />
        <el-table-column prop="occr_Dt" label="AS 발생일" width="120" align="center">
          <template #default="{ row }">
            {{ formatDate(row.occr_Dt) }}
          </template>
        </el-table-column>
        <el-table-column prop="occr_RSN" label="AS 발생사유" width="150" align="center" />
        <el-table-column prop="close_Dt" label="AS 종결일" width="120" align="center">
          <template #default="{ row }">
            {{ formatDate(row.close_Dt) }}
          </template>
        </el-table-column>
        <el-table-column prop="close_RSLT" label="AS 처리결과" width="150" align="center" />
        <el-table-column prop="delivery_DT" label="납품일" width="120" align="center">
          <template #default="{ row }">
            {{ formatDate(row.delivery_DT) }}
          </template>
        </el-table-column>
        <el-table-column prop="create_DT" label="생성일" width="120" align="center">
          <template #default="{ row }">
            {{ formatDate(row.create_DT) }}
          </template>
        </el-table-column>
        <el-table-column prop="create_ID" label="생성자" width="100" align="center" />
        <el-table-column prop="update_DT" label="수정일" width="120" align="center">
          <template #default="{ row }">
            {{ formatDate(row.update_DT) }}
          </template>
        </el-table-column>
        <el-table-column prop="update_ID" label="수정자" width="100" align="center" />
        <el-table-column prop="del_YN" label="삭제여부" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.del_YN === 'Y' ? 'danger' : 'success'">
              {{ row.del_YN === 'Y' ? '삭제됨' : '사용중' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="작업" width="200" align="center" v-if="userAcl >= 2">
          <template #default="{ row }">
            <el-button size="small" @click="editAs(row)" style="margin-right: 5px;">수정</el-button>
            <el-button
              v-if="row.del_YN !== 'Y'"
              size="small"
              type="danger"
              @click="deleteAs(row)"
              style="margin-left: 5px;"
            >
              삭제
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- AS 등록/수정 폼 모달 -->
    <el-dialog v-model="showAsForm" :title="isEditMode ? 'AS 수정' : 'AS 등록'" width="600px">
      <el-form :model="asForm" label-width="120px">
        <el-form-item label="AS 문서번호">
          <el-input v-model="asForm.asDoc" placeholder="AS 문서번호 입력" />
        </el-form-item>
        <el-form-item label="AS 발생일">
          <el-date-picker
            v-model="asForm.occrDt"
            type="datetime"
            placeholder="AS 발생일 선택"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="AS 발생사유">
          <el-input v-model="asForm.occrRsn" type="textarea" placeholder="AS 발생사유 입력" />
        </el-form-item>
        <el-form-item label="AS 종결일">
          <el-date-picker
            v-model="asForm.closeDt"
            type="datetime"
            placeholder="AS 종결일 선택"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="AS 처리결과">
          <el-input v-model="asForm.closeRslt" type="textarea" placeholder="AS 처리결과 입력" />
        </el-form-item>
        <el-form-item label="납품일">
          <el-date-picker
            v-model="asForm.deliveryDt"
            type="datetime"
            placeholder="납품일 선택"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="수정자">
          <el-input v-model="asForm.updateId" placeholder="수정자 사번 입력" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAsForm = false">취소</el-button>
          <el-button type="primary" @click="saveAs">저장</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 검색 도움말 사이드바 -->
    <div class="search-help-sidebar" :class="{ 'show': showSearchHelp }">
      <div class="sidebar-header">
        <h3>🔍 검색 도움말</h3>
        <el-button type="text"
          :icon="Close" 
          @click="showSearchHelp = false"
          class="close-btn"
        />
      </div>
      <div class="search-help-content">
        <p>다음 조건 중 <strong>하나 이상</strong>을 입력하여 검색할 수 있습니다:</p>
        
        <div class="help-item">
          <h5>📱 MAC주소</h5>
          <p>태그의 MAC 주소를 입력하세요. (예: 00:11:22:33:44:55)</p>
        </div>
        
        <div class="help-item">
          <h5>🏭 공장코드</h5>
          <p>태그가 설치된 공장의 코드를 입력하세요.</p>
        </div>
        
        <div class="help-item">
          <h5>🔢 시리얼번호</h5>
          <p>태그의 시리얼 번호를 입력하세요.</p>
        </div>
        
        <div class="help-item">
          <h5>🗑️ 삭제여부</h5>
          <p>전체/사용중/삭제됨 중 선택하여 필터링할 수 있습니다.</p>
        </div>
        
        <el-alert
          title="💡 팁"
          description="검색 조건을 입력하지 않으면 데이터가 표시되지 않습니다. 정확한 검색을 위해 가능한 많은 정보를 입력해주세요."
          type="success"
          :closable="false"
          show-icon
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { QuestionFilled, Close, InfoFilled } from '@element-plus/icons-vue'
import Header from './Header.vue'


// Props
const props = defineProps({
  userInfo: {
    type: Object,
    default: () => ({})
  },
  activeMenu: {
    type: String,
    default: 'tag-management'
  },
  ordNo: {
    type: String,
    default: null
  },
  subMenu: {
    type: String,
    default: null
  }
})

// Emits
const emit = defineEmits(['menu-select', 'user-command'])

// 라우터 설정
const router = useRouter()
const route = useRoute()

// 사용자 정보
const userInfo = computed(() => props.userInfo)
const userAcl = computed(() => parseInt(userInfo.value.user_acl || 0))



// 검색 조건
const searchMac = ref('')
const searchSn = ref('')
const searchFacCd = ref('')
const searchDelFilter = ref('')

// 삭제여부 옵션
const delFilterOptions = [
  { label: '전체', value: 'all' },
  { label: '사용중', value: 'active' },
  { label: '삭제됨', value: 'deleted' }
]

// 선택된 삭제여부 라벨
const selectedDelFilterLabel = computed(() => {
  const option = delFilterOptions.find(opt => opt.value === searchDelFilter.value)
  return option ? option.label : ''
})

// 테이블 데이터
const tableData = ref([])
const loading = ref(false)

// 페이지네이션 상태
const currentPage = ref(1)
const pageSize = ref(10)
const totalTagCount = ref(0)

// 서브메뉴 관련 상태
const currentSubMenu = ref(null)

// 세팅정보 편집 모드 상태
const isEditMode = ref(false)
const originalSettingData = ref(null)

const searchTagNo = ref('')
const tagNumberList = ref([])



const filteredData = computed(() => {
  if (!Array.isArray(tableData.value)) {
    return []
  }
  
  return tableData.value.filter(row => {
    const macMatch = searchMac.value ? row.mac_Addr?.includes(searchMac.value) : true
    const snMatch = searchSn.value ? row.fac_No?.includes(searchSn.value) : true
    const facCdMatch = searchFacCd.value ? row.fac_Cd?.includes(searchFacCd.value) : true
    return macMatch && snMatch && facCdMatch
  })
})

// 페이지네이션된 데이터
const paginatedData = computed(() => {
  const startIndex = (currentPage.value - 1) * pageSize.value
  const endIndex = startIndex + pageSize.value
  return filteredData.value.slice(startIndex, endIndex)
})

// 페이지네이션 이벤트 핸들러
function handleSizeChange(size) {
  pageSize.value = size
  currentPage.value = 1 // 페이지 크기가 변경되면 첫 페이지로 이동
}

function handleCurrentChange(page) {
  currentPage.value = page
}

async function doSearch() {
  console.log('검색 시작:', { 
    searchTagNo: searchTagNo.value,
    searchMac: searchMac.value, 
    searchSn: searchSn.value, 
    searchFacCd: searchFacCd.value,
    searchDelFilter: searchDelFilter.value 
  })
  
  // 태그번호 전체가 입력된 경우 자동으로 분해
  let macAddr = searchMac.value.trim()
  let facCd = searchFacCd.value.trim()
  let facNo = searchSn.value.trim()
  
  // 태그번호 전체가 입력된 경우 (예: AABBCCDDEE01FAC001001)
  if (searchTagNo.value && searchTagNo.value.trim()) {
    const ordNo = searchTagNo.value.trim()
    if (ordNo.length >= 20) {
      // MAC주소 부분 (12자리)
      macAddr = ordNo.substring(0, 12)
      // 공장코드 부분 (6자리)
      facCd = ordNo.substring(12, 18)
      // 시리얼번호 부분 (나머지)
      facNo = ordNo.substring(18)
      
      console.log('태그번호 분해:', { macAddr, facCd, facNo })
    } else {
      // 20자 미만이면 MAC주소 검색에 사용
      macAddr = ordNo
    }
  }
  
  // 검색 조건이 없으면 경고 메시지 표시
  const hasSearchCondition = (searchTagNo.value && searchTagNo.value.trim()) || 
                           (macAddr && macAddr.trim()) || 
                           (facCd && facCd.trim()) || 
                           (facNo && facNo.trim())
  
  if (!hasSearchCondition) {
    ElMessage.warning('검색 조건을 하나 이상 입력해주세요.')
    return
  }
  
  loading.value = true
  try {
    // 빈 문자열이 아닌 경우에만 파라미터에 포함
    const params = {}
    if (macAddr && macAddr.trim()) {
      params.macAddr = macAddr.trim()
    }
    if (facCd && facCd.trim()) {
      params.facCd = facCd.trim()
    }
    if (facNo && facNo.trim()) {
      params.facNo = facNo.trim()
    }
    if (searchDelFilter.value && searchDelFilter.value !== 'all') {
      params.delFilter = searchDelFilter.value
    }
    
    console.log('API 요청 파라미터:', params)
    const res = await axios.get('/tags/getTagList', { params })
    
    // API 응답 구조에 맞게 데이터 추출
    let responseData = res.data
    
    // 응답이 래핑된 경우 body에서 추출
    if (responseData && typeof responseData === 'object' && responseData.body !== undefined) {
      responseData = responseData.body
    }
    
    // 배열이 아닌 경우 빈 배열로 설정
    tableData.value = Array.isArray(responseData) ? responseData : []
    
    // 검색 후 첫 페이지로 이동
    currentPage.value = 1
    
    console.log('검색 결과:', responseData)
    console.log('결과 개수:', tableData.value.length)
    
    // 검색 결과가 있으면 첫 번째 태그의 태그번호를 저장
    if (tableData.value.length > 0 && tableData.value[0].ordNo) {
      const ordNo = tableData.value[0].ordNo
      sessionStorage.setItem('currentTagNo', ordNo)
      console.log('현재 태그번호 저장:', ordNo)
      
      // 검색 결과가 하나의 태그만 나오면 자동으로 해당 서브메뉴의 상세 정보 로드
      if (tableData.value.length === 1 && currentSubMenu.value) {
        console.log('단일 태그 검색 결과, 상세 정보 자동 로드:', currentSubMenu.value)
        loadDetailDataBySubMenu(ordNo, currentSubMenu.value)
      }
      
      // AS 이력조회 서브메뉴에서 검색 시 AS 이력 자동 로드
      if (currentSubMenu.value === 'tag-as' && tableData.value.length > 0) {
        console.log('AS 이력조회 서브메뉴에서 검색됨, AS 이력 자동 로드 시작')
        await loadAsForFirstTag()
      }
    }
    
    if (tableData.value.length === 0) {
      ElMessage.info('검색 결과가 없습니다.')
    }
  } catch (error) {
    console.error('검색 오류:', error)
    console.error('오류 응답:', error.response?.data)
    ElMessage.error('검색 중 오류가 발생했습니다.')
  } finally {
    loading.value = false
  }
}

function resetSearch() {
  searchTagNo.value = ''
  searchMac.value = ''
  searchSn.value = ''
  searchFacCd.value = ''
  searchDelFilter.value = 'all'
  tableData.value = []
  tagNumberList.value = []
  commonHistoryData.value = []
  versionHistoryData.value = []
  asInfoData.value = []
  currentPage.value = 1 // 초기화 시 첫 페이지로 이동
}

// 헤더 이벤트 핸들러
function handleMenuSelect(key) {
  emit('menu-select', key)
}

function handleUserCommand(command) {
  emit('user-command', command)
}

// 모달 상태 및 데이터
const procStepVisible = ref(false)
const procStepData = ref(null)
const settingInfoVisible = ref(false)
const settingInfoData = ref(null)
const versionHistoryVisible = ref(false)
const versionHistoryData = ref([])

// versionHistoryData를 안전하게 초기화하는 함수
function resetVersionHistoryData() {
  versionHistoryData.value = []
}
const commonHistoryVisible = ref(false)
const commonHistoryData = ref([])
const asInfoVisible = ref(false)
const asInfoData = ref(null)
const showSearchHelp = ref(false)
const showAsForm = ref(false)
const currentTagNo = ref(null)
const asForm = ref({
  id: null,
  asDoc: '',
  occrDt: '',
  occrRsn: '',
  closeDt: '',
  closeRslt: '',
  deliveryDt: '',
  updateId: ''
})

// 날짜 포맷팅 함수
const formatDate = (dateString) => {
  if (!dateString) return '-'
  try {
    const date = new Date(dateString)
    return date.toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    })
  } catch (e) {
    return dateString
  }
}



// 라우터 변경 감지
watch(() => route.name, (newRouteName) => {
  console.log('라우터 변경 감지:', newRouteName)
  updateSubMenu(newRouteName, route.params.ordNo)
}, { immediate: true })

// 라우터 파라미터 변경 감지
watch(() => route.params.ordNo, (newTagNo) => {
  console.log('라우터 파라미터 변경 감지:', newTagNo)
  updateSubMenu(route.name, newTagNo)
}, { immediate: true })

// props.subMenu 변경 감지
watch(() => props.subMenu, (newSubMenu) => {
  console.log('props.subMenu 변경 감지:', newSubMenu)
  if (newSubMenu) {
    currentSubMenu.value = newSubMenu
  }
}, { immediate: true })

// 서브메뉴 업데이트 함수
function updateSubMenu(routeName, routeTagNo) {
  // 기존 서브메뉴 저장
  const previousSubMenu = currentSubMenu.value
  
  if (routeTagNo) {
    searchTagNo.value = routeTagNo
  }
  
  // props로 전달된 subMenu가 있으면 우선 처리
  if (props.subMenu) {
    currentSubMenu.value = props.subMenu
  } else {
    // 라우터 이름에 따라 서브메뉴 설정
    if (routeName === 'TagManagement') {
      // 기본 태그관리 페이지 (태그 검색)
      currentSubMenu.value = null
    } else if (routeName === 'TagProcStep' || routeName === 'TagProcStepSearch') {
      currentSubMenu.value = 'tag-proc-step'
      if (routeTagNo) {
        loadProcStepData(routeTagNo)
      }
    } else if (routeName === 'TagSetting' || routeName === 'TagSettingSearch') {
      currentSubMenu.value = 'tag-setting'
      if (routeTagNo) {
        loadSettingData(routeTagNo)
      }
    } else if (routeName === 'TagVersion' || routeName === 'TagVersionSearch') {
      currentSubMenu.value = 'tag-version'
      if (routeTagNo) {
        loadVersionData(routeTagNo)
      }
    } else if (routeName === 'TagCommon' || routeName === 'TagCommonSearch') {
      currentSubMenu.value = 'tag-common'
      if (routeTagNo) {
        loadCommonData(routeTagNo)
      }
    } else if (routeName === 'TagAs' || routeName === 'TagAsSearch') {
      currentSubMenu.value = 'tag-as'
      if (routeTagNo) {
        loadAsData(routeTagNo)
      }
    } else {
      // 기본 태그관리 페이지
      currentSubMenu.value = null
    }
  }
  
  // 서브메뉴가 변경된 경우 검색 조건 초기화
  if (previousSubMenu !== currentSubMenu.value) {
    resetSearch()
  }
}

// 서브메뉴 초기화
onMounted(() => {
  // 초기 상태 설정
  if (!props.subMenu && route.name === 'TagManagement') {
    currentSubMenu.value = null
  }
})

// 데이터 로드 함수들
async function loadProcStepData(ordNo) {
  if (!ordNo) return
  try {
    const res = await axios.get(`/tags/proc_step_${ordNo}`)
    procStepData.value = res.data.body || res.data
  } catch (error) {
    console.error('처리단계 조회 오류:', error)
    ElMessage.error('처리단계 정보를 불러오는 중 오류가 발생했습니다.')
  }
}

async function loadSettingData(ordNo) {
  if (!ordNo) return
  try {
    const res = await axios.get(`/tags/setting_info_${ordNo}`)
    const data = res.data.body || res.data
    
    // 단일 객체를 배열로 감싸서 처리
    if (data && !Array.isArray(data)) {
      settingInfoData.value = [data]
    } else {
      settingInfoData.value = data || []
    }
  } catch (error) {
    console.error('세팅정보 조회 오류:', error)
    ElMessage.error('세팅정보를 불러오는 중 오류가 발생했습니다.')
    settingInfoData.value = []
  }
}

async function loadVersionData(ordNo) {
  if (!ordNo) return
  try {
    const res = await axios.get(`/tags/version-history/${ordNo}`)
    versionHistoryData.value = res.data.body || res.data || []
  } catch (error) {
    console.error('버전 이력 조회 오류:', error)
    ElMessage.error('버전 이력을 불러오는 중 오류가 발생했습니다.')
  }
}

async function loadCommonData(ordNo) {
  if (!ordNo) return
  try {
    console.log('공통정보 조회 시작:', ordNo)
    const res = await axios.get(`/tags/common_history_${ordNo}`)
    console.log('공통정보 응답:', res.data)
    
    let data = res.data.body || res.data
    if (data && !Array.isArray(data)) {
      data = [data]
    }
    commonHistoryData.value = data || []
    
    console.log('설정된 공통정보 데이터:', commonHistoryData.value)
  } catch (error) {
    console.error('공통정보 조회 오류:', error)
    ElMessage.error('공통정보를 불러오는 중 오류가 발생했습니다.')
    commonHistoryData.value = []
  }
}

async function loadAsData(ordNo) {
  if (!ordNo) return
  try {
    const res = await axios.get(`/tags/prod_as_${ordNo}`)
    const data = res.data.body || res.data
    if (Array.isArray(data)) {
      asInfoData.value = data
    } else {
      asInfoData.value = [data]
    }
    console.log('AS 이력 데이터:', asInfoData.value)
  } catch (error) {
    console.error('AS 이력 조회 오류:', error)
    ElMessage.error('AS 이력을 불러오는 중 오류가 발생했습니다.')
  }
}

// 서브메뉴에 따른 상세 정보 로드 함수
async function loadDetailDataBySubMenu(ordNo, subMenu) {
  console.log('loadDetailDataBySubMenu 호출:', ordNo, subMenu)
  
  switch (subMenu) {
    case 'tag-proc-step':
      await loadProcStepData(ordNo)
      break
    case 'tag-setting':
      await loadSettingData(ordNo)
      break
    case 'tag-version':
      await loadVersionData(ordNo)
      break
    case 'tag-common':
      await loadCommonData(ordNo)
      break
    case 'tag-as':
      await loadAsData(ordNo)
      break
    default:
      console.log('알 수 없는 서브메뉴:', subMenu)
  }
}

// 서브메뉴별 검색 함수들
async function searchProcStep() {
  if (!searchTagNo.value.trim()) {
    ElMessage.warning('태그번호를 입력해주세요.')
    return
  }
  try {
    const res = await axios.get(`/tags/proc_step_${searchTagNo.value.trim()}`)
    console.log('처리단계 응답 데이터:', res.data)
    procStepData.value = res.data.body || res.data
    if (!procStepData.value) {
      ElMessage.warning('해당 태그의 처리단계 정보가 없습니다.')
    }
  } catch (error) {
    console.error('처리단계 조회 오류:', error)
    ElMessage.error('처리단계 정보를 불러오는 중 오류가 발생했습니다.')
  }
}

async function searchSettingInfo() {
  if (!searchTagNo.value.trim()) {
    ElMessage.warning('태그번호를 입력해주세요.')
    return
  }
  try {
    const res = await axios.get(`/tags/setting_info_${searchTagNo.value.trim()}`)
    console.log('세팅정보 응답 데이터:', res.data)
    const data = res.data.body || res.data
    
    // 단일 객체를 배열로 감싸서 처리
    if (data && !Array.isArray(data)) {
      settingInfoData.value = [data]
    } else {
      settingInfoData.value = data || []
    }
    
    if (!settingInfoData.value || settingInfoData.value.length === 0) {
      ElMessage.warning('해당 태그의 세팅정보가 없습니다.')
    }
    // 편집 모드 초기화
    isEditMode.value = false
    originalSettingData.value = null
  } catch (error) {
    console.error('세팅정보 조회 오류:', error)
    ElMessage.error('세팅정보를 불러오는 중 오류가 발생했습니다.')
    settingInfoData.value = []
  }
}

async function searchVersionHistory() {
  if (!searchTagNo.value.trim()) {
    ElMessage.warning('태그번호를 입력해주세요.')
    return
  }
  try {
    console.log('버전 이력 조회 시작:', searchTagNo.value.trim())
    const res = await axios.get(`/tags/version-history/${searchTagNo.value.trim()}`)
    console.log('버전 이력 응답:', res.data)
    versionHistoryData.value = res.data.body || res.data || []
    console.log('설정된 versionHistoryData:', versionHistoryData.value)
    console.log('데이터 길이:', versionHistoryData.value.length)
    
    if (!versionHistoryData.value || versionHistoryData.value.length === 0) {
      ElMessage.warning('해당 태그의 버전 이력이 없습니다.')
    } /* else {
      ElMessage.success(`버전 이력 ${versionHistoryData.value.length}건을 찾았습니다.`)
    } */
  } catch (error) {
    console.error('버전 이력 조회 오류:', error)
    ElMessage.error('버전 이력을 불러오는 중 오류가 발생했습니다.')
  }
}

// 태그번호 검색 함수
async function searchTagNumbers() {
  if (!searchTagNo.value.trim()) {
    ElMessage.warning('태그번호를 입력해주세요.')
    return
  }
  
  try {
    console.log('태그번호 검색 시작:', searchTagNo.value.trim())
    const res = await axios.get('/tags/tag-numbers', {
      params: { query: searchTagNo.value.trim() }
    })
    console.log('태그번호 검색 응답:', res.data)
    const tagNumbers = res.data.body || res.data || []
    console.log('태그번호 목록:', tagNumbers)
    
    // 태그번호 목록을 상세 정보와 함께 가져오기
    const detailedList = []
    for (const ordNo of tagNumbers) {
      // 태그번호가 20자리 이상인 경우에만 분해
      let macAddr = ''
      let facCd = ''
      let facNo = ''
      
      if (ordNo && ordNo.length >= 20) {
        // MAC주소 부분 (12자리)
        macAddr = ordNo.substring(0, 12)
        // 공장코드 부분 (6자리)
        facCd = ordNo.substring(12, 18)
        // 시리얼번호 부분 (나머지)
        facNo = ordNo.substring(18)
      } else {
        // 20자리 미만인 경우 전체를 MAC주소로 처리
        macAddr = ordNo || ''
      }
      
      detailedList.push({
        tag_No: ordNo,
        mac_Addr: macAddr,
        fac_Cd: facCd,
        fac_No: facNo
      })
    }
    
    console.log('상세 목록:', detailedList)
    tagNumberList.value = detailedList
    totalTagCount.value = detailedList.length
    
    if (tagNumberList.value.length === 0) {
      ElMessage.info('검색 결과가 없습니다.')
    }
  } catch (error) {
    console.error('태그번호 검색 오류:', error)
    ElMessage.error('검색 중 오류가 발생했습니다.')
  }
}

// 태그번호 선택 함수
function selectTagNumber(row) {
  searchTagNo.value = row.tag_No
  tagNumberList.value = [] // 검색 결과 숨기기
  
  // 현재 서브메뉴에 따라 해당 정보 조회
  if (currentSubMenu.value === 'tag-setting') {
    searchSettingInfo()
  } else if (currentSubMenu.value === 'tag-version') {
    searchVersionHistory()
  } else if (currentSubMenu.value === 'tag-common') {
    searchCommonHistory()
  } else if (currentSubMenu.value === 'tag-as') {
    searchAsInfo()
  }
}

// 태그 검색 초기화 함수
function clearTagSearch() {
  searchTagNo.value = ''
  tagNumberList.value = []
  totalTagCount.value = 0
  resetVersionHistoryData()
}

// 페이지네이션된 태그 목록
const paginatedTagList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return tagNumberList.value.slice(start, end)
})



async function searchCommonHistory() {
  if (!searchTagNo.value.trim()) {
    ElMessage.warning('태그번호를 입력해주세요.')
    return
  }
  try {
    const res = await axios.get(`/tags/common_history_${searchTagNo.value.trim()}`)
    
    let data = res.data.body || res.data
    if (data && !Array.isArray(data)) {
      data = [data]
    }
    commonHistoryData.value = data || []
    
    if (!commonHistoryData.value || commonHistoryData.value.length === 0) {
      ElMessage.warning('해당 태그의 공통정보가 없습니다.')
    }
  } catch (error) {
    console.error('공통정보 조회 오류:', error)
    ElMessage.error('공통정보를 불러오는 중 오류가 발생했습니다.')
    commonHistoryData.value = []
  }
}

async function searchAsInfo() {
  if (!searchTagNo.value.trim()) {
    ElMessage.warning('태그번호를 입력해주세요.')
    return
  }
  try {
    const res = await axios.get(`/tags/prod_as_${searchTagNo.value.trim()}`)
    const data = res.data.body || res.data
    if (Array.isArray(data)) {
      asInfoData.value = data
    } else {
      asInfoData.value = [data]
    }
    if (!asInfoData.value || asInfoData.value.length === 0) {
      ElMessage.warning('해당 태그의 AS 이력이 없습니다.')
    }
  } catch (error) {
    console.error('AS 이력 조회 오류:', error)
    ElMessage.error('AS 이력을 불러오는 중 오류가 발생했습니다.')
  }
}

// 첫 번째 태그의 AS 이력 조회
async function loadAsForFirstTag() {
  if (tableData.value.length === 0) {
    ElMessage.warning('검색된 태그가 없습니다.')
    return
  }
  
  const firstTag = tableData.value[0]
  if (!firstTag.ordNo) {
    ElMessage.warning('태그번호 정보가 없습니다.')
    return
  }
  
  try {
    console.log('첫 번째 태그의 AS 이력 조회:', firstTag.ordNo)
    await loadAsData(firstTag.ordNo)
    ElMessage.success('AS 이력을 조회했습니다.')
  } catch (error) {
    console.error('AS 이력 조회 오류:', error)
    ElMessage.error('AS 이력을 불러오는 중 오류가 발생했습니다.')
  }
}

// 이벤트 핸들러
async function showProcStep(ordNo) {
  // 라우터를 사용해서 URL 변경
  router.push(`/tag-management/proc-step/${ordNo}`)
}

async function showVersionHistory(ordNo) {
  router.push(`/tag-management/version/${ordNo}`)
}

async function showCommonHistory(ordNo) {
  router.push(`/tag-management/common/${ordNo}`)
}

async function showSettingInfo(ordNo) {
  router.push(`/tag-management/setting/${ordNo}`)
}

// 처리단계 관련 함수들
function editProcStep() {
  if (userAcl.value < 2) {
    ElMessage.warning('수정 권한이 없습니다.')
    return
  }
  ElMessage.info('처리단계 수정 기능은 개발 중입니다.')
}

function deleteProcStep() {
  if (userAcl.value < 2) {
    ElMessage.warning('삭제 권한이 없습니다.')
    return
  }
  
  ElMessageBox.confirm('정말 삭제하시겠습니까?', '확인', {
    confirmButtonText: '삭제',
    cancelButtonText: '취소',
    type: 'warning'
  }).then(async () => {
    try {
      await axios.put(`/tags/update_proc_step_${procStepData.value.ordNo}`, {
        status: 'Y',
        updateId: userInfo.value.user_id
      })
      ElMessage.success('처리단계가 삭제되었습니다.')
      // 삭제 후 데이터 다시 로드
      await loadProcStepData(procStepData.value.ordNo)
    } catch (error) {
      console.error('처리단계 삭제 오류:', error)
      ElMessage.error('처리단계 삭제 중 오류가 발생했습니다.')
    }
  }).catch(() => {
    // 취소
  })
}

// 세팅정보 관련 함수들
function editSettingInfo() {
  if (userAcl.value < 2) {
    ElMessage.warning('수정 권한이 없습니다.')
    return
  }
  ElMessage.info('세팅정보 수정 기능은 개발 중입니다.')
}

function deleteSettingInfo() {
  if (userAcl.value < 2) {
    ElMessage.warning('삭제 권한이 없습니다.')
    return
  }
  
  ElMessageBox.confirm('정말 삭제하시겠습니까?', '확인', {
    confirmButtonText: '삭제',
    cancelButtonText: '취소',
    type: 'warning'
  }).then(async () => {
    try {
      await axios.put(`/tags/update_setting_${settingInfoData.value[0]?.ordNo}`, {
        status: 'Y',
        updateId: userInfo.value.user_id
      })
      ElMessage.success('세팅정보가 삭제되었습니다.')
      // 삭제 후 데이터 다시 로드
      await loadSettingData(settingInfoData.value[0]?.ordNo)
    } catch (error) {
      console.error('세팅정보 삭제 오류:', error)
      ElMessage.error('세팅정보 삭제 중 오류가 발생했습니다.')
    }
  }).catch(() => {
    // 취소
  })
}

async function showAsInfo(ordNo) {
  router.push(`/tag-management/as/${ordNo}`)
}

// AS 관련 함수들
function editAs(row) {
  if (userAcl.value < 2) {
    ElMessage.warning('수정 권한이 없습니다.')
    return
  }
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
  } catch (e) {
    return dateString
  }
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
    
    const res = await axios.get(`/tags/prod_as_${currentTagNo.value}`)
    const data = res.data.body || res.data
    if (Array.isArray(data)) {
      asInfoData.value = data
    } else {
      asInfoData.value = [data]
    }
    
    showAsForm.value = false
    isEditMode.value = false
    resetAsForm()
    
    ElMessage.success(isEditMode.value ? 'AS 정보가 수정되었습니다.' : 'AS 정보가 등록되었습니다.')
  } catch (error) {
    console.error('AS 저장 오류:', error)
    ElMessage.error('AS 정보 저장 중 오류가 발생했습니다.')
  }
}

function resetAsForm() {
  asForm.value = {
    id: null,
    asDoc: '',
    occrDt: '',
    occrRsn: '',
    closeDt: '',
    closeRslt: '',
    deliveryDt: '',
    updateId: ''
  }
  isEditMode.value = false
}

function addNewAs() {
  if (userAcl.value < 2) {
    ElMessage.warning('등록 권한이 없습니다.')
    return
  }
  isEditMode.value = false
  asForm.value = {
    id: null,
    asDoc: '',
    occrDt: '',
    occrRsn: '',
    closeDt: '',
    closeRslt: '',
    deliveryDt: '',
    updateId: ''
  }
  showAsForm.value = true
}

async function deleteAs(row) {
  if (userAcl.value < 2) {
    ElMessage.warning('삭제 권한이 없습니다.')
    return
  }
  
  try {
    await ElMessageBox.confirm('정말 삭제하시겠습니까?', '확인', {
      confirmButtonText: '삭제',
      cancelButtonText: '취소',
      type: 'warning'
    })
    
    await axios.delete(`/tags/delete_as/${row.id}`)
    
    const res = await axios.get(`/tags/prod_as_${currentTagNo.value}`)
    const data = res.data.body || res.data
    if (Array.isArray(data)) {
      asInfoData.value = data
    } else {
      asInfoData.value = [data]
    }
    
    ElMessage.success('AS 정보가 삭제되었습니다.')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('AS 삭제 오류:', error)
      ElMessage.error('AS 정보 삭제 중 오류가 발생했습니다.')
    }
  }
}

// 공통정보 관련 함수들
function addNewCommon() {
  if (userAcl.value < 2) {
    ElMessage.warning('등록 권한이 없습니다.')
    return
  }
  ElMessage.info('공통정보 등록 기능은 개발 중입니다.')
}

function editCommon(row) {
  if (userAcl.value < 2) {
    ElMessage.warning('수정 권한이 없습니다.')
    return
  }
  ElMessage.info('공통정보 수정 기능은 개발 중입니다.')
}

function deleteCommon(row) {
  if (userAcl.value < 2) {
    ElMessage.warning('삭제 권한이 없습니다.')
    return
  }
  
  ElMessageBox.confirm('정말 삭제하시겠습니까?', '확인', {
    confirmButtonText: '삭제',
    cancelButtonText: '취소',
    type: 'warning'
  }).then(async () => {
    try {
      await axios.put(`/tags/update_common_${row.ordNo}`, {
        status: 'Y',
        updateId: userInfo.value.user_id
      })
      ElMessage.success('공통정보가 삭제되었습니다.')
      // 삭제 후 데이터 다시 로드
      await loadCommonData(row.ordNo)
    } catch (error) {
      console.error('공통정보 삭제 오류:', error)
      ElMessage.error('공통정보 삭제 중 오류가 발생했습니다.')
    }
  }).catch(() => {
    // 취소
  })
}

// 버전 관련 함수들
function addNewVersion() {
  if (userAcl.value < 2) {
    ElMessage.warning('등록 권한이 없습니다.')
    return
  }
  ElMessage.info('버전 등록 기능은 개발 중입니다.')
}

function editVersion(row) {
  if (userAcl.value < 2) {
    ElMessage.warning('수정 권한이 없습니다.')
    return
  }
  ElMessage.info('버전 수정 기능은 개발 중입니다.')
}

function deleteVersion(row) {
  if (userAcl.value < 2) {
    ElMessage.warning('삭제 권한이 없습니다.')
    return
  }
  
  ElMessageBox.confirm('정말 삭제하시겠습니까?', '확인', {
    confirmButtonText: '삭제',
    cancelButtonText: '취소',
    type: 'warning'
  }).then(async () => {
    try {
      await axios.put(`/tags/update_version_${row.ordNo}`, {
        status: 'Y',
        updateId: userInfo.value.user_id
      })
      ElMessage.success('버전이 삭제되었습니다.')
      // 삭제 후 데이터 다시 로드
      await loadVersionData(row.ordNo)
    } catch (error) {
      console.error('버전 삭제 오류:', error)
      ElMessage.error('버전 삭제 중 오류가 발생했습니다.')
    }
  }).catch(() => {
    // 취소
  })
}

// 로그아웃
function logout() {
  sessionStorage.removeItem('jwt_token')
  sessionStorage.removeItem('user_info')
  window.location.reload()
}

// 컴포넌트 마운트 시 권한 체크만
onMounted(() => {
  if (userInfo.value.user_acl === '0') {
    sessionStorage.removeItem('jwt_token')
    sessionStorage.removeItem('user_info')
    window.location.reload()
    return
  }
  
  // 삭제여부 초기값 설정
  searchDelFilter.value = 'all'
  
  // 초기에는 검색하지 않음 - 사용자가 검색 조건을 입력해야 함
})

function toggleEditMode() {
  if (isEditMode.value) {
    // 저장 모드일 때
    saveSettingInfo()
  } else {
    // 편집 모드로 전환할 때
    startEditMode()
  }
}

function startEditMode() {
  // 원본 데이터 백업
  originalSettingData.value = JSON.parse(JSON.stringify(settingInfoData.value))
  isEditMode.value = true
  ElMessage.info('편집 모드가 활성화되었습니다. 값을 수정한 후 저장 버튼을 클릭하세요.')
}

function cancelEdit() {
  // 원본 데이터로 복원
  if (originalSettingData.value) {
    settingInfoData.value = JSON.parse(JSON.stringify(originalSettingData.value))
  }
  isEditMode.value = false
  originalSettingData.value = null
  ElMessage.info('편집이 취소되었습니다.')
}

async function saveSettingInfo() {
  try {
    // null 값을 빈 문자열로 변환하는 헬퍼 함수
    const convertNullToEmpty = (value) => value === null || value === undefined ? '' : value
    
    // 전송할 데이터 준비
    const requestData = {
      HW_VER: convertNullToEmpty(settingInfoData.value[0]?.hw_version),
      FW_VER: convertNullToEmpty(settingInfoData.value[0]?.fw_version),
      LED_SEC: convertNullToEmpty(settingInfoData.value[0]?.led_SEC),
      RI_MS: convertNullToEmpty(settingInfoData.value[0]?.ri_MS),
      TX_POWER: convertNullToEmpty(settingInfoData.value[0]?.tx_POWER),
      RANDOM_DV: convertNullToEmpty(settingInfoData.value[0]?.random_DV),
      RF_PROFILE: convertNullToEmpty(settingInfoData.value[0]?.rf_PROFILE),
      CHANNEL: convertNullToEmpty(settingInfoData.value[0]?.channel),
      SLEEP_MODE: convertNullToEmpty(settingInfoData.value[0]?.sleep_MODE),
      SLEEP_TH_HOLD: convertNullToEmpty(settingInfoData.value[0]?.sleep_TH_HOLD),
      SLEEP_INTERVAL: convertNullToEmpty(settingInfoData.value[0]?.sleep_INTERVAL),
      SLEEP_PERIOD: convertNullToEmpty(settingInfoData.value[0]?.sleep_PERIOD),
      BC_VER: convertNullToEmpty(settingInfoData.value[0]?.bc_VER),
      BC_PERIOD: convertNullToEmpty(settingInfoData.value[0]?.bc_PERIOD),
      BC_SLEEP: convertNullToEmpty(settingInfoData.value[0]?.bc_SLEEP),
      DEVICE_IP: convertNullToEmpty(settingInfoData.value[0]?.device_IP),
      SERVER_IP: convertNullToEmpty(settingInfoData.value[0]?.server_IP),
      GATEWAY: convertNullToEmpty(settingInfoData.value[0]?.gateway),
      SUB_MASK: convertNullToEmpty(settingInfoData.value[0]?.sub_MASK),
      TDMA: convertNullToEmpty(settingInfoData.value[0]?.tdma),
      PORT: convertNullToEmpty(settingInfoData.value[0]?.port)
    }
    
    // JWT 토큰을 헤더에 포함
    const token = sessionStorage.getItem('jwt_token')
    const headers = {
      'Authorization': token
    }
    
    // 수정된 데이터로 API 호출
    const res = await axios.put(`/tags/update_setting_${settingInfoData.value[0]?.ordNo}`, requestData, { headers })
    
    // 백엔드 응답 구조에 맞게 성공 체크
    if (res.data.statusCode === 'OK' || res.data.statusCodeValue === 200 || res.status === 200) {
      ElMessage.success('세팅정보가 성공적으로 수정되었습니다.')
      // 편집 모드 종료
      isEditMode.value = false
      originalSettingData.value = null
      // 최신 데이터 다시 로드
      await loadSettingData(settingInfoData.value[0]?.ordNo)
    } else {
      ElMessage.error('세팅정보 수정에 실패했습니다.')
      console.error('수정 실패 응답:', res.data)
    }
  } catch (error) {
    console.error('세팅정보 수정 오류:', error)
    ElMessage.error('세팅정보 수정 중 오류가 발생했습니다.')
  }
}

// null 값 처리를 위한 헬퍼 함수
function getDisplayValue(value) {
  return value || '-'
}

// 버전 필드에 "v" 접두사를 추가하는 헬퍼 함수
function getVersionDisplay(value) {
  if (!value || value === '-') return '-'
  // 이미 "v"로 시작하면 그대로 반환, 아니면 "v" 추가
  return value.startsWith('v') ? value : `v${value}`
}
</script>

<style scoped>
.tag-management-page {
  width: 100%;
  height: 100%;
  padding: 0;
  background: #f8f9fa;
  overflow: auto;
  margin: 0;
  box-sizing: border-box;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.search-section {
  margin-bottom: 20px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.search-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.search-header h3 {
  margin: 0;
  color: #2c3e50;
  font-size: 1.2rem;
  font-weight: 600;
}

.search-help-content {
  line-height: 1.6;
}

.help-item {
  margin-bottom: 15px;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 6px;
  border-left: 4px solid #409eff;
}

.help-item h5 {
  margin: 0 0 5px 0;
  color: #2c3e50;
  font-size: 1rem;
}

.help-item p {
  margin: 0;
  color: #666;
  font-size: 0.9rem;
}

/* 검색 도움말 사이드바 */
.search-help-sidebar {
  position: fixed;
  top: 0;
  right: -400px;
  width: 400px;
  height: 100vh;
  background: white;
  box-shadow: -2px 0 10px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  transition: right 0.3s ease;
  overflow-y: auto;
  padding: 20px;
}

.search-help-sidebar.show {
  right: 0;
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.sidebar-header h3 {
  margin: 0;
  color: #2c3e50;
  font-size: 1.3rem;
  font-weight: 600;
}

.close-btn {
  font-size: 18px;
  color: #666;
}

.close-btn:hover {
  color: #409eff;
}

.pagination-section {
  display: flex;
  justify-content: center;
  padding: 20px;
  background: white;
  border-top: 1px solid #eee;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 테이블 스타일 조정 */
:deep(.el-table) {
  table-layout: fixed !important;
}

:deep(.el-table th) {
  padding: 8px 0 !important;
}

:deep(.el-table td) {
  padding: 8px 0 !important;
}

:deep(.el-table .cell) {
  padding: 8px 0 !important;
}

/* el-scrollbar 영역 내 테이블 정렬 */
:deep(.el-scrollbar .el-table th) {
  text-align: center !important;
}

:deep(.el-scrollbar .el-table th .cell) {
  text-align: center !important;
}

:deep(.el-scrollbar .el-table td) {
  text-align: center !important;
}

:deep(.el-scrollbar .el-table td .cell) {
  text-align: center !important;
}

.submenu-content {
  padding: 32px;
  background: white;
  min-height: calc(100vh - 200px);
}

.submenu-content h2 {
  margin: 0 0 10px 0;
  color: #2c3e50;
  font-size: 1.8rem;
  font-weight: 600;
}

.submenu-content p {
  margin: 0 0 30px 0;
  color: #666;
  font-size: 1rem;
}

.result-section {
  margin-top: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e9ecef;
}

.result-header h3 {
  margin: 0;
  color: #2c3e50;
  font-size: 1.3rem;
  font-weight: 600;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

/* 안내 메시지 스타일 */
.info-message {
  margin-top: 20px;
}

.info-message .el-alert {
  border-radius: 8px;
}

/* 직접 검색 섹션 스타일 */
.direct-search-section {
  margin-top: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.direct-search-section h3 {
  margin: 0 0 15px 0;
  color: #2c3e50;
  font-size: 1.2rem;
  font-weight: 600;
}

.direct-search-form {
  margin-bottom: 0;
}

/* 태그 검색 결과 스타일 */
.tag-search-results {
  margin-top: 20px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.tag-search-results h3 {
  margin: 0 0 15px 0;
  color: #2c3e50;
  font-size: 1.2rem;
  font-weight: 600;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  padding: 15px 0;
  border-top: 1px solid #e9ecef;
}

:deep(.editable-row) {
  background-color: #f5f7fa;
}

/* 편집 가능한 테이블 스타일 */
:deep(.editable-row .el-input) {
  width: 100%;
}

:deep(.editable-row .el-input__inner) {
  border: 1px solid #409eff;
  background-color: #fff;
  font-size: 12px;
  padding: 4px 8px;
}

:deep(.editable-row .el-input__inner:focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

/* 편집 모드일 때 테이블 행 스타일 */
:deep(.editable-row td) {
  background-color: #f0f9ff !important;
  border-color: #b3d8ff !important;
}

:deep(.editable-row td:hover) {
  background-color: #e6f3ff !important;
}

/* 편집 불가능한 필드 스타일 */
:deep(.editable-row .readonly-field) {
  background-color: #f5f5f5;
  color: #666;
  font-style: italic;
}

/* 액션 버튼 스타일 */
.action-buttons .el-button {
  margin-left: 8px;
}

.action-buttons .el-button:first-child {
  margin-left: 0;
}

/* 테이블 스크롤 스타일 */
:deep(.el-table) {
  overflow-x: auto;
}

:deep(.el-table__body-wrapper) {
  overflow-x: auto;
}

/* 편집 모드 안내 메시지 */
.edit-mode-notice {
  margin-bottom: 15px;
  padding: 10px 15px;
  background-color: #e6f7ff;
  border: 1px solid #91d5ff;
  border-radius: 6px;
  color: #1890ff;
  font-size: 14px;
}

.edit-mode-notice .el-icon {
  margin-right: 8px;
  color: #1890ff;
}

:deep(.editable-descriptions) {
  background-color: #f5f7fa;
}

:deep(.editable-descriptions .el-input) {
  width: 100%;
}

:deep(.editable-descriptions .el-input__inner) {
  border: 1px solid #409eff;
  background-color: #fff;
  font-size: 12px;
  padding: 4px 8px;
}

:deep(.editable-descriptions .el-input__inner:focus) {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

:deep(.editable-descriptions .el-descriptions-item) {
  padding: 8px 0 !important;
}

:deep(.editable-descriptions .el-descriptions-item .label) {
  font-weight: bold;
}
</style>