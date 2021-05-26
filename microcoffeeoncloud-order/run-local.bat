@echo off

setlocal

set ACTIVE_SPRING_PROFILES=devlocal
set SPRING_CLOUD_CONFIG_URI=https://localhost:8454
set ORDER_CLIENT_SECRET=c7c9f908-47b5-40d3-b2ba-216176aa5613

mvn spring-boot:run -Dspring-boot.run.profiles=%ACTIVE_SPRING_PROFILES%

endlocal
