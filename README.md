# OsloToilet

A REST API for discovering, reviewing, and reporting public toilets in Oslo — built with Spring Boot, PostgreSQL, and Spring Security.

## Overview

OsloToilet lets users find public toilets, see their features (wheelchair access, baby changing, etc.), payment options, and opening hours, leave reviews, and report issues. Community contributions (features, payment options, new locations) go through a suggest-and-verify workflow, moderated by trusted users.

## Features

- **Browse & search** public toilets with location, fees, conditions, and seasonal/24-7 status
- **Reviews** — rate cleanliness, equipment, and accessibility
- **Features & payment options** — crowdsourced and moderator-verified
- **Opening hours** per day of week
- **Error reporting** for outdated or incorrect toilet info
- **Location requests** — users suggest new toilets; admins review and approve
- **JWT authentication** with role-based access control (`USER`, `MODERATOR`, `ADMIN`)
- **Contribution points** leaderboard

## Tech Stack

| Layer       | Technology                   |
| ----------- | ---------------------------- |
| Language    | Java 22                      |
| Framework   | Spring Boot 4                |
| Security    | Spring Security + JWT (jjwt) |
| Persistence | Spring Data JPA / Hibernate  |
| Database    | PostgreSQL                   |
| Validation  | Jakarta Bean Validation      |
| Build tool  | Maven                        |

## Architecture

The project follows a layered architecture, with each resource organized as its own package:

```
com.app.oslotoilet
├── auth/                  # Registration, login, JWT issuance
├── security/              # JwtService, JwtAuthFilter, UserPrincipal
├── config/                # SecurityConfig and other Spring configuration
├── exception/             # Global exception handling
├── user/                  # User management
├── toilet/                # Core toilet resource
├── feature/                    # Reference table: available features
├── toiletFeature/               # Join table: toilet ↔ feature
├── paymentOption/               # Reference table: available payment methods
├── toiletPaymentOption/          # Join table: toilet ↔ payment option
├── openingHours/           # Per-day opening hours
├── review/                 # User reviews
├── errorReport/            # User-reported issues
└── locationRequest/        # User-submitted new toilet suggestions
```

Each resource package typically contains:

- **Entity** — JPA-mapped database model
- **Repository** — Spring Data JPA interface
- **Service** — business logic, transaction boundaries
- **Controller** — REST endpoints
- **DTOs** — request/response objects, decoupled from the entity

## Security Model

Authentication is handled via **stateless JWT** — no server-side sessions. Tokens are signed with HMAC-SHA256 and carry the user's ID as the subject.

**Roles:**
| Role | Can do |
|---|---|
| `USER` | Browse, review, suggest features/payment options, submit location/error reports |
| `MODERATOR` | Everything a `USER` can, plus verify contributed features/payment options |
| `ADMIN` | Full control — create/delete toilets, manage reference data, approve location requests, manage users |

Access control is enforced with `@PreAuthorize`, combining role checks (`hasRole(...)`) with ownership checks (e.g. a user can only edit their own profile or delete their own review).

## Getting Started

### Prerequisites

- Java 22+
- Maven
- PostgreSQL

### Setup

1. Clone the repository

   ```bash
   git clone https://github.com/<your-username>/OsloToilet.git
   cd OsloToilet
   ```

2. Create a PostgreSQL database and update `application.properties` with your connection details.

3. Set the required environment variable for JWT signing:

   ```bash
   JWT_SECRET=<a long, random, base64-encoded secret>
   ```

4. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

The API will be available at `http://localhost:8080`.

## API Overview

| Resource        | Base path                 |
| --------------- | ------------------------- |
| Auth            | `/api/v1/auth`            |
| Users           | `/api/v1/users`           |
| Toilets         | `/api/v1/toilets`         |
| Features        | `/api/v1/features`        |
| Payment options | `/api/v1/payment-options` |
| Reviews         | `/api/v1/reviews`         |

Example: register and get a token

```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "nickname": "kkuni",
  "email": "kkuni@example.com",
  "password": "supersecret123"
}
```

Then include the returned token on subsequent requests:

```
Authorization: Bearer <token>
```

## Roadmap

- [ ] Flyway database migrations
- [ ] Refresh tokens
- [ ] Pagination on list endpoints
- [ ] OpenAPI / Swagger documentation
- [ ] Unit and integration test coverage
- [ ] Rate limiting on auth endpoints

## License

This project is for educational/portfolio purposes.
