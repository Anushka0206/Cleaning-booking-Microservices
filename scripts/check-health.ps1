# Quick check: is the stack reachable?
$urls = @(
  @{ Name = "API Gateway"; Url = "http://localhost:8080/actuator/health" },
  @{ Name = "Eureka"; Url = "http://localhost:8761" },
  @{ Name = "Config Server"; Url = "http://localhost:8888/actuator/health" },
  @{ Name = "Frontend (dev)"; Url = "http://localhost:5173" }
)

foreach ($u in $urls) {
  try {
    $r = Invoke-WebRequest -Uri $u.Url -UseBasicParsing -TimeoutSec 3
    Write-Host "[OK] $($u.Name) -> $($r.StatusCode)" -ForegroundColor Green
  } catch {
    Write-Host "[--] $($u.Name) not reachable" -ForegroundColor Yellow
  }
}
