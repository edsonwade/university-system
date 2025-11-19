# Stage 1: Build the application
# We use a Maven image to build the artifact
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /app

# Copy the parent POM and the module
# IMPORTANT: This Dockerfile assumes the build context is the project ROOT
# Run with: docker build -f config-server/Dockerfile .
COPY pom.xml .
COPY config-server/pom.xml config-server/pom.xml
COPY config-server/src config-server/src

# Build the application, skipping tests as requested
# We use -pl config-server -am to build only the config-server and its dependencies (if any)
# But since we only copied config-server, we can just point to its pom
RUN mvn -f config-server/pom.xml clean package -DskipTests

# Stage 2: Run the application
# We use a lightweight Alpine JRE image for the runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Install curl for health checks
RUN apk add --no-cache curl

# Copy the JAR from the builder stage
COPY --from=builder /app/config-server/target/config-server-0.0.1-SNAPSHOT.jar app.jar

# Health check using Actuator
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:8888/actuator/health || exit 1

# Expose the application port
EXPOSE 8888

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
