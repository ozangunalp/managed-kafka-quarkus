# rhoas Project

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

You can then execute your native executable with: `./target/rhoas-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.html.

## Provided Code

### Creating Red Hat OpenShift Streams for Apache Kafka Instance

Prerequisites: 
Red Hat Developer Account :

[//]: # (All Link)

1. Install rhoas CLI: Link 
2. Login: `rhoas login` (with `--print-sso-url` if not on the default browser window.)
3. Create Managed Kafka Instance: `rhoas kafka create --name clickstream`
4. Create RHOAS Service Account: `rhoas service-account --file-format json --short-description="clickstream"`
5. Note the created `credentials.json` file. Note `clientID` and `clientSecret`, which will be `rhoas.client.id` and `rhoas.client.secret` respectively.

```json
{
	"clientID":"srvc-acct-30091810-de11-4585-2ee7-a78247870c6e",
	"clientSecret":"5a3cedf4-2285-4fe5-96f7-69a89c92d18c",
	"oauthTokenUrl":"https://identity.api.openshift.com/auth/realms/rhoas/protocol/openid-connect/token"
}
```
6. Wait for `rhoas status` to be _ready_. Note `Bootstrap URL`, which will be `rhoas.bootstrap.servers` config property.

```shell
rhoas status

  Kafka
  ---------------------------------
  ID:              c9fd3lp87jrulap7u3eg
  Name:            clickstream
  Status:          ready

```
7. Grant access to the service account: `rhoas kafka acl grant-access --producer --consumer --service-account [clientID] --topic all --group all`
8. Create topic: `rhoas kafka topic create --name clickstream --partitions 3`
9. Create Service Registry: `rhoas service-registry create --name clickstream`
10. Wait for `rhoas status` to be _ready_. Note `Registry URL`, which will be `rhoas.service.registry.url` config property.

```shell
rhoas status

  Service Registry
  ------------------------------------------------------------------------------------------------------
  ID:                    0e95af2c-6e11-475e-82ee-f13bd782df24
  Name:                  clickstream
  Status:                ready
  Registry URL:          https://bu98.serviceregistry.rhcloud.com/t/0e95af2c-6e11-475e-82ee-f13bd782df24
```

11. Add Manager role to the service account `rhoas service-registry role add --role manager --service-account [clientID]`

### Complete RHOAS config

Fill in following config

```properties
rhoas.client.id=CHANGE_ME
rhoas.client.secret=CHANGE_ME
rhoas.token.endpoint=https://identity.api.openshift.com/auth/realms/rhoas/protocol/openid-connect/token

rhoas.bootstrap.servers=CHANGE_ME
rhoas.service.registry.url=CHANGE_ME
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
