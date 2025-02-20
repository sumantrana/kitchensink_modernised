##  Modernised 'kitchensink' : A demo of App Modernization Process

### Technologies Used

#### Frontend

* Thymeleaf

#### Backend

* Spring Boot (3.3.8)
* Spring Data JPA (for relation DB)
* Spring Data Mongo (for Mongo DB)
* Spring Boot Actuator
* H2 Database
* Devtools
* Prometheus Integration
* Testcontainers
* Docker Compose

### Pre-requisites

* JDK 21
* Docker

### Build and Deploy
* Open a terminal and clone the repository.

   `git clone https://github.com/sumantrana/kitchensink_modernised.git`

* Type the following command to build the code
    
    `mvn clean package`
* The unit and integration tests will be run as a part of the above command. 
* Type the following command to execute the code
    
    `mvn spring-boot:run`

* Spring Boot deploys the code on an embedded tomcat server.

### Access the application

* The application can be accessed at the following url: http://localhost:8083/kitchensink/.
* The actuator can be accessed at the following url: http://localhost:8083/kitchensink/actuator
* The prometheus data can be accessed at the following url: http://localhost:8083/kitchensink/actuator/prometheus

### MongoDB Integration

* Pull all branches from git

  `git pull --all`

* Checkout the mongo-integration branch

  `git checkout mongo-integration`

* Build the code. The tests will be run against a mongo container. Ensure docker is up and running.

  `mvn clean install`

* Run the code

  `mvn spring-boot:run`

* Access the application at the following url: http://localhost:8083/kitchensink/

