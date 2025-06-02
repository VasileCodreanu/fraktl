## Fraktl: High-Performance URL Shortener

### 🚀 Features

- Shorten long URLs into compact short links
- Redirect from short links to original URLs

### 🛠 Tech Stack

- **Java 21**
- **Maven**
- **Lombok**
- **Spring Boot 3.5**
  - Spring Web
  - Spring Validation
  - Spring Data
- **Postgres**
- **Flyway (DB migrations)**
- **Docker**

### ⚙️ Running the Application Locally

#### 1. Prerequisites
- Ensure Docker is installed and running on your machine.
- Rename the provided file `env.properties.example` to `env.properties`, and configure the environment variables according to your system setup.

#### 2. Start PostgreSQL with Docker
- Run the following command to start the PostgreSQL database using Docker Compose:
  ```bash
    docker compose up --build
  ```
#### 3. Run a Spring Boot application

#### 4. Test the Application
- Use the API request examples provided in the `api-request` directory to test service functionality.
