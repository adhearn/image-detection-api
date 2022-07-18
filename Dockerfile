FROM openjdk:8-alpine

COPY target/uberjar/image-detection-api.jar /image-detection-api/app.jar

COPY ./migrate_and_run.sh /image-detection-api/migrate_and_run.sh

RUN chmod 0755 /image-detection-api/migrate_and_run.sh

EXPOSE 3000

CMD "/image-detection-api/migrate_and_run.sh"
