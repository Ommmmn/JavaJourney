# 🐾 PetyMate — India's One-Stop Pet Platform

> **"India's Shaadi.com for Pets"** — One Platform. Every Pet Need.

PetyMate is a complete, production-grade web application that serves as the one-stop platform for everything pets in India. From area-based pet matchmaking to verified veterinarians, certified trainers, and a full pet products store — PetyMate connects pet owners across India.

---

## ✨ Features

| # | Feature | Description |
|---|---------|-------------|
| 🐾 | **Pet Matchmaking** | Area-based mating partner search with Haversine distance filtering |
| 🛍️ | **Buy, Sell & Adopt** | Browse verified breeders and heartwarming adoption listings |
| 📦 | **Pet Products Store** | Food, toys, medicine, grooming, accessories — delivered to your door |
| 🩺 | **Vet Consultations** | Book online or clinic appointments with verified veterinarians |
| 🎓 | **Pet Trainers** | Hire certified trainers for home visits, center, online, or group sessions |
| 💳 | **Contact Unlock** | Unlock owner contact details for just ₹99 via Razorpay |
| 🤖 | **Marshal AI Chatbot** | Keyword-based intelligent pet assistant on every page |
| 👑 | **Subscription Plans** | FREE / BASIC (₹199/mo) / PREMIUM (₹499/mo) tiers |

---

## 🛠️ Tech Stack

### Backend

| Technology | Purpose |
|-----------|---------|
| Java 17 | Core language |
| Spring Boot 3.2 | Application framework |
| Spring Security 6 | Authentication & authorization (JWT) |
| Hibernate / JPA | ORM & database access |
| MySQL 8.0 | Relational database |
| Razorpay Java SDK | Payment processing |
| Springdoc OpenAPI 3 | API documentation (Swagger UI) |
| Lombok | Boilerplate reduction |
| JUnit 5 + Mockito | Unit testing |
| Maven | Build tool |

### Frontend

| Technology | Purpose |
|-----------|---------|
| React 18 (Vite) | UI framework |
| React Router v6 | Client-side routing |
| Tailwind CSS v3 | Utility-first CSS framework |
| Framer Motion | Page transitions & animations |
| Axios | HTTP client |
| React Hook Form + Zod | Form validation |
| React Hot Toast | Toast notifications |
| Lucide React | Icon library |
| Swiper.js | Carousels |
| React Query | Server state caching |

---

## 📸 Screenshots

See the `/screenshots` folder after running the application locally.

---

## 📋 Prerequisites

- **Java** 17+
- **Node.js** 18+
- **MySQL** 8.0+
- **Maven** 3.8+
- **npm** 9+

---

## 🚀 Setup Instructions

### 1. Clone the repository

```bash
git clone https://github.com/your-username/petymate.git
cd petymate
```

### 2. Create MySQL database

```sql
CREATE DATABASE petymate_db;
```

### 3. Run the schema

```bash
mysql -u root -p petymate_db < backend/src/main/resources/schema.sql
```

### 4. Configure backend

Edit `backend/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    username: root
    password: your_password

razorpay:
  key-id: rzp_test_your_key_id_here
  key-secret: your_test_key_secret_here

petymate:
  jwt:
    secret: your-very-long-256-bit-secret-key-replace-this-in-production
```

### 5. Start the backend

```bash
cd backend
mvn spring-boot:run
```

Backend runs at: **http://localhost:8080**

### 6. Start the frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend runs at: **http://localhost:5173**

### 7. Open the app

Navigate to **http://localhost:5173** in your browser.

---

## 📖 API Documentation

Swagger UI is available at: **http://localhost:8080/swagger-ui.html**

OpenAPI JSON: **http://localhost:8080/api/docs**

---

## 🔑 Test Credentials

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@petymate.in | Admin@123 |
| User | testuser@petymate.in | Test@123 |

---

## 💳 Razorpay Test Cards

