@echo off

setlocal

docker-compose down
docker-compose up -d
docker-compose logs -f

endlocal
