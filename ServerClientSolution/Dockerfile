FROM openjdk:17-jdk-alpine
MAINTAINER baeldung.com
COPY target/ServerClientSolution-1.0-SNAPSHOT.jar app.jar
EXPOSE 49994
ENTRYPOINT ["java","-jar","/app.jar"]