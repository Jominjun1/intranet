# 🏷️ TEIA 인트라넷 개발 

## 📋 목차

- [개요](#개요)
- [주요 기능](#주요-기능)
- [기술 스택](#기술-스택)
- [프로젝트 구조](#프로젝트-구조)
- [설치 및 실행](#설치-및-실행)
- [API 문서](#api-문서)
- [데이터베이스](#데이터베이스)
- [보안](#보안)

## 🎯 개요

TEIA 인트라넷은 스마트 태그의 제조부터 폐기까지의 전 과정을 디지털화하여 태그의 기본 정보, 처리 단계, 설정 정보, AS 이력, 버전 관리 등을 통합적으로 관리하는 스마트 태그 재고 관리 기능과 더불어 기존 사용자 인증, 프로젝트 관리, 일일보고서 기능 등을 제공하는 사내 웹페이지 역할을 합니다.

### 핵심 가치
- **추적성**: 태그의 전체 생명주기 추적
- **투명성**: 모든 변경사항의 이력 관리
- **효율성**: 통합된 웹 인터페이스로 업무 효율성 향상
- **보안성**: JWT 기반 인증 및 권한 관리

## 🚀 주요 기능

### 1. 태그 관리 (Tag Management)
- **재고 관리**: 스마트 태그 재고 현황 조회 및 관리
- **처리 단계**: 태그별 제조/테스트/배송 단계 추적
- **설정 관리**: 하드웨어/펌웨어 버전, 통신 설정 등
- **AS 관리**: After Service 이력 등록 및 관리
- **버전 관리**: 제품 버전 이력 추적 및 관리
- **공통 정보**: MAC 주소, 시리얼 번호, 공장 코드 관리

### 2. 사용자 관리 (User Management)
- **인증 시스템**: JWT 토큰 기반 로그인/로그아웃
- **권한 관리**: 사용자별 접근 권한 제어
- **계정 보안**: 비밀번호 암호화, 로그인 실패 시 계정 잠금
- **사용자 정보**: 직원 정보 등록 및 관리

### 3. 프로젝트 관리 (Project Management)
- **프로젝트 등록**: 프로젝트 정보 생성 및 관리
- **진행 상황**: 프로젝트 상태 및 진행률 추적
- **고객 관리**: 고객사 정보 관리
- **부서 연동**: 담당 부서별 프로젝트 관리

### 4. 일일보고서 (Daily Report)
- **업무 보고**: 일일 업무 내용 작성 및 제출
- **시간 관리**: 업무 시간 기록
- **승인 시스템**: 보고서 승인 프로세스
- **프로젝트 연동**: 프로젝트별 업무 시간 배분

### 5. 시스템 관리 (System Management)
- **부서 관리**: 조직도 및 부서 정보 관리
- **로그 관리**: 시스템 사용 이력 및 감사 로그
- **공통 메뉴**: 시스템 공통 설정 관리

## 🛠️ 기술 스택

### Backend
- **Java 21**: 메인 개발 언어
- **Spring Boot 3.5.3**: 웹 애플리케이션 프레임워크
- **Spring Security**: 인증 및 보안
- **Spring Data JPA**: 데이터 접근 계층
- **JWT**: 토큰 기반 인증
- **MariaDB/MySQL**: 메인 데이터베이스
- **H2**: 개발용 인메모리 데이터베이스
- **Lombok**: 코드 간소화
- **Jasypt**: 설정 암호화

### Frontend
- **Vue.js 3.5.17**: 프론트엔드 프레임워크
- **Element Plus 2.11.3**: UI 컴포넌트 라이브러리
- **Vue Router 4.5.1**: 클라이언트 사이드 라우팅
- **Axios 1.12.0**: HTTP 클라이언트
- **Vite 7.1.5**: 빌드 도구

### Infrastructure
- **Gradle**: 빌드 도구
- **WebSocket**: 실시간 통신
- **CORS**: 크로스 오리진 리소스 공유

## 📁 프로젝트 구조

```
TAG_DEV/
├── src/main/java/com/example/tag_dev/
│   ├── Common/                 # 공통 모듈
│   │   ├── Controller/         # 공통 컨트롤러
│   │   ├── DTO/               # 공통 DTO
│   │   ├── Model/             # 공통 엔티티
│   │   ├── Repository/        # 공통 레포지토리
│   │   └── Service/           # 공통 서비스
│   ├── Config/                # 설정 클래스
│   ├── LOG/                   # 로그 관리
│   ├── SYSTEM/                # 시스템 관리
│   │   ├── Controller/        # 시스템 컨트롤러
│   │   ├── DTO/              # 시스템 DTO
│   │   ├── Model/            # 시스템 엔티티
│   │   ├── Repository/       # 시스템 레포지토리
│   │   └── Service/          # 시스템 서비스
│   ├── TAG/                  # 태그 관리
│   │   ├── Controller/       # 태그 컨트롤러
│   │   ├── DTO/             # 태그 DTO
│   │   ├── Model/           # 태그 엔티티
│   │   ├── Repository/      # 태그 레포지토리
│   │   └── Service/         # 태그 서비스
│   ├── USER/                # 사용자 관리
│   │   ├── Controller/      # 사용자 컨트롤러
│   │   ├── DTO/            # 사용자 DTO
│   │   ├── Model/          # 사용자 엔티티
│   │   ├── Repository/     # 사용자 레포지토리
│   │   └── Service/        # 사용자 서비스
│   └── TagDevApplication.java # 메인 애플리케이션
├── front/                    # 프론트엔드
│   ├── src/
│   │   ├── components/      # Vue 컴포넌트
│   │   ├── views/          # 페이지 컴포넌트
│   │   │   ├── Common/     # 공통 컴포넌트
│   │   │   ├── DailyReport/ # 일일보고서
│   │   │   ├── System/     # 시스템 관리
│   │   │   └── tag/        # 태그 관리
│   │   ├── router/         # 라우터 설정
│   │   ├── css/           # 스타일시트
│   │   └── utils/         # 유틸리티
│   ├── package.json
│   └── vite.config.js
├── build.gradle
├── settings.gradle
└── README.md
```

## 🚀 설치 및 실행

### 사전 요구사항
- Java 21 이상
- Node.js 18 이상
- MariaDB 10.6 이상 또는 MySQL 8.0 이상
- Gradle 7.0 이상

### 1. 데이터베이스 설정

```sql
-- MariaDB/MySQL 데이터베이스 생성
CREATE DATABASE intra_web_dev CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 애플리케이션 설정

`src/main/resources/application.yml` 파일을 수정하여 데이터베이스 연결 정보를 설정합니다:

```yaml
spring:
  datasource:
    driver-class-name: org.mariadb.jdbc.Driver
    url: jdbc:mariadb://localhost:3307/intra_web_dev
    username: [your_username]
    password: [your_password]
  security:
    user:
      name: [admin]
      password: [admin_password]
    jwt:
      token:
        secret-key: ["your_jwt_secret_key"]
        expire-length: 3600000
        issuer: TAG_DEV
```

### 3. 백엔드 실행

```bash
# 프로젝트 루트 디렉토리에서
./gradlew bootRun

# 또는
./gradlew build
java -jar build/libs/tag-dev-0.0.1-SNAPSHOT.jar
```

### 4. 프론트엔드 실행

```bash
# 프론트엔드 디렉토리로 이동
cd front

# 의존성 설치
npm install

# 개발 서버 실행
npm run dev

# 프로덕션 빌드 (백엔드 static 폴더로 복사)
npm run build:full
```

### 5. 접속

- **개발 환경**: http://localhost:3000 (프론트엔드), http://localhost:8080 (백엔드)
- **프로덕션**: http://localhost:8080

## 📚 API 문서

상세한 API 문서는 `api.txt` 파일을 참조하세요.

### 주요 API 엔드포인트

#### 사용자 관리
- `POST /user/login` - 로그인
- `POST /user/logout` - 로그아웃
- `POST /user/register` - 회원가입
- `GET /user/findID/{login_id}` - 아이디 찾기

#### 태그 관리
- `GET /tags/getTagList` - 태그 재고 목록 조회
- `GET /tags/proc_step_{ordNo}` - 처리단계 상세 조회
- `GET /tags/setting_info_{ordNo}` - 세팅정보 조회
- `POST /tags/{ordNo}_as` - AS 등록

#### 프로젝트 관리
- `GET /project/searchAll` - 프로젝트 조회
- `POST /project/create` - 프로젝트 생성
- `PUT /project/update{projectCode}` - 프로젝트 수정

#### 일일보고서
- `GET /daily/list` - 보고서 조회
- `POST /daily/create` - 보고서 생성
- `PUT /daily/update` - 보고서 수정

## 🗄️ 데이터베이스

### 주요 테이블

#### 태그 관련
- `TB_TAG_BASIC_INFO`: 태그 기본 정보
- `TB_TAG_COMMON_INFO`: 태그 공통 정보 (MAC, 시리얼번호 등)
- `TB_TAG_SETTING_INFO`: 태그 설정 정보
- `TB_TAG_VERSION_INFO`: 태그 버전 정보
- `TB_TAG_PROC_STEP`: 태그 처리 단계
- `TB_TAG_PROD_AS`: AS 이력

#### 시스템 관련
- `TB_BI_USER_INFO`: 사용자 정보
- `TB_BI_PROJECT_INFO`: 프로젝트 정보
- `TB_BI_DAILY_REPORT_INFO`: 일일보고서 정보
- `TB_BI_DEPT_INFO`: 부서 정보

#### 로그 관련
- `TB_LOG_*`: 각종 로그 테이블들

## 🔒 보안

### 인증 및 권한
- **JWT 토큰**: 7일 유효기간의 JWT 토큰 기반 인증
- **비밀번호 암호화**: BCrypt를 사용한 비밀번호 해싱
- **계정 잠금**: 5회 로그인 실패 시 계정 잠금
- **권한 기반 접근**: 사용자 ACL 레벨에 따른 기능 제한

### 보안 기능
- **CORS 설정**: 크로스 오리진 요청 제어
- **SQL 인젝션 방지**: JPA를 통한 파라미터화된 쿼리
- **XSS 방지**: 입력 데이터 검증 및 이스케이프
- **세션 관리**: 안전한 세션 처리

## 🧪 테스트

```bash
# 백엔드 테스트 실행
./gradlew test

# 프론트엔드 테스트 실행 (프론트엔드 디렉토리에서)
npm run test
```

## 📝 로깅

시스템은 다음과 같은 로그를 자동으로 기록합니다:
- 사용자 로그인/로그아웃 이력
- 태그 정보 변경 이력
- AS 작업 이력
- 버전 관리 이력
- 시스템 사용 이력
