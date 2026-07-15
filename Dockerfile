# build stage
FROM azul/zulu-openjdk:25-latest AS build
WORKDIR /build

# Cache dependencies
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw -B dependency:go-offline

# Build
COPY src/ src/
RUN ./mvnw -B clean package -DskipTests

# runtime stage
FROM azul/zulu-openjdk:25-jre-latest
WORKDIR /app

# Run as a non-root user
RUN useradd --system --uid 1001 appuser

COPY --from=build /build/target/VoteAPI-*.jar app.jar

USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
