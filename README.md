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


## Screenshots
- Auth form & JWT entry point
![Auth Page](Screenshots/AuthComponent.png)


## Dashboard / Ticket List
- Role-based navigation + ticket status
![Dashboard Page](screenshots/DashboardPage.png)

## Ticket Detail + Real-Time Chat
- Ticket info + WebSocket chat + image upload
![Ticket Details Page](screenshots/TicketDetails.png)

## Admin User Management
- Role update panel
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

## Ticket Workflow
PENDING → ASSIGNED → QOUTED → AWAITING_DEVICE → DEVICE_RECEIVED → IN_PROGRESS → READY_FOR_COMFIRMATION → PAID → SHIPPED → DELIVERED

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
