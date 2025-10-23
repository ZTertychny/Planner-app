FROM eclipse-temurin:21-jdk AS build
WORKDIR /src
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew :services:planner-service:bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /src/services/planner-service/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]