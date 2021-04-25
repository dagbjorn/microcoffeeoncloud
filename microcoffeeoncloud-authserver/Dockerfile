FROM jboss/keycloak:12.0.4

COPY --chown=jboss src/main/scripts/*.sh /opt/microcoffee/
COPY --chown=jboss target/keystore/certs/*.pem /opt/microcoffee/certs/

USER root

RUN chmod +x /opt/microcoffee/*.sh \
    && mkdir -p /etc/x509/https \
    && chown -R jboss /etc/x509

USER jboss
