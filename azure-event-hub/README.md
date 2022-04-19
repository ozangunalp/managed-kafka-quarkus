# azure-event-hub Project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/azure-event-hub-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.html.

## Provided Code

### Create Azure Event Hub Kafka-enabled instance 

* Create an Event Hubs cluster (and resource group), note the _Host name_, which will be the `event.hub.url` config below.
* Create an Event Hub (Kafka topic), called `clickstream`.
* Create a Schema Registry Schema Group called `pointer-event`.
* Create an App Registration, and create a client secret. Note the _Application (client) ID_ and _Directory (tenant) ID_, which will be `oauth.client.id` and `oauth.tenant.id` in the config below.
* Create a client secret for the created app. Note the generated secret, which will be the `oauth.client.secret` config below. More info here: https://docs.microsoft.com/en-us/azure/event-hubs/authenticate-application
* In the Event Hub cluster, Add Role Assignment to the created app in "Access Control (IAM)" pane. Add "Azure Event Hubs Data Owner" and "Schema Registry Contributor (Preview)" roles to the created app.

[//]: # (TODO Write `az` CLI commands for the steps above)

### Complete Azure Event Hub config

Fill in following config

```properties
oauth.client.id=CHANGE_ME
oauth.client.secret=CHANGE_ME
oauth.tenant.id=CHANGE_ME

event.hub.url=example.servicebus.windows.net
```

### Run the application with Azure Event Hub config

```shell script
./mvnw compile quarkus:dev -Dquarkus.profile=managed
```

### Check for sending Kafka messages

```shell script
curl --header "Content-Type: application/json" \
  --request POST \
  --data '{"userId": "a84204f4-ea70-41db-a864-f4787a0603da", "sessionId": "5504bc7f-f753-4634-91f3-2fcd6db6f586", "pointerType": "mouse", "xpath": "/html[1]/body[1]", "screenX": 0, "screenY": 0, "clientX": 0, "clientY": 0}' \
  http://localhost:8080/clicks
```