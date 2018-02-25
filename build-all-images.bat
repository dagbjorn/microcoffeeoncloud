call mvn clean package docker:build -f microcoffeeoncloud-configserver\pom.xml
call mvn clean package docker:build -f microcoffeeoncloud-database\pom.xml
call mvn clean package docker:build -f microcoffeeoncloud-discovery\pom.xml
call mvn clean package docker:build -f microcoffeeoncloud-gateway\pom.xml
call mvn clean package docker:build -f microcoffeeoncloud-creditrating\pom.xml
call mvn clean package docker:build -f microcoffeeoncloud-location\pom.xml
call mvn clean package docker:build -f microcoffeeoncloud-order\pom.xml
call mvn clean package docker:build -f microcoffeeoncloud-web\pom.xml
