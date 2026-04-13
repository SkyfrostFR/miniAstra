# MiniAstra

Préparateur de données pour simulateur de train (France).
Application locale mono-utilisateur permettant de créer et exporter des scénarios ferroviaires.

---

## Fonctionnalités

- Création et gestion de **scénarios ferroviaires** (corridor, durée, heure de départ)
- Saisie de **5 types d'objets** : tronçons de voie, trains passagers, trains fret, obstacles, signaux
- **Catalogue prédéfini** de matériel roulant SNCF (TGV Duplex, TER Régiolis, BB 75000, etc.)
- **Carte interactive** MapLibre GL JS pour placer et visualiser les objets
- **DataGrid** AG Grid avec édition inline synchronisée avec la carte
- **Export Excel** (`.xlsx`) par type d'objet via Apache POI
- **Validation** des règles métier (RV bloquantes + RW warnings) avec re-validation en cascade
- **Curseur de temps** pour visualiser les obstacles temporels
- Simulation de la France avec les 10 plus grandes villes et trains pré-positionnés

---

## Stack technique

| Couche | Technologie |
|--------|-------------|
| Backend | Java 21 + Spring Boot 3.3 |
| Persistance | Spring Data JPA + Hibernate + Flyway |
| Base de données | PostgreSQL 16 |
| Export | Apache POI 5.x |
| Frontend | React 18 + TypeScript + Vite |
| Carte | MapLibre GL JS |
| Grid | AG Grid Community |
| UI | shadcn/ui + Tailwind CSS |
| State | Zustand |
| Validation | Zod |
| Process | PM2 |

---

## Prérequis

- Java 21+
- Maven 3.9+
- Node.js 20+
- Docker (pour PostgreSQL)
- PM2 (`npm install -g pm2`)

---

## Démarrage rapide

```bash
# Tout démarrer (BDD + backend + frontend)
./dev.sh

# Ou composante par composante
./dev.sh db          # PostgreSQL uniquement (Docker)
./dev.sh backend     # Backend Spring Boot
./dev.sh frontend    # Frontend Vite
```

| Service | URL |
|---------|-----|
| Frontend | http://localhost:5173 |
| Backend API | http://localhost:8080/api |
| Scénarios | http://localhost:8080/api/scenarios |

---

## Commandes dev.sh

```bash
./dev.sh              # Démarre tout
./dev.sh stop         # Arrête tous les services
./dev.sh logs         # Logs PM2 backend
./dev.sh logs-front   # Logs PM2 frontend
./dev.sh status       # Statut PM2 + Docker
```

---

## Commandes PM2

```bash
pm2 status            # État des processus
pm2 logs              # Logs en direct
pm2 restart all       # Redémarrer
pm2 stop all          # Arrêter
```

---

## Structure du projet

```
miniAstra/
├── backend/                    # Spring Boot (Java)
│   └── src/main/java/fr/miniastra/
│       ├── domain/             # Entités DDD (scenario, track, train, obstacle, signal)
│       ├── application/        # Services métier + validation + export
│       ├── infrastructure/     # Repositories JPA + seeder
│       └── api/rest/           # Controllers REST + DTOs
├── frontend/                   # React + TypeScript (Vite)
│   └── src/
│       ├── components/         # Composants UI
│       ├── store/              # Zustand stores
│       └── api/                # Client HTTP
├── docs/                       # Spécifications + architecture + tickets + ADR
├── docker-compose.yml          # PostgreSQL 16
├── ecosystem.config.cjs        # Configuration PM2
└── dev.sh                      # Script de démarrage
```

---

## API REST

Base URL : `http://localhost:8080/api`

```
GET/POST   /scenarios
GET/PUT/DELETE /scenarios/{id}

GET/POST   /scenarios/{id}/tracks
GET/POST   /scenarios/{id}/passenger-trains
GET/POST   /scenarios/{id}/freight-trains
GET/POST   /scenarios/{id}/obstacles
GET/POST   /scenarios/{id}/signals

GET        /catalog/passenger-models
GET        /catalog/freight-models

GET        /scenarios/{id}/validate

GET        /scenarios/{id}/export/tracks
GET        /scenarios/{id}/export/passenger-trains
GET        /scenarios/{id}/export/freight-trains
GET        /scenarios/{id}/export/obstacles
GET        /scenarios/{id}/export/signals
```

---

## Documentation

| Fichier | Contenu |
|---------|---------|
| `docs/specs.md` | Spécifications complètes v1.0 |
| `docs/architecture.md` | Architecture technique détaillée |
| `docs/tickets-backend.md` | Tickets backend |
| `docs/tickets-frontend.md` | Tickets frontend |
| `docs/adr/` | Architecture Decision Records |

---

## Données de démo

Au démarrage, un scénario **"Simulation France — 10 grandes villes"** est automatiquement chargé avec :
- 10 tronçons entre Paris, Lyon, Marseille, Toulouse, Nice, Nantes, Strasbourg, Montpellier, Bordeaux, Lille
- Trains passagers et fret pré-positionnés

Un second scénario **"Corridor Paris-Lyon"** contient 10 exemples par type d'objet.
