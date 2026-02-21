# 1. 구글 서버 안에서 자바 빌드 환경을 세팅함
FROM gradle:8.5-jdk21 AS build
WORKDIR /app

# 2. 형의 전체 소스 코드를 구글 서버로 복사함
COPY --chown=gradle:gradle . .

# 3. 구글 서버 안에서 징~ 빌드 돌아감 (형 컴퓨터 자원 안 씀!)
RUN ./gradlew clean build -x test

# 4. 빌드 완료된 알맹이(jar 파일)만 쏙 빼서 가벼운 실행 환경으로 옮김
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar

# 5. 서버 포트 8080 열고 실행
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]