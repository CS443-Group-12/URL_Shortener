
# shortener

A simple URL Shortening project developed for **"CS443: Cloud Computing & Mobile Applications"** course at Bilkent University. To use the service user needs to enter their desired link to be shortened. Additional settings can be configured for the links such as custom expiration date and custom short link. With the *Analytics* page, users also may see the number of clicks for a short link created using the **shortener**. 

## Develeopment Tools
Project is developed using the following technologies:

 - Quarkus (Back-end)
 - Docker (Containerization)
 - Kubernetes (Container orchestration)
 - OpenShift (Deployment platform)
 - Prometheus (Metrics)
 - Grafana (Metrics visualization and monitoring)
 - Google Cloud SQL (Database)
 - Bootstrap 4 (Front-end) (Website)
 - jQuery (Client-side back-end)
 - React Native (Mobile application)

## Overview

The project is a cloud-native service deployed as an on-prem solution with the OpenShift. Back-end is developed with Java and Quarkus framework. Project is containerized automatically with JVM image created with Quarkus and pushed to DockerHub. The image then is deployed to OpenShift with the Kubernetes auto-scaling and orchestration enabled. Prometheus and Grafana are deployed to OpenShift as well and connected to the back-end for metrics collection, monitoring and visualization with the metrics provided by Quarkus. On the client-side, website is developed with Bootstrap 4 and mobile application is developed with the React Native. Google Cloud SQL is used as the relational database solution for storing the links and necessary information and scheduled events to delete the out-of-date links. 

Project offers an external API for third party usage; more information can be found on the API Docs page on the website. 

## DockerHub
The ready-to-go docker image can be pulled with:

    docker pull zxyctn/shortener-jvm-hub

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
./mvnw quarkus:dev
```

## Packaging and running the application

The application can be packaged using `./mvnw package`.
It produces the `shortener-1.0-SNAPSHOT-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using `java -jar target/shortener-1.0-SNAPSHOT-runner.jar`.

## Creating a native executable

You can create a native executable using: `./mvnw package -Pnative`.

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: `./mvnw package -Pnative -Dquarkus.native.container-build=true`.

You can then execute your native executable with: `./target/shortener-1.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image.
