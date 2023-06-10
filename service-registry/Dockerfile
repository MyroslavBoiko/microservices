FROM openjdk:17-jdk-alpine
ARG JAR_FILE=build/libs/*jar
ADD ${JAR_FILE} service-registry.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/service-registry.jar"]