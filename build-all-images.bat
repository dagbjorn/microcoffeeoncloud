::
:: build-all-images [-Ppush-image]
::
call mvn clean package -f microcoffeeoncloud-configserver\pom.xml %*
call mvn clean package -f microcoffeeoncloud-database\pom.xml %*
call mvn clean package -f microcoffeeoncloud-discovery\pom.xml %*
call mvn clean package -f microcoffeeoncloud-gateway\pom.xml %*
call mvn clean package -f microcoffeeoncloud-creditrating\pom.xml %*
call mvn clean package -f microcoffeeoncloud-location\pom.xml %*
call mvn clean package -f microcoffeeoncloud-order\pom.xml %*
