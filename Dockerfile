# syntax=docker/dockerfile:1

# Build stage
FROM maven:3.8.1-jdk-11-slim@sha256:ef7292a27fe81f13d13dfdf0dabe13443ecb8bcbad6c7a63a9a0cf1e8bd2aa58 AS build
WORKDIR /project
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Package stage
FROM adoptopenjdk/openjdk11:jre-11.0.11_9-alpine@sha256:1fd034ce2ba4ac2c67925782a7be9c6b42f2f2f943330e52d668010a3221ff95
WORKDIR /app
COPY --from=build /project/target/*.jar app.jar
CMD ["java","-jar","app.jar"]