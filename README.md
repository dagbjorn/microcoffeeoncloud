# microcoffeeoncloud - The &micro;Coffee Shop powered by ![Spring Boot 2.0](https://raw.githubusercontent.com/dagbjorn/microcoffeeoncloud/master/docs/images/spring-boot-2.0.png "Spring Boot 2.0") and ![Docker](https://raw.githubusercontent.com/dagbjorn/microcoffeeoncloud/master/docs/images/docker-horizontal.png "Docker")

## Change log

Date | Change
---- | -------
10.03.2018 | Finalised markdown file.
08.09.2018 | Upgraded to Docker 18.06.1-ce.
30.10.2018 | Added extra on how to run Microcoffee on Google Kubernetes Engine (GKE).
12.11.2018 | Updated API URLs with leading /api.
30.01.2019 | Added Swagger URL to API doc.
24.02.2019 | Added extra on load testing with Gatling.
28.02.2019 | Added extra on how to run Microcoffee on Minikube.
28.03.2019 | Migrated to Java 11.
03.04.2019 | Upgraded to Docker 18.09.3. Stated recommended versions of minikube and kubectl.
24.10.2019 | Added extra on how to run Microcoffee on Amazon Elastic Kubernetes Service (EKS).
11.12.2019 | Updated GKE to use NodePort 30017 instead of ClusterIP for database.
10.09.2020 | Updated Swagger URL after Springfox 3.0.0 upgrade.
15.10.2020 | Added extra on how to run Microcoffee on Microsoft Azure Kubernetes Service (AKS).
30.10.2020 | Added AmazonSSMReadOnlyAccess to Amazon EKS Minimum policy configuration.
30.11.2020 | Migrated from Spring Cloud Netflix Zuul to Spring Cloud Gateway. Support of Zuul is discontinued in Spring Cloud 2020.
30.11.2020 | HTTP is no longer supported by API gateway after migration to Spring Cloud Gateway. (Netty, which is used by Spring Cloud Gateway under the hood, does not support multiple ports.)
11.12.2020 | Migrated from Spring Cloud Netflix Hystrix to Resilience4J. Support of Hystrix is discontinued in Spring Cloud 2020.
23.12.2020 | Upgraded to Spring Boot 2.4.1 and Spring Cloud 2020.0.0.
05.01.2021 | Added support for Spring WebClient since RestTemplate is in maintenance mode. However, still using RestTemplate as an alternative.
19.05.2021 | Integrated Keycloak, an authorization server, in Microcoffee. CreditRating API is now requiring the OAuth2 client credentials grant.
30.05.2021 | Upgraded to Spring Boot 2.5.0 and Spring Cloud 2020.0.3.
23.11.2021 | Added extra on required setup for GitHub Actions workflows.
09.12.2021 | Upgraded to Spring Boot 2.6.1 and Spring Cloud 2021.0.0.
27.12.2021 | Migrated from Springfox to SpringDoc. Springfox appears to be a more or less dead project.
11.02.2022 | Extended GitHub Action build workflow with Sonar analysis using SonarCloud.
10.05.2022 | Added extra on WSL installation.
18.11.2022 | Replaced Docker Toolbox with Docker Desktop for local building and testing. Updated Minikube doc. General brush-up of outdated info.
28.11.2022 | Updated section on API testing with Gatling. Migrated from Scala IDE to IntelliJ.
28.12.2022 | Upgraded to Spring Boot 3.0.1 and Spring Cloud 2022.0.0.
26.03.2023 | Upgraded to Spring Boot 3.0.5. Microcoffee now requires OpenSSL 3.x to generate the self-signed certificates.
11.05.2023 | Developed new frontend in ReactJS. (Old AngularJS frontend is still available.)
18.05.2023 | Added screendumps of Microcoffee GUI in doc page.
23.05.2023 | Upgraded to Spring Boot 3.1.0.
16.10.2023 | Implemented CSRF protection in the Order API (POST operation).
09.12.2023 | Upgraded to Spring Boot 3.2.0 and Spring Cloud 2023.0.0.
10.12.2023 | Upgraded to Java 21.
03.01.2024 | Added roles/servicedirectory.editor in Setup for GitHub Actions workflows.
02.06.2024 | Upgraded to Spring Boot 3.3.0.
03.09.2024 | Added port 8457/30457 used by Keycloak management interface for Kubernetes readiness/liveness probes.
03.12.2024 | Upgraded to Spring Boot 3.4.0 and Spring Cloud 2024.0.0.
01.06.2025 | Upgraded to Spring Boot 3.5.0 and Spring Cloud 2025.0.0.

## Contents

