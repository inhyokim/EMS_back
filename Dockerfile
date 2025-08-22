# Build stage
FROM maven:3.9-openjdk-17 AS build

WORKDIR /app

# Copy pom.xml first for dependency caching
COPY pom.xml ./

# Download dependencies
RUN mvn dependency:go-offline -B || true

# Copy source code
COPY src ./src

# Build application
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-jre-slim

WORKDIR /app

# Copy built jar from build stage
COPY --from=build /app/target/ems-*.jar app.jar

# Create non-root user
RUN groupadd -r ems && useradd -r -g ems ems
RUN chown -R ems:ems /app
USER ems

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
