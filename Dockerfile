FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN apk add --no-cache maven && \
    mvn clean package -DskipTests -q && \
    # Remove source to reduce final image size
    rm -rf src

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Add health check user
RUN addgroup -S app && adduser -S app -G app

COPY --from=build /app/target/*.jar app.jar

# Set JVM options for container environment
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

EXPOSE 10010

USER app

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
