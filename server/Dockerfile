FROM eclipse-temurin:21-jdk-alpine

# 2. 컨테이너 안에서 작업할 폴더 지정
WORKDIR /app

# 3. 형이 빌드한 jar 파일을 컨테이너 안으로 복사
COPY build/libs/*-SNAPSHOT.jar app.jar

# 4. 서버가 사용할 8080 포트 열어두기
EXPOSE 8080

# 5. 서버 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]