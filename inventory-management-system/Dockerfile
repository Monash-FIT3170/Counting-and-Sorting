FROM openjdk:17-jdk-slim
COPY target/*.jar app.jar
EXPOSE 8080
WORKDIR /app
ENTRYPOINT ["java", "-jar", "/app.jar"]
EXPOSE 20231