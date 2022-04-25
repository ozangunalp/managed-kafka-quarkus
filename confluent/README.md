# confluent Project

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

You can then execute your native executable with: `./target/confluent-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.html.

## Provided Code

### Creating Confluent Cloud Kafka Instance

Prerequisites:
Confluent Cloud Account :

[//]: # (All Link)

1. Install confluent CLI: https://docs.confluent.io/confluent-cli/current/install.html#cli-install
2. Login: `confluent login --save`
3. Create Confluent Kafka Cluster: `confluent kafka cluster create clickstream --cloud "gcp" --region "europe-west2" --type "basic"`
4. Wait for `confluent kafka cluster list` to list the cluster with status _UP_. Note `Resource ID`, eg `lkc-w7zmyw`.
5. Activate the created cluster in the environment `confluent kafka cluster use lkc-w7zmyw`.
6. Create topic `confluent kafka topic create clickstream`.
7. Note the `Endpoint` from the output of `confluent kafka cluster describe`, which will be `confluent.kafka.bootstrap.servers` config property.
8. Create Confluent Service Account: `confluent iam service-account create clickstream --description "clickstream"`, note the service account id `sa-pg2xyo`.
9. Grant access to the service account: `confluent kafka acl create --allow --service-account sa-pg2xyo --operation READ --operation CREATE --operation WRITE --topic '*'`
9. Grant access to the service account: `confluent kafka acl create --allow --service-account sa-pg2xyo --operation READ --operation CREATE --operation WRITE --consumer-group '*'`
11. Create API key for the service account: `confluent api-key create --service-account sa-pg2xyo --resource lkc-w7zmyw`.
12. Note the output `API Key` and `Secret`, which will be `confluent.kafka.api.key` and `confluent.kafka.api.secret` config properties respectively.

```shell
It may take a couple of minutes for the API key to be ready.
Save the API key and secret. The secret is not retrievable later.
+---------+------------------------------------------------------------------+
| API Key | OCF2NV6NWTMNPN3F                                                 |
| Secret  | a1O0IqkAwe8I24s6gZt4s7rPMuAFzfOdinvOMlYMK/kZdrSQqBAraLpMf2nhW0qX |
+---------+------------------------------------------------------------------+
```
11. Enable Confluent Schema Registry `confluent schema-registry cluster enable --cloud gcp --geo eu`.

```shell
+--------------+-----------------------------------------------------+
| Id           | lsrc-mvkozw                                         |
| Endpoint URL | https://psrc-kk5gg.europe-west3.gcp.confluent.cloud |
+--------------+-----------------------------------------------------+
```
12. Note the `Endpoint URL`, which will be the `confluent.schema.registry.url` config property.
13. Create another API Key, this time for the schema registry `confluent api-key create --service-account sa-pg2xyo --resource lsrc-mvkozw`.
10. Note the output `API Key` and `Secret`, which will be `confluent.schema.registry.api.key` and `confluent.schema.registry.api.secret` config properties respectively.

### Complete Confluent config

Fill in following config

```properties
confluent.kafka.bootstrap.servers=CHANGE_ME
confluent.kafka.api.key=CHANGE_ME
confluent.kafka.api.secret=CHANGE_ME

confluent.schema.registry.url=CHANGE_ME
confluent.schema.registry.api.key=CHANGE_ME
confluent.schema.registry.api.secret=CHANGE_ME
```

### Run the application with Confluent Cloud config

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
