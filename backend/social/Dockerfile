# ---- build ----
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /workspace

COPY pom.xml ./pom.xml
COPY social/pom.xml ./social/pom.xml

COPY social/src ./social/src

WORKDIR /workspace/social
RUN mvn -DskipTests package

# ---- runtime ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /workspace/social/target/*.jar /app/app.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

