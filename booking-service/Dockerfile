# Stage 1: Build stage
FROM maven:3.9.11-amazoncorretto-25 AS build

# Copy Maven files for dependency resolution
COPY common/pom.xml common/pom.xml
COPY booking-service/pom.xml booking-service/pom.xml

# Copy application source code
COPY common/src common/src
COPY booking-service/src booking-service/src

# Build common first, then booking-service
RUN mvn -f common/pom.xml -DskipTests clean install
RUN mvn -f booking-service/pom.xml -DskipTests clean package

# Stage 2: Run stage
FROM amazoncorretto:25

# Set working directory
WORKDIR booking-service

# Copy the JAR file from the build stage
COPY --from=build /booking-service/target/*.jar booking-service.jar

# Expose port 8082
EXPOSE 8082

# Set the entrypoint command for running the application
ENTRYPOINT ["java", "-jar", "booking-service.jar"]