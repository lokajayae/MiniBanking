# 🏦 Mini Banking System

A mini banking system built with microservices architecture, demonstrating core banking operations such as account management, fund transfers, and transaction history.

<br>

## 📋 Architecture

<br>

## 💻 Tech Stack
- **Java 17**
- **Spring Boot 3.2**
- **PostgreSQL 14**
- **Docker & Docker Compose**
- **Maven 3.9.4**

<br>

## 📦 Services

### account-service (port 8081)
Manages bank accounts — create, retrieve, and update balances.

### transaction-service (port 8082)
Handles all financial transactions — deposit, withdrawal, and transfer between accounts. Communicates with account-service via REST.

<br>

## ✅ Prerequisites

Make sure you have the following installed:

| Tool | Version |
|------|---------|
| Docker | 20.x or higher |
| Docker Compose | 2.x or higher |

> Java, Maven, PostgreSQL are **not required** on your machine — they run inside Docker.

<br>

## 🚀 How to Run

**1. Clone the repository**
```bash
git clone https://github.com/lokajayae/MiniBanking.git
cd MiniBanking
```

**2. Run with Docker Compose**
```bash
docker compose up --build
```

**3. Services will be available at:**
- Account Service: `http://localhost:8081`
- Transaction Service: `http://localhost:8082`
- Account Service Swagger: `http://localhost:8081/swagger-ui/index.html`
- Transaction Service Swagger: `http://localhost:8082/swagger-ui/index.html`

**To stop:**
```bash
docker compose down
```

**To stop and reset database:**
```bash
docker compose down -v
```

<br>

## 📡 API Endpoints

### Account Service

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/accounts` | Create new account |
| GET | `/api/accounts/{accountNumber}` | Get account by account number |
| GET | `/api/accounts?minBalance={amount}` | Get accounts with minimum balance |
| PUT | `/api/accounts/{accountNumber}/balance` | Update account balance |

### Transaction Service

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/transactions/deposit` | Deposit to account |
| POST | `/api/transactions/withdraw` | Withdraw from account |
| POST | `/api/transactions/transfer` | Transfer between accounts |
| GET | `/api/transactions/history/{accountNumber}` | Get transaction history |
| GET | `/api/transactions/large?minAmount={amount}` | Get large transactions |
