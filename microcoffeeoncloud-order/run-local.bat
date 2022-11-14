@echo off

setlocal

set ACTIVE_SPRING_PROFILES=devlocal
set SPRING_CLOUD_CONFIG_URI=https://localhost:8454
set ORDER_CLIENT_SECRET=1vmdl2uVCkVn65ISRr5WjLXD7iVhmZCw

mvn spring-boot:run -Dspring-boot.run.profiles=%ACTIVE_SPRING_PROFILES%

endlocal
