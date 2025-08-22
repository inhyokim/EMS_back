#!/bin/bash

# EMS 테스트 데이터 시드 스크립트 (cURL 버전)
# Usage: chmod +x seed-data.sh && ./seed-data.sh

BASE_URL="http://localhost:8080"
CONTENT_TYPE="Content-Type: application/json"

echo "🚀 EMS 테스트 데이터 생성을 시작합니다..."
echo ""

# Health Check
echo "📊 Health Check..."
curl -s -X GET "$BASE_URL/api/health" | jq '.'
echo ""

# Device 생성
echo "🔧 Device 생성 중..."

echo "1. 회의실 에너지미터 생성..."
DEVICE1_RESPONSE=$(curl -s -X POST "$BASE_URL/api/devices" \
  -H "$CONTENT_TYPE" \
  -d '{
    "name": "회의실 에너지미터",
    "type": "METER",
    "location": "3층 회의실 A",
    "active": true
  }')
echo "$DEVICE1_RESPONSE" | jq '.'

echo "2. 로비 온도센서 생성..."
DEVICE2_RESPONSE=$(curl -s -X POST "$BASE_URL/api/devices" \
  -H "$CONTENT_TYPE" \
  -d '{
    "name": "로비 온도센서",
    "type": "SENSOR",
    "location": "1층 메인 로비",
    "active": true
  }')
echo "$DEVICE2_RESPONSE" | jq '.'

echo "3. 사무실 에어컨 컨트롤러 생성..."
DEVICE3_RESPONSE=$(curl -s -X POST "$BASE_URL/api/devices" \
  -H "$CONTENT_TYPE" \
  -d '{
    "name": "사무실 에어컨 컨트롤러",
    "type": "CONTROLLER",
    "location": "2층 사무실",
    "active": true
  }')
echo "$DEVICE3_RESPONSE" | jq '.'

echo ""
echo "📋 생성된 Device 목록 확인..."
curl -s -X GET "$BASE_URL/api/devices" | jq '.'

echo ""
echo "⚡ EnergyReading 데이터 생성 중..."

# Device 1 (회의실 미터) - 24시간 데이터
echo "Device 1 (회의실 미터) 에너지 사용량 데이터 생성..."

# 최근 24시간, 1시간 간격 데이터 생성 (실제 시간 기반)
CURRENT_TIME=$(date -u +"%Y-%m-%dT%H:00:00.000Z")
echo "현재 시간 기준: $CURRENT_TIME"

# macOS date 명령어 사용 (Linux에서는 date -d 사용)
for i in {23..0}; do
  if [[ "$OSTYPE" == "darwin"* ]]; then
    # macOS
    TIMESTAMP=$(date -u -v-${i}H +"%Y-%m-%dT%H:00:00.000Z")
  else
    # Linux
    TIMESTAMP=$(date -u -d "$i hours ago" +"%Y-%m-%dT%H:00:00.000Z")
  fi
  
  # 시간대별 에너지 사용량 패턴 (회의실 - 낮에 높고 밤에 낮음)
  HOUR=$(echo $TIMESTAMP | cut -d'T' -f2 | cut -d':' -f1)
  
  if [ $HOUR -ge 8 ] && [ $HOUR -le 18 ]; then
    # 업무시간 (8-18시): 높은 사용량
    KWH=$(echo "scale=1; 15 + ($RANDOM % 10) + ($(echo $TIMESTAMP | cut -c18) % 5)" | bc)
  elif [ $HOUR -ge 19 ] && [ $HOUR -le 22 ]; then
    # 저녁시간 (19-22시): 중간 사용량  
    KWH=$(echo "scale=1; 10 + ($RANDOM % 6)" | bc)
  else
    # 새벽/밤시간: 낮은 사용량
    KWH=$(echo "scale=1; 5 + ($RANDOM % 3)" | bc)
  fi
  
  curl -s -X POST "$BASE_URL/api/readings" \
    -H "$CONTENT_TYPE" \
    -d "{
      \"deviceId\": 1,
      \"ts\": \"$TIMESTAMP\",
      \"kwh\": $KWH
    }" > /dev/null
  
  echo "  ✓ $TIMESTAMP - ${KWH}kWh"
done

echo ""

# Device 2 (로비 센서) - 24시간 데이터
echo "Device 2 (로비 센서) 에너지 사용량 데이터 생성..."

