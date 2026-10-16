# Spring Boot Notification Microservice

A highly resilient, asynchronous, event-driven notification service built to handle email communications across a distributed microservices architecture.

This service operates completely decoupled from the rest of the system. It listens for cross-service domain events via RabbitMQ, processes custom Thymeleaf HTML templates, and fires emails asynchronously. It relies on the API Gateway for identity extraction rather than a heavyweight internal Spring Security configuration.

## 🚀 Key Features

* **Event-Driven Consumption:** Listens to a shared RabbitMQ Topic Exchange (`user-exchange`) to capture lifecycle events like registrations, password resets, and account deactivations.
* **Asynchronous Processing (`@Async`):** Acknowledges RabbitMQ messages instantly to free up queues, passing the actual SMTP network handshake to a background worker thread.
* **Automated Retry Resilience (`@Scheduled`):** A self-healing background cron job sweeps the database every 5 minutes to detect and resend `FAILED` notifications caused by SMTP timeouts or network drops.
* **HTML Templating:** Uses Thymeleaf to inject dynamic variables (like reset links and user names) into branded, professional HTML email layouts.
* **Header-Based Security:** Fully offloads JWT validation to the API Gateway. Endpoints are secured by checking `X-User-Email` and `X-User-Roles` headers injected by the Gateway.
* **Mailtrap Integration:** Configured for safe local development using Mailtrap to prevent ISP SMTP blocking on ports 465/587.

## 🛠️ Tech Stack

* **Runtime:** Java 21 (LTS)
* **Framework:** Spring Boot 3.4.2
* **Messaging:** RabbitMQ (Spring AMQP)
* **Email:** JavaMailSender (Jakarta Mail) + Thymeleaf
* **Database:** MySQL 8 (Dockerized) & Spring Data JPA
* **Build Tool:** Gradle 8.5+

## 🛤️ API Endpoints

| Method | Endpoint | Access | Header Requirement | Description |
|---|---|---|---|---|
| `GET` | `/api/v1/notifications/my-notifications` | Protected | `X-User-Email` | Retrieves the authenticated user's notification history. |
| `GET` | `/api/v1/notifications` | Admin Only | `X-User-Roles: ROLE_ADMIN` | Retrieves global notification history. |
| `GET` | `/api/v1/notifications/{id}` | Admin Only | `X-User-Roles: ROLE_ADMIN` | Retrieves a specific notification by ID. |
| `POST` | `/api/v1/notifications` | Admin Only | `X-User-Roles: ROLE_ADMIN` | Manually creates and fires a notification. |

## 🏗️ Supported RabbitMQ Events
The service dynamically routes HTML generation based on the `eventType` payload:
* `USER_REGISTERED`
* `PASSWORD_RESET_REQUESTED`
* `PASSWORD_CHANGED`
* `ACCOUNT_DEACTIVATED`