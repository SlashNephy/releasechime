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

FROM amazoncorretto:21.0.5@sha256:a442b4beb07a8f4b20f66263bf620187d2e48a6ec882ce4d6a1ed63135c281d7 as runtime
WORKDIR /app

COPY --from=build-app /app/build/libs/releasechime-all.jar /app/releasechime.jar

CMD ["java", "-jar", "/app/releasechime.jar"]
