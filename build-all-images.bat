::
:: build-all-images [-Pbuild] [-Pbuild,push] [-Pitest]
::
call mvn clean install -f microcoffeeoncloud-authserver\pom.xml %* || goto :error
call mvn clean install -f microcoffeeoncloud-configserver\pom.xml %* || goto :error
call mvn clean install -f microcoffeeoncloud-database\pom.xml %* || goto :error
call mvn clean install -f microcoffeeoncloud-discovery\pom.xml %* || goto :error
call mvn clean install -f microcoffeeoncloud-gateway\pom.xml %* || goto :error
call mvn clean install -f microcoffeeoncloud-creditrating\pom.xml %* || goto :error
call mvn clean install -f microcoffeeoncloud-location\pom.xml %* || goto :error
call mvn clean install -f microcoffeeoncloud-order\pom.xml %* || goto :error
goto :EOF

:error
echo FAILED WITH ERROR #%errorlevel%.
exit /b %errorlevel%
