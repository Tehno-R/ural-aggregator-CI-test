FROM eclipse-temurin:21-jre

WORKDIR /app
COPY ural-aggregator-service/target/*.jar service.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","service.jar"]
