# Price Calculation Microservice

A REST microservice for calculating Test Purchase prices with support for customer-specific pricing, country-based rates, category multipliers, and type-based adjustments.

## Overview

This microservice provides a comprehensive pricing engine that calculates test purchase fees based on:
- Customer-specific base fees (stored in database)
- Country-specific default rates (Germany, Austria, etc.)
- Category multipliers (S, M, L, XL)
- Service type multipliers (Forwarding to Client, Return Back to Seller)
- Postage fees based on package category

**Used by:** [Test Purchase Application](https://github.com/mitetodb/test-purchase-application) — live at https://testkauf.getinweb.de (this service is internal only, not publicly exposed).

## Architecture
```
 test-purchase app ──HTTP + X-API-Key──> pricing-svc :8081  (/api/pricing/calculate, /api/pricing/customers/...)
   (Feign client)     internal docker network        │  Spring Boot 3, JPA
                                                      ▼
                                                 MySQL 8.4 (pricedb)
 /actuator/health is open (container healthcheck); everything under /api requires X-API-Key.
```

## Quick local run
```bash
export PRICING_API_KEY=$(openssl rand -hex 24) DB_PASSWORD=...   # never commit secrets
docker compose up -d --build        # no host port by default: add ports ["127.0.0.1:8081:8081"] in an override; or: ./mvnw spring-boot:run with DB_URL/DB_USERNAME/DB_PASSWORD set
scripts/ci.sh                       # tests + secret scan
curl -H "X-API-Key: $PRICING_API_KEY" -H 'Content-Type: application/json' \
  -d '{"customerId":"00000000-0000-0000-0000-000000000001","country":"GERMANY","category":"M","type":"FORWARDING_TO_CLIENT","productTotal":100}' \
  http://localhost:8081/api/pricing/calculate
```

## Deployment (Hetzner, with the app)
Production runs inside the test-purchase-application compose stack (`deploy/docker-compose.prod.yml`), built from this repo
(`PRICING_BUILD_CONTEXT`), with no published ports, a `mem_limit`, and the API key from the root-only `deploy/.env`.
See the app README / `deploy/server-setup.sh` for the full procedure.

## Features

- 🧮 **Dynamic Price Calculation** - Calculate prices with multiple factors (customer, country, category, type)
- 💾 **Customer-Specific Pricing** - Store and retrieve custom base fees per customer
- 🌍 **Country-Based Rates** - Default pricing for different countries
- 📦 **Category Support** - Pricing tiers for S, M, L, XL package categories
- 🔄 **Service Type Handling** - Different multipliers for forwarding vs. return services
- 🐳 **Docker Support** - Containerized deployment with Docker Compose
- 🏥 **Health Monitoring** - Actuator endpoints for health checks
- 📊 **Logging & Monitoring** - AOP-based logging for performance tracking
- ✅ **Comprehensive Testing** - 48+ unit and integration tests

## Technology Stack

- **Java** 17
- **Spring Boot** 3.4.0
- **Spring Data JPA** - Database persistence
- **MySQL** - Production database
- **H2** - In-memory database for testing
- **Maven** - Build tool
- **Lombok** - Reduced boilerplate code
- **Docker** - Containerization

## Prerequisites

- Java 17 or higher
- Maven 3.6+ (or use included Maven Wrapper)
- MySQL 8.0+ (for production) or use Docker Compose
- Docker & Docker Compose (optional, for containerized deployment)

## Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/mitetodb/price-calculation-microservice.git
cd price-calculation-microservice
```

### 2. Configure Database

#### Option A: Local MySQL Database

Update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/testpurchase_rest_db?useSSL=false&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

#### Option B: Docker Compose (Recommended)

```bash
docker-compose up -d
```

This will start:
- MySQL database on port 3307
- Spring Boot application on port 8080

### 3. Build the Application

```bash
# Using Maven Wrapper (recommended)
./mvnw clean install

# Or using Maven directly
mvn clean install
```

### 4. Run the Application

```bash
# Using Maven Wrapper
./mvnw spring-boot:run

# Or using Maven
mvn spring-boot:run

# Or run the JAR directly
java -jar target/price-calculation-microservice-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8081/api` (default port 8081 with `/api` context path).

## Configuration

### Application Properties

Key configuration in `application.properties`:

```properties
# Server Configuration
server.port=8081
server.servlet.context-path=/api

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/testpurchase_rest_db
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update

# Actuator Configuration
management.endpoints.web.exposure.include=health,info,env,metrics
management.endpoint.health.show-details=always

# Demo Mode (optional)
pricing.demo-mode=false
```

### Environment Variables

You can override configuration using environment variables:

```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/mydb
export SPRING_DATASOURCE_USERNAME=user
export SPRING_DATASOURCE_PASSWORD=pass
export PRICING_DEMO_MODE=true
```

## API Documentation

### Base URL

All endpoints are prefixed with `/api`

### Endpoints

#### 1. Calculate Price

Calculate test purchase price based on customer, country, category, and type.

**POST** `/api/pricing/calculate`

**Request Body:**
```json
{
  "customerId": "550e8400-e29b-41d4-a716-446655440000",
  "country": "GERMANY",
  "category": "M",
  "type": "FORWARDING_TO_CLIENT",
  "productTotal": 99.99
}
```

**Response:**
```json
{
  "testPurchaseFee": 187.50,
  "postageFee": 15.00,
  "productPrice": 99.99,
  "currency": "EUR"
}
```

#### 2. Create Customer Base Fee

Create a custom base fee for a customer.

**POST** `/api/customers/{customerId}/base-fee`

**Request Body:**
```json
{
  "baseServiceFee": 200.00,
  "changedAt": "2024-01-15T10:30:00"
}
```

**Response:** `201 Created`

#### 3. Update Customer Base Fee

Update an existing customer base fee.

**PUT** `/api/customers/{customerId}/base-fee`

**Request Body:**
```json
{
  "baseServiceFee": 250.00,
  "changedAt": "2024-01-15T11:00:00"
}
```

**Response:** `200 OK`

#### 4. Delete Customer Base Fee

Delete a customer's custom base fee.

**DELETE** `/api/customers/{customerId}/base-fee`

**Response:** `204 No Content`

#### 5. Health Check

Check application health status.

**GET** `/api/health/details`

**Response:**
```json
{
  "status": "UP",
  "engineRules": "country multipliers + category fees + FX currency table loaded",
  "timestamp": 1705312800000
}
```

## Pricing Calculation Logic

The price calculation follows this formula:

```
Test Purchase Fee = Base Rate × Category Multiplier × Type Multiplier
```

### Base Rate

1. **Customer-Specific Rate** (if exists in database)
2. **Country Default Rate** (if no customer rate):
   - Germany: €150.00
   - Austria: €160.00
   - Default: €150.00

### Category Multipliers

| Category | Multiplier | Description |
|----------|------------|-------------|
| S        | 1.00       | Standard (no change) |
| M        | 1.25       | +25% |
| L        | 1.50       | +50% |
| XL       | 1.75       | +75% |

### Type Multipliers

| Type | Multiplier | Description |
|------|------------|-------------|
| FORWARDING_TO_CLIENT | 1.00 | Standard (no change) |
| RETURN_BACK_TO_SELLER | 1.50 | +50% |

### Postage Fees

| Category | Postage Fee |
|----------|-------------|
| S        | €10.00 |
| M        | €15.00 |
| L        | €20.00 |
| XL       | €30.00 |

### Example Calculation

**Request:**
- Customer ID: Custom fee of €100.00
- Country: GERMANY
- Category: M (1.25 multiplier)
- Type: RETURN_BACK_TO_SELLER (1.50 multiplier)
- Product Total: €10.00

**Calculation:**
- Base Rate: €100.00 (customer-specific)
- Test Purchase Fee: €100.00 × 1.25 × 1.50 = €187.50
- Postage Fee: €15.00 (category M)
- **Total: €187.50 + €15.00 = €202.50**

## Testing

### Run All Tests

```bash
./mvnw test
```

### Run Specific Test Class

```bash
./mvnw test -Dtest=PriceCalculationServiceTest
```

### Test Coverage

The project includes comprehensive test coverage:
- **48+ tests** covering all services, controllers, and repositories
- Unit tests for business logic
- Integration tests for API endpoints
- Repository tests with in-memory H2 database

### Test Configuration

Tests use an in-memory H2 database configured in `src/test/resources/application-test.properties`.

## Docker Deployment

### Build Docker Image

```bash
docker build -t price-calculation-microservice .
```

### Run with Docker Compose

```bash
docker-compose up -d
```

This will:
1. Start MySQL database container
2. Build and start the Spring Boot application
3. Create database schema automatically
4. Expose application on port 8080

### Access Application

- Application: `http://localhost:8080/api`
- Health Check: `http://localhost:8080/api/health/details`

### Stop Services

```bash
docker-compose down
```

To remove volumes (database data):

```bash
docker-compose down -v
```

## Project Structure

```
src/
├── main/
│   ├── java/com/
│   │   ├── config/              # Configuration classes
│   │   │   ├── ActionLoggingAspect.java
│   │   │   └── InfoContributorConfig.java
│   │   ├── controller/          # REST controllers
│   │   │   ├── CustomerBaseFeeController.java
│   │   │   ├── HealthController.java
│   │   │   └── PriceCalculationController.java
│   │   ├── exception/           # Exception handlers
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── model/               # Entity models and DTOs
│   │   │   ├── CustomerBaseFee.java
│   │   │   ├── PriceCalculationRequest.java
│   │   │   ├── PriceCalculationResponse.java
│   │   │   └── dto/
│   │   ├── repository/          # JPA repositories
│   │   │   └── CustomerBaseFeeRepository.java
│   │   ├── service/             # Business logic
│   │   │   ├── ContractPricingService.java
│   │   │   ├── CustomerBaseFeeService.java
│   │   │   └── PriceCalculationService.java
│   │   └── PriceCalculationMicroserviceApplication.java
│   └── resources/
│       └── application.properties
└── test/
    ├── java/com/                # Test classes
    └── resources/
        └── application-test.properties
```

## Monitoring & Logging

### Actuator Endpoints

- `/api/actuator/health` - Health status
- `/api/actuator/info` - Application information
- `/api/actuator/metrics` - Application metrics
- `/api/actuator/env` - Environment variables

### Logging

The application includes AOP-based logging:
- **Before**: Logs incoming controller requests
- **After**: Logs successful responses
- **Performance**: Measures service method execution time
- **Errors**: Logs exceptions with stack traces

Log levels are configurable in `application.properties`.

## Configuration & Secrets

No secrets are committed. All credentials come from environment variables; copy `.env.example` to `.env` (gitignored) and fill it in.

| Variable | Purpose | Default |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `demo` (local) or `prod` (server) | `demo` |
| `DB_URL` | JDBC URL | local MySQL (demo only) |
| `DB_USERNAME` / `DB_PASSWORD` | DB credentials | empty (required in `prod`) |
| `PRICING_API_KEY` | Shared secret, sent as `X-API-Key` header | empty = check disabled; **required in `prod`** (startup fails without it) |
| `PRICING_DEFAULT_BASE_RATE` | Base fee for countries without an explicit rate | `150.00` |
| `PRICING_COUNTRY_BASE_RATES_<COUNTRY>` | Base fee per country, e.g. `..._AUSTRIA=160.00` | GERMANY 150, AUSTRIA 160 |
| `JPA_DDL_AUTO` | Hibernate schema mode | `update` |
| `SPRINGDOC_SWAGGER_UI_ENABLED` | Swagger UI in `prod` | `false` |

Base rate resolution: customer contract fee (DB, `/customers/{id}/base-fee`) → configured country rate (case-insensitive) → `pricing.default-base-rate`.

### API key

Every request except `/api/actuator/health`, `/api/actuator/info`, `/api/v3/api-docs` and `/api/swagger-ui` needs `X-API-Key: $PRICING_API_KEY`, otherwise `401`. Configure the same key in test-purchase-application.

### OpenAPI

- Spec: `GET /api/v3/api-docs`
- Swagger UI: `/api/swagger-ui.html` (enabled in `demo`; off in `prod` unless `SPRINGDOC_SWAGGER_UI_ENABLED=true`)

## Local CI

```bash
scripts/ci.sh            # secret scan + mvn clean verify
scripts/ci.sh --docker   # ... plus docker build
```
Requires Java 17 (and Maven or the wrapper). No GitHub Actions are used.

## Deployment

The service is **internal only**: it must never be published to the internet. test-purchase-application reaches it at `http://pricing-svc:8081/api` on a private Docker network.

1. On the server, create `.env` from `.env.example` with fresh random values (`openssl rand -base64 32`), `chmod 600 .env`, owned by root. Never commit it.
2. `docker compose up -d --build` – `pricing-svc` and `price-db` join the `internal` network only; no ports are published, the DB is never exposed.
3. In the combined production stack, attach `pricing-svc` to the same internal network as the app and pass `PRICING_API_KEY` to both.
4. The image runs as a non-root user, uses the `prod` profile and has a Docker `HEALTHCHECK` on `/api/actuator/health`. Containers have `mem_limit`s.
5. Back up the `db_data` volume (e.g. `mysqldump` via `docker compose exec price-db`) before upgrades.

## Development

### Code Style

- Java 17 features (switch expressions, records support)
- Spring Boot best practices
- RESTful API design
- Layered architecture (Controller → Service → Repository)

### Adding New Features

1. **New Endpoint**: Add method to appropriate controller
2. **Business Logic**: Add/update service classes
3. **Database Changes**: Update entity classes and run migrations
4. **Tests**: Add corresponding unit and integration tests

## Troubleshooting

### Database Connection Issues

- Verify MySQL is running: `mysql -u root -p`
- Check connection string in `application.properties`
- Ensure database exists or `createDatabaseIfNotExist=true` is set

### Port Already in Use

Change the port in `application.properties`:
```properties
server.port=8082
```

### Schema Not Created

Ensure `spring.jpa.hibernate.ddl-auto=update` is set in `application.properties`.

## License

[Specify your license here]

