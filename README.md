# LandLens 🌍🔍 — AI-Powered Government Land Verification & Fraud Prevention Platform

[![Java Version](https://img.shields.io/badge/Java-21-orange.svg)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.0-green.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18-61DAFB.svg)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.0-3178C6.svg)](https://www.typescriptlang.org/)
[![Docker](https://img.shields.io/badge/Docker-Enabled-blue.svg)](https://www.docker.com/)
[![Firebase](https://img.shields.io/badge/Firebase-Hosting%20%7C%20Firestore-FFCA28.svg?logo=firebase&logoColor=black)](https://firebase.google.com/)
[![Render](https://img.shields.io/badge/Render-Backend%20Deployed-46E3B7.svg?logo=render&logoColor=black)](https://render.com/)
[![Cloudinary](https://img.shields.io/badge/Cloudinary-Media%20Storage-3448C5.svg?logo=cloudinary&logoColor=white)](https://cloudinary.com/)

<p align="center">
  <img src="./frontend-react/public/logo.png" alt="LandLens Logo" width="280"/>
</p>

<p align="center">
  <a href="https://landlens-c0007.web.app">
    <img src="https://img.shields.io/badge/Live_Production-https%3A%2F%2Flandlens--c0007.web.app-brightgreen?style=for-the-badge&logo=firebase" alt="Live Production URL"/>
  </a>
</p>

### 🌐 **Live Production Web Portal:** [https://landlens-c0007.web.app](https://landlens-c0007.web.app)

---

## 📑 Master Table of Contents
1. [Real-Time Need & Problem Statement](#1-real-time-need--problem-statement)
2. [Project Abilities & Core Advantages](#2-project-abilities--core-advantages)
3. [Application Screenshots & UI Showcase](#3-application-screenshots--ui-showcase)
4. [Team Members & Key Contributions](#4-team-members--key-contributions)
5. [1-Week Rapid Implementation Sprint & Bug Fixes](#5-1-week-rapid-implementation-sprint--bug-fixes)
6. [Complete Technology Stack by Service](#6-complete-technology-stack-by-service)
7. [Platform & Cloud Infrastructure Services](#7-platform--cloud-infrastructure-services)
8. [Live Deployment & Production Endpoints](#8-live-deployment--production-endpoints)
9. [Folder & Package Architecture](#9-folder--package-architecture)
10. [System Architecture & Sequence Diagrams](#10-system-architecture--sequence-diagrams)
11. [Database Module Overview & ERD](#11-database-module-overview--erd)
12. [Complete REST API Directory](#12-complete-rest-api-directory)
13. [Local Development & Setup Guide](#13-local-development--setup-guide)
14. [Environment Variables Reference](#14-environment-variables-reference)
15. [Security, Auth & Rate Limiting](#15-security-auth--rate-limiting)
16. [Application Scalability & Performance](#16-application-scalability--performance)
17. [Cloud Infrastructure Cost Projections](#17-cloud-infrastructure-cost-projections)
18. [Future Roadmap & Improvements](#18-future-roadmap--improvements)
19. [Contributing & License](#19-contributing--license)

---

## 1. Real-Time Need & Problem Statement

Real estate and land transactions are plaguing buyers, financial institutions, and government authorities with multi-billion-dollar annual losses due to:
*   **Forged Land Deeds & Documents**: Unscrupulous sellers uploading fake Patta or altered sale deeds.
*   **Double-Selling & Overlap Claims**: Selling the same piece of land to multiple buyers or claiming overlapping survey boundaries.
*   **Manual Inspection Bottlenecks**: Government officers spending weeks conducting physical site visits to verify boundary coordinates and ownership claims.
*   **Lack of Remote Trust**: Buyers buying land remotely without an immutable audit trail or interactive 360° visual evidence.

### 💡 The LandLens Solution
**LandLens** introduces an immutable, AI-driven government land verification portal. By combining **Optical Character Recognition (OCR)**, **AI Trust Scoring**, **Mapbox GIS Spatial Overlap Detection**, and **360° Panoramic Virtual Tours**, LandLens digitizes land verification into a seamless, automated, and fraud-proof experience.

---

## 2. Project Abilities & Core Advantages

*   ⚡ **Instant AI Document Verification**: Uploaded Patta, sale deeds, and tax receipts undergo automated OCR processing and AI forgery evaluation within seconds.
*   🗺️ **Interactive GIS Boundary Mapping**: Mapbox GL JS engine lets users draw, cluster, and verify exact polygon survey boundaries to catch spatial overlap claims.
*   🌐 **360° Virtual Panoramic Tours**: Allows prospective buyers and government inspectors to inspect land plots remotely in immersive 360° VR without traveling.
*   🛡️ **Multi-Tiered Government Audit Trail**: Officer verification dashboard with remarks, timeline logs (`UPLOADED` ➔ `AI_CHECK` ➔ `APPROVED` / `REJECTED`), and immutable state tracking.
*   🔑 **Developer API Ecosystem**: Allows third-party fintech, banking, and real estate apps to query land verification status via secured rate-limited API keys.

---

## 3. Application Screenshots & UI Showcase

Below is an interactive visual walkthrough of the live **LandLens** platform structured in a 3-column showcase:

### 🔐 1. Authentication, Portals & Buyer Dashboard
| Login Portal (Desktop) | Mobile Responsive Interface | Buyer Dashboard & Overview |
| :---: | :---: | :---: |
| ![Login Desktop](./dashscreenshots/login-desktop.png) | ![Login Mobile](./dashscreenshots/login-mobile.png) | ![Buyer Dashboard](./dashscreenshots/userdash.png) |

---

### 🗺️ 2. Property Marketplace, GIS Mapping & Location Details
| Property Exploration Marketplace | Mapbox GIS Boundaries | Spatial Coordinates & Land Details |
| :---: | :---: | :---: |
| ![Property Marketplace](./dashscreenshots/userexplore.png) | ![Mapbox Boundaries](./dashscreenshots/usermap.png) | ![Location Details](./dashscreenshots/loacationdetaislofland.png) |

---

### 🤖 3. AI Trust Analysis, Property Details & Seller Portal
| AI Document Verification Analysis | Property Overview & 360° VR | Land Provider / Seller Dashboard |
| :---: | :---: | :---: |
| ![AI Analysis](./dashscreenshots/aianalysis.png) | ![Property Details](./dashscreenshots/propertydetails.png) | ![Seller Dashboard](./dashscreenshots/sellerdash.png) |

---

### 💼 4. Scheduled Inspection Visits & User Account
| Scheduled Visit Bookings | User Profile & Settings | System Dashboard Overview |
| :---: | :---: | :---: |
| ![Scheduled Visits](./dashscreenshots/userschedules.png) | ![User Account](./dashscreenshots/useracct.png) | ![User Dashboard](./dashscreenshots/userdash.png) |

---

## 4. Team Members & Key Contributions

| Avatar | GitHub Profile | Developer | Role & Key Contributions |
| :---: | :--- | :--- | :--- |
| <img src="https://github.com/santhipriyaa27.png" width="65" style="border-radius: 15px;"/> | [@santhipriyaa27](https://github.com/santhipriyaa27) | **Santhi Priya** | **Team Lead, DevOps & QA Automation**<br>Managed project milestones; architected Jenkins CI/CD automation pipelines, SonarQube code quality auditing (`automate_sonar.py`), bug tracking, and test suite execution. |
| <img src="https://github.com/Pavankumarswamy.png" width="65" style="border-radius: 15px;"/> | [@Pavankumarswamy](https://github.com/Pavankumarswamy) | **Pavan Kumar Swamy** | **Technical Architect & Core Full-Stack Lead**<br>Conceived and built the core architecture; designed 3NF database schema; built React 18 + Vite UI, glassmorphism dashboards, Mapbox GL JS engine, 360° panorama viewer, and API integration. |
| <img src="https://github.com/vasavi985.png" width="65" style="border-radius: 15px;"/> | [@vasavi985](https://github.com/vasavi985) | **Rama Vasavi Patchikolla** | **Lead Backend Engineer**<br>Flawlessly developed all Spring Boot 3.4 REST APIs, JWT authentication, RBAC filters, database JPA services, and co-executed full-stack deployments and DevOps pipelines. |
| <img src="https://github.com/hemanthkotipalli.png" width="65" style="border-radius: 15px;"/> | [@hemanthkotipalli](https://github.com/hemanthkotipalli) | **Hemanth Kotipalli** | **AI & GenAI Systems Engineer**<br>Built the interactive AI Chatbot assistant, OCR document extraction pipeline, GenAI verification algorithms, and automated land trust score calculations. |
| <img src="https://github.com/keerthithammisetty.png" width="65" style="border-radius: 15px;"/> | [@keerthithammisetty](https://github.com/keerthithammisetty) | **Keerthi Thammisetty** | **Database Architect & Schema Designer**<br>Engineered and normalized the 3NF relational database schema (`schema.sql`), optimized JPA query relationships, entity mappings, and database transaction boundaries. |
| <img src="https://github.com/ramasai98.png" width="65" style="border-radius: 15px;"/> | [@ramasai98](https://github.com/ramasai98) | **Rama Sai** | **DevOps & Cloud Infrastructure Architect**<br>Architected the cloud topology, configuring CDN edge distribution, static hosting, container clusters, load balancers, and deployment automation scripts. |

---

## 5. 1-Week Rapid Implementation Sprint & Bug Fixes

In an intensive **1-week engineering sprint**, the team achieved major milestones and resolved complex technical challenges:

1.  **Frontend Modernization**: Completely ported legacy Angular code to a high-performance **React 18 + Vite** stack, boosting bundle build speed by **8x** and runtime responsiveness.
2.  **HTTPS & API Communication**: Solved browser Mixed Content and CORS challenges by establishing secure, encrypted HTTPS routing between the frontend and the cloud backend API.
3.  **Code Quality & SonarQube Automation**: Built custom Python automation (`automate_sonar.py`) to run static analysis, eliminating code smells, memory leaks, and unhandled exceptions.
4.  **Security & Credential Hardening**: Secured API keys, JWT secret management, and role-based access verification across environments.
5.  **Multi-Role Dashboards**: Built 4 distinct role-tailored portals (Buyer, Land Provider, Government Officer, Admin) in record time.

---

## 6. Complete Technology Stack by Service

| 🎨 Frontend Tier (`/frontend-react`) | ⚙️ Backend & DB Tier (`/back_end`) | ☁️ Cloud & DevOps Tier (Production) |
| :--- | :--- | :--- |
| **React 18** — Component Architecture | **Spring Boot 3.4.0** — Java 21 Framework | **Firebase Hosting** — Global SSL CDN Edge Hosting |
| **Vite 5** — Fast HMR Bundler & Compiler | **Spring Security** — Dual JWT & Firebase Auth Filter | **Render** — Managed Containerized Backend Runtime |
| **TypeScript 5.0** — Type-Safe Application | **Google Cloud Firestore** — NoSQL Document Database | **Google Cloud Platform** — Identity & Firestore Services |
| **Tailwind CSS** — Glassmorphism Design | **Firebase Admin SDK** — User Token Verification | **Cloudinary** — Media Storage & CDN Delivery |
| **Mapbox GL JS** — GIS Interactive Mapping | **Cloudinary Java/REST** — Media Integration | **Docker** — Multi-Stage Production Containerization |
| **Pannellum VR** — 360° Panorama Viewer | **BCrypt** — Secure Password Hashing | **GitHub Actions** — CI/CD Automation & Build Verification |
| **Axios** — Auth Bearer Interceptors | **Jackson & OpenAPI** — JSON & Swagger | **SonarQube** — Static Code Analysis & Security Auditing |

### 🔄 Architectural Tier Interactions
```mermaid
graph TD
    subgraph ClientLayer [Client & User Layer]
        FE[React 18 + Vite Frontend Application]
        MB[Mapbox GIS Engine]
        VR[Pannellum 360 VR Player]
    end

    subgraph HostingLayer [Global Edge CDN & Frontend Hosting]
        FH[Firebase Hosting Global CDN]
    end

    subgraph ServerLayer [Application Server Layer - Render]
        API[Spring Boot 3.4 REST API Container]
        SEC[Spring Security & Firebase Token Filter]
    end

    subgraph CloudPersistence [Cloud Services & Persistence Layer]
        FS[(Google Cloud Firestore NoSQL Database)]
        FA[Firebase Authentication & Identity]
        CD[Cloudinary Media & Document Storage]
        AI[AI Trust & OCR Engine]
    end

    FE -->|1. Request Static Bundle| FH
    FH -->|2. Deliver Optimized SPA Assets| FE
    FE -->|3. HTTPS REST API Calls| API
    API -->|4. Intercept & Validate Token| SEC
    SEC -->|5. Verify Firebase ID Token| FA
    API -->|6. Firestore Read/Write Operations| FS
    FE -->|7. Upload Images / Documents / 360 VR| CD
    API -->|8. Store Media URLs & Document References| FS
    API -->|9. Trigger Document OCR & Trust Analysis| AI
```

---

## 7. Platform & Cloud Infrastructure Services

| Domain | Service / Platform | Usage & Responsibility |
| :--- | :--- | :--- |
| **Frontend Web Hosting** | **Firebase Hosting** | Global SSL termination, HTTP/2 CDN distribution, custom domain routing for the compiled React SPA. |
| **Backend Compute** | **Render** | Managed container runtime running the Spring Boot 3.4 (Java 21) REST API with automated health monitoring. |
| **Application Database** | **Google Cloud Firestore** | Serverless, highly available NoSQL document database storing properties, user records, and verification audit trails. |
| **Identity & Auth** | **Firebase Authentication** | Secure token generation, user identity management, and role synchronization across frontend and backend. |
| **Media & Document Storage** | **Cloudinary** | Cloud object storage and optimized CDN delivery for property images, documents, deeds, and 360° virtual tours. |
| **Containerization** | **Docker** | Multi-stage Docker container build ensuring reproducible execution across local and cloud environments. |
| **GIS & Mapping Engine** | **Mapbox GL JS / OpenStreetMap** | Real-time geospatial rendering, polygon survey boundary inspection, and spatial clustering. |
| **Virtual Tour Engine** | **Pannellum VR** | Equirectangular 360° panoramic viewer enabling remote property inspection. |
| **CI/CD Automation** | **GitHub Actions** | Automated build verification, test suite execution, and continuous deployment workflows. |
| **Code Quality** | **SonarQube & Python Runner** | Static code analysis, vulnerability scanning, and code smell remediation (`automate_sonar.py`). |

---

## 8. Live Deployment & Production Endpoints

The LandLens platform is deployed and fully operational on cloud infrastructure:

*   🌐 **Live Web Portal**: [https://landlens-c0007.web.app](https://landlens-c0007.web.app)
*   ⚙️ **Backend API**: [https://landlens-0n4k.onrender.com](https://landlens-0n4k.onrender.com)
*   💓 **Backend Health Check**: [https://landlens-0n4k.onrender.com/actuator/health](https://landlens-0n4k.onrender.com/actuator/health)
*   💓 **Backend Liveness**: [https://landlens-0n4k.onrender.com/actuator/health/liveness](https://landlens-0n4k.onrender.com/actuator/health/liveness)
*   💓 **Backend Readiness**: [https://landlens-0n4k.onrender.com/actuator/health/readiness](https://landlens-0n4k.onrender.com/actuator/health/readiness)
*   🔥 **Firebase Services**: Firebase Authentication + Cloud Firestore Database
*   ☁️ **Media Storage**: Cloudinary Media Platform (Cloud Name: `jrgwnblg`, Preset: `landlens_upload`)

---

## 9. Folder & Package Architecture

### Root Directory Overview
```text
LandLens/
 ├── README.md                      # Master Unified Documentation
 ├── automate_sonar.py              # Automated SonarQube Code Quality Analysis Script
 ├── firebase.json                  # Firebase Hosting & Project Deployment Configuration
 ├── .firebaserc                    # Firebase Active Project Aliases
 ├── frontend-react/                # Production React 18 + Vite Frontend Application
 │    ├── src/                      # Components, Dashboards, Mapbox & Services
 │    ├── public/                   # Static Media Assets (logo.png, icons)
 │    ├── .env.example              # Frontend Environment Template
 │    ├── package.json              # React Dependencies & Scripts
 │    └── vite.config.ts            # Vite Compiler Configuration
 └── back_end/                      # Spring Boot 3.4 (Java 21) REST Backend Application
      ├── src/main/java/com/landlens/ # Feature Packages (Auth, Property, AI, Fraud, etc.)
      ├── src/main/resources/       # application.properties
      ├── render.yaml               # Render Cloud Blueprint & Deployment Definition
      ├── Dockerfile                # Multi-stage Docker Container Definition
      └── pom.xml                   # Maven Dependencies & Build Definitions
```

### Spring Boot Package Layout (`com.landlens`)
```text
com.landlens
 ├── LandlensApplication.java  # Application Entry Point
 ├── config                    # Firebase & Cloud Service Configuration (FirebaseConfig)
 ├── common                    # AbstractFirestoreRepository, JacksonConfig, RootController
 ├── auth                      # Security Config, JwtAuthenticationFilter, AuthService
 ├── user                      # User Profile Management & FirestoreUserRepository
 ├── property                  # Listings, Images, Videos, Saved, & FirestorePropertyRepository
 ├── document                  # Verification registry document uploads & Firestore Repository
 ├── verification              # Government Review, Timeline transitions, & Repositories
 ├── ai                        # AI scoring outputs, valuation, and Firestore Repository
 ├── fraud                     # Duplicate claim coordinates & FraudReportRepository
 ├── notification              # Real-time alerts, Firestore Notification Repository
 ├── api                       # Developer API keys, rate-limiting, and Firestore Repositories
 └── analytics                 # Daily dashboard statistics & Firestore Repository
```

---

## 10. System Architecture & Sequence Diagrams

### A. Production Cloud Topology
```mermaid
graph TD
    User([Client / Web & Mobile Browsers]) -->|1. HTTPS Request| FH[Firebase Hosting Global CDN]
    FH -->|2. Deliver Static React SPA| User
    User -->|3. HTTPS API Request| Render[Render Cloud Runtime]
    
    subgraph RenderApp [Render Backend Container]
        Render -->|4. Port 8080| SB[Spring Boot 3.4 REST Service]
        SB -->|5. Validate Auth Token| SEC[Spring Security & JWT Filter]
    end

    subgraph FirebaseCloud [Firebase & Google Cloud Platform]
        SEC -->|6. Verify Identity| FBAuth[Firebase Authentication]
        SB -->|7. Query & Persist Data| FS[(Cloud Firestore NoSQL DB)]
    end

    subgraph MediaCloud [Cloudinary CDN]
        User -->|8. Direct Media Upload| CD[Cloudinary Media Storage]
        SB -->|9. Image/Document Metadata| CD
    end

    SB -->|10. Asynchronous OCR & Trust Evaluation| AI[AI Verification Engine]
```

### B. Application Request Processing Lifecycle
```mermaid
sequenceDiagram
    autonumber
    actor Client
    participant Security as Spring Security Filter Chain
    participant Controller as REST Controller
    participant Service as Service Layer
    participant Repos as Firestore Repository
    participant DB as Cloud Firestore (NoSQL)
    participant AI as AI Engine & OCR

    Client->>Security: Send HTTP Request (e.g., POST /api/properties)
    alt Anonymous path permitted (e.g., /actuator/health)
        Security->>Controller: Forward to Controller
    else Protected path
        Note over Security: Validate JWT token or Firebase ID Token
        alt Token Valid
            Security->>Controller: Forward with Auth Principal & Role
        else Token Invalid / Missing
            Security-->>Client: Return 401 Unauthorized / 403 Forbidden
        end
    end

    Controller->>Service: Call Business Logic (e.g., createProperty)
    Service->>Repos: Invoke Repository Operation
    Repos->>DB: Query / Insert / Update (Firestore Documents)
    DB-->>Repos: Return Document Snapshots
    Repos-->>Service: Return Entity Model

    opt Needs AI Verification (Documents Uploaded)
        Service->>AI: Trigger Asynchronous Verification Task
        Note over AI: Process OCR (Patta/Sale Deed) & evaluate Trust Score
        AI->>DB: Update Verification Results & Scores
    end

    Service-->>Controller: Return DTO Payload
    Controller-->>Client: Return JSON Response + HTTP Status 200/201
```

### C. Property Verification State Machine
```mermaid
stateDiagram-v2
    [*] --> UPLOADED : User Uploads Land Details & Deeds
    
    UPLOADED --> AI_VERIFICATION_PENDING : Trigger OCR & AI Checks
    
    state AI_VERIFICATION_PENDING {
        [*] --> ExtractingDocuments
        ExtractingDocuments --> CalculatingTrustScore
        CalculatingTrustScore --> CheckingSpatialOverlap
    }
    
    AI_VERIFICATION_PENDING --> AI_REJECTED : Forgery / Overlap Detected
    AI_VERIFICATION_PENDING --> PENDING_GOVT_AUDIT : High Trust Score (Passed AI)
    
    PENDING_GOVT_AUDIT --> APPROVED : Officer Approves Claim
    PENDING_GOVT_AUDIT --> REJECTED : Officer Rejects Claim
    
    APPROVED --> LIVE : Published on Marketplace
    LIVE --> DISPUTED : Community Fraud Report Filed
    
    DISPUTED --> PENDING_GOVT_AUDIT : Re-audit Investigation
    
    AI_REJECTED --> [*]
    REJECTED --> [*]
```

### D. AI Price Estimation Flow
```mermaid
sequenceDiagram
    participant User as Buyer / Provider
    participant React as React Frontend
    participant Render as Render Cloud API
    participant AI as Spring Boot AI Engine
    participant DB as Cloud Firestore

    User->>React: Input Survey No, Area & Coordinates
    React->>Render: POST /api/ai/estimate-price
    Render->>AI: Forward Request Payload
    AI->>DB: Fetch Local Historical Sales & Base Rates
    DB-->>AI: Return Benchmark Data
    AI->>AI: Execute ML Valuation Model
    AI-->>Render: Return Price Range, SqFt Rate & Confidence Score
    Render-->>React: JSON Response Payload
    React-->>User: Render Interactive Valuation Breakdown
```

---

## 11. Database Module Overview & Entity Directory

The application data model is structured across dedicated Firestore collections utilizing the `AbstractFirestoreRepository` base layer. Every document includes standardized audit attributes:
*   `id` (`VARCHAR(36)` UUID, Primary Key / Document ID)
*   `created_at` (`TIMESTAMP`, Default creation timestamp)
*   `updated_at` (`TIMESTAMP`, Last updated timestamp)
*   `created_by` (`VARCHAR(36)` UUID, Creator reference)
*   `updated_by` (`VARCHAR(36)` UUID, Updater reference)
*   `is_active` (`BOOLEAN`, Soft-deletion status flag)

| Module | Collection / Entity | Description |
| :--- | :--- | :--- |
| **Auth & User** | `roles` | Roles mapping to RBAC privileges (`ADMIN`, `GOVERNMENT_OFFICER`, `PROVIDER`, `BUYER`). |
| | `users` | User profile, role reference, credentials hash. |
| | `refresh_tokens` | Active JWT refresh tokens for persistent sessions. |
| | `login_histories` | Security log of user login attempts. |
| **Property** | `properties` | Core property listings and ownership details. |
| **Media** | `property_images` | Image URLs, thumbnails, and custom displays. |
| | `property_videos` | Video paths, duration, and thumbnail images. |
| **Documents** | `property_documents` | Property verification documents and OCR status. |
| **Verification** | `ai_verifications` | AI-driven Trust, Forgery, and Duplicate reports. |
| | `government_verifications` | Official government verification remarks and status. |
| | `verification_timelines` | Historic log of all verification events. |
| **Fraud & Duplicate**| `duplicate_claims` | AI-flagged duplicate submissions for overlapping properties. |
| | `fraud_reports` | Public or officer reported fraud details. |
| **Interactions** | `property_visits` | Scheduled viewings by buyers. |
| | `saved_properties` | Bookmarked listings for prospective buyers. |
| **Notifications & Chat**| `notifications` | Read/unread alerts for users. |
| | `ai_conversations` | Conversation threads with AI chat. |
| | `ai_messages` | Individual messages within an AI conversation. |
| **Developer API** | `api_keys` | Hashed authentication keys for developers. |
| | `api_usages` | Daily rolled up API access quotas. |
| | `api_logs` | Trace log of developer API requests. |
| | `api_rate_limits` | Current rate limiting windows for active keys. |
| **Analytics** | `daily_analytics` | Pre-aggregated system metrics per day. |

---

### Entity Relationship Diagram (ERD)

```mermaid
erDiagram
    roles {
        string id PK
        string name UK
        string description
        timestamp created_at
        timestamp updated_at
        boolean is_active
    }

    users {
        string id PK
        string email UK
        string password_hash
        string first_name
        string last_name
        string phone_number
        string role_id FK
        timestamp created_at
        timestamp updated_at
        boolean is_active
    }

    refresh_tokens {
        string id PK
        string user_id FK
        string token UK
        timestamp expiry_date
        boolean revoked
        timestamp created_at
        timestamp updated_at
        boolean is_active
    }

    properties {
        string id PK
        string property_code UK
        string title
        string category
        decimal area
        decimal price
        text description
        string survey_number
        string address
        decimal latitude
        decimal longitude
        string district
        string village
        string state
        string pincode
        string three_sixty_image_url
        string status
        string provider_id FK
        timestamp created_at
        timestamp updated_at
        boolean is_active
    }

    property_images {
        string id PK
        string property_id FK
        string image_url
        string thumbnail_url
        int display_order
        timestamp created_at
        timestamp updated_at
        boolean is_active
    }

    property_videos {
        string id PK
        string property_id FK
        string video_url
        int duration
        string thumbnail_url
        timestamp created_at
        timestamp updated_at
        boolean is_active
    }

    property_documents {
        string id PK
        string property_id FK
        string document_type
        string file_url
        string ocr_status
        string verification_status
        timestamp created_at
        timestamp updated_at
        boolean is_active
    }

    ai_verifications {
        string id PK
        string property_id FK
        decimal ai_trust_score
        decimal forgery_score
        decimal duplicate_score
        boolean ownership_match
        decimal risk_score
        text summary
        decimal confidence
        timestamp generated_date
        timestamp created_at
        timestamp updated_at
        boolean is_active
    }

    government_verifications {
        string id PK
        string property_id FK
        string officer_id FK
        text remarks
        string status
        timestamp verified_date
        timestamp created_at
        timestamp updated_at
        boolean is_active
    }

    verification_timelines {
        string id PK
        string property_id FK
        timestamp timestamp
        string action
        text remarks
        string user_id FK
        timestamp created_at
        timestamp updated_at
        boolean is_active
    }

    duplicate_claims {
        string id PK
        string property_a_id FK
        string property_b_id FK
        decimal similarity
        text reason
        string status
        string decision
        timestamp created_at
        timestamp updated_at
        boolean is_active
    }

    fraud_reports {
        string id PK
        string reporter_id FK
        string property_id FK
        string reason
        text description
        string status
        string officer_id FK
        timestamp created_at
        timestamp updated_at
        boolean is_active
    }

    property_visits {
        string id PK
        string buyer_id FK
        string property_id FK
        date visit_date
        time visit_time
        string status
        timestamp created_at
        timestamp updated_at
        boolean is_active
    }

    saved_properties {
        string id PK
        string buyer_id FK
        string property_id FK
        timestamp created_at
        timestamp updated_at
        boolean is_active
    }

    roles ||--o{ users : "assigns"
    users ||--o{ refresh_tokens : "generates"
    users ||--o{ properties : "owns"
    users ||--o{ government_verifications : "performs"
    users ||--o{ verification_timelines : "triggers"
    users ||--o{ fraud_reports : "reports"
    users ||--o{ property_visits : "schedules"
    users ||--o{ saved_properties : "saves"

    properties ||--o{ property_images : "has"
    properties ||--o{ property_videos : "has"
    properties ||--o{ property_documents : "requires"
    properties ||--|| ai_verifications : "analyzed"
    properties ||--|| government_verifications : "assessed"
    properties ||--o{ verification_timelines : "logs"
    properties ||--o{ duplicate_claims : "acts-as-A"
    properties ||--o{ duplicate_claims : "acts-as-B"
    properties ||--o{ fraud_reports : "accused-in"
    properties ||--o{ property_visits : "hosts"
    properties ||--o{ saved_properties : "saved-in"
```

---

## 12. Complete REST API Directory

### 🔐 1. Authentication (`/api/auth`)
*   `POST /api/auth/register` — Register new user account (`BUYER`, `PROVIDER`, `GOVERNMENT_OFFICER`, `ADMIN`)
*   `POST /api/auth/login` — Authenticate credentials & generate JWT Access/Refresh tokens
*   `POST /api/auth/refresh` — Rotate expired access token using valid refresh token
*   `POST /api/auth/logout` — Revoke active refresh token session
*   `GET /api/auth/me` — Fetch currently authenticated user profile

### 🏡 2. Property Management (`/api/properties`)
*   `GET /api/properties` — Search & list properties with filters (city, state, price range, land type)
*   `GET /api/properties/{id}` — Fetch detailed property metadata, images, videos, and verification status
*   `POST /api/properties` — Create a new property listing (Providers only)
*   `PUT /api/properties/{id}` — Update existing property details
*   `DELETE /api/properties/{id}` — Soft-delete property listing (Admin/Owner)
*   `POST /api/properties/{id}/images` — Upload property gallery photos
*   `POST /api/properties/{id}/video` — Upload property walk-through video
*   `POST /api/properties/{id}/panorama` — Upload 360° virtual tour panorama image

### 📄 3. Documents & Verification (`/api/documents` & `/api/verification`)
*   `POST /api/documents/upload` — Upload land deed, Patta, tax receipt, or survey certificate
*   `GET /api/documents/property/{propertyId}` — Retrieve documents attached to a property
*   `GET /api/verification/{propertyId}` — View government audit status & AI verification score
*   `POST /api/verification/{propertyId}/approve` — Officer approval of land verification
*   `POST /api/verification/{propertyId}/reject` — Officer rejection with audit remarks
*   `GET /api/verification/{propertyId}/timeline` — Audit log timeline of state changes

### 🤖 4. AI Engine & Valuation (`/api/ai`)
*   `POST /api/ai/estimate-price` — Calculate AI market valuation based on survey number & coordinates
*   `POST /api/ai/verify-documents` — Trigger OCR document text extraction & authenticity validation
*   `POST /api/ai/chat` — Interact with LandLens AI conversational assistant

### 🚨 5. Fraud Detection (`/api/fraud`)
*   `POST /api/fraud/report` — Submit community land dispute or fraud report
*   `GET /api/fraud/overlap-check` — Evaluate spatial coordinate overlaps between registered lands
*   `GET /api/fraud/reports` — Review fraud investigation queue (Admin/Officer)

### 📈 6. Analytics & API Key Operations (`/api/analytics` & `/api/developer`)
*   `GET /api/analytics/daily` — Daily pre-aggregated platform metrics (views, listings, verifications)
*   `POST /api/developer/keys` — Generate developer API access key
*   `GET /api/developer/usage` — Track API request usage and rate-limit counters

---

## 13. Local Development & Setup Guide

### A. Frontend Setup (`/frontend-react`)
```bash
cd frontend-react
npm install
npm run dev
```
The frontend will run at `http://localhost:5173`.

### B. Backend Setup (`/back_end`)
```powershell
cd back_end
.\mvnw.cmd spring-boot:run
```
The server will start listening at `http://localhost:8080`.

### C. Docker Deployment (Local & Production Container)
```bash
cd back_end
docker build -t landlens-backend .
docker run -p 8080:8080 landlens-backend
```
Boots the Spring Boot application cleanly inside an isolated multi-stage Docker container.

---

## 14. Environment Variables Reference

### Backend Configuration (`/back_end`)
| Variable Name | Description | Default Fallback (Development) |
|---|---|---|
| `PORT` | Embedded server listening port | `8080` |
| `FIREBASE_API_KEY` | Firebase Web API Key | Configured in environment |
| `FIREBASE_PROJECT_ID` | Google Cloud / Firebase Project ID | Configured in environment (`landlens-c0007`) |
| `FIREBASE_CREDENTIALS_JSON` | Firebase Admin SDK service account credentials JSON string | Set via cloud environment secret |
| `JWT_SECRET` | HMAC SHA-256 Signature Secret for local JWT validation | Development default secret |
| `JWT_EXPIRATION_MS` | JWT Access Token duration (ms) | `86400000` (24 Hours) |
| `JWT_REFRESH_EXPIRATION_MS` | Refresh Token expiry duration (ms) | `2592000000` (30 Days) |
| `OPENAI_API_KEY` | NVIDIA / AI integration API key | Development fallback |

### Frontend Configuration (`/frontend-react`)
| Variable Name | Description | Example / Production Value |
|---|---|---|
| `VITE_API_BASE_URL` | Base URL for backend REST API | `https://landlens-0n4k.onrender.com` |
| `VITE_CLOUDINARY_CLOUD_NAME` | Cloudinary public Cloud Name | `jrgwnblg` |
| `VITE_CLOUDINARY_UPLOAD_PRESET` | Cloudinary unsigned upload preset | `landlens_upload` |
| `VITE_MAPBOX_ACCESS_TOKEN` | Mapbox GL JS Access Token | Configured in environment |
| `VITE_MAPBOX_STYLE` | Mapbox tile layer style URL | `mapbox://styles/mapbox/satellite-streets-v12` |

---

## 15. Security, Auth & Rate Limiting

*   **Credential Encryption**: Secure password hashing using BCrypt for password fields.
*   **Dual Token Authorization**: Custom `JwtAuthenticationFilter` intercepts HTTP headers to validate both standard JWT bearer signatures and Google/Firebase ID tokens, automatically extracting user roles (`ROLE_PROVIDER`, `ROLE_BUYER`, `ROLE_GOVERNMENT_OFFICER`, `ROLE_ADMIN`).
*   **External API Guarding**: Interceptor (`ApiKeyInterceptor`) locks all `/api/v1/external/**` routes requiring `x-api-key`.
*   **Rate Limits**: Automated tracker logs developer usage and blocks keys exceeding defined thresholds (`429 Rate Limit Exceeded`).

---

## 16. Application Scalability & Performance

*   📈 **Managed Container Compute**: Render cloud environment continuously manages container health, automated restarts, and zero-downtime rolling deployments.
*   ⚡ **Global Edge CDN Caching**: Firebase Hosting delivers compiled React single-page assets across Google's global SSD-backed CDN edges with sub-100ms load times worldwide.
*   🗄️ **Serverless Document Scalability**: Google Cloud Firestore provides automatic horizontal scaling, auto-sharding, and zero connection pool bottlenecks under high concurrency.
*   ☁️ **High-Performance Media Delivery**: Cloudinary CDN automatically optimizes, compresses, and delivers property images, virtual tour panoramas, and legal documents on-the-fly.
*   🚀 **Sub-Second API Response Times**: Optimized Spring Boot REST endpoints with non-blocking Firestore operations minimize latency.

---

## 17. Cloud Infrastructure Cost Projections

| User Scale | Frontend (Firebase Hosting) | Backend API (Render) | Database & Auth (Firestore & Firebase) | Media Storage (Cloudinary) | Total Estimated Monthly Cost |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **100 Users** | **$0** *(Spark Free Tier)* | **$0 - $7** *(Render Starter)* | **$0** *(Free Quota)* | **$0** *(Free Tier)* | **$0 - $7 / mo** (~₹0 - ₹600) |
| **1,000 Users** | **$0 - $2** | **$7 - $15** *(Render Individual)* | **$2 - $5** | **$0 - $5** | **~$10 - $25 / mo** (~₹800 - ₹2,000) |
| **10,000 Users** | **$5 - $10** | **$25 - $50** *(Render Pro)* | **$15 - $30** | **$15 - $30** | **~$60 - $120 / mo** (~₹5k - ₹10k) |
| **100,000 (1 Lakh)**| **$25 - $50** | **$85 - $150** *(Render Scale)* | **$80 - $160** *(Blaze Pay-as-you-go)* | **$50 - $100** | **~$240 - $460 / mo** (~₹20k - ₹38k) |

---

## 18. Future Roadmap & Improvements

*   **Test Isolation with H2 / Testcontainers**: Automated mock test profile (`application-test.properties`) so build steps execute offline cleanly.
*   **Redis Caching Layer**: Cache wrapper for public property search endpoints to reduce Firestore read hits.
*   **Asynchronous Message Queue**: Transition AI processing and OCR triggers from inline threads to RabbitMQ/Kafka.
*   **Geospatial Clustering**: Enhanced GeoFirestore geospatial indexing for ultra-dense polygon boundary searches and heatmaps.

---

## 19. Contributing & License

1.  Create a feature branch from `main` (`git checkout -b feature/amazing-feature`).
2.  Commit your changes using meaningful, structured commit messages.
3.  Submit a Pull Request targeting the `main` branch.
 
