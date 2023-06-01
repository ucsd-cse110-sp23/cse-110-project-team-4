#
# Build Stage
#
FROM --platform=linux/x86_64 maven:3.9-eclipse-temurin-17-alpine AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package -Dmaven.test.skip


#
# Package Stage
#
FROM --platform=linux/x86_64 eclipse-temurin:17-jre-alpine
RUN mkdir -p "/sayit-server"
RUN addgroup -S server && adduser -S server -G server
RUN chown server "/sayit-server"
USER server
WORKDIR "/sayit-server"
VOLUME "/tmp"
EXPOSE 8080
COPY --from=build /home/app/target/sayitassistant-2.0.jar /sayit-server/server.jar
ADD .env /sayit-server/.env
ENTRYPOINT ["java", "-jar", "/sayit-server/server.jar"]