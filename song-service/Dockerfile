FROM openjdk:17-jdk-alpine
ARG JAR_FILE=build/libs/*jar
ADD ${JAR_FILE} song-service.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/song-service.jar"]