| Method | Details |
|--------|---------|
| Card | `4111 1111 1111 1111` |
| CVV | Any 3 digits |
| Expiry | Any future date |
| UPI | `success@razorpay` |

---

## 🏗️ Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                     FRONTEND (React 18 + Vite)              │
│  ┌─────────┐ ┌──────────┐ ┌─────────┐ ┌─────────────────┐  │
│  │  Pages  │ │Components│ │Contexts │ │ Axios Services  │  │
│  └────┬────┘ └────┬─────┘ └────┬────┘ └────────┬────────┘  │
│       └───────────┴────────────┴────────────────┘           │
│                          │ HTTP (Axios)                      │
└──────────────────────────┼──────────────────────────────────┘
                           │
                    ┌──────▼──────┐
                    │  Nginx /    │
                    │  Vite Proxy │
                    └──────┬──────┘
                           │
┌──────────────────────────┼──────────────────────────────────┐
│              BACKEND (Spring Boot 3.2)                      │
│  ┌──────────────┐  ┌────▼─────┐  ┌───────────────────────┐ │
│  │ JWT Auth     │  │Controllers│  │  Spring Security 6   │ │
│  │ Filter       │──│          │──│  SecurityFilterChain  │ │
│  └──────────────┘  └────┬─────┘  └───────────────────────┘ │
│                    ┌────▼─────┐                              │
│                    │ Services │                              │
│                    └────┬─────┘                              │
│  ┌──────────┐     ┌────▼─────┐     ┌───────────────────┐   │
│  │ Razorpay │─────│  Repos   │─────│ Entities (JPA)    │   │
│  │ Client   │     └────┬─────┘     └───────────────────┘   │
│  └──────────┘          │                                    │
│                   ┌────▼─────┐                              │
│                   │MySQL 8.0 │                              │
│                   │16 Tables │                              │
│                   └──────────┘                              │
└─────────────────────────────────────────────────────────────┘
```

---

## 📡 API Endpoint Summary

### Auth
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login |
| POST | `/api/auth/refresh` | Refresh access token |
| POST | `/api/auth/logout` | Logout |
| GET | `/api/auth/me` | Get current user profile |
| PUT | `/api/auth/me` | Update profile |

### Pets
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/pets` | Create pet listing |
| GET | `/api/pets` | Search pets with filters |
| GET | `/api/pets/{id}` | Pet detail |
| PUT | `/api/pets/{id}` | Update pet |
| DELETE | `/api/pets/{id}` | Soft delete pet |
| GET | `/api/pets/my` | My pet listings |

### Matchmaking
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/matches/request` | Send match request |
| GET | `/api/matches/received` | Received requests |
| GET | `/api/matches/sent` | Sent requests |
| PUT | `/api/matches/{id}/accept` | Accept request |
| PUT | `/api/matches/{id}/reject` | Reject request |
| POST | `/api/matches/{id}/create-unlock-order` | Create unlock payment |
| POST | `/api/matches/{id}/unlock` | Unlock contact (₹99) |

### Subscriptions
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/subscriptions/plans` | View available plans |
| POST | `/api/subscriptions/create-order` | Create subscription order |
| POST | `/api/subscriptions/verify` | Verify & activate |
| GET | `/api/subscriptions/my` | Current subscription |

### Shop (Buy/Adopt Pets)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/shop/pets` | Browse pets for sale/adoption |
| GET | `/api/shop/pets/{id}` | Pet detail |

### Products
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/products` | Search products |
| GET | `/api/products/{id}` | Product detail |
| POST | `/api/products/{id}/review` | Add review |

### Orders
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/orders/cart/create-order` | Create cart order |
| POST | `/api/orders/cart/verify` | Verify payment |
| GET | `/api/orders/my` | Order history |
| GET | `/api/orders/{id}` | Order detail |

### Veterinarians
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/vets` | Search vets |
| GET | `/api/vets/{id}` | Vet profile |
| POST | `/api/vets/{id}/review` | Add review |

### Appointments
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/appointments/create-order` | Book appointment |
| POST | `/api/appointments/verify` | Verify payment |
| GET | `/api/appointments/my` | My appointments |
| PUT | `/api/appointments/{id}/cancel` | Cancel appointment |

