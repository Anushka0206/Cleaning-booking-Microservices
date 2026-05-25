# Start Postgres, Kafka, Redis, observability (local dev)
$ErrorActionPreference = "Stop"
$Root = Split-Path $PSScriptRoot -Parent
Set-Location $Root

if (-not (Test-Path ".env")) {
  Write-Host "Copy .env.example to .env first." -ForegroundColor Yellow
  exit 1
}

Write-Host "Starting Docker infrastructure..." -ForegroundColor Cyan
docker compose -f docker-local-compose.yml up -d

Write-Host ""
Write-Host "Done. Wait ~30s for Kafka health, then start Java services (see docs/QUICKSTART.md)." -ForegroundColor Green
