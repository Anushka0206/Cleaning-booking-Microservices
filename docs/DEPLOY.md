# Deploy (free — for resume link)

Easiest **free** setup for a resume:

1. **Frontend** → [Vercel](https://vercel.com) (free, 10 minutes)
2. **Backend** → [Oracle Cloud Always Free VM](https://www.oracle.com/cloud/free/) (free, ~1–2 hours once)

## Is it easy?

| Part | Difficulty | Time |
|------|------------|------|
| Vercel frontend | Easy | ~10 min |
| Oracle VM backend | Medium (one-time) | 1–2 h |
| **Resume link** | Copy Vercel URL | — |

Without a public backend, the Vercel site shows UI only — **login/booking will not work**.

---

## A) Deploy frontend (Vercel)

1. Push project to **GitHub**
2. https://vercel.com → Import repository
3. Settings:
   - **Root Directory:** `frontend`
   - **Framework:** Vite (auto)
   - **Build:** `npm run build`
   - **Output:** `dist`
4. Environment variable:

   ```
   VITE_API_BASE_URL=http://YOUR_VM_PUBLIC_IP:8080
   ```

   Use your Oracle VM IP after step B (or redeploy after).

5. Deploy → you get: `https://your-app.vercel.app`

**Resume line:**

> Live demo: https://your-app.vercel.app

`frontend/vercel.json` is already included for SPA routing.

---

## B) Deploy backend (Oracle Cloud Free VM)

1. Create an **Always Free** Ubuntu VM
2. Install Docker + Git
3. Clone repo, copy `.env.example` → `.env`
4. Run:

   ```bash
   docker compose -f docker-local-compose.yml up -d
   ```

5. Start Java services (same order as [QUICKSTART.md](./QUICKSTART.md)) — or use full `docker-compose.yml` if you build images
6. Open firewall port **8080** (Oracle security list + `ufw` if used)
7. Test: `http://VM_IP:8080/actuator/health`
8. Update Vercel `VITE_API_BASE_URL` → **Redeploy** frontend

Optional: free hostname with [DuckDNS](https://www.duckdns.org) + HTTPS (Caddy).

---

## C) Resume text template

```
Cleaning Booking Microservices
• Live UI: https://your-app.vercel.app
• GitHub: https://github.com/YOUR_USERNAME/repo-name
• Stack: React, Spring Boot, PostgreSQL, Kafka, JWT, Eureka, API Gateway
```

---

## What not to use (for this project)

- Multiple Render free apps (sleep, no Kafka)
- Frontend-only deploy without saying "UI demo" in resume

See also: [WORKFLOW.md](./WORKFLOW.md)
