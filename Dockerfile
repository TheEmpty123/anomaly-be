# ==========================
# Build Stage
# ==========================
FROM maven:3.9.11-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY src src

RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# ==========================
# Runtime Stage
# ==========================
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=builder /app/target/backend.jar app.jar

RUN mkdir -p /data/backend/logs

EXPOSE 8080

VOLUME ["/data/backend/logs"]

ENTRYPOINT ["java","-jar","app.jar"]
