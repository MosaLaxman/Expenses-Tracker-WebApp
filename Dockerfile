FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml mvnw mvnw.cmd ./
COPY .mvn .mvn
COPY src src

RUN mvn -q -DskipTests clean package

FROM eclipse-temurin:17-jre
WORKDIR /app

ENV APP_PROFILE=prod
ENV SERVER_PORT=8080

COPY --from=build /app/target/ExpensesTracker-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
