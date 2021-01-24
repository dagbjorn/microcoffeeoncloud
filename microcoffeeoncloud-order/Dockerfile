FROM openjdk:11.0.10-slim
VOLUME /tmp
WORKDIR /
ARG JAR_FILE
COPY target/$JAR_FILE app.jar
COPY target/keystore/microcoffee-keystore.jks .
# openjdk:11.0.3-slim and newer no longer contain /etc/ssl/certs/java/cacerts. Falling back to /usr/local/openjdk-11/lib/security/cacerts.
RUN touch app.jar \
    && keytool -importkeystore -noprompt -srckeystore microcoffee-keystore.jks -srcstorepass 12345678 -destkeystore /usr/local/openjdk-11/lib/security/cacerts -deststorepass changeit \
    && rm microcoffee-keystore.jks
ENTRYPOINT ["java","-jar","/app.jar"]

