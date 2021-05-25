#!/bin/sh

set -x

/opt/microcoffee/scripts/init-tls.sh
/opt/jboss/tools/docker-entrypoint.sh "$@"
