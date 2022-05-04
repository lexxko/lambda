FROM openjdk:18-alpine3.15
VOLUME /tmp
ARG JAR_FILE=target/lambda-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080