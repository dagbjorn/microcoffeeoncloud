@echo off

setlocal

D:
cd \bin\keycloak-13.0.1
bin\standalone.bat -Djboss.http.port=8093 -Djboss.https.port=8456

endlocal
