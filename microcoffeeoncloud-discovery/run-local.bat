@echo off

setlocal

set SPRING_PROFILES_ACTIVE=devlocal
set SPRING_CLOUD_CONFIG_URI=https://localhost:8454

mvn spring-boot:run

endlocal
