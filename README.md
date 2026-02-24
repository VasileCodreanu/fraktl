## Fraktl: High-Performance URL Shortener

### 🚀 Features

- Shorten long URLs into compact short links
- Redirect short links to original URLs
- JWT-based authentication (Keycloak)
- Database versioning with Flyway
- Fully Dockerized local/stage environment
- Automated Keycloak realm configuration

### 🛠 Tech Stack

- **Java 21**
- **Maven**
- **Lombok**
- **Spring Boot 3.5**
  (Web, Validation, Data)
- **Postgres**
- **Flyway (DB migrations)**
- **Keycloak (OAuth2 / OpenID Connect)**
- **Docker & Docker Compose**

### 🔐 Authentication & Authorization

The application uses Keycloak as an OAuth2 Authorization Server.
- JWT tokens are issued by Keycloak
- Protected endpoints require a valid Bearer token
- Realm configuration is automatically imported during container startup
- No manual Keycloak setup required

#### Token Flow
1. Client requests access token from Keycloak
2. Keycloak validates credentials
3. Keycloak issues signed JWT
4. Client calls protected API with: Authorization: Bearer <access_token>

---

### ⚙️ Running the Application Locally

#### 1. Prerequisites

### 1️⃣ Prerequisites

- Docker installed and running
- Rename:
  - `env.properties.example` → `env.properties`
  - `.env.example` → `.env`
- Configure environment variables according to your system(if needed)

#### 2. Start service

- Run the following command to start `url-shortener-service`:
  ```bash
    make stage-up
  ```

#### 3. Test the Application

- Use the example API requests located in the `api-request` directory to verify that the service is
  functioning correctly.
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

- Run **unit-tests** only:
    - Option 1: The standard Maven command for running tests in the 'test' phase
    ```bash
        ./mvnw clean test
    ```

    - Option 2: Using the 'verify' phase and explicitly skipping integration tests
    ```bash 
        ./mvnw clean verify -DskipIntegrationTests=true
    ```

- Run **integration-tests** only:
  ```bash
    ./mvnw clean verify -DskipUnitTests=true
  ```

- Run **all** tests:
  ```bash
    ./mvnw clean verify
  ```

