@echo off
::
:: Deploy all servers and applications to specified Kubernetes platform.
::

if "%1" == "gke" goto :deploy
if "%1" == "eks" goto :deploy
if "%1" == "aks" goto :deploy
if "%1" == "mkube" goto :deploy
echo ERROR: Unsupported Kubernetes platform
echo.
echo Usage: deploy-k8s-all platform
echo        platform    gke, eks, aks or mkube
goto :EOF

:deploy
@echo.
@echo Step 1 of 7: Deploy and wait for Keycloak authorization server to start
kubectl apply -f microcoffeeoncloud-authserver/k8s-service-%1.yml
kubectl wait --for=condition=Ready pod -l app=authserver --timeout=300s

@echo.
@echo Step 2 of 7: Create Kubernetes secret
for /f "delims=" %%i in ('gcloud compute instances list --format json ^| jq -r ".[0].networkInterfaces[0].accessConfigs[0].natIP"') do set EXTERNAL_IP=%%i
for /f "delims=" %%i in ('curl -s -k -d "client_id=admin-cli" -d "username=admin" -d "password=admin" -d "grant_type=password" https://%EXTERNAL_IP%:30456/auth/realms/master/protocol/openid-connect/token ^| jq -r ".access_token"') do set ADMINTOKEN=%%i
for /f "delims=" %%i in ('curl -s -k -H "Authorization: Bearer %ADMINTOKEN%" https://%EXTERNAL_IP%:30456/auth/admin/realms/microcoffee/clients ^| jq -r ".[] | select(.clientId == \"order-service\") | .id"') do set ID=%%i
curl -i -k -X POST -H "Authorization: Bearer %ADMINTOKEN%" https://%EXTERNAL_IP%:30456/auth/admin/realms/microcoffee/clients/%ID%/client-secret
for /f "delims=" %%i in ('curl -s -k -H "Authorization: Bearer %ADMINTOKEN%" https://%EXTERNAL_IP%:30456/auth/admin/realms/microcoffee/clients/%ID%/client-secret ^| jq -r ".value"') do set CLIENT_SECRET=%%i
kubectl delete secret order-client-secret --ignore-not-found
kubectl create secret generic order-client-secret --from-literal=client-secret=%CLIENT_SECRET%

@echo.
@echo Step 3 of 7: Deploy and wait for config server and database to start
kubectl apply -f microcoffeeoncloud-configserver/k8s-service-%1.yml
kubectl apply -f microcoffeeoncloud-database/k8s-service-%1.yml
kubectl wait --for=condition=Ready pod -l "app in (configserver,mongodb)" --timeout=180s

@echo.
@echo Step 4 of 7:Load database
cd \home\study\java\dockerproject\microcoffeeoncloud
for /f "delims=" %%i in ('gcloud compute instances list --format json ^| jq -r ".[0].networkInterfaces[0].accessConfigs[0].natIP"') do set EXTERNAL_IP=%%i
call mvn -f microcoffeeoncloud-database\pom.xml gplus:execute -Ddbhost=%EXTERNAL_IP% -Ddbport=30017 -Ddbname=microcoffee -Dshopfile=microcoffeeoncloud-database\oslo-coffee-shops.xml

@echo.
@echo Step 5 of 7: Deploy and wait for discovery server to start
kubectl apply -f microcoffeeoncloud-discovery/k8s-service-%1.yml
kubectl wait --for=condition=Ready pod -l app=discovery --timeout=120s

@echo.
@echo Step 6 of 7: Deploy and wait for apps to start
:: Always redeploy order to make sure that it reads the latest Kubernetes secret
kubectl delete deployment order --ignore-not-found
kubectl wait --for=delete pod -l app=order --timeout=60s

kubectl apply -f microcoffeeoncloud-gateway/k8s-service-%1.yml
kubectl apply -f microcoffeeoncloud-location/k8s-service-%1.yml
kubectl apply -f microcoffeeoncloud-order/k8s-service-%1.yml
kubectl apply -f microcoffeeoncloud-creditrating/k8s-service-%1.yml
kubectl wait --for=condition=Ready pod -l "app in (gateway,location,order,creditrating)" --timeout=300s

@echo.
@echo Step 7 of 7: Check services and pods
kubectl get services
kubectl get pods
