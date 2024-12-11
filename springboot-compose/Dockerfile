# Stage 1: Build dependencies
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy the pom.xml and mvnw to utilize Docker's cache and avoid rebuilding dependencies unless they change
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Download dependencies in the first build step to cache dependencies and avoid re-downloading every time
RUN ./mvnw dependency:go-offline

# Copy the source code
COPY src src

# Build the application (in this case, assuming a Spring Boot app)
RUN ./mvnw package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port your app listens on
EXPOSE 8081

# Command to run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]