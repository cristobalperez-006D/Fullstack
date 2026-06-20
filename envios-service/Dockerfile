FROM maven:3.9.11-eclipse-temurin-25 AS build
WORKDIR /app

COPY pom.xml .

RUN --mount=type=cache,id=maven-cache,target=/root/.m2,sharing=locked \
    mvn dependency:go-offline -B

COPY src ./src

RUN --mount=type=cache,id=maven-cache,target=/root/.m2,sharing=locked \
    mvn clean package -DskipTests

FROM eclipse-temurin:25-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]