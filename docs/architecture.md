# Lifecycle Service Architecture Design
## Overview    
The `lifecycle-service` is responsible for tracking the lifecycle of all platform components and performing component creation and upgrade where necessary. 
The service should be able to reconfigure itself as needed to be aware of newly updated and created components. 
It will interact directly with the cloud-mgmt-service and the cluster-mgmt-service to create cloud resources and Kubernetes clusters.  

The MVP targets AWS and provides automated lifecycle management for CrateKube. 
Authentication will be set at runtime using a shared token, to be superseded by more robust authentication in the future.  

## Components 
`The following architecture is comprised of multiple components.`  

#### lifecycle-service  
The primary service in this architecture. See [Overview](#overview) for roles and responsibilities.  

#### cloud-mgmt-service  
A microservice developed by the CrateKube team responsible for provisioning and monitoring cloud resources and services. 

#### cluster-mgmt-service  
A microservice developed by the CrateKube team responsible for providing everything needed for creating, monitoring and deleting a Kubernetes cluster and configuring it post-bootstrap.   

## Diagrams
#### Component and Logical

![Component and Logical](https://www.plantuml.com/plantuml/img/bPAz2i8m58NtFiMbWbj05n47AQPqgelY90unlIt1f2rfAeZuxgQ_Y2rIptH3ldFk8SGh3DEcLD8nmaY4KCJ87znEHzHtmH48q4pbMOgfWJc4MI8ua-0BbcYc76YKCtvZ2O9BPLP6i6UfFQdAqiQuyEpOMcTUWKGbof2YGIumMom2gnvyzLEVF-Wt29OTyP4R1BiLNVxAfR8i3EffpMlqktkCqVewjcEi4I7RyOiDqCF9d-0TR_67VVZyhTd3DB8VuWq0)  

<details><summary>Show UML Code</summary>
<p>
  
```
@startuml
title Lifecycle Service - Component & Logical Diagram
       package "Cloud Management Service" {
            [cloud-mgmt-service] #00FFFF
        }
        package "Lifecycle Service" {
            [lifecycle-service] #FFB6C1
        }
        package "Cluster Management Service" {
            [cluster-mgmt-service] #fed8b1
        }
        [lifecycle-service] -->  [lifecycle-service] : CRUD
        [lifecycle-service] -->  [cluster-mgmt-service] : CRUD
        [lifecycle-service] -->  [cloud-mgmt-service] : CRUD
@enduml
```
  
</p>
</details>

#### Physical

![Physical](https://www.plantuml.com/plantuml/img/jP4xRy8m483t_8fhkhGI9hH3LJ5KG80fVKXKwe0wEFQHMFWHsGu8LVptEWGg16tLfRVuzVcxytdWFBM-LZAw49h9cYEKtbUkdoIMhhk5y6MTrmujCzgZzbrcL4BNJjBSDICHuS2HXR6aabGtfaHHvz4cN3YV3DzcgL7Aw6xrVqcUqA-DNGfT33LFMySOfLYXtIIJR3IiAYaOzSBep50-ea72qBObUU4bGYOMo3Oip6PeLu8X47WfDovSap0MjB1KHKoQciCalOMF24ByhBwYCk_4pGuSjk9E_9hDOA9fBf77rpj3m81OKPIUFC3ykKAxKpb8PTsyxntwO4tjqUrmRKBhudCitXVKH4NPaXO1sv_18zLXhK9F3cyxnfkOinObgb2Uk1PwXyi5yllyvWlTVfUZwSYVCWJmrD_hJKEqn6zPpZxdJ2yFn95zEXtTfQVLRwDjD43Dmyz_0W00)  

<details><summary>Show UML Code</summary>
<p>
  
```
@startuml
!include https://raw.githubusercontent.com/awslabs/aws-icons-for-plantuml/master/dist/AWSCommon.puml
!include https://raw.githubusercontent.com/awslabs/aws-icons-for-plantuml/master/dist/NetworkingAndContentDelivery/ELBApplicationLoadBalancer.puml
title Lifecycle Service - Physical Diagram
cloud EC2 {
    ELBApplicationLoadBalancer(alb,"Load Balancer","TLS Enabled")
    alb -right-> [Operations Cluster] : routes
    node "Operations Cluster" {
        package "Cluster Management Service" {
            [cluster-mgmt-service] #fed8b1
        }
        package "Cloud Management Service" {
            [cloud-mgmt-service] #00FFFF      
        }
        package "Lifecycle Service" {
            [lifecycle-service] #FFB6C1
        }
    }
}
@enduml
```
  
</p>
</details>

#### Security 

![Security](https://www.plantuml.com/plantuml/img/dL9D2y8m3BtlLmIzMj2BY0SHbPqK3nvLf7PnAwxTQ5D1n_wxzMFW1sFYEoNfoslooDWID-HK6f2a564k3oZEmaShD2Sf49YFX3EIpZ2JF3PS1JgB45hB70qdcMaBqzHPGjE28W2Fd8iZZptbMOS5rpvNgAcIhQWthCM3nbIiYDa7OQCBoeweTznHSYctq3vWDDZtxbtb-pTVGx-ffzLhlNGOJlhJL-aVcXWR_JZv45uAeWC9KMxBJwSo5pNxv4aDBdMxeSTVgpIAronI1cFKaR-XAm00)  

<details><summary>Show UML Code</summary>
<p>
  
```
@startuml
title Lifecycle Service - Security Diagram
node "Operations Cluster" {
    package "Cluster Management Service" {
        [cluster-mgmt-service\n{token_authz}] #fed8b1
    }
    package "Lifecycle Service" {
        [lifecycle-service\n{token_authz}] #FFB6C1
    }
    [lifecycle-service\n{token_authz}] --> [cluster-mgmt-service\n{token_authz}] : {token_authc, https}
    package "Cloud Management Service" {
        [cloud-mgmt-service\n{token_authz}] #00FFFF
    }
    [lifecycle-service\n{token_authz}] --> [cloud-mgmt-service\n{token_authz}] : {token_authc, https}
}
@enduml
```
  
</p>
</details>
