@echo off
:: Usage: docker-setenv machine
:: Example: docker-setenv docker-vm

if "%1" == "" (
  echo Usage: docker-setenv machine
  goto ret
)

for /f "tokens=*" %%i in ('docker-machine env --shell cmd %1') do %%i

echo Connected shell to %1
set | grep DOCKER

:ret
