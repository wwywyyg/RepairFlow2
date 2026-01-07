# RepairFlow — Full-Stack Repair Ticketing Platform

A full-stack repair ticketing platform built with Spring Boot, React, and PostgreSQL, featuring JWT authentication, role-based access control, ticket workflow management, and real-time chat with file uploads.

## Demo (Docker-based)
This project is fully containerized.
You can run the entire system locally with Docker Compose.

```
 docker compose -f docker-compose.prod.yml up -d
```
After startup:

Frontend: http://localhost:3000
Backend API: http://localhost:8080
PostgreSQL: localhost:5435

### Test Account: Admin 
- Email: admin@test.com , Password: 11223344
### Test Account: Employee
- Email: employee@test.com , Password: 11223344
### Test Account: Customer
- Email: customer@test.com , Password: 11223344


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
- JWT Authentication with Spring Security 6
- Role-Based Access Control (ADMIN / EMPLOYEE / CUSTOMER)
- Repair Ticket Workflow (State Machine)
- Real-Time Chat using WebSocket (STOMP)
- Image & File Uploads
- Admin User & Ticket Management
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
- IN_PROGRESS → READY_FOR_COMFIRMATION
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

### DevOps
- Docker
- Docker Compose

Future Improvements
- Payment integration (Stripe)
- Cloud file storage (AWS S3)
- CI/CD pipeline & cloud deployment

## Author
- Guang Yang
