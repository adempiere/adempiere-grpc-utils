# ADempiere gRPC Utils

This project just add some libraries for start a gRPC easy with ADempiere.

## Requirements
- [JDK 11 or later](https://adoptium.net/)
- [Gradle 8.0.1 or later](https://gradle.org/install/)


### Packages Names
The prefix for package names is `org.spin.service.grpc`, a complete list the follows: 


- `org.spin.service.grpc.authentication`: Manage Authentication and authorization
- `org.spin.service.grpc.context`: All related to ADempiere context multi-thread
- `org.spin.service.grpc.util`: Just a util class for convert values from and to gRPC stub

## Binary Project

You can get all binaries from github [here](https://central.sonatype.com/artifact/io.github.adempiere/adempiere-grpc-utils/1.0.0).

All contruction is from github actions


## Some XML's:

All dictionary changes are writing from XML and all XML's hare `xml/migration`


## How to add this library?

Is very easy.

- Gradle

```Java
implementation 'io.github.adempiere:adempiere-grpc-utils:1.0.0'
```

- SBT

```
libraryDependencies += "io.github.adempiere" % "adempiere-grpc-utils" % "1.0.0"
```

- Apache Maven

```
<dependency>
    <groupId>io.github.adempiere</groupId>
    <artifactId>adempiere-grpc-utils</artifactId>
    <version>1.0.0</version>
</dependency>
```
