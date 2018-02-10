@echo off

setlocal

docker-compose -f docker-compose-gateway-only.yml down
docker-compose -f docker-compose-gateway-only.yml up -d
docker-compose -f docker-compose-gateway-only.yml logs -f

endlocal
