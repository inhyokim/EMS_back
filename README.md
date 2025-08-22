# EMS Backend - Energy Management System

Spring Boot 기반 에너지 관리 시스템 백엔드

## 주요 기능

- 센서 및 위치 관리 (CRUD)
- 측정값 데이터 저장 및 조회
- CSV 파일 업로드 및 검증
- 주간/월간 에너지 사용량 리포트
- 외부 API 연동 (Open-Meteo 날씨)
- RESTful API 제공
- 전역 예외 처리
- CORS 설정 및 보안

## 기술 스택

- Java 17
- Spring Boot 3.3.5
- Spring Data JPA
- Spring WebFlux (외부 API 호출)
- H2 Database (개발용)
- PostgreSQL (선택사항)
- Maven
- Apache Commons CSV

## 빠른 시작

### 1. 사전 요구사항
- Java 17+
- Maven 3.6+

### 2. 로컬 실행
```bash
cd backend
./mvnw spring-boot:run
```

### 3. 헬스체크
```bash
curl http://localhost:8081/api/health
```

## API 엔드포인트

### 센서 관리
- `GET /api/sensors` - 센서 목록 조회
- `GET /api/sensors/{id}` - 센서 상세 조회
- `POST /api/sensors` - 센서 생성
- `PUT /api/sensors/{id}` - 센서 수정
- `DELETE /api/sensors/{id}` - 센서 삭제

### 측정값 관리
- `POST /api/measurements` - 측정값 생성
- `GET /api/measurements?from=2025-08-22&to=2025-08-22` - 측정값 조회 (기간별)

### CSV 업로드
- `POST /api/readings/upload` - CSV 파일 업로드 및 검증
- `DELETE /api/readings/uploads/{batchId}` - 업로드 배치 삭제

### 리포트
- `GET /api/reports/summary` - 주간/월간 요약 (JSON)
- `GET /api/reports/summary.csv` - 주간/월간 요약 (CSV 다운로드)
- `GET /api/metrics/daily-average` - 일 평균 지표

### 외부 API 연동
- `GET /api/ext/weather-usage` - 서울 현재 날씨 + 최근 24시간 에너지 사용량

### 시스템
- `GET /api/health` - 헬스체크

## 데이터베이스

### 개발 환경 (H2)
- 인메모리 데이터베이스
- 자동 스키마 생성
- 샘플 데이터 자동 로드

### 프로덕션 환경 (PostgreSQL)
```bash
# Docker로 PostgreSQL 실행
docker compose -f infra/docker-compose.yml up -d

# 백엔드 실행 (docker 프로필)
./mvnw spring-boot:run -Dspring-boot.run.profiles=docker
```

### 주요 테이블
- `location` - 위치 정보
- `sensor` - 센서 정보
- `measurement` - 측정값 데이터
- `upload_batch` - CSV 업로드 배치 추적
- `upload_error` - CSV 업로드 오류 정보

## CSV 업로드 형식

### 필수 헤더
```
building_name,zone_name,meter_no,timestamp,value
```

### 예시 데이터
```
building_name,zone_name,meter_no,timestamp,value
Building A,Zone 1,METER001,2025-08-22T10:00:00Z,100.5
Building A,Zone 1,METER001,2025-08-22T11:00:00Z,105.2
Building A,Zone 2,METER002,2025-08-22T10:00:00Z,75.8
```

### 요구사항
- `timestamp`: ISO-8601 형식 (예: `2025-08-22T10:00:00Z`)
- `value`: 0 이상의 숫자
- 모든 필드는 필수

## 측정값 생성 예시

### JSON 요청
```json
{
  "sensorId": 1,
  "value": 125.5,
  "measuredAt": "2025-08-22T14:30:00Z"
}
```

### cURL 예시
```bash
curl -X POST http://localhost:8081/api/measurements \
  -H "Content-Type: application/json" \
  -d '{"sensorId": 1, "value": 125.5}'
```

## CORS 설정

프론트엔드(localhost:3000)에서의 요청을 허용하도록 설정되어 있습니다.

