<!-- FILE: README.md -->
# Mobile Wallet With Merchant Payment Using Android

A production-ready, secure, modular, and scalable full-stack digital wallet platform enabling **QR-based payments between Consumers and Merchants** using Android applications and a Spring Boot backend.

![Status](https://img.shields.io/badge/status-production--ready-brightgreen)
![Java](https://img.shields.io/badge/Java-21_LTS-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.1-green)
![Android](https://img.shields.io/badge/Android-API_24+-blue)
![MySQL](https://img.shields.io/badge/MySQL-8.4_LTS-00758f)
![License](https://img.shields.io/badge/license-MIT-blue)

---

## 📋 Table of Contents

1. [Overview](#overview)
2. [High-Level Architecture](#high-level-architecture)
3. [Component Diagram](#component-diagram)
4. [Technology Stack Summary](#technology-stack-summary)
5. [Security Architecture](#security-architecture)
6. [Project Structure](#project-structure)
7. [Modules](#modules)
8. [Getting Started](#getting-started)
9. [Documentation](#documentation)
10. [Contributing](#contributing)
11. [License](#license)

---

## 🎯 Overview

**Mobile Wallet With Merchant Payment Using Android** is a comprehensive digital payment platform that enables secure wallet-based transactions between **Consumers** and **Merchants** using **QR codes**.

The system consists of **four major components**:

| Component | Description | Port |
|-----------|-------------|------|
| **Spring Boot Backend** | REST API, JWT authentication, payment processing | `9090` |
| **Consumer Android App** | Customer wallet, QR payments, recharges, bill payments | — |
| **Merchant Android App** | Merchant wallet, QR generation, bank transfers | — |
| **MySQL Database** | Persistent storage for users, wallets, transactions | `3306` |

### ✨ Key Features

#### Consumer Application
- ✅ User registration & OTP verification
- ✅ JWT-based authentication with refresh tokens
- ✅ Guest/demo login mode
- ✅ Wallet balance management
- ✅ Add money via cards/bank accounts
- ✅ QR code generation for receiving payments
- ✅ QR code scanning for making payments
- ✅ Transaction history with filters
- ✅ Card management (add, update, delete, set default)
- ✅ Bank account management
- ✅ Mobile/DTH recharge
- ✅ Bill payments (electricity, water, gas, etc.)
- ✅ Push notifications
- ✅ Profile & settings

#### Merchant Application
- ✅ Merchant registration with business details
- ✅ JWT-based authentication
- ✅ Dynamic QR code generation
- ✅ QR code scanning for payments
- ✅ Receive payments to merchant wallet
- ✅ Wallet-to-bank transfers
- ✅ Transaction history
- ✅ Bank detail management
- ✅ Recharge & bill payments
- ✅ Push notifications
- ✅ Merchant profile management

#### Backend
- ✅ RESTful APIs with OpenAPI/Swagger documentation
- ✅ JWT access + refresh tokens
- ✅ Argon2/BCrypt password hashing
- ✅ AES encryption for sensitive data
- ✅ OTP generation & verification
- ✅ Atomic payment transactions
- ✅ Idempotency keys for duplicate prevention
- ✅ Optimistic & pessimistic locking
- ✅ Audit logging
- ✅ Rate limiting
- ✅ Firebase Cloud Messaging integration
- ✅ Role-based access control (RBAC)

---

## 🏗️ High-Level Architecture

┌──────────────────────────────┐
│ MOBILE WALLET SYSTEM │
└──────────────────────────────┘

┌──────────────────┐ ┌──────────────────┐
│ CONSUMER APP │ │ MERCHANT APP │
│ (Android) │ │ (Android) │
│ │ │ │
│ • Register │ │ • Register │
│ • Login/OTP │ │ • Login/OTP │
│ • Wallet │◄──────────────────►│ • Wallet │
│ • QR Generate │ HTTPS/REST │ • QR Scan │
│ • QR Scan │ │ • Bank Transfer │
│ • Payments │ │ • Payments │
│ • Cards/Banks │ │ • Recharge │
│ • Notifications │ │ • Notifications │
└────────┬─────────┘ └────────┬─────────┘
│ │
└───────────────┬───────────────────────┘
│
▼
┌────────────────────────┐
│ NGINX Reverse Proxy │
│ HTTPS / Rate Limit │
└────────────┬───────────┘
│
▼
┌────────────────────────────────────────────────────────────┐
│ SPRING BOOT BACKEND │
│ │
│ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ │
│ │ Auth │ │ Wallet │ │ Payment │ │ QR │ │
│ │ Module │ │ Module │ │ Module │ │ Module │ │
│ └──────────┘ └──────────┘ └──────────┘ └──────────┘ │
│ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ │
│ │ Card │ │ Bank │ │ Recharge │ │ Admin │ │
│ │ Module │ │ Module │ │ Module │ │ Module │ │
│ └──────────┘ └──────────┘ └──────────┘ └──────────┘ │
│ │
│ ┌────────────────────────────────────────────────────┐ │
│ ┌────────────────────────────────────────────────────┐ │
│ │ Security Layer (JWT + BCrypt + AES) │ │
│ └────────────────────────────────────────────────────┘ │
└────────────────────────────┬───────────────────────────────┘
│
▼
┌────────────────────────┐
│ MySQL 8.4 LTS │
│ (Persistent Store) │
└────────────────────────┘
▲
│
┌────────────────────────┐
│ Firebase Cloud │
│ Messaging (FCM) │
└────────────────────────┘


---

## 🧩 Component Diagram

┌─────────────────────────────────────────────────────────────────────────┐
│ COMPONENT DIAGRAM │
├─────────────────────────────────────────────────────────────────────────┤
│ │
│ ┌──────────────────────────────────────────────────────────────────┐ │
│ │ PRESENTATION LAYER │ │
│ │ │ │
│ │ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ │ │
│ │ │ Consumer │ │ Merchant │ │ Swagger UI │ │ │
│ │ │ Android App │ │ Android App │ │ (OpenAPI) │ │ │
│ │ └──────┬───────┘ └──────┬───────┘ └──────┬───────┘ │ │
│ └─────────┼─────────────────┼─────────────────┼──────────────────┘ │
│ │ │ │ │
│ └─────────────────┼─────────────────┘ │
│ │ HTTPS / REST │
│ ▼ │
│ ┌──────────────────────────────────────────────────────────────────┐ │
│ │ CONTROLLER LAYER │ │
│ │ │ │
│ │ ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐ │ │
│ │ │ Auth │ │ Wallet │ │Payment │ │ QR │ │ Card │ │ │
│ │ │ Ctrl │ │ Ctrl │ │ Ctrl │ │ Ctrl │ │ Ctrl │ │ │
│ │ └────────┘ └────────┘ └────────┘ └────────┘ └────────┘ │ │
│ │ ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐ │ │
│ │ │ Bank │ │Recharge│ │Notify │ │ Admin │ │ Auth/ │ │ │
│ │ │ Ctrl │ │ Ctrl │ │ Ctrl │ │ Ctrl │ │ Guest │ │ │
│ │ └────────┘ └────────┘ └────────┘ └────────┘ └────────┘ │ │
│ └──────────────────────────────┬───────────────────────────────────┘ │
│ │ │
│ ▼ │
│ ┌──────────────────────────────────────────────────────────────────┐ │
│ │ SERVICE LAYER │ │
│ │ │ │
│ │ ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐ │ │
│ │ │ Auth │ │ Wallet │ │Payment │ │ QR │ │ Card │ │ │
│ │ │Service │ │Service │ │Service │ │Service │ │Service │ │ │
│ │ └────────┘ └────────┘ └────────┘ └────────┘ └────────┘ │ │
│ │ ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐ │ │
│ │ │ Bank │ │Recharge│ │Notify │ │ OTP │ │ Admin │ │ │
│ │ │Service │ │Service │ │Service │ │Service │ │Service │ │ │
│ │ └────────┘ └────────┘ └────────┘ └────────┘ └────────┘ │ │
│ └──────────────────────────────┬───────────────────────────────────┘ │
│ │ │
│ ▼ │
│ ┌──────────────────────────────────────────────────────────────────┐ │
│ │ REPOSITORY LAYER │ │
│ │ │ │
│ │ ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐ │ │
│ │ │ User │ │ Wallet │ │Payment │ │ QR │ │ Card │ │ │
│ │ │ Repo │ │ Repo │ │ Repo │ │ Repo │ │ Repo │ │ │
│ │ └────────┘ └────────┘ └────────┘ └────────┘ └────────┘ │ │
│ │ ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐ │ │
│ │ │ Bank │ │ Audit │ │Notify │ │ OTP │ │Refresh │ │ │
│ │ │ Repo │ │ Repo │ │ Repo │ │ Repo │ │ Token │ │ │
│ │ └────────┘ └────────┘ └────────┘ └────────┘ └────────┘ │ │
│ └──────────────────────────────┬───────────────────────────────────┘ │
│ │ │
│ ▼ │
│ ┌──────────────────────────────────────────────────────────────────┐ │
│ │ DATABASE LAYER │ │
│ │ MySQL 8.4 LTS │ │
│ └──────────────────────────────────────────────────────────────────┘ │
│ │
└─────────────────────────────────────────────────────────────────────────┘



---

## 🛠️ Technology Stack Summary

### Backend

| Layer | Technology | Version |
|-------|------------|---------|
| **Language** | Java | 21 LTS |
| **Framework** | Spring Boot | 3.4.1 |
| **Security** | Spring Security | Managed |
| **ORM** | Spring Data JPA / Hibernate | Managed |
| **Build** | Maven | 3.9.x+ |
| **Database** | MySQL | 8.4 LTS |
| **Auth** | JWT (jjwt) | 0.12.6 |
| **Password Hash** | BCrypt | Spring |
| **QR Code** | ZXing | 3.5.3 |
| **Docs** | SpringDoc OpenAPI | 2.5.0 |
| **Notification** | Firebase Admin SDK | 9.2.0 |
| **Mapping** | MapStruct | 1.6.3 |
| **Boilerplate** | Lombok | 1.18.34 |

### Android

| Layer | Technology | Version |
|-------|------------|---------|
| **Language** | Java | 17 |
| **Min SDK** | Android | API 24 (7.0) |
| **Target SDK** | Android | API 35 |
| **UI** | Material Design 3 | 1.12.0 |
| **Architecture** | MVVM + Repository | — |
| **Async** | LiveData + ViewModel | 2.8.7 |
| **Network** | Retrofit | 2.11.0 |
| **HTTP** | OkHttp | 4.12.0 |
| **JSON** | Gson | 2.11.0 |
| **QR Gen** | ZXing | 3.5.3 |
| **QR Scan** | ML Kit Barcode | 17.3.0 |
| **Camera** | CameraX | 1.4.1 |
| **Push** | Firebase Messaging | BOM 33.7.0 |
| **Storage** | EncryptedSharedPreferences | 1.1.0 |
| **Images** | Glide | 4.16.0 |
| **Charts** | MPAndroidChart | 3.1.0 |
| **Animation** | Lottie | 6.6.2 |

### DevOps

| Component | Technology |
|-----------|------------|
| **Containerization** | Docker, Docker Compose |
| **Reverse Proxy** | Nginx |
| **CI/CD** | GitHub Actions |
| **Monitoring** | Spring Actuator |

---

## 🔐 Security Architecture

┌─────────────────────────────────────────────────────────────────────────┐
│ SECURITY ARCHITECTURE │
├─────────────────────────────────────────────────────────────────────────┤
│ │
│ ┌───────────────────────────────────────────────────────────────────┐ │
│ │ AUTHENTICATION LAYER │ │
│ │ │ │
│ │ ┌────────────┐ ┌────────────┐ ┌───────────────────────────┐ │ │
│ │ │ Registration │ │ Login │ │ OTP Verification (SMS) │ │ │
│ │ │ (BCrypt) │ │ (JWT) │ │ (SecureRandom 6-digit) │ │ │
│ │ └────────────┘ └────────────┘ └───────────────────────────┘ │ │
│ │ │ │
│ │ ┌──────────────────────────────────────────────────────────┐ │ │
│ │ │ JWT Token Management │ │ │
│ │ │ • Access Token — 15 minutes │ │ │
│ │ │ • Refresh Token — 7 days │ │ │
│ │ │ • HMAC-SHA256 Signature │ │ │
│ │ │ • Role Claims: ROLE_CONSUMER / ROLE_MERCHANT / ROLE_ADMIN│ │ │
│ │ └──────────────────────────────────────────────────────────┘ │ │
│ └───────────────────────────────────────────────────────────────────┘ │
│ │ │
│ ▼ │
│ ┌───────────────────────────────────────────────────────────────────┐ │
│ │ AUTHORIZATION LAYER │ │
│ │ │ │
│ │ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ │ │
│ │ │ROLE_CONSUMER │ │ROLE_MERCHANT │ │ ROLE_ADMIN │ │ │
│ │ └──────────────┘ └──────────────┘ └──────────────┘ │ │
│ │ │ │
│ │ Method-Level Security: @PreAuthorize("hasRole('CONSUMER')") │ │
│ │ Endpoint Protection: .requestMatchers("/admin/**").hasRole() │ │
│ └───────────────────────────────────────────────────────────────────┘ │
│ │ │
│ ▼ │
│ ┌───────────────────────────────────────────────────────────────────┐ │
│ │ DATA SECURITY LAYER │ │
│ │ │ │
│ │ ┌────────────┐ ┌────────────┐ ┌─────────────────────────┐ │ │
│ │ │ Password │ │ Sensitive │ │ Audit Logging │ │ │
│ │ │ Hashing │ │ Data │ │ (Financial Ops) │ │ │
│ │ │ (BCrypt 12)│ │ Encryption │ │ │ │ │
│ │ │ │ │ (AES-256) │ │ │ │ │
│ │ └────────────┘ └────────────┘ └─────────────────────────┘ │ │
│ │ │ │
│ │ ┌───────────────────────────────────────────────────────────┐ │ │
│ │ │ Transactional Integrity & Locking │ │ │
│ │ │ • Pessimistic Locking — Wallet balance updates │ │ │
│ │ │ • Optimistic Locking — Version-based concurrency │ │ │
│ │ │ • Atomic @Transactional — ACID compliance │ │ │
│ │ │ • Idempotency Keys — Duplicate payment prevention │ │ │
│ │ └───────────────────────────────────────────────────────────┘ │ │
│ └───────────────────────────────────────────────────────────────────┘ │
│ │ │
│ ▼ │
│ ┌───────────────────────────────────────────────────────────────────┐ │
│ │ TRANSPORT SECURITY │ │
│ │ │ │
│ │ • HTTPS/TLS 1.3 (enforced via Nginx) │ │
│ │ • JWT transmission over HTTPS only │ │
│ │ • Security Headers: HSTS, X-Content-Type-Options, X-Frame-Options│ │
│ │ • Certificate validation on Android │ │
│ │ • Network Security Config (no cleartext in production) │ │
│ └───────────────────────────────────────────────────────────────────┘ │
│ │
└─────────────────────────────────────────────────────────────────────────┘


### Security Measures Summary

| Threat | Mitigation |
|--------|-----------|
| **Password Theft** | BCrypt hashing (never AES) |
| **Session Hijacking** | JWT with short expiry + refresh tokens |
| **Replay Attacks** | Idempotency keys + nonce in QR |
| **Double Spending** | Atomic DB transactions + pessimistic locks |
| **Duplicate Payments** | Unique reference IDs + idempotency keys |
| **QR Tampering** | Server-side signed QR payloads |
| **Client-Side Amount Manipulation** | Backend always validates authoritative amount |
| **Brute Force** | Rate limiting + account lockout |
| **Sensitive Data Exposure** | AES encryption for card/bank data |
| **SQL Injection** | JPA/Hibernate parameterized queries |
| **XSS / CSRF** | Stateless auth + CORS configuration |
| **MITM** | HTTPS only, certificate pinning |
| **CVV Storage** | Never store CVV |
| **Card Number Storage** | Only masked (**** **** **** 1234) |

---

## 📁 Project Structure

mobile-wallet-with-merchant-payment/
│
├── backend/ # Spring Boot REST API
│ ├── pom.xml
│ ├── Dockerfile
│ ├── docker-compose.yml
│ ├── src/
│ │ ├── main/
│ │ │ ├── java/com/mobilewallet/
│ │ │ │ ├── config/
│ │ │ │ ├── controller/
│ │ │ │ ├── dto/
│ │ │ │ ├── entity/
│ │ │ │ ├── exception/
│ │ │ │ ├── mapper/
│ │ │ │ ├── repository/
│ │ │ │ ├── security/
│ │ │ │ ├── service/
│ │ │ │ ├── service/impl/
│ │ │ │ ├── util/
│ │ │ │ └── validator/
│ │ │ └── resources/
│ │ │ ├── application.properties
│ │ │ ├── application-dev.properties
│ │ │ ├── application-test.properties
│ │ │ └── application-prod.properties
│ │ └── test/
│ └── README.md # ← Backend README
│
├── consumer-app/ # Consumer Android App
│ ├── app/
│ │ ├── build.gradle
│ │ └── src/main/
│ │ ├── AndroidManifest.xml
│ │ ├── java/com/mobilewallet/consumer/
│ │ │ ├── activities/
│ │ │ ├── adapters/
│ │ │ ├── api/
│ │ │ ├── fragments/
│ │ │ ├── models/
│ │ │ ├── notifications/
│ │ │ ├── repository/
│ │ │ ├── utils/
│ │ │ └── viewmodels/
│ │ └── res/
│ │ ├── drawable/
│ │ ├── layout/
│ │ ├── menu/
│ │ ├── values/
│ │ └── xml/
│ └── README.md # ← Consumer App README
│
├── merchant-app/ # Merchant Android App
│ ├── app/
│ └── README.md
│
├── database/ # SQL Scripts
│ ├── schema.sql
│ ├── indexes.sql
│ ├── sample-data.sql
│ └── README.md # ← Database README
│
├── postman/ # API Testing
│ ├── mobile-wallet.postman_collection.json
│ ├── mobile-wallet.postman_environment.json
│ └── README.md # ← Postman README
│
├── nginx/
│ └── nginx.conf
│
├── docs/
│ ├── architecture.md
│ ├── api-documentation.md
│ ├── database-design.md
│ ├── security.md
│ └── diagrams/
│
├── .github/
│ └── workflows/
│ ├── backend-ci.yml
│ ├── consumer-android-ci.yml
│ └── merchant-android-ci.yml
│
├── .gitignore
└── README.md 



---

## 🧩 Modules

| Module | Responsibility |
|--------|----------------|
| **Auth** | Registration, login, OTP, JWT, guest login |
| **Wallet** | Balance, add money, debit/credit |
| **Payment** | Initiate payments, QR payments, history |
| **QR** | Generate, validate, scan QR codes |
| **Card** | Add/update/delete cards, set default |
| **Bank** | Add/update/delete bank accounts, transfers |
| **Recharge** | Mobile/DTH recharge, bill payments |
| **Notification** | Push notifications via Firebase |
| **Admin** | User management, reports, audit logs |
| **Audit** | Log all financial & security events |

---

## 🚀 Getting Started

### Prerequisites

```bash
# Required
java -version       # 21+
mvn -version        # 3.9+
docker --version    # 20+
mysql --version     # 8.4+

# Android (optional)
Android Studio      # Latest


## Quick Start

# 1. Clone
git clone https://github.com/amitv28242/Wobile_Wallet_UPI.git
cd mobile-wallet

# 2. Setup database
mysql -u root -p < database/schema.sql
mysql -u root -p < database/indexes.sql
mysql -u root -p < database/sample-data.sql

# 3. Configure backend
cd backend
cp src/main/resources/application.properties.example \
   src/main/resources/application.properties
# Edit DB credentials

# 4. Run backend
mvn clean install
mvn spring-boot:run

# 5. Verify
curl http://localhost:9090/api/actuator/health
# → {"status":"UP"}

# 6. Open Swagger
open http://localhost:9090/api/swagger-ui.html

## Docker Start

cd backend
docker-compose up -d

docker-compose logs -f backend
docker-compose down
