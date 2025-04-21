# 1️⃣ Gradle 빌드 환경 설정 (빌드 전용)
FROM gradle:8.4-jdk21 AS builder

# 2️⃣ 작업 디렉토리 설정
WORKDIR /app

# 3️⃣ 프로젝트 소스 코드 복사
COPY --chown=gradle:gradle . .

# 4️⃣ Gradle 빌드 실행 (bootJar 명시적으로 실행, 테스트 제외)
RUN ./gradlew clean bootJar -x test --stacktrace

# 5️⃣ 실행 환경 설정 (Slim JDK)
FROM openjdk:21-jdk-slim

# 6️⃣ 작업 디렉토리 설정
WORKDIR /app

# 7️⃣ 빌드된 실행 가능한 JAR 파일 복사 (정확한 파일명 사용)
COPY --from=builder /app/build/libs/chat-0.0.1-SNAPSHOT.jar app.jar

# 8️⃣ 컨테이너 실행 시 애플리케이션 실행
CMD ["java", "-jar", "app.jar"]
