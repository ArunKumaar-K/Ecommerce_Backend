# Ecommerce_Backend
A secure, RESTful E-Commerce backend application built using Java, Spring Boot, Spring Security, JWT Authentication, Spring Data JPA, and MySQL.

# 🛒 E-Commerce Backend API

A secure, RESTful E-Commerce backend application built using **Java, Spring Boot, Spring Security, JWT Authentication, Spring Data JPA, and MySQL**.

This project provides APIs for product management and user authentication, with role-based access control, request validation, centralized exception handling, and automated testing.

## 📌 Project Overview

The E-Commerce Backend API is designed to provide the server-side functionality of an online shopping application. It follows a layered architecture that separates controllers, business logic, database access, and security responsibilities.

The application exposes REST APIs that can be tested using Postman or integrated with any frontend application.

### Key Highlights

- RESTful API development using Spring Boot
- User registration and authentication
- JWT-based authentication
- Role-based authorization for customers and administrators
- Product CRUD operations
- Input validation and centralized exception handling
- MySQL database integration using Spring Data JPA and Hibernate
- Automated testing using JUnit 5, Mockito, and MockMvc
- Maven-based project management

## 🛠️ Tech Stack

|        Technology       |                Purpose                |
|-------------------------|---------------------------------------|
| Java                    | Programming language                  |
| Spring Boot             | Backend application framework         |
| Spring Web MVC          | REST API development                  |
| Spring Security         | Authentication and authorization      |
| JSON Web Token (JWT)    | Token-based authentication            |
| Spring Data JPA         | Database persistence                  |
| Hibernate               | ORM implementation                    |
| MySQL                   | Relational database                   |
| Jakarta Bean Validation | Request validation                    |
| Maven                   | Dependency and build management       |
| JUnit 5                 | Unit testing                          |
| Mockito                 | Mocking dependencies in tests         |
| MockMvc                 | Controller and HTTP endpoint testing  |
| Postman                 | API testing                           |
| Git & GitHub            | Version control                       |

## ✨ Features

### 1. User Authentication

- User registration
- Password hashing before storage
- Login using email and password
- JWT generation after successful authentication
- JWT signature and expiration validation
- Stateless authentication
- Authentication error handling

### 2. Role-Based Access Control

The application supports two roles:

- `CUSTOMER`
- `ADMIN`

Access permissions are configured as follows:

| Operation                 | CUSTOMER | ADMIN |
|---------------------------|----------|-------|
| View products             | Yes      | Yes   |
| Create products           | No       | Yes   |
| Update products           | No       | Yes   |
| Partially update products | No       | Yes   |
| Delete products           | No       | Yes   |

Protected endpoints require a valid JWT and the appropriate role.

### 3. Product Management

The application supports the following product operations:

- Create a product
- Retrieve all products
- Retrieve a product by ID
- Update a product
- Partially update a product
- Delete a product

### 4. Input Validation

Request validation helps prevent invalid data from entering the application.

Examples include:

- Product name is required.
- Product names cannot exceed 150 characters.
- Product descriptions cannot exceed 1,000 characters.
- Product price must be at least `0.01`.
- Stock quantity cannot be negative.

### 5. Centralized Exception Handling

A global exception handler provides consistent error responses for supported application errors.

Examples include:

- Invalid request data
- Validation failures
- Duplicate email registration
- Authentication failures
- Other handled application exceptions

### 6. Automated Testing

The project includes tests for:

- Product service logic
- Product controller endpoints
- Product authorization rules
- Authentication service behavior
- JWT generation and validation
- Authentication controller responses

Testing technologies include JUnit 5, Mockito, and MockMvc.

## 🏗️ Project Architecture

The application follows a layered backend architecture.

```text
ecommerce/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/arun/ecommerce/
│   │   │       ├── config/
│   │   │       │   └── SecurityConfig.java
│   │   │       ├── controller/
│   │   │       │   ├── AuthController.java
│   │   │       │   └── ProductController.java
│   │   │       ├── dto/
│   │   │       ├── entity/
│   │   │       ├── exception/
│   │   │       ├── repository/
│   │   │       ├── security/
│   │   │       │   ├── JwtAuthenticationFilter.java
│   │   │       │   ├── JwtService.java
│   │   │       │   └── CustomUserDetailsService.java
│   │   │       ├── service/
│   │   │       └── EcommerceApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/arun/ecommerce/
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```


### Request Flow

```text
Client / Postman
       |
       v
REST Controller
       |
       v
Spring Security and JWT Filter
       |
       v
Service Layer
       |
       v
Repository Layer
       |
       v
MySQL Database
```

## 🔌 API Endpoints

Base URL:

```text
http://localhost:8080
```

### Authentication APIs

| Method |        Endpoint      |            Description        | Access |
|--------|----------------------|-------------------------------|--------|
| POST   | `/api/auth/register` | Register a new user           | Public |
| POST   | `/api/auth/login`    | Authenticate and obtain a JWT | Public |

### Product APIs

