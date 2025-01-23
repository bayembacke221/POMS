#Image de base
FROM openjdk:17-alpine
LABEL maintainer="mbackembaye74@gmail.com"
VOLUME /main-app
ADD target/demo-mssql-server-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
# java -jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
