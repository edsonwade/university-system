#########################################
# Build Stage: Build the application using Maven
#########################################
FROM maven:3.8.1-openjdk-17 as builder

WORKDIR /app

# First, copy only the pom.xml to leverage Docker cache for dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the rest of the source code and build the application
COPY src src
RUN mvn package -DskipTests


#########################################
# Package Stage: Create the final container image
#########################################
FROM openjdk:17

# Set environment variables
ENV SERVER_PORT=8081

# Copy the built JAR file from the build stage to the new image
COPY --from=builder /app/target/*.jar /student-management.jar

# Expose the port that the application will run on
EXPOSE ${SERVER_PORT}

# Specify the command to run your application
ENTRYPOINT ["java", "-jar", "/student-management.jar"]

