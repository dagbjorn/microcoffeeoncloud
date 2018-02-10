call mvn clean package docker:build eclipse:eclipse -f microcoffeeoncloud-configserver\pom.xml
call mvn clean package docker:build eclipse:eclipse -f microcoffeeoncloud-discovery\pom.xml
call mvn clean package docker:build eclipse:eclipse -f microcoffeeoncloud-gateway\pom.xml
call mvn clean package docker:build eclipse:eclipse -f microcoffeeoncloud-creditrating\pom.xml
call mvn clean package docker:build eclipse:eclipse -f microcoffeeoncloud-location\pom.xml
call mvn clean package docker:build eclipse:eclipse -f microcoffeeoncloud-order\pom.xml
call mvn clean package docker:build eclipse:eclipse -f microcoffeeoncloud-web\pom.xml
