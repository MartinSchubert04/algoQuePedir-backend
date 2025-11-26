# AlgoQuePedir Backend

[![master build](https://github.com/algo3-unsam/algo-que-pedir-backend-2025-grupo9/actions/workflows/build.yml/badge.svg)](https://github.com/algo2-unsam/tp-algo-que-pedir-grupo13/actions/workflows/build.yml)
[![codecov](https://github.com/algo3-unsam/algo-que-pedir-backend-2025-grupo9/blob/main/.github/badges/jacoco.svg)](https://github.com/algo3-unsam/algo-que-pedir-backend-2025-grupo9/blob/main/.github/badges/jacoco.svg)

A backend application built with Kotlin and Spring Boot, designed to support the AlgoQuePedir food ordering platform.
It provides user management, authentication, product listings, order processing, and integration with the frontend client.

## Features

- REST API built with Spring Boot  
- User registration and authentication  
- Product and category management  
- Order creation and tracking  
- Layered architecture with services, controllers, and repositories  
- Database persistence using Spring Data JPA  
- Centralized error handling  
- Dependency injection and clean code structure

## Tech Stack

- Language: Kotlin  
- Framework: Spring Boot  
- Dependencies: Spring Web, Spring Data JPA, Spring Validation, Security (if applicable)  
- Database: (Specify the one you are using: PostgreSQL, MySQL, H2, etc.)  
- Build Tool: Gradle  
- Testing: JUnit (if applicable)

## Project Structure

```bs
src/
 └── main/
     ├── kotlin/
     │    └── com.algoquepedir
     │         ├── controller/     # REST controllers
     │         ├── service/        # Business logic
     │         ├── repository/     # Data access layer
     │         ├── model/          # Entities and DTOs
     │         └── AlgoQuePedirApplication.kt
     └── resources/
          ├── application.properties
          └── data.sql (optional)
```


## Getting Started

### Prerequisites

- JDK 17 or later  
- Gradle  

## API Endpoints (Example)

| Method | Endpoint       | Description            |
|--------|----------------|------------------------|
| GET    | /products      | Get all products       |
| POST   | /auth/register | Register new user      |
| POST   | /auth/login    | User login             |
| POST   | /orders        | Create a new order     |

