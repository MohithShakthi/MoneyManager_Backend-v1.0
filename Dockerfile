# Stage 1 — Build the project
FROM eclipse-temurin:21 AS build
WORKDIR /app

COPY . .
RUN ./mvnw clean package -DskipTests

# Stage 2 — Run the app
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 9090
ENTRYPOINT ["java", "-jar", "app.jar"]
