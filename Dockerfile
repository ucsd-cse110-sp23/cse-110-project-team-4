FROM --platform=linux/x86_64 eclipse-temurin:17-jre-alpine
RUN mkdir -p "/sayit-server"
RUN addgroup -S server && adduser -S server -G server
RUN chown server "/sayit-server"
USER server
WORKDIR "/sayit-server"
VOLUME "/tmp"
EXPOSE 8080
ARG JAR_FILE="target/sayitassistant-2.0.jar"
ADD ${JAR_FILE} "/sayit-server/server.jar"
ADD ".env" "/sayit-server/.env"
ENTRYPOINT ["java", "-jar", "/sayit-server/server.jar"]