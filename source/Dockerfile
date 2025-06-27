FROM openjdk:21-jdk-slim

WORKDIR /app

COPY did-issuer-server/gradlew .
COPY did-issuer-server/gradle gradle
COPY did-issuer-server/build.gradle .
COPY did-issuer-server/settings.gradle .
COPY did-issuer-server/libs libs
COPY did-issuer-admin /did-issuer-admin
COPY did-issuer-server/src src

RUN chmod +x ./gradlew

ENV GRADLE_OPTS="-Xmx2g -XX:MaxMetaspaceSize=512m"
RUN ./gradlew bootJar --no-daemon --max-workers=1

CMD ["java", "-jar", "/app/build/libs/did-issuer-server-2.0.0.jar"]