@echo off
:: Usage: minikube-setenv

for /f "tokens=*" %%i in ('gsudo minikube docker-env') do %%i

set | grep DOCKER
