# Deploy backend on Oracle Cloud (free VM)

Use this so **Vercel** (HTTPS) can talk to your API.  
**Do not** use bare `http://VM_IP:8080` in Vercel — browsers block HTTPS → HTTP (mixed content).

Recommended: **DuckDNS** (free subdomain) + **Caddy** (HTTPS), included in `docker-compose.deploy.yml`.

---

## Overview

| Step | What |
|------|------|
| 1 | Create Oracle Always Free Ubuntu VM |
| 2 | Open ports 22, 80, 443, 8080 |
| 3 | Install Docker |
| 4 | Clone GitHub repo |
| 5 | Configure `.env` + DuckDNS |
| 6 | `docker compose -f docker-compose.deploy.yml up -d --build` |
| 7 | Vercel `VITE_API_BASE_URL=https://your-api.duckdns.org` → Redeploy |

**First build time:** ~20–40 minutes on ARM VM (Maven builds all services).

---

## Part 1 — Oracle VM

1. https://www.oracle.com/cloud/free/ → sign up  
2. **Compute → Instances → Create instance**  
3. Settings:
   - **Name:** `cleaning-booking-vm`
   - **Image:** Ubuntu 22.04 or 24.04 (aarch64 / ARM — Always Free)
   - **Shape:** Ampere — **VM.Standard.A1.Flex** (1–4 OCPU, 6–24 GB RAM; use at least 2 OCPU, 12 GB for Docker builds)
   - **Networking:** assign public IPv4  
   - **SSH keys:** download private key or paste your public key  
4. **Create**

Note the **public IP** (e.g. `129.146.x.x`).

### Security rules (Oracle console)

**Networking → Virtual cloud networks → your VCN → Security list → Ingress:**

| Port | Source | Purpose |
|------|--------|---------|
| 22 | 0.0.0.0/0 | SSH |
| 80 | 0.0.0.0/0 | HTTP (Caddy / Let's Encrypt) |
| 443 | 0.0.0.0/0 | HTTPS API |
| 8080 | 0.0.0.0/0 | Gateway direct test (optional) |

### Ubuntu firewall (on VM)

```bash
sudo iptables -I INPUT -p tcp --dport 22 -j ACCEPT
sudo iptables -I INPUT -p tcp --dport 80 -j ACCEPT
sudo iptables -I INPUT -p tcp --dport 443 -j ACCEPT
sudo iptables -I INPUT -p tcp --dport 8080 -j ACCEPT
# Persist if using iptables-persistent; or rely on Oracle security list only
```

---

## Part 2 — SSH into VM

From Windows PowerShell:

```powershell
ssh -i "C:\path\to\your-private-key.key" ubuntu@YOUR_VM_PUBLIC_IP
```

---

## Part 3 — Install Docker

On the VM:

```bash
sudo apt update && sudo apt upgrade -y
sudo apt install -y git ca-certificates curl

curl -fsSL https://get.docker.com | sudo sh
sudo usermod -aG docker $USER
newgrp docker
docker compose version
```

---

## Part 4 — Free HTTPS domain (DuckDNS)

1. https://www.duckdns.org → login  
2. Create subdomain, e.g. `cleaning-api` → `cleaning-api.duckdns.org`  
3. Point it to your **VM public IP**  
4. You'll use: `API_DOMAIN=cleaning-api.duckdns.org`

---

## Part 5 — Clone project

```bash
cd ~
git clone https://github.com/Anushka0206/Cleaning-booking-Microservices.git
cd Cleaning-booking-Microservices
```

Pull latest if you added deploy files:

```bash
git pull origin main
```

---

## Part 6 — Environment file

```bash
cp .env.example .env
nano .env
```

**Minimum changes:**

```env
# Strong secret (32+ chars) — same on all services via compose
JWT_SECRET=change-me-to-a-long-random-string-min-32-chars

# DuckDNS hostname (no https:// prefix)
API_DOMAIN=cleaning-api.duckdns.org

POSTGRES_USER=justlife
POSTGRES_PASSWORD=choose-a-strong-db-password
```

Save (`Ctrl+O`, `Enter`, `Ctrl+X`).

---

## Part 7 — Build and run

```bash
docker compose -f docker-compose.deploy.yml up -d --build
```

Watch logs:

```bash
docker compose -f docker-compose.deploy.yml logs -f api-gateway auth-service
```

Wait until gateway is up (~5–15 min after build finishes).

### Health checks

```bash
curl -s http://localhost:8080/actuator/health
curl -s https://cleaning-api.duckdns.org/actuator/health
```

(Use your `API_DOMAIN`.)

### Login test (from VM)

```bash
curl -s -X POST https://cleaning-api.duckdns.org/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"customer@justlife.com","password":"customer123"}'
```

Should return JSON with `token`.

---

## Part 8 — Connect Vercel

1. Vercel → your project → **Settings → Environment Variables**  
2. Set:

   ```
   VITE_API_BASE_URL=https://cleaning-api.duckdns.org
   ```

   (Your DuckDNS URL, **https**, no trailing slash.)

3. **Deployments → Redeploy**  
4. Open Vercel site → Login with `customer@justlife.com` / `customer123`

---

## Troubleshooting

| Problem | Fix |
|---------|-----|
| Build OOM | VM RAM 12GB+; `docker system prune`; build again |
| Caddy no HTTPS | `API_DOMAIN` must match DuckDNS; ports 80/443 open |
| Vercel still CORS | Push latest `WebConfig.java`; rebuild auth-service on VM |
| Vercel API disconnected | Wrong `VITE_API_BASE_URL`; must be **https** DuckDNS URL |
| Eureka / 500 errors | `docker compose ... logs discovery-server booking-service auth-service` |
| Demo login fails | DB empty — check auth flyway logs; register new user on UI |

### Restart stack

```bash
cd ~/Cleaning-booking-Microservices
docker compose -f docker-compose.deploy.yml down
docker compose -f docker-compose.deploy.yml up -d --build
```

### Update after git push

```bash
git pull
docker compose -f docker-compose.deploy.yml up -d --build
```

---

## Resume lines

```
Live app: https://your-app.vercel.app
API: https://cleaning-api.duckdns.org
GitHub: https://github.com/Anushka0206/Cleaning-booking-Microservices
```

---

## Without DuckDNS (not recommended for Vercel)

- `http://VM_IP:8080` works for **Postman/curl** only  
- **Vercel HTTPS** will not call HTTP API reliably (mixed content)  
- Use DuckDNS + Caddy or Cloudflare Tunnel for HTTPS
