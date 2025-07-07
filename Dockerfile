FROM eclipse-temurin:21-jdk AS build
ARG SENTRY_AUTH_TOKEN
WORKDIR /app
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./
RUN chmod +x gradlew

RUN ./gradlew --no-daemon dependencies

COPY src src

RUN ./gradlew build -x check --no-daemon

FROM eclipse-temurin:21-jre
ARG SENTRY_AUTH_TOKEN
ENV SENTRY_AUTH_TOKEN=$SENTRY_AUTH_TOKEN
WORKDIR /app
ENV JAVA_OPTS="-Xmx256m -Xms128m -XX:+UseSerialGC -XX:MaxMetaspaceSize=128m"

# Download Sentry agent
RUN curl -o sentry-opentelemetry-agent.jar https://repo1.maven.org/maven2/io/sentry/sentry-opentelemetry-agent/8.16.0/sentry-opentelemetry-agent-8.16.0.jar

COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "SENTRY_AUTO_INIT=false java $JAVA_OPTS -javaagent:sentry-opentelemetry-agent.jar -jar app.jar"]
