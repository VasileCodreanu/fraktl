## Fraktl: High-Performance URL Shortener

### 🚀 Features

- Shorten long URLs into compact short links
- Redirect from short links to original URLs

### 🛠 Tech Stack

- **Java 21**
- **Maven**
- **Lombok**
- **Spring Boot 3.5**
  (Web, Validation, Data)
- **Postgres**
- **Flyway (DB migrations)**
- **Docker**

### ⚙️ Running the Application Locally

#### 1. Prerequisites
- Ensure Docker is installed and running on your machine.
- Rename the provided file `env.properties.example` to `env.properties`, and configure the environment variables according to your system setup.
- Rename the provided file `.env.example` to `.env`, and configure the environment variables for docker according to your system setup.

#### 2. Start service
- Run the following command to start `url-shortener-service`:
  ```bash
    make stage-up
  ```

#### 3. Test the Application
-  Use the example API requests located in the `api-request` directory to verify that the service is functioning correctly.
   Refer to the `README.md` file in the same directory for detailed instructions.

#### 4. Stop service
- Run the following command to stop `url-shortener-service`:
  ```bash
    make stage-down
  ```
---

#### 5. Running Tests
Project includes both unit-tests and integration-tests.
  You can run them as:
- Run Unit Tests Only:
    - Option 1: The standard Maven command for running tests in the 'test' phase
    ```bash 
        mvn clean test
    ```
  
    - Option 2: Using the 'verify' phase and explicitly skipping integration tests
    ```bash 
        mvn clean verify -DskipIntegrationTests=true
    ```
  
- Run Unit Integration-tests Only:
  ```bash
    mvn clean verify -DskipUnitTests=true
  ```
  
- Run All tests:
  ```bash
    mvn clean verify
  ```

