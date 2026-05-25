# Quick start (local)

Get the app running on your machine in **~15 minutes** (after Docker + Java are installed).

## Prerequisites

- **Docker Desktop** (running)
- **Java 21+** and **Maven**
- **Node.js 18+** (frontend)

## Step 1 — Infrastructure

From project root:

```powershell
copy .env.example .env
.\scripts\start-infra.ps1
```

Or:

```powershell
docker compose -f docker-local-compose.yml up -d
```

Wait until Kafka is healthy (~30–60 seconds).

## Step 2 — Java services (order matters)

Set environment variable (Windows PowerShell), **once per terminal session**:

```powershell
$env:CONFIG_REPO_PATH = "C:\full\path\to\cleaning-booking-microservices-main\config-repo"
```

Start each service (IDE Run or `mvn spring-boot:run` in each folder):

| # | Service | Port |
|---|---------|------|
| 1 | `config-server` | 8888 |
| 2 | `discovery-server` | 8761 |
| 3 | `api-gateway` | **8080** |
| 4 | `professional-service` | 8081 |
| 5 | `booking-service` | 8082 |
| 6 | `auth-service` | 8086 |
| 7 | `notification-service` | 8087 |
| 8 | `ai-service` (optional, chatbot) | 8085 |

Check:

```powershell
.\scripts\check-health.ps1
```

Gateway must return **OK**: http://localhost:8080/actuator/health

## Step 3 — Frontend

```powershell
cd frontend
npm install
copy .env.example .env
npm run dev
```

Open **http://localhost:5173** — navbar should show **API: Connected**.

## Step 4 — Demo login

| Role | Email | Password |
|------|--------|----------|
| Customer | `customer@justlife.com` | `customer123` |
| Cleaner | `cleaner@justlife.com` | `cleaner123` |

Then: **Services → Book → My Bookings**.

## Troubleshooting

| Problem | Fix |
|---------|-----|
| API disconnected | Start `api-gateway` and earlier services |
| Login 401/500 | Start `auth-service`, check Docker postgres-auth |
| Booking fails | `booking-service` + `professional-service` |
| Friday error | Expected — no bookings on Fridays |
| Chatbot empty | Start `ai-service`, set `ai-service/.env` (see `docs/FEATURE_AI_CHATBOT.md`) |
