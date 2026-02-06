FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app/

COPY pom.xml .
RUN mvn -e -B dependency:resolve

COPY src ./src
# Skip tests during image build (correct property is -DskipTests)
RUN mvn -B package -DskipTests

FROM eclipse-temurin:21-jre-jammy

RUN useradd -r -u 10001 spring

WORKDIR /app
# Copy the built jar from the builder stage. Use a loose wildcard to match the artifact.
COPY --from=builder /app/target/catalogo-service-*.jar app.jar

RUN chown spring:spring app.jar
USER spring
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

EXPOSE 8082