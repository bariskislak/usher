FROM gradle:8.11.1-jdk21 AS build

WORKDIR /workspace
COPY . .

ARG MODULE
RUN gradle ":${MODULE}:bootJar" --no-daemon

FROM eclipse-temurin:21-jre

WORKDIR /app

ARG MODULE
COPY --from=build /workspace/${MODULE}/build/libs/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
