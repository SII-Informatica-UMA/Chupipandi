FROM maven:3.8.5-eclipse-temurin-17 AS builder

WORKDIR /back
RUN apt update && apt install -y tini

COPY pom.xml ./
RUN [ "mvn", "verify", "clean", "--fail-never" ]

COPY src ./src

EXPOSE 8080

ENTRYPOINT [ "tini", "--" ]

CMD [ "mvn", "spring-boot:run", "-Dspring-boot.run.arguments='--spring.profiles.active=docker'" ]