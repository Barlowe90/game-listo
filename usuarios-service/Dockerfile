# ---- build ----
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /workspace

COPY pom.xml ./pom.xml
COPY usuarios-service/pom.xml ./usuarios-service/pom.xml

COPY usuarios-service/src ./usuarios-service/src

WORKDIR /workspace/usuarios-service
RUN mvn -DskipTests package

# ---- runtime ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /workspace/usuarios-service/target/*.jar /app/app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]