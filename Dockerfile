FROM gradle:7.4.2-jdk17 AS cache-deps
WORKDIR /app
ENV GRADLE_USER_HOME /app/gradle

COPY *.gradle.kts gradle.properties /app/

RUN gradle shadowJar --parallel --console=verbose

FROM eclipse-temurin:17.0.3_7-jdk as build-jre

RUN $JAVA_HOME/bin/jlink \
    --add-modules java.base \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output /opt/jre

FROM gradle:7.4.2-jdk17 AS build-app
WORKDIR /app

COPY --from=cache-deps /app/gradle/ /home/gradle/.gradle/
COPY *.gradle.kts gradle.properties /app/
COPY src/main/ /app/src/main/

RUN gradle shadowJar --parallel --console=verbose

FROM debian:11.3-slim as runtime
WORKDIR /app
ENV JAVA_HOME=/opt/jre
ENV PATH="${JAVA_HOME}/bin:${PATH}"

RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get -y clean && \
    rm -rf /var/lib/apt/lists/*

COPY --from=build-jre /opt/jre $JAVA_HOME
COPY --from=build-app /app/build/libs/releasechime-all.jar /app/releasechime.jar

CMD ["java", "-jar", "/app/releasechime.jar"]
