FROM openjdk:17-jdk-slim
ARG JAR_FILE=build/libs/saisai-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul","-jar", "app.jar", "--spring.profiles.active=dev"]
