# Application workflow

## Customer

1. **Register / login** → JWT stored in browser
2. **Profile** (optional) → name, phone, address
3. **Services** → pick package → **Book**
4. **Availability** (optional) → week calendar, pick day
5. Choose **date** (not Friday) + **time slot** from API
6. **Confirm booking** → `POST /api/bookings`
7. **My bookings** → reschedule (`PUT`) or cancel (`POST .../cancel`)

## Cleaner

1. **Login** as cleaner (demo account)
2. **Cleaner dashboard** → notifications from Kafka
3. **Cancel booking** → frees slot for others

## System (behind the scenes)

```
Browser → API Gateway (8080) → microservices
Booking created → Kafka booking.events → notification-service
```

## Business rules (short)

- No **Fridays**
- Hours **08:00–22:00**
- Duration **2h or 4h**
- **1–3** professionals, same vehicle
- **30 min** break between appointments

Full rules: [CASE_STUDY.md](./CASE_STUDY.md)
