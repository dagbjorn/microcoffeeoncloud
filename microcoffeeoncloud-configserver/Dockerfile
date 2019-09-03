FROM openjdk:11.0.2-slim
VOLUME /tmp
WORKDIR /
ARG JAR_FILE
COPY target/$JAR_FILE app.jar
COPY target/keystore/microcoffee-keystore.jks .
RUN touch app.jar \
    && find -iname "cacerts" \
    && keytool -importkeystore -noprompt -srckeystore microcoffee-keystore.jks -srcstorepass 12345678 -destkeystore /etc/ssl/certs/java/cacerts -deststorepass changeit \
    && rm microcoffee-keystore.jks
ENTRYPOINT ["java","-jar","/app.jar"]
