# 1. Base image 선택
FROM openjdk:17-jdk-slim

# 2. 변수 설정, 작업 디렉토리 설정
ARG JAR_FILE=build/libs/*.jar
WORKDIR /app

# 3. JAR 파일 복사
COPY ${JAR_FILE} server.jar

# 4. config 디렉토리 생성
RUN mkdir -p /app/config

# 5. 컨테이너 시작 시 JAR 파일 실행
CMD ["java", "-jar", "/app/server.jar", "--spring.config.additional-location=/app/config/"]