#FROM frolvlad/alpine-oraclejdk8:slim
FROM java:8
VOLUME /tmp
WORKDIR /
COPY microcoffeeoncloud-web-1.0.0-SNAPSHOT.jar app.jar
COPY microcoffee-keystore.jks .
RUN touch app.jar \
    && keytool -importkeystore -noprompt -srckeystore microcoffee-keystore.jks -srcstorepass 12345678 -destkeystore /etc/ssl/certs/java/cacerts -deststorepass changeit \
    && rm microcoffee-keystore.jks
ENTRYPOINT ["java","-jar","/app.jar"]
