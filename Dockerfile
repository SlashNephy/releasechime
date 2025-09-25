FROM gradle:8.3.0-jdk17@sha256:5f4ab273b15961c5f22969136ea884ca0343f1d8b2df5c4c6fe0ca8939b401b1 AS cache-deps
WORKDIR /app
ENV GRADLE_USER_HOME /app/gradle

COPY *.gradle.kts gradle.properties /app/

RUN gradle shadowJar --parallel --console=verbose

FROM gradle:8.3.0-jdk17@sha256:5f4ab273b15961c5f22969136ea884ca0343f1d8b2df5c4c6fe0ca8939b401b1 AS build-app
WORKDIR /app

COPY --from=cache-deps /app/gradle/ /home/gradle/.gradle/
COPY *.gradle.kts gradle.properties /app/
COPY src/main/ /app/src/main/

RUN gradle shadowJar --parallel --console=verbose

FROM amazoncorretto:21.0.8@sha256:759f912b96b3aab92dd071cdc9582fe71e527016020716f98ddd2dfd6d42b4fe as runtime
WORKDIR /app

COPY --from=build-app /app/build/libs/releasechime-all.jar /app/releasechime.jar

CMD ["java", "-jar", "/app/releasechime.jar"]
