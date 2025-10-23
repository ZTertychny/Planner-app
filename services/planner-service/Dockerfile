FROM openjdk:21-jdk
WORKDIR /app
COPY /build/libs/* app.jar
EXPOSE 8088
ENTRYPOINT ["java", "-jar", "app.jar"]