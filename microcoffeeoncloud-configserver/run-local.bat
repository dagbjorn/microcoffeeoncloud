@echo off

setlocal

set SPRING_PROFILES_ACTIVE=devlocal

mvn spring-boot:run

endlocal
