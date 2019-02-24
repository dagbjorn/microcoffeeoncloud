# microcoffeeoncloud - The &micro;Coffee Shop powered by ![Spring Boot 2.0](https://raw.githubusercontent.com/dagbjorn/microcoffeeoncloud/master/docs/images/spring-boot-2.0.png "Spring Boot 2.0") and ![Docker](https://raw.githubusercontent.com/dagbjorn/microcoffeeoncloud/master/docs/images/docker-horizontal.png "Docker")

## Revision log

Date | Change
---- | -------
10.03.2018 | Finialised markdown file.
08.09.2018 | Upgraded to Docker 18.06.1-ce.
30.10.2018 | Added extras on Google Kubernetes Engine (GKE).
12.11.2018 | Updated API URLs with leading /api.
30.01.2019 | Added Swagger URL to API doc.
24.02.2019 | Added load testing with Gatling.

## Contents

* [Acknowledgements](#acknowledgements)
* [The application](#application)
* [Prerequisite](#prerequisite)
* [Start Docker VM](#start-docker-vm)
* [Building Microcoffee](#building-microcoffee)
* [Application configuration](#configuration)
* [Run Microcoffee](#run-microcoffee)
* [Setting up the database](#setting-up-database)
* [Give Microcoffee a spin](#give-a-spin)
* [REST API](#rest-api)
* [Spring Cloud Netflix](#spring-cloud-netflix)
* [Extras](#extras)
  - [Download geodata from OpenStreetMap](#download-geodata)
  - [Microcoffee on Google Kubernetes Engine (GKE)](#microcoffee-on-gke)
  - [API load testing with Gatling](#api-load-testing-gatling)

## <a name="acknowledgements"></a>Acknowledgements
The &micro;Coffee Shop application is based on the coffee shop application coded live by Trisha Gee during her fabulous talk, "HTML5, Angular.js, Groovy, Java, MongoDB all together - what could possibly go wrong?", given at QCon London 2014. A few differences should be noted however; Microcoffee uses a microservice architecture, runs on Docker and is developed in Spring Boot instead of Dropwizard as in Trisha's version.

## <a name="application"></a>The application
The application has a simple user interface written in AngularJS and uses REST calls to access the backend services. After loading the coffee shop menu from the backend, your favorite coffee drink may be ordered. The user may also locate the nearest coffee shop and show it on Google Maps.

### The microservices
The figure shows the microservice architecture of the application. Spring Boot, Spring Cloud and MongoDB are the central technologies in the backend. AngularJS and Bootstrap in the frontend.

![Microcoffee architecture](https://raw.githubusercontent.com/dagbjorn/microcoffeeoncloud/master/docs/images/microcoffee-architecture.png "Microcoffee architecture")

The application is made up by the following microservices, each running in its own Docker container:

* The Config Server for externalized configuration in a GIT backend.
* The Discovery server for service discovery with Eureka.
* The API Gateway for proxying of calls to the backend REST services. Static web resources are also served by the API gateway.
* The backend REST API provided as three different microservices.
* The MongoDB database.

Each microservice, apart from the database, is implemented by a Spring Boot application.

The application supports both http and https on all communication channels. However, https is a requirement in most browsers to get the HTML Geolocation API going, so https is needed to unlock all available functions in Microcoffee.

#### microcoffeeoncloud-configserver
Contains the configuration server for serving externalized configuration to the application. The configuration server is based on the Spring Cloud Config Server.

The configuration is located in property/YAML files in a GIT repo. For simplicity, a GIT repo on GitHub is used. Upon startup, each microservice reads its own configuration using the Spring Cloud Config Client.

:warning: Using a public repo on GitHub is not recommended because the configuration usually contains sensitive data like passwords and details of the internal network. However, Microcoffee is just a study application and contains no secrets.

#### microcoffeeoncloud-discovery
Contains the discovery server for service discovery. The discovery server is based on Spring Cloud Netflix Eureka.

Upon startup, each microservice registers with the discovery server using an Eureka client.

#### microcoffeeoncloud-gateway
Contains the gateway server for proxying of REST calls to the backend services. The gateway server is based on Spring Cloud Netflix Zuul.

Zuul acts as a reverse proxy for REST calls from the user interface of Microcoffee, hence the user interface can use a single point to access all REST services as well as the static web resources of the application. This simplifies things, avoiding the need to manage REST service endpoints and CORS concerns independently for all the backends.

#### microcoffeeoncloud-location
Contains the Location REST service for locating the nearest coffee shop. Coffee shop geodata is downloaded from [OpenStreetMap](https://www.openstreetmap.org) and imported into the database.

:bulb: The `microcoffeeoncloud-database` project contains a geodata file, `oslo-coffee-shops.xml`, with all Oslo coffee shops currently registered on OpenStreetMap. See [Download geodata from OpenStreetMap](#download-geodata) for how this file is created.

#### microcoffeeoncloud-order
Contains the Menu and Order REST services. Provides APIs for reading the coffee menu and placing coffee orders.

Order uses the CreditRating REST service as a backend service for checking if a customer is creditworthy when placing an order. CreditRating is an unreliable service, hence giving us an "excuse" to use Hystrix from Spring Cloud Netflix for supervising service calls.

#### microcoffeeoncloud-creditrating
Contains an extremely simple credit rating service. Provides an API for reading the credit rating of a customer. Used by the Order service.

Mainly introduced to act as an unreliable backend service. The actual behavior may be configured by configuration properties. Current options include stable, failing, slow and unstable behaviors. See the [Hystrix section](#hystrix) below for details.

#### microcoffeeoncloud-database
Contains the MongoDB database. The database image is based on the official [mongo](https://hub.docker.com/r/_/mongo/) image on DockerHub.

The database installation uses a Docker volume, *mongodbdata*, for data storage. This volume needs to be created before starting the container.

:warning: The database runs without any security enabled.

### Common artifacts
The application also contains common artifacts (for the time being only one) which are used by more than one microservice. Each artifact is built by its own Maven project.

A word of warning: Common artifacts should be used wisely in a microservice architecture.

#### microcoffeeoncloud-certificates
Creates a self-signed PKI certificate, contained in the Java keystore `microcoffee-keystore.jks`, needed by the application to run https. As a matter of fact, two certificates are created:

* A wildcard certificate with common name (CN) `*.microcoffee.study` for use when running the application with Docker Compose.
* A wildcard certificare with common name (CN) `*.default` for use when running the application on Kubernetes. (See Extras)
* A certificate with common name (CN) `localhost` for use when testing outside Docker.

:bulb: The application creates three user-defined bridge networks for networking; one for the config server, another for the discovery server and finally a network for the rest of the microservices.

## <a name="prerequisite"></a>Prerequisite
Microcoffee is developed on Windows 10 and tested on Docker 18.06.1-ce/Docker Compose 1.22.0 running on Oracle VM VirtualBox 5.2.18.

For building and testing the application, you need to install Docker on a suitable Linux host environment (native, Vagrant, Oracle VM VirtualBox etc.)

:bulb: On Windows, install [Docker Toolbox](https://github.com/docker/toolbox/releases) to get all necessary tools (Docker client, Compose, Machine, Kitematic and VirtualBox).

A Docker VM is needed. To create a Docker VM called `docker-vm` for use with VirtualBox, execute the following command:

    docker-machine create --driver virtualbox docker-vm

In addition, you'll need the basic Java development tools (IDE w/ Java 1.8 and Maven) installed on your development machine.

Finally, OpenSSL is needed to create a self-signed wildcard certificate.

:warning: Java keytool won't work because it doesn't support wildcard host names as SAN (Subject Alternative Name) values.

## <a name="start-docker-vm"></a>Start Docker VM
Before moving on and start building Microcoffee, we need a running VM. The reason is that the Docker images being built are stored in the local Docker repository inside the VM.

To start your Docker VM (called `docker-vm`), run:

    docker-machine start docker-vm

Next, configure the Docker environment variables in your shell by following the instructions displayed by:

    docker-machine env docker-vm

:bulb: On Windows, it is handy to create a batch file to do this, e.g. `docker-setenv.bat`. The file should be placed in a folder on your Windows path.

To check the status of your Docker VM, run:

    docker-machine status docker-vm

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
microcoffee.study | \*.microcoffee.study | \*.microcoffee.study, ${vmHostIp} | Wildcard certificate used when running with Docker.
wildcard.default | \*.default | \*.default | Wildcard certificate used when running on Kubernetes.
localhost | localhost | | Certificate used when testing outside Docker.

`${vmHostIp}` is a Maven property defining the IP address of VM host. Default value is `192.168.99.100`.

To generate new certificates, run:

    mvn clean install -Pgen-certs

To inspect the created keystore, run:

    keytool -list -v -keystore target\classes\microcoffee-keystore.jks -storepass 12345678

To specify a different VM host IP, run:

    mvn clean install -DvmHostIp=10.0.0.100

:bulb: The keystore properties are specified in `${application}-${profile}.properties` of each microservice that is using the `microcoffeeoncloud-certificates` artifact.

### Build the microservices
Use Maven to build each microservice in turn by running:

    mvn clean package [-Pbuild,push]

Specify the `build` and `push` profiles to build and push, respectively, the Docker image to Docker Hub.

:exclamation: Just remember that your Docker VM must be running for building the Docker images successfully.

:bulb: On Windows, all projects may be built by running the `build-all-images.bat -Pbuild,push` file from the top-level folder.

## <a name="configuration"></a>Application configuration
Application and environment-specific properties are defined in standard Spring manner by `${application}-${profile}.properties` files in the `microcoffeeoncloud-appconfig` project. Supported profiles are:

* devdocker: Development environment on Docker (VM host).
* devlocal: Development environment on development machine (localhost).

In addition, the gateway routing configuration is defined in `gateway.yml`.

Configuration is served by the configuration server. The URL of the configuration server itself is defined in each microservice project as follows, depending on the current profile:

* devdocker: In `docker-compose.yml`.
* devlocal: In `bootstrap-devlocal.properties`.

In particular, you need to pay attention to the IP address of the VM. Default value used by the application is **192.168.99.100**. (Suits VirtualBox.)

The port numbers are:

Microservice | http port | https port
------------ | --------- | ----------
gateway | 8080 | 8443
location | 8081 | 8444
order | 8082 | 8445
creditrating | 8083 | 8446
configserver | 8091 | 8454
discovery | 8092 | 8455
database | 27017 | 27017

## <a name="setting-up-database"></a>Setting up the database

### Create a Docker volume for the MongoDB database
Create a Docker volume named *mongodbdata* to be used by the MongoDB database.

    docker volume create --name mongodbdata

Verify by:

    docker volume inspect mongodbdata

### Load data into the database collections
The `microcoffeeoncloud-database` project is used to load coffee shop locations, `oslo-coffee-shops.xml`, and menu data into a database called  *microcoffee*. This is accomplished by running the below Maven command. (We run it twice to also load the test database, *microcoffee-test*.) Make sure to specify the correct IP address of your VM.

But first, we need to start MongoDB (from `microcoffeeoncloud-database`):

    docker-compose up -d

Then run:

    mvn gplus:execute -Ddbhost=192.168.99.100 -Ddbport=27017 -Ddbname=microcoffee -Dshopfile=oslo-coffee-shops.xml
    mvn gplus:execute -Ddbhost=192.168.99.100 -Ddbport=27017 -Ddbname=microcoffee-test -Dshopfile=oslo-coffee-shops.xml

To verify the database loading, start the MongoDB client in a Docker container. (Use `docker ps` to find the container ID or name.)

    docker exec -it microcoffeeonclouddatabase_mongodb_1 mongo microcoffee

    > show databases
    admin             0.000GB
    local             0.000GB
    microcoffee       0.000GB
    microcoffee-test  0.000GB
    > use microcoffee
    switched to db microcoffee
    > show collections
    coffeeshop
    drinkoptions
    drinksizes
    drinktypes
    > db.coffeeshop.count()
    94
    > db.coffeeshop.findOne()
    {
            "_id" : ObjectId("58610703e113eb24f46a97a8"),
            "openStreetMapId" : "292135703",
            "location" : {
                    "coordinates" : [
                            10.7587531,
                            59.9234799
                    ],
                    "type" : "Point"
            },
            "addr:city" : "Oslo",
            "addr:country" : "NO",
            "addr:housenumber" : "55",
            "addr:postcode" : "0555",
            "addr:street" : "Thorvald Meyers gate",
            "amenity" : "cafe",
            "cuisine" : "coffee_shop",
            "name" : "Kaffebrenneriet",
            "opening_hours" : "Mo-Fr 07:00-19:00; Sa-Su 09:00-17:00",
            "operator" : "Kaffebrenneriet",
            "phone" : "+47 95262675",
            "website" : "http://www.kaffebrenneriet.no/butikkene/butikkside/kaffebrenneriet_thorvald_meyersgate_55/",
            "wheelchair" : "no"
    }
    >

Finally, stop the database container:

    docker-compose down

## <a name="run-microcoffee"></a>Run Microcoffee
When running microcoffee, the config server must be started first followed by the discovery server. Finally, the other microservices are started all together. Make sure the previous microservice has started completely, before starting the next.

A microservice is started by running `docker-compose up -d` or by using the convenience batch file `run-docker.bat` as shown below. The batch file will stop any running containers before bringing them up again.

:warning: The startup time is rather long, at least on my machine.

From the `configserver`folder, run:

    run-docker.bat

Then, when the config server is up and running (takes about 40 secs), move on to the `discovery` folder and start the discovery server.

Finally, when the discovery server is up and running (takes another 60 secs), move on to the `gateway` folder and start the remaining microservices (may take as long as 5 minutes or even more).

For testing individual projects outside Docker, run:

    run-local.bat

This is a batch file that starts a Spring Boot application by running `mvn spring-boot:run -Dspring-boot.run.profiles=devlocal`.

## <a name="give-a-spin"></a>Give Microcoffee a spin
After microcoffee has started, navigate to the coffee shop to place your first coffee order:

    https://192.168.99.100:8443/coffee.html

assuming the VM host IP 192.168.99.100.

:warning: Because of the self-signed certificate, a security-aware browser will complain a bit.
* Firefox: Click Advanced, then click Add Exception... and Confirm Security Exception.
* Chrome: Click ADVANCED and hit the "Proceed to 192.168.99.100 (unsafe)" link.
* Opera: Just click Continue anyway.

:no_entry: The application doesn't work on IE11. Error logged in console: "Object doesn't support property or method 'assign'." Object.assign is used in coffee.js. Needs some fixing... And Microsoft Edge? Cannot even find the site...

## <a name="rest-api"></a>REST API

### APIs

* [Location API](#location-api)
* [Menu API](#menu-api)
* [Order API](#order-api)
* [CreditRating API](#creditrating-api)

### <a name="swagger-doc"></a>Swagger API documentation

Centralized browsing of API documentation is available at the following URL:

    https://192.168.99.100:8443/swagger-ui.html

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

    GET http://192.168.99.100:8081/api/coffeeshop/nearest/59.920161/10.683517/200

Response:

    {
      "_id": {
        "timestamp": 1482086231,
        "machineIdentifier": 5422646,
        "processIdentifier": 19508,
        "counter": 9117700,
        "time": 1482086231000,
        "date": 1482086231000,
        "timeSecond": 1482086231
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
      "website": "http://www.kaffebrenneriet.no/butikkene/butikkside/kaffebrenneriet_karenslyst_alle_22/"
    }

**Testing with curl**

    curl -i http://192.168.99.100:8081/api/coffeeshop/nearest/59.920161/10.683517/200
    curl -i --insecure https://192.168.99.100:8444/api/coffeeshop/nearest/59.920161/10.683517/200

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

    GET http://192.168.99.100:8082/api/coffeeshop/menu

Response (abbreviated):

    {
        "types": [
            {
                "_id": {
                    "timestamp": 1482086232,
                    "machineIdentifier": 5422646,
                    "processIdentifier": 19508,
                    "counter": 9117791,
                    "time": 1482086232000,
                    "date": 1482086232000,
                    "timeSecond": 1482086232
                },
                "name": "Americano",
                "family": "Coffee"
            },
            ..
        ],
        "sizes": [
            {
                "_id": {
                    "timestamp": 1482086232,
                    "machineIdentifier": 5422646,
                    "processIdentifier": 19508,
                    "counter": 9117795,
                    "time": 1482086232000,
                    "date": 1482086232000,
                    "timeSecond": 1482086232
                },
                "name": "Small"
            },
            ..
        ],
        "availableOptions": [
            {
                "_id": {
                    "timestamp": 1482086232,
                    "machineIdentifier": 5422646,
                    "processIdentifier": 19508,
                    "counter": 9117800,
                    "time": 1482086232000,
                    "date": 1482086232000,
                    "timeSecond": 1482086232
                },
                "name": "soy",
                "appliesTo": "milk"
            },
            ..
        ]
    }

**Testing with curl**

    curl -i http://192.168.99.100:8082/api/coffeeshop/menu
    curl -i --insecure https://192.168.99.100:8445/api/coffeeshop/menu

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

    POST http://192.168.99.100:8082/api/coffeeshop/1/order

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

    curl -i -H "Accept: application/json" -H "Content-Type: application/json" -X POST -d @src\test\curl\order.json http://192.168.99.100:8082/api/coffeeshop/1/order
    curl -i --insecure -H "Accept: application/json" -H "Content-Type: application/json" -X POST -d @src\test\curl\order.json https://192.168.99.100:8445/api/coffeeshop/1/order

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

    GET http://192.168.99.100:8082/api/coffeeshop/1/order/585fe5230d248f00011173ce

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

    curl -i http://192.168.99.100:8082/api/coffeeshop/1/order/585fe5230d248f00011173ce
    curl -i --insecure https://192.168.99.100:8445/api/coffeeshop/1/order/585fe5230d248f00011173ce

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

    GET http://192.168.99.100:8083/api/coffeeshop/creditrating/john

Response:

    {
        "creditRating": 70
    }

**Testing with curl**

    curl -i http://192.168.99.100:8083/api/coffeeshop/creditrating/john
    curl -i --insecure https://192.168.99.100:8446/api/coffeeshop/creditrating/john

## <a name="spring-cloud-netflix"></a>Spring Cloud Netflix

### <a name="eureka"></a>Eureka

#### <a name="eureka-dashboard"></a>Eureka Dashboard

The Eureka Dashboard allows you to inspect the registered instances.

To open the dashboard, navigate to https://192.168.99.100:8455.

![Snapshot of Eureka Dashboard](https://raw.githubusercontent.com/dagbjorn/microcoffeeoncloud/master/docs/images/eureka-dashboard.png "Snapshot of Eureka Dashboard")

### <a name="hystrix"></a>Hystrix

[Hystrix](https://github.com/Netflix/Hystrix/wiki), an implementation of the [Circuit Breaker pattern](https://martinfowler.com/bliki/CircuitBreaker.html), is a latency and fault tolerance library provided by Spring Cloud Netflix. See the [Hystrix section](https://cloud.spring.io/spring-cloud-netflix/single/spring-cloud-netflix.html#_circuit_breaker_hystrix_clients) of the Spring Cloud Netflix reference doc for how to integrate Hystrix in an application.

The Order service is using Hystrix to supervise calls to CreditRating, a service that can be configured to behave in an unreliable manner.

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

#### <a name="hystrix-dashboard"></a>Hystrix Dashboard

The [Hystrix Dashboard](https://github.com/Netflix/Hystrix/wiki/Dashboard) allows you to monitor Hystrix metrics in real time.

To start monitoring the Order service, navigate to https://192.168.99.100:8445/hystrix and enter the following values:

- Stream: https://192.168.99.100:8445/hystrix.stream
- Delay: 2000 ms (default is fine)
- Title: Order

Then, click Monitor Stream. (A snippet of the dashboard is shown below.)

![Snapshot of Order Hystrix Dashboard](https://raw.githubusercontent.com/dagbjorn/microcoffeeoncloud/master/docs/images/hystrix-dashboard-ok.png "Snapshot of Order Hystrix Dashboard")

:no_entry: For the time being, the Hystrix Dashboard appears to be broken. When trying to open the stream, the message "<span style="color:red">**Unable to connect to Command Metric Stream.**</span>" is displayed.

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

See [Kubernetes Engine Quickstart](https://cloud.google.com/kubernetes-engine/docs/quickstart) to getting started with GKE. In particular, the following needs to be carried out:

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

    gcloud container clusters create microcoffeeoncloud-cluster

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

Also, for simple data loading in MongoDB, create another firewall rule for port 27017. (May be deleted again as soon as the database
loading is completed.)

    gcloud compute firewall-rules create microcoffee-database --allow tcp:27017

List firewall rules.

    gcloud compute firewall-rules list

Delete the firewall rules.

    gcloud compute firewall-rules delete microcoffee --quiet
    gcloud compute firewall-rules delete microcoffee-database --quiet

#### Run Microcoffee

From the `microcoffeeoncloud` top-level folder, run the following batch files (Windows!) in turn.

    deploy-k8s-1-servers.bat gke
    deploy-k8s-2-servers.bat gke
    deploy-k8s-3-apps.bat gke

Make sure that the pods are up and running before starting the next. (Check the log from each pod.)

#### Loading the database

From the `microcoffeeoncloud-database` project, run:

    mvn gplus:execute -Ddbhost=EXTERNAL_IP -Ddbport=27017 -Ddbname=microcoffee -Dshopfile=oslo-coffee-shops.xml

EXTERNAL_IP is found by listing the created VM instances by using `gcloud compute disks list`.

To verify the database loading, start the MongoDB client in the database pod. (Use `kubectl get pods` to find the PODNAME.)

    kubectl exec -it PODNAME -- mongo microcoffee

#### Give Microcoffee a spin - in the Google cloud

Navigate to:

    https://EXTERNAL_IP:30443/coffee.html

As usual, run `gcloud compute disks list` to get an EXTERNAL_IP of one of the created VM instances.

#### Summary of port numbers

Microservice | http port | https port
------------ | --------- | ----------
gateway | 30080 | 30443
location | 30081 | 30444
order | 30082 | 30445
creditrating | 30083 | 30446
configserver | 30091 | 30454
discovery | 30092 | 30455
database | 27017 | 27017

### <a name="api-load-testing-gatling"></a>API load testing with Gatling

#### About Gatling

Gatling is a load testing tool for testing HTTP servers. Test scenarios are written in Scala, however no deep Scala skills are
needed since Gatling provides an easy-to-use DSL. See [Gatling documentation](https://gatling.io/docs/current) for more information.

Gatling may be used in two ways:

1. Use Gatling as a standalone tool.
2. Use Gatling with a build tool (Maven or sbt).

For load testing of the Microcoffee API we use Gatling with Maven. This requires no separate tool installation, however it is very
useful to install an IDE that supports Scala. The Eclipse-based [Scala IDE](http://scala-ide.org) is an ok alternative.

:warning: The current version of Scala IDE is based on Eclipse Oxygen (4.7). Hence, installing the plugin option in a newer Eclipse
release is not recommended and will only give you grief. (E.g. in Eclipse 2018-09, the auto-completion feature is broken and
produces very annoying error messages.)

#### The test scenarios

The test scenarios are developed in the `microcoffeeoncloud-gatlingtest` project and consist of the following simulation classes:

Simulation class  | Test data feed | Template JSON
----------------- | -------------- | -------------
LocationApiTest   | locations.csv  | -
MenuApiTest       | -              | -
OrderApiTest      | orders.csv     | OrderTemplate.json

The simulation classes use the following system properties for test configuration:

System property     | Mandatory | Default value | Description
------------------- | --------- | ------------- | -----------
app.baseUrl         | Yes       |               | Base URL of API. Example value: https://192.168.99.100:8443
app.numberOfUsers   | No        | 1             | Number of concurrent REST calls.
app.durationMinutes | No        | 1             | Duration of a test run in number of minutes.

#### Running load tests from Maven

Load tests are run using the `gatling-maven-plugin`. Use the `gatling.simulationClass` property to specify the
fully qualified name (FQN) of the simulation class to run.

From the `microcoffeeoncloud-gatlingtest` project, run:

    mvn gatling:test -Dgatling.simulationClass=study.microcoffee.scenario.LocationApiTest -Dapp.baseUrl=https://192.168.99.100:8443 -Dapp.numberOfUsers=10 -Dapp.durationMinutes=30

#### Running load tests in Scala IDE

During development of simulation classes, it is very handy to test scenarios in the IDE. To accomplish this, create
a Run Configuration as follows:

Run Configurations... > Scala Application > New
* Main
  - Name: LocationApiTest
  - Project: microcoffeeoncloud-gatlingtest
  - Main class: io.gatling.app.Gatling
* Arguments
  - Program arguments: -s study.microcoffee.scenario.LocationApiTest -sf src/test/scala -rf target/gatling
  - VM arguments: -Dapp.baseUrl=https://192.168.99.100:8443 -Dapp.numberOfUsers=2 -Dapp.durationMinutes=2

#### Test report

The file name of the HTML test report is displayed upon termination of a load test. A snapshot of the test report opened in a web
browser is shown below.

![Snapshot of Gatling Test Report](https://raw.githubusercontent.com/dagbjorn/microcoffeeoncloud/master/docs/images/gatling-test-report.png "Snapshot of Gatling Test Report")
