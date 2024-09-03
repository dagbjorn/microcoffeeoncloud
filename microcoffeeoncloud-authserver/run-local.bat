@echo off

setlocal

set KEYCLOAK_HOME=D:\bin\keycloak-25.0.4

set KEYCLOAK_ADMIN=admin
set KEYCLOAK_ADMIN_PASSWORD=admin

copy target\keystore\localhost.p12 %KEYCLOAK_HOME%\.

md %KEYCLOAK_HOME%\data\import\
copy src\main\keycloak\microcoffee-realm.json %KEYCLOAK_HOME%\data\import\.

cd /d %KEYCLOAK_HOME%
bin\kc.bat start-dev --import-realm ^
                     --http-enabled=true --http-port=8093 ^
                     --https-port=8456 --https-key-store-file=localhost.p12 --https-key-store-password=12345678 ^
                     --http-management-port=8457 ^
                     --health-enabled=true --metrics-enabled=true

endlocal
