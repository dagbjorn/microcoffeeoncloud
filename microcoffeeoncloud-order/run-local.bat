@echo off

setlocal

set ACTIVE_SPRING_PROFILES=devlocal
set SPRING_CLOUD_CONFIG_URI=https://localhost:8454
set ORDER_CLIENT_SECRET=cdfdf4e5-6ada-469c-bd56-f8847fa39b2e

mvn spring-boot:run -Dspring-boot.run.profiles=%ACTIVE_SPRING_PROFILES%

endlocal
