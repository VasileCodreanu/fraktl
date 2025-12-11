
# Stage 1: Build
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Download OpenTelemetry Java Agent
ARG OTEL_VERSION=2.21.0
RUN wget -O /app/opentelemetry-javaagent.jar \
    https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_VERSION}/opentelemetry-javaagent.jar

# Download dependencies
COPY ../pom.xml ./
COPY url-manager ./url-manager
RUN mvn dependency:go-offline -B

# Build app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM gcr.io/distroless/java21-debian12

COPY --from=build /app/opentelemetry-javaagent.jar ./opentelemetry-javaagent.jar
COPY --from=build /app/url-manager/target/*.jar ./app.jar

USER nonroot

# JVM config
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

EXPOSE 9090

ENTRYPOINT ["java", "-javaagent:./opentelemetry-javaagent.jar", "-jar", "./app.jar"]