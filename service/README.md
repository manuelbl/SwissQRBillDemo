# QR Bill Service

This project uses Quarkus framework, version 3.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell
./mvnw compile quarkus:dev
```

A Dev UI is available in dev mode at http://localhost:8081/qrbill-api/q/dev/.


## Packaging and running the application

The application can be packaged using:
```shell
./mvnw package
```
It produces the `qrbill-service-x.x.x-runner.jar` file in the `target/` directory.
It is an _über-jar_ including all the dependencies.

The application is now runnable using `java -jar target/qrbill-service-x.x.x-runner.jar`.

## Creating a native executable

You can create a native executable using:
```shell
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:
```shell
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

Actually used command:

```shell
quarkus build --native -Dquarkus.native.container-build=true -Dquarkus.native.container-runtime=podman
```

## Creating a docker image

Build docker image:

```shell
 buildx build --file=src/main/docker/Dockerfile.native-micro --tag=qrbill/qrbill-service --output=type=docker .
```

Run it locally:

```shell
docker run -i --rm  -e QUARKUS_HTTP_CORS='false' -p 8081:8081 qrbill/qrbill-service
```
