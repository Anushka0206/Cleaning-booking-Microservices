# Cleaning Booking Microservices

<p align="center">
  <img src="screenshots/main_image.png" alt="Cleaning Booking" width="700">
</p>

Home-cleaning **booking platform**: customers book visits, cleaners get notifications, availability and business rules enforced in the backend. Includes a **React** UI, **Spring Boot** microservices, **Kafka**, and an optional **AI chatbot**.

| | |
|---|---|
| **Live demo** | _Add after deploy:_ `https://your-app.vercel.app` |
| **Run locally** | [docs/QUICKSTART.md](docs/QUICKSTART.md) |
| **Deploy (free)** | [docs/DEPLOY.md](docs/DEPLOY.md) · **Oracle backend:** [docs/DEPLOY_ORACLE.md](docs/DEPLOY_ORACLE.md) |
| **Project layout** | [docs/PROJECT_STRUCTURE.md](docs/PROJECT_STRUCTURE.md) |

---

## Features

- JWT **auth** (customer + cleaner roles)
- **Availability** by date and by slot
- **Create / reschedule / cancel** bookings
- **Kafka** events → cleaner **notifications**
- **React** app: services, week calendar, profile, my bookings
- **AI chatbot** (OpenAI or FAQ fallback) — [setup](docs/FEATURE_AI_CHATBOT.md)
- Observability: Prometheus, Grafana, Zipkin (Docker)

---

## Tech stack

| Layer | Technologies |
|-------|----------------|
| Frontend | React 18, Vite, Tailwind, React Router |
| Backend | Java, Spring Boot 3, Spring Cloud Gateway, Eureka, Config Server |
| Data | PostgreSQL, Flyway, Redis |
| Messaging | Kafka |
| Auth | JWT, BCrypt |
| API docs | OpenAPI via gateway |

---

## Project structure (short)

```
frontend/              → UI (http://localhost:5173)
api-gateway/           → http://localhost:8080
auth-service/          → login & register
booking-service/       → bookings & availability
professional-service/  → vehicles & cleaners
notification-service/  → cleaner alerts
ai-service/            → chatbot
config-repo/           → shared YAML config
docker-local-compose.yml → Postgres, Kafka, Redis…
docs/                  → guides
scripts/               → helper PowerShell scripts
```

Full map: **[docs/PROJECT_STRUCTURE.md](docs/PROJECT_STRUCTURE.md)**

---

## Quick start

### 1. Infrastructure (Docker)

```powershell
copy .env.example .env
.\scripts\start-infra.ps1
```

### 2. Java services

Set config path (adjust to your machine):

```powershell
$env:CONFIG_REPO_PATH = "C:\path\to\this-repo\config-repo"
```

Start in order: **config-server** → **discovery-server** → **api-gateway** → **professional-service** → **booking-service** → **auth-service** → **notification-service** → *(optional)* **ai-service**.

Details: **[docs/QUICKSTART.md](docs/QUICKSTART.md)**

### 3. Frontend

```powershell
cd frontend
npm install
npm run dev
```

Open **http://localhost:5173** — navbar **API: Connected** = ready.

### 4. Demo accounts

| Role | Email | Password |
|------|--------|----------|
| Customer | `customer@justlife.com` | `customer123` |
| Cleaner | `cleaner@justlife.com` | `cleaner123` |

Health check:

```powershell
.\scripts\check-health.ps1
```

---

## Frontend routes

| Route | Description |
|-------|-------------|
| `/` | Home |
| `/services` | All packages (+ search) |
| `/book/:id` | Book (login required) |
| `/availability` | Week calendar + slots |
| `/bookings` | My bookings, cancel, reschedule |
| `/profile` | Name, phone, address |
| `/login` | Sign in / register |
| `/cleaner` | Cleaner notifications |

---

## Main APIs (via gateway `http://localhost:8080`)

| Method | Endpoint |
|--------|----------|
| POST | `/api/auth/register`, `/api/auth/login` |
| GET | `/api/availability?date=` |
| GET | `/api/availability/slot?...` |
| POST | `/api/bookings` |
| GET | `/api/bookings/me` |
| PUT | `/api/bookings/{id}` |
| POST | `/api/bookings/{id}/cancel` |
| POST | `/api/bookings/{id}/cleaner-cancel` |

Postman: `postman_collection/` · Full tables: **[docs/CASE_STUDY.md](docs/CASE_STUDY.md)**

---

## Deploy for resume (free)

**Easy path:** Frontend on **Vercel** (~10 min) + backend on **Oracle Cloud free VM**.

Step-by-step: **[docs/DEPLOY.md](docs/DEPLOY.md)**

---

## Documentation index

| Document | Content |
|----------|---------|
| [docs/QUICKSTART.md](docs/QUICKSTART.md) | Run everything locally |
| [docs/DEPLOY.md](docs/DEPLOY.md) | Free hosting + resume link |
| [docs/WORKFLOW.md](docs/WORKFLOW.md) | Customer & cleaner flows |
| [docs/PROJECT_STRUCTURE.md](docs/PROJECT_STRUCTURE.md) | Folders & ports |
| [docs/CASE_STUDY.md](docs/CASE_STUDY.md) | Original case study, rules, screenshots |
| [docs/FEATURE_AI_CHATBOT.md](docs/FEATURE_AI_CHATBOT.md) | Chatbot / OpenAI |
| [frontend/README.md](frontend/README.md) | Frontend-only notes |

---

## Business rules (summary)

| Rule | Detail |
|------|--------|
| Fridays | No bookings |
| Hours | 08:00 – 22:00 |
| Duration | 2h or 4h only |
| Team size | 1–3 pros, same vehicle |
| Break | 30 min between jobs |

---

## License & credits

Based on the [cleaning-booking-microservices](https://github.com/Rapter1990/cleaning-booking-microservices) case study architecture. Extended with auth, notifications, React UI, and AI assistant.