### Trainers
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/trainers` | Search trainers |
| GET | `/api/trainers/{id}` | Trainer profile |
| POST | `/api/trainers/{id}/review` | Add review |

### Training Sessions
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/training/sessions/create-order` | Book session |
| POST | `/api/training/sessions/verify` | Verify payment |
| GET | `/api/training/sessions/my` | My sessions |
| PUT | `/api/training/sessions/{id}/cancel` | Cancel session |
| POST | `/api/training/packages/{id}/buy-order` | Buy package |
| POST | `/api/training/packages/{id}/buy-verify` | Verify package purchase |
| GET | `/api/training/packages/my` | My packages |

### Chatbot
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/chatbot/message` | Chat with Marshal AI |

### Admin
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/admin/dashboard` | Dashboard stats |
| GET | `/api/admin/users` | Manage users |
| PUT | `/api/admin/users/{id}/ban` | Ban user |
| PUT | `/api/admin/users/{id}/unban` | Unban user |
| GET | `/api/admin/pets` | Manage listings |
| GET | `/api/admin/vets` | Manage vets |
| PUT | `/api/admin/vets/{id}/verify` | Verify vet |
| GET | `/api/admin/trainers` | Manage trainers |
| PUT | `/api/admin/trainers/{id}/verify` | Verify trainer |
| GET | `/api/admin/orders` | Manage orders |
| PUT | `/api/admin/orders/{id}/status` | Update order status |

---

## 🗂️ Project Structure

```
petymate/
├── backend/
│   ├── src/main/java/com/petymate/
│   │   ├── config/          (Security, CORS, Razorpay, OpenAPI)
│   │   ├── controller/      (14 REST controllers)
│   │   ├── service/         (12 service interfaces + impls)
│   │   ├── repository/      (16 JPA repositories)
│   │   ├── entity/          (16 JPA entities)
│   │   ├── dto/             (Request/Response DTOs, ApiResponse<T>)
│   │   ├── security/        (JWT util, filter, UserDetailsService)
│   │   ├── exception/       (Global handler + custom exceptions)
│   │   └── util/            (PincodeUtil for geocoding)
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── schema.sql       (Complete DDL for 16 tables)
│   └── src/test/java/       (9 JUnit 5 test classes)
├── frontend/
│   ├── src/
│   │   ├── pages/           (17 page components)
│   │   ├── components/
│   │   │   ├── common/      (13 reusable components)
│   │   │   └── layout/      (Navbar, Footer)
│   │   ├── context/         (Auth, Cart, Notification)
│   │   ├── services/        (14 Axios service modules)
│   │   └── utils/           (petImageMap.js)
│   ├── tailwind.config.js
│   └── vite.config.js
├── postman/
│   └── PetyMate.postman_collection.json
└── README.md
```

---

## 🔮 Future Roadmap

- 💬 **Real-time Chat** — WebSocket/STOMP messaging between matched pet owners
- 📱 **Mobile App** — React Native app for iOS and Android
- 🧠 **AI Breed Recognition** — Upload pet photos to auto-detect breed
- 🔔 **Vaccination Reminders** — Push notifications for upcoming vaccinations
- 🛡️ **Pet Insurance** — Insurance marketplace integration
- 📍 **GPS Pet Walks** — Track and share pet walks with the community
- 🗣️ **Community Forum** — Discussion boards for pet owners
- 📹 **Video Consultations** — Live video calls with veterinarians
- 🏥 **Pet Hospital Directory** — Comprehensive hospital listings by city
- 📊 **Pet Health Tracker** — Track weight, food, and exercise over time

---

## 📜 License

This project is proprietary. All rights reserved.

---

<p align="center">
  Made with 🐾 in India · © 2024 PetyMate Pvt Ltd
</p>
