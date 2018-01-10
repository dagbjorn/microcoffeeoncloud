#FROM frolvlad/alpine-oraclejdk8:slim
FROM java:8
VOLUME /tmp
ADD microcoffeeoncloud-order-1.0.0-SNAPSHOT.jar app.jar
RUN sh -c 'touch /app.jar'
ENTRYPOINT ["java","-jar","/app.jar"]
