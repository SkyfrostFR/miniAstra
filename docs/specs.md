# MiniAstra — Spécifications v1.0

## Préparateur de données pour simulateur de train (France)

**Décisions arrêtées :**
- Application locale (pas de déploiement web)
- Export Excel (1 type d'objet / fichier)
- Données saisies manuellement par l'utilisateur
- Obstacles temporels (apparition/disparition dans le temps)
- Modèles de matériel roulant prédéfinis
- Stack : Java (Spring Boot) + React + PostgreSQL

---

## 1. MVP — 5 types d'objets

| # | Type | Description |
|---|------|-------------|
| 1 | **Tronçon de voie** | Segment de rail entre deux points, support de tous les autres objets |
| 2 | **Train de passagers** | Rame passagers avec modèle prédéfini |
| 3 | **Train de marchandises** | Convoi fret avec modèle prédéfini |
| 4 | **Obstacle** | Perturbation sur la voie, avec fenêtre temporelle |
| 5 | **Signal** | Sémaphore / carré sur un tronçon |

10 objets d'exemple seront pré-chargés pour chaque type au démarrage (seed).

---

## 2. Modèle de données

### 2.1 Catalogue de matériel roulant (prédéfini, non modifiable)

#### Trains passagers

| Code | Modèle | Longueur | Masse | Vitesse max | Traction |
|------|--------|----------|-------|-------------|----------|
| `TGV-DUPLEX` | TGV Duplex | 200 m | 380 t | 320 km/h | 25kV AC |
| `TGV-INOUISEE` | TGV inoui (Euroduplex) | 200 m | 410 t | 320 km/h | 25kV AC |
| `TER-REGIOLIS` | TER Régiolis | 82 m | 118 t | 160 km/h | 25kV AC / Diesel |
| `TER-REGIO2N` | TER Régio 2N | 110 m | 200 t | 160 km/h | 25kV AC |
| `IC-CORAIL` | Intercités Corail (BB 26000) | 300 m | 450 t | 200 km/h | 25kV AC / 1500V DC |
| `RER-MI09` | RER A MI09 | 225 m | 310 t | 140 km/h | 1500V DC |
| `THALYS` | Thalys PBKA | 200 m | 385 t | 300 km/h | Multi-tension |

#### Trains de marchandises

| Code | Modèle | Longueur | Masse (vide) | Vitesse max | Traction |
|------|--------|----------|--------------|-------------|----------|
| `FRET-BB75000` | BB 75000 + wagons plats | 400 m | 800 t | 120 km/h | 25kV AC |
| `FRET-BB60000` | BB 60000 + wagons citernes | 350 m | 1100 t | 100 km/h | 1500V DC |
| `FRET-BB27000` | BB 27000 + wagons tombereaux | 500 m | 1400 t | 100 km/h | 25kV AC |
| `FRET-G1000` | Gravibus G1000 (Diesel) | 300 m | 600 t | 80 km/h | Diesel |

### 2.2 Entités

#### Scénario

| Attribut | Type | Description |
|----------|------|-------------|
| `id` | UUID | Identifiant |
| `name` | VARCHAR(100) | Nom du scénario |
| `description` | TEXT | Description libre |
| `duration_s` | INTEGER | Durée totale de simulation (secondes) |
| `start_time` | TIME | Heure de début de la simulation |
| `created_at` | TIMESTAMP | Date de création |
| `updated_at` | TIMESTAMP | Dernière modification |

#### Tronçon de voie (track_segment)

| Attribut | Type | Unité | Description |
|----------|------|-------|-------------|
| `id` | UUID | — | Identifiant |
| `scenario_id` | UUID | — | Scénario parent |
| `name` | VARCHAR(100) | — | Libellé (ex : "Voie principale Nord") |
| `start_lat` | DOUBLE | degrés | Latitude départ |
| `start_lon` | DOUBLE | degrés | Longitude départ |
| `end_lat` | DOUBLE | degrés | Latitude fin |
| `end_lon` | DOUBLE | degrés | Longitude fin |
| `waypoints` | JSONB | — | Points intermédiaires `[[lat,lon],...]` — tableau ordonné, peut être vide `[]`. Chaque point : lat ∈ [-90,90], lon ∈ [-180,180]. |
| `length_m` | DOUBLE | mètres | Longueur calculée automatiquement par la formule Haversine appliquée segment par segment (start → waypoints → end). |
| `max_speed_kmh` | INTEGER | km/h | Vitesse limite |
| `track_count` | INTEGER | — | Nombre de voies (1 ou 2) |
| `electrification` | ENUM | — | `NONE`, `AC_25KV`, `DC_1500V` |

> **Convention de positionnement** : `position_m = 0` correspond toujours au point `(start_lat, start_lon)`, quelle que soit la direction du train. La direction `PAIR`/`IMPAIR` indique le sens de circulation mais ne change pas l'origine de la mesure.
| `grade_permil` | DOUBLE | ‰ | Pente (+ = montée). Plage valide : [-80, 80] — au-delà des limites réalistes ferroviaires. |

#### Train de passagers (passenger_train)

| Attribut | Type | Unité | Description |
|----------|------|-------|-------------|
| `id` | UUID | — | Identifiant |
| `scenario_id` | UUID | — | Scénario parent |
| `name` | VARCHAR(100) | — | Libellé (ex : "TGV 6201") |
| `model_code` | VARCHAR(30) | — | Référence catalogue (ex : `TGV-DUPLEX`) |
| `track_id` | UUID | — | Tronçon de positionnement (FK, requis) |
| `position_m` | DOUBLE | mètres | Position sur le tronçon |
| `direction` | ENUM | — | `PAIR`, `IMPAIR` |
| `initial_speed_kmh` | DOUBLE | km/h | Vitesse à T=0 |
| `passenger_count` | INTEGER | — | Nombre de passagers |
| `service_number` | VARCHAR(20) | — | Numéro de service (ex : "6201") |

#### Train de marchandises (freight_train)

| Attribut | Type | Unité | Description |
|----------|------|-------|-------------|
| `id` | UUID | — | Identifiant |
| `scenario_id` | UUID | — | Scénario parent |
| `name` | VARCHAR(100) | — | Libellé |
| `model_code` | VARCHAR(30) | — | Référence catalogue (ex : `FRET-BB75000`) |
| `track_id` | UUID | — | Tronçon de positionnement (FK, requis) |
| `position_m` | DOUBLE | mètres | Position sur le tronçon |
| `direction` | ENUM | — | `PAIR`, `IMPAIR` |
| `initial_speed_kmh` | DOUBLE | km/h | Vitesse à T=0 |
| `load_t` | DOUBLE | tonnes | Charge transportée |
| `cargo_type` | ENUM | — | `VIDE`, `GENERAL`, `VRAC`, `CITERNE`, `DANGEREUX` |

#### Obstacle (obstacle)

| Attribut | Type | Unité | Description |
|----------|------|-------|-------------|
| `id` | UUID | — | Identifiant |
| `scenario_id` | UUID | — | Scénario parent |
| `name` | VARCHAR(100) | — | Libellé (ex : "Éboulement km 142") |
| `type` | ENUM | — | `VEHICULE`, `CHANTIER`, `GLISSEMENT`, `ANIMAL`, `OBJET` |
| `track_id` | UUID | — | Tronçon concerné (FK, requis) |
| `position_m` | DOUBLE | mètres | Position sur le tronçon |
| `length_m` | DOUBLE | mètres | Longueur de l'obstacle |
| `blocking` | BOOLEAN | — | Voie complètement bloquée |
| `speed_limit_kmh` | INTEGER | km/h | Limite si non bloquant (0 si bloquant) |
| `visibility_m` | DOUBLE | mètres | Distance de visibilité |
| `appear_at_s` | INTEGER | secondes | Temps d'apparition (0 = présent dès le début) |
| `disappear_at_s` | INTEGER | secondes | Temps de disparition (NULL = permanent) |

#### Signal (signal)

| Attribut | Type | Description |
|----------|------|-------------|
| `id` | UUID | Identifiant |
| `scenario_id` | UUID | Scénario parent |
| `name` | VARCHAR(100) | Libellé (ex : "S142 Carré") |
| `type` | ENUM | `CARRE`, `SEMAPHORE`, `AVERTISSEMENT`, `GUIDON`, `TGV_R` |
| `track_id` | UUID | Tronçon concerné (FK, requis) |
| `position_m` | DOUBLE | Position sur le tronçon |
| `direction` | ENUM | `PAIR`, `IMPAIR`, `BIDIR` |
| `initial_state` | ENUM | `VOIE_LIBRE`, `ARRET`, `AVERTISSEMENT` |

---

## 3. Règles de validation

### Bloquantes (RV)

| ID | Règle |
|----|-------|
| RV-001 | Un train DOIT référencer un tronçon existant dans le même scénario |
| RV-002 | `position_m` d'un train/obstacle/signal DOIT être dans `[0, length_m]` du tronçon |
| RV-003 | Un signal DOIT référencer un tronçon existant |
| RV-004 | Un obstacle DOIT référencer un tronçon existant |
| RV-005 | `disappear_at_s` DOIT être > `appear_at_s` si les deux sont renseignés |
| RV-006 | `appear_at_s` et `disappear_at_s` DOIVENT être dans `[0, duration_s]` du scénario |
| RV-007 | Train électrique (AC_25KV ou DC_1500V) sur tronçon `NONE` : export bloqué (incompatibilité traction) |
| RV-008 | Le nom d'un tronçon (`track_segment.name`) DOIT être unique au sein d'un même scénario |
| RV-009 | Les coordonnées de chaque point dans `waypoints` DOIVENT être dans lat ∈ [-90,90] et lon ∈ [-180,180] |
| RV-010 | `grade_permil` DOIT être dans [-80, 80] |
| RV-011 | Si `duration_s` d'un scénario est modifié, tous les obstacles du scénario sont re-validés contre RV-006 ; les obstacles en violation passent en état d'erreur et bloquent l'export |

### Warnings (RW)

| ID | Règle |
|----|-------|
| RW-001 | Vitesse initiale d'un train > `min(vitesse_max_train, max_speed_kmh_tronçon)` |
| RW-002 | Deux trains sur le même tronçon avec positions qui se chevauchent à T=0 |
| RW-003 | ~~Train électrique sur tronçon non électrifié~~ — promu en RV-007 |
| RW-004 | Un signal et un obstacle partagent exactement la même `position_m` sur le même tronçon |
| RW-005 | Un train démarre (`position_m` à T=0) à la même position qu'un obstacle bloquant présent dès T=0 |

---

## 4. Architecture UI

### 4.1 Layout

```
┌─────────────────────────────────────────────────────────┐
│  Barre d'outils : [Sélection] [Tracer voie] [Ajouter ▼] │
│                   [Valider] [Exporter ▼] [Sauvegarder]   │
├──────────────────────────────┬──────────────────────────┤
│                              │                          │
│          CARTE               │       DATAGRID           │
│      (MapLibre GL)           │    (AG Grid)             │
│                              │  Filtre type d'objet:    │
│                              │  [ Tronçons ▼ ]          │
│                              │                          │
│                              │  Propriétés de           │
│                              │  l'objet sélectionné     │
├──────────────────────────────┴──────────────────────────┤
│  Statut : lat/lon | zoom | [⚠ 2 erreurs] [✓ 0 warnings]  │
└─────────────────────────────────────────────────────────┘
```

### 4.2 Tuiles cartographiques et mode offline

MapLibre GL JS charge par défaut les tuiles depuis les serveurs OpenStreetMap. Pour les environnements sans accès Internet :

- Le frontend tente de détecter la connectivité au démarrage.
- En cas d'échec, un message d'avertissement non bloquant est affiché ("Carte indisponible hors ligne").
- Les données (tronçons, trains, etc.) restent utilisables via le DataGrid sans la carte.
- **Post-MVP** : support d'un tile server local (ex: Martin ou PMTiles) configurable via `settings`.

### 4.3 Interactions carte

- **Tracer un tronçon** : mode tracé → clics pour points → double-clic pour terminer (le nom doit être unique dans le scénario, validé à la sauvegarde)
- **Placer un train/obstacle/signal** : drag depuis palette → snap sur tronçon → refus si pas de tronçon
- **Sélectionner** : clic → DataGrid affiche les propriétés
- **Déplacer** : drag d'un objet → recalcul de position_m
- **Supprimer** : touche Suppr + confirmation si dépendants

### 4.4 Représentation temporelle des obstacles

La barre d'outils inclut un **curseur de temps** (0 → duration_s).
Déplacer le curseur affiche/masque les obstacles selon leur `appear_at_s` / `disappear_at_s`.
En mode édition normale, tous les obstacles sont visibles avec une indication visuelle de leur statut temporel.

---

## 5. Export Excel

### Comportement

- Bouton **"Exporter"** → menu déroulant avec les 5 types d'objets
- 1 type sélectionné → 1 fichier `.xlsx` généré et téléchargé
- Format : 1 feuille, 1 ligne d'en-tête, 1 ligne par objet

### Colonnes par type

**Tronçons de voie**
`id | name | start_lat | start_lon | end_lat | end_lon | length_m | max_speed_kmh | track_count | electrification | grade_permil`

**Trains de passagers**
`id | name | model_code | track_name | position_m | direction | initial_speed_kmh | passenger_count | service_number`

**Trains de marchandises**
`id | name | model_code | track_name | position_m | direction | initial_speed_kmh | load_t | cargo_type`

**Obstacles**
`id | name | type | track_name | position_m | length_m | blocking | speed_limit_kmh | visibility_m | appear_at_s | disappear_at_s`

**Signaux**
`id | name | type | track_name | position_m | direction | initial_state`

> Note : les références FK (track_id) sont remplacées par les noms lisibles dans l'export.

---

## 6. Stack technique

### Backend — Java / Spring Boot

| Composant | Choix | Version |
|-----------|-------|---------|
| Framework | Spring Boot | 3.3 |
| API | Spring Web (REST) | — |
| Persistance | Spring Data JPA + Hibernate | — |
| Base de données | PostgreSQL | 16 |
| Migrations | Flyway | — |
| Export Excel | Apache POI | 5.x |
| Mapping | MapStruct | 1.6 |
| Lombok | Lombok | — |
| Build | Maven | 3.9 |

### Frontend — React / TypeScript

| Composant | Choix | Justification |
|-----------|-------|---------------|
| Framework | React 18 + TypeScript | — |
| State | Zustand | Léger, partagé carte/grid |
| Carte | MapLibre GL JS | Open source, performant |
| Fond de carte | OpenStreetMap (tiles libres) | Gratuit, local-friendly |
| DataGrid | AG Grid Community | Édition inline, gratuit |
| UI | shadcn/ui + Tailwind CSS | Composants accessibles |
| Validation | Zod | Schémas TypeScript-first |
| Drag & Drop | dnd-kit | Palette → carte |
| Build | Vite | — |

### Architecture applicative

```
Frontend (React + Vite)
      │  HTTP REST (localhost:8080)
      ▼
Backend (Spring Boot)
      │  JPA
      ▼
PostgreSQL (localhost:5432)
```

L'application tourne entièrement en local. Pas d'authentification.

### Structure des packages Java

```
src/main/java/fr/miniastra/
  domain/
    scenario/       # Entité Scenario
    track/          # TrackSegment
    train/          # PassengerTrain, FreightTrain, TrainCatalog
    obstacle/       # Obstacle
    signal/         # Signal
  application/
    ScenarioService
    ExportService   # Apache POI
    ValidationService
  infrastructure/
    persistence/    # Repositories JPA
    export/         # ExcelExporter
  api/
    rest/           # Controllers REST
    dto/            # Request/Response DTOs
```

---

## 7. API REST

### 7.1 Routes

```
# Scénarios
GET    /api/scenarios
POST   /api/scenarios
GET    /api/scenarios/{id}
PUT    /api/scenarios/{id}
DELETE /api/scenarios/{id}

# Objets par scénario (pagination : ?page=0&size=100, défaut size=200, max=500)
GET    /api/scenarios/{id}/tracks
POST   /api/scenarios/{id}/tracks
PUT    /api/scenarios/{id}/tracks/{trackId}
DELETE /api/scenarios/{id}/tracks/{trackId}

GET    /api/scenarios/{id}/passenger-trains
POST   /api/scenarios/{id}/passenger-trains
PUT    /api/scenarios/{id}/passenger-trains/{trainId}
DELETE /api/scenarios/{id}/passenger-trains/{trainId}

GET    /api/scenarios/{id}/freight-trains
POST   /api/scenarios/{id}/freight-trains
PUT    /api/scenarios/{id}/freight-trains/{trainId}
DELETE /api/scenarios/{id}/freight-trains/{trainId}

GET    /api/scenarios/{id}/obstacles
POST   /api/scenarios/{id}/obstacles
PUT    /api/scenarios/{id}/obstacles/{obstacleId}
DELETE /api/scenarios/{id}/obstacles/{obstacleId}

GET    /api/scenarios/{id}/signals
POST   /api/scenarios/{id}/signals
PUT    /api/scenarios/{id}/signals/{signalId}
DELETE /api/scenarios/{id}/signals/{signalId}

# Catalogue matériel roulant
GET    /api/catalog/passenger-models
GET    /api/catalog/freight-models

# Validation
GET    /api/scenarios/{id}/validate

# Export Excel (bloqué si violations RV actives)
GET    /api/scenarios/{id}/export/tracks          → .xlsx
GET    /api/scenarios/{id}/export/passenger-trains → .xlsx
GET    /api/scenarios/{id}/export/freight-trains   → .xlsx
GET    /api/scenarios/{id}/export/obstacles        → .xlsx
GET    /api/scenarios/{id}/export/signals          → .xlsx
```

### 7.2 Format des réponses d'erreur

Toutes les erreurs retournent un JSON structuré :

```json
{
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "Description lisible de l'erreur",
  "details": [
    { "field": "position_m", "code": "RV-002", "message": "Dépasse length_m du tronçon (450.0 m)" }
  ],
  "timestamp": "2026-04-06T11:30:00Z"
}
```

| Code HTTP | Cas d'usage |
|-----------|-------------|
| 400 | Données invalides (RV violations, format incorrect) |
| 404 | Ressource introuvable |
| 409 | Conflit (ex: nom de tronçon déjà utilisé dans ce scénario) |
| 422 | Entité sémantiquement incorrecte (ex: export bloqué par RV) |
| 500 | Erreur serveur inattendue |

### 7.3 État des signaux

Les signaux sont **statiques dans le MVP** : seul `initial_state` est géré. Le changement d'état dynamique (block signaling, état en fonction du temps ou des trains) est hors scope MVP et documenté comme évolution post-MVP.

---

## 8. Données d'exemple (seed)

10 exemples par type seront insérés via Flyway dans un scénario de démo "Corridor Paris-Lyon".

**Zone géographique de référence du seed :** bbox approximative entre Paris (48.85°N, 2.35°E) et Lyon (45.75°N, 4.83°E). Les tronçons suivent un tracé fictif dans cette zone, électrification `AC_25KV`, pentes dans [-15, 15]‰.

| Type | Exemples |
|------|---------|
| Tronçons | 10 segments constituant une ligne fictive avec courbes (noms uniques, ex: "Tronçon T01" à "T10") |
| Trains passagers | 5 TGV Duplex + 3 TER Régiolis + 2 Intercités Corail |
| Trains fret | 4 BB 75000 + 3 BB 60000 + 3 BB 27000 |
| Obstacles | Mix de types : chantier permanent, véhicule temporaire, animal, glissement |
| Signaux | Alternance de carrés et sémaphores sur les tronçons |

---

## 9. Phases de développement

### Phase 1 — Fondations (Sprint 1-2)
- Projet Spring Boot + PostgreSQL + Flyway
- Entités JPA + migrations
- API REST CRUD pour les 5 types
- Seed de données d'exemple
- Projet React + Vite + Zustand

### Phase 2 — Carte (Sprint 3-4)
- Intégration MapLibre GL JS
- Rendu des tronçons (lignes)
- Rendu des trains (icônes positionnées)
- Rendu des obstacles et signaux
- Mode tracé de tronçon
- Placement par snap sur tronçon

### Phase 3 — DataGrid (Sprint 5-6)
- AG Grid par type d'objet
- Édition inline synchronisée avec la carte
- Validation Zod côté frontend
- Affichage erreurs/warnings

### Phase 4 — Export + polish (Sprint 7-8)
- Export Excel (Apache POI) — bloqué si violations RV actives
- Curseur de temps pour les obstacles
- Validation complète (RV + RW), re-validation en cascade sur modification de `duration_s`
- Panneau de validation cliquable
- Tests unitaires backend (JUnit 5 + Mockito)
- Tests intégration API (Spring Boot Test)

> **Note :** Undo/redo et versioning de scénario sont hors scope MVP. En l'absence de ces mécanismes, la suppression d'objets demande une confirmation systématique (voir interaction "Supprimer" section 4.3). Le versioning est identifié comme évolution prioritaire post-MVP.

---

## 10. Critères de succès MVP

| Critère | Mesure |
|---------|--------|
| Tracer et sauvegarder un tronçon | Fonctionnel via carte |
| Placer un train sur un tronçon | Snap + validation position |
| Éditer tous les attributs d'un objet | DataGrid avec validation |
| Obstacle temporel affiché/masqué | Curseur de temps opérationnel |
| Export Excel par type | Fichier `.xlsx` correct |
| 10 exemples par type en seed | Visibles sur la carte au démarrage |
| Validation RV bloquante | Erreurs affichées, export bloqué |
