<!-- FILE: README.md -->
# Mobile Wallet With Cunsumer Payment Using Android

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
8. [ER Diagram](#er-diagram)
9. [Getting Started](#getting-started)
10. [Documentation](#documentation)
11. [Contributing](#contributing)
12. [License](#license)

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

┌─────────────────────────────────────────────────────────────────────────────┐
│                              MOBILE WALLET SYSTEM                          │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌──────────────────┐              ┌──────────────────┐                   │
│  │   CONSUMER APP   │              │   MERCHANT APP   │                   │
│  │                  │              │                  │                   │
│  │  - Registration  │              │  - Registration  │                   │
│  │  - Login/OTP     │              │  - Login/OTP     │                   │
│  │  - Wallet        │              │  - Wallet        │                   │
│  │  - QR Generate   │◄────────────►│  - QR Scan       │                   │
│  │  - QR Scan       │              │  - Bank Transfer │                   │
│  │  - Payments      │              │  - Payments      │                   │
│  │  - Cards         │              │  - Recharge      │                   │
│  │  - Recharge      │              │  - Notifications │                   │
│  │  - Notifications │              │                  │                   │
│  └────────┬─────────┘              └────────┬─────────┘                   │
│           │                                 │                             │
│           └──────────────┬──────────────────┘                             │
│                          │                                                │
│                          ▼                                                │
│              ┌───────────────────────┐                                    │
│              │    NGINX Reverse      │                                    │
│              │    Proxy / Gateway    │                                    │
│              └───────────┬───────────┘                                    │
│                          │                                                │
│                          ▼                                                │
│  ┌───────────────────────────────────────────────────────────────────┐   │
│  │                      SPRING BOOT BACKEND                         │   │
│  │                                                                  │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐            │   │
│  │  │   AUTH      │  │   WALLET    │  │   PAYMENT   │            │   │
│  │  │  MODULE     │  │   MODULE    │  │   MODULE    │            │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘            │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐            │   │
│  │  │   QR        │  │   CARD      │  │   BANK      │            │   │
│  │  │  MODULE     │  │   MODULE    │  │   MODULE    │            │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘            │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐            │   │
│  │  │  RECHARGE   │  │ NOTIFICA-   │  │   ADMIN     │            │   │
│  │  │  MODULE     │  │  TION MOD   │  │   MODULE    │            │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘            │   │
│  │                                                                  │   │
│  │  ┌─────────────────────────────────────────────────────────┐    │   │
│  │  │              SECURITY & JWT LAYER                      │    │   │
│  │  └─────────────────────────────────────────────────────────┘    │   │
│  └───────────────────────────────────────────────────────────────────┘   │
│                                    │                                     │
│                                    ▼                                     │
│                    ┌─────────────────────────────┐                       │
│                    │         MYSQL 8.x           │                       │
│                    │    DATABASE SERVER          │                       │
│                    └─────────────────────────────┘                       │
│                                    ▲                                     │
│                                    │                                     │
│                    ┌─────────────────────────────┐                       │
│                    │  FIREBASE CLOUD MESSAGING  │                       │
│                    │  & EXTERNAL SERVICES        │                       │
│                    └─────────────────────────────┘                       │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘

---

## 🧩 Component Diagram

┌────────────────────────────────────────────────────────────────────────────┐
│                           COMPONENT DIAGRAM                               │
├────────────────────────────────────────────────────────────────────────────┤
│                                                                            │
│  ┌──────────────────────────────────────────────────────────────────┐     │
│  │                     PRESENTATION LAYER                            │     │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐         │     │
│  │  │   Consumer   │  │   Merchant   │  │   Swagger    │         │     │
│  │  │   Android    │  │   Android    │  │   API Docs   │         │     │
│  │  │   App        │  │   App        │  │              │         │     │
│  │  └──────────────┘  └──────────────┘  └──────────────┘         │     │
│  └──────────────────────────────────────────────────────────────────┘     │
│                                    │                                      │
│                                    ▼                                      │
│  ┌──────────────────────────────────────────────────────────────────┐     │
│  │                     CONTROLLER LAYER                             │     │
│  │  ┌────────────┐ ┌────────────┐ ┌────────────┐ ┌────────────┐ │     │
│  │  │   Auth     │ │   Wallet   │ │  Payment   │ │    QR      │ │     │
│  │  │ Controller │ │ Controller │ │ Controller │ │ Controller │ │     │
│  │  └────────────┘ └────────────┘ └────────────┘ └────────────┘ │     │
│  │  ┌────────────┐ ┌────────────┐ ┌────────────┐ ┌────────────┐ │     │
│  │  │   Card     │ │   Bank     │ │  Recharge  │ │   Admin    │ │     │
│  │  │ Controller │ │ Controller │ │ Controller │ │ Controller │ │     │
│  │  └────────────┘ └────────────┘ └────────────┘ └────────────┘ │     │
│  └──────────────────────────────────────────────────────────────────┘     │
│                                    │                                      │
│                                    ▼                                      │
│  ┌──────────────────────────────────────────────────────────────────┐     │
│  │                     SERVICE LAYER                                │     │
│  │  ┌────────────┐ ┌────────────┐ ┌────────────┐ ┌────────────┐ │     │
│  │  │   Auth     │ │   Wallet   │ │  Payment   │ │    QR      │ │     │
│  │  │  Service   │ │  Service   │ │  Service   │ │  Service   │ │     │
│  │  └────────────┘ └────────────┘ └────────────┘ └────────────┘ │     │
│  │  ┌────────────┐ ┌────────────┐ ┌────────────┐ ┌────────────┐ │     │
│  │  │   Card     │ │   Bank     │ │  Recharge  │ │   Admin    │ │     │
│  │  │  Service   │ │  Service   │ │  Service   │ │  Service   │ │     │
│  │  └────────────┘ └────────────┘ └────────────┘ └────────────┘ │     │
│  └──────────────────────────────────────────────────────────────────┘     │
│                                    │                                      │
│                                    ▼                                      │
│  ┌──────────────────────────────────────────────────────────────────┐     │
│  │                     REPOSITORY LAYER                             │     │
│  │  ┌────────────┐ ┌────────────┐ ┌────────────┐ ┌────────────┐ │     │
│  │  │   User     │ │   Wallet   │ │  Payment   │ │    QR      │ │     │
│  │  │ Repository │ │ Repository │ │ Repository │ │ Repository │ │     │
│  │  └────────────┘ └────────────┘ └────────────┘ └────────────┘ │     │
│  │  ┌────────────┐ ┌────────────┐ ┌────────────┐ ┌────────────┐ │     │
│  │  │   Card     │ │   Bank     │ │  Notifica- │ │   Audit    │ │     │
│  │  │ Repository │ │ Repository │ │  tion Rep  │ │  Log Rep   │ │     │
│  │  └────────────┘ └────────────┘ └────────────┘ └────────────┘ │     │
│  └──────────────────────────────────────────────────────────────────┘     │
│                                    │                                      │
│                                    ▼                                      │
│  ┌──────────────────────────────────────────────────────────────────┐     │
│  │                     DATABASE LAYER                               │     │
│  │                       MySQL 8.x                                  │     │
│  └──────────────────────────────────────────────────────────────────┘     │
│                                                                            │
└────────────────────────────────────────────────────────────────────────────┘


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

┌────────────────────────────────────────────────────────────────────────────┐
│                         SECURITY ARCHITECTURE                             │
├────────────────────────────────────────────────────────────────────────────┤
│                                                                            │
│  ┌──────────────────────────────────────────────────────────────────┐     │
│  │                     AUTHENTICATION LAYER                          │     │
│  │                                                                  │     │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────┐ │     │
│  │  │ Registration │  │  Login       │  │  OTP Verification    │ │     │
│  │  │ (BCrypt)     │  │  (JWT)       │  │  (Secure Random)    │ │     │
│  │  └──────────────┘  └──────────────┘  └──────────────────────┘ │     │
│  │                                                                  │     │
│  │  ┌──────────────────────────────────────────────────────────┐   │     │
│  │  │              JWT Token Management                        │   │     │
│  │  │  - Access Token (15 min)  - Refresh Token (7 days)     │   │     │
│  │  │  - Role-Based Claims      - Token Validation Filter    │   │     │
│  │  └──────────────────────────────────────────────────────────┘   │     │
│  └──────────────────────────────────────────────────────────────────┘     │
│                                    │                                      │
│                                    ▼                                      │
│  ┌──────────────────────────────────────────────────────────────────┐     │
│  │                     AUTHORIZATION LAYER                          │     │
│  │                                                                  │     │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────┐ │     │
│  │  │ ROLE_        │  │ ROLE_        │  │ ROLE_ADMIN          │ │     │
│  │  │ CONSUMER     │  │ MERCHANT     │  │                      │ │     │
│  │  └──────────────┘  └──────────────┘  └──────────────────────┘ │     │
│  │                                                                  │     │
│  │  ┌──────────────────────────────────────────────────────────┐   │     │
│  │  │              Method-Level Security (@PreAuthorize)       │   │     │
│  │  └──────────────────────────────────────────────────────────┘   │     │
│  └──────────────────────────────────────────────────────────────────┘     │
│                                    │                                      │
│                                    ▼                                      │
│  ┌──────────────────────────────────────────────────────────────────┐     │
│  │                     DATA SECURITY LAYER                         │     │
│  │                                                                  │     │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────┐ │     │
│  │  │ Password     │  │ Sensitive    │  │ Audit Logging        │ │     │
│  │  │ Hashing      │  │ Data         │  │ (Financial Ops)      │ │     │
│  │  │ (BCrypt)     │  │ Encryption   │  │                      │ │     │
│  │  │              │  │ (AES)        │  │                      │ │     │
│  │  └──────────────┘  └──────────────┘  └──────────────────────┘ │     │
│  │                                                                  │     │
│  │  ┌──────────────────────────────────────────────────────────┐   │     │
│  │  │        Transactional Integrity & Locking                │   │     │
│  │  │  - Optimistic Locking for Wallets                       │   │     │
│  │  │  - Atomic Database Transactions                         │   │     │
│  │  │  - Idempotency Keys for Payments                        │   │     │
│  │  └──────────────────────────────────────────────────────────┘   │     │
│  └──────────────────────────────────────────────────────────────────┘     │
│                                    │                                      │
│                                    ▼                                      │
│  ┌──────────────────────────────────────────────────────────────────┐     │
│  │                     TRANSPORT SECURITY                           │     │
│  │                                                                  │     │
│  │  - HTTPS/TLS 1.3                                                │     │
│  │  - JWT Transmission over HTTPS                                  │     │
│  │  - Secure Headers (HSTS, XSS Protection, etc.)                  │     │
│  └──────────────────────────────────────────────────────────────────┘     │
│                                                                            │
└────────────────────────────────────────────────────────────────────────────┘

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
├── backend/
│   ├── pom.xml
│   ├── Dockerfile
│   ├── docker-compose.yml
│   ├── .dockerignore
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/
│   │   │   │       └── mobilewallet/
│   │   │   │           ├── MobileWalletApplication.java
│   │   │   │           ├── config/
│   │   │   │           │   ├── AppConfig.java
│   │   │   │           │   ├── AsyncConfig.java
│   │   │   │           │   ├── CorsConfig.java
│   │   │   │           │   ├── FirebaseConfig.java
│   │   │   │           │   ├── OpenApiConfig.java
│   │   │   │           │   ├── PasswordConfig.java
│   │   │   │           │   ├── SecurityConfig.java
│   │   │   │           │   └── WebConfig.java
│   │   │   │           ├── controller/
│   │   │   │           │   ├── AdminController.java
│   │   │   │           │   ├── AuthController.java
│   │   │   │           │   ├── BankAccountController.java
│   │   │   │           │   ├── CardController.java
│   │   │   │           │   ├── NotificationController.java
│   │   │   │           │   ├── PaymentController.java
│   │   │   │           │   ├── QRController.java
│   │   │   │           │   ├── RechargeController.java
│   │   │   │           │   └── WalletController.java
│   │   │   │           ├── dto/
│   │   │   │           │   ├── admin/
│   │   │   │           │   │   ├── AdminDashboardDTO.java
│   │   │   │           │   │   ├── AdminUserDTO.java
│   │   │   │           │   │   ├── BlockUserRequest.java
│   │   │   │           │   │   └── ReportDTO.java
│   │   │   │           │   ├── auth/
│   │   │   │           │   │   ├── AuthRequest.java
│   │   │   │           │   │   ├── AuthResponse.java
│   │   │   │           │   │   ├── GuestLoginRequest.java
│   │   │   │           │   │   ├── GuestLoginResponse.java
│   │   │   │           │   │   ├── OTPRequest.java
│   │   │   │           │   │   ├── OTPResponse.java
│   │   │   │           │   │   ├── RefreshTokenRequest.java
│   │   │   │           │   │   ├── RegisterRequest.java
│   │   │   │           │   │   └── RegisterResponse.java
│   │   │   │           │   ├── bank/
│   │   │   │           │   │   ├── BankAccountRequest.java
│   │   │   │           │   │   ├── BankAccountResponse.java
│   │   │   │           │   │   └── BankTransferRequest.java
│   │   │   │           │   ├── card/
│   │   │   │           │   │   ├── CardRequest.java
│   │   │   │           │   │   └── CardResponse.java
│   │   │   │           │   ├── notification/
│   │   │   │           │   │   ├── NotificationResponse.java
│   │   │   │           │   │   └── NotificationStatusRequest.java
│   │   │   │           │   ├── payment/
│   │   │   │           │   │   ├── PaymentInitiateRequest.java
│   │   │   │           │   │   ├── PaymentResponse.java
│   │   │   │           │   │   └── PaymentStatusResponse.java
│   │   │   │           │   ├── qr/
│   │   │   │           │   │   ├── QRGenerateRequest.java
│   │   │   │           │   │   ├── QRGenerateResponse.java
│   │   │   │           │   │   ├── QRScanRequest.java
│   │   │   │           │   │   └── QRVerifyRequest.java
│   │   │   │           │   ├── recharge/
│   │   │   │           │   │   ├── BillPaymentRequest.java
│   │   │   │           │   │   ├── BillPaymentResponse.java
│   │   │   │           │   │   ├── RechargeRequest.java
│   │   │   │           │   │   └── RechargeResponse.java
│   │   │   │           │   ├── transaction/
│   │   │   │           │   │   ├── TransactionDTO.java
│   │   │   │           │   │   └── TransactionListResponse.java
│   │   │   │           │   └── wallet/
│   │   │   │           │       ├── AddMoneyRequest.java
│   │   │   │           │       ├── AddMoneyResponse.java
│   │   │   │           │       ├── WalletBalanceResponse.java
│   │   │   │           │       └── WalletResponse.java
│   │   │   │           ├── entity/
│   │   │   │           │   ├── Admin.java
│   │   │   │           │   ├── AuditLog.java
│   │   │   │           │   ├── BankAccount.java
│   │   │   │           │   ├── BillPayment.java
│   │   │   │           │   ├── Card.java
│   │   │   │           │   ├── Merchant.java
│   │   │   │           │   ├── Notification.java
│   │   │   │           │   ├── OTPVerification.java
│   │   │   │           │   ├── Payment.java
│   │   │   │           │   ├── QRTransaction.java
│   │   │   │           │   ├── RechargeTransaction.java
│   │   │   │           │   ├── RefreshToken.java
│   │   │   │           │   ├── TransactionHistory.java
│   │   │   │           │   ├── User.java
│   │   │   │           │   └── Wallet.java
│   │   │   │           ├── exception/
│   │   │   │           │   ├── ApiError.java
│   │   │   │           │   ├── ApiException.java
│   │   │   │           │   ├── DuplicatePaymentException.java
│   │   │   │           │   ├── GlobalExceptionHandler.java
│   │   │   │           │   ├── InsufficientBalanceException.java
│   │   │   │           │   ├── InvalidOTPException.java
│   │   │   │           │   ├── InvalidQRException.java
│   │   │   │           │   ├── JwtAuthenticationException.java
│   │   │   │           │   ├── ResourceNotFoundException.java
│   │   │   │           │   └── UnauthorizedAccessException.java
│   │   │   │           ├── mapper/
│   │   │   │           │   ├── BankAccountMapper.java
│   │   │   │           │   ├── CardMapper.java
│   │   │   │           │   ├── PaymentMapper.java
│   │   │   │           │   ├── TransactionMapper.java
│   │   │   │           │   └── WalletMapper.java
│   │   │   │           ├── repository/
│   │   │   │           │   ├── AdminRepository.java
│   │   │   │           │   ├── AuditLogRepository.java
│   │   │   │           │   ├── BankAccountRepository.java
│   │   │   │           │   ├── BillPaymentRepository.java
│   │   │   │           │   ├── CardRepository.java
│   │   │   │           │   ├── MerchantRepository.java
│   │   │   │           │   ├── NotificationRepository.java
│   │   │   │           │   ├── OTPVerificationRepository.java
│   │   │   │           │   ├── PaymentRepository.java
│   │   │   │           │   ├── QRTransactionRepository.java
│   │   │   │           │   ├── RechargeTransactionRepository.java
│   │   │   │           │   ├── RefreshTokenRepository.java
│   │   │   │           │   ├── TransactionHistoryRepository.java
│   │   │   │           │   ├── UserRepository.java
│   │   │   │           │   └── WalletRepository.java
│   │   │   │           ├── security/
│   │   │   │           │   ├── JwtAuthenticationEntryPoint.java
│   │   │   │           │   ├── JwtAuthenticationFilter.java
│   │   │   │           │   ├── JwtTokenProvider.java
│   │   │   │           │   ├── Role.java
│   │   │   │           │   ├── SecurityConstants.java
│   │   │   │           │   ├── SecurityUtils.java
│   │   │   │           │   └── UserPrincipal.java
│   │   │   │           ├── service/
│   │   │   │           │   ├── AdminService.java
│   │   │   │           │   ├── AuthService.java
│   │   │   │           │   ├── BankAccountService.java
│   │   │   │           │   ├── CardService.java
│   │   │   │           │   ├── NotificationService.java
│   │   │   │           │   ├── OTPService.java
│   │   │   │           │   ├── PaymentService.java
│   │   │   │           │   ├── QRService.java
│   │   │   │           │   ├── RechargeService.java
│   │   │   │           │   └── WalletService.java
│   │   │   │           ├── service/impl/
│   │   │   │           │   ├── AdminServiceImpl.java
│   │   │   │           │   ├── AuthServiceImpl.java
│   │   │   │           │   ├── BankAccountServiceImpl.java
│   │   │   │           │   ├── CardServiceImpl.java
│   │   │   │           │   ├── NotificationServiceImpl.java
│   │   │   │           │   ├── OTPServiceImpl.java
│   │   │   │           │   ├── PaymentServiceImpl.java
│   │   │   │           │   ├── QRServiceImpl.java
│   │   │   │           │   ├── RechargeServiceImpl.java
│   │   │   │           │   └── WalletServiceImpl.java
│   │   │   │           ├── util/
│   │   │   │           │   ├── AESEncryptionUtil.java
│   │   │   │           │   ├── AuditUtil.java
│   │   │   │           │   ├── DateUtil.java
│   │   │   │           │   ├── QRCodeGenerator.java
│   │   │   │           │   ├── QRCodeParser.java
│   │   │   │           │   ├── ReferenceNumberGenerator.java
│   │   │   │           │   └── ValidationUtil.java
│   │   │   │           └── validator/
│   │   │   │               ├── PhoneValidator.java
│   │   │   │               └── TransactionValidator.java
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       ├── application-dev.properties
│   │   │       ├── application-test.properties
│   │   │       ├── application-prod.properties
│   │   │       ├── firebase/
│   │   │       │   └── service-account.json.example
│   │   │       └── db/
│   │   │           ├── schema.sql
│   │   │           ├── indexes.sql
│   │   │           └── sample-data.sql
│   │   └── test/
│   │       ├── java/
│   │       │   └── com/
│   │       │       └── mobilewallet/
│   │       │           ├── config/
│   │       │           ├── controller/
│   │       │           ├── integration/
│   │       │           ├── repository/
│   │       │           └── service/
│   │       └── resources/
│   │           └── application-test.properties
│   └── README.md
│
├── consumer-app/
│   ├── settings.gradle
│   ├── build.gradle
│   ├── gradle.properties
│   ├── gradle/
│   │   └── wrapper/
│   │       ├── gradle-wrapper.jar
│   │       └── gradle-wrapper.properties
│   ├── app/
│   │   ├── build.gradle
│   │   └── src/
│   │       ├── main/
│   │       │   ├── AndroidManifest.xml
│   │       │   ├── java/
│   │       │   │   └── com/
│   │       │   │       └── mobilewallet/
│   │       │   │           └── consumer/
│   │       │   │               ├── ConsumerApplication.java
│   │       │   │               ├── activities/
│   │       │   │               │   ├── DashboardActivity.java
│   │       │   │               │   ├── LoginActivity.java
│   │       │   │               │   ├── MainActivity.java
│   │       │   │               │   ├── OTPActivity.java
│   │       │   │               │   ├── PaymentConfirmationActivity.java
│   │       │   │               │   ├── PaymentSuccessActivity.java
│   │       │   │               │   ├── ProfileActivity.java
│   │       │   │               │   ├── QRGeneratorActivity.java
│   │       │   │               │   ├── QRScannerActivity.java
│   │       │   │               │   ├── RechargeActivity.java
│   │       │   │               │   ├── RegisterActivity.java
│   │       │   │               │   ├── SettingsActivity.java
│   │       │   │               │   ├── SplashActivity.java
│   │       │   │               │   ├── TransactionDetailsActivity.java
│   │       │   │               │   └── WalletActivity.java
│   │       │   │               ├── adapters/
│   │       │   │               │   ├── NotificationAdapter.java
│   │       │   │               │   ├── TransactionAdapter.java
│   │       │   │               │   └── CardAdapter.java
│   │       │   │               ├── api/
│   │       │   │               │   ├── ApiClient.java
│   │       │   │               │   ├── ApiInterface.java
│   │       │   │               │   └── ApiResponse.java
│   │       │   │               ├── fragments/
│   │       │   │               │   ├── BillsFragment.java
│   │       │   │               │   ├── CardsFragment.java
│   │       │   │               │   ├── HomeFragment.java
│   │       │   │               │   ├── NotificationsFragment.java
│   │       │   │               │   ├── ProfileFragment.java
│   │       │   │               │   └── TransactionsFragment.java
│   │       │   │               ├── models/
│   │       │   │               │   ├── BankAccount.java
│   │       │   │               │   ├── Card.java
│   │       │   │               │   ├── Notification.java
│   │       │   │               │   ├── Payment.java
│   │       │   │               │   ├── QR.java
│   │       │   │               │   ├── Transaction.java
│   │       │   │               │   ├── User.java
│   │       │   │               │   └── Wallet.java
│   │       │   │               ├── repository/
│   │       │   │               │   └── AppRepository.java
│   │       │   │               ├── utils/
│   │       │   │               │   ├── Constants.java
│   │       │   │               │   ├── NetworkUtils.java
│   │       │   │               │   ├── PermissionUtils.java
│   │       │   │               │   ├── PreferenceManager.java
│   │       │   │               │   ├── QRCodeGenerator.java
│   │       │   │               │   ├── QRCodeScanner.java
│   │       │   │               │   └── TokenManager.java
│   │       │   │               ├── viewmodels/
│   │       │   │               │   ├── AuthViewModel.java
│   │       │   │               │   ├── DashboardViewModel.java
│   │       │   │               │   ├── PaymentViewModel.java
│   │       │   │               │   ├── QRViewModel.java
│   │       │   │               │   ├── TransactionViewModel.java
│   │       │   │               │   └── WalletViewModel.java
│   │       │   │               └── notifications/
│   │       │   │                   └── FCMService.java
│   │       │   └── res/
│   │       │       ├── drawable/
│   │       │       ├── drawable-v24/
│   │       │       ├── layout/
│   │       │       │   ├── activity_dashboard.xml
│   │       │       │   ├── activity_login.xml
│   │       │       │   ├── activity_main.xml
│   │       │       │   ├── activity_otp.xml
│   │       │       │   ├── activity_payment_confirmation.xml
│   │       │       │   ├── activity_payment_success.xml
│   │       │       │   ├── activity_profile.xml
│   │       │       │   ├── activity_qr_generator.xml
│   │       │       │   ├── activity_qr_scanner.xml
│   │       │       │   ├── activity_recharge.xml
│   │       │       │   ├── activity_register.xml
│   │       │       │   ├── activity_settings.xml
│   │       │       │   ├── activity_splash.xml
│   │       │       │   ├── activity_transaction_details.xml
│   │       │       │   ├── activity_wallet.xml
│   │       │       │   ├── fragment_bills.xml
│   │       │       │   ├── fragment_cards.xml
│   │       │       │   ├── fragment_home.xml
│   │       │       │   ├── fragment_notifications.xml
│   │       │       │   ├── fragment_profile.xml
│   │       │       │   ├── fragment_transactions.xml
│   │       │       │   ├── item_notification.xml
│   │       │       │   ├── item_transaction.xml
│   │       │       │   └── item_card.xml
│   │       │       ├── menu/
│   │       │       │   └── bottom_nav_menu.xml
│   │       │       ├── values/
│   │       │       │   ├── colors.xml
│   │       │       │   ├── strings.xml
│   │       │       │   ├── themes.xml
│   │       │       │   └── dimens.xml
│   │       │       └── xml/
│   │       │           ├── network_security_config.xml
│   │       │           └── data_extraction_rules.xml
│   │       └── test/
│   │           └── java/
│   │               └── com/
│   │                   └── mobilewallet/
│   │                       └── consumer/
│   │                           ├── unit/
│   │                           └── integration/
│   └── README.md
│
├── merchant-app/
│   ├── settings.gradle
│   ├── build.gradle
│   ├── gradle.properties
│   ├── gradle/
│   │   └── wrapper/
│   │       ├── gradle-wrapper.jar
│   │       └── gradle-wrapper.properties
│   ├── app/
│   │   ├── build.gradle
│   │   └── src/
│   │       ├── main/
│   │       │   ├── AndroidManifest.xml
│   │       │   ├── java/
│   │       │   │   └── com/
│   │       │   │       └── mobilewallet/
│   │       │   │           └── merchant/
│   │       │   │               ├── MerchantApplication.java
│   │       │   │               ├── activities/
│   │       │   │               │   ├── DashboardActivity.java
│   │       │   │               │   ├── LoginActivity.java
│   │       │   │               │   ├── MainActivity.java
│   │       │   │               │   ├── OTPActivity.java
│   │       │   │               │   ├── PaymentConfirmationActivity.java
│   │       │   │               │   ├── PaymentSuccessActivity.java
│   │       │   │               │   ├── ProfileActivity.java
│   │       │   │               │   ├── QRScannerActivity.java
│   │       │   │               │   ├── RechargeActivity.java
│   │       │   │               │   ├── RegisterActivity.java
│   │       │   │               │   ├── SettingsActivity.java
│   │       │   │               │   ├── SplashActivity.java
│   │       │   │               │   ├── TransactionDetailsActivity.java
│   │       │   │               │   ├── WalletActivity.java
│   │       │   │               │   └── BankTransferActivity.java
│   │       │   │               ├── adapters/
│   │       │   │               │   ├── NotificationAdapter.java
│   │       │   │               │   └── TransactionAdapter.java
│   │       │   │               ├── api/
│   │       │   │               │   ├── ApiClient.java
│   │       │   │               │   └── ApiInterface.java
│   │       │   │               ├── fragments/
│   │       │   │               │   ├── BillsFragment.java
│   │       │   │               │   ├── HomeFragment.java
│   │       │   │               │   ├── NotificationsFragment.java
│   │       │   │               │   ├── ProfileFragment.java
│   │       │   │               │   └── TransactionsFragment.java
│   │       │   │               ├── models/
│   │       │   │               │   ├── BankAccount.java
│   │       │   │               │   ├── Notification.java
│   │       │   │               │   ├── Payment.java
│   │       │   │               │   ├── Transaction.java
│   │       │   │               │   ├── User.java
│   │       │   │               │   └── Wallet.java
│   │       │   │               ├── repository/
│   │       │   │               │   └── AppRepository.java
│   │       │   │               ├── utils/
│   │       │   │               │   ├── Constants.java
│   │       │   │               │   ├── NetworkUtils.java
│   │       │   │               │   ├── PermissionUtils.java
│   │       │   │               │   ├── PreferenceManager.java
│   │       │   │               │   ├── QRCodeScanner.java
│   │       │   │               │   └── TokenManager.java
│   │       │   │               ├── viewmodels/
│   │       │   │               │   ├── AuthViewModel.java
│   │       │   │               │   ├── DashboardViewModel.java
│   │       │   │               │   ├── PaymentViewModel.java
│   │       │   │               │   ├── QRViewModel.java
│   │       │   │               │   ├── TransactionViewModel.java
│   │       │   │               │   └── WalletViewModel.java
│   │       │   │               └── notifications/
│   │       │   │                   └── FCMService.java
│   │       │   └── res/
│   │       │       ├── drawable/
│   │       │       ├── drawable-v24/
│   │       │       ├── layout/
│   │       │       │   ├── activity_dashboard.xml
│   │       │       │   ├── activity_login.xml
│   │       │       │   ├── activity_main.xml
│   │       │       │   ├── activity_otp.xml
│   │       │       │   ├── activity_payment_confirmation.xml
│   │       │       │   ├── activity_payment_success.xml
│   │       │       │   ├── activity_profile.xml
│   │       │       │   ├── activity_qr_scanner.xml
│   │       │       │   ├── activity_recharge.xml
│   │       │       │   ├── activity_register.xml
│   │       │       │   ├── activity_settings.xml
│   │       │       │   ├── activity_splash.xml
│   │       │       │   ├── activity_transaction_details.xml
│   │       │       │   ├── activity_wallet.xml
│   │       │       │   ├── activity_bank_transfer.xml
│   │       │       │   ├── fragment_bills.xml
│   │       │       │   ├── fragment_home.xml
│   │       │       │   ├── fragment_notifications.xml
│   │       │       │   ├── fragment_profile.xml
│   │       │       │   ├── fragment_transactions.xml
│   │       │       │   ├── item_notification.xml
│   │       │       │   └── item_transaction.xml
│   │       │       ├── menu/
│   │       │       │   └── bottom_nav_menu.xml
│   │       │       ├── values/
│   │       │       │   ├── colors.xml
│   │       │       │   ├── strings.xml
│   │       │       │   ├── themes.xml
│   │       │       │   └── dimens.xml
│   │       │       └── xml/
│   │       │           ├── network_security_config.xml
│   │       │           └── data_extraction_rules.xml
│   │       └── test/
│   │           └── java/
│   │               └── com/
│   │                   └── mobilewallet/
│   │                       └── merchant/
│   │                           ├── unit/
│   │                           └── integration/
│   └── README.md
│
├── database/
│   ├── schema.sql
│   ├── indexes.sql
│   └── sample-data.sql
│
├── postman/
│   ├── mobile-wallet.postman_collection.json
│   └── mobile-wallet.postman_environment.json
│
├── nginx/
│   └── nginx.conf
│
├── docs/
│   ├── architecture.md
│   ├── api-documentation.md
│   ├── database-design.md
│   ├── security.md
│   ├── testing.md
│   ├── deployment.md
│   └── diagrams/
│       ├── system-architecture.mermaid
│       ├── component-diagram.mermaid
│       ├── er-diagram.mermaid
│       ├── qr-payment-flow.mermaid
│       └── authentication-flow.mermaid
│
├── .github/
│   └── workflows/
│       ├── backend-ci.yml
│       ├── consumer-android-ci.yml
│       └── merchant-android-ci.yml
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






# Mobile Wallet With Merchant Payment Using Android currently working on project
