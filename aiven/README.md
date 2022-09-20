# aiven Project

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

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/aiven-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.html.

## Provided Code

### Creating aiven Kafka Service

Prerequisites:
aiven Account : https://console.aiven.io/signup

1. Install aiven CLI: https://docs.aiven.io/docs/tools/cli.html
2. Login: `avn user login --token`. You can create an authentication token from the aiven user profile.
3. Create aiven Kafka Cluster: `avn service create -t kafka --plan [PLAN] --cloud [CLOUD_NAME] [CLUSTER_NAME]`
4. Wait for the cluster to have _RUNNING_ status using `avn service wait [CLUSTER_NAME]`.
5. Activate the schema registry on the cluster: `avn service update -c schema_registry=true [CLUSTER_NAME]`.
6. Create topic: `avn service topic-create --partitions 3 --replication 2 kafka-test clickstream`.
7. Generate and download client truststore and keystore using `avn service user-kafka-java-creds --username avnadmin [CLUSTER_NAME]`. 
This will download the following files `client.truststore.jks`, `client.keystore.p12`, `service.cert`, `service.key` and `ca.pem`.
If you wish to use SASL instead of SSL certificates, you need to enable the SASL authentication mechanism on the cluster and uncomment the relevant lines on `application.properties`.
8. Move `client.truststore.jks` to the `aiven` project `src/main/resources/client.truststore.jks`. Move `client.keystore.p12` to the `aiven` project `src/main/resources/client.keystore.p12`.
The user `avnadmin` is the default user. Generated truststore and keystore will have `changeit` password.
12. Note the cluster details from `avn service get -v [CLUSTER_NAME]`.
 Note the `service_uri`, `schema_registry_uri`, `username` and `password` from the output, which will be `aiven.kafka.bootstrap.servers`, `aiven.schema.registry.url`, `aiven.schema.registry.api.key` and `aiven.schema.registry.api.secret` config properties respectively.

```shell
SERVICE_NAME  SERVICE_TYPE  STATE    CLOUD_NAME           PLAN       CREATE_TIME           UPDATE_TIME           NOTIFICATIONS
============  ============  =======  ===================  =========  ====================  ====================  =============
kafka-test    kafka         RUNNING  google-europe-west4  startup-2  2022-09-20T14:44:00Z  2022-09-20T14:55:23Z
    service_uri         = kafka-test-redhat-e911.aivencloud.com:12364
    disk_space_mb       = 92160
    users               = {"access_cert": "...", "password": "AVNS_HgaRpItZxrBEUnZ8Wpn", "type": "primary", "username": "avnadmin"}
    schema_registry_uri = https://avnadmin:AVNS_HgaRpItZxrBEUnZ8Wpn@kafka-test-redhat-e911.aivencloud.com:12367
```

### Complete aiven config

Fill in following config

```properties
aiven.kafka.bootstrap.servers=CHANGE_ME

aiven.schema.registry.url=CHANGE_ME
aiven.schema.registry.api.key=CHANGE_ME
aiven.schema.registry.api.secret=CHANGE_ME
```

### Run the application with aiven Cloud config

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
