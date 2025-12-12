# Price Calculation Microservice

A REST microservice for calculating Test Purchase prices with support for customer-specific pricing, country-based rates, category multipliers, and type-based adjustments.

## Overview

This microservice provides a comprehensive pricing engine that calculates test purchase fees based on:
- Customer-specific base fees (stored in database)
- Country-specific default rates (Germany, Austria, etc.)
- Category multipliers (S, M, L, XL)
- Service type multipliers (Forwarding to Client, Return Back to Seller)
- Postage fees based on package category

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
git clone <repository-url>
cd price-calculation-microservice
```

### 2. Configure Database

#### Option A: Local MySQL Database

Update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/testpurchase_rest_db?useSSL=false&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=root
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
spring.datasource.username=root
spring.datasource.password=root
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