### 허용된 Origin
- `http://localhost:3000`
- `http://127.0.0.1:3000`

### 허용된 메서드
- GET, POST, PUT, DELETE, PATCH, OPTIONS

## 문제 해결

### 1. 포트 충돌
기본 포트 8081이 사용 중인 경우:
```bash
./mvnw spring-boot:run -Dserver.port=8082
```

### 2. 데이터베이스 연결 오류
H2 데이터베이스 콘솔 접속:
```
http://localhost:8081/h2-console
JDBC URL: jdbc:h2:mem:emsdb
Username: sa
Password: (비워두기)
```

### 3. CORS 오류
- 백엔드 서버가 실행 중인지 확인
- 프론트엔드가 `http://localhost:3000`에서 실행 중인지 확인
- `CorsConfig` 클래스가 정상적으로 로드되었는지 확인

### 4. CSV 업로드 오류
- 파일 형식이 올바른지 확인
- 헤더가 정확한지 확인
- 데이터 값이 유효한지 확인

## 개발 가이드

### 프로젝트 구조
```
src/main/java/com/kt/ems/
├── config/          # 설정 클래스
├── controller/      # REST 컨트롤러
├── domain/         # 엔티티 클래스
├── dto/            # 데이터 전송 객체
├── repository/     # 데이터 접근 계층
├── service/        # 비즈니스 로직
└── web/            # 웹 관련 클래스
```

### 로깅
- 로그 레벨: DEBUG (개발), INFO (운영)
- 로그 파일: `logs/` 디렉토리

### 테스트
```bash
# 단위 테스트 실행
./mvnw test

# 통합 테스트 실행
./mvnw verify
```

## 배포

### JAR 파일 생성
```bash
./mvnw clean package
```

### 실행
```bash
java -jar target/ems-backend-0.0.1-SNAPSHOT.jar
```

### 환경 변수
- `SPRING_PROFILES_ACTIVE`: 실행 프로필 (local, docker, production)
- `FRONT_ORIGIN`: 프론트엔드 Origin URL
- `DATABASE_URL`: 데이터베이스 연결 URL
```

이제 프론트엔드 README를 업데이트하겠습니다:

```markdown:frontend/README.md
# EMS Frontend - Energy Management System

Next.js 14 기반 에너지 관리 시스템 프론트엔드

## 주요 기능

- 대시보드 및 통계 시각화
- 센서 관리 (CRUD)
- 측정값 입력 및 조회
- CSV 파일 업로드
- 일 평균 지표 및 리포트
- 반응형 웹 디자인
- 실시간 데이터 업데이트

## 기술 스택

- **Framework**: Next.js 14 (App Router)
- **Language**: TypeScript
- **Styling**: Tailwind CSS
- **Icons**: Lucide React
- **HTTP Client**: Axios
- **State Management**: React Query (TanStack Query)
- **Form Handling**: React Hook Form + Zod
- **Package Manager**: npm

## 빠른 시작

### 1. 사전 요구사항
- Node.js 18+
- npm 9+
- 백엔드 서버 실행 (localhost:8081)

### 2. 의존성 설치
```bash
cd frontend
npm install
```

### 3. 환경 변수 설정
`.env.local` 파일 생성:
```bash
NEXT_PUBLIC_API_BASE_URL=http://localhost:8081
```

### 4. 개발 서버 실행
```bash
npm run dev
```

### 5. 접속
- **URL**: http://localhost:3000
- **대시보드**: http://localhost:3000/dashboard
- **센서 관리**: http://localhost:3000/sensors
- **측정치 입력**: http://localhost:3000/measurements/new
- **데이터 업로드**: http://localhost:3000/upload
- **일 평균 지표**: http://localhost:3000/metrics
- **리포트**: http://localhost:3000/reports

## 페이지 구성

### 대시보드 (/dashboard)
- 에너지 사용량 통계 카드
- 최근 측정값 차트
- 시스템 상태 모니터링
- 빠른 액션 버튼

