@echo off

setlocal

set ACTIVE_SPRING_PROFILES=devlocal

mvn spring-boot:run -Dspring-boot.run.profiles=%ACTIVE_SPRING_PROFILES%

endlocal
