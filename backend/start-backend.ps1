# Start Backend Script
Write-Host "Starting Sales & Store Analytics Backend..." -ForegroundColor Cyan
Write-Host ""

# Delete old database
Write-Host "[1/2] Cleaning old database..." -ForegroundColor Yellow
if (Test-Path "data/analytics.mv.db") {
    Remove-Item "data/analytics.mv.db" -Force
    Write-Host "  ✓ Deleted analytics.mv.db" -ForegroundColor Green
}
if (Test-Path "data/analytics.trace.db") {
    Remove-Item "data/analytics.trace.db" -Force
    Write-Host "  ✓ Deleted analytics.trace.db" -ForegroundColor Green
}
Write-Host ""

# Start backend
Write-Host "[2/2] Starting backend (this will take 2-3 minutes)..." -ForegroundColor Yellow
Write-Host "  Watch for: 'Successfully imported 22762 sales transactions'" -ForegroundColor Cyan
Write-Host ""

# Use cmd to run mvnw.cmd
cmd /c "mvnw.cmd spring-boot:run"
