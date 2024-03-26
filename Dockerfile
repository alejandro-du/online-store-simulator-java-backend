FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
COPY pom.xml .
COPY src/ src/
RUN mvn dependency:go-offline
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
COPY --from=build /target/*.jar /app.jar
CMD java -jar /app.jar
