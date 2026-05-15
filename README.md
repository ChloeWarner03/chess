# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Chess Service Diagram: Phase 2
[Chess Service Diagram Link](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2Z0YKAE9VuImgDmMAAwA6AJyZMdqBACu2AMQALADMABwATK4gMP7IdgAWYDoIPoYASih2SKrmckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9TsKDAAFUBhi3h8UKTqYplGpVJSjDpaqzDBzZoZIDAAGJITgwUVQHmYHQo4AAa3IEDmBUMmyQYHiMHFcxgwAQao4FhgKAAHmiNDzafy7gjySp6orlYiVK7KjdnjAFBaUMAre0NegAKK2lTYAgFMnUSh++DIMz1QKOYLDcZzdTARkLZZRqDeOrGgacs0hsPW+Tq9BQ0ycTBOvnqVM+lD1NA+BAIJNUCn3dt01S1EBq9GK9kDHnc7TOzv3Yy1BQcDgK+faIcjqlLjsTqeh9EKHyGjHAC-xBdtw-jwVrjdb8+G73uwyle4wp4V7Foniaj9lgv5wl21SBq8VYSks9T7CC16Gu0ECNmgcHLJcQ4pt+lTFBmMDhI4ji5hMMGfDA8HAssSHxChaEYfsVzoBwHheL4ATQOwjIxNKcBRtIcAKDAAAyEBZIU+FlP6kEVs0bRdL0BjqPkaCkSa8yQiCvz-ICTHYfClRgVBZFTLBWnrPofw7F80KPOBboiB6MAIOJcoYmJEkEkSYCkt2gpjvyDJMrOGmLjSR5PsKoqVmZppSrK8peruqqhpqiowGgEDMAAZr48rQDAOk7DAcogNAKLgPeEWPrhVROT2mX9ggMCFcl8h7r6dUBhWzLTDe0BIAAXigHAxnGCaFD1qZSWAmaOAAjKR+aqIWmklmW0D1D4-WGoNI27MxraBSujnDs57XAJ1X4HjVQUnnIKBvvEV43neJ0CquwovkGb27v5dXGRWnlyhkqggZgQMQcmFbQXFmkITRN70U2kJXNNdWzfUREkaMpnvOZiNjLRKPoWjLasZ43h+P4XgoOgMRxIkdMM55vhYLNgo9fUDTSFGIlRu0UbdD0ymqKpwwk6h6AGYKQP1FLaGQ-ZOEyQ19SuXY7MeeJ7PeWovnXQFD5BTAjJgM9r3IdLaDhbytWVGuiWvn98gwDo1qKzLqUavAvgIBwaAAOS5XKLvW0rH3Q+djV9gORvdbJnq7fE+2jeNKDxqpsuY+mGDzUteMrWtxZjKW5bbSnaeHRT1X2y6ifq7977-Z+xt3fSHAoNwZ43lbdE23by6fY7wrSN3TKGM9H4Nam8uibrF5gxDUONzULzo7JM153NhHEbmR2U+xNMolu-jYHKmoiWiMAAOIShonNnYGDS34LIt2BKkvIzbOdGSrFYvaFFXmrGOGs0T33zB5CBD99YkgTrdeu9IzZMktkAoekUvq1Gds3W82h3aex-krH2mo4D+0DiHGAOUw64NJnXYe0ckRNXjgDZ+vUq5QGGunWMmdJo53uFjGAWZC55n5CXeC5ctrGg4VwmuLF6GYLOkw6erdZ6jhNvSZAORIFqAxBgh2Qpai3yZDWFqOjlTGFTB9eo18cgAB5zHaHKAo2qSjnK2LAA4iUPJnGsP-rCQMHidGqGXggUCADo4mU-vmYsDRnDxM6JvGG28Si7xxqRaJahYnjEyQASWkAsBa4RgiBBBJseIBoUCKk5F8EEyRQDqmqYTaiYxMkADlmlQkSbXKmHF-AcAAOyuEcCgRwMQozBDgPxAAbPAachgdFFB3lzJOjRWgdA-l-FOpNcztIlEk24P4AEKyIegNYOSJQdM+OTEB9UwEwAeuiHRGI4DzJ0XAw2ANEHD2ChbPu6CXEN1HtgmhKi3YezNKcwoJC-b9goaHeUz06FRzXkwuOg4-F3MDH1Wi1cM5Z0TBjARO8C7LTEUWCRm0Kw7VxZwg6zZ5EorcY1MFV0vkyA0ceN5EoMSZPepyqK65Nx328buSxdVrFmwlPkwFp1QFMLydIBORyAkVleaeFA7zgJhOVqqyJsNWnSoKfUIpJSDk4WJak7G+88aKuLKawIh82LUwCBYburlNiMyQAkMAbqBwQE9QAKQgHKEVpp-D1JAOqJZqSVkwx5k0ZkikeiZO-hHdAuZsAIGAG6qAZDXJQDWIq81hkHiqpOemtA5yfjZtzfm6AewADqLBclCx6AAIREgoOAABpb4dragOrsnq5l9QABWIa0DPODXKLVhIDZ+TbuojuE4UF-MvAClFwKcGsoIZCytKo1SkPIcHBF4cB6RwFai5y6LlWgOxTIg6+K+FErwiSoRi0yUFgpRtCu0jaWyIZcdK9o7cEzxju3JBE5zbPMVfooFhicGOPBdaRVh60rwHYFlM9YaxBMvlTe5qd6sUVnbWGD5z7s6vrTFaj9uNRHfvWmXKl9Q9BbhRPOnIQHWL4buUw5DV0YUfNGg8iABaayWnrJGKaS62H1GDJJiMaFKOEq3rnWjWYcxF3JUxyRgDaxWhgA2JsTr2XWL8FoJ5PK+XaHgyuYFzJsCWYWaKt2+pDSZRQJ6rNObaCYvnjOqdEpQnhJHXJmAIwS0pIIuk0YTrek0y8Dmr1PqkvWkQKGWAwBsBZsIHkRMT971yT5gLIWItjB-zLX+eoGIsruwgFaUktzuz1BANwPALz2tQA+YutR3yjyTi63o2VI9DHjx7oYc0LVLprAE2sVlI3GHOQxFQENHBesQcBsctMmWQu6r-Pqje-C320di5FimQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
