<template>
  <div class="tag-management-page">
    <!-- 헤더 -->
    <Header 
      :active-menu="activeMenu" 
      :user-info="userInfo"
      @menu-select="handleMenuSelect"
      @user-command="handleUserCommand"
    />
    
    <!-- 서브메뉴 네비게이션 -->
    <div class="submenu-navigation" v-if="currentSubMenu">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item>태그 관리</el-breadcrumb-item>
        <el-breadcrumb-item>{{ getSubMenuTitle(currentSubMenu) }}</el-breadcrumb-item>
      </el-breadcrumb>
      <el-button @click="backToMain" type="text" :icon="ArrowLeft">
        메인으로 돌아가기
      </el-button>
    </div>
    
    <!-- 서브메뉴별 컨텐츠 -->
    <div v-if="currentSubMenu === 'tag-proc-step'" class="submenu-content">
      <h2>처리단계 조회</h2>
      <p>태그 번호를 입력하여 처리단계 정보를 조회하세요.</p>
      <el-form :inline="true" class="search-form">
        <el-form-item label="태그번호">
          <el-input v-model="searchTagNo" placeholder="태그번호 입력" style="width: 300px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchProcStep">조회</el-button>
        </el-form-item>
      </el-form>
      <div v-if="procStepData" class="result-section">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="태그번호">{{ procStepData.tagNo }}</el-descriptions-item>
          <el-descriptions-item label="입고일">{{ formatDate(procStepData.receipt_DT) }}</el-descriptions-item>
          <el-descriptions-item label="납품일">{{ formatDate(procStepData.delivery_DT) }}</el-descriptions-item>
          <el-descriptions-item label="연구소 검수일">{{ formatDate(procStepData.lab_INSP_DT) }}</el-descriptions-item>
          <el-descriptions-item label="연구소 검수소견">{{ procStepData.lab_INSP_DESC }}</el-descriptions-item>
          <el-descriptions-item label="융합기술팀 검수일">{{ formatDate(procStepData.tech_INSP_DT) }}</el-descriptions-item>
          <el-descriptions-item label="융합기술팀 검수소견">{{ procStepData.tech_INSP_DESC }}</el-descriptions-item>
          <el-descriptions-item label="생성일">{{ formatDate(procStepData.create_DT) }}</el-descriptions-item>
          <el-descriptions-item label="생성자">{{ procStepData.create_ID }}</el-descriptions-item>
          <el-descriptions-item label="수정일">{{ formatDate(procStepData.update_DT) }}</el-descriptions-item>
          <el-descriptions-item label="수정자">{{ procStepData.update_ID }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </div>
    
    <div v-else-if="currentSubMenu === 'tag-setting'" class="submenu-content">
      <h2>세팅정보 조회</h2>
      <p>태그 번호를 입력하여 세팅정보를 조회하세요.</p>
      <el-form :inline="true" class="search-form">
        <el-form-item label="태그번호">
          <el-input v-model="searchTagNo" placeholder="태그번호 입력" style="width: 300px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchSettingInfo">조회</el-button>
        </el-form-item>
      </el-form>
      <div v-if="settingInfoData" class="result-section">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="태그번호">{{ settingInfoData.tagNo }}</el-descriptions-item>
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
    </div>
    
    <div v-else-if="currentSubMenu === 'tag-version'" class="submenu-content">
      <h2>버전 이력 조회</h2>
      <p>태그 번호를 입력하여 버전 이력을 조회하세요.</p>
      <el-form :inline="true" class="search-form">
        <el-form-item label="태그번호">
          <el-input v-model="searchTagNo" placeholder="태그번호 입력" style="width: 300px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchVersionHistory">조회</el-button>
        </el-form-item>
      </el-form>
      <div v-if="versionHistoryData.length > 0" class="result-section">
        <el-table :data="versionHistoryData" style="width: 100%">
          <el-table-column prop="TAG_VER" label="버전" width="100" />
          <el-table-column prop="HW_VER" label="하드웨어버전" width="120" />
          <el-table-column prop="FW_VER" label="펌웨어버전" width="120" />
          <el-table-column prop="CREATE_DT" label="생성일" width="120">
            <template #default="{ row }">
              {{ formatDate(row.CREATE_DT) }}
            </template>
          </el-table-column>
          <el-table-column prop="CREATE_ID" label="생성자" width="100" />
        </el-table>
      </div>
    </div>
    
    <div v-else-if="currentSubMenu === 'tag-common'" class="submenu-content">
      <h2>공통정보 조회</h2>
      <p>태그 번호를 입력하여 공통정보를 조회하세요.</p>
      <el-form :inline="true" class="search-form">
        <el-form-item label="태그번호">
          <el-input v-model="searchTagNo" placeholder="태그번호 입력" style="width: 300px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchCommonHistory">조회</el-button>
        </el-form-item>
      </el-form>
      <div v-if="commonHistoryData.length > 0" class="result-section">
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
        </el-table>
      </div>
    </div>
    
    <div v-else-if="currentSubMenu === 'tag-as'" class="submenu-content">
      <h2>AS 이력 조회</h2>
      <p>태그 번호를 입력하여 AS 이력을 조회하세요.</p>
      <el-form :inline="true" class="search-form">
        <el-form-item label="태그번호">
          <el-input v-model="searchTagNo" placeholder="태그번호 입력" style="width: 300px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchAsInfo">조회</el-button>
        </el-form-item>
      </el-form>
      <div v-if="asInfoData && asInfoData.length > 0" class="result-section">
        <el-table :data="asInfoData" style="width: 100%">
          <el-table-column prop="as_CNT" label="AS 횟수" width="80" align="center" />
          <el-table-column prop="mac_ADDR" label="MAC주소" width="150" align="center" />
          <el-table-column prop="as_DOC" label="AS 문서번호" width="120" align="center" />
          <el-table-column prop="occr_DT" label="AS 발생일" width="120" align="center">
            <template #default="{ row }">
              {{ formatDate(row.occr_DT) }}
            </template>
          </el-table-column>
          <el-table-column prop="occr_RSN" label="AS 발생사유" width="150" align="center" />
          <el-table-column prop="close_DT" label="AS 종결일" width="120" align="center">
            <template #default="{ row }">
              {{ formatDate(row.close_DT) }}
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
    </div>
    
    <!-- 메인 검색 영역 (기본 화면) -->
    <div v-else class="search-section">
      <div class="search-header">
        <h3>태그 검색</h3>
        <el-button 
          type="info" 
          :icon="QuestionFilled" 
          circle 
          size="small"
          @click="showSearchHelp = true"
          title="검색 도움말"
        />
      </div>
      <el-form :inline="true" class="search-form">
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
            style="width: 100%"
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
          <el-button type="primary" @click="doSearch">검색</el-button>
          <el-button @click="resetSearch">초기화</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 테이블 영역 -->
    <div class="table-section">
      <el-table :data="paginatedData" style="width:100%" v-loading="loading">
        <el-table-column prop="tag_No" label="태그번호" width="150" />
        <el-table-column prop="mac_Addr" label="MAC주소" width="150" />
        <el-table-column prop="fac_Cd" label="공장코드" width="100" />
        <el-table-column prop="fac_No" label="시리얼번호" width="120" />
        <el-table-column prop="tag_Version" label="제품버전" width="100" />
        <el-table-column prop="tag_Type" label="태그타입" width="100" />
        <el-table-column prop="erp_Code" label="ERP코드" width="120" />
        <el-table-column prop="Mng_Category" label="관리카테고리" width="120" />
        <el-table-column prop="Lot" label="LOT번호" width="120" />
        <el-table-column prop="Prod_order" label="생산지시" width="120" />
        <el-table-column prop="Project_code" label="프로젝트코드" width="120" />
        <el-table-column prop="Project_manager" label="프로젝트매니저" width="120" />
        <el-table-column prop="Mac_duple_yn" label="MAC중복여부" width="120" />
        <el-table-column prop="as_Cnt" label="AS횟수" width="80" />
        <el-table-column prop="Status" label="삭제여부" width="100">
          <template #default="{ row }">
            <el-tag :type="row.Status === 'Y' ? 'danger' : 'success'">
              {{ row.Status === 'Y' ? '삭제됨' : '사용중' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="작업" width="250" align="right">
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

    <!-- 모달들 -->
    <!-- 처리단계 모달 -->
    <el-dialog v-model="procStepVisible" title="처리단계 정보" width="600">
      <div v-if="procStepData">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="태그번호">{{ procStepData.tagNo }}</el-descriptions-item>
          <el-descriptions-item label="입고일">{{ formatDate(procStepData.receipt_DT) }}</el-descriptions-item>
          <el-descriptions-item label="납품일">{{ formatDate(procStepData.delivery_DT) }}</el-descriptions-item>
          <el-descriptions-item label="연구소 검수일">{{ formatDate(procStepData.lab_INSP_DT) }}</el-descriptions-item>
          <el-descriptions-item label="연구소 검수소견">{{ procStepData.lab_INSP_DESC }}</el-descriptions-item>
          <el-descriptions-item label="융합기술팀 검수일">{{ formatDate(procStepData.tech_INSP_DT) }}</el-descriptions-item>
          <el-descriptions-item label="융합기술팀 검수소견">{{ procStepData.tech_INSP_DESC }}</el-descriptions-item>
          <el-descriptions-item label="생성일">{{ formatDate(procStepData.create_DT) }}</el-descriptions-item>
          <el-descriptions-item label="생성자">{{ procStepData.create_ID }}</el-descriptions-item>
          <el-descriptions-item label="수정일">{{ formatDate(procStepData.update_DT) }}</el-descriptions-item>
          <el-descriptions-item label="수정자">{{ procStepData.update_ID }}</el-descriptions-item>
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
          <el-descriptions-item label="태그번호">{{ settingInfoData.tagNo }}</el-descriptions-item>
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
      <el-table :data="versionHistoryData" style="width: 100%">
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
        <el-table-column prop="as_CNT" label="AS 횟수" width="80" align="center" />
        <el-table-column prop="mac_ADDR" label="MAC주소" width="150" align="center" />
        <el-table-column prop="as_DOC" label="AS 문서번호" width="120" align="center" />
        <el-table-column prop="occr_DT" label="AS 발생일" width="120" align="center">
          <template #default="{ row }">
            {{ formatDate(row.occr_DT) }}
          </template>
        </el-table-column>
        <el-table-column prop="occr_RSN" label="AS 발생사유" width="150" align="center" />
        <el-table-column prop="close_DT" label="AS 종결일" width="120" align="center">
          <template #default="{ row }">
            {{ formatDate(row.close_DT) }}
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
        <el-button type="link"
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
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { QuestionFilled, Close, ArrowLeft } from '@element-plus/icons-vue'
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
  }
})

