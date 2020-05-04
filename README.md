# lifecycle-service
[![License](http://img.shields.io/badge/license-APACHE-blue.svg?style=flat)](http://choosealicense.com/licenses/apache-2.0/)
[![SemVer](http://img.shields.io/badge/semver-2.0.0-blue.svg?style=flat)](http://semver.org/spec/v2.0.0)
[![Download](https://api.bintray.com/packages/cratekube/maven/lifecycle-service-client/images/download.svg)](https://bintray.com/cratekube/maven/lifecycle-service-client/_latestVersion)
[![Build Status](https://travis-ci.com/cratekube/lifecycle-service.svg?branch=master)](https://travis-ci.com/cratekube/lifecycle-service)
[![Coverage Status](https://coveralls.io/repos/github/cratekube/lifecycle-service/badge.svg?branch=master)](https://coveralls.io/github/cratekube/lifecycle-service?branch=master)

_A service responsible for managing CrateKube platform services and upgrades_

## Introduction
The **_lifecycle service_** is part of an [MVaP architecture](https://github.com/cratekube/cratekube/blob/master/docs/Architecture.md) 
and set of [requirements](https://github.com/cratekube/cratekube/blob/master/docs/Requirements.md) for 
[CrateKube](https://cratekube.github.io/). 
This microservice _can_ operate as an independent API, 
however it is designed as part of a larger system in order to provide a 
hybrid-cloud management platform that is 
[default-secure](https://github.com/cratekube/cratekube/blob/master/docs/Requirements.md#automatic-container-hardening) and 
[ephemeral](https://github.com/cratekube/cratekube/blob/master/docs/Requirements.md#read-only-container-file-systems) with 
[elastic provisioning](https://github.com/cratekube/cratekube/blob/master/docs/Requirements.md#elastic-provisioning), 
[hardened pod security policies](https://github.com/cratekube/cratekube/blob/master/docs/Requirements.md#pod-security-policies), 
self-healing [event-driven architecture](https://github.com/cratekube/cratekube/blob/master/docs/Requirements.md#event-based-control-plane-automation), 
and [persistent storage](https://github.com/cratekube/cratekube/blob/master/docs/Requirements.md#persistent-storage).

## Quick links
- CrateKube [operator documentation](http://cratekube.github.io)
- [System Architecture](https://github.com/cratekube/cratekube/blob/master/docs/Architecture.md) and high-level [Requirements](https://github.com/cratekube/cratekube/blob/master/docs/Requirements.md)
- [How to contribute](https://github.com/cratekube/cratekube/blob/master/CONTRIBUTING.md) 
- Contributor [architecture](https://github.com/cratekube/cratekube/blob/master/contributing/Architecture%20Guidelines.md), [roles and responsibilities](https://github.com/cratekube/cratekube/blob/master/docs/RolesAndResponsibilities.md), and [developer documentation](https://github.com/cratekube/cratekube/blob/master/docs/Development.md)

## What does this service do?
The lifecycle-service is in charge of managing the lifecycle of all CrateKube **platform services** (known as **managed components**), including itself. 
These **managed components** run in a [Kubernetes cluster](https://kubernetes.io/docs/tutorials/kubernetes-basics/create-cluster/) 
and the **lifecycle-service** uses [kubectl](https://kubernetes.io/docs/reference/kubectl/overview/) to perform management operations.  

There are three default **managed components**: [cloud-mgmt-service](https://github.com/cratekube/cloud-mgmt-service), [cluster-mgmt-service](https://github.com/cratekube/cluster-mgmt-service), and [lifecycle-service](https://github.com/cratekube/lifecycle-service).
Each **managed component**, including the **lifecycle-service** itself, contains a `deployment.yml` file in the root of its GitHub repository with **Kubernetes** [NodePort Service](https://kubernetes.io/docs/concepts/services-networking/service/#nodeport) and [Deployment](https://kubernetes.io/docs/concepts/workloads/controllers/deployment/) resources. 
This `deployment.yml` file is used by the **lifecycle-service** to deploy the **managed component** . 

The **lifecycle-service** manages [Deployment](https://kubernetes.io/docs/concepts/workloads/controllers/deployment/)s using [matchLabels](https://kubernetes.io/docs/concepts/overview/working-with-objects/labels/#resources-that-support-set-based-requirements). 
The **matchLabels** key used is `name` and its value is the name of the **managed component**'s repository. The name of the container must also match the repository name, for example: `lifecycle-service`, `cloud-mgmt-service` and `cluster-mgmt-service`. 
A good example of the necessary configuration is the **lifecycle-service**'s [deployment.yml](https://github.com/cratekube/lifecycle-service/blob/master/deployment.yml).

Versions of `deployment.yml` are managed by release (tag) and if a **managed component** does not exist on the **Kubernetes cluster** the **lifecycle-service** is configured to manage, the latest version (retrieved from the repositories API endpoint`/tags`) is used to retrieve the `deployment.yml` and deploy it. 
This behavior can be overridden for each **managed compent** by providing the `CLUSTER_MGMT_ENABLED`, `CLOUD_MGMT_ENABLED`, or `LIFECYCLE_ENABLED` environment variables with `false` values when running the application

The **lifecycle-service** uses a [kubeconfig](https://kubernetes.io/docs/concepts/configuration/organize-cluster-access-kubeconfig/) file to communicate with the **Kubernetes cluster**. 
This defaults to `/app/kube/kubeconfig` and can be changed by providing the `KUBE_CONFIG_LOCATION` environment variable when running the application. 

## Quickstart
To run this service, simply execute:
```bash
docker run -p 8080:9000 -v /path/to/kube/config:/app/kube/kubeconfig cratekube/lifecycle-service
```
Note: We are bind mounting a **kubeconfig** `/path/to/kube/config` file inside of the container at `/app/kube/kubeconfig` in order for **kubectl** to execute successfully.

## Development
### Configuration
Internal and external services are configured by extending the Dropwizard application configuration with additional
settings. An environment variable parser is used to allow configuration settings to be overridden at runtime. 
These configuration options can be seen in the [app config file](app.yml).

### How this app works
This application utilizes two [Dropwizard Quartz Integration](https://github.com/dropwizard-jobs/dropwizard-jobs) scheduled jobs to assist in managing the lifecycle of CrateKube platform components. 

The [UpgradeAvailabilityJob](src/main/groovy/io/cratekube/lifecycle/job/UpgradeAvailabilityJob.groovy) is responsible for retrieving and caching the configuration and deployed version of managed platform components from [Kubernetes](https://kubernetes.io/) using [kubectl](https://kubernetes.io/docs/reference/kubectl/overview/), and the latest version by querying [GitHub](https://github.com/). 
This information is used to expose managed component upgrade availability. 

The [ComponentDeploymentJob](src/main/groovy/io/cratekube/lifecycle/job/ComponentDeploymentJob.groovy) deploys the latest version of **managed components** enabled for deployment that are not currently running in `Kubernetes` by retrieving deployment yaml located in its `GitHub` repository and applying it via `kubectl`. 

This service can act as a stand-alone service for managing any **Kubernetes** deployable yaml as long as it conforms to the conventions outlined [above](#what-does-this-service-do). 
It can be easily extended by forking and [developing](https://github.com/cratekube/cratekube/blob/master/docs/Development.md) as a [CrateKube contributor](https://github.com/cratekube/cratekube/blob/master/CONTRIBUTING.md). 

### Building with Docker
We strive to have our builds repeatable across development environments so we also provide a Docker build to generate 
the Dropwizard application container. The examples below should be executed from the root of the project.

##### Run the base docker build:
```bash
docker build -t lifecycle-service:local --target build .
```
Note: This requires docker 19.03.x or above. Docker 18.09 will throw errors for mount points and the `--target` flag.

##### Build the package target:
```bash
docker build -t lifecycle-service:local --target package .
```
##### Run the docker application locally on port 8080:
```bash
docker run -p 8080:9000 -v /path/to/kube/config:/app/kube/kubeconfig -d lifecycle-service:local
```

### Building with Gradle
This project uses [gradle](https://github.com/gradle/gradle) for building and testing.  We also use the gradle wrapper
to avoid downloading a local distribution.  The commands below are helpful for building and testing.
- `./gradlew build` compile and build the application
- `./gradlew check` run static code analysis and test the application
- `./gradlew shadowJar` builds a fat jar that can be used to run the Dropwizard application
- `./gradlew buildClient` generates the API client code for the Dropwizard application
- `./gradlew publishToMavenLocal` publishes any local artifacts to the local .m2 repository

After you have generated the fat jar you can run your application with java using:
```bash
KUBE_CONFIG_LOCATION=/path/to/kube/config java -jar build/libs/lifecycle-service-1.0.0-SNAPSHOT-all.jar
```
Note: We are using the `KUBE_CONFIG_LOCATION` environment variable to specify the path to a **kubeconfig** `/path/to/kube/config` file in order for **kubectl** to execute successfully.

## API Documentation
The API docs for this project are powered by the Swagger Specification. After starting up the application the available
APIs can be found at `http://localhost:8080/swagger`

Note: The `POST` endpoint requires API bearer token authentication. 
The token value can be configured by providing the `ADMIN_APIKEY` environment variable when running the application. 
The default value is `eknvDrmcDtseeieSMTvngo`

### Using the API
The API has endpoints that allow you to retrieve component upgrade availability and deploy specific **managed component** versions. 

The resulting operations exist as REST endpoints, which you can hit in your browser or with a tool such as [Postman](https://www.postman.com/downloads/).

| HTTP Verb | Endpoint | Payload | Authorization | Function |
| --- | --- | --- | --- | --- |
| GET | /component/version | None | None | Retrieve a list of all **managed components** with upgrade availability |
| GET | /component/{name} | None | None |Retrieve a specific managed component with upgrade availability |
| POST | /component/{name}/version | <code>{"version":"string"}</code> | API bearer token | Deploy a specific version of a managed component |

## API client
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

## Contributing
If you are interested in contributing to this project please review the [contribution guidelines](https://github.com/cratekube/cratekube/blob/master/CONTRIBUTING.md).
Thank you for your interest in CrateKube!
