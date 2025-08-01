# 스마트태그 인벤토리 관리 시스템 API 명세서

## 개요
스마트태그 인벤토리 관리 시스템의 REST API 명세서입니다. 이 시스템은 스마트태그의 재고, 처리단계, 세팅정보, AS 이력 등을 관리하는 웹 애플리케이션입니다.

## 기본 정보
- **Base URL**: `http://localhost:8080`
- **인증 방식**: JWT Bearer Token
- **Content-Type**: `application/json`

## 인증 헤더
대부분의 API는 JWT 토큰 인증이 필요합니다. 요청 시 다음 헤더를 포함해야 합니다:
```
Authorization: Bearer {JWT_TOKEN}
```

---

## 1. 사용자 관리 API

### 1.1 사용자 인증

#### 로그인
```http
POST /user/login
Content-Type: application/json

{
  "login_id": "string",
  "user_pwd": "string"
}
```

#### 로그아웃
```http
POST /user/logout
Authorization: Bearer {JWT_TOKEN}
```

#### ID 찾기
```http
GET /user/findID/{login_id}
Content-Type: application/json

{
  "login_id": "string"
}
```

#### ID 중복 체크
```http
GET /user/checkID?loginId={login_id}
```

#### 비밀번호 변경
```http
PUT /user/changePwd/{login_id}
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
  "user_pwd": "string"
}
```

### 1.2 관리자 기능

#### 전체 사용자 조회
```http
GET /Admin/all-user
Authorization: Bearer {JWT_TOKEN}
```

#### 사용자 생성
```http
POST /Admin/createUser
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
  "login_id": "string",
  "user_pwd": "string",
  "user_name": "string",
  "user_acl": "string"
}
```

#### 사용자 권한 변경
```http
PUT /Admin/ChangeAcl/{user_id}?userAcl={user_acl}
Authorization: Bearer {JWT_TOKEN}
```

#### 사용자 정보 수정
```http
PUT /Admin/update/{user_id}
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
  "login_id": "string",
  "user_name": "string",
  "user_acl": "string"
}
```

#### 관리자 비밀번호 변경
```http
PUT /Admin/changePassword/{login_id}
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
  "user_pwd": "string"
}
```

### 1.3 부서 관리

#### 부서 생성
```http
POST /Admin/createDept
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
  "dept_code": "string",
  "dept_name": "string"
}
```

#### 부서 수정
```http
PUT /Admin/changeDept/{deptCode}
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
  "dept_name": "string"
}
```

---

## 2. 태그 관리 API

### 2.1 태그 재고 조회

#### 태그 목록 조회 (검색 포함)
```http
GET /tags/getTagList?macAddr={mac_addr}&facCd={fac_cd}&facNo={fac_no}&delFilter={del_filter}
```

**파라미터:**
- `macAddr` (optional): MAC 주소
- `facCd` (optional): 공장 코드
- `facNo` (optional): 공장 번호
- `delFilter` (optional): 삭제 필터 (기본값: "all")

### 2.2 처리단계 관리

#### 처리단계 조회
```http
GET /tags/proc_step_{tagNo}
```

### 2.3 세팅정보 관리

#### 세팅정보 조회 (최신)
```http
GET /tags/setting_info_{tagNo}
```

#### 세팅정보 수정
```http
PUT /tags/update_setting_{tagNo}
Content-Type: application/json

{
  "setting_info": "string"
}
```

### 2.4 AS 이력 관리

#### AS 이력 조회
```http
GET /tags/prod_as_{tagNo}?filter={filter}
```

**파라미터:**
- `filter` (optional): 필터링 옵션 (기본값: "active")

#### AS 등록
```http
POST /tags/{tagNo}_as
Content-Type: application/json

{
  "as_content": "string",
  "as_date": "string"
}
```

#### AS 수정
```http
PUT /tags/update_{tagNo}_as
Content-Type: application/json

{
  "prod_as_id": "number",
  "as_content": "string",
  "as_date": "string"
}
```

#### AS 삭제
```http
DELETE /tags/delete/{prodAsId}
```

### 2.5 버전 관리

#### 버전 이력 조회
```http
GET /tags/version-history/{tagNo}
```

#### 버전 등록
```http
POST /tags/version_{tagNo}
Content-Type: application/json

{
  "version_info": "string",
  "version_date": "string"
}
```

#### 버전 수정
```http
PUT /tags/update/version_{tagNo}
Content-Type: application/json

{
  "version_info": "string",
  "version_date": "string"
}
```

### 2.6 공통정보 관리

