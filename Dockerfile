# syntax=docker/dockerfile:1
# --- Build stage ---
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# --- Runtime stage (non-root) ---
FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S app && adduser -S -G app -H -s /sbin/nologin app
WORKDIR /app
COPY --from=build --chown=app:app /app/target/*.jar app.jar
USER app
ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError"
EXPOSE 8081
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD wget -qO- http://127.0.0.1:8081/api/actuator/health | grep -q '"UP"' || exit 1
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
