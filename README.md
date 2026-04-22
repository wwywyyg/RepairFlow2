# RepairFlow — Full-Stack Repair Ticketing Platform

## Highlights

- Backend-focused full-stack application using Java (Spring Boot)
- Designed and implemented 15+ RESTful APIs (HTTP + JSON)
- Real-time chat system using WebSocket (STOMP)
- CI/CD pipeline with GitHub Actions (build, test, deploy)
- Dockerized deployment on AWS EC2 with Cloudflare integration
- Role-based access control with JWT authentication
- End-to-end testing with Playwright covering real user workflows and real-time features

A production-style full-stack repair platform built with Java (Spring Boot) and React, featuring scalable APIs, real-time communication, and CI/CD deployment.

## Demo (Docker-based)

Fully containerized application. Run the entire system locally using Docker Compose:

```
 docker compose -f docker-compose.prod.yml up -d
```

After startup:

Frontend: http://localhost:3000
Backend API: http://localhost:8080
PostgreSQL: localhost:5435

## Test Account Info

```
| Role     | Test Account        | Password |
|----------|---------------------|----------|
| Admin    | admin@test.com      | 11223344 |
| Employee | employee@test.com   | 11223344 |
| Customer | customer@test.com   | 11223344 |
```

## Architecture Overview

- Backend: Spring Boot (REST APIs, WebSocket, JWT Security)
- Frontend: React (role-based UI, API integration via Axios)
- Database: PostgreSQL (managed with Flyway migrations)
- Deployment: Dockerized services running on AWS EC2
- Networking: Cloudflare for DNS, SSL/TLS, and public access
- API Communication: REST (HTTP/JSON) + WebSocket (STOMP)

## CI/CD Pipeline

Implemented CI/CD pipelines using GitHub Actions:

- Automated build, test, and deployment workflows on each code push
- Ensured consistent and repeatable release processes
- Reduced manual deployment errors and improved system reliability
- Integrated backend tests (JUnit) and frontend E2E tests (Playwright) into CI pipeline
- Ensures API correctness, UI workflows, and real-time features are validated on every push

## Testing

Implemented a layered testing strategy across the application:

- Unit tests for service layer using JUnit and Mockito to validate business logic and edge cases
- Integration tests for controller layer using Spring Boot Test and MockMvc to verify API behavior and HTTP responses
- Repository tests to validate database queries, pagination, and filtering logic
- Security tests using @WithMockUser to verify authentication and role-based authorization (401/403 scenarios)
- End-to-end API validation using Postman

Implemented end-to-end UI testing using Playwright (Python) as part of CI pipeline:

- Automated browser-based tests simulating real user interactions (login, ticket creation, status updates)
- Designed multi-role workflow tests (Customer ↔ Employee interaction)
- Validated real-time chat functionality using WebSocket (STOMP)
- Tested file upload and attachment rendering in chat system
- Used storage state to persist authentication and reduce redundant login steps
- Structured tests using Page Object Model (POM) for maintainability and reuse

These tests ensure system reliability, prevent regressions, and validate behavior across all application layers.

## End-to-End Workflow Testing (Playwright)

Example automated workflow:

1. Customer creates a repair ticket
2. Employee claims the ticket
3. Employee updates ticket status (quote, repair progress)
4. Customer confirms and updates status
5. Real-time chat interaction between Customer and Employee
6. Image upload and attachment validation in chat

These workflows simulate real-world usage scenarios and validate:

- Role-based behavior
- State transitions
- Real-time communication (WebSocket)
- UI consistency across multiple user sessions

## Screenshots

Auth form & JWT entry point
![Auth Page](screenshots/AuthComponent.png)

## Dashboard / Ticket List

Role-based navigation + ticket status
![Dashboard Page](screenshots/DashboardPage.png)

## Ticket Detail + Real-Time Chat

Ticket info + WebSocket chat + image upload
![Real Time Chat](screenshots/RealTimeChat.png)

## Admin User Management

Role update panel
![Role Management ](screenshots/AdminRoleManagement.png)

## Key Features

- RESTful APIs with Spring Boot
- JWT Authentication with Spring Security
- Role-Based Access Control (ADMIN / EMPLOYEE / CUSTOMER)
- Real-Time Chat using WebSocket (STOMP)
- Repair Ticket Workflow (State Machine)
- Image & File Uploads
- Flyway Database Migrations
- Dockerized Full-Stack Setup

## Ticket Status Workflow

PENDING → ASSIGNED → QUOTED → AWAITING_DEVICE → DEVICE_RECEIVED → IN_PROGRESS → READY_FOR_COMFIRMATION → PAID → SHIPPED → DELIVERED

### Customer-operable status

- QUOTED → AWAITING_DEVICE
- READY_FOR_COMFIRMATION → PAID
- SHIPPED → DELIVERED

### Employee-operable status

- PENDING → ASSIGNED
- ASSIGNED → QUOTED
- AWAITING_DEVICE → DEVICE_RECEIVED
- DEVICE_RECEIVED → IN_PROGRESS
- IN_PROGRESS → READY_FOR_CONFIRMATION
- PAID → SHIPPED

## Tech Stack

### Frontend

- React 18
- React Router
- Bootstrap / React-Bootstrap
- Axios

### Backend

- Spring Boot 3
- Spring Security + JWT
- Spring Data JPA
- WebSocket (STOMP)
- Flyway

### Database

- PostgreSQL 16

### Testing

- JUnit 5
- Mockito
- Spring Boot Test (MockMvc)
- Postman

See test implementations in:

- src/test/java/org/repairflow/repairflowa

### DevOps

- Docker
- Docker Compose
- GitHub Actions (CI/CD)

## Future Improvements

- Payment integration (Stripe)
- Cloud file storage (AWS S3)

## Author

- Guang Yang
