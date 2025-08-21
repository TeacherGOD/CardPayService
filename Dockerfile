FROM maven:3.9.8-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
ARG SERVICE_NAME
COPY --from=builder /app/${SERVICE_NAME}/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]