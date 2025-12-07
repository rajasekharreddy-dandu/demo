# 1. Stage: Use an official, lightweight Java 21 image
# We use 'slim' for a smaller final image size.
FROM openjdk:21-jdk-slim

# 2. Set environment variable for the application jar name
ARG JAR_FILE=target/identity-service.jar

# 3. Copy the packaged JAR file into the container
# This assumes you have already run 'mvn package' or 'mvn install'
COPY ${JAR_FILE} identity-service.jar

# 4. Expose the port the application runs on
# This is usually the server.port defined in your application.properties (8081)
EXPOSE 8081

# 5. Define the entry point to run the Spring Boot application
# 'java -jar app.jar' starts the executable Spring Boot JAR
ENTRYPOINT ["java", "-jar", "identity-service.jar"]