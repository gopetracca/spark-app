# spark-app

Spark Application example with Clean Architecture.

## Requirements

- Maven > 3.9
- JVM 8. (Tested with Corretto-8.362.08.1)
- Docker (Only needed run integration-tests)

## Description

This is an example Spark Application that can be used as a Template.

It abstracts the business logic from the input/output using the Dependency Inversion Principle
allowing to inject the dependencies to your business logic from the Main method.

### Class Diagram

```mermaid
classDiagram
    class ReadRepositoryInt {
        read(): DataFrame
    }
    <<trait>> ReadRepositoryInt

    class FakeReadRepository {
        read(): DataFrame
    }
    class LocalReadRepository {
        read(): DataFrame
    }
    class S3ReadRepository {
        read(): DataFrame
    }
    ReadRepositoryInt <|-- FakeReadRepository
    ReadRepositoryInt <|-- S3ReadRepository
    ReadRepositoryInt <|-- LocalReadRepository
    
    class WriteRepositoryInt {
        write(): DataFrame
    }
    <<trait>> WriteRepositoryInt
    class S3WriteRepository {
        write(): DataFrame
    }
    WriteRepositoryInt <|-- S3WriteRepository
    
    class UseCase1 {
        userLoader: ReadRepositoryInt
        consentLoader: ReadRepositoryInt
        writer: WriteRepositoryInt
        run()
        filterByAge()
        filterByConsent()
        joinUserConsent()
    }

    class Main
    Main ..> S3ReadRepository : Initializes(x2)
    Main ..> S3ReadRepository : Initializes
    Main ..> UseCase1 : injects userLoader\n(S3ReadRepository)
    Main ..> UseCase1 : injects consentLoader\n(S3ReadRepository)
    Main ..> UseCase1 : injects writer\n(S3WriteRepository)
```

### Sequence Diagram

![sequence](doc/sequence.png)


## Build and test

To compile the project run: `mvn clean compile`

To execute Unit tests run: `mvn test`

Create the package: `mvn package`

To execute Integration tests:
- Start Docker daemon or Docker Desktop
- Run `mvn test -Pintegration-test`

## Run the application

Local execution:
```bash
mvn package
java -cp target/scala-app-1.0-SNAPSHOT-jar-with-dependencies.jar com.gopetracca.App
```

Spark cluster execution:
```bash
$SPARK_HOME/bin/spark-submit --master="local[2]" --class=com.gopetracca.App target/scala-app-1.0-SNAPSHOT.jar
```

## TODO
- Configuration module
