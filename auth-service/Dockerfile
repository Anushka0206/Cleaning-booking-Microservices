# Stage 1: Build
FROM maven:3.9.11-amazoncorretto-25 AS build

COPY common/pom.xml common/pom.xml
COPY auth-service/pom.xml auth-service/pom.xml
COPY common/src common/src
COPY auth-service/src auth-service/src

RUN mvn -f common/pom.xml -DskipTests clean install
RUN mvn -f auth-service/pom.xml -DskipTests clean package

# Stage 2: Run
FROM amazoncorretto:25
WORKDIR auth-service
COPY --from=build /auth-service/target/*.jar auth-service.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "auth-service.jar"]
