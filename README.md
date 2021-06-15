# votebloke.fullnode
## Description
This is an implementation of a full node in the VoteBloke blockchain system - a blockchain-based voting system. See the [blockchain repo](https://github.com/VoteBloke/votebloke.blockchain) to read up on the details.

This application:
* provides an HTML-based API to interact with the blockchain
* a way to store a full blockchain.

## Requirements
This application was written under Java 16, so it needs a Java Runtime Environment compatible with Java version 16 to run the application and Java JDK 16 to compile it. We recomment to download the latest Java 16 JDK if you want to compile the application yourself or Java 16 JRE if you just want to run the .jar executable.

## Getting the JAR file
### Download
We provide links to the major release versions of the application.
Releases:
* [1.0.0](https://drive.google.com/file/d/1-Ct5H8rHBHCun32ivlWCljObA_jPuOty/view?usp=sharing)

### Compiling
This application was built under Java JDK 16, and we recommend to use it to compile this application. If you user another version, you may encounter unforeseen bugs. This project uses Gradle for its build tasks, so we recommend to download the repositorium and build it using Gradle.
1. Download the repositorium
```
git clone https://github.com/VoteBloke/votebloke.fullnode.git
```
2. Navigate to the repositorium
```
cd votebloke.fullnode
```
3. Run the Gradle bootJar task.

Linux/MacOS:
```
gradlew bootJar
```
Windows:
```
gradlew.bat bootJar
```

## Running the application
Java JRE 16 is required to run the application.
```
java -jar votebloke-fullnode-1.0.0.jar
```

By default, the application listens to the incoming HTTP requests on port 8080.
