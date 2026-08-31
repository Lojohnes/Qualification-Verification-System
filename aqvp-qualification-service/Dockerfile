FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /workspace
COPY . .
RUN mvn -B -DskipTests package -pl aqvp-qualification-service -am

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /workspace/aqvp-qualification-service/target/*.jar app.jar
ENTRYPOINT ["sh", "-c", "exec java -Dserver.port=${PORT:-8080} -jar app.jar"]
