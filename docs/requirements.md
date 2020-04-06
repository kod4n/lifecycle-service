# Lifecycle Service Design Requirements
## Introduction  
The following requirements are intended to provide guidance and structure for implementing a Lifecycle Service. 
Each requirement has been identified as an essential part of the architecture and must be incorporated to maximize value to administrators and customers.

## Scope  
These requirements are scoped to encompass both business and technical requirements for a Lifecycle Service.  

## Requirements  
### Independent component  
![Generic badge](https://img.shields.io/badge/BUSINESS-MVP-GREEN.svg)  
As a user I want the Lifecycle Service to be an independent component,
so that I can version functionality and features, 
because functionality and features need to be incrementally added.

### Management of CrateKube components  
![Generic badge](https://img.shields.io/badge/BUSINESS-MVP-GREEN.svg)  
As a user I want the Lifecycle Service to manage CrateKube components, 
so that components can be created and updated, 
because the platform cannot operate without the correct components.  

#### Automated component creation
![Generic badge](https://img.shields.io/badge/TECHNICAL-MVP-GREEN.svg)  
As a user I want the Lifecycle Service to create components in an automated fashion, 
so that I can create components easily and without errors, 
because manual creation is difficult and error prone.

#### Automated component upgrades  
![Generic badge](https://img.shields.io/badge/TECHNICAL-MVP-GREEN.svg)  
As a user I want the Lifecycle Service to upgrade components in an automated fashion, 
so that I can upgrade components easily and without errors, 
because manual upgrade is difficult and error prone. 

#### Expose component upgrade availability
![Generic badge](https://img.shields.io/badge/BUSINESS-MVP-GREEN.svg)  
As a user I want the Lifecycle Service to expose component upgrade availability, 
so that I can be aware of upgrades, 
because I want to know of the latest bug fixes and features.  

#### Allow component upgrades to be applied
![Generic badge](https://img.shields.io/badge/BUSINESS-MVP-GREEN.svg)  
As a user I want the Lifecycle Service to allow component upgrades to be applied, 
so that my components stay up to date, 
because I want to receive the latest bug fixes and features.  

##### Upgrade component on schedule 
![Generic badge](https://img.shields.io/badge/TECHNICAL-POSTMVP-YELLOW.svg)  
As a user I want the Lifecycle Service to upgrade components on schedule, 
so that I am able to choose when upgrades are applied, 
because I do not want components upgraded without my knowledge.  

##### Upgrade component on release 
![Generic badge](https://img.shields.io/badge/TECHNICAL-POSTMVP-YELLOW.svg)  
As a user I want the Lifecycle Service to upgrade components on release, 
so that my components are always up to date, 
because I want to receive the latest bug fixes and features.  

### Async for long running tasks  
![Generic badge](https://img.shields.io/badge/TECHNICAL-MVP-GREEN.svg)  
As a user, I want long running tasks to be handled asynchronously, 
so that cloud resources have enough time to be created, 
because creating cloud resources could take a long time and clients will timeout waiting for a synchronous response.  

### Security  
![Generic badge](https://img.shields.io/badge/BUSINESS-MVP-GREEN.svg)  
As a user, I want the Lifecycle Service to be secure, 
so that my components are protected, 
because without security components may be manipulated by unauthorized users.   

#### token_authc and token_authz  
![Generic badge](https://img.shields.io/badge/TECHNICAL-MVP-GREEN.svg)  
As a user, I want token authentication and authorization implemented at runtime, 
so that REST resources are protected, 
because without security resources may be manipulated by unauthorized users.  

## Decisions made during requirements gathering  
The following decisions were made during requirements gathering:  

- `The lifecycle service will be treated as a component itself.`
