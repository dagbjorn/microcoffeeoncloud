FROM openjdk:8-slim
VOLUME /tmp
WORKDIR /
COPY target/microcoffeeoncloud-location-1.0.0-SNAPSHOT.jar app.jar
COPY target/keystore/microcoffee-keystore.jks .
RUN touch app.jar \
    && keytool -importkeystore -noprompt -srckeystore microcoffee-keystore.jks -srcstorepass 12345678 -destkeystore /etc/ssl/certs/java/cacerts -deststorepass changeit \
    && rm microcoffee-keystore.jks
ENTRYPOINT ["java","-jar","/app.jar"]
