# QR Bill Service

This project uses Quarkus framework, version 3.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

For a setup with both the React UI and this service running on *localhost*, CORS handling should be disabled by
commenting the below line in `application.properties`:

```properties
# quarkus.http.cors=true
```

A Dev UI is available in dev mode at http://localhost:8081/qrbill-api/q/dev/.


## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `qrbill-service-x.x.x-runner.jar` file in the `target/` directory.
It is an _über-jar_ including all the dependencies.

The application is now runnable using `java -jar target/qrbill-service-x.x.x-runner.jar`.
