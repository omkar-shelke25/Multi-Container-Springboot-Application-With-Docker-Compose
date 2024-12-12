# Multi-Container Spring Boot Application with Docker Compose

This project demonstrates how to containerize a Spring Boot application with a PostgreSQL database using Docker and Docker Compose. The setup simplifies deployment and environment management.

## Software and System Versions

- **Linux:** Ubuntu 22.04
- **Docker:** 24.0.5
- **Docker Compose:** 2.22.0
- **OpenJDK:** 21
- **PostgreSQL:** 15
- **Spring Boot:** 3.1.3
- **Maven:** 3.9.9
- **Visual Studio Code**

## Project Structure

- [Dockerfile](springboot-compose/Dockerfile): Builds the application image in two stages:
  1. Build dependencies and compile the application.
  2. Create the runtime image.
- [docker-compose.yml](springboot-compose/docker-compose.yml): Orchestrates the Spring Boot application and PostgreSQL database as separate services.
- [Spring Boot Application](springboot-compose): Implements CRUD operations on the database.

---

## Dockerfile

```dockerfile
# Stage 1: Build dependencies
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy the pom.xml and mvnw to utilize Docker's cache and avoid rebuilding dependencies unless they change
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Download dependencies in the first build step to cache dependencies and avoid re-downloading every time
RUN ./mvnw dependency:go-offline

# Copy the source code
COPY src src

# Build the application (in this case, assuming a Spring Boot app)
RUN ./mvnw package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port your app listens on
EXPOSE 8081

# Command to run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Explanation

1. **Multi-Stage Build:** The Dockerfile uses a multi-stage build approach to keep the runtime image lightweight.
2. **Dependency Caching:** Maven dependencies are downloaded and cached in the first stage to speed up future builds.
3. **Runtime Image:** The final runtime image contains only the compiled JAR file and the JDK, reducing size and potential attack surface.

To test the Dockerfile, run:

```bash
docker build -t springboot-app .
```

---

## Docker Compose File

```yaml
version: '3.8'

services:
  app:
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - "8081:8081"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/employee_db
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: postgres
    depends_on:
      - db

  db:
    image: postgres:15
    container_name: postgres
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
      POSTGRES_DB: employee_db
    ports:
      - "5433:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

### Explanation

1. **Services:**
   - **`app`**: Builds the Spring Boot application from the provided Dockerfile and maps port `8081`.
   - **`db`**: Uses the official PostgreSQL 15 image, exposing it on port `5433`.
2. **Environment Variables:** Pass database connection details to the Spring Boot app via environment variables.
3. **Volumes:** Persist PostgreSQL data to ensure it remains intact across container restarts.
4. **`depends_on`**: Ensures the database container starts before the application container.

To build and run the setup:

```bash
docker-compose up --build
```

---

### Verifying the Application

After running the containers, you can check the running containers using:

```bash
docker ps
```

This command will show two containers running: one for the Spring Boot application and one for PostgreSQL. Both will be connected to each other.

You can access the Spring Boot application at [http://localhost:8081](http://localhost:8081).

### Testing the Application with Postman

Use the following queries to test the Spring Boot application:

1. **Get All Employees**  
   **Request**: `GET`  
   **URL**: `http://localhost:8081/api/employees`  

2. **Get Employee by ID**  
   **Request**: `GET`  
   **URL**: `http://localhost:8081/api/employees/{id}`  

3. **Create Employee**  
   **Request**: `POST`  
   **URL**: `http://localhost:8081/api/employees`  
   **Body** (JSON):
   ```json
   {
     "firstName": "Emily",
     "lastName": "Davis",
     "email": "emily.davis@example.com",
     "department": "Sales"
   }
   ```

4. **Update Employee by ID**  
   **Request**: `PUT`  
   **URL**: `http://localhost:8081/api/employees/{id}`  
   **Body** (JSON):
   ```json
   {
     "firstName": "Emily",
     "lastName": "Davis",
     "email": "emily.davis.updated@example.com",
     "department": "Marketing"
   }
   ```

5. **Delete Employee by ID**  
   **Request**: `DELETE`  
   **URL**: `http://localhost:8081/api/employees/{id}`

---

## Steps to Run the Project

1. **Clone the Repository:**

   ```bash
   git clone <repository-url>
   cd <repository-folder>
   ```

2. **Build and Run with Docker Compose:**

   ```bash
   docker-compose up --build
   ```

3. **Access the Application:**

   - Application: [http://localhost:8081](http://localhost:8081)
   - Database: Accessible on port `5433`.

4. **Verify Application Logs:**

   ```bash
   docker logs <container-id>
   ```

---

## Conclusion

This project showcases an efficient way to containerize and manage a Spring Boot application with a PostgreSQL database using Docker and Docker Compose. The setup is lightweight, scalable, and ideal for local development or small-scale deployments.

--- 

Let me know if you need any further adjustments or additions!