for i in {23..0}; do
  if [[ "$OSTYPE" == "darwin"* ]]; then
    TIMESTAMP=$(date -u -v-${i}H +"%Y-%m-%dT%H:00:00.000Z")
  else
    TIMESTAMP=$(date -u -d "$i hours ago" +"%Y-%m-%dT%H:00:00.000Z")
  fi
  
  HOUR=$(echo $TIMESTAMP | cut -d'T' -f2 | cut -d':' -f1)
  
  if [ $HOUR -ge 6 ] && [ $HOUR -le 22 ]; then
    # 운영시간: 중간 사용량
    KWH=$(echo "scale=1; 7 + ($RANDOM % 4)" | bc)
  else
    # 비운영시간: 낮은 사용량
    KWH=$(echo "scale=1; 3 + ($RANDOM % 2)" | bc)
  fi
  
  curl -s -X POST "$BASE_URL/api/readings" \
    -H "$CONTENT_TYPE" \
    -d "{
      \"deviceId\": 2,
      \"ts\": \"$TIMESTAMP\",
      \"kwh\": $KWH
    }" > /dev/null
  
  echo "  ✓ $TIMESTAMP - ${KWH}kWh"
done

echo ""

# Device 3 (사무실 컨트롤러) - 24시간 데이터  
echo "Device 3 (사무실 컨트롤러) 에너지 사용량 데이터 생성..."

for i in {23..0}; do
  if [[ "$OSTYPE" == "darwin"* ]]; then
    TIMESTAMP=$(date -u -v-${i}H +"%Y-%m-%dT%H:00:00.000Z")
  else
    TIMESTAMP=$(date -u -d "$i hours ago" +"%Y-%m-%dT%H:00:00.000Z")
  fi
  
  HOUR=$(echo $TIMESTAMP | cut -d'T' -f2 | cut -d':' -f1)
  
  if [ $HOUR -ge 8 ] && [ $HOUR -le 18 ]; then
    # 업무시간: 높은 사용량 (에어컨 풀가동)
    KWH=$(echo "scale=1; 25 + ($RANDOM % 10)" | bc)
  elif [ $HOUR -ge 19 ] && [ $HOUR -le 22 ]; then
    # 연장근무: 중간 사용량
    KWH=$(echo "scale=1; 18 + ($RANDOM % 6)" | bc)
  else
    # 비업무시간: 낮은 사용량 (대기전력)
    KWH=$(echo "scale=1; 10 + ($RANDOM % 3)" | bc)
  fi
  
  curl -s -X POST "$BASE_URL/api/readings" \
    -H "$CONTENT_TYPE" \
    -d "{
      \"deviceId\": 3,
      \"ts\": \"$TIMESTAMP\",
      \"kwh\": $KWH
    }" > /dev/null
  
  echo "  ✓ $TIMESTAMP - ${KWH}kWh"
done

echo ""
echo "✅ 테스트 데이터 생성 완료!"
echo ""

# 조회 테스트
echo "📊 생성된 데이터 조회 테스트..."

echo ""
echo "1. Device 1의 최근 24시간 에너지 사용량:"
if [[ "$OSTYPE" == "darwin"* ]]; then
  FROM_TIME=$(date -u -v-24H +"%Y-%m-%dT%H:00:00.000Z")
  TO_TIME=$(date -u +"%Y-%m-%dT%H:00:00.000Z")
else
  FROM_TIME=$(date -u -d "24 hours ago" +"%Y-%m-%dT%H:00:00.000Z")
  TO_TIME=$(date -u +"%Y-%m-%dT%H:00:00.000Z")
fi

curl -s -X GET "$BASE_URL/api/readings?deviceId=1&from=$FROM_TIME&to=$TO_TIME" | jq '.data | length as $count | "총 \($count)개 데이터 조회됨"'

echo ""
echo "2. 모든 Device의 최근 24시간 에너지 사용량:"
curl -s -X GET "$BASE_URL/api/readings/all?from=$FROM_TIME&to=$TO_TIME" | jq '.data | length as $count | "총 \($count)개 데이터 조회됨"'

echo ""
echo "3. Device별 최신 5개 데이터 미리보기:"
for device_id in 1 2 3; do
  echo "   Device $device_id:"
  curl -s -X GET "$BASE_URL/api/readings/device/$device_id?limit=5" | jq '.data[] | "  - \(.ts): \(.kwh)kWh (\(.deviceName))"'
done

echo ""
echo "🎉 시드 데이터 생성 및 테스트 완료!"
echo ""
echo "📝 추가 테스트를 원한다면 다음 명령어를 사용하세요:"
echo "   curl -s '$BASE_URL/api/devices' | jq '.'"
echo "   curl -s '$BASE_URL/api/readings?deviceId=1&from=$FROM_TIME&to=$TO_TIME' | jq '.'"