// Emits
const emit = defineEmits(['menu-select', 'user-command'])

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
const pageSize = ref(20)

// 서브메뉴 관련 상태
const currentSubMenu = ref(null)
const searchTagNo = ref('')

// 서브메뉴 제목 매핑
const getSubMenuTitle = (subMenu) => {
  const titles = {
    'tag-search': '태그 검색',
    'tag-proc-step': '처리단계 조회',
    'tag-setting': '세팅정보 조회',
    'tag-version': '버전 이력 조회',
    'tag-common': '공통정보 조회',
    'tag-as': 'AS 이력 조회'
  }
  return titles[subMenu] || '알 수 없음'
}

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
    searchMac: searchMac.value, 
    searchSn: searchSn.value, 
    searchFacCd: searchFacCd.value,
    searchDelFilter: searchDelFilter.value 
  })
  
  // 검색 조건이 없으면 경고 메시지 표시
  const hasSearchCondition = (searchMac.value && searchMac.value.trim()) || 
                           (searchFacCd.value && searchFacCd.value.trim()) || 
                           (searchSn.value && searchSn.value.trim())
  
  if (!hasSearchCondition) {
    ElMessage.warning('검색 조건을 하나 이상 입력해주세요.')
    return
  }
  
  loading.value = true
  try {
    // 빈 문자열이 아닌 경우에만 파라미터에 포함
    const params = {}
    if (searchMac.value && searchMac.value.trim()) {
      params.macAddr = searchMac.value.trim()
    }
    if (searchFacCd.value && searchFacCd.value.trim()) {
      params.facCd = searchFacCd.value.trim()
    }
    if (searchSn.value && searchSn.value.trim()) {
      params.facNo = searchSn.value.trim()
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
  searchMac.value = ''
  searchSn.value = ''
  searchFacCd.value = ''
  searchDelFilter.value = 'all'
  tableData.value = []
  currentPage.value = 1 // 초기화 시 첫 페이지로 이동
  ElMessage.info('검색 조건이 초기화되었습니다. 검색 조건을 입력하고 검색해주세요.')
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
const commonHistoryVisible = ref(false)
const commonHistoryData = ref([])
const asInfoVisible = ref(false)
const asInfoData = ref(null)
const showSearchHelp = ref(false)
const showAsForm = ref(false)
const isEditMode = ref(false)
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

// 서브메뉴 관련 함수들
function backToMain() {
  currentSubMenu.value = null
  searchTagNo.value = ''
  sessionStorage.removeItem('tagSubMenu')
}

// 서브메뉴 초기화
onMounted(() => {
  console.log('TagManagement onMounted 실행됨')
  const subMenu = sessionStorage.getItem('tagSubMenu')
  console.log('sessionStorage에서 가져온 서브메뉴:', subMenu)
  if (subMenu) {
    currentSubMenu.value = subMenu
    console.log('currentSubMenu 설정됨:', subMenu)
    sessionStorage.removeItem('tagSubMenu')
  }
})

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
    settingInfoData.value = res.data.body || res.data
    if (!settingInfoData.value) {
      ElMessage.warning('해당 태그의 세팅정보가 없습니다.')
    }
  } catch (error) {
    console.error('세팅정보 조회 오류:', error)
    ElMessage.error('세팅정보를 불러오는 중 오류가 발생했습니다.')
  }
}

