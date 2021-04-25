#!/bin/sh

set -x

/opt/microcoffee/init-tls.sh
/opt/jboss/tools/docker-entrypoint.sh "$@"
