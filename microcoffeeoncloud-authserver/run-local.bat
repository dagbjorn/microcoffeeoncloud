@echo off

setlocal

D:
cd \bin\keycloak-12.0.4
bin\standalone.bat -Djboss.http.port=8093 -Djboss.https.port=8456

endlocal