#### 공통정보 이력 조회
```http
GET /tags/common_history/{tagNo}
```

#### 공통정보 등록
```http
POST /tags/common_history
Content-Type: application/json

{
  "tag_no": "string",
  "mac_addr": "string",
  "fac_cd": "string",
  "serial_no": "string"
}
```

#### 공통정보 수정
```http
PUT /tags/update/common_{tagNo}
Content-Type: application/json

{
  "mac_addr": "string",
  "fac_cd": "string",
  "serial_no": "string"
}
```

---

## 3. 로그 관리 API

### 3.1 로그 조회

#### 로그 조회
```http
GET /Log/getLog?type={log_type}
```

**파라미터:**
- `type`: 로그 타입 (예: "user", "tag", "system" 등)

---

## 4. 프로젝트 관리 API

### 4.1 프로젝트 관리

#### 프로젝트 조회
```http
GET /project/searchAll
Authorization: Bearer {JWT_TOKEN}
```

#### 프로젝트 생성
```http
POST /project/create
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
  "project_code": "string",
  "project_name": "string",
  "project_desc": "string"
}
```

#### 프로젝트 수정
```http
POST /project/update{projectCode}
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
  "project_name": "string",
  "project_desc": "string"
}
```

---

## 5. 응답 형식

### 성공 응답
```json
{
  "status": "success",
  "data": {},
  "message": "string"
}
```

### 에러 응답
```json
{
  "status": "error",
  "message": "에러 메시지",
  "timestamp": "2024-01-01T00:00:00"
}
```

---

## 6. HTTP 상태 코드

- `200 OK`: 요청 성공
- `201 Created`: 리소스 생성 성공
- `400 Bad Request`: 잘못된 요청
- `401 Unauthorized`: 인증 실패
- `403 Forbidden`: 권한 없음
- `404 Not Found`: 리소스를 찾을 수 없음
- `500 Internal Server Error`: 서버 내부 오류

---

## 7. 데이터 모델

### UserDTO
```json
{
  "login_id": "string",
  "user_pwd": "string",
  "user_name": "string",
  "user_acl": "string"
}
```

### DeptDTO
```json
{
  "dept_code": "string",
  "dept_name": "string"
}
```

### ProjectDTO
```json
{
  "project_code": "string",
  "project_name": "string",
  "project_desc": "string"
}
```

### TagSettingDTO
```json
{
  "setting_info": "string"
}
```

---

## 8. 개발 환경 설정

### 데이터베이스
- **Database**: MariaDB
- **Host**: localhost:3307
- **Database Name**: intra_web_dev

### JWT 설정
- **Secret Key**: CREATE$<TEIA_MMIN_2025>$/v'@'v/JWT_TOKEN_SECRET_KEYS
- **Expire Length**: 3600000ms (1시간)
- **Issuer**: TAG_DEV

---

## 9. 프론트엔드 연동

이 API는 Vue.js 3 + Element Plus로 구성된 프론트엔드와 연동됩니다. 주요 기능:

- 사용자 인증 및 권한 관리
- 태그 재고 검색 및 관리
- 처리단계, 세팅정보, AS 이력 조회
- 로그 관리
- 프로젝트 관리

### 프론트엔드 개발 환경

프론트엔드 코드는 `front/` 디렉토리에 위치하며, Vite를 사용하여 개발 서버를 실행할 수 있습니다.

#### 개발 서버 실행
```bash
cd front
npm install
npm run dev
```

#### 프로덕션 빌드
```bash
cd front
npm run build
```

빌드된 파일은 `front/dist/` 디렉토리에 생성됩니다.

#### Spring Boot 정적 리소스로 빌드
Spring Boot 애플리케이션의 정적 리소스로 사용하려면 `src/main/resources/static` 디렉토리에 빌드 결과물을 복사해야 합니다.

```bash
# 1. 프론트엔드 빌드
cd front
npm run build

# 2. 빌드 결과물을 Spring Boot 정적 리소스 디렉토리로 복사
cp -r dist/* ../src/main/resources/static/

# Windows PowerShell의 경우:
# Copy-Item -Path "dist\*" -Destination "..\src\main\resources\static\" -Recurse -Force
```

또는 `vite.config.js`에서 빌드 출력 디렉토리를 직접 설정할 수 있습니다:

```javascript
// vite.config.js
export default defineConfig({
  build: {
    outDir: '../src/main/resources/static',
    emptyOutDir: true
  }
})
```

이렇게 설정하면 `npm run build` 실행 시 자동으로 Spring Boot의 정적 리소스 디렉토리에 빌드 결과물이 생성됩니다.
