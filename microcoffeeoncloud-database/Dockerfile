FROM mongo:8.2.3
WORKDIR /
COPY target/keystore/*.p12 ./
RUN openssl version \
    && openssl pkcs12 -in microcoffee.study.p12 -password pass:12345678 -nodes -out microcoffee.study-key.pem \
    && openssl pkcs12 -in wildcard.default.p12 -password pass:12345678 -nodes -out wildcard.default-key.pem \
    && chmod go+r *.pem \
    && rm microcoffee.study.p12 \
    && rm wildcard.default.p12
