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


```plantuml
class Hello

```

```mermaid
classDiagram
    Class01 <|-- AveryLongClass : Cool
    Class03 *-- Class04
    Class05 o-- Class06
    Class07 .. Class08
    Class09 --> C2 : Where am i?
    Class09 --* C3
    Class09 --|> Class07
    Class07 : equals()
    Class07 : Object[] elementData
    Class01 : size()
    Class01 : int chimp
    Class01 : int gorilla
    Class08 <--> C2: Cool label

```

## Build and test

To compile the project run: `mvn clean compile`

To execute Unit tests run: `mvn test`

Create the package: `mvn package`

To execute Integration tests:
- Stack Docker daemon or Docker Desktop
- Run `mvn integration-test`

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
