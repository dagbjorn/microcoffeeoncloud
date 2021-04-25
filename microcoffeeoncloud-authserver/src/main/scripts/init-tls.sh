#!/bin/sh

set -x

cp /opt/microcoffee/certs/${MICROCOFFEE_CERT_DOMAIN}-cert.pem /etc/x509/https/tls.crt
cp /opt/microcoffee/certs/${MICROCOFFEE_CERT_DOMAIN}-key.pem /etc/x509/https/tls.key
