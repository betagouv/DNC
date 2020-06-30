FROM openjdk:12-jdk-alpine

RUN addgroup -S spring && adduser -S spring -G spring && mkdir /app && chown spring:spring -R /app
USER spring:spring

ARG JAR_FILE=target/*.jar
COPY --chown=spring:spring ${JAR_FILE} /app/app.jar
WORKDIR /app

ENV PORT 8084
EXPOSE $PORT

ENTRYPOINT ["java","-jar","app.jar"]