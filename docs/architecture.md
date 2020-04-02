# Lifecycle Service Architecture Design
## Overview    
The `lifecycle-service` will be the primary initiator of all platform components. 
It is responsible for tracking the lifecycle of all platform components and handling service creation, update, and teardown. 
The service will provide a bootstrap mode in which it is responsible for creating the `Operations Cluster` and should be able to reconfigure itself as needed to be aware of newly updated and created components. 
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

![Component and Logical](https://www.plantuml.com/plantuml/img/bL8xRy8m4DxpAquWTRDA5rGn85IZJC00weGONEU8BFn0zeMgglhVwn0IjS4gz1Rx_3ryv_CiaF1K6PsH8esmK7kK7pBkDkXFIY8muCuSdKLBy00BLoefDCoLABqm6JHn5F8WIeG1rwugO2bip4mjkTWCuBFb0cnbpMAcDCH2W-zWE1hbCLhMLqVll5T8xyuVO4FEnuDVbh2r3Sf2W-uwBE5qrMNFIxVuZ_Bo_6NCd_vovReAXFvMztNxPsB_0VPOFB-bRaZLmzWq3Ko0hr_dTsZJLVn3VZt3kyNzOTLAZu8mF1QeCQxP36qH_-Kt)  

<details><summary>Show UML Code</summary>
<p>
  
```
@startuml
title Lifecycle Service - Component & Logical Diagram
       package "Cloud Management Service" {
         [cloud-mgmt-service] #00FFFF
       }
        package "Network Storage" {
          [network-storage]
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
        [lifecycle-service] -->  [network-storage] : Creates/deletes
@enduml
```
  
</p>
</details>

#### Physical

![Physical](https://www.plantuml.com/plantuml/img/jP8zJyCm48Rt_8eZB23LJM50Y06r3QqOmeTK98QgW-DSKmj_HFQbLONwtt6Q5bKK40ktsBbxyjxhi_kUkADQg-X46g7g0c51LFcR975yrIqbBUgyzkY4DOI6kiBgXA-yuhblLYP3mREvTQnIt3HAYUQUq2M5z9GCNYUfrTgQRlL_JHwHLjQzILCEJ96s6dUev1BTEXbbmq5LAIauIMioouiX3o82NMk992c4JCvHh4NOJT0jfK1WyBnO-_2RWZl9IyTr99Ij2nYbb_0UGOYVfS-uoZjnau5zAkx4Bza4HeRd2eluVAiGC61EbWjYjp1zgj1jfJoagcxEDuCRSBOcz5lSs32w-1YBTuQQgBXuusN0TZLuu2Pywp2J_TaEyIQceaMPBZKntq8pE9rZSPrVVAARxteq4_bJXm1-rU_rnY5QuhSkHvTp9A_sn87wU3oyIdzrFnfE8BFD3TzR7xoJTI5_96xQvyLyMvzzzRg9Db4VJH5UqmS0)  

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
        package "EBS Local Host Storage" {
            [network-storage] 
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
