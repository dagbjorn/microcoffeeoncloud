FROM eclipse-temurin:21.0.6_7-jre-alpine
VOLUME /tmp
WORKDIR /
ARG JAR_FILE
ENV JVM_TRUSTSTORE=/opt/java/openjdk/lib/security/cacerts
COPY target/$JAR_FILE app.jar
COPY target/keystore/microcoffee-keystore.jks .
RUN touch app.jar \
    && find / -iname "cacerts" \
    && keytool -importkeystore -noprompt -srckeystore microcoffee-keystore.jks -srcstorepass 12345678 -destkeystore $JVM_TRUSTSTORE -deststorepass changeit \
    && rm microcoffee-keystore.jks
ENTRYPOINT ["java","-jar","/app.jar"]