* [Acknowledgements](#acknowledgements)
* [The application](#application)
* [Prerequisite](#prerequisite)
* [Start Docker VM](#start-docker-vm)
* [Building Microcoffee](#building-microcoffee)
* [Application configuration](#configuration)
* [Setting up the database](#setting-up-database)
* [Setting up the authorization server](#setting-up-authserver)
* [Run Microcoffee](#run-microcoffee)
* [Give Microcoffee a spin](#give-a-spin)
* [REST API](#rest-api)
* [Spring Cloud Netflix](#spring-cloud-netflix)
* [Resilience4J](#resilience4j)
* [Extras](#extras)
  - [Download geodata from OpenStreetMap](#download-geodata)
  - [Microcoffee on Google Kubernetes Engine (GKE)](#microcoffee-on-gke)
  - [Microcoffee on Amazon Elastic Kubernetes Service (EKS)](#microcoffee-on-eks)
  - [Microcoffee on Microsoft Azure Kubernetes Service (AKS)](#microcoffee-on-aks)
  - [Microcoffee on Minikube](#microcoffee-on-minikube)
  - [API load testing with Gatling](#api-load-testing-gatling)
  - [Keycloak - configuration examples](#keycloak-config)
  - [Setup for GitHub Actions workflows](#github-actions-setup)
  - [Building Microcoffee on WSL](#building-microcoffee-on-wsl)
    - [Installing WSL](#installing-wsl)
    - [Installing Docker on WSL](#installing-docker-on-wsl)

## <a name="acknowledgements"></a>Acknowledgements
The &micro;Coffee Shop application is based on the coffee shop application coded live by Trisha Gee in her fabulous talk, "HTML5, Angular.js, Groovy, Java, MongoDB all together - what could possibly go wrong?", given at QCon London 2014. A few differences should be noted however; Microcoffee uses a microservice architecture, runs on Docker and is developed in Spring Boot instead of Dropwizard as in Trisha's version. Also, the frontend is rewritten in ReactJS.

## <a name="application"></a>The application
The application has a simple user interface written in ReactJS and uses REST calls to access the backend services. After loading the coffee shop menu from the backend, your favorite coffee drink may be ordered. The user may also locate the nearest coffee shop and show it on Google Maps.

Screenshots from the Microcoffee GUI:

![Microcoffee GUI 1(4)](https://raw.githubusercontent.com/dagbjorn/microcoffeeoncloud/master/docs/images/microcoffee-frontend-1.png  "Microcoffee order page") | ![Microcoffee GUI 2(4)](https://raw.githubusercontent.com/dagbjorn/microcoffeeoncloud/master/docs/images/microcoffee-frontend-2.png "Ordering a coffee")
--- | ---
![Microcoffee GUI 3(4)](https://raw.githubusercontent.com/dagbjorn/microcoffeeoncloud/master/docs/images/microcoffee-frontend-3.png  "Order sent & details link") | ![Microcoffee GUI 4(4)](https://raw.githubusercontent.com/dagbjorn/microcoffeeoncloud/master/docs/images/microcoffee-frontend-4.png "Coffee shop location on Google Maps")

### The microservices
The figure shows the microservice architecture of the application. Spring Boot, Spring Cloud and MongoDB are the key technologies in the backend. ReactJS and Bootstrap in the frontend. Spring Security OAuth and Keycloak are used for securing one of the backend APIs.

![Microcoffee architecture](https://raw.githubusercontent.com/dagbjorn/microcoffeeoncloud/master/docs/images/microcoffee-architecture-202305.png "Microcoffee architecture")

The application is made up by the following microservices, each running in its own Docker container:

* The Config Server for externalized configuration in a GIT backend.
* The Discovery server for service discovery with Eureka.
* The API Gateway for proxying of calls to the backend REST services. Static web resources are also served by the API gateway.
* The backend REST API provided as three different microservices.
* The Keycloak authorization server for securing the APIs. (So far only the CreditRating API is secured.)
* The MongoDB database.

Each microservice, apart from the authorization server and the database, is implemented by a Spring Boot application.

The application supports both http and https on all communication channels (apart from API Gateway which only supports https). However, https is a requirement in most browsers to get the HTML Geolocation API going, so https is needed to unlock all available functions in Microcoffee.

#### microcoffeeoncloud-configserver
Contains the configuration server for serving externalized configuration to the application. The configuration server is based on the Spring Cloud Config Server.

The configuration is located in property/YAML files in a GIT repo. For simplicity, a GIT repo on GitHub is used. Upon startup, each microservice reads its own configuration using the Spring Cloud Config Client.

:warning: Using a public repo on GitHub is not recommended because the configuration usually contains sensitive data like passwords and details of the internal network. However, Microcoffee is just a study application and contains no secrets.

#### microcoffeeoncloud-discovery
Contains the discovery server for service discovery. The discovery server is based on Spring Cloud Netflix Eureka.

Upon startup, each microservice registers with the discovery server using an Eureka client.

#### microcoffeeoncloud-gateway
Contains the gateway server for proxying of REST calls to the backend services. The gateway server is based on Spring Cloud Gateway.

Spring Cloud Gateway acts as a reverse proxy for REST calls from the user interface of Microcoffee, hence the user interface can use a single point to access all REST services as well as the static web resources of the application. This simplifies things, avoiding the need to manage REST service endpoints and CORS concerns independently for all the backends.

#### microcoffeeoncloud-location
Contains the Location REST service for locating the nearest coffee shop. Coffee shop geodata is downloaded from [OpenStreetMap](https://www.openstreetmap.org) and imported into the database.

:bulb: The `microcoffeeoncloud-database` project contains a geodata file, `oslo-coffee-shops.xml`, with all Oslo coffee shops currently registered on OpenStreetMap. See [Download geodata from OpenStreetMap](#download-geodata) for how this file is created.

#### microcoffeeoncloud-order
Contains the Menu and Order REST services. Provides APIs for reading the coffee menu and placing coffee orders.

Order uses the CreditRating REST service as a backend service for checking if a customer is creditworthy when placing an order. CreditRating is an unreliable service, hence giving us an "excuse" to use Resilience4J for retrying service calls that fail.

Order is an OAuth2 client and uses the [client credentials flow](https://auth0.com/docs/flows/client-credentials-flow) to access CreditRating. The API implements CSRF (Cross-Site Request Forgery) protection on the POST operation.

#### microcoffeeoncloud-creditrating
Contains an extremely simple credit rating service. Provides an API for reading the credit rating of a customer. Used by the Order service.

Mainly introduced to act as an unreliable backend service. The actual behavior may be configured by configuration properties. Current options include stable, failing, slow and unstable behaviors. See the [Resilience4J section](#resilience4j) below for details.

CreditRating is an OAuth2 resource server. It requires a Bearer JWT access token with proper scope and audience claim values for access.

#### microcoffeeoncloud-authserver
Contains the Keycloak authorization server. The authorization server image is based on the official [quay.io/keycloak/keycloak](https://quay.io/repository/keycloak/keycloak) image on Redhat quay.io.

A microcoffee realm is automatically imported when starting Keycloak. The realm contains the necessary configuration for securing the
CreditRating API with an OAuth2 client credentials grant.

#### microcoffeeoncloud-database
Contains the MongoDB database. The database image is based on the official [mongo](https://hub.docker.com/r/_/mongo/) image on DockerHub.

The database installation uses a Docker volume, *mongodbdata*, for data storage. This volume needs to be created before starting the container.

:warning: The database runs without any security enabled.

### Common artifacts
The application also contains common artifacts (for the time being only one) which are used by more than one microservice. Each artifact is built by its own Maven project.

A word of warning: Common artifacts should be used wisely in a microservice architecture.

#### microcoffeeoncloud-certificates
Creates a self-signed PKI certificate, contained in the Java keystore `microcoffee-keystore.jks`, needed by the application to run https. As a matter of fact, three certificates are created:

* A wildcard certificate with common name (CN) `*.microcoffee.study` for use when running the application on Docker.
* A wildcard certificate with common name (CN) `*.default` for use when running the application on Kubernetes. (See Extras)
* A certificate with common name (CN) `localhost` for use when testing outside Docker.

:bulb: The application creates four user-defined bridge networks for networking; one for the each of the config, discovery and authorization servers, and finally one for the rest of the microservices.

#### microcoffeeoncloud-jwttest
A test library with support for creating signed JWT access tokens. The tokens are signed using the self-signed PKI certificate provided
by `microcoffeeoncloud-certificates`. Also a suite of types to use when mocking an OIDC provider's metadata and JWKS API is
provided.

## <a name="prerequisite"></a>Prerequisite
Microcoffee was originally developed on Windows 10 and tested on Docker running on Oracle VirtualBox. Late 2022, the development environment was modernized and consists now of Windows 11 Pro with Docker Desktop running with WSL2 based engine.

The code was originally written in Java 8, but was later migrated to Java 11. In early 2022, Microcoffee moved on to Java 17. Then in december 2023, Microcoffee moved to Java 21.

For building and testing the application on Windows, Docker Desktop with the WSL 2 engine is recommended. See [Install on Docker Desktop on Windows](https://docs.docker.com/desktop/install/windows-install/#install-docker-desktop-on-windows) for installation guidelines.

:bulb: Some experiences from the trenches.
* Always install WSL 2 before Docker Desktop. This installation order reduces the possibilities for issues.
* If Docker Desktop at some later time fails to start (or stop), try to unregister docker-desktop from WSL before trying to start Docker Desktop again.

        wsl --list -v
        wsl --unregister docker-desktop
        wsl --unregister docker-desktop-data

In addition, you'll need the basic Java development tools (IDE w/ Java 21 and Maven) as well as Groovy 5 installed on your
development machine.

You will also need OpenSSL 3.x to create a self-signed wildcard certificate. Finally, [curl](https://curl.se/) and [jq](https://stedolan.github.io/jq/) are needed.

:warning: Java keytool won't work because it doesn't support wildcard host names as SAN (Subject Alternative Name) values.

## <a name="start-docker-vm"></a>Start Docker Desktop
Before moving on and start building Microcoffee, we need Docker Desktop running. But first, make sure that "Expose daemon on tcp://localhost:2375 without TLS" is checked in Docker Settings > General. This is required for the Maven building to work.

To verify that Docker is running, run:

    docker version

If both client and server information is listed, you are good to go.

## <a name="building-microcoffee"></a>Building Microcoffee

### Get the code from GitHub
Clone the microcoffee project on GitHub, including the appconfig project, or download the projects as zip files.

* https://github.com/dagbjorn/microcoffeeoncloud.git
* https://github.com/dagbjorn/microcoffeeoncloud-appconfig.git

### Build common artifacts

#### Create the certificate artifact
In order for https to work, a self-signed certificate needs to be created. The `microcoffeeoncloud-certificates` project builds a jar containing a Java keystore, `microcoffee-keystore.jks`, with the following certificates:

Key alias | Common Name (CN) | Subject Alternative Name (SAN) | Comment
--------- | ---------------- | ------------------------------ | --------
microcoffee.study | \*.microcoffee.study | \*.microcoffee.study, host.docker.internal, ${vmHostIp} | Wildcard certificate used when running with Docker.
wildcard.default | \*.default | \*.default | Wildcard certificate used when running on Kubernetes.
localhost | localhost | | Certificate used when testing outside Docker.

`host.docker.internal` is the hostname of the development machine added by Docker Desktop in the local hosts file.

`${vmHostIp}` is a Maven property defining the IP address of a VM host. Historically, this has been used with Oracle VirtualBox. With Docker Desktop it is not required. Default value is `192.168.99.100`.

To generate new certificates, run:

    mvn clean install -Pgen-certs

To inspect the created keystore, run:

    keytool -list -v -keystore target\classes\microcoffee-keystore.jks -storepass 12345678

To specify a different VM host IP, run:

    mvn clean install -Pgen-certs -DvmHostIp=10.0.0.100

:bulb: The keystore properties are specified in `${application}-${profile}.properties` of each microservice that is using the `microcoffeeoncloud-certificates` artifact.

### Build the microservices
Use Maven to build each microservice in turn by running:

    mvn clean install [-Pbuild,push]

Specify the `build` and `push` profiles to build and push, respectively, the Docker image to Docker Hub.

Or take advantage of the aggregator pom in the top-level folder and build all microservices in one go.

:exclamation: Just remember that Docker must be running for building the Docker images successfully.

## <a name="configuration"></a>Application configuration
Application and environment-specific properties are defined in standard Spring manner by `${application}-${profile}.properties` files in the `microcoffeeoncloud-appconfig` project. Supported profiles are:

* devdocker: Run Microcoffee on Docker.
* devlocal: Run Microcoffee without Docker.

In addition, the gateway routing configuration is defined in `gateway.yml`.

Configuration is served by the configuration server. The URL of the configuration server itself is defined by an environment variable in each microservice project depending on the current profile as follows:

* devdocker: In `docker-compose.yml`.
* devlocal: In `run-local.bat`.

The port numbers are:

Microservice | http port | https port | https mgmt port | Note
------------ | --------- | ---------- | --------------- | ----
gateway | - | 8443 | | gateway is based on Spring Cloud Gateway running Netty under the hood. Netty only supports a single port in one and the same application, hence gateway has no port to spare for http.
location | 8081 | 8444 | |
order | 8082 | 8445 | |
creditrating | 8083 | 8446 | |
configserver | 8091 | 8454 | |
discovery | 8092 | 8455 | |
authserver | 8092 | 8456 | 8457 |
database | 27017 | 27017 | |

## <a name="setting-up-database"></a>Setting up the database

### Create a Docker volume for the MongoDB database
Create a Docker volume named *mongodbdata* to be used by the MongoDB database.

    docker volume create --name mongodbdata

Verify by:

    docker volume inspect mongodbdata

### Load data into the database collections
The `microcoffeeoncloud-database` project is used to load coffee shop locations, `oslo-coffee-shops.xml`, and menu data into a database called  *microcoffee*. This is accomplished by running the below Maven command.

But first, we need to start MongoDB (from `microcoffeeoncloud-database`):

    run-docker.bat

Then run:

    mvn gplus:execute -Ddbhost=localhost -Ddbport=27017 -Ddbname=microcoffee -Dshopfile=oslo-coffee-shops.xml

To verify the database loading, start the MongoDB client in a Docker container. (Use `docker ps` to find the container ID or name.)

    docker exec -it microcoffeeoncloud-database-mongodb-1 mongosh microcoffee

    microcoffee> show databases
    admin         40.00 KiB
    config        12.00 KiB
    local         40.00 KiB
    microcoffee  188.00 KiB
    microcoffee> show collections
    coffeeshop
    drinkoptions
    drinksizes
    drinktypes
    microcoffee> db.coffeeshop.db.coffeeshop.countDocuments()
    94
    microcoffee> db.coffeeshop.findOne()
    {
      _id: ObjectId("63752874dd36ed3fb1c1f378"),
      openStreetMapId: '292135703',
      location: { coordinates: [ 10.7587531, 59.9234799 ], type: 'Point' },
      'addr:city': 'Oslo',
      'addr:country': 'NO',
      'addr:housenumber': '55',
      'addr:postcode': '0555',
      'addr:street': 'Thorvald Meyers gate',
      amenity: 'cafe',
      cuisine: 'coffee_shop',
      name: 'Kaffebrenneriet',
      opening_hours: 'Mo-Fr 07:00-19:00; Sa-Su 09:00-17:00',
      operator: 'Kaffebrenneriet',
      phone: '+47 95262675',
      website: 'http://www.kaffebrenneriet.no/butikkene/butikkside/kaffebrenneriet_thorvald_meyersgate_55/',
      wheelchair: 'no'
    }
    microcoffee> exit

Finally, stop MongoDB:

    docker-compose down

## <a name="setting-up-authserver"></a>Setting up the authorization server

### Start Keycloak
Start by getting Keyclock up and running (from `microcoffeeoncloud-authserver`):

    run-docker.bat

### Regenerate client secret of order-service
All required configuration of Keycloak is automatically imported when the Docker container starts. However, the client secret of the OAuth2 client `order-service` must be regenerated each time Keycloak is restarted. This can be done in two ways, either manually from the administration console, or by using the Keycloak Admin REST API.

Additionally, the client secret must be stored as a key-value pair in a file called `order_client_secret.env`. This file is specified by the *env_file* attribute in `docker-compose.yml` and should be located in the same directory.

Contents of `order_client_secret.env`:

    ORDER_CLIENT_SECRET=<secret>

#### Using the Admin Console
Open https://localhost:8456/admin/ in a browser.

1. Log in as user `admin` and password `admin`.
1. Switch realm from `master` to `microcoffee`.
1. Navigate to Clients > order-service > Credentials and regenerate Client secret. Click Yes to confirm regenerating the client secret.
1. Copy the Secret value.
1. In `microcoffeeoncloud-order`, create `order_client_secret.env` as described above.
1. Also copy `order_client_secret.env` to the `microcoffeeoncloud-gateway` folder.

#### Using the Admin REST API
Run the following commands (Windows) from the top-level folder of Microcoffee:

    set AUTHSERVER=localhost:8456

    :: Get an admin token (only valid for 60s secs)
    for /f "delims=" %I in ('curl -s -k -d "client_id=admin-cli" -d "username=admin" -d "password=admin" -d "grant_type=password" https://%AUTHSERVER%/realms/master/protocol/openid-connect/token ^| jq -r ".access_token"') do set ADMINTOKEN=%I

    :: Get the id value of the order-service client to use in the resource URL for regenerating the client secret
    for /f "delims=" %I in ('curl -s -k -H "Authorization: Bearer %ADMINTOKEN%" https://%AUTHSERVER%/admin/realms/microcoffee/clients ^| jq -r ".[] | select(.clientId == \"order-service\") | .id"') do set ID=%I

    :: Regenerate the client secret
    curl -i -k -X POST -H "Authorization: Bearer %ADMINTOKEN%" https://%AUTHSERVER%/admin/realms/microcoffee/clients/%ID%/client-secret

    :: Read the client secret and save it in order_client_secret.env
    for /f "delims=" %I in ('curl -s -k -H "Authorization: Bearer %ADMINTOKEN%" https://%AUTHSERVER%/admin/realms/microcoffee/clients/%ID%/client-secret ^| jq -r ".value"') do set CLIENT_SECRET=%I
    echo ORDER_CLIENT_SECRET=%CLIENT_SECRET% > microcoffeeoncloud-order\order_client_secret.env
    echo ORDER_CLIENT_SECRET=%CLIENT_SECRET% > microcoffeeoncloud-gateway\order_client_secret.env

Verify the contents of `order_client_secret.env` and we are good to go.

## <a name="run-microcoffee"></a>Run Microcoffee
When running microcoffee, you need to observe the dependencies between the microservices so they are started in the correct order.

1. Start with the authorization server (Keycloak).
1. Start the config server. This may be done in parallel with the authorization server.
1. When the config server is running, start the discovery server.
1. When all three servers are running, start the other microservices, including the database, all together.

A microservice is started by running `docker-compose up -d` or by using the already mentioned convenience batch file `run-docker.bat` as shown below. The batch file will stop any running containers before bringing them up again.

:warning: Depending on the machine, the startup time can be rather long.

Given that the authorization server is already running and the client secret is regenerated, start the config server from the `microcoffeeoncloud-configserver`:

    run-docker.bat

Then, move on to the `microcoffeeoncloud-discovery` folder and start the discovery server.

Finally, move on to the `microcoffeeoncloud-gateway` folder and start the remaining microservices (including the database).

For testing individual projects outside Docker, run:

    run-local.bat

:point_right: Check the contents of each batch file to see what it does. For instance, for starting the database and the authorization server, MongoDB and Keycloak must be installed locally.

## <a name="give-a-spin"></a>Give Microcoffee a spin
After Microcoffee has started, navigate to the coffee shop to place your first coffee order:

    https://localhost:8443

:warning: Because of the self-signed certificate, a security-aware browser will complain a bit.
* Firefox: Click Advanced and then "Accept the Risk and Continue".
* Chrome: Click Advanced and hit the "Proceed to localhost (unsafe)" link.
* Opera: Click "Help me understand" and hit the "Proceed to localhost (unsafe)" link.
* Edge: Click Advanced and hit the "Continue to localhost (unsafe)" link.

:bulb: Old AngularJS frontend is available on `https://localhost:8443/coffee.html`.

## <a name="rest-api"></a>REST API

### APIs

* [Location API](#location-api)
* [Menu API](#menu-api)
* [Order API](#order-api)
* [CreditRating API](#creditrating-api)

### <a name="swagger-doc"></a>Swagger API documentation

Centralized browsing of API documentation is available at the following URL:

    https://localhost:8443/swagger-ui.html

Select the spec of interest in the upper right corner.

### <a name="location-api"></a>Location API

#### Get nearest coffee shop

**Syntax**

    GET /api/coffeeshop/nearest/{latitude}/{longitude}/{maxdistance}

Finds the nearest coffee shop within *maxdistance* meters from the position given by the WGS84 *latitude*/*longitude* coordinates.

**Response**

HTTP status | Description
----------- | -----------
200 | Coffee shop found. The name, location etc. is returned in JSON-formatted HTTP response body.
204 | No coffee shop found within specified distance from given position.

**Example**

Find the coffee shop closest to the Capgemini Skøyen office:

    GET http://localhost:8081/api/coffeeshop/nearest/59.920161/10.683517/200

Response:

    {
      "_id": {
        "$oid": "63752874dd36ed3fb1c1f37b"
      },
      "openStreetMapId": "428063059",
      "location": {
        "coordinates": [
          10.6834023,
          59.920229
        ],
        "type": "Point"
      },
      "addr:city": "Oslo",
      "addr:country": "NO",
      "addr:housenumber": "22",
      "addr:postcode": "0278",
      "addr:street": "Karenslyst Allé",
      "amenity": "cafe",
      "cuisine": "coffee_shop",
      "name": "Kaffebrenneriet",
      "opening_hours": "Mo-Fr 07:00-18:00; Sa-Su 09:00-17:00",
      "operator": "Kaffebrenneriet",
      "phone": "+47 22561324",
      "website": "http://www.kaffebrenneriet.no/butikkene/butikkside/kaffebrenneriet_karenslyst_alle_22/",
      "wheelchair": "yes"
    }

**Testing with curl**

    curl -i http://localhost:8081/api/coffeeshop/nearest/59.920161/10.683517/200
    curl -i --insecure https://localhost:8444/api/coffeeshop/nearest/59.920161/10.683517/200

:bulb: For testing with https, use a recent curl version that supports SSL. (7.46.0 is good.)

### <a name="menu-api"></a>Menu API

#### Get menu

**Syntax**

    GET /api/coffeeshop/menu

Gets the coffee shop menu.

**Response**

HTTP status | Description
----------- | -----------
200 | Menu returned in JSON-formatted HTTP response body.

**Example**

    GET http://localhost:8082/api/coffeeshop/menu

Response (abbreviated):

    {
      "types": [
        {
          "_id": {
            "timestamp": 1668622453,
            "date": "2022-11-16T18:14:13.000+00:00"
          },
          "name": "Americano",
          "family": "Coffee"
        },
        ..
      ],
      "sizes": [
        {
          "_id": {
            "timestamp": 1668622453,
            "date": "2022-11-16T18:14:13.000+00:00"
          },
          "name": "Small"
        },
        ..
      ],
      "availableOptions": [
        {
          "_id": {
            "timestamp": 1668622453,
            "date": "2022-11-16T18:14:13.000+00:00"
          },
          "name": "soy",
          "appliesTo": "milk"
        },
        ..
      ]
    }

**Testing with curl**

    curl -i http://localhost:8082/api/coffeeshop/menu
    curl -i --insecure https://localhost:8445/api/coffeeshop/menu

### <a name="order-api"></a>Order API

#### Place order

**Syntax**

    POST /api/coffeeshop/{coffeeShopId}/order

Places an order to the coffee shop with ID *coffeeShopId*. The order details are given in the JSON-formatted HTTP request body.

The returned Location header contains the URL of the created order.

**Response**

HTTP status | Description
----------- | -----------
201 | New order created.
402 | Too low credit rating to accept order. Payment required!

**Example**

    POST http://localhost:8082/api/coffeeshop/1/order

    {
        "coffeeShopId": 1,
        "drinker": "Dagbjørn",
        "size": "Small",
        "type": {
            "name": "Americano",
            "family": "Coffee"
        },
        "selectedOptions": [
            "decaf"
        ]
    }

Response:

    {
        "id": "585fe5230d248f00011173ce",
        "coffeeShopId": 1,
        "drinker": "Dagbjørn",
        "size": "Small",
        "type": {
            "name": "Americano",
            "family": "Coffee"
        },
        "selectedOptions": [
            "decaf"
        ]
    }

**Testing with curl**

:white_check_mark: Must be run from `microcoffee-order` to find the JSON file `src\test\curl\order.json`.

Due to the CSRF protection implemented by the Order API, the POST operation must provide a valid CSRF token in an `XSRF-TOKEN` cookie as well as in an `X-XSRF-TOKEN` header. To get a valid token, we first make a dummy call to the GET operation of the Order API. The CSRF token is then extracted from the returned `X-XSRF-TOKEN` header.

    :: Calls the GET operation and sets the environment variable CSRF-TOKEN with the current CSRF token.
    for /f "delims=" %I in ('curl -s -i http://localhost:8082/api/coffeeshop/1/order/123 ^| grep X-XSRF-TOKEN ^| sed -nr "s/X-XSRF-TOKEN: (.*)/\1/p"') do set CSRF-TOKEN=%I

    :: Test the Order API. Both with http and https.
    curl -i -H "Cookie: XSRF-TOKEN=%CSRF-TOKEN%" -H "X-XSRF-TOKEN: %CSRF-TOKEN%" -H "Accept: application/json" -H "Content-Type: application/json" -X POST -d @src\test\curl\order.json http://localhost:8082/api/coffeeshop/1/order

     curl -i --insecure -H "Cookie: XSRF-TOKEN=%CSRF-TOKEN%" -H "X-XSRF-TOKEN: %CSRF-TOKEN%" -H "Accept: application/json" -H "Content-Type: application/json" -X POST -d @src\test\curl\order.json https://localhost:8445/api/coffeeshop/1/order

#### Get order details

**Syntax**

    GET /api/coffeeshop/{coffeeShopId}/order/{orderId}

Reads the details of the order of the given ID *orderId* from coffee shop with the given ID coffeeShopId.

**Response**

HTTP status | Description
----------- | -----------
200 | Order found and returned in the JSON-formatted HTTP response body.
204 | Requested order ID is not found.

**Example**

    GET http://localhost:8082/api/coffeeshop/1/order/585fe5230d248f00011173ce

Response:

    {
        "id": "585fe5230d248f00011173ce",
        "coffeeShopId": 1,
        "drinker": "Dagbjørn",
        "size": "Small",
        "type": {
            "name": "Americano",
            "family": "Coffee"
        },
        "selectedOptions": [
            "decaf"
        ]
    }

**Testing with curl**

    curl -i http://localhost:8082/api/coffeeshop/1/order/585fe5230d248f00011173ce
    curl -i --insecure https://localhost:8445/api/coffeeshop/1/order/585fe5230d248f00011173ce

### <a name="creditrating-api"></a>CreditRating API

#### Get credit rating

**Syntax**

    GET /api/coffeeshop/creditrating/{customerId}

Gets the credit rating of the customer with ID *customerId*. For the time being, a credit rating of 70 is always returned!

**Response**

HTTP status | Description
----------- | -----------
200 | Credit rating returned in the JSON-formatted HTTP response body.

**Example**

    GET http://localhost:8083/api/coffeeshop/creditrating/john

Response:

    {
        "creditRating": 70
    }

**Testing with curl**

The example below automates the process of getting the client secret of the order-service client from Keycloak. The client id and secret are needed for authentication to get an access token for calling the CreditRating API. Alternatively, the client secret could be read manually by logging on to the Keycloak admin UI at https://localhost:8456/admin.

    set AUTHSERVER=https://localhost:8456

    :: To read the client secret, we need an admin token.
    for /f "delims=" %I in ('curl -s -k -d "client_id=admin-cli" -d "username=admin" -d "password=admin" -d "grant_type=password" %AUTHSERVER%/realms/master/protocol/openid-connect/token ^| jq -r ".access_token"') do set ADMINTOKEN=%I

    :: Get the internal id of the order-service client.
    for /f "delims=" %I in ('curl -s -k -H "Authorization: Bearer %ADMINTOKEN%" %AUTHSERVER%/admin/realms/microcoffee/clients ^| jq -r ".[] | select(.clientId == \"order-service\") | .id"') do set ID=%I

    :: Use the internal id to locate the client secret of the order-service.
    for /f "delims=" %I in ('curl -s -k -H "Authorization: Bearer %ADMINTOKEN%" %AUTHSERVER%/admin/realms/microcoffee/clients/%ID%/client-secret ^| jq -r ".value"') do set CLIENT_SECRET=%I

    :: Get access token by authenticating with clientId and clientSecret.
    for /f "delims=" %I in ('curl -s -k -H "Host: authserver.microcoffee.study:8456" -H "Content-Type: application/x-www-form-urlencoded" --data-urlencode "grant_type=client_credentials" --data-urlencode "scope=creditrating" --data-urlencode "client_id=order-service" --data-urlencode "client_secret=%CLIENT_SECRET%" -X POST %AUTHSERVER%/realms/microcoffee/protocol/openid-connect/token ^| jq -r ".access_token"') do set ACCESSTOKEN=%I

    :: Finally, call the CreditRating API.
    curl -i -H "Authorization: Bearer %ACCESSTOKEN%" http://localhost:8083/api/coffeeshop/creditrating/john
    curl -i --insecure -H "Authorization: Bearer %ACCESSTOKEN%" https://localhost:8446/api/coffeeshop/creditrating/john

## <a name="spring-cloud-netflix"></a>Spring Cloud Netflix

### <a name="eureka"></a>Eureka

#### <a name="eureka-dashboard"></a>Eureka Dashboard

The Eureka Dashboard allows you to inspect the registered instances.

To open the dashboard, navigate to https://localhost:8455.

![Snapshot of Eureka Dashboard](https://raw.githubusercontent.com/dagbjorn/microcoffeeoncloud/master/docs/images/eureka-dashboard.png "Snapshot of Eureka Dashboard")

### <a name="hystrix"></a>Hystrix

[Spring Cloud Netflix Hystrix](https://github.com/Netflix/Hystrix/wiki) is discontinued in Spring Cloud 2020, hence Microcoffee has migrated to [Resilience4J](https://github.com/resilience4j/resilience4j).

## <a name="resilience4j"></a>Resilience4J

[Resilience4J](https://github.com/resilience4j/resilience4j) is a lightweight fault tolerance library inspired by Netflix Hystrix, but designed for Java 8 and functional programming. It offers classic Hystrix functions like circuit breaker, rate limiter, bulkhead and retry.

The Order service is using Resilience4J to retry failed calls to CreditRating, a service that can be configured to behave in an unreliable manner.

The desired behavior of CreditRating is configurable by means of configuration properties in `creditrating-${profile}.properties`.

Property | Description
-------- | -----------
app.creditrating.service.behavior | 0=Stable, 1=Failing, 2=Slow, 3=Unstable. Default is 0.
app.creditrating.service.behavior.delay | Delay in seconds when behavior 2 or 3 is selected. Default is 10 secs.

The service behaviors may be described as follows:

Behavior | Value | Description
-------- | ----- | -----------
Stable | 0 | All service calls returns after a brief delay.
Failing | 1 | All service calls throws an exception after a brief delay.
Slow | 2 | All service calls is delayed by ${app.creditrating.service.behavior.delay} secs.
Unstable | 3 | A random mix of stable, failing and slow behaviors.

## <a name="extras"></a>Extras

### <a name="download-geodata"></a>Download geodata from OpenStreetMap

:construction: Just some old notes for now...

Download geodata:
- Go to https://www.openstreetmap.org
- Search for Oslo
- Select a search result
- Adjust wanted size of map
- Click Export
- Click Overpass API (works better)
- Save to file: `oslo.osm`

osmfilter:
http://wiki.openstreetmap.org/wiki/Osmfilter
osmfilter is used to filter OpenStreetMap data files for specific tags.

Install dir for exe (Windows): `C:\apps\utils`

List of all Keys, sorted by Occurrence:

    osmfilter oslo.osm --out-count

List of a Key's Values, sorted by Occurrence:

    osmfilter oslo.osm --out-key=cuisine | sort /r

Get all coffee shops:

    osmfilter oslo.osm --keep="all cuisine=coffee_shop" > oslo-coffee-shops.xml

### <a name="microcoffee-on-gke"></a>Microcoffee on Google Kubernetes Engine (GKE)

#### Getting started

See [Kubernetes Engine Quickstart](https://cloud.google.com/kubernetes-engine/docs/quickstart) for getting started with GKE. In particular, the following needs to be carried out:

1. Sign in to Google Cloud Platform using your Gmail account.
1. Activate a free trial of one year/USD 300. (Or use an existing paid account if you have...)
1. Create a project called microcoffeeoncloud.
1. Enable billing for the microcoffeeoncloud project.
1. Choose a shell. The following options are available:
   * Local shell (recommended): Install [Google Cloud SDK](https://cloud.google.com/sdk/docs/quickstarts) locally. Make sure that `gcloud init` is run as part of the installation. Also, make sure to install kubectl. (The simplest way is to run `gcloud components install kubectl`.)
   * Cloud shell: Activate Google Cloud Shell.
1. Verify default settings for the gcloud CLI tool (region, zone, account and project).
   * `gcloud config list`
1. Enable Compute Engine API (gcloud compute) from [Google Cloud Console](https://console.cloud.google.com), Navigation Menu > APIs & Services.

#### Create cluster

Create a default cluster consisting of three nodes.

    gcloud container clusters create microcoffeeoncloud-cluster --machine-type=n1-standard-1 --disk-size=10GB

List all created VM instances.

    gcloud compute instances list

Remember to delete the cluster when your finished. (This will save you a few bucks a day...)

    gcloud container clusters delete microcoffeeoncloud-cluster --quiet

#### Create MongoDB disk

Create a 10GB disk for use by MongoDB.

    gcloud compute disks create mongodb-disk --size 10GB

List all created disks.

    gcloud compute disks list

Delete the disk. (The daily charge of the disk is minimal.)

    gcloud compute disks delete mongodb-disk --quiet

#### Create firewall openings

When the service is exposed by a NodePort, we need to manually add firewall openings for the selected port numbers.

    gcloud compute firewall-rules create microcoffee --allow tcp:30080-30099,tcp:30443-30462

Also, for simple data loading in MongoDB, create another firewall rule for port 30017. (May be deleted again as soon as the database
loading is completed.)

    gcloud compute firewall-rules create microcoffee-database --allow tcp:30017

List firewall rules.

    gcloud compute firewall-rules list

Delete the firewall rules.

    gcloud compute firewall-rules delete microcoffee --quiet
    gcloud compute firewall-rules delete microcoffee-database --quiet

#### Regenerate secret in authorization server

The authorization server, Keycloak, is automatically configured by importing a previously exported Microcoffee realm when the server starts. However, secrets are not exported and need to be regenerated.

From the `microcoffeeoncloud` top-level folder, start Keycloak by running the following batch file:

    deploy-k8s-1-servers.bat gke

When Keycloak is up and running, regenerate the client secret of the OAuth2 client called *order-service*.

*EXTERNAL-IP* is found by listing the created VM instances by running `gcloud compute disks list`.

    set EXTERNAL_IP=EXTERNAL-IP

    :: Get an admin token (only valid for 60s secs)
    for /f "delims=" %I in ('curl -s -k -d "client_id=admin-cli" -d "username=admin" -d "password=admin" -d "grant_type=password" https://%EXTERNAL_IP%:30456/realms/master/protocol/openid-connect/token ^| jq -r ".access_token"') do set ADMINTOKEN=%I

    :: Get the id value of the order-service client to use in the resource URL for regenerating the client secret
    for /f "delims=" %I in ('curl -s -k -H "Authorization: Bearer %ADMINTOKEN%" https://%EXTERNAL_IP%:30456/admin/realms/microcoffee/clients ^| jq -r ".[] | select(.clientId == \"order-service\") | .id"') do set ID=%I

    :: Regenerate the client secret
    curl -i -k -X POST -H "Authorization: Bearer %ADMINTOKEN%" https://%EXTERNAL_IP%:30456/admin/realms/microcoffee/clients/%ID%/client-secret

    :: Read the client secret and assign it to an environment variable called CLIENT_SECRET
    for /f "delims=" %I in ('curl -s -k -H "Authorization: Bearer %ADMINTOKEN%" https://%EXTERNAL_IP%:30456/admin/realms/microcoffee/clients/%ID%/client-secret ^| jq -r ".value"') do set CLIENT_SECRET=%I

Create a Kubernetes secret called *order-client-secret* which will contain the client secret of *order-service*.

    kubectl create secret generic order-client-secret --from-literal=client-secret=%CLIENT_SECRET%

Verify the secret.

    kubectl get secret order-client-secret -o json | jq -r ".data.\"client-secret\"" | base64 -i -d

#### Run Microcoffee

From the `microcoffeeoncloud` top-level folder, run the following batch files in turn:

    deploy-k8s-2-servers.bat gke
    deploy-k8s-3-servers.bat gke
    deploy-k8s-4-apps.bat gke

Make sure that the pods are up and running before starting the next. (Check the log from each pod.)

#### Loading the database

From the `microcoffeeoncloud-database` project, run:

    set EXTERNAL_IP=EXTERNAL-IP
    mvn gplus:execute -Ddbhost=%EXTERNAL_IP% -Ddbport=30017 -Ddbname=microcoffee -Dshopfile=oslo-coffee-shops.xml

*EXTERNAL-IP* is found by listing the created VM instances by running `gcloud compute disks list`.

To verify the database loading, start the MongoDB client in the database pod. (Use `kubectl get pods` to find the PODNAME.)

    kubectl exec -it PODNAME -- mongosh microcoffee

#### Give Microcoffee a spin - in the Google cloud

Navigate to:

    https://EXTERNAL_IP:30443

As usual, run `gcloud compute disks list` to get an EXTERNAL_IP of one of the created VM instances.

:bulb: Old AngularJS frontend is available on `https://EXTERNAL_IP:30443/coffee.html`.

#### Summary of external port numbers

Microservice | http port | https port | https mgmt port
------------ | --------- | ---------- | ---------------
gateway | - | 30443 |
location | 30081 | 30444 |
order | 30082 | 30445 |
creditrating | 30083 | 30446 |
configserver | 30091 | 30454 |
discovery | 30092 | 30455 |
authserver | 30093 | 30456 | 30457
database | 30017 | 30017 |

### <a name="microcoffee-on-eks"></a>Microcoffee on Amazon Elastic Kubernetes Service (EKS)

#### Getting started

See [Getting started with Amazon EKS](https://aws.amazon.com/eks/getting-started/) for information on how to get along with
Amazon EKS. In particular, the following needs to be carried out:

1. Create a new AWS account at https://portal.aws.amazon.com/billing/signup or sign in to an existing
one. Unfortunately, Amazon doesn't include EKS in AWS Free Tier so be ready to spend a few bucks. How much? It depends, but
less than $10 should be sufficient to get Microcoffee up and running.

1. Install the AWS CLI. See [Installing the AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html).

1. Create access keys for an IAM user. See details in the following subsection.

1. Configure the AWS CLI using `aws configure`. See [Configuring the AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-configure.html) for details.
Make sure you have the access key ID and secret access key of the created IAM user at hand.
:bulb: For region names, go to [AWS Service Endpoints > View the service endpoints](https://docs.aws.amazon.com/general/latest/gr/rande.html#view-service-endpoints), move on to [Service endpoints and quotas](https://docs.aws.amazon.com/general/latest/gr/aws-service-information.html) and search for "Amazon EKS".
Also, note that as of October 2019 eu-north-1 (Stockholm) does not support t2.micro nodes, the freebies in AWS Free Tier (12 months).

1. Install eksctl. See [eksctl - The official CLI for Amazon EKS](https://eksctl.io/).

##### Create access keys for an IAM user

As a best practice, do not use the AWS account root user access keys for any task where it's not required. Instead, create a new
administrator IAM user with access keys for yourself.

1. Log in to the [Identity and Access Management (IAM) dashboard](https://console.aws.amazon.com/iam) with your root user credentials.

1. Create a new group and attach the managed policy `AdministratorAccess`. This is the easy solution, however, if you like to assign
only the minimum number of required policies, see [Minimum policy configuration (permissions)](#eks-minimum-policy-config) below.

1. Add a new user with access type `Programmatic access`. Add the user to the group you created in the previous step. Then just
follow the wizard to the end and, finally, create the user. :warning: Before closing the dialogue, make sure to make a note of the following information (or simply download the offered .csv file):
   * AWS Management Console URL.
   * Access key ID.
   * Secret access key.

#### Create cluster

Create a cluster that contains seven `t2.micro` nodes. (One for each microservice.)

    eksctl create cluster --name=microcoffeeoncloud --node-type=t2.micro --nodes=7

:bulb: See [Amazon EC2 T2 Instances](https://aws.amazon.com/ec2/instance-types/t2) for information on T2 instances.

After completed cluster creation, list the created nodes.

    kubectl get nodes -o wide

Remember to delete the cluster when your finished. See [Clean-up of resources](#eks-cleanup) below.

#### Create MongoDB volume

Create a 5GB volume for use by MongoDB.

    aws ec2 create-volume --availability-zone=eu-west-1a --size=5 --volume-type=gp2 --tag-specifications="ResourceType=volume,Tags=[{Key=app,Value=mongodb}]"

:point_right: Choose a zone within the region configured by `aws configure`.

After successful creation, volume details are listed in the response:

    {
        "AvailabilityZone": "eu-west-1a",
        "CreateTime": "2019-10-16T17:17:12.000Z",
        "Encrypted": false,
        "Size": 5,
        "SnapshotId": "",
        "State": "creating",
        "VolumeId": "vol-085f29972a3e552a2",
        "Iops": 100,
        "Tags": [
            {
                "Key": "app",
                "Value": "mongodb"
            }
        ],
        "VolumeType": "gp2"
    }

:point_right: The VolumeId must be specified in `microcoffeeoncloud-database/k8s-service.eks.yml` before deploying the application.

The volume details may also be listed by running:

    aws ec2 describe-volumes --filters="Name=tag:app,Values=mongodb"

#### Regenerate secret in authorization server

The authorization server, Keycloak, is automatically configured by importing a previously exported Microcoffee realm when the server starts. However, secrets are not exported and need to be regenerated.

From the `microcoffeeoncloud` top-level folder, start Keycloak by running the following batch file:

    deploy-k8s-1-servers.bat eks

When Keycloak is up and running, regenerate the client secret of the OAuth2 client called *order-service*.

*EXTERNAL_IP* is found by listing the created VM instances by running `gcloud compute disks list`.

    set EXTERNAL_IP=EXTERNAL-IP

    :: Get an admin token (only valid for 60s secs)
    for /f "delims=" %I in ('curl -s -k -d "client_id=admin-cli" -d "username=admin" -d "password=admin" -d "grant_type=password" https://%EXTERNAL_IP%:30456/realms/master/protocol/openid-connect/token ^| jq -r ".access_token"') do set ADMINTOKEN=%I

    :: Get the id value of the order-service client to use in the resource URL for regenerating the client secret
    for /f "delims=" %I in ('curl -s -k -H "Authorization: Bearer %ADMINTOKEN%" https://%EXTERNAL_IP%:30456/admin/realms/microcoffee/clients ^| jq -r ".[] | select(.clientId == \"order-service\") | .id"') do set ID=%I

    :: Regenerate the client secret
    curl -i -k -X POST -H "Authorization: Bearer %ADMINTOKEN%" https://%EXTERNAL_IP%:30456/admin/realms/microcoffee/clients/%ID%/client-secret

    :: Read the client secret and assign it to an environment variable called CLIENT_SECRET
    for /f "delims=" %I in ('curl -s -k -H "Authorization: Bearer %ADMINTOKEN%" https://%EXTERNAL_IP%:30456/admin/realms/microcoffee/clients/%ID%/client-secret ^| jq -r ".value"') do set CLIENT_SECRET=%I

Create a Kubernetes secret called *order-client-secret* which will contain the client secret of *order-service*.

    kubectl create secret generic order-client-secret --from-literal=client-secret=%CLIENT_SECRET%

Verify the secret.

    kubectl get secret order-client-secret -o json | jq -r ".data.\"client-secret\"" | base64 -i -d

#### Run Microcoffee

From the `microcoffeeoncloud` top-level folder, run the following batch files in turn:

    deploy-k8s-2-servers.bat eks
    deploy-k8s-3-servers.bat eks
    deploy-k8s-4-apps.bat eks

Make sure that the pods are up and running before starting the next. (Run `kubectl get pods -w` and wait for
STATUS = Running and READY = 1/1.)

#### Create firewall openings

In Microcoffee, creating firewall openings is only necessary when accessing services via node ports (NodePort). This is necessary for:

   * Initial loading of the database.
   * Accessing Microcoffee services using the static node ports. (By default, port 80 and 443 are open when exposing a service via
   a load balancer.)

Firewall openings are created by `aws ec2 authorize-security-group-ingress`. This command requires the group ID of the
security group associated with the node group. There are (at least) two ways of obtaining the group ID, either from the Amazon EKS
Dashboard (Amazon EKS > Clusters > microcoffeeoncloud > Networking > Security Groups) or run the following command:

    aws ec2 describe-instances --query "Reservations[].Instances[].SecurityGroups[]"

Then specify the GroupId value of the `eksctl-microcoffeeoncloud-nodegroup-ng-xxxxxxx` security group when creating
the firewall openings.

For accessing the node port of the database, run (replacing `GroupId` by the actual value):

    aws ec2 authorize-security-group-ingress --group-id=GroupId --protocol=tcp --port=30017 --cidr=0.0.0.0/0

And similar, for accessing the node ports of the Microcoffee services, run.

    aws ec2 authorize-security-group-ingress --group-id=GroupId --protocol=tcp --port=30080-30099 --cidr=0.0.0.0/0
    aws ec2 authorize-security-group-ingress --group-id=GroupId --protocol=tcp --port=30443-30462 --cidr=0.0.0.0/0

#### Loading the database

From the `microcoffeeoncloud-database` project, run:

    set EXTERNAL_IP=EXTERNAL-IP
    mvn gplus:execute -Ddbhost=%EXTERNAL_IP% -Ddbport=30017 -Ddbname=microcoffee -Dshopfile=oslo-coffee-shops.xml

*EXTERNAL-IP* is found by listing the created nodes by running `kubectl get nodes -o wide`. You can pick any node.

To verify the database loading, start the MongoDB client in the database pod. (Run `kubectl get pods` to find the PODNAME.)

    kubectl exec -it PODNAME -- mongosh microcoffee

#### Give Microcoffee a spin - in the AWS cloud

Navigate to:

    https://EXTERNAL-IP

Run `kubectl get services gateway-lb` to get the *EXTERNAL-IP* (hostname) of the gateway load balancer service.
Example value for region eu-west-1: `aa6937282f5b811e9ae9e02466ed2eca-133344008.eu-west-1.elb.amazonaws.com`

Alternatively, use the static node port:

    https://EXTERNAL-IP:30443

However, this time run `kubectl get nodes -o wide` to find an *EXTERNAL-IP* (IP address). You can pick any node.

:bulb: Old AngularJS frontend is available on `https://EXTERNAL_IP/coffee.html`.

#### <a name="eks-cleanup"></a>Clean-up of resources

##### Undeploy application

From the `microcoffeeoncloud` top-level folder, run the following batch file.

    undeploy-k8s-all.bat

:point_right: It is important to explicitly delete any services that have an associated *EXTERNAL-IP* value. These services are
fronted by an Elastic Load Balancing load balancer, and you must delete them in Kubernetes to allow the load balancer and associated resources to be properly released. In Microcoffee, the `gateway` microservice offers a service of type LoadBalancer.

##### Delete cluster

    eksctl delete cluster --name=microcoffeeoncloud

:bulb: Deleting the cluster after finishing each day's work, will save you a little money.

##### Delete volume

Find VolumeId:

    aws ec2 describe-volumes --filters="Name=tag:app,Values=mongodb" --query="Volumes[].VolumeId"

And delete it:

    aws ec2 delete-volume --volume-id=VolumeId

:bulb: During the 12 months period of AWS Free Tier, there is no charge in keeping the volume alive. Just keeping the volume during
the 12 months Free Tier period, will save you the hazzle of recreating it and loading the database over and over again.

#### <a name="eks-minimum-policy-config"></a>Minimum policy configuration (permissions)

Instead of assigning the IAM user full administrator access, the following is the minimum number of managed policies required for
running Microcoffee on Amazon EKS.

- AmazonEC2FullAccess
- AmazonEKSClusterPolicy
- AmazonEKSServicePolicy
- AmazonEKSWorkerNodePolicy
- AmazonSSMReadOnlyAccess
- AutoScalingFullAccess
- AWSCloudFormationFullAccess
- IAMFullAccess

In addition to the managed policies above, create the following inline policy to get a few missing permissions:

 - Policy name: Inline\_Policy\_EKS_Cluster
 - Policy document:

    {
        "Version": "2012-10-17",
        "Statement": [
            {
                "Effect": "Allow",
                "Action": [
                    "eks:UpdateClusterVersion",
                    "eks:ListUpdates",
                    "eks:DescribeUpdate",
                    "eks:DescribeCluster",
                    "eks:ListClusters",
                    "eks:CreateCluster",
                    "eks:DeleteCluster"
                ],
                "Resource": "*"
            }
        ]
    }

:bulb: Remember to validate the policy to check that it is all right.

#### Summary of external port numbers

Microservice | http port | https port | https mgmt port | Note
------------ | --------- | ---------- | --------------- | ----
gateway | - | 443/30443 | | Load balancer: 443, Node port: 30443
location | 30081 | 30444 | |
order | 30082 | 30445 | |
creditrating | 30083 | 30446 | |
configserver | 30091 | 30454 | |
discovery | 30092 | 30455 | |
authserver | 30093 | 30456 | 30457
database | 30017 | 30017 | |

#### Overview of AWS dashboards

Dashboard URL | Description
------------- | -----------
https://console.aws.amazon.com/billing/home | Billing! Keep an eye on this...
https://console.aws.amazon.com/eks/home | The clusters appear here.
https://console.aws.amazon.com/cloudformation/home | The stacks appear here.
https://console.aws.amazon.com/ec2/home | Among others, instances (nodes) and volumes appear here.
https://console.aws.amazon.com/iam/home | Identity and access management.: Users, groups, policies and other security related stuff.

:point_right: Remember to select the configured region (the one listed by `aws configure list`) on dashboards where this is
applicable.

### <a name="microcoffee-on-aks"></a>Microcoffee on Microsoft Azure Kubernetes Service (AKS)

A great resource for getting an AKS cluster up and running is [Quickstart: Deploy an Azure Kubernetes Service cluster using the Azure CLI](https://docs.microsoft.com/en-us/azure/aks/kubernetes-walkthrough). However, first you need to make sure you have a valid Azure account.

#### Account stuff
Before getting started with Microsoft AKS, you need an Azure account.

1. Sign up or log in to an existing Microsoft account.
1. [Create a free Azure 12 months account](https://azure.microsoft.com/en-us/free/kubernetes-service/) in case you don't have a paid one. Even the free account needs a credit card. Also, select a verification method. SMS code is a good choice.

:point_right: Be aware of that the really free period is only 30 days long. After this you need to upgrade to a paid account. For study use, a Pay-As-You-Go account with no technical support is sufficient. Then you are only charged for the non-free services you actually use.

#### Getting started

##### Install the Azure CLI

Install the Azure CLI on your developement machine. See
[Install the Azure CLI](https://docs.microsoft.com/en-us/cli/azure/install-azure-cli?view=azure-cli-latest) for details. The following assumes Windows.

1. Download and run the MSI installer. The path is automatically added to the Windows path.
1. Verify the installation by running `az --version`.

:bulb: Instead of installing the Azure CLI on your local machine, you could also use the [Azure Cloud Shell](https://shell.azure.com/).

##### Sign in with Azure CLI

Sign in with Azure CLI by running `az login`.

Your default browser opens and lets you sign in to your Azure account. On successful login, all the subscriptions to which you have access are listed. In case no subscriptions are found, double-check that you are logged in to the correct account (in case you have several).

:bulb: List account details by running `az account list`.

##### Configure default location

Run `az account list-locations` to list available locations. Pick a location near you, however, be aware that not all locations support Kubernetes clusters. In my case, living in Norway, `westeurope` appears to be the working choice.

Set default location:

    az configure --defaults location=westeurope

Verify current defaults by running:

    az configure --list-defaults

##### Enable Azure Monitor for containers

[Azure Monitor for containers](https://docs.microsoft.com/en-us/azure/azure-monitor/insights/container-insights-overview) is enabled using the `--enable-addons monitoring` when creating the cluster. This requires *Microsoft.OperationsManagement* and *Microsoft.OperationalInsights* to be registered on your subscription.

    az provider register --namespace Microsoft.OperationsManagement
    az provider register --namespace Microsoft.OperationalInsights

Registration may take some time. Check status by running:

    az provider show -n Microsoft.OperationsManagement -o table
    az provider show -n Microsoft.OperationalInsights -o table

#### Create resource group

Create a resource group in which other resources are deployed and managed.

    az group create --name microcoffeeoncloud

On completion, a JSON-formatted response is listed. Amongst other, the `provisioningState` property should contain the
value `Succeeded`.

To list all resource groups, run:

    az group list -o table

#### Create cluster

Create a cluster containing two nodes.

    az aks create --resource-group microcoffeeoncloud --name microcoffeeoncloud --node-count 2 --enable-addons monitoring --generate-ssh-keys

Creating a cluster takes a few minutes, but eventually the command lists a JSON-formatted response containing information about the cluster.

To list managed Kubernetes clusters, run:

    az aks list -o table

#### Configure kubectl

Configure kubectl to connect to the cluster just created. By default, the command updates the default kubectl config file located in `%USERPROFILE%\.kube\config` (Windows). Unfortunately, the standard `KUBECONFIG` environment variable is not respected by Microsoft AKS. So in order to use a custom file, specify `--file %KUBECONFIG%` on the command line.

    az aks get-credentials --resource-group microcoffeeoncloud --name microcoffeeoncloud --overwrite-existing --file %KUBECONFIG%

Verify the connection by listing the cluster nodes:

    kubectl get nodes -o wide

#### Create MongoDB disk

Create a 2GB disk for use by MongoDB.

:point_right: The disk must be created in the **node resource group** of the cluster. This is **not** the same as the resource group created above.

Get the name of the node resource group.

    $ az aks show --resource-group microcoffeeoncloud --name microcoffeeoncloud --query nodeResourceGroup -o tsv
    MC_microcoffeeoncloud_microcoffeeoncloud_westeurope

Then, create the disk specifying the above node resource group name in the `--resource-group` option.

    az disk create --resource-group MC_microcoffeeoncloud_microcoffeeoncloud_westeurope --name mongodb-disk --sku StandardSSD_LRS --size-gb 2 --query id --output tsv

:point_right: The *id* must be specified in `microcoffeeoncloud-database/k8s-service.aks.yml` before deploying the application.

#### Regenerate secret in authorization server

The authorization server, Keycloak, is automatically configured by importing a previously exported Microcoffee realm when the server starts. However, secrets are not exported and need to be regenerated.

From the `microcoffeeoncloud` top-level folder, start Keycloak by running the following batch file:

    deploy-k8s-1-servers.bat aks

When Keycloak is up and running, regenerate the client secret of the OAuth2 client called *order-service*.

*EXTERNAL-IP* of a cluster node is found by listing the created VM instances by running `gcloud compute disks list` and then set the environment variable *EXTERNAL_IP* to one of the instances.

However, this can be automated by the following command, picking the external IP address of the first instance:

    for /f "delims=" %I in ('gcloud compute instances list --format json ^| jq -r ".[0].networkInterfaces[0].accessConfigs[0].natIP"') do set EXTERNAL_IP=%I

Then carry on and regenerate the secret.

    :: Get an admin token (only valid for 60s secs)
    for /f "delims=" %I in ('curl -s -k -d "client_id=admin-cli" -d "username=admin" -d "password=admin" -d "grant_type=password" https://%EXTERNAL_IP%:30456/realms/master/protocol/openid-connect/token ^| jq -r ".access_token"') do set ADMINTOKEN=%I

    :: Get the id value of the order-service client to use in the resource URL for regenerating the client secret
    for /f "delims=" %I in ('curl -s -k -H "Authorization: Bearer %ADMINTOKEN%" https://%EXTERNAL_IP%:30456/admin/realms/microcoffee/clients ^| jq -r ".[] | select(.clientId == \"order-service\") | .id"') do set ID=%I

    :: Regenerate the client secret
    curl -i -k -X POST -H "Authorization: Bearer %ADMINTOKEN%" https://%EXTERNAL_IP%:30456/admin/realms/microcoffee/clients/%ID%/client-secret

    :: Read the client secret and assign it to an environment variable called CLIENT_SECRET
    for /f "delims=" %I in ('curl -s -k -H "Authorization: Bearer %ADMINTOKEN%" https://%EXTERNAL_IP%:30456/admin/realms/microcoffee/clients/%ID%/client-secret ^| jq -r ".value"') do set CLIENT_SECRET=%I

Create a Kubernetes secret called *order-client-secret* which will contain the client secret of *order-service*.

    kubectl create secret generic order-client-secret --from-literal=client-secret=%CLIENT_SECRET%

Verify the secret.

    kubectl get secret order-client-secret -o json | jq -r ".data.\"client-secret\"" | base64 -i -d

#### Run Microcoffee

From the `microcoffeeoncloud` top-level folder, run the following batch files in turn:

    deploy-k8s-2-servers.bat aks
    deploy-k8s-3-servers.bat aks
    deploy-k8s-4-apps.bat aks

Make sure that the pods are up and running before starting the next. (Run `kubectl get pods -w` and wait for
STATUS = Running and READY = 1/1.)

#### Loading the database

From the `microcoffeeoncloud-database` project, run:

    set EXTERNAL_IP=EXTERNAL-IP
    mvn gplus:execute -Ddbhost=%EXTERNAL_IP% -Ddbport=27017 -Ddbname=microcoffee -Dshopfile=oslo-coffee-shops.xml

*EXTERNAL-IP* is found by listing the cluster nodes by running `gcloud compute instances list` or see above for how to automate setting of the environment variable.

To verify the database loading, start the MongoDB client in the database pod. (Run `kubectl get pods` to find the PODNAME.)

    kubectl exec -it PODNAME -- mongosh microcoffee

#### Give Microcoffee a spin - in the Azure cloud

Navigate to:

    https://EXTERNAL-IP:8443

Run `gcloud compute instances list` to get the *EXTERNAL-IP* of one of the cluster nodes.

:bulb: Old AngularJS frontend is available on `https://EXTERNAL-IP:8443/coffee.html`.

#### Clean-up of resources

Simply delete the resource group to delete all resources including the cluster and the disk.

    az group delete --name microcoffeeoncloud --yes

Verify that all resources are gone:

    az aks list -o table
    az disk list -o table

Verify that both the resource group and the node resource group of Microcoffee are gone:

    az group list -o table

#### Summary of external port numbers

Microservice | http port | https port | https mgmt port | Note
------------ | --------- | ---------- | --------------- | ----
gateway | - | 8443 | |
location | 8081 | 8444 | |
order | 8082 | 8445 | |
creditrating | 8083 | 8446 | |
configserver | 8091 | 8454 | |
discovery | 8092 | 8455 | | NodePort => No external IP.
authserver | 8093 | 8456 | 8457
database | 27017 | 27017 | |

Only LoadBalancer services are externally available.

### <a name="microcoffee-on-minikube"></a>Microcoffee on Minikube

#### Getting started

Minikube runs a single-node Kubernetes cluster inside a VM on your development machine. See [minikube start](https://minikube.sigs.k8s.io/docs/start/) to getting started with Minikube. In particular, the following has to be carried out:

1. Install Minikube. Download the executable from the [Minikube repo on GitHub](https://github.com/kubernetes/minikube/releases) and
place it in a folder on the OS path.
1. Install kubectl - the Kubernetes CLI. See [Install Tools - kubectl](https://kubernetes.io/docs/tasks/tools/#kubectl)
for guidelines.
1. Optionally, define the following environment variables if you don't want the default locations in your user home directory.
   * MINIKUBE\_HOME: The location of the .minikube folder in which the Minikube VM is created. Example value (on Windows): `D:\var\minikube`
   * KUBECONFIG: The Kubernetes config file. Example value (on Windows): `D:\var\kubectl\config`
1. On Windows, Minikube requires admin privileges. Usually this requires that you need to run all Minikube commands from an administrator prompt. To avoid this, install [gsudo - a sudo for Windows](https://github.com/gerardog/gsudo).

:bulb: This setup is verified with the following versions:
* minikube v1.28.0
* kubectl v1.25.2

#### Start Minikube

To start Minikube, run:

    gsudo minikube start

:bulb: To avoid the UAC popup each time gsudo is run, start an elevated cache session by running `gsudo cache on`.

Next, configure the Docker environment variables in your shell following the instructions displayed by:

    gsudo minikube docker-env

:bulb: On Windows, it is handy to create a batch file to do this. The file should be placed in a folder on your Windows path. A sample batch file, `minikube-setenv.bat`, is located in the `utils` folder of `microcoffeeoncloud-utils`.

To check the status of your local Kubernetes cluster, run:

    gsudo minikube status

#### Regenerate secret in authorization server

The authorization server, Keycloak, is automatically configured by importing a previously exported Microcoffee realm when the server starts. However, secrets are not exported and need to be regenerated.

From the `microcoffeeoncloud` top-level folder, start Keycloak by running the following batch file:

    deploy-k8s-1-servers.bat mkube

When Keycloak is up and running, regenerate the client secret of the OAuth2 client called *order-service*.

*NODE_IP* is the IP address assigned to the Minikube single-node cluster. (Displayed by `gsudo minikube ip`.)

    for /f "delims=" %I in ('gsudo minikube ip') do set NODE_IP=%I

    :: Get an admin token (only valid for 60s secs).
    for /f "delims=" %I in ('curl -s -k -d "client_id=admin-cli" -d "username=admin" -d "password=admin" -d "grant_type=password" https://%NODE_IP%:30456/realms/master/protocol/openid-connect/token ^| jq -r ".access_token"') do set ADMINTOKEN=%I

    :: Get the id value of the order-service client to use in the resource URL for regenerating the client secret.
    for /f "delims=" %I in ('curl -s -k -H "Authorization: Bearer %ADMINTOKEN%" https://%NODE_IP%:30456/admin/realms/microcoffee/clients ^| jq -r ".[] | select(.clientId == \"order-service\") | .id"') do set ID=%I

    :: Regenerate the client secret.
    curl -i -k -X POST -H "Authorization: Bearer %ADMINTOKEN%" https://%NODE_IP%:30456/admin/realms/microcoffee/clients/%ID%/client-secret

    :: Read the client secret and assign it to an environment variable called CLIENT_SECRET.
    for /f "delims=" %I in ('curl -s -k -H "Authorization: Bearer %ADMINTOKEN%" https://%NODE_IP%:30456/admin/realms/microcoffee/clients/%ID%/client-secret ^| jq -r ".value"') do set CLIENT_SECRET=%I

Create a Kubernetes secret called *order-client-secret* which will contain the client secret of *order-service*.

    kubectl create secret generic order-client-secret --from-literal=client-secret=%CLIENT_SECRET%

Verify the secret.

    kubectl get secret order-client-secret -o json | jq -r ".data.\"client-secret\"" | base64 -i -d

#### Run Microcoffee

From the `microcoffeeoncloud` top-level folder, run the following batch files in turn.

    deploy-k8s-2-servers.bat mkube
    deploy-k8s-3-servers.bat mkube
    deploy-k8s-4-apps.bat mkube

Make sure that the pods are up and running before starting the next. (Check the log from each pod. Use `kubectl get pods` to
find the PODNAME.)

#### <a name="setting-up-database"></a>Setting up the database

##### The Kubernetes volume used by the MongoDB database

The manifest file `k8s-service-mkube.yml` in `microcoffeeoncloud-database` creates a Kubernetes volume of type hostPath for
use by the MongoDB database. The properties of the volume are as follows:

* Name: `mongodbdata`
* mountPath (inside the container): `/data/db`
* hostPath (on the host): `/mnt/sda1/data/mongodbdata`

The data stored in the volume survive restarts. This is because Minikube by default is configured to persist files stored in `/data` (a softlink to `/mnt/sda1/data`).

##### Loading the database

From the `microcoffeeoncloud-database` project, run:

    mvn gplus:execute -Ddbhost=NODE_IP -Ddbport=27017 -Ddbname=microcoffee -Dshopfile=oslo-coffee-shops.xml

*NODE_IP* is the IP address assigned to the Minikube single-node cluster. (Displayed by `gsudo minikube ip`.)

To verify the database loading, start the MongoDB client in the database pod. (Use `kubectl get pods` to find the PODNAME.)

    kubectl exec -it PODNAME -- mongosh microcoffee

#### Give Microcoffee a spin

Navigate to:

    https://NODE_IP:30443

As usual, run `gsudo minikube ip` to get the NODE_IP of the Minikube cluster.

:bulb: Old AngularJS frontend is available on `https://NODE_IP:30443/coffee.html`.

#### Summary of port numbers

Microservice | http port | https port | https mgmt port
------------ | --------- | ---------- | ---------------
gateway | - | 30443 |
location | 30081 | 30444 |
order | 30082 | 30445 |
creditrating | 30083 | 30446 |
configserver | 30091 | 30454 |
discovery | 30092 | 30455 |
authserver | 30093 | 30456 | 30457
database | 27017 | 27017 |

### <a name="api-load-testing-gatling"></a>API load testing with Gatling

#### About Gatling

[Gatling](https://gatling.io) is a load test tool for testing HTTP servers. Test scenarios are written in Scala, however
no deep Scala skills are needed since Gatling provides an easy-to-use DSL. See [Gatling documentation](https://gatling.io/docs/current)
for more information.

Gatling may be used in two ways:

1. Use Gatling as a standalone tool.
2. Use Gatling with a build tool (Maven or sbt).

For load testing of the Microcoffee API we use Gatling with Maven. This requires no separate tool installation, however it is very
useful to install an IDE that supports Scala. [IntelliJ](https://www.jetbrains.com/idea/) is a good alternative.

:point_right: When configuring a Scala SDK in IntelliJ from File > Project Structure... > Platform Settings > Global Libraries, make sure to select a Scala 2.13 SDK.

#### The test scenarios

The test scenarios are developed in the `microcoffeeoncloud-gatlingtest` project and consist of the following simulation classes:

Simulation class  | Test data feed | Template JSON
----------------- | -------------- | -------------
LocationApiTest   | locations.csv  | -
MenuApiTest       | -              | -
OrderApiTest      | orders.csv     | OrderTemplate.json

The simulation classes use the following system properties for test configuration:

System property      | Mandatory | Default value | Description
-------------------- | --------- | ------------- | -----------
app.baseUrl          | Yes       |               | Base URL of API. Example value: https://localhost:8443
test.numberOfUsers   | No        | 1             | Number of concurrent REST calls.
test.durationMinutes | No        | 0             | Duration of a test run in number of minutes.
test.durationSeconds | No        | 1             | Duration of a test run in number of seconds.

The test duration will be the total of `test.durationMinutes` and `test.durationSeconds`.

#### Running load tests from Maven

Load tests are run using the `gatling-maven-plugin`. Use the `gatling.simulationClass` property to specify the
fully qualified name (FQN) of the simulation class to run.

From the `microcoffeeoncloud-gatlingtest` project, run:

    mvn gatling:test -Dgatling.simulationClass=study.microcoffee.scenario.OrderApiTest -Dapp.baseUrl=https://localhost:8443 -Dtest.numberOfUsers=5 -Dtest.durationMinutes=30 -Dtest.durationSeconds=0

#### Running load tests in IntelliJ

During development of simulation classes, it is very handy to test scenarios in the IDE. To accomplish this, create
a Run Configuration like the following example shows:

Run > Edit Configurations... > + (Add New Configuration) > Application
* Name: OrderApiTest
* Main class: study.microcoffee.scenario.OrderApiTestRunner
* Modify options > Add VM options
  - VM options: `--add-opens=java.base/java.lang=ALL-UNNAMED -Dapp.baseUrl=https\://localhost:8443 -Dtest.numberOfUsers=1 -Dtest.durationMinutes=0 -Dtest.durationSeconds=10`

:point_right: The VM option `--add-opens=java.base/java.lang=ALL-UNNAMED` is required starting from Gatling 3.13.

#### Test report

The file name of the HTML test report is displayed upon termination of a load test. A snapshot of the test report opened in a web
browser is shown below.

![Snapshot of Gatling Test Report](https://raw.githubusercontent.com/dagbjorn/microcoffeeoncloud/master/docs/images/gatling-test-report-384.png "Snapshot of Gatling Test Report")

### <a name="keycloak-config"></a>Keycloak - configuration examples

#### Realm

##### Create a new realm

###### By adding a new one

Hover current realm > Add realm
- Name: microcoffee
- Enabled: ON
- Create
- Display name: Microcoffee
- Save

###### By importing a realm file

Hoover current realm > Add realm
- Select file: microcoffeeoncloud-authserver\src\main\keycloak\microcoffee-realm.json
- Create

##### Export a realm

Microcoffee realm > Export
- Export groups and roles: ON
- Export clients: ON
- Export
- File name: microcoffeeoncloud-authserver\src\main\keyclock\microcoffee-realm.json

#### Configuration of credit credentials grant

##### Create OAuth2 client

Microcoffee realm > Clients > Create
- Client ID: order-service
- Client Protocol: openid-connect
- Save

##### Enable client credentials grant

Microcoffee realm > Clients > order-service > Settings
- Access Type: confidential
- Standard Flow Enabled: OFF
- Direct Access Grants Enabled: OFF
- Service Accounts Enabled: ON
- Save

##### Increase access token life time

Microcoffee realm > Realm Settings > Tokens
- Access Token Lifespan: 60 min
- Save

#####  Create creditrating scope

Microcoffee realm > Client Scopes > Create
- Name: creditrating
- Protocol: openid-connect
- Display on Consent Screen: OFF
- Include in Token Scope: ON
- Save

##### Add creditrating scope to order-service client

Microcoffee realm > Clients > order-service > Client Scopes > Setup
- Default Client Scopes: Add creditrating

##### Add audience mapping to CreditRating API in order-service client

Microcoffee realm > Clients > order-service > Mappers > Create
- Name: CreditRating API
- Mapper Type: Audience
- Included Custom Audience: creditrating
- Add to access token: ON
- Save

##### Get client_id and client_secret of order-service client

Microcoffee realm > Clients > order-service > Settings
- Client ID: <Copy ID>
Microcoffee realm > Clients > order-service > Credentials
- Client Authenticator: Client id and Secret
- Secret: <Copy secret>

##### Regenerate secret of order-service client

Microcoffee realm > Clients > order-service > Credentials > Regenerate Secret

### <a name="github-actions-setup"></a>Setup for GitHub Actions workflows

#### About GitHub Actions

[GitHub Actions](https://docs.github.com/en) is a CI tool for creating workflows that automate software building, testing and deploying right out of your GitHub repos. With GitHub Free for user accounts, GitHub Actions is free for public repositories. For private repositories, you get 2000 automation minutes/month for free.

In Microcoffee, workflows are created for building the Microcoffee Docker images, creating/deleting clusters on Google Kubernetes Engine (GKE) and deploy/undeploying the Microcoffee Docker images in the GKE cluster.

All workflows are stored in the standard `.github/workflows` folder.

:point_right: The build flow performs a Sonar analysis of the Microcoffee projects. This requires an account on [SonarCloud](https://sonarcloud.io). Please note that SonarCloud is free for open-source projects :slightly_smiling_face:

#### Resources

Some useful resources:
- [GitHub Actions docs](https://docs.github.com/en/actions)
- [GitHub Marketplace for Actions](https://github.com/marketplace?type=actions)
- [Deploying to Google Kubernetes Engine](https://docs.github.com/en/enterprise-cloud@latest/actions/deployment/deploying-to-your-cloud-provider/deploying-to-google-kubernetes-engine)
- [Understanding roles in Google Cloud](https://cloud.google.com/iam/docs/understanding-roles) :bulb: Very useful mapping between roles and permissions.
- [Google Kubernetes Engine API permissions](https://cloud.google.com/kubernetes-engine/docs/reference/api-permissions)
- [Compute Engine IAM roles and permissions](https://cloud.google.com/compute/docs/access/iam)

#### Setup for running the workflows

##### Create an access token in DockerHub

The build workflow requires an access token for authenticating to DockerHub.

Open https://hub.docker.com, sign in to your DockerHub account and create an access token as follows.

Select your username > Account Settings > Security > New Access Token
- Access Token Description: My only access token
- Access permissions: Read, Write, Delete (Only choice available for free accounts)
- Generate

:point_right: Make a copy of the generated token; you won't see it again...

:information_source: With free accounts, you can only create one single access token.

##### Store DockerHub access token in a GitHub secret

Go back to your GitHub repo and add your DockerHub username and access token in two separate GitHub secrets.

microcoffeeoncloud repo > Settings > Secrets > New repository secret
- Name: DOCKERHUB_USER
- Value: \<username\>
- Add secret

And another one for the token.

- Name: DOCKERHUB_TOKEN
- Value: \<access token\>
- Add secret

##### Make sure Kubernetes Engine and Container Registry APIs are enabled

Run:

    gcloud services list --enabled | grep "container"

Verify that the follow APIs are listed.

    container.googleapis.com          Kubernetes Engine API
    containerregistry.googleapis.com  Container Registry API

If not, enable them:

    gcloud services enable container.googleapis.com containerregistry.googleapis.com

##### Create a service account and store its credentials in the GitHub repo

Create a service account in Google Cloud with the necessary roles to create and deploy to GKE clusters.

1: Create a new service account called `srvgha`.

    gcloud iam service-accounts create srvgha

2: Find its email address.

    $ gcloud iam service-accounts list
    DISPLAY NAME                            EMAIL                                               DISABLED
    Compute Engine default service account  MY_PROJECT_NUMBER-compute@developer.gserviceaccount.com  False
                                            srvgha@microcoffeeoncloud.iam.gserviceaccount.com   False

Observe the email address `srvgha@microcoffeeoncloud.iam.gserviceaccount.com`. This is the address that will be used when configuring the service account.

3: Add `compute.admin`, `container.admin`, `iam.serviceAccountUser` and `roles/servicedirectory.editor` roles to the service account.

    gcloud projects add-iam-policy-binding microcoffeeoncloud --member=serviceAccount:srvgha@microcoffeeoncloud.iam.gserviceaccount.com --role=roles/compute.admin
    gcloud projects add-iam-policy-binding microcoffeeoncloud --member=serviceAccount:srvgha@microcoffeeoncloud.iam.gserviceaccount.com --role=roles/container.admin
    gcloud projects add-iam-policy-binding microcoffeeoncloud --member=serviceAccount:srvgha@microcoffeeoncloud.iam.gserviceaccount.com --role=roles/iam.serviceAccountUser
    gcloud projects add-iam-policy-binding microcoffeeoncloud --member=serviceAccount:srvgha@microcoffeeoncloud.iam.gserviceaccount.com --role=roles/servicedirectory.editor

4: Verify the roles.

    $ gcloud projects get-iam-policy microcoffeeoncloud --flatten="bindings[].members" --format="table(bindings.role)" --filter="bindings.members:srvgha@microcoffeeoncloud.iam.gserviceaccount.com"
    ROLE
    roles/compute.admin
    roles/container.admin
    roles/iam.serviceAccountUser
    roles/servicedirectory.editor

5: Create a private key for the service account and download it to a JSON key file.

    gcloud iam service-accounts keys create key.json --iam-account=srvgha@microcoffeeoncloud.iam.gserviceaccount.com

6: Base64 encode the JSON key file.

    cat key.json | base64

7: Add key secret in the GitHub repo.

microcoffeeoncloud repo > Settings > Secrets > New repository secret
- Name: GKE_SRVGHA_KEY
- Value: \<base64 encoded content of JSON key file\>
- Add secret

8: Add Keycloak admin username and password secrets.

microcoffeeoncloud repo > Settings > Secrets > New repository secret
- Name: KEYCLOAK_USERNAME
- Value: admin
- Add secret

And another one for the password.

- Name: KEYCLOAK_PASSWORD
- Value: admin
- Add secret

##### Create Sonar token in SonarCloud

The build workflow requires a Sonar token for authenticating to SonarCloud.

Open https://sonarcloud.io, sign in to your SonarCloud account and create a Sonar token as follows.

Select Account avatar > My Account > Security
- In Generate Tokens, enter a name: GitHub Microcoffee
- Generate

:point_right: Make a copy of the generated token; you won't see it again...

##### Store Sonar token in a GitHub secret

Go back to your GitHub repo and add your Sonar token in a GitHub secret.

microcoffeeoncloud repo > Settings > Secrets > New repository secret
- Name: SONAR_TOKEN
- Value: \<base64 encoded content of JSON key file\>
- Add secret

#### Tips & tricks

##### Fetch credentials for a running cluster

If you need to connect to a running cluster from your local development machine, run the following command.

    gcloud container clusters get-credentials microcoffeeoncloud-cluster

### <a name="building-microcoffee-on-wsl"></a>Building Microcoffee on WSL

#### About WSL

[Windows Subsystem for Linux](https://docs.microsoft.com/en-us/windows/wsl/about) (WSL) makes it possible to run a Linux environment directly on Windows without the overhead of a traditional virtual machine (like Oracle VirtualBox) or dual-boot setup.

In Microcoffee, WSL has been tested as an alternative platform for building Docker images during development. Historically, VirtualBox and Docker Toolbox have been used for this. However, [Docker Toolbox](https://github.com/docker-archive/toolbox) is now discontinued and the last release is 19.03.1 from July 31, 2019. Needless to say, Docker Toolbox is becoming more and more outdated for each day that passes.

[Docker Desktop](https://www.docker.com/products/docker-desktop) has become the primary tool for running Docker on Windows and Mac, however with the recent change of licensing policy, people have started to look for alternative solutions. This is where WSL comes into play. By installing Docker on WSL, we can make the Docker CLI available in a Windows PowerShell even though both the Docker client and server are running on Linux.

#### <a name="installing-wsl"></a>Installing WSL

Let's start by installing WSL. Open a Windows Command Prompt (or PowerShell) as administrator, and run:

    wsl --install

This will enable the Windows features:
- Virtual Machine Platform
- Windows Subsystem for Linux,

and set WSL 2 as the default version. Then, a Ubuntu Linux distribution is downloaded and installed. During the Linux installation, a Ubuntu shell will open in which you will be prompted to enter the username and password of your user account on Linux. Any username will do, for instance `admin`.

    Enter new UNIX username: admin
    New password: ********
    Retype new password: ********

The user has sudo privileges, however, to avoid the hazzle of typing the password each time, configure sudo without password for `admin` as follows. In the Ubuntu shell, run:

    sudo visudo

and add the last line:

    #includedir /etc/sudoers.d
    admin ALL=(ALL) NOPASSWD:ALL

:bulb: On Windows, the location of the Ubuntu file system is `%LOCALAPPDATA%\Packages\CanonicalGroupLimited.UbuntuonWindows_79rhkp1fndgsc\LocalState\ext4.vhdx` (or similar).

#### <a name="installing-docker-on-wsl"></a>Installing Docker on WSL

##### Installing Docker

From the Ubuntu shell, install Docker.

:thumbsup: Credits to https://www.objectivity.co.uk/blog/how-to-live-without-docker-desktop-developers-perspective for sharing the major part of this script (1-5).

    #/bin/bash

    # 1. Required dependencies
    sudo apt-get update
    sudo apt-get -y install apt-transport-https ca-certificates curl gnupg lsb-release

    # 2. GPG key
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

    # 3. Use stable repository for Docker
    echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

    # 4. Install Docker
    sudo apt-get update
    sudo apt-get -y install docker-ce docker-ce-cli containerd.io docker-compose

    # 5. Add user to docker group
    sudo groupadd docker
    sudo usermod -aG docker $USER

    # 6. Make Docker available on TCP port 2375
    echo '{ "hosts": ["unix:///var/run/docker.sock", "tcp://0.0.0.0:2375"] }' | sudo tee -a /etc/docker/daemon.json

    # 7. Install network utilities
    sudo apt-get update
    sudo apt-get -y install net-tools ncat

##### Start Docker and verify installation

Start Docker daemon and check status.

    sudo service docker start
    sudo service docker status

Verify the installation.

    docker run hello-world

:warning: Each time WSL is shut down, Docker must be started again. As of Windows 10, there is no easy way to autostart services on WSL.

##### Add DOCKER_HOST on Windows

To make the docker daemon available for tools like Fabric8 `docker-maven-plugin` on Windows, add the environment variable:

    DOCKER_HOST=tcp://localhost:2375

from `Control Panel` > `Edit the system environment variables`.

##### Install Minikube and create kubectl alias

In addition to Docker, we also install Minikube, the single-node Kubernetes cluster.

Download the latest version of Minikube and install it in `/usr/local/bin`.

    mkdir -p ~/download/minikube/latest
    cd ~/download/minikube/latest
    curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
    sudo install minikube-linux-amd64 /usr/local/bin/minikube

Minikube comes with [kubectl built-in](https://minikube.sigs.k8s.io/docs/handbook/kubectl). Just create an alias to make the use more convenient.

    echo 'alias kubectl="minikube kubectl --"' | sudo tee -a /etc/profile.d/kubernetes.sh

##### Create PowerShell aliases in $PROFILE

With WSL, we can run Linux commands on Windows by preceding the command with "wsl". (Linux aliases don't appear to be supported.)

    wsl minikube start
    wsl docker ps

However, with PowerShell this can be made more convenient by creating aliases in the start script that is executed when a PowerShell window is opened.

The location and name of the start script is defined by `$PROFILE`. Make sure the script folder exists and open it in your favorite editor. (Here, we simply use notepad.)

    mkdir -Force (Split-Path $PROFILE)
    notepad $PROFILE

Then, paste the following into the file and save it:

```
# Add WSL aliases. Will override any commands with the same name on the Windows path.

Function Start-WslDocker {
    wsl docker $args
}

Function Start-WslDockerCompose {
    wsl docker-compose $args
}

Function Start-WslMinikube {
    wsl minikube $args
}

Function Start-WslKubectl {
    wsl minikube kubectl -- $args
}

Set-Alias -Name docker -Value Start-WslDocker
Set-Alias -Name docker-compose -Value Start-WslDockerCompose
Set-Alias -Name minikube -Value Start-WslMinikube
Set-Alias -Name kubectl -Value Start-WslKubectl
```

`docker`, `docker-compose`, `minikube` and `kubectl` are now available when opening a new PowerShell window.

:point_right: Remember that `kubectl` will use the Kubernetes cluster configured on Linux, not Windows.

#### Building Microcoffee

##### Create PowerShell shortcut for Java 17

:point_right: Needs an update to Java 21.

Microcoffee is built on Java 17. If Java 17 is not the default JDK, a PowerShell window can be configured to use Java 17 as follows.

Create a PowerShell script called `set-java17.ps1` (here in `C:\apps\utils`).

    notepad C:\apps\utils\set-java17.ps1

Add the following (provided a Java 17 installation in `C:\apps\java\jdk-17.0.2`):

```
$env:JAVA_HOME = "C:\apps\java\jdk-17.0.2"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
```

Finally, create the shortcut (called `PowerShell Java 17`) and set the following `Target` value:

```
%SystemRoot%\system32\WindowsPowerShell\v1.0\powershell.exe -noexit -ExecutionPolicy Bypass -File C:\apps\utils\set-java17.ps1
```

##### Build Microcoffee

With Docker running in WSL, everything should now be in place for building the Docker images and push them to DockerHub.

Then, build

    mvn clean install "-Pbuild,push"

#### Running Microcoffee on WSL

##### Running on Docker

To run Microcoffee on Docker, we need to configure the host IP. Unfortunately, on WSL, this is a dynamic address that changes each time WSL is restarted. This makes Docker on WSL a suboptimal platform for running Microcoffee.

The host IP is used in two places:
1. Subject alternative name (SAN) when generating the SSL certificate.
1. The value of the eureka.instance.hostname property.

To get the host IP, run:

    wsl hostname -I

If several IP addresses are listed, the host IP is always the first one listed.

Then, regenerate the `microcoffeeoncloud-certificate` artifact and rebuild all services.

    mvn -f microcoffeeoncloud-certificates\pom.xml clean install -Pgen-certs -DvmHostIp="HOST_IP"
    mvn clean install -Pbuild

Finally, in `microcoffeeoncloud-appconfig`, update `eureka.instance.hostname` of the following property files:

    creditrating-devdocker.properties
    gateway-devdocker.properties
    location-devdocker.properties
    order-devdocker.properties

    eureka.instance.hostname=HOST_IP

To run Microcoffee, see the main documentation above. To test Microcoffee, open the URL `https://localhost:8443`.

:point_right: Remember to use `run-docker.ps1` when starting the services from PowerShell windows.

:bulb: Old AngularJS frontend is available on `https://localhost:8443/coffee.html`.

##### Running on Minikube

When starting Minikube with the docker driver (will be auto-detected by default), we need to expose all ports used by Microcoffee by specifying a number of `--ports` flags on command-line as follows.

    minikube start --ports=27017:27017 --ports=30080:30080 --ports=30081:30081 --ports=30082:30082 --ports=30083:30083 --ports=30091:30091 --ports=30092:30092 --ports=30093:30093 --ports=30443:30443 --ports=30444:30444 --ports=30445:30445 --ports=30446:30446 --ports=30454:30454 --ports=30455:30455 --ports=30456:30456 --ports=30457:30457

:warning: Port mappings cannot be changed once the Minikube cluster is created. Any `--ports` flags specified are ignored when starting an existing cluster. To change the port mappings, delete the cluster first by running `minikube delete`.

See [Microcoffee on Minikube](#microcoffee-on-minikube) for how to run Microcoffee on Minikube, however, please note the following:
- The Docker environment is already set by means of the DOCKER_HOST variable.
- Use the `.ps1` scripts instead of the `.bat` files when deploying/undeploying Microcoffee.
- The VM_IP variable takes the value `localhost`.
- The commands to regenerate the client secret of the OAuth2 `order_service` client must be run from a command prompt (if not rewritten in PowerShell syntax).
- A PowerShell compatible mvn command line to load the database may look as follows:

    mvn -f microcoffeeoncloud-database\pom.xml gplus:execute -Ddbhost=localhost -Ddbport=27017 -Ddbname=microcoffee -Dshopfile="microcoffeeoncloud-database\oslo-coffee-shops.xml"

Finally, with everything is up and running, navigate to `https://localhost:8443`.
