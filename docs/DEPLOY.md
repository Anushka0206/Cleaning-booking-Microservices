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

### "Invalid CORS request" on Vercel login

Usually two causes:

1. **Auth service CORS** only allowed `localhost:5173` — fixed in `auth-service` `WebConfig` (allows `https://*.vercel.app`). Restart **auth-service** after pulling.
2. **`VITE_API_BASE_URL=http://localhost:8080`** on Vercel — the browser cannot call your laptop from a public URL. You need a **public** API URL (VM IP, ngrok, etc.) in Vercel env, then **Redeploy**.

Until then, use **http://localhost:5173** for login demo (with backend running locally).

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

**Full step-by-step:** **[DEPLOY_ORACLE.md](./DEPLOY_ORACLE.md)** (recommended).

Quick summary:

1. Oracle Always Free Ubuntu VM (ARM, 12GB+ RAM)
2. Open ports **22, 80, 443, 8080**
3. `git clone` → `cp .env.example .env` → set `JWT_SECRET` + `API_DOMAIN` (DuckDNS)
4. `docker compose -f docker-compose.deploy.yml up -d --build`
5. Vercel: `VITE_API_BASE_URL=https://your-subdomain.duckdns.org` → **Redeploy**

Use **HTTPS** API URL for Vercel (not `http://VM_IP:8080`) — see deploy guide.

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
