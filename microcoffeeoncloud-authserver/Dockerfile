FROM jboss/keycloak:15.0.2

COPY --chown=jboss src/main/scripts/*.sh /opt/microcoffee/scripts/
COPY --chown=jboss src/main/keycloak/*.json /opt/microcoffee/keycloak/
COPY --chown=jboss target/keystore/certs/*.pem /opt/microcoffee/certs/

USER root

RUN chmod +x /opt/microcoffee/scripts/*.sh \
    && mkdir -p /etc/x509/https \
    && chown -R jboss /etc/x509

USER jboss
