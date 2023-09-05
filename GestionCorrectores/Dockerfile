FROM maven:3.8.5-eclipse-temurin-17 AS builder

WORKDIR /back
RUN apt update && apt install -y tini

COPY pom.xml ./
RUN [ "mvn", "verify", "clean", "--fail-never" ]

COPY src ./src

# Por defecto se presupone 'db-service' como nombre del servicio de base de datos
# Este valor se puede cambiar en tiempo de ejecución
ENV SPRING_DATASOURCE_URL jdbc:h2:tcp://db-service/ms_database

EXPOSE 8081

ENTRYPOINT [ "tini", "--" ]

CMD [ "mvn", "spring-boot:run", "-Dspring-boot.run.arguments='--spring.profiles.active=docker'" ]