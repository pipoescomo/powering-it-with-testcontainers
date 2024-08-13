# Powering integration tests with Testcontainers
## Workshop repo link: [bit.ly/testcontainers-repo](https://bit.ly/testcontainers-repo)
## Pastebin link: [bit.ly/testcontainers-pb](https://bit.ly/testcontainers-pb)
### Disclaimers
#### We're not related to Testcontainers in any way. We just hate mocking and love Docker.

## Pre-requisites
* Java 17+
* Docker environment

## Testcontainers, what and why?
https://testcontainers.com/guides/introducing-testcontainers/

## What are we going to do?
1. Overview a simple Spring Boot application
2. Review how to set up Testcontainers
3. Write a few integration tests using Testcontainers

### Gradle dependencies
```groovy
dependencies {
    testImplementation 'io.rest-assured:rest-assured'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.kafka:spring-kafka-test'
    testImplementation 'org.springframework.boot:spring-boot-testcontainers'
    testImplementation 'org.testcontainers:junit-jupiter'
    testImplementation 'org.testcontainers:kafka'
    testImplementation 'org.testcontainers:postgresql'
    testImplementation 'org.awaitility:awaitility'
}
```