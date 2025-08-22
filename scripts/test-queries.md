# EMS API 테스트 쿼리 가이드

## 🚀 시드 데이터 생성

### 방법 1: HTTP 파일 사용 (VS Code REST Client)
```bash
# VS Code에서 seed-data.http 파일을 열고
# 각 요청을 순차적으로 실행 (Ctrl/Cmd + Alt + R)
```

### 방법 2: Bash 스크립트 사용
```bash
# 실행 권한 부여
chmod +x seed-data.sh

# 스크립트 실행
./seed-data.sh
```

## 📊 생성되는 테스트 데이터

### Devices (3개)
1. **회의실 에너지미터** (ID: 1)
   - Type: METER
   - Location: 3층 회의실 A
   - 업무시간 높은 사용량, 밤시간 낮은 사용량

2. **로비 온도센서** (ID: 2)  
   - Type: SENSOR
   - Location: 1층 메인 로비
   - 운영시간 중간 사용량, 비운영시간 낮은 사용량

3. **사무실 에어컨 컨트롤러** (ID: 3)
   - Type: CONTROLLER
   - Location: 2층 사무실
   - 업무시간 최고 사용량, 연장근무 중간, 밤시간 대기전력

### EnergyReadings (72개)
- 각 Device당 24개 데이터 (최근 24시간, 1시간 간격)
- 실제 에너지 사용 패턴을 반영한 현실적인 데이터
- 시간대별 차등 사용량 적용

## 🔍 주요 API 테스트 쿼리

### 1. Health Check
```bash
curl -X GET "http://localhost:8080/api/health"
```

### 2. Device 목록 조회
```bash
curl -X GET "http://localhost:8080/api/devices" | jq '.'
```

### 3. 특정 Device의 24시간 에너지 사용량
```bash
# Device 1의 최근 24시간 데이터
curl -X GET "http://localhost:8080/api/readings?deviceId=1&from=2025-01-21T15:00:00.000Z&to=2025-01-22T15:00:00.000Z" | jq '.'
```

### 4. 모든 Device의 24시간 에너지 사용량
```bash
curl -X GET "http://localhost:8080/api/readings/all?from=2025-01-21T15:00:00.000Z&to=2025-01-22T15:00:00.000Z" | jq '.'
```

### 5. Device별 최신 데이터 조회
```bash
# Device 1의 최신 10개 데이터
curl -X GET "http://localhost:8080/api/readings/device/1?limit=10" | jq '.'
```

### 6. 실시간 시간 기준 쿼리 (동적)
```bash
# macOS 
FROM_TIME=$(date -u -v-24H +"%Y-%m-%dT%H:00:00.000Z")
TO_TIME=$(date -u +"%Y-%m-%dT%H:00:00.000Z")

# Linux
FROM_TIME=$(date -u -d "24 hours ago" +"%Y-%m-%dT%H:00:00.000Z")  
TO_TIME=$(date -u +"%Y-%m-%dT%H:00:00.000Z")

# 쿼리 실행
curl -X GET "http://localhost:8080/api/readings?deviceId=1&from=$FROM_TIME&to=$TO_TIME" | jq '.'
```

## 📈 예상 응답 형식

### Device 목록 응답
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "회의실 에너지미터",
      "type": "METER",
      "location": "3층 회의실 A",
      "active": true,
      "createdAt": "2025-01-22T15:00:00.000Z"
    }
  ]
}
```

### EnergyReading 조회 응답
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "deviceId": 1,
      "deviceName": "회의실 에너지미터",
      "ts": "2025-01-22T14:00:00.000Z",
      "kwh": 28.3
    }
  ]
}
```

## 🛠️ 트러블슈팅

### Backend가 실행되지 않는 경우
```bash
cd backend
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
mvn -U clean spring-boot:run
```

### PostgreSQL이 실행되지 않는 경우  
```bash
cd infra
docker compose up -d
```

### 데이터가 보이지 않는 경우
1. 시간대 확인 (UTC 기준)
2. deviceId 파라미터 확인
3. from/to 시간 형식 확인 (ISO-8601)

## 🎯 성능 테스트

### 대량 데이터 조회
```bash
# 모든 장치의 전체 데이터
curl -w "@curl-format.txt" -X GET "http://localhost:8080/api/readings/all?from=2025-01-20T00:00:00.000Z&to=2025-01-23T00:00:00.000Z"
```

### 제한된 결과 조회
```bash
# 최대 10개 결과만
curl -X GET "http://localhost:8080/api/readings?deviceId=1&from=2025-01-21T00:00:00.000Z&to=2025-01-22T00:00:00.000Z&limit=10"
```

## 📋 데이터 검증 체크리스트

- [ ] 3개 Device 생성 완료
- [ ] 총 72개 EnergyReading 생성 완료 (24개 × 3장치)
- [ ] 시간대별 사용량 패턴 적용 확인
- [ ] Device별 조회 정상 작동
- [ ] 시간 범위 필터링 정상 작동
- [ ] API 응답 형식 일관성 확인
