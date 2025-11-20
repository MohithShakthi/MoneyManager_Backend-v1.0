# Stage 1 — Build the JAR
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY moneymanager/pom.xml .
COPY moneymanager/src ./src

RUN mvn clean package -DskipTests

# Stage 2 — Run the JAR
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 9090
ENTRYPOINT ["java", "-jar", "app.jar"]
