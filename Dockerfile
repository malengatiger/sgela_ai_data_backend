# Use a base image with the necessary runtime environment
FROM openjdk:11-jre-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the application JAR file to the container
COPY target/scraper-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose the port on which your application listens
EXPOSE 8080

# Set the command to run your application
CMD ["java", "-jar", "app.jar"]
