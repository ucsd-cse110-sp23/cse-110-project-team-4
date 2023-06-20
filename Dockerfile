#
# Build Stage
#
FROM --platform=linux/x86_64 maven:3.9-eclipse-temurin-17-alpine AS build
COPY ./pom.xml ./pom.xml
RUN mvn dependency:go-offline -B
COPY ./src ./src
RUN mvn clean package -Dmaven.test.skip


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
COPY --from=build ./target/sayitassistant-2.0.jar /sayit-server/server.jar
ADD .env /sayit-server/.env
ENV spring_profiles_active=prod
ENTRYPOINT ["java", "-jar", "/sayit-server/server.jar"]