async function searchVersionHistory() {
  if (!searchTagNo.value.trim()) {
    ElMessage.warning('태그번호를 입력해주세요.')
    return
  }
  try {
    const res = await axios.get(`/tags/version-history/${searchTagNo.value.trim()}`)
    versionHistoryData.value = res.data.body || res.data || []
    if (!versionHistoryData.value || versionHistoryData.value.length === 0) {
      ElMessage.warning('해당 태그의 버전 이력이 없습니다.')
    }
  } catch (error) {
    console.error('버전 이력 조회 오류:', error)
    ElMessage.error('버전 이력을 불러오는 중 오류가 발생했습니다.')
  }
}

async function searchCommonHistory() {
  if (!searchTagNo.value.trim()) {
    ElMessage.warning('태그번호를 입력해주세요.')
    return
  }
  try {
    const res = await axios.get(`/tags/common_history/${searchTagNo.value.trim()}`)
    commonHistoryData.value = res.data.body || res.data || []
    if (!commonHistoryData.value || commonHistoryData.value.length === 0) {
      ElMessage.warning('해당 태그의 공통정보가 없습니다.')
    }
  } catch (error) {
    console.error('공통정보 조회 오류:', error)
    ElMessage.error('공통정보를 불러오는 중 오류가 발생했습니다.')
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
      asInfoData.value = data.map((item, index) => ({
        ...item,
        seq: index + 1
      }))
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

// 이벤트 핸들러
async function showProcStep(tagNo) {
  try {
    const res = await axios.get(`/tags/proc_step_${tagNo}`)
    console.log('처리단계 응답 데이터:', res.data)
    procStepData.value = res.data.body || res.data
    procStepVisible.value = true
  } catch (error) {
    console.error('처리단계 조회 오류:', error)
    ElMessage.error('처리단계 정보를 불러오는 중 오류가 발생했습니다.')
  }
}

async function showVersionHistory(tagNo) {
  try {
    const res = await axios.get(`/tags/version-history/${tagNo}`)
    versionHistoryData.value = res.data.body || res.data || []
    versionHistoryVisible.value = true
  } catch (error) {
    ElMessage.error('버전 이력을 불러오는 중 오류가 발생했습니다.')
  }
}

async function showCommonHistory(tagNo) {
  try {
    const res = await axios.get(`/tags/common_history/${tagNo}`)
    commonHistoryData.value = res.data.body || res.data || []
    commonHistoryVisible.value = true
  } catch (error) {
    ElMessage.error('공통정보 이력을 불러오는 중 오류가 발생했습니다.')
  }
}

async function showSettingInfo(tagNo) {
  try {
    const res = await axios.get(`/tags/setting_info_${tagNo}`)
    console.log('세팅정보 응답 데이터:', res.data)
    settingInfoData.value = res.data.body || res.data
    settingInfoVisible.value = true
  } catch (error) {
    console.error('세팅정보 조회 오류:', error)
    ElMessage.error('세팅정보를 불러오는 중 오류가 발생했습니다.')
  }
}

function editSettingInfo() {
  // 세팅정보 수정 기능 구현
  ElMessage.info('세팅정보 수정 기능은 개발 중입니다.')
}

async function showAsInfo(tagNo) {
  currentTagNo.value = tagNo
  try {
    const res = await axios.get(`/tags/prod_as_${tagNo}`)
    const data = res.data.body || res.data
    // 데이터가 배열인지 확인하고 처리
    if (Array.isArray(data)) {
      asInfoData.value = data.map((item, index) => ({
        ...item,
        seq: index + 1
      }))
    } else {
      asInfoData.value = [data]
    }
    asInfoVisible.value = true
  } catch (error) {
    console.error('AS 이력 조회 오류:', error)
    ElMessage.error('AS 이력을 불러오는 중 오류가 발생했습니다.')
  }
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
    asDoc: row.as_DOC || '',
    occrDt: row.occr_DT ? formatDateForInput(row.occr_DT) : '',
    occrRsn: row.occr_RSN || '',
    closeDt: row.close_DT ? formatDateForInput(row.close_DT) : '',
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
      tagNo: currentTagNo.value,
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
    if (Array.isArray(res.data)) {
      asInfoData.value = res.data.map((item, index) => ({
        ...item,
        seq: index + 1
      }))
    } else {
      asInfoData.value = [res.data]
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
    await ElMessageBox.confirm('정말 삭제하시겠습니까?', '확인', {     confirmButtonText: '삭제',     cancelButtonText: '취소',     type: 'warning'
    })
    
    await axios.delete(`/tags/delete/${row.id}`)
    
    const res = await axios.get(`/tags/prod_as_${currentTagNo.value}`)
    if (Array.isArray(res.data)) {
      asInfoData.value = res.data.map((item, index) => ({
        ...item,
        seq: index + 1
      }))
    } else {
      asInfoData.value = [res.data]
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
  // 공통정보 등록 로직
}

function editCommon(row) {
  if (userAcl.value < 2) {
    ElMessage.warning('수정 권한이 없습니다.')
    return
  }
  // 공통정보 수정 로직
}

// 버전 관련 함수들
function addNewVersion() {
  if (userAcl.value < 2) {
    ElMessage.warning('등록 권한이 없습니다.')
    return
  }
  // 버전 등록 로직
}

function editVersion(row) {
  if (userAcl.value < 2) {
    ElMessage.warning('수정 권한이 없습니다.')
    return
  }
  // 버전 수정 로직
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

.table-section {
  margin-top: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden;
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

/* 테이블 헤더 중앙 정렬 */
:deep(.el-table th) {
  text-align: center !important;
}

:deep(.el-table th .cell) {
  text-align: center !important;
}

:deep(.el-table__header-wrapper .el-table__header th) {
  text-align: center !important;
}

:deep(.el-table__header-wrapper .el-table__header th .cell) {
  text-align: center !important;
}

:deep(.el-table) {
  table-layout: fixed !important;
}

:deep(.el-table__body-wrapper) {
  text-align: center !important;
}

:deep(.el-table__header-wrapper) {
  text-align: center !important;
}

:deep(.el-table th) {
  text-align: center !important;
  padding: 8px 0 !important;
}

:deep(.el-table td) {
  text-align: center !important;
  padding: 8px 0 !important;
}

:deep(.el-table .cell) {
  text-align: center !important;
  padding: 8px 0 !important;
}

:deep(.el-table td) {
  text-align: center !important;
}

:deep(.el-table td .cell) {
  text-align: center !important;
}

/* 서브메뉴 관련 스타일 */
.submenu-navigation {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 32px;
  background: white;
  border-bottom: 1px solid #eee;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
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

:deep(.el-breadcrumb) {
  font-size: 0.9rem;
}

:deep(.el-breadcrumb__item) {
  color: #666;
}

:deep(.el-breadcrumb__item:last-child) {
  color: #409eff;
  font-weight: 600;
}
</style> 