### 센서 관리 (/sensors)
- 센서 목록 테이블
- 센서 추가/수정/삭제
- 실시간 상태 표시
- 필터링 및 검색

### 측정치 입력/조회 (/measurements/new)
- 개별 측정치 입력 폼
- 기간별 측정치 조회
- 측정치 데이터 테이블
- 실시간 데이터 업데이트

### 데이터 업로드 (/upload)
- CSV 파일 업로드
- 파일 검증 및 오류 표시
- 업로드 결과 요약
- 배치 관리

### 일 평균 지표 (/metrics)
- 일별 평균 사용량 차트
- 센서별 통계
- 기간별 비교 분석

### 리포트 (/reports)
- 주간/월간 요약 리포트
- CSV 다운로드
- 데이터 분석 차트

## 컴포넌트 구조

```
src/
├── components/           # 재사용 컴포넌트
│   ├── MeasurementForm.tsx
│   ├── MeasurementRange.tsx
│   ├── SensorForm.tsx
│   ├── SensorTable.tsx
│   └── DailyAverageChart.tsx
├── lib/                  # 유틸리티 및 설정
│   ├── api.ts           # API 클라이언트
│   ├── queryClient.tsx  # React Query 설정
│   └── types.ts         # TypeScript 타입 정의
└── types/               # 타입 정의
    └── index.ts
```

## API 통신

### API 클라이언트 (`src/lib/api.ts`)
```typescript
// 기본 설정
const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE_URL,
  timeout: 15000,
  headers: { "Content-Type": "application/json" },
});

// 에러 처리
export const err = (e: any) => e?.response?.data?.error ?? e?.message ?? "Unexpected error";
```

### 주요 API 호출
```typescript
// 센서 목록 조회
const sensors = await api.get("/api/sensors");

// 측정치 생성
const measurement = await api.post("/api/measurements", {
  sensorId: 1,
  value: 125.5,
  measuredAt: "2025-08-22T14:30:00Z"
});

// CSV 업로드
const formData = new FormData();
formData.append("file", csvFile);
const result = await api.post("/api/readings/upload", formData, {
  headers: { "Content-Type": undefined }
});
```

## 데이터 타입

### 센서 (Sensor)
```typescript
interface Sensor {
  id: number;
  sensorName: string;
  type: string;
  location: Location;
}
```

### 측정치 (Measurement)
```typescript
interface Measurement {
  id: number;
  sensor: Sensor;
  value: number;
  measuredAt: string;
}
```

### 측정치 생성 DTO
```typescript
interface MeasurementCreateDto {
  sensorId: number;
  value: number;
  measuredAt?: string;
}
```

## 사용 예시

### 측정치 입력
1. **Sensor ID**: 1 (MAIN_FEEDER - B1 전기실)
2. **Value**: 125.5
3. **Measured At**: 2025-08-22T14:30:00Z (선택사항)

### CSV 업로드
1. CSV 파일 준비 (building_name, zone_name, meter_no, timestamp, value 헤더)
2. 파일 선택
3. 업로드 버튼 클릭
4. 결과 확인

## 스타일링

### Tailwind CSS 클래스
```css
/* 버튼 */
.btn, .btn-danger

/* 입력 필드 */
.input

/* 카드 */
.border.rounded-xl.bg-white

/* 테이블 */
.table.w-full.border-separate
```

### 반응형 디자인
- 모바일 우선 설계
- Tailwind CSS 브레이크포인트 활용
- 터치 친화적 인터페이스

## 개발 도구

### 스크립트
```bash
# 개발 서버
npm run dev

# 프로덕션 빌드
npm run build

# 프로덕션 서버
npm run start

# 타입 체크
npm run type-check

# 린트 검사
npm run lint
```

### 코드 품질
- **ESLint**: 코드 스타일 및 오류 검사
- **TypeScript**: 정적 타입 검사
- **Prettier**: 코드 포맷팅

## 문제 해결

