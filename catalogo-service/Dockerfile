# ---- build ----
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /workspace

COPY pom.xml ./pom.xml
COPY catalogo-service/pom.xml ./catalogo-service/pom.xml

COPY catalogo-service/src ./catalogo-service/src

WORKDIR /workspace/catalogo-service
RUN mvn -DskipTests package

# ---- runtime ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /workspace/catalogo-service/target/*.jar /app/app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]