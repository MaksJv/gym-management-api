# 🏋️‍♂️ Gym Training Management System

[![Java Version](https://img.shields.io/badge/Java-25-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk25-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Build-Maven-blue.svg)](https://maven.apache.org/)

A robust **Spring Boot REST API** designed for comprehensive gym ecosystem management. This system facilitates seamless coordination between trainees and trainers, scheduling workouts, and managing user profiles with high-performance in-memory storage.

---

## 🌟 Key Features

* **User Management**: Full Lifecycle CRUD for system users.
* **Specialized Profiles**: Distinct logic for **Trainees** (fitness goals, health metrics) and **Trainers** (specializations, experience).
* **Smart Scheduling**: Create and track training sessions with duration and type validation.
* **Relationship Mapping**: Dynamic assignment of trainers to specific trainees.
* **Data Integrity**: Strict input validation using `Jakarta Bean Validation`.
* **Database Migrations**: Automated schema and initial data management powered by **Liquibase**.
* **Containerization**: Full Docker & Docker Compose setup for production-like deployment in one command.

---

## 🏗️ Architecture & Design

The project follows a classic **Layered Architecture** to ensure separation of concerns and maintainability:

-   **Controller Layer**: Handles REST endpoints and HTTP status codes.
-   **Service Layer**: Encapsulates business logic and DTO mapping.
-   **Repository Layer**: Low-level data access (In-memory implementation).
-   **DTO Pattern**: Ensures secure data transfer without exposing internal entities.



---

## 🛠 Tech Stack

* **Language:** Java 25
* **Framework:** Spring Boot 4.x
* **Database:** PostgreSQL 16+
* **Migrations:** Liquibase
* **ORM / JPA:** Hibernate / Spring Data JPA
* **Containerization:** Docker, Docker Compose
* **Tooling:** Lombok, Maven
* **Quality Assurance:** Checkstyle, PMD, SpotBugs

---

## 🚀 Getting Started

### Prerequisites
* **Docker Desktop** (recommended for the quickest setup)
* Alternatively, **JDK 25** and **Maven 3.9+** (for running without Docker)

---

### 🐳 Option 1: Run with Docker Compose (Recommended)

This method spins up both the **PostgreSQL** database and the **Spring Boot REST API** in isolated containers.

1. **Clone the repository:**
```bash
   git clone https://github.com/MaksJv/gym-training-app.git
   cd gym-training-app
```

2. **Start the application & database:**
```bash
   docker compose up --build
```

ℹ️ **Note:** Liquibase will automatically create all required database tables and insert initial seed data.

3. **Access the API:**
    - Base URL: `http://localhost:8080`
    - Health Check: `http://localhost:8080/actuator/health`

4. **Stop the containers:**
```bash
   docker compose down
```

To wipe the database volume and perform a clean reset, use:
```bash
   docker compose down -v
```

---

### 💻 Option 2: Run Locally (Local JDK & PostgreSQL)

If you prefer running the application directly from your IDE (e.g., IntelliJ IDEA) or command line:

1. Ensure PostgreSQL is running locally on port `5432` with a database named `gym` (or configure your connection parameters in `application.properties`).

2. **Build the project:**
```bash
   mvn clean install
```

3. **Run the application:**
```bash
   mvn spring-boot:run
```

## 📡 API Documentation

### 🔐 Authentication & Registration
| Method | Endpoint | Description |
|:---:|:---|:---|
| `POST` | `/api/trainees/register` | Register a new trainee (returns credentials) |
| `POST` | `/api/trainers/register` | Register a new trainer (returns credentials) |
| `POST` | `/api/login` | System authentication |

### 👤 Trainee Operations
| Method | Endpoint | Description |
|:---:|:---|:---|
| `GET` | `/api/trainees/profile/{username}` | Get full trainee profile & trainers list |
| `PUT` | `/api/trainees/{username}` | Update profile (name, DOB, address, status) |
| `DELETE` | `/api/trainees/{username}` | Soft delete trainee profile |
| `GET` | `/api/trainees/{username}/trainings` | Get list of trainings with filters |
| `GET` | `/api/trainees/{id}/trainers` | List all trainers assigned to trainee |

### 👨‍🏫 Trainer Operations
| Method | Endpoint | Description |
|:---:|:---|:---|
| `GET` | `/api/trainers/profile/{username}` | Get trainer profile & assigned trainees |
| `PUT` | `/api/trainers/{username}` | Update trainer profile (name, status) |
| `GET` | `/api/trainers/{username}/trainings` | Get list of trainings with filters |
| `GET` | `/api/trainers/{id}/trainees` | List all trainees assigned to trainer |

### 🏋️ Trainings & Management
| Method | Endpoint | Description |
|:---:|:---|:---|
| `POST` | `/api/trainings` | Schedule a new fitness session |
| `GET` | `/api/trainings/{id}` | Fetch specific session details |
| `GET` | `/api/trainings?traineeId={id}` | Get all sessions for a specific trainee |
| `GET` | `/api/trainings?trainerId={id}` | Get all sessions for a specific trainer |
| `GET` | `/api/training-types` | List all available gym categories |

### 🛠️ System Utility
| Method | Endpoint | Description |
|:---:|:---|:---|
| `GET` | `/api/health` | Service health status check |

---

## 🧪 Testing & Quality Assurance

### Postman Integration
The project includes a comprehensive **Postman Collection** with automated test scripts.

1.  **Import files** from the `/postman` folder:
   * `Api.postman_collection.json`
   * `Gym Training Enviroment.postman_environment.json`
2.  **Select "Local" environment** in the top-right corner.
3.  **Run Collection**: Use the *Collection Runner* to execute all tests automatically.

> **Tests included:** Status code validation, JSON schema validation, and response latency checks.

---

## 📂 Project Directory Structure
```text
src/main/java/com/gymtraining/application/
├── config/       # Application Configuration (Beans, CORS, etc.)
├── controller/   # REST API Endpoints (Handling HTTP requests)
├── dto/          # Data Transfer Objects (Request/Response models)
├── exception/    # Global Exception Handling & Custom Errors
├── mapper/       # Object Mapping (Converting Entities to DTOs)
├── model/        # Domain Entities (Core data structures)
├── repository/   # Data Access Layer (In-memory storage implementation)
├── service/      # Business Logic (Core app functionality)
├── util/         # Utility Classes (Helper methods, etc.)
└── GymTrainingAppApplication.java  # Main Application Entry Point
```
---

<p align="center">
  Developed by <a href="https://github.com/MaksJv">Maksym Kornenko</a><br>
  <b>EPAM Learning Course Project • 2026</b>
</p>