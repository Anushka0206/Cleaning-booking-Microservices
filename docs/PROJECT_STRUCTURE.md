# Project structure

```
cleaning-booking-microservices-main/
│
├── frontend/                 # React + Vite UI (port 5173)
├── api-gateway/              # Entry point (port 8080)
├── discovery-server/         # Eureka (8761)
├── config-server/            # Spring Cloud Config (8888)
├── config-repo/              # YAML configs for all services
├── common/                   # Shared Java library
│
├── auth-service/             # Register / login, JWT (8086)
├── booking-service/            # Bookings + availability (8082)
├── professional-service/     # Vehicles + cleaners (8081)
├── notification-service/   # Cleaner alerts via Kafka (8087)
├── ai-service/               # Chatbot / FAQ (8085)
│
├── docker/                   # Docker-related assets
├── docker-local-compose.yml  # Local infra only (DB, Kafka, Redis…)
├── docker-compose.yml        # Full stack in Docker (optional)
├── postman_collection/       # API tests
├── screenshots/              # README images
├── scripts/                  # start-infra.ps1, check-health.ps1
├── docs/                     # Guides (this folder)
├── misc/                     # Unrelated files (not part of app)
├── .env.example              # Copy to .env for Docker
└── README.md                 # Start here
```

## What each doc is for

| File | Purpose |
|------|---------|
| [../README.md](../README.md) | Overview + links |
| [QUICKSTART.md](./QUICKSTART.md) | Run locally |
| [DEPLOY.md](./DEPLOY.md) | Free deploy for resume |
| [WORKFLOW.md](./WORKFLOW.md) | Customer/cleaner flows |
| [CASE_STUDY.md](./CASE_STUDY.md) | Original case study + API tables |
| [FEATURE_AI_CHATBOT.md](./FEATURE_AI_CHATBOT.md) | OpenAI chatbot setup |

## Ports (local)

| Port | Service |
|------|---------|
| 5173 | Frontend |
| 8080 | API Gateway |
| 8761 | Eureka |
| 8888 | Config Server |
| 8081 | Professional |
| 8082 | Booking |
| 8086 | Auth |
| 8087 | Notification |
| 8085 | AI |
| 9092 | Kafka |
| 5433–5436 | PostgreSQL (per service DB) |
