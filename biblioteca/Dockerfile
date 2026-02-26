# ---- build ----
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /workspace

COPY pom.xml ./pom.xml
COPY biblioteca/pom.xml ./biblioteca/pom.xml

COPY biblioteca/src ./biblioteca/src

WORKDIR /workspace/biblioteca
RUN mvn -DskipTests package

# ---- runtime ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /workspace/biblioteca/target/*.jar /app/app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "/app/app.jar"]