### 1. CORS 오류
- 백엔드 서버가 실행 중인지 확인
- `NEXT_PUBLIC_API_BASE_URL` 환경 변수 확인
- 백엔드 CORS 설정 확인

### 2. API 연결 오류
```bash
# 백엔드 헬스체크
curl http://localhost:8081/api/health

# 프론트엔드 환경 변수 확인
cat .env.local
```

### 3. 빌드 오류
```bash
# 의존성 재설치
rm -rf node_modules package-lock.json
npm install

# 캐시 클리어
npm run build -- --no-cache
```

### 4. 타입 오류
```bash
# TypeScript 타입 체크
npm run type-check

# 타입 정의 업데이트
npm run build
```

## 배포

### Vercel 배포
1. GitHub 저장소 연결
2. 환경 변수 설정
3. 자동 배포 설정

### Docker 배포
```dockerfile
FROM node:18-alpine
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production
COPY . .
RUN npm run build
EXPOSE 3000
CMD ["npm", "start"]
```

### 환경 변수
- `NEXT_PUBLIC_API_BASE_URL`: 백엔드 API URL
- `NODE_ENV`: 실행 환경 (development, production)

## 성능 최적화

### Next.js 기능
- **App Router**: 최신 라우팅 시스템
- **Server Components**: 서버 사이드 렌더링
- **Image Optimization**: 자동 이미지 최적화
- **Bundle Optimization**: 자동 코드 분할

### 클라이언트 최적화
- React Query를 통한 캐싱
- 컴포넌트 지연 로딩
- 불필요한 리렌더링 방지
- 이미지 최적화

## 보안

- 환경 변수를 통한 설정 관리
- CSRF 보호 (Next.js 기본 제공)
- XSS 방지 (React 기본 제공)
- 클라이언트 사이드 입력 검증
- API 키 보안 관리
```

이제 루트 README도 업데이트하겠습니다:

```markdown:README.md
# EMS - Energy Management System

KT 스마트빌딩 EMS Lite 시스템

## 프로젝트 개요

에너지 관리 시스템(EMS)은 건물의 에너지 사용량을 모니터링하고 관리하는 웹 애플리케이션입니다. 센서 데이터 수집, CSV 파일 업로드, 측정치 입력, 리포트 생성 등의 기능을 제공합니다.

## 시스템 아키텍처

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │    Backend      │    │   Database      │
│   (Next.js)     │◄──►│  (Spring Boot)  │◄──►│   (H2/PostgreSQL)│
│   localhost:3000│    │  localhost:8081 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 주요 기능

###  대시보드
- 실시간 에너지 사용량 모니터링
- 센서별 상태 표시
- 최근 활동 타임라인

###  센서 관리
- 센서 등록/수정/삭제
- 위치별 센서 그룹화
- 실시간 센서 상태 모니터링

###  측정치 관리
- 개별 측정치 입력
- 기간별 측정치 조회
- 실시간 데이터 업데이트

###  데이터 업로드
- CSV 파일 업로드 및 검증
- 배치 처리 및 오류 관리
- 업로드 결과 요약

###  리포트
- 주간/월간 에너지 사용량 리포트
- CSV 다운로드 기능
- 데이터 분석 차트

## 기술 스택

### Frontend
- **Framework**: Next.js 14 (App Router)
- **Language**: TypeScript
- **Styling**: Tailwind CSS
- **State Management**: React Query (TanStack Query)
- **Form Handling**: React Hook Form + Zod
- **Icons**: Lucide React

### Backend
- **Framework**: Spring Boot 3.3.5
- **Language**: Java 17
- **Database**: H2 (개발), PostgreSQL (운영)
- **ORM**: Spring Data JPA
- **API**: RESTful API
- **Build Tool**: Maven

## 빠른 시작

### 1. 저장소 클론
```bash
git clone <repository-url>
cd ems
```

### 2. 백엔드 실행
```bash
cd backend
./mvnw spring-boot:run
```

### 3. 프론트엔드 실행
```bash
cd frontend
npm install
npm run dev
```

