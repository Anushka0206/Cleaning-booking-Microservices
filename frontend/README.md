# Frontend

React + Vite app for the Cleaning Booking platform.

**Main docs:** [../README.md](../README.md) · [../docs/QUICKSTART.md](../docs/QUICKSTART.md) · [../docs/DEPLOY.md](../docs/DEPLOY.md)

## Run

```bash
npm install
cp .env.example .env   # or copy on Windows
npm run dev
```

http://localhost:5173 — requires API gateway at `VITE_API_BASE_URL` (default `http://localhost:8080`).

## Deploy (Vercel)

- Root directory: `frontend`
- Env: `VITE_API_BASE_URL=https://your-public-api:8080`
- `vercel.json` included for SPA routing

## Pages

See [../README.md#frontend-routes](../README.md#frontend-routes).
