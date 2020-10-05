@echo off
::
:: Deploy application to specified Kubernetes platform.
::

if "%1" == "gke" goto :deploy
if "%1" == "eks" goto :deploy
if "%1" == "aks" goto :deploy
if "%1" == "mkube" goto :deploy
echo ERROR: Unsupported Kubernetes platform
echo.
echo Usage: deploy-k8s-3-apps platform
echo        platform    gke, eks, aks or mkube
goto :EOF

:deploy
echo.
echo ### Some useful commands:
echo kubectl get pods
echo kubectl logs PODNAME -f

echo on
kubectl apply -f microcoffeeoncloud-gateway/k8s-service-%1.yml
kubectl apply -f microcoffeeoncloud-location/k8s-service-%1.yml
kubectl apply -f microcoffeeoncloud-order/k8s-service-%1.yml
kubectl apply -f microcoffeeoncloud-creditrating/k8s-service-%1.yml
kubectl get services
kubectl get pods -w
