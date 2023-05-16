FROM openjdk:17-jdk-alpine
ARG JAR_FILE=build/libs/*jar
ADD ${JAR_FILE} resource-processor.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/resource-processor.jar"]