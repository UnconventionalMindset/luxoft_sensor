FROM hseeberger/scala-sbt:17.0.2_1.6.2_3.1.1 as build

WORKDIR /app

COPY . .

RUN sbt assembly

# Use a smaller base image for the final image
FROM openjdk:17.0.2-slim

WORKDIR /app

# Copy the built application from the previous stage to the final image
COPY --from=build /app/target/scala-3.3.1/humiditySensorStatistics.jar .

RUN mkdir -p /directory

# Define the command to run when the container starts
CMD ["java", "-jar", "/app/humiditySensorStatistics.jar", "/directory"]