### 4. 접속
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8081
- **H2 Console**: http://localhost:8081/h2-console

## 사용 가이드

### 측정치 입력
1. **Sensor ID**: 1 (MAIN_FEEDER), 2 (CHILLER_TEMP)
2. **Value**: 측정값 (숫자)
3. **Measured At**: 측정 시간 (ISO-8601 형식, 선택사항)

### CSV 업로드
1. CSV 파일 준비 (building_name, zone_name, meter_no, timestamp, value 헤더)
2. 파일 선택 후 업로드
3. 검증 결과 확인

### 센서 관리
1. 센서 목록에서 센서 선택
2. 센서 정보 수정 또는 삭제
3. 새로운 센서 추가

## API 문서

### 주요 엔드포인트
- `GET /api/health` - 헬스체크
- `GET /api/sensors` - 센서 목록
- `POST /api/measurements` - 측정치 생성
- `POST /api/readings/upload` - CSV 업로드
- `GET /api/reports/summary` - 리포트 요약

### 예시 요청
```bash
# 측정치 생성
curl -X POST http://localhost:8081/api/measurements \
  -H "Content-Type: application/json" \
  -d '{"sensorId": 1, "value": 125.5}'

# 센서 목록 조회
curl http://localhost:8081/api/sensors

# 헬스체크
curl http://localhost:8081/api/health
```

## 개발 가이드

### 프로젝트 구조
```
ems/
├── backend/           # Spring Boot 백엔드
│   ├── src/
│   ├── pom.xml
│   └── README.md
├── frontend/          # Next.js 프론트엔드
│   ├── src/
│   ├── package.json
│   └── README.md
├── infra/            # 인프라 설정
│   └── docker-compose.yml
└── README.md         # 프로젝트 문서
```

### 개발 환경 설정
1. **Java 17** 설치
2. **Node.js 18+** 설치
3. **Maven** 설치 (선택사항, Maven Wrapper 사용)
4. **Git** 설치

### 코드 스타일
- **Backend**: Google Java Style Guide
- **Frontend**: ESLint + Prettier
- **TypeScript**: Strict Mode

## 배포

### 개발 환경
```bash
# 백엔드
cd backend && ./mvnw spring-boot:run

# 프론트엔드
cd frontend && npm run dev
```

### 운영 환경
```bash
# Docker Compose
docker compose -f infra/docker-compose.yml up -d

# 또는 개별 배포
# Backend
cd backend && ./mvnw clean package
java -jar target/ems-backend-0.0.1-SNAPSHOT.jar

# Frontend
cd frontend && npm run build && npm start
```

## 문제 해결

### 일반적인 문제
1. **포트 충돌**: 8081, 3000 포트 사용 확인
2. **CORS 오류**: 백엔드 CORS 설정 확인
3. **데이터베이스 연결**: H2/PostgreSQL 연결 확인
4. **API 오류**: 백엔드 로그 확인

### 로그 확인
```bash
# 백엔드 로그
tail -f backend/logs/application.log

# 프론트엔드 로그
# 브라우저 개발자 도구 콘솔
```

## 기여 가이드

1. Fork 저장소
2. 기능 브랜치 생성
3. 변경사항 커밋
4. Pull Request 생성

## 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.

## 연락처

- **개발팀**: KT EMS 개발팀
- **이메일**: ems-dev@kt.com
- **문서**: [Wiki](https://github.com/kt/ems/wiki)
```

이제 각 README 파일이 최신 상태로 업데이트되었습니다. 주요 변경사항:

### Backend README
- 최신 기능 및 기술 스택 반영
- 상세한 API 문서 추가
- 문제 해결 가이드 개선
- 개발 가이드 추가

### Frontend README
- Next.js 14 App Router 반영
- React Query, React Hook Form 등 최신 라이브러리 반영
- 상세한 컴포넌트 구조 설명
- API 통신 예시 추가

### Root README
- 전체 시스템 아키텍처 개요
- 빠른 시작 가이드
- 사용 가이드 추가
- 배포 및 문제 해결 가이드