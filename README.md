# user-device-api
This is the user-device API responsible for users and devices management.

### Summary
This API has endpoints to create a User, Device, assign a Device to User, list Users with pagination & sorting and create a Device.
<br>
It uses H2 as a database, Flyway for migrations and Swagger-UI to make testing easier.

### Requirements
Java 17,  IDE

### Running the application
1. Clone the project and open it in your preferred IDE.
2. Make sure all the Gradle dependencies are loaded.

After the application starts, you will be able to  see the Swagger documentation at the following URL:
http://localhost:8080/user-device-api/swagger-ui/index.html

### Stack
* Java 17
* Spring Boot 3
* H2
* Flyway
* Gradle
