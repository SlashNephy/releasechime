FROM gradle:8.1.1-jdk17@sha256:f7befd3501bae42a8e36ef45ce60a542bc7bbf91cde6195f0b4af98d9dcda0f6 AS cache-deps
WORKDIR /app
ENV GRADLE_USER_HOME /app/gradle

COPY *.gradle.kts gradle.properties /app/

RUN gradle shadowJar --parallel --console=verbose

FROM gradle:8.1.1-jdk17@sha256:f7befd3501bae42a8e36ef45ce60a542bc7bbf91cde6195f0b4af98d9dcda0f6 AS build-app
WORKDIR /app

COPY --from=cache-deps /app/gradle/ /home/gradle/.gradle/
COPY *.gradle.kts gradle.properties /app/
COPY src/main/ /app/src/main/

RUN gradle shadowJar --parallel --console=verbose

FROM amazoncorretto:18.0.2@sha256:1128cff77f7fb4512215a4ded2bf0a6ec3cd2bf0f414a72136b1bb1d5f6b0518 as runtime
WORKDIR /app

COPY --from=build-app /app/build/libs/releasechime-all.jar /app/releasechime.jar

CMD ["java", "-jar", "/app/releasechime.jar"]
