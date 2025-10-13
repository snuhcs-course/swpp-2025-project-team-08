FROM eclipse-temurin:17-jdk-focal
WORKDIR /app
ARG JAR_FILE=build/libs/itda-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8080