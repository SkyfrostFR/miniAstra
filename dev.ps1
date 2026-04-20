# ─────────────────────────────────────────────────────────────────────────────
# dev.ps1 — Script de démarrage miniAstra (Windows / PowerShell)
#
# Usage :
#   .\dev.ps1              → démarre tout (BDD + backend + frontend)
#   .\dev.ps1 backend      → backend uniquement (PostgreSQL déjà lancé)
#   .\dev.ps1 frontend     → frontend uniquement
#   .\dev.ps1 db           → PostgreSQL uniquement (Docker)
#   .\dev.ps1 stop         → arrête tous les services
#   .\dev.ps1 logs         → affiche les logs PM2 (backend)
#   .\dev.ps1 logs-front   → affiche les logs PM2 (frontend)
#   .\dev.ps1 status       → statut PM2
# ─────────────────────────────────────────────────────────────────────────────
param(
    [string]$CMD = "all"
)

$ErrorActionPreference = "Stop"
$SCRIPT_DIR = Split-Path -Parent $MyInvocation.MyCommand.Path

function log  { Write-Host "[miniAstra] $args" -ForegroundColor Cyan }
function ok   { Write-Host "[OK] $args" -ForegroundColor Green }
function warn { Write-Host "[!]  $args" -ForegroundColor Yellow }
function err  { Write-Host "[X]  $args" -ForegroundColor Red }

function Check-Command($name) {
    return $null -ne (Get-Command $name -ErrorAction SilentlyContinue)
}

function Check-Deps {
    $missing = @()
    if (-not (Check-Command "docker"))  { $missing += "docker" }
    if (-not (Check-Command "mvn"))     { $missing += "maven" }
    if (-not (Check-Command "pm2"))     { $missing += "pm2  (npm install -g pm2)" }
    if ($missing.Count -gt 0) {
        err "Dépendances manquantes : $($missing -join ', ')"
        exit 1
    }
}

function Wait-Postgres {
    log "Attente que PostgreSQL soit prêt..."
    $tries = 0
    do {
        $tries++
        if ($tries -ge 20) {
            err "PostgreSQL ne répond pas après 20 tentatives."
            exit 1
        }
        Start-Sleep -Seconds 1
        $result = docker exec miniastra-postgres pg_isready -U miniastra 2>&1
    } until ($LASTEXITCODE -eq 0)
    ok "PostgreSQL prêt (localhost:5432)"
}

function Start-DB {
    # Vérifie si le conteneur postgres tourne déjà
    $running = docker ps --filter "name=miniastra-postgres" --format "{{.Names}}" 2>&1
    if ($running -match "miniastra-postgres") {
        ok "PostgreSQL déjà disponible sur localhost:5432"
        return
    }
    log "Démarrage de PostgreSQL (Docker)..."
    Push-Location $SCRIPT_DIR
    docker compose up -d postgres
    Pop-Location
    Wait-Postgres
}

function Start-Backend {
    log "Démarrage du backend Spring Boot via PM2..."
    Push-Location $SCRIPT_DIR
    $list = pm2 list 2>&1
    if ($list -match "miniastra-8080") {
        pm2 restart miniastra-8080
    } else {
        pm2 start ecosystem.config.cjs --only miniastra-8080
    }
    Pop-Location
    ok "Backend démarré → http://localhost:8080"
    ok "API         → http://localhost:8080/api/scenarios"
}

function Start-Frontend {
    log "Démarrage du frontend Vite via PM2..."
    Push-Location $SCRIPT_DIR
    $list = pm2 list 2>&1
    if ($list -match "miniastra-front") {
        pm2 restart miniastra-front
    } else {
        pm2 start ecosystem.config.cjs --only miniastra-front
    }
    Pop-Location
    ok "Frontend démarré → http://localhost:5173"
}

switch ($CMD) {
    "all" {
        Check-Deps
        Start-DB
        Start-Backend
        Start-Frontend
        Write-Host ""
        log "miniAstra est lancé !"
        log "  Frontend → http://localhost:5173"
        log "  Backend  → http://localhost:8080"
        log "  Logs     → .\dev.ps1 logs | .\dev.ps1 logs-front"
        log "  Statut   → .\dev.ps1 status"
    }
    "db" {
        Check-Deps
        Start-DB
    }
    "backend" {
        Check-Deps
        Start-Backend
    }
    "frontend" {
        Check-Deps
        Start-Frontend
    }
    "stop" {
        log "Arrêt des services..."
        pm2 stop miniastra-8080 miniastra-front 2>&1 | Out-Null
        Push-Location $SCRIPT_DIR
        docker compose stop postgres 2>&1 | Out-Null
        Pop-Location
        ok "Services arrêtés"
    }
    "logs" {
        pm2 logs miniastra-8080
    }
    "logs-front" {
        pm2 logs miniastra-front
    }
    "status" {
        Write-Host ""
        pm2 status
        Write-Host ""
        Push-Location $SCRIPT_DIR
        docker compose ps 2>&1
        Pop-Location
    }
    default {
        Write-Host "Usage: .\dev.ps1 [all|backend|frontend|db|stop|logs|logs-front|status]"
        exit 1
    }
}
