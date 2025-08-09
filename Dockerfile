# Build stage
# FROM maven:3.9-eclipse-temurin-17 AS build

# LABEL maintainer="k.ladhani1@gmail.com" AUTHOR="Kunal Ladhani" VERSION="1.0.0" DESCRIPTION="Parser Engine"

# WORKDIR /app

# COPY pom.xml .

# COPY src ./src

# RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY /target/parser-engine-1.0.0.jar /app/parser-engine.jar

# Set timezone
# ENV TZ=Asia/Kolkata

# RUN apt-get update && apt-get install -y tzdata && rm -rf /var/lib/apt/lists/*

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "parser-engine.jar"]