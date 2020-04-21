# lifecycle-service
[![License](http://img.shields.io/badge/license-APACHE-blue.svg?style=flat)](http://choosealicense.com/licenses/apache-2.0/)
[![SemVer](http://img.shields.io/badge/semver-2.0.0-blue.svg?style=flat)](http://semver.org/spec/v2.0.0)
[![Download](https://api.bintray.com/packages/cratekube/maven/lifecycle-service-client/images/download.svg)](https://bintray.com/cratekube/maven/lifecycle-service-client/_latestVersion)
[![Build Status](https://travis-ci.com/cratekube/lifecycle-service.svg?branch=master)](https://travis-ci.com/cratekube/lifecycle-service)
[![Coverage Status](https://coveralls.io/repos/github/cratekube/lifecycle-service/badge.svg?branch=master)](https://coveralls.io/github/cratekube/lifecycle-service?branch=master)

_A service responsible for managing CrateKube platform services and upgrades_

## Introduction
If you are unfamiliar with [CrateKube](https://github.com/cratekube/cratekube/blob/master/docs/Architecture.md), please read our [User Documentation for the Lifecycle Service](https://github.com/cratekube/cratekube/blob/master/docs/user/LifecycleService.md).  This microservice _can_ operate as an independent API, however it is designed as part of a [larger system](https://github.com/cratekube/cratekube/blob/master/docs/Architecture.md) that addresses the following [requirements](https://github.com/cratekube/cratekube/blob/master/docs/Requirements.md) in order to provide a hybrid-cloud management platform that is [default-secure](https://github.com/cratekube/cratekube/blob/master/docs/Requirements.md#automatic-container-hardening) and [ephemeral](https://github.com/cratekube/cratekube/blob/master/docs/Requirements.md#read-only-container-file-systems) with [elastic provisioning](https://github.com/cratekube/cratekube/blob/master/docs/Requirements.md#elastic-provisioning), [hardened pod security policies](https://github.com/cratekube/cratekube/blob/master/docs/Requirements.md#pod-security-policies), self-healing [event-driven architecture](https://github.com/cratekube/cratekube/blob/master/docs/Requirements.md#event-based-control-plane-automation), and [persistent EBS backed storage](https://github.com/cratekube/cratekube/blob/master/docs/Requirements.md#persistent-storage).

## Quick links
- [User documentation](https://github.com/cratekube/cratekube/blob/master/docs/user/LifecycleService.md) for the `lifecycle-service`
- CrateKube [operator documentation](http://cratekube.github.io)
- [System Architecture](https://github.com/cratekube/cratekube/blob/master/docs/Architecture.md) and high-level [Requirements](https://github.com/cratekube/cratekube/blob/master/docs/Requirements.md)
- [How to contribute](https://github.com/cratekube/cratekube/blob/master/CONTRIBUTING.md) 
- Contributor [architecture](https://github.com/cratekube/cratekube/blob/master/contributing/Architecture%20Guidelines.md), [roles and responsibilities](https://github.com/cratekube/cratekube/blob/master/docs/RolesAndResponsibilities.md), and [developer documentation](https://github.com/cratekube/cratekube/blob/master/docs/Development.md)

## How this app works
This application utilizes two [Dropwizard Quartz Integration](https://github.com/dropwizard-jobs/dropwizard-jobs) scheduled jobs to manage the lifecycle of CrateKube platform components. 

The [UpgradeAvailabilityJob](src/main/groovy/io/cratekube/lifecycle/job/UpgradeAvailabilityJob.groovy) is responsible for retrieving and caching the configuration and deployed version of managed platform components from [Kubernetes](https://kubernetes.io/) using [kubectl](https://kubernetes.io/docs/reference/kubectl/overview/), and the latest version by querying [GitHub](https://github.com/). 
The [ComponentDeploymentJob](src/main/groovy/io/cratekube/lifecycle/job/ComponentDeploymentJob.groovy) deploys the latest version of managed components enabled for deployment that are not currently running in `Kubernetes` by retrieving deployment yaml located in its `GitHub` repository and applying it via `kubectl`. 

In order for `kubectl` calls to be successful, a [kubeconfig file](https://kubernetes.io/docs/concepts/configuration/organize-cluster-access-kubeconfig/) must exist at the configured path. This path can be overridden by providing the `KUBE_CONFIG_LOCATION` environment variable when running the application.

## Configuration
Internal and external services are configured by extending the Dropwizard application configuration with additional
settings. An environment variable parser is used to allow configuration settings to be overridden at runtime. 
These configuration options can be seen in the [app config file](app.yml).

## Local development
### Gradle builds
This project uses [gradle](https://github.com/gradle/gradle) for building and testing.  We also use the gradle wrapper
to avoid downloading a local distribution.  The commands below are helpful for building and testing.
- `./gradlew build` compile and build the application
- `./gradlew check` run static code analysis and test the application
- `./gradlew shadowJar` builds a fat jar that can be used to run the Dropwizard application
- `./gradlew buildClient` generates the API client code for the Dropwizard application
- `./gradlew publishToMavenLocal` publishes any local artifacts to the local .m2 repository

After you have generated the fat jar you can run your application with java using:
```bash
java -jar build/libs/lifecycle-service-1.0.0-SNAPSHOT-all.jar
```

### Docker builds
We strive to have our builds repeatable across development environments so we also provide a Docker build to generate 
the Dropwizard application container.  The examples below should be executed from the root of the project.

Run the base docker build:
```bash
docker build -t lifecycle-service:build --target build .
```
Note: This requires docker 19.03.x or above.  Docker 18.09 will throw errors for mount points and the `--target` flag.

Build the package target:
```
docker build -t lifecycle-service:package --target package .
```
Run the docker application locally on port 8080:
```bash
docker run -p 8080:9000 -d lifecycle-service:package
```

Fire up the Swagger specification by visiting the following URL in a browser:
```bash
http://localhost:8080/swagger
```

## Using the API client
This application generates a client for the Dropwizard application by using the swagger specification.  The maven asset
is available in JCenter, make sure you include the JCenter repository (https://jcenter.bintray.com/) when pulling this
client.  To use the client provide the following dependency in your project:

Gradle:
```groovy
implementation 'io.cratekube:lifecycle-service:1.0.0'
``` 

Maven:
```xml
<dependency>
  <groupId>io.cratekube</groupId>
  <artifactId>lifecycle-service</artifactId>
  <version>1.0.0</version>
</dependency>
```

## API Documentation
The API docs for this project are powered by the Swagger Specification. After starting up the application the available
APIs can be found at `http://localhost:<configured port>/swagger`

## Contributing
If you are interested in contributing to this project please review the [contribution guidelines](https://github.com/cratekube/cratekube/blob/master/CONTRIBUTING.md).
Thank you for your interest in CrateKube!
