@echo off
::
:: Deploy servers part 1 to specified Kubernetes platform.
::

if "%1" == "gke" goto :deploy
if "%1" == "mkube" goto :deploy
echo ERROR: Unsupported Kubernetes platform
echo.
echo Usage: deploy-k8s-1-servers platform
echo        platform    gke or mkube
goto :EOF

:deploy
echo.
echo ### Some useful commands:
echo kubectl get pods
echo kubectl logs PODNAME -f
echo kubectl exec -it PODNAME -- mongo microcoffee

echo on
kubectl create -f microcoffeeoncloud-configserver/k8s-service-%1.yml
kubectl create -f microcoffeeoncloud-database/k8s-service-%1.yml
kubectl get services
kubectl get pods -w
