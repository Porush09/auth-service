# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy only the pom.xml first to cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code and build the jar
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the final lightweight image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar auth-service.jar

# Expose the port (Auth service usually runs on 8080 or 9000)
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "auth-service.jar"]