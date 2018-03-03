FROM mongo:3.6.3
WORKDIR /
COPY microcoffee.study.p12 .
RUN openssl pkcs12 -in microcoffee.study.p12 -password pass:12345678 -nodes -out microcoffee.study-key.pem \
    && rm microcoffee.study.p12
