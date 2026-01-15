# University SaaS Blueprint

A comprehensive, production-ready microservices-based University Management System built with Spring Boot, Spring Cloud, and Domain-Driven Design (DDD) principles.

## 🏗️ Architecture Overview

This project implements a complete microservices architecture with:

- **9 Microservices** (User, Course, Grades, Billing, Library, Notification + Infrastructure)
- **Service Discovery** (Eureka)
- **API Gateway** (Spring Cloud Gateway)
- **Event-Driven Architecture** (Kafka)
- **Caching** (Redis)
- **Database Per Service** (PostgreSQL)

## 📋 Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Deployment](#deployment)
- [Monitoring](#monitoring)

## Features  ✨

### Core Services

#### 1. **User Management Service** (Port 8081)

- User registration and authentication
- JWT-based security
- Role-based access control
- Password encryption (BCrypt)

#### 2. **Course & Enrollment Service** (Port 8082)

- Course management (CRUD)
- Student enrollment
- Department-based filtering
- Instructor assignment

#### 3. **Grades Service** (Port 8083)

- Grade management for enrolled courses
- Multiple grade types (Assignment, Quiz, Midterm, Final, Project)
- Average grade calculation
- Grade history tracking

#### 4. **Billing & Payments Service** (Port 8084)

- Invoice generation
- Payment processing
- Multiple payment methods
- Payment history
- Invoice status tracking

#### 5. **Library Service** (Port 8085)

- Book catalog management
- Book borrowing and returns
- Search by title, author, category
- Availability tracking

#### 6. **Notification Service** (Port 8086)

- Email notifications
- SMS notifications (framework ready)
- Event-driven notifications via Kafka
- Notification history

### Infrastructure Services

#### 7. **Service Discovery** (Port 8761)

- Eureka Server for service registration
- Dynamic service discovery
- Health monitoring

#### 8. **API Gateway** (Port 8080)

- Single entry point for all services
- Request routing
- Load balancing

## 🛠️ Technology Stack

### Core Technologies

- **Java**: 17+
- **Spring Boot**: 3.2.3
- **Spring Cloud**: 2023.0.0
- **Maven**: 3.8+

### Frameworks & Libraries

- **Spring Data JPA**: Database access
- **Spring Security**: Authentication & Authorization
- **Spring Cloud Gateway**: API Gateway
- **Spring Cloud Netflix Eureka**: Service Discovery
- **Spring Kafka**: Event-driven messaging
- **Spring Mail**: Email notifications
- **JWT (jjwt)**: Token-based authentication
- **Lombok**: Reduce boilerplate code
- **PostgreSQL**: Relational database
- **Redis**: Caching layer

### Infrastructure

- **Docker**: Containerization
- **Docker Compose**: Multi-container orchestration
- **Kafka + Zookeeper**: Message broker
- **Redis**: In-memory cache

## 🏛️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    API Gateway (8080)                        │
│              Single Entry Point for All Clients              │
└────────────────────┬────────────────────────────────────────┘
                     │
        ┌────────────┴────────────┐
        │                         │
┌───────▼────────┐       ┌───────▼────────┐
│ Service        │       │  Microservices │
│ Discovery      │◄──────┤  Register &    │
│ (Eureka:8761)  │       │  Discover      │
└────────────────┘       └────────────────┘
                                │
        ┌───────────────────────┼───────────────────────┐
        │                       │                       │
┌───────▼────────┐  ┌──────────▼─────────┐  ┌─────────▼────────┐
│ User Service   │  │ Course Service     │  │ Grades Service   │
│ (8081)         │  │ (8082)             │  │ (8083)           │
│ + user_db      │  │ + course_db        │  │ + grades_db      │
└────────┬───────┘  └────────┬───────────┘  └──────────┬───────┘
         │                   │                          │
         │         ┌─────────▼──────────┐              │
         │         │   Kafka Broker     │              │
         └────────►│   Event Bus        │◄─────────────┘
                   └─────────┬──────────┘
                             │
         ┌───────────────────┼───────────────────────┐
         │                   │                       │
┌────────▼───────┐  ┌────────▼──────────┐  ┌────────▼────────┐
│ Billing Svc    │  │ Library Service   │  │ Notification    │
│ (8084)         │  │ (8085)            │  │ Service (8086)  │
│ + billing_db   │  │ + library_db      │  │ + notif_db      │
└────────────────┘  └───────────────────┘  └─────────────────┘
         │                   │                       │
         └───────────────────┴───────────────────────┘
                             │
                   ┌─────────▼──────────┐
                   │   Redis Cache      │
                   │   (6379)           │
                   └────────────────────┘
```

## 🚀 Getting Started

### Prerequisites

- **Java 17+** installed
- **Maven 3.8+** installed
- **Docker** and **Docker Compose** installed
- **Git** installed

### Clone the Repository

```bash
git clone <repository-url>
cd university-saas
```

### Build the Project

```bash
mvn clean install -DskipTests
```

### Run with Docker Compose

```bash
docker-compose up --build
```

This will start:

- All 9 microservices
- 6 PostgreSQL databases
- Kafka + Zookeeper
- Redis
- Eureka Server
- API Gateway

### Access the Services

| Service              | URL                   | Description      |
| -------------------- | --------------------- | ---------------- |
| Eureka Dashboard     | http://localhost:8761 | Service registry |
| API Gateway          | http://localhost:8080 | Main entry point |
| User Service         | http://localhost:8081 | Direct access    |
| Course Service       | http://localhost:8082 | Direct access    |
| Grades Service       | http://localhost:8083 | Direct access    |
| Billing Service      | http://localhost:8084 | Direct access    |
| Library Service      | http://localhost:8085 | Direct access    |
| Notification Service | http://localhost:8086 | Direct access    |

## 📚 API Documentation

### User Service APIs

#### Register User

```http
POST /api/users
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@university.edu",
  "password": "SecurePass123"
}
```

#### Login

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "john.doe@university.edu",
  "password": "SecurePass123"
}

Response:
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": { ... }
  }
}
```

