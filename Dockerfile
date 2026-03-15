FROM container-registry.oracle.com/java/jre:21

WORKDIR /app
COPY ural-aggregator-service/target/*.jar service.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","service.jar"]