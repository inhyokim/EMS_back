# EMS Backend - Energy Management System

Spring Boot 기반 에너지 관리 시스템 백엔드

## 주요 기능

- 센서 및 위치 관리 (CRUD)
- 측정값 데이터 저장 및 조회
- CSV 파일 업로드 및 검증
- 주간/월간 에너지 사용량 리포트
- **외부 API 연동 (Open-Meteo 날씨)**
- RESTful API 제공
- 전역 예외 처리

## 기술 스택

- Java 17
- Spring Boot 3.3.5
- Spring Data JPA
- Spring WebFlux (외부 API 호출)
- H2 Database (개발용)
- PostgreSQL (선택사항)
- Maven

## 서버 점검 체크리스트

1) **백엔드 실행 프로필 확인**
   - local: jdbc:postgresql://localhost:5432/emsdb  
   - docker: jdbc:postgresql://ems-postgres:5432/emsdb  
   - `./mvnw spring-boot:run -Dspring-boot.run.profiles=local|docker`

2) **DB 볼륨 초기화(POSTGRES_USER/DB 변경 시 필수)**
```bash
docker compose -f infra/docker-compose.yml down -v
docker compose -f infra/docker-compose.yml up -d
```

3) **SQL 자동 실행 여부**
   - `spring.sql.init.mode=always` (schema.sql / data.sql이 매 실행 적용)

4) **CORS**
   - `CorsConfig`에서 `http://localhost:3000` 허용
   - 프론트는 `file://`이 아닌 `http://localhost:3000`으로 실행

5) **헬스체크**
   - `GET http://localhost:8080/api/health` → `{"status":"UP"}`

## API 엔드포인트

### 센서 관리
- `GET /api/sensors` - 센서 목록 조회
- `GET /api/sensors/{id}` - 센서 상세 조회
- `POST /api/sensors` - 센서 생성
- `PUT /api/sensors/{id}` - 센서 수정
- `DELETE /api/sensors/{id}` - 센서 삭제

### 측정값 관리
- `POST /api/measurements` - 측정값 생성
- `GET /api/measurements` - 측정값 조회 (기간별)

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

## 실행 방법

### 로컬 실행
```bash
cd backend
./mvnw -U spring-boot:run -Dspring-boot.run.profiles=local
```

### Docker 실행
```bash
# PostgreSQL 컨테이너 시작
docker compose -f infra/docker-compose.yml up -d

# 백엔드 실행
cd backend
./mvnw -U spring-boot:run -Dspring-boot.run.profiles=docker
```

## 데이터베이스 스키마

### 주요 테이블
- `location` - 위치 정보
- `sensor` - 센서 정보
- `measurement` - 측정값 데이터
- `upload_batch` - CSV 업로드 배치 추적
- `upload_error` - CSV 업로드 오류 정보

## 연결 오류 해결 체크리스트

1) **백엔드 실행**
   ```bash
   cd backend
   ./mvnw -U spring-boot:run -Dspring-boot.run.profiles=local
   ```
   * `GET http://localhost:8081/api/health` → `{"status":"UP"}` 확인
   * `GET http://localhost:8081/api/ext/weather-usage` → 날씨 데이터 확인

2) **프론트 실행**
   ```bash
   cd frontend
   npm i
   cp .env.local.example .env.local 2>/dev/null || true
   npm run dev
   # http://localhost:3000 접속
   ```

3) **404 발생 시**
   * URL 오타 확인: `/api/health`, `/api/sensors`, `/api/metrics/daily-average`, `/api/reports/summary`
   * 백엔드 로그에 매핑 인식되는지 확인

4) **CORS 에러 시**
   * `CorsConfig`가 컴파일/빈 등록 되었는지, 프론트 Origin(`http://localhost:3000`)이 맞는지
   * (옵션) 프록시로 `/_ems/*`를 사용하면 CORS 없이 우회 가능