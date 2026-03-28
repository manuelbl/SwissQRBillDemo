# QR Bill Service

This project uses Quarkus framework, version 3.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell
./mvnw quarkus:dev
```

A Dev UI is available in dev mode at http://localhost:8081/qrbill-api/q/dev/.


## Packaging and running the application

The application can be packaged using:
```shell
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

## Creating a native executable

You can create a native executable using:
```shell
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:
```shell
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

Docker or Podman must be installed and running on your machine. The resulting image
is a Linux image.


## Creating a docker image

Build docker image:

```shell
docker build --file=src/main/docker/Dockerfile.native-micro --tag=qrbill/qrbill-service .
```

Run it locally:

```shell
docker run -i --rm  -e QUARKUS_HTTP_CORS_ENABLED='false' -p 8081:8081 qrbill/qrbill-service
```
