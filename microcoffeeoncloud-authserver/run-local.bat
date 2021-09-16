@echo off

setlocal

D:
cd \bin\keycloak-15.0.2
bin\standalone.bat -Djboss.http.port=8093 -Djboss.https.port=8456

endlocal
