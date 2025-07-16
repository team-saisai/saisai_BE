FROM openjdk:17-jdk-slim AS builder
WORKDIR /app
COPY . .
RUN ./gradlew bootJar

FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar /app.jar
ENTRYPOINT ["java", "-Xmx400m", "-jar", "-Duser.timezone=Asia/Seoul", "-Dspring.profiles.active=dev", "/app.jar"]