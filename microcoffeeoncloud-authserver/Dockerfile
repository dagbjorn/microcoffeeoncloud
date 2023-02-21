FROM quay.io/keycloak/keycloak:20.0.5

COPY --chown=keycloak src/main/keycloak/microcoffee-realm.json /opt/keycloak/data/import/
COPY --chown=keycloak target/keystore/*.p12 /opt/microcoffee/keystore/
