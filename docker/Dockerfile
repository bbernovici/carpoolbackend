FROM java:8
EXPOSE 8080
ADD carpool-backend-1.0-SNAPSHOT.jar carpool-backend.jar
ENTRYPOINT ["java", "-Dmongodb.uri=//mongodb", "-Dneo4j.uri=//neo4j" "-jar", "carpool-backend.jar"]