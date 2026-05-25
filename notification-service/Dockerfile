# Stage 1: Build
FROM maven:3.9.11-amazoncorretto-25 AS build

COPY common/pom.xml common/pom.xml
COPY notification-service/pom.xml notification-service/pom.xml
COPY common/src common/src
COPY notification-service/src notification-service/src

RUN mvn -f common/pom.xml -DskipTests clean install
RUN mvn -f notification-service/pom.xml -DskipTests clean package

# Stage 2: Run
FROM amazoncorretto:25
WORKDIR notification-service
COPY --from=build /notification-service/target/*.jar notification-service.jar
EXPOSE 8087
ENTRYPOINT ["java", "-jar", "notification-service.jar"]
