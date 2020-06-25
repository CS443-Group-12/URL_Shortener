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

The project is a cloud-native service deployed as an on-prem solution with the OpenShift. Back-end is developed with Java and Quarkus framework. Project is containerized automatically with JVM image created with Quarkus and pushed to DockerHub. The image then is deployed to OpenShift with the Kubernetes auto-scaling and orchestration enabled. Prometheus and Grafana are deployed to OpenShift as well and connected to the back-end for metrics collection, monitoring and visualization with the metrics provided by Quarkus. On the client-side, website is developed with Bootstrap 4 and [mobile application](https://github.com/CS443-Group-12/URL_Shortener_mobile) is developed with the React Native. Google Cloud SQL is used as the relational database solution for storing the links and necessary information and scheduled events to delete the out-of-date links. 

Project offers an external API for third party usage; more information can be found on the API Docs page on the website. 
