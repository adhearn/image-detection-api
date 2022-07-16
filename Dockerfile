FROM openjdk:8-alpine

COPY target/uberjar/image-detection-api.jar /image-detection-api/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/image-detection-api/app.jar"]
