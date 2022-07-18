FROM openjdk:8-alpine

COPY ./wait-for.sh /image-detection-api/wait-for.sh
RUN chmod 0755 /image-detection-api/wait-for.sh
COPY ./migrate_and_run.sh /image-detection-api/migrate_and_run.sh
RUN chmod 0755 /image-detection-api/migrate_and_run.sh

COPY target/uberjar/image-detection-api.jar /image-detection-api/app.jar

EXPOSE 3000

CMD "/image-detection-api/migrate_and_run.sh"
