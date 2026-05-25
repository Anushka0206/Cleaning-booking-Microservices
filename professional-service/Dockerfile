# Stage 1: Build stage
FROM maven:3.9.11-amazoncorretto-25 AS build

# Copy Maven files for dependency resolution
COPY common/pom.xml common/pom.xml
COPY professional-service/pom.xml professional-service/pom.xml

# Copy application source code
COPY common/src common/src
COPY professional-service/src professional-service/src

# Build the project and create the executable JAR
RUN mvn -f common/pom.xml -DskipTests clean install
RUN mvn -f professional-service/pom.xml -DskipTests clean package

# Stage 2: Run stage
FROM amazoncorretto:25

# Set working directory
WORKDIR professional-service

# Copy the JAR file from the build stage
COPY --from=build /professional-service/target/*.jar professional-service.jar

# Expose port 8081
EXPOSE 8081

# Set the entrypoint command for running the application
ENTRYPOINT ["java", "-jar", "professional-service.jar"]