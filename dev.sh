#!/usr/bin/env bash
# ─────────────────────────────────────────────────────────────────────────────
# dev.sh — Script de démarrage miniAstra (développement local)
#
# Usage :
#   ./dev.sh              → démarre tout (BDD + backend + frontend)
#   ./dev.sh backend      → backend uniquement (PostgreSQL déjà lancé)
#   ./dev.sh frontend     → frontend uniquement
#   ./dev.sh db           → PostgreSQL uniquement (Docker)
#   ./dev.sh stop         → arrête tous les services
#   ./dev.sh logs         → affiche les logs PM2 (backend)
#   ./dev.sh logs-front   → affiche les logs PM2 (frontend)
#   ./dev.sh status       → statut PM2
# ─────────────────────────────────────────────────────────────────────────────

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$SCRIPT_DIR/backend"

# ── Couleurs ─────────────────────────────────────────────────────────────────
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
CYAN='\033[0;36m'
NC='\033[0m'

log()  { echo -e "${CYAN}[miniAstra]${NC} $*"; }
ok()   { echo -e "${GREEN}[✓]${NC} $*"; }
warn() { echo -e "${YELLOW}[!]${NC} $*"; }
err()  { echo -e "${RED}[✗]${NC} $*" >&2; }

# ── Commandes ─────────────────────────────────────────────────────────────────
CMD="${1:-all}"

check_deps() {
    local missing=()
    command -v docker  >/dev/null 2>&1 || missing+=("docker")
    command -v mvn     >/dev/null 2>&1 || missing+=("maven")
    command -v pm2     >/dev/null 2>&1 || missing+=("pm2 (npm install -g pm2)")
    if [ ${#missing[@]} -gt 0 ]; then
        err "Dépendances manquantes : ${missing[*]}"
        exit 1
    fi
}

start_db() {
    # Si quelque chose écoute déjà sur 5432, on suppose que PostgreSQL est prêt
    if pg_isready -h localhost -p 5432 -U miniastra >/dev/null 2>&1; then
        ok "PostgreSQL déjà disponible sur localhost:5432 (instance locale)"
        return 0
    fi

    log "Démarrage de PostgreSQL (Docker)..."
    cd "$SCRIPT_DIR"
    docker compose up -d postgres
    log "Attente que PostgreSQL soit prêt..."
    local tries=0
    until pg_isready -h localhost -p 5432 -U miniastra >/dev/null 2>&1; do
        tries=$((tries + 1))
        if [ $tries -ge 20 ]; then
            err "PostgreSQL ne répond pas après 20 tentatives."
            exit 1
        fi
        sleep 1
    done
    ok "PostgreSQL prêt (localhost:5432)"
}

start_backend() {
    log "Démarrage du backend Spring Boot via PM2..."
    cd "$SCRIPT_DIR"
    if pm2 list 2>/dev/null | grep -q "miniastra-8080"; then
        pm2 restart miniastra-8080
    else
        pm2 start ecosystem.config.cjs --only miniastra-8080
    fi
    ok "Backend démarré → http://localhost:8080"
    ok "API         → http://localhost:8080/api/scenarios"
}

start_frontend() {
    log "Démarrage du frontend Vite via PM2..."
    cd "$SCRIPT_DIR"
    if pm2 list 2>/dev/null | grep -q "miniastra-front"; then
        pm2 restart miniastra-front
    else
        pm2 start ecosystem.config.cjs --only miniastra-front
    fi
    ok "Frontend démarré → http://localhost:5173"
}

case "$CMD" in
    all)
        check_deps
        start_db
        start_backend
        start_frontend
        echo ""
        log "miniAstra est lance !"
        log "  Frontend → http://localhost:5173"
        log "  Backend  → http://localhost:8080"
        log "  Logs     → ./dev.sh logs | ./dev.sh logs-front"
        log "  Statut   → ./dev.sh status"
        ;;
    db)
        check_deps
        start_db
        ;;
    backend)
        check_deps
        start_backend
        ;;
    frontend)
        check_deps
        start_frontend
        ;;
    stop)
        log "Arret des services..."
        pm2 stop miniastra-8080 miniastra-front 2>/dev/null || true
        cd "$SCRIPT_DIR" && docker compose stop postgres 2>/dev/null || true
        ok "Services arretes"
        ;;
    logs)
        pm2 logs miniastra-8080
        ;;
    logs-front)
        pm2 logs miniastra-front
        ;;
    status)
        echo ""
        pm2 status
        echo ""
        docker compose ps 2>/dev/null || true
        ;;
    *)
        echo "Usage: $0 [all|backend|frontend|db|stop|logs|logs-front|status]"
        exit 1
        ;;
esac