| Method |        Endpoint      |         Description        |      Access     |
|--------|----------------------|----------------------------|-----------------|
| POST   | `/api/products`      | Create a product           | ADMIN           |
| GET    | `/api/products`      | Retrieve all products      | CUSTOMER, ADMIN |
| GET    | `/api/products/{id}` | Retrieve a product by ID   | CUSTOMER, ADMIN |
| PUT    | `/api/products/{id}` | Update a product           | ADMIN           |
| PATCH  | `/api/products/{id}` | Partially update a product | ADMIN           |
| DELETE | `/api/products/{id}` | Delete a product           | ADMIN           |

## ⚙️ Getting Started

Follow these steps to run the project locally.

### Prerequisites

Install the following:

- Java Development Kit (JDK)
- MySQL Server
- Git
- Postman (recommended for API testing)

Maven is available through the included Maven Wrapper, so a separate Maven installation is generally unnecessary.

### 1. Clone the Repository

```bash
git clone https://github.com/ArunKumaar-K/Ecommerce_Backend.git
cd ecommerce
```

### 2. Create the MySQL Database

Open MySQL and execute:

```sql
CREATE DATABASE ecommerce_db;
```

### 3. Configure Application Properties

Open:

```text
src/main/resources/application.properties
```

Configure your local database connection and JWT settings.

Example:

```properties
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/ecommerce_db}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

app.jwt.secret=${JWT_SECRET}
app.jwt.expiration-ms=900000
```

**Important configuration notes:**

- Set `DB_PASSWORD` to your local MySQL password.
- Set `JWT_SECRET` to a securely generated Base64-encoded secret suitable for your configured signing algorithm.
- Keep your actual credentials and JWT secret out of Git.
- The JWT secret must be compatible with the decoder and signing-key requirements in `JwtService`.
- The example uses a 15-minute token lifetime.
- Confirm that these property names match your actual application configuration before using this example.

For local development, configure environment variables in your IDE or terminal before starting the application.

### 4. Run the Application

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

On Linux or macOS:

```bash
./mvnw spring-boot:run
```

If startup succeeds, the application should be available at:

```text
http://localhost:8080
```

### 5. Run Automated Tests

On Windows:

```powershell
.\mvnw.cmd clean test
```

On Linux or macOS:

```bash
./mvnw clean test
```

Review the Maven output to confirm that the tests pass.

## 🧪 Testing with Postman

You can test the application using Postman.

### Step 1: Register a User

Send a `POST` request to:

```text
http://localhost:8080/api/auth/register
```

Set the request body to JSON using the fields defined in `UserRegisterRequest`.

Example structure:

```json
{
  "name": "Test Customer",
  "email": "customer@example.com",
  "password": "YourTestPassword123"
}
```

*Adjust the field names to match your actual DTO.*

### Step 2: Log In

Send a `POST` request to:

```text
http://localhost:8080/api/auth/login
```

Use the email and password accepted by your login DTO.

A successful login should return an access token.

### Step 3: Access a Protected Endpoint


E-Commerce Backend API
│
├── Products
│   ├── Create Product          POST
│   ├── Get All Products        GET
│   ├── Get Product By ID       GET
│   ├── Update Product          PUT
│   └── Delete Product          DELETE
│
├── Authentication
│   ├── Register User           POST
│   └── Login User              POST
│
└── Security Tests
    ├── Request Without Token
    ├── Request With Invalid Token
    └── Request With Valid Token

Send a `GET` request to:

```text
http://localhost:8080/api/products
```

Add this HTTP header:

```text
Authorization: Bearer YOUR_ACCESS_TOKEN
```

Replace `YOUR_ACCESS_TOKEN` with the token returned by the login API.

### Step 4: Test Authorization

Verify that:

- A customer with a valid token can retrieve products.
- A customer cannot create or modify products.
- An administrator with a valid token can perform permitted product-management operations.
- Requests without valid authentication cannot access protected endpoints.

Use a valid administrator account configured through your application's intended role-management process. Do not assume that public registration should grant administrator privileges.

## 🔒 Security Considerations

The project uses Spring Security and JWT for authentication and authorization.

Security practices include:

- Password hashing before database storage
- Stateless request authentication
- Token signature verification
- Token expiration checks
- Role-based access control
- Request validation
- Avoiding password exposure in API responses

For a production deployment:

- Store secrets in environment variables or a dedicated secrets manager.
- Never commit passwords, database credentials, or signing keys.
- Use HTTPS.
- Avoid logging JWTs, passwords, or sensitive authentication details.
- Review administrator account provisioning.
- Configure production database settings and schema migrations appropriately.

## 🗺️ Future Enhancements

The following features are potential future improvements and are not represented as completed functionality:

- Shopping cart management
- Order placement and order history
- Stock updates during order processing
- Product search, filtering, sorting, and pagination
- Customer profile management
- Swagger/OpenAPI documentation
- Integration testing with a real test database
- Docker containerization
- Deployment to a cloud platform

## 🎯 Project Objectives

This project demonstrates practical experience with:

- Java backend development
- Spring Boot REST API design
- Spring Security and JWT authentication
- Role-based authorization
- Relational database integration
- DTOs and input validation
- Centralized exception handling
- Unit and controller testing
- Maven-based development workflows

## 👨‍💻 Author

**Arun K**

Java Backend Developer | Spring Boot | REST APIs | MySQL | Spring Security | Cyber Security

GitHub: [Add your GitHub profile URL here.](https://github.com/ArunKumaar-K)

LinkedIn: www.linkedin.com/in/arun-k-05074b285