### Course Service APIs

#### Create Course

```http
POST /api/courses
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Introduction to Computer Science",
  "description": "Fundamentals of programming",
  "credits": 3,
  "department": "Computer Science",
  "instructorId": 1
}
```

#### Enroll Student

```http
POST /api/enrollments
Authorization: Bearer <token>
Content-Type: application/json

{
  "courseId": 1,
  "studentId": 2
}
```

### Grades Service APIs

#### Submit Grade

```http
POST /api/grades
Authorization: Bearer <token>
Content-Type: application/json

{
  "enrollmentId": 1,
  "studentId": 2,
  "courseId": 1,
  "score": 95.5,
  "maxScore": 100,
  "gradeType": "MIDTERM",
  "comments": "Excellent work"
}
```

### Billing Service APIs

#### Create Invoice

```http
POST /api/invoices
Authorization: Bearer <token>
Content-Type: application/json

{
  "studentId": 2,
  "dueDate": "2024-12-31",
  "items": [
    {
      "description": "Tuition Fee - Fall 2024",
      "quantity": 1,
      "unitPrice": 5000.00
    }
  ]
}
```

#### Process Payment

```http
POST /api/payments
Authorization: Bearer <token>
Content-Type: application/json

{
  "invoiceId": 1,
  "studentId": 2,
  "amount": 5000.00,
  "paymentMethod": "CREDIT_CARD"
}
```

### Library Service APIs

#### Add Book

```http
POST /api/books
Authorization: Bearer <token>
Content-Type: application/json

{
  "isbn": "978-0-13-468599-1",
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "category": "Software Engineering",
  "totalCopies": 5
}
```

#### Borrow Book

```http
POST /api/borrow-records
Authorization: Bearer <token>
Content-Type: application/json

{
  "bookId": 1,
  "userId": 2,
  "dueDate": "2024-12-31"
}
```

## 🐳 Deployment

### Docker Compose Deployment

1. **Build all services:**

```bash
mvn clean package -DskipTests
```

2. **Start all services:**

```bash
docker-compose up -d
```

3. **View logs:**

```bash
docker-compose logs -f <service-name>
```

4. **Stop all services:**

```bash
docker-compose down
```

### Production Deployment

For production deployment, consider:

1. **Use environment-specific profiles:**

    - Create `application-prod.yml` for each service
    - Set `SPRING_PROFILES_ACTIVE=prod`

2. **Externalize configuration:**

    - Use Spring Cloud Config Server
    - Store secrets in HashiCorp Vault or AWS Secrets Manager

3. **Use managed services:**

    - AWS RDS for PostgreSQL
    - AWS MSK for Kafka
    - AWS ElastiCache for Redis

4. **Implement monitoring:**
    - Add Prometheus metrics
    - Set up Grafana dashboards
    - Configure ELK stack for logging

## 📊 Monitoring

### Health Checks

Each service exposes health endpoints:

```http
GET /actuator/health
```

### Eureka Dashboard

Monitor all registered services at:

```
http://localhost:8761
```

## 🔒 Security

- **JWT Authentication**: All protected endpoints require valid JWT tokens
- **Password Encryption**: BCrypt hashing for user passwords
- **HTTPS**: Configure SSL/TLS for production
- **API Rate Limiting**: Implement using Spring Cloud Gateway filters

## 🧪 Testing

### Run Unit Tests

```bash
mvn test
```

### Run Integration Tests

```bash
mvn verify
```

## 📝 Project Structure

```
university-saas/
├── shared-kernel/          # Common utilities and base classes
├── service-discovery/      # Eureka Server
├── api-gateway/            # Spring Cloud Gateway
├── user-service/           # User management
├── course-service/         # Course and enrollment
├── grades-service/         # Grades management
├── billing-service/        # Billing and payments
├── library-service/        # Library management
├── notification-service/   # Notifications (Email/SMS)
├── docker-compose.yml      # Docker orchestration
└── pom.xml                 # Parent POM
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request


## 👥 Authors

- **edsonwade** - Initial work

## 🙏 Acknowledgments

- Spring Boot team for excellent documentation
- Domain-Driven Design community
- Microservices patterns community

---

**Built with ❤️ using Spring Boot and Microservices Architecture**


## License ⚖️
This project is licensed under the [MIT License](https://opensource.org/licenses/MIT).



