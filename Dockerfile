FROM openjdk:17-jdk-slim
WORKDIR /app
COPY . .
RUN apt-get update && apt-get install -y tzdata
ENV TZ=Asia/Kolkata
RUN ./gradlew build
EXPOSE 8082
ENTRYPOINT ["java","-javaagent:agent/applicationinsights-agent-3.5.4.jar","-jar","/app/build/libs/UserManagementService-0.0.1-SNAPSHOT.jar"]