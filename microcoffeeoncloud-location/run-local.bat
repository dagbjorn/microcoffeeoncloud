@echo off

setlocal

set ACTIVE_SPRING_PROFILES=devlocal
set SPRING_CLOUD_CONFIG_URI=https://localhost:8454

mvn spring-boot:run -Dspring-boot.run.profiles=%ACTIVE_SPRING_PROFILES%

endlocal
