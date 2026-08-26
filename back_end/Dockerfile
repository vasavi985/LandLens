# ========================================================
# Stage 1: Build Phase
# ========================================================
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /build

# Copy only pom.xml to cache dependency resolution
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build the package
COPY src ./src
RUN mvn package -DskipTests -B

# ========================================================
# Stage 2: Runtime Phase
# ========================================================
FROM eclipse-temurin:21-jre AS runner
WORKDIR /app

# Install curl for docker-compose healthchecks
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*

# Add a non-root system user for security hardening
RUN groupadd -r spring && useradd -r -g spring spring

# Create logs directory and set ownership of /app to the spring user
RUN mkdir -p /app/logs && chown -R spring:spring /app

USER spring:spring

# Copy built jar from stage 1
COPY --from=builder /build/target/landlens-*.jar app.jar

# Application environment defaults
ENV PORT=8080
EXPOSE 8080

# Environment-based tuning for JVM in containers
ENTRYPOINT ["java", \
            "-XX:+UseContainerSupport", \
            "-XX:MaxRAMPercentage=75.0", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-Dspring.profiles.active=prod", \
            "-jar", "app.jar"]
