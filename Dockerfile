FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S server && adduser -S server -G server
USER server
VOLUME /tmp
EXPOSE 8080
ARG JAR_FILE="target/sayitassistant-2.0.jar"
ADD ${JAR_FILE} server.jar
ENTRYPOINT ["java", "-jar", "/server.jar"]