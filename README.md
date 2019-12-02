# Invillia recruitment challenge

[![Build Status](https://travis-ci.org/shelsonjava/invillia.svg?branch=master)](https://travis-ci.org/shelsonjava/invillia)

![Invillia Logo](https://invillia.com/public/assets/img/logo-invillia.svg)
[Invillia](https://https://www.invillia.com/) - A transformação começa aqui.

The ACME company is migrating their monolithic system to a microservice architecture and you’re responsible to build their MVP (minimum viable product)  .
https://en.wikipedia.org/wiki/Minimum_viable_product

Your challenge is:
Build an application with those features described below, if you think the requirements aren’t detailed enough please leave a comment (portuguese or english) and proceed as best as you can.

You can choose as many features you think it’s necessary for the MVP,  IT’S NOT necessary build all the features, we strongly recommend to focus on quality over quantity, you’ll be evaluated by the quality of your solution.

If you think something is really necessary but you don’t have enough time to implement please at least explain how you would implement it.

## Tasks

Your task is to develop one (or more, feel free) RESTful service(s) to:
* Create a **Store**
* Update a **Store** information
* Retrieve a **Store** by parameters
* Create an **Order** with items
* Create a **Payment** for an **Order**
* Retrieve an **Order** by parameters
* Refund **Order** or any **Order Item**

Fork this repository and submit your code with partial commits.

## Business Rules

* A **Store** is composed by name and address
* An **Order** is composed by address, confirmation date and status
* An **Order Item** is composed by description, unit price and quantity.
* A **Payment** is composed by status, credit card number and payment date
* An **Order** just should be refunded until ten days after confirmation and the payment is concluded.

## Non functional requirements

Your service(s) must be resilient, fault tolerant, responsive. You should prepare it/them to be highly scalable as possible.

The process should be closest possible to "real-time", balancing your choices in order to achieve the expected
scalability.

## Nice to have features (describe or implement):
* Asynchronous processing - Should be implemented using Kafka for message queues. 
    The idea is to make the dependency between Order and Payment microservices as decoupled
    as possible. The payments are processed by the Payment microservice, and it may signal by
    messaging kafka topic related to payments updates. The Order microservice may listen to the payments
    updates topic and update the status of the Order to reflect payment status for the orders.
* Database - In this implementation MariaDB was used for each microservice, but it may be better to use some NoSQL database depending on other requirements. Also, it may be necessary to restructure the microservices to put together the ones that requires microservices calling each other frequently, if that happens with some requirements changes.
* Docker - In this implementation docker is being used with docker-compose configuration to make sure all of the dependencies are up when necessary. There is a configuration (JHIPSTER_SLEEP) that sets to 120 seconds the wait time for the spring boot applications to start, making sure the environment is ready.
* AWS - AWS should be used to deploy the current configuration, or even change it to use EKS providing a kubernetes configuration that allows scalability. Other AWS services that should be used are: RDS instead of pure MariaDB instances,  Amazon Kinesis for the payment processing using messaging streams.
* Security - The project was configured to use JWT, but should be setup to use Oauth2 or even a provider like Okra.
* Swagger - The project uses swagger for proper API documentation, it can be accessed at the Jhipster Registry page from the administration menu->API.
* Clean Code - There's some refactoring that should be made to make sure we have more separation of concernes and a cleaner code base.

## Instructions to build and run the system
To build the system use the following command on the root of the project:
```bash
$ ./mvnw -Pprod verify jib:dockerBuild
```

To run it using docker:
```bash
$ cd docker-compose
$ docker-compose up -d
```

To check and follow logs:
```bash
$ docker-compose logs -f
```

## TODOs
* The cancellation/refund call between the Order microservice and the Payment microservice was not properly implemented. It should use some async implementation as messaging queues for example or an async REST call.