FROM openjdk:8-jdk-alpine
FROM maven:alpine
COPY target/catalogapi-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT [ "java", "-jar", "app.jar"  ]