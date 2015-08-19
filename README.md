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


# Using the Service Registry REST interface

* To add a new set of services with embedded plans:
```
# Bulk insert a set of services with nested plans
curl -v -u <user>:<password> http://service-registry-uri/serviceDefns -d @<JsonPayloadFile>.json -H "Content-type: application/json" -X POST
Example:
curl -v -u testuser:testuser http://test-service-registry.xyz.com/serviceDefns/ -d @./add-services.json -H "Content-type: application/json" -X POST
```

* To add a new plan under an existing service:
```
curl -v -u <user>:<password> http://service-registry-uri/serviceDefns/<ServiceId>/<NewPlanId> -d @<JsonPayloadFile>.json -H "Content-type: application/json" -X PUT
Example:
# To create a new plan with id 'test-plan' under service with id **`test-service`**  
curl -v -u testuser:testuser http://test-service-registry.xyz.com/serviceDefns/1/test-plan -d @./add-plan.json -H "Content-type: application/json" -X PUT
```

* To associate a credential to an existing plan (within a service):
```
curl -v -u <user>:<password> http://service-registry-uri/serviceDefns/<ServiceId>/<PlanId>/<NewCredsId> -d @<JsonPayloadFile>.json -H "Content-type: application/json" -X PUT
Example: 
# To create a new credentials with id 'test-cred' under service with id **`test-service`**  and plan id **`test-plan`** 
curl -v -u testuser:testuser http://test-service-registry.xyz.com/serviceDefns/test-service/test-plan/test-cred -d @./add-cred.json -H "Content-type: application/json" -X PUT
```

# Notes

* To lock down access to the broker using a specific set of credentials, define following properties via the application.properties 
```
   security.user.name=<UserName>
   security.user.password=<UserPassword>
```

or the cf env during app push (or via manifest.yml)

```
   cf set-env <appName> SECURITY_USER_NAME <UserName>
   cf set-env <appName> SECURITIY_USER_PASSWORD <UserPassword>
```

* There can be multiple set of services, each with unlimited plans.

* Each plan in any service would be associated with one and only credentials row.
The Plan can be space or env specific and allow the service instance to use the set of credentials associated with the plan.

* Administrator can open up or lock down access to the plans using orgs and spaces.
Refer to [Service Plans Access control] (https://docs.cloudfoundry.org/services/access-control.html#enable-access)

* The Hibernate jpa can delete the table data and update the schemas each time the app instance is started with the update option (check application.properties).
To avoid loss of data, remove the auto creation from application.properties and manually create the tables using the service-registry.ddl file.
Or go with `update` auto option and then once the tables are created with right data set, switch off the auto table creation to `none`
Either make the change in the application.properties or via env variable passed via manifest.yml

* Credentials
Atleast one attribute needs to be passed in during creation of credentials : **`uri`**. username, password and other attributes can be null.
There can be any number of additional attributes (like username, password, certFile, certLocation, certType, otherTags...) that can be passed in during creation of the credentials entry. Check the add-creds.json file for some sample input. These would be then passed along to the apps consuming the instantiated services.
