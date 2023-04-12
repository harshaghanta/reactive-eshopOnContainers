FROM openjdk:8-jre-alpine as base

FROM maven:alpine as build
WORKDIR /src
COPY BuildingBlocks/EventBus/ .

WORKDIR /src/eventbus
RUN mvn install

WORKDIR /src/eventbus-rabbitmq
RUN mvn install

WORKDIR /src/catalogapi
COPY Services/catalogapi .
RUN mvn package

FROM base as final
WORKDIR /
COPY --from=build /src/catalogapi/target/catalogapi-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT [ "java", "-jar", "app.jar", "--spring.profiles.active=prod" ]