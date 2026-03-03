# ---- build ----
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /workspace

COPY pom.xml ./pom.xml
COPY usuarios/pom.xml ./usuarios/pom.xml

COPY usuarios/src ./usuarios/src

WORKDIR /workspace/usuarios
RUN mvn -DskipTests package

# ---- runtime ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /workspace/usuarios/target/*.jar /app/app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]