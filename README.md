# service-registry-broker

This is a sample of a service broker exposing service registry functionality to applications using it. Information about backend service can be registered and then made available to other apps that bind to the exposed service plans. Registration of a service can include multiple plans and associated credentials per plan (like bronze plan will allow only 5 connections to a dev instance endpoint, while gold will allow 50 connections to a prod instance endpoint). The data gets persisted in DB. Once a service is created based on the plan, the apps bound to the service would get the associated endpoint and any credentials information in the form of a VCAP_SERVICES env variable. The client/consumer app needs to parse the VCAP_SERVICES env variable to arrive at the endpoint of its related service.

There needs to be a backend service at some endpoint.

Steps to deploy the service-registry-broker:

* Deploy the backend or simulated service. A sample simulated service is available at [document-service] (https://github.com/cf-platform-eng/document-service)
* Edit the input.sql under src/main/resources folder to populate some prebuilt services and associated plans, credentials, endpoints etc.
* Run maven to build
* Push the app to CF.
* Registry the app as a service broker (this requires admin privileges on the CF instance).
* Expose the services/plans within a specific org or publicly accessible.
* Create the service based on the plan
* Deploy the client app that would bind to the service and consume the service.
A sample client app is available on github at [sample-doc-retrieve-gateway client] (https://github.com/cf-platform-eng/sample-doc-retrieve-gateway/)

Sample:
```
# Edit the domain endpoint within input.sql file 
# Change the reference to correct CF App Domain
mvn clean install
cf push test-service-registry -m 512m -p target/service-registry-broker-0.0.1-SNAPSHOT.jar -b java_buildpack

cf services
# Edit the domain endpoint
cf create-service-broker service-registry-broker testuser testuser http://test-service-registry.<CF_APP_DOMAIN>
cf enable-service-access EDMSRetreiveInterface
cf enable-service-access PolicyInterface
cf create-service EDMSRetreiveInterface basic EDMSRetreiveInterface-basic

# Now push a sample app that can bind to the newly created service
# cf bind-service sample-registry-client EDMSRetreiveInterface-basic
```
