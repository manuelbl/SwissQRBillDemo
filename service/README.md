# QR Bill Service

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  A Dev UI is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `qrbill-service-x.x.x-runner.jar` file in the `target/` directory.
It is an _über-jar_ including all the dependencies.

The application is now runnable using `java -jar target/qrbill-service-x.x.x-runner.jar`.
