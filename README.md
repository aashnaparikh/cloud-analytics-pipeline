# Cloud Analytics Pipeline

A production-grade, real-time cloud analytics platform built for engineering interviews. Demonstrates Java/Spring Boot backend engineering, React frontend, PostgreSQL, Redis caching, Docker containerization, and GitHub Actions CI/CD.

---

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        nginx (port 80)                       │
│                    React SPA + API Proxy                     │
└─────────────────────────┬───────────────────────────────────┘
                          │ /api/*
┌─────────────────────────▼───────────────────────────────────┐
│              Spring Boot 3.2 (Java 21)  :8080               │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌────────────┐  │
│  │ Auth/JWT │  │  Events  │  │ Metrics  │  │ Analytics  │  │
│  └──────────┘  └──────────┘  └──────────┘  └────────────┘  │
│         ↕ JPA/Hibernate         ↕ Redis Cache               │
└──────────────┬──────────────────────────────────────────────┘
               │
   ┌───────────┴──────────────┐
   │                          │
┌──▼──────────┐         ┌─────▼──────┐
│ PostgreSQL  │         │   Redis    │
│  port 5432  │         │  port 6379 │
│ (Flyway)    │         │ (LRU 256MB)│
└─────────────┘         └────────────┘
```

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 21, Spring Boot 3.2, Spring Security, JPA/Hibernate |
| Auth | JWT (jjwt 0.12), BCrypt, method-level `@PreAuthorize` |
| Database | PostgreSQL 16, Flyway migrations |
| Cache | Redis 7 (Spring Cache abstraction) |
| API Docs | SpringDoc OpenAPI / Swagger UI |
| Observability | Micrometer → Prometheus → Grafana |
| Frontend | React 18, TypeScript, Vite, TanStack Query, Recharts, Tailwind CSS, Zustand |
| Containerization | Docker multi-stage builds, docker-compose |
| CI/CD | GitHub Actions (test → build → push GHCR → deploy Render) |

---

## Quick Start

### Option A — Full Docker stack (recommended)

```bash
# 1. Copy env file
cp .env.example .env

# 2. Start everything
docker compose up --build -d

# 3. Open the app
open http://localhost          # React dashboard
open http://localhost:8080/swagger-ui   # API docs
open http://localhost:9090     # Prometheus
open http://localhost:3001     # Grafana (admin / admin)
```

Demo credentials: `admin@demo.com` / `Demo1234!`

### Option B — Local development

**Prerequisites:** Java 21, Maven 3.9+, Node 20, Docker (for infra)

```bash
# Start only infra
docker compose -f docker-compose.dev.yml up -d

# Backend
cd backend
./mvnw spring-boot:run

# Frontend (new terminal)
cd frontend
npm install
npm run dev
# → http://localhost:5173
```

---

## API Endpoints

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | `/api/v1/auth/register` | None | Register user |
| POST | `/api/v1/auth/login` | None | Get JWT token |
| POST | `/api/v1/events` | ANALYST+ | Ingest single event |
| POST | `/api/v1/events/batch` | ANALYST+ | Ingest up to 1000 events |
| GET | `/api/v1/events` | ANY | Query events (paginated, filterable) |
| GET | `/api/v1/events/sources` | ANY | List distinct sources |
| POST | `/api/v1/metrics` | ANALYST+ | Record metric data point |
| GET | `/api/v1/metrics/timeseries` | ANY | Hourly time series |
| GET | `/api/v1/metrics/aggregates` | ANY | AVG/MIN/MAX/SUM by metric |
| GET | `/api/v1/analytics/summary` | ANY | Full dashboard summary |
| GET | `/actuator/health` | None | Health check |
| GET | `/actuator/prometheus` | ADMIN | Prometheus metrics |

Full interactive docs: `http://localhost:8080/swagger-ui`

---

## Project Structure

```
cloud-analytics-pipeline/
├── backend/                     # Spring Boot application
│   ├── src/main/java/com/cloudanalytics/
│   │   ├── config/              # Security, Redis, OpenAPI config
│   │   ├── controller/          # REST controllers
│   │   ├── dto/                 # Request/response DTOs
│   │   ├── entity/              # JPA entities
│   │   ├── exception/           # Global error handling (RFC 9457)
│   │   ├── repository/          # Spring Data JPA repositories
│   │   ├── security/            # JWT filter + service
│   │   └── service/             # Business logic layer
│   ├── src/main/resources/
│   │   ├── db/migration/        # Flyway V1–V5 SQL migrations
│   │   └── application.yml      # Config (profiles: default, test)
│   └── Dockerfile               # Multi-stage JDK21 → JRE21
├── frontend/                    # React TypeScript SPA
│   ├── src/
│   │   ├── components/          # charts/, common/, dashboard/, layout/
│   │   ├── pages/               # Dashboard, Events, Metrics, Login
│   │   ├── services/            # Axios API clients
│   │   ├── store/               # Zustand auth store
│   │   └── types/               # TypeScript interfaces
│   ├── nginx.conf               # SPA routing + API proxy
│   └── Dockerfile               # Multi-stage Node20 → nginx
├── monitoring/
│   ├── prometheus.yml
│   └── grafana/provisioning/
├── .github/workflows/
│   ├── ci.yml                   # Main CI/CD pipeline
│   └── pr-check.yml             # PR quality gate
├── scripts/seed.sh              # API-based seed script
├── docker-compose.yml           # Full stack
└── docker-compose.dev.yml       # Dev infra only
```

---

## CI/CD Pipeline

```
push to main
     │
     ├─► backend-ci   Java 21 + Postgres + Redis service containers
     │                ./mvnw verify (tests + JaCoCo coverage)
     │
     ├─► frontend-ci  Node 20 + tsc + vite build
     │
     └─► docker-build  Build & push to GHCR (multi-arch)
              │
              └─► deploy  Trigger Render deploy hook
```

---

## Key Design Decisions

- **Multi-tenancy** — every event/metric is scoped to a `tenant_id` derived from the JWT; no cross-tenant data leakage
- **Pagination everywhere** — all list endpoints are paginated with max page size enforcement
- **Redis caching** — analytics summary cached for 2 min, metric names for 10 min; evicted on writes
- **RFC 9457 errors** — `ProblemDetail` on all error responses (Spring 6 native)
- **Flyway migrations** — database schema changes are versioned and applied on startup
- **Batch ingestion** — events and metrics support batch endpoints (up to 1000) with JPA batch inserts
- **Non-root Docker** — backend container runs as `appuser:appgroup`; JVM tuned for containers via `UseContainerSupport`
- **Observability** — Micrometer → Prometheus + Grafana stack included out of the box

---

## Running Tests

```bash
cd backend
./mvnw test              # unit tests only
./mvnw verify            # unit + integration (requires Docker for Testcontainers)
```

Coverage report: `backend/target/site/jacoco/index.html`
