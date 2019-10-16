@echo off
::
:: Deploy servers part 1 to specified Kubernetes platform.
::

if "%1" == "gke" goto :deploy
if "%1" == "eks" goto :deploy
if "%1" == "mkube" goto :deploy
echo ERROR: Unsupported Kubernetes platform
echo.
echo Usage: deploy-k8s-1-servers platform
echo        platform    gke, eks or mkube
goto :EOF

:deploy
echo.
echo ### Some useful commands:
echo kubectl get pods
echo kubectl logs PODNAME -f
echo kubectl exec -it PODNAME -- mongo microcoffee

echo on
kubectl apply -f microcoffeeoncloud-configserver/k8s-service-%1.yml
kubectl apply -f microcoffeeoncloud-database/k8s-service-%1.yml
kubectl get services
kubectl get pods -w
