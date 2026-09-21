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
- **Flyway migrations**

## Tech Stack

| Layer       | Technology                   |
| ----------- | ---------------------------- |
| Language    | Java 17                      |
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
├── security/              # SecurityConfig, JwtService, JwtAuthFilter, SecurityUser
├── enums/                 # Role, RequestStatus, FeatureCode, PaymentCode, SourceType
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

Access control is enforced in two complementary ways:

- **`@PreAuthorize`** on controller methods, combining role checks (`hasRole(...)`) with ownership checks in SpEL (e.g. `#id == authentication.principal.user.id`, so a user can only read or edit their own profile).
- **Service-level ownership checks** where ownership can only be determined after loading the record — deleting a review, an error report or a location request, and reading a single location request.

Resources are always created for the authenticated caller: `POST` bodies carry no user id, and the owner is taken from the JWT principal. It is therefore not possible to submit a review, an error report or a location request on another user's behalf.

## Getting Started

### Prerequisites

- Docker

### Setup

1. Clone the repository

   ```bash
   git clone https://github.com/stekarls/OsloToilet.git
   cd OsloToilet
   ```

2. (optional) create .env file and set these variables yourself or use default
   ```
   JWT_SECRET_KEY=<a long, random, base64-encoded secret>
   DB_USERNAME=<username>
   DB_PASSWORD=<password>
   
   ```

4. Run docker compose command

   ```docker
   docker compose up
   ```

The API will be available at `http://localhost:8080`.

## API Overview

| Resource                | Base path                                     |
| ----------------------- | --------------------------------------------- |
| Auth                    | `/api/v1/auth`                                |
| Users                   | `/api/v1/users`                               |
| Toilets                 | `/api/v1/toilets`                             |
| Opening hours           | `/api/v1/toilets/{toiletId}/opening-hours`    |
| Toilet features         | `/api/v1/toilets/{toiletId}/features`         |
| Toilet payment options  | `/api/v1/toilets/{toiletId}/payment-options`  |
| Features (reference)    | `/api/v1/features`                            |
| Payment options (ref.)  | `/api/v1/payment-options`                     |
| Reviews                 | `/api/v1/reviews`                             |
| Error reports           | `/api/v1/error-reports`                       |
| Location requests       | `/api/v1/location-requests`                   |

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

Example: an admin approves a location request, which creates the toilet

```http
PATCH /api/v1/location-requests/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "requestStatus": "APPROVED",
  "adminComment": "Verified on site"
}
```

Both fields are optional — omitting one leaves the current value untouched. Error reports are updated the same way via `PATCH /api/v1/error-reports/{id}` with `status` and `adminComment`.

## Roadmap

- [ ] Refresh tokens
- [ ] Toke revocation
- [ ] Pagination on list endpoints
- [ ] OpenAPI / Swagger documentation
- [ ] Unit and integration test coverage
- [ ] Rate limiting on auth endpoints
- [ ] Database call optimizations
- [ ] CORS
- [ ] MODERATOR role implementation
- [ ] Forgot Password

## License

This project is for educational/portfolio purposes.
