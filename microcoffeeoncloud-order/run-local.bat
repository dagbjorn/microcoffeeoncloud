@echo off

setlocal

set ACTIVE_SPRING_PROFILES=devlocal
set SPRING_CLOUD_CONFIG_URI=https://localhost:8454
set ORDER_CLIENT_SECRET=x6UeVnSOeu7wB8ocM5SNEJPEyjA924Vi

mvn spring-boot:run -Dspring-boot.run.profiles=%ACTIVE_SPRING_PROFILES%

endlocal
