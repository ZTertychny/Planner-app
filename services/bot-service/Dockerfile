# ===== build =====
FROM eclipse-temurin:21-jdk AS build
WORKDIR /src
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew :services:bot-service:bootJar --no-daemon

# ===== runtime =====
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /src/services/bot-service/build/libs/*.jar app.jar
EXPOSE 8088
ENTRYPOINT ["java","-jar","app.jar"]