#!/bin/bash

set -e  # 에러 발생 시 즉시 종료

echo "🔍 .env 파일 존재 여부 확인 중..."
if [ ! -f .env ]; then
  echo "❌ .env 파일이 없습니다. 환경 변수를 확인하세요."
  exit 1
fi

# .env 파일 로드 (환경변수 사용을 위해)
export $(grep -v '^#' .env | xargs)

APP_IMAGE="${DOCKER_USERNAME}/sunsuwedding-chat:latest"

echo "🧼 기존 컨테이너 정리 중..."
docker-compose down -v --remove-orphans

echo "🗑 기존 앱 이미지 전체 삭제 중..."
docker images "$APP_IMAGE" --format "{{.ID}}" | xargs -r docker rmi

echo "🧹 dangling 이미지 정리 중..."
docker image prune -f

echo "📦 앱 이미지 최신 pull 중..."
docker-compose pull app

echo "🚀 컨테이너 재시작 중..."
docker-compose up -d

echo "✅ 배포 완료!"