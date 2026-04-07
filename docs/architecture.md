# MiniAstra — Architecture v1.0

> Préparateur de données pour simulateur de train (France)
> Application locale mono-utilisateur, pas de déploiement web.

---

## 1. Vue d'ensemble

```
 ┌──────────────────────────────────────────────────────────────────┐
 │                     FRONTEND  (localhost:5173)                   │
 │                                                                  │
 │  ┌────────────────────┐   Zustand   ┌──────────────────────┐    │
 │  │   MapLibre GL JS   │◄───────────►│     AG Grid          │    │
 │  │  (carte interactive)│   Store     │  (édition DataGrid)  │    │
 │  └────────┬───────────┘  partagé    └──────────┬───────────┘    │
 │           │                                     │                │
 │           └──────────┬──────────────────────────┘                │
 │                      │                                           │
 │              ┌───────▼────────┐                                  │
 │              │  API Client    │  Zod validation                  │
 │              │  (fetch/axios) │  avant envoi                     │
 │              └───────┬────────┘                                  │
 └──────────────────────┼──────────────────────────────────────────┘
                        │ HTTP REST (JSON)
                        │ localhost:8080/api/*
 ┌──────────────────────┼──────────────────────────────────────────┐
 │                      │       BACKEND  (localhost:8080)           │
 │              ┌───────▼────────┐                                  │
 │              │  REST          │  @RestController                 │
 │              │  Controllers   │  DTO validation (@Valid)         │
 │              └───────┬────────┘                                  │
 │                      │ MapStruct (DTO <-> Entity)                │
 │              ┌───────▼────────┐                                  │
 │              │  Application   │  Services métier                 │
 │              │  Services      │  Validation (RV/RW)              │
 │              └───────┬────────┘                                  │
 │                      │                                           │
 │         ┌────────────┼────────────┐                              │
 │         │            │            │                               │
 │  ┌──────▼──────┐ ┌──▼─────┐ ┌───▼──────────┐                   │
 │  │ Domain      │ │ JPA    │ │ Excel Export  │                   │
 │  │ Entities    │ │ Repos  │ │ (Apache POI)  │                   │
 │  └─────────────┘ └──┬─────┘ └──────────────┘                   │
 │                      │                                           │
 └──────────────────────┼──────────────────────────────────────────┘
                        │ JDBC / Hibernate
 ┌──────────────────────┼──────────────────────────────────────────┐
 │             PostgreSQL 16  (localhost:5432)                      │
 │             base: miniastra                                      │
 │             migrations: Flyway                                   │
 └─────────────────────────────────────────────────────────────────┘
```

---

## 2. Backend — Structure des packages Java

```
src/main/java/fr/miniastra/
├── MiniAstraApplication.java            # Point d'entrée Spring Boot
│
├── domain/                              # --- BOUNDED CONTEXTS (DDD) ---
│   ├── scenario/
│   │   ├── Scenario.java                # Entité agrégat racine
│   │   ├── ScenarioRepository.java      # Interface repository (port)
│   │   └── ScenarioValidator.java       # Règles métier du scénario
│   │
│   ├── track/
│   │   ├── TrackSegment.java            # Entité
│   │   ├── Electrification.java         # Enum: NONE, AC_25KV, DC_1500V
│   │   ├── Waypoint.java                # Value Object [lat, lon]
│   │   └── TrackSegmentRepository.java  # Interface repository
│   │
│   ├── train/
│   │   ├── PassengerTrain.java          # Entité
│   │   ├── FreightTrain.java            # Entité
│   │   ├── Direction.java               # Enum: PAIR, IMPAIR
│   │   ├── CargoType.java               # Enum: VIDE, GENERAL, VRAC, CITERNE, DANGEREUX
│   │   ├── TrainCatalog.java            # Value Object (modèles prédéfinis)
│   │   ├── PassengerModel.java          # Record immutable (catalogue)
│   │   ├── FreightModel.java            # Record immutable (catalogue)
│   │   ├── PassengerTrainRepository.java
│   │   └── FreightTrainRepository.java
│   │
│   ├── obstacle/
│   │   ├── Obstacle.java                # Entité
│   │   ├── ObstacleType.java            # Enum: VEHICULE, CHANTIER, GLISSEMENT, ANIMAL, OBJET
│   │   └── ObstacleRepository.java
│   │
│   ├── signal/
│   │   ├── Signal.java                  # Entité
│   │   ├── SignalType.java              # Enum: CARRE, SEMAPHORE, AVERTISSEMENT, GUIDON, TGV_R
│   │   ├── SignalState.java             # Enum: VOIE_LIBRE, ARRET, AVERTISSEMENT
│   │   ├── SignalDirection.java         # Enum: PAIR, IMPAIR, BIDIR
│   │   └── SignalRepository.java
│   │
│   └── validation/
│       ├── ValidationResult.java        # Value Object (errors + warnings)
│       ├── ValidationError.java         # Record: code, message, objectId
│       ├── ValidationWarning.java       # Record: code, message, objectId
│       └── ScenarioValidationService.java  # Règles RV-* et RW-*
│
├── application/                         # --- SERVICES APPLICATIFS ---
│   ├── scenario/
│   │   ├── ScenarioAppService.java      # CRUD scénario + orchestration
│   │   └── ScenarioDto.java             # Projection lecture
│   ├── track/
│   │   └── TrackSegmentAppService.java
│   ├── train/
│   │   ├── PassengerTrainAppService.java
│   │   └── FreightTrainAppService.java
│   ├── obstacle/
│   │   └── ObstacleAppService.java
│   ├── signal/
│   │   └── SignalAppService.java
│   ├── export/
│   │   └── ExcelExportAppService.java   # Orchestration export par type
│   └── catalog/
│       └── CatalogAppService.java       # Lecture catalogue matériel
│
├── infrastructure/                      # --- ADAPTATEURS ---
│   ├── persistence/
│   │   ├── jpa/
│   │   │   ├── ScenarioJpaRepository.java
│   │   │   ├── TrackSegmentJpaRepository.java
│   │   │   ├── PassengerTrainJpaRepository.java
│   │   │   ├── FreightTrainJpaRepository.java
│   │   │   ├── ObstacleJpaRepository.java
│   │   │   └── SignalJpaRepository.java
│   │   └── adapter/
│   │       ├── ScenarioRepositoryAdapter.java   # Implémente domain.ScenarioRepository
│   │       ├── TrackSegmentRepositoryAdapter.java
│   │       ├── PassengerTrainRepositoryAdapter.java
│   │       ├── FreightTrainRepositoryAdapter.java
│   │       ├── ObstacleRepositoryAdapter.java
│   │       └── SignalRepositoryAdapter.java
│   ├── export/
│   │   └── ExcelExporter.java           # Apache POI, génère le .xlsx
│   └── catalog/
│       └── InMemoryTrainCatalog.java    # Catalogue en dur (records immuables)
│
├── api/                                 # --- COUCHE API ---
│   ├── rest/
│   │   ├── ScenarioController.java
│   │   ├── TrackSegmentController.java
│   │   ├── PassengerTrainController.java
│   │   ├── FreightTrainController.java
│   │   ├── ObstacleController.java
│   │   ├── SignalController.java
│   │   ├── CatalogController.java
│   │   ├── ExportController.java
│   │   └── ValidationController.java
│   ├── dto/
│   │   ├── request/
│   │   │   ├── CreateScenarioRequest.java
│   │   │   ├── UpdateScenarioRequest.java
│   │   │   ├── CreateTrackSegmentRequest.java
│   │   │   ├── UpdateTrackSegmentRequest.java
│   │   │   ├── CreatePassengerTrainRequest.java
│   │   │   ├── UpdatePassengerTrainRequest.java
│   │   │   ├── CreateFreightTrainRequest.java
│   │   │   ├── UpdateFreightTrainRequest.java
│   │   │   ├── CreateObstacleRequest.java
│   │   │   ├── UpdateObstacleRequest.java
│   │   │   ├── CreateSignalRequest.java
│   │   │   └── UpdateSignalRequest.java
│   │   └── response/
│   │       ├── ScenarioResponse.java
│   │       ├── TrackSegmentResponse.java
│   │       ├── PassengerTrainResponse.java
│   │       ├── FreightTrainResponse.java
│   │       ├── ObstacleResponse.java
│   │       ├── SignalResponse.java
│   │       ├── ValidationResponse.java
│   │       ├── CatalogResponse.java
│   │       └── ApiError.java
│   ├── mapper/
│   │   ├── ScenarioMapper.java          # MapStruct: Entity <-> DTO
│   │   ├── TrackSegmentMapper.java
│   │   ├── PassengerTrainMapper.java
│   │   ├── FreightTrainMapper.java
│   │   ├── ObstacleMapper.java
│   │   └── SignalMapper.java
│   └── exception/
│       ├── GlobalExceptionHandler.java  # @ControllerAdvice
│       ├── ResourceNotFoundException.java
│       └── ValidationException.java
│
└── config/
    ├── CorsConfig.java                  # CORS localhost:5173
    ├── JpaConfig.java
    └── JacksonConfig.java
```

```
src/main/resources/
├── application.yml
├── application-dev.yml
└── db/migration/
    ├── V1__create_scenario_table.sql
    ├── V2__create_track_segment_table.sql
    ├── V3__create_passenger_train_table.sql
    ├── V4__create_freight_train_table.sql
    ├── V5__create_obstacle_table.sql
    ├── V6__create_signal_table.sql
    └── V7__seed_paris_lyon_demo.sql
```

```
src/test/java/fr/miniastra/
├── domain/         # Tests unitaires domaine (JUnit 5 + Mockito)
├── application/    # Tests unitaires services
├── api/            # Tests intégration controllers (MockMvc)
└── infrastructure/ # Tests intégration repositories (@DataJpaTest)
```

---

## 3. Frontend — Structure des dossiers React

```
src/
├── main.tsx
├── App.tsx                              # Layout principal
│
├── api/                                 # --- COUCHE HTTP ---
│   ├── client.ts                        # Fetch wrapper, base URL, error handling
│   ├── scenarios.api.ts
│   ├── tracks.api.ts
│   ├── passengerTrains.api.ts
│   ├── freightTrains.api.ts
│   ├── obstacles.api.ts
│   ├── signals.api.ts
│   ├── catalog.api.ts
│   ├── validation.api.ts
│   └── export.api.ts                    # GET export (blob download)
│
├── stores/                              # --- ZUSTAND STORES ---
│   ├── scenarioStore.ts                 # Scénario actif, liste scénarios
│   ├── mapStore.ts                      # État carte: zoom, center, mode
│   ├── selectionStore.ts                # Objet sélectionné (type + id)
│   ├── objectsStore.ts                  # Tous les objets du scénario
│   └── validationStore.ts              # Résultats validation
│
├── schemas/                             # --- ZOD VALIDATION ---
│   ├── scenario.schema.ts
│   ├── trackSegment.schema.ts
│   ├── passengerTrain.schema.ts
│   ├── freightTrain.schema.ts
│   ├── obstacle.schema.ts
│   └── signal.schema.ts
│
├── types/                               # --- TYPES TYPESCRIPT ---
│   ├── scenario.types.ts
│   ├── track.types.ts
│   ├── train.types.ts
│   ├── obstacle.types.ts
│   ├── signal.types.ts
│   ├── catalog.types.ts
│   ├── validation.types.ts
│   └── enums.ts                         # Electrification, Direction, CargoType, etc.
│
├── components/
│   ├── layout/
│   │   ├── AppLayout.tsx
│   │   ├── Toolbar.tsx
│   │   ├── StatusBar.tsx
│   │   └── TimeSlider.tsx               # Curseur de temps obstacles
│   │
│   ├── map/
│   │   ├── MapContainer.tsx             # Initialisation MapLibre GL JS
│   │   ├── MapControls.tsx
│   │   ├── layers/
│   │   │   ├── TrackLayer.tsx           # Rendu lignes GeoJSON
│   │   │   ├── TrainLayer.tsx           # Icônes sur tronçons
│   │   │   ├── ObstacleLayer.tsx        # Icônes avec opacité temporelle
│   │   │   └── SignalLayer.tsx
│   │   ├── interactions/
│   │   │   ├── DrawTrackMode.tsx        # Mode tracé tronçon
│   │   │   ├── PlaceObjectMode.tsx      # Snap sur tronçon + création
│   │   │   ├── SelectMode.tsx           # Clic -> sync DataGrid
│   │   │   └── DragMoveMode.tsx         # Déplacement -> recalcul position_m
│   │   └── utils/
│   │       ├── geoUtils.ts              # Haversine, projection, snap-to-line
│   │       └── styleUtils.ts
│   │
│   ├── grid/
│   │   ├── DataGridPanel.tsx            # Container AG Grid + filtre type
│   │   ├── columns/
│   │   │   ├── trackColumns.ts
│   │   │   ├── passengerTrainColumns.ts
│   │   │   ├── freightTrainColumns.ts
│   │   │   ├── obstacleColumns.ts
│   │   │   └── signalColumns.ts
│   │   ├── editors/
│   │   │   ├── EnumCellEditor.tsx
│   │   │   └── NumberCellEditor.tsx
│   │   └── renderers/
│   │       ├── DirectionRenderer.tsx
│   │       └── ElectrificationRenderer.tsx
│   │
│   ├── validation/
│   │   ├── ValidationPanel.tsx          # Liste erreurs/warnings cliquables
│   │   └── ValidationBadge.tsx
│   │
│   └── shared/
│       ├── ConfirmDialog.tsx
│       ├── ExportMenu.tsx
│       └── ScenarioSelector.tsx
│
├── hooks/
│   ├── useScenario.ts
│   ├── useObjects.ts
│   ├── useMapInteraction.ts
│   ├── useSelection.ts
│   ├── useValidation.ts
│   └── useExport.ts
│
└── lib/
    └── constants.ts                     # API_BASE_URL, couleurs, configs
```

```
tests/
├── unit/
│   ├── schemas/
│   ├── stores/
│   └── components/                      # Vitest + Testing Library
├── integration/
│   ├── api/                             # Appels API mockés (MSW)
│   └── grid/
└── e2e/
    └── scenarios/                       # Playwright
```

---

## 4. Modèle de données

### Diagramme entités-relations

```
                         ┌─────────────────┐
                         │    SCENARIO      │
                         ├─────────────────┤
                         │ id         UUID  │ PK
                         │ name       VC100 │
                         │ description TEXT │
                         │ duration_s  INT  │
                         │ start_time  TIME │
                         │ created_at  TS   │
                         │ updated_at  TS   │
                         └────────┬────────┘
                                  │ 1
                   ┌──────────────┼──────────────────────────┐
                   │              │                           │
                   │ *            │ *                         │ *
          ┌────────▼──────┐  ┌───▼──────────┐  ┌────────────▼─────┐
          │ TRACK_SEGMENT │  │ PASSENGER_   │  │ FREIGHT_         │
          ├───────────────┤  │ TRAIN        │  │ TRAIN            │
          │ id       UUID │  ├──────────────┤  ├──────────────────┤
          │ scenario_id   │  │ id      UUID │  │ id          UUID │
          │ name     VC100│  │ scenario_id  │  │ scenario_id      │
          │ start_lat DBL │  │ name    VC100│  │ name        VC100│
          │ start_lon DBL │  │ model_code   │  │ model_code       │
          │ end_lat   DBL │  │ track_id  FK─┤  │ track_id     FK──┤
          │ end_lon   DBL │  │ position_m   │  │ position_m       │
          │ waypoints JSNB│  │ direction    │  │ direction        │
          │ length_m  DBL │  │ init_speed   │  │ init_speed       │
          │ max_speed INT │  │ pax_count    │  │ load_t           │
          │ track_count   │  │ service_num  │  │ cargo_type       │
          │ electrif ENUM │  └──────────────┘  └──────────────────┘
          │ grade_permil  │
          └───────┬───────┘
                  │ 1
           ┌──────┼──────────────────────┐
           │ *                      *    │
  ┌────────▼──────┐         ┌────────────▼─────┐
  │   OBSTACLE    │         │     SIGNAL       │
  ├───────────────┤         ├──────────────────┤
  │ id       UUID │         │ id          UUID │
  │ scenario_id   │         │ scenario_id      │
  │ name     VC100│         │ name        VC100│
  │ type     ENUM │         │ type        ENUM │
  │ track_id  FK  │         │ track_id     FK  │
  │ position_m    │         │ position_m       │
  │ length_m  DBL │         │ direction   ENUM │
  │ blocking BOOL │         │ initial_state    │
  │ speed_lim INT │         └──────────────────┘
  │ visibility DBL│
  │ appear_at_s   │
  │ disappear_s   │
  └───────────────┘
```

**Relations clés :**
- `Scenario` 1 — * `TrackSegment` (cascade delete)
- `Scenario` 1 — * `PassengerTrain | FreightTrain | Obstacle | Signal` (cascade delete)
- `TrackSegment` 1 — * `PassengerTrain | FreightTrain | Obstacle | Signal` (tout objet est placé SUR un tronçon, cascade delete)
- Toutes les FK vers `track_id` sont NOT NULL

---

## 5. Flux de données

### 5.1 Sélection carte → DataGrid → API → DB

```
Utilisateur clique sur un objet (carte)
        │
        ▼
MapContainer.onClick(feature)
        │
        ▼
selectionStore.select({ type: 'obstacle', id: 'uuid-xxx' })
        │
        ├────────────────────────┐
        ▼                        ▼
MapContainer                DataGridPanel
- Highlight l'objet         - Filtre sur type 'obstacle'
                            - Scroll vers la ligne uuid-xxx
                            - Active l'édition inline
        │
        ▼
Utilisateur modifie un champ dans AG Grid (ex: appear_at_s = 120)
        │
        ▼
AG Grid onCellValueChanged(event)
        │
        ▼
Zod.parse(obstacleSchema, updatedRow)  -- validation frontend
        │
        ├── Échec: afficher erreur dans la cellule, pas d'appel API
        │
        ▼ Succès
objectsStore.updateObstacle(id, { appear_at_s: 120 })  -- mise à jour optimiste
        │
        ▼
PUT /api/scenarios/{scenarioId}/obstacles/{id}
    Body: { appear_at_s: 120, ... }
        │
        ▼
Backend: @Valid sur UpdateObstacleRequest
        │
        ▼
Backend: ObstacleAppService.update()
  - Charge l'entité
  - Applique les modifications (objet immutable)
  - Validation métier (RV-005, RV-006)
        │
        ├── Échec: 400 Bad Request → frontend rollback store
        │
        ▼ Succès
PostgreSQL: UPDATE obstacle SET appear_at_s = 120 WHERE id = ?
        │
        ▼
Frontend: confirme mise à jour + re-render carte
```

### 5.2 Tracé d'un tronçon (création)

```
Utilisateur active le mode "Tracer voie" (touche T)
        │
        ▼
mapStore.setMode('draw-track')
        │
        ▼
DrawTrackMode active:
  - Clic 1: point départ (start_lat, start_lon)
  - Clic 2..N: waypoints
  - Double-clic: point fin (end_lat, end_lon)
        │
        ▼
geoUtils.calculateLength(points)  -- Haversine
        │
        ▼
POST /api/scenarios/{id}/tracks
    Body: { name, start_lat, start_lon, end_lat, end_lon, waypoints: [...] }
        │
        ▼
Backend calcule length_m, persiste, retourne TrackSegmentResponse
        │
        ▼
objectsStore.addTrack(response) → Carte + DataGrid se mettent à jour
```

### 5.3 Export Excel

```
Utilisateur clique "Exporter" > "Obstacles"
        │
        ▼
GET /api/scenarios/{id}/export/obstacles
    Accept: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
        │
        ▼
ExcelExportAppService:
  1. Charge tous les obstacles du scénario
  2. Résout les FK (track_id → track.name) pour colonnes lisibles
  3. Délègue à ExcelExporter.generate(obstacles, columnDefs)
        │
        ▼
ExcelExporter (Apache POI):
  1. Crée un Workbook XSSF
  2. Crée une Sheet "Obstacles"
  3. Ligne 0: en-têtes
  4. Lignes 1..N: données
  5. Auto-size colonnes
  6. Retourne byte[]
        │
        ▼
Controller: ResponseEntity<byte[]>
  Content-Disposition: attachment; filename="obstacles_scenarioName.xlsx"
        │
        ▼
Frontend: Blob → URL.createObjectURL → <a download> → clic auto
```

---

## 6. API REST — Liste complète des endpoints

### 6.1 Scénarios

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/scenarios` | Liste tous les scénarios |
| `POST` | `/api/scenarios` | Crée un scénario |
| `GET` | `/api/scenarios/{id}` | Détail d'un scénario |
| `PUT` | `/api/scenarios/{id}` | Met à jour un scénario |
| `DELETE` | `/api/scenarios/{id}` | Supprime (cascade) |

### 6.2 Tronçons de voie

| Méthode | Endpoint |
|---------|----------|
| `GET` | `/api/scenarios/{id}/tracks` |
| `POST` | `/api/scenarios/{id}/tracks` |
| `PUT` | `/api/scenarios/{id}/tracks/{trackId}` |
| `DELETE` | `/api/scenarios/{id}/tracks/{trackId}` |

### 6.3 Trains de passagers

| Méthode | Endpoint |
|---------|----------|
| `GET` | `/api/scenarios/{id}/passenger-trains` |
| `POST` | `/api/scenarios/{id}/passenger-trains` |
| `PUT` | `/api/scenarios/{id}/passenger-trains/{trainId}` |
| `DELETE` | `/api/scenarios/{id}/passenger-trains/{trainId}` |

### 6.4 Trains de marchandises

| Méthode | Endpoint |
|---------|----------|
| `GET` | `/api/scenarios/{id}/freight-trains` |
| `POST` | `/api/scenarios/{id}/freight-trains` |
| `PUT` | `/api/scenarios/{id}/freight-trains/{trainId}` |
| `DELETE` | `/api/scenarios/{id}/freight-trains/{trainId}` |

### 6.5 Obstacles

| Méthode | Endpoint |
|---------|----------|
| `GET` | `/api/scenarios/{id}/obstacles` |
| `POST` | `/api/scenarios/{id}/obstacles` |
| `PUT` | `/api/scenarios/{id}/obstacles/{obstacleId}` |
| `DELETE` | `/api/scenarios/{id}/obstacles/{obstacleId}` |

### 6.6 Signaux

| Méthode | Endpoint |
|---------|----------|
| `GET` | `/api/scenarios/{id}/signals` |
| `POST` | `/api/scenarios/{id}/signals` |
| `PUT` | `/api/scenarios/{id}/signals/{signalId}` |
| `DELETE` | `/api/scenarios/{id}/signals/{signalId}` |

### 6.7 Catalogue (lecture seule)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/catalog/passenger-models` | 7 modèles passagers |
| `GET` | `/api/catalog/freight-models` | 4 modèles fret |

### 6.8 Validation

| Méthode | Endpoint | Réponse |
|---------|----------|---------|
| `GET` | `/api/scenarios/{id}/validate` | `ValidationResponse` |

```json
{
  "valid": false,
  "errors": [
    { "code": "RV-001", "message": "Train 'TGV 6201' référence un tronçon inexistant", "objectId": "uuid", "objectType": "PASSENGER_TRAIN" }
  ],
  "warnings": [
    { "code": "RW-003", "message": "Train électrique sur tronçon non électrifié", "objectId": "uuid", "objectType": "FREIGHT_TRAIN" }
  ]
}
```

### 6.9 Export Excel

| Méthode | Endpoint |
|---------|----------|
| `GET` | `/api/scenarios/{id}/export/tracks` |
| `GET` | `/api/scenarios/{id}/export/passenger-trains` |
| `GET` | `/api/scenarios/{id}/export/freight-trains` |
| `GET` | `/api/scenarios/{id}/export/obstacles` |
| `GET` | `/api/scenarios/{id}/export/signals` |

### 6.10 Format d'erreur standard

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "details": [
    { "field": "appear_at_s", "message": "doit être >= 0" }
  ],
  "timestamp": "2026-04-05T10:30:00Z"
}
```

---

## 7. Principes DDD

### 7.1 Bounded Contexts

```
┌─────────────────────────────────────────────────────┐
│                  SCENARIO CONTEXT                    │
│  Agrégat racine: Scenario                            │
│  Responsabilité: cycle de vie, durée, metadata       │
└─────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────┐
│               INFRASTRUCTURE CONTEXT                 │
│  Agrégat racine: TrackSegment                        │
│  Responsabilité: réseau de voies, géométrie,         │
│  électrification, vitesse                            │
└─────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────┐
│               ROLLING STOCK CONTEXT                  │
│  Agrégats: PassengerTrain, FreightTrain              │
│  Value Objects: PassengerModel, FreightModel         │
│  Responsabilité: positionnement, catalogue matériel  │
└─────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────┐
│              OPERATIONAL CONTEXT                     │
│  Agrégats: Obstacle, Signal                          │
│  Responsabilité: éléments opérationnels,             │
│  fenêtres temporelles                                │
└─────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────┐
│              VALIDATION CONTEXT                      │
│  Services: ScenarioValidationService                 │
│  Responsabilité: règles RV-* (bloquantes),           │
│  règles RW-* (warnings), cross-context               │
└─────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────┐
│                EXPORT CONTEXT                        │
│  Services: ExcelExportAppService, ExcelExporter      │
│  Responsabilité: génération .xlsx, résolution FK     │
└─────────────────────────────────────────────────────┘
```

### 7.2 Agrégats et invariants

| Agrégat | Invariants |
|---------|------------|
| `Scenario` | `duration_s > 0`, `name` non vide |
| `TrackSegment` | `length_m` calculée, `start != end` |
| `PassengerTrain` | `model_code` dans catalogue, `track_id` valide |
| `FreightTrain` | `model_code` dans catalogue, `track_id` valide |
| `Obstacle` | `appear_at_s < disappear_at_s`, `track_id` valide |
| `Signal` | `track_id` valide, `position_m` dans bornes |

### 7.3 Interface Repository type

```java
public interface TrackSegmentRepository {
    List<TrackSegment> findByScenarioId(UUID scenarioId);
    Optional<TrackSegment> findById(UUID id);
    TrackSegment save(TrackSegment segment);
    void deleteById(UUID id);
    void deleteByScenarioId(UUID scenarioId);
    boolean existsById(UUID id);
}
```

### 7.4 Value Objects

| VO | Package | Description |
|----|---------|-------------|
| `Waypoint` | `domain/track/` | Paire [lat, lon], immutable |
| `ValidationResult` | `domain/validation/` | Liste errors + warnings, immutable |
| `ValidationError` | `domain/validation/` | Record(code, message, objectId, objectType) |
| `ValidationWarning` | `domain/validation/` | Record(code, message, objectId, objectType) |
| `PassengerModel` | `domain/train/` | Record catalogue (code, nom, longueur, masse, vitesse, traction) |
| `FreightModel` | `domain/train/` | Record catalogue |

---

## 8. Décisions techniques (ADR résumés)

### ADR-001 : Application locale sans authentification

- **Contexte** : Outil de préparation de données, mono-utilisateur.
- **Décision** : Pas d'auth, pas de HTTPS, CORS ouvert entre localhost:5173 et localhost:8080.
- **Conséquences** : Simplicité maximale. Ne jamais déployer tel quel sur un réseau partagé.

### ADR-002 : Zustand comme store partagé carte/grid

- **Contexte** : MapLibre et AG Grid doivent rester synchronisés.
- **Décision** : Zustand (store global léger) plutôt que Redux ou Context API.
- **Justification** : API minimaliste, pas de boilerplate, subscriptions sélecteurs performants.

### ADR-003 : MapStruct pour le mapping DTO/Entity

- **Contexte** : Nombreuses entités avec beaucoup de champs.
- **Décision** : MapStruct génère le code de mapping à la compilation.
- **Justification** : Zéro reflection à l'exécution, erreurs détectées à la compilation.

### ADR-004 : Catalogue matériel roulant en mémoire

- **Contexte** : 11 modèles fixes (7 passagers + 4 fret), non modifiables.
- **Décision** : Records Java immutables dans `InMemoryTrainCatalog`, pas de table DB.
- **Justification** : Pas de migration pour des données statiques, chargement instantané.

### ADR-005 : Validation double couche (frontend Zod + backend Java)

- **Contexte** : Saisie utilisateur dans AG Grid et via API REST.
- **Décision** : Zod valide les schémas côté frontend. `@Valid` + service de validation métier côté backend.
- **Justification** : UX réactive (erreurs instantanées) + sécurité (backend ne fait jamais confiance au frontend).

### ADR-006 : Flyway pour les migrations de schéma

- **Contexte** : Schéma PostgreSQL avec 6 tables.
- **Décision** : Flyway avec migrations versionnées SQL (V1 à V7).
- **Justification** : Déterministe, reproductible, intégré à Spring Boot.

### ADR-007 : Export Excel via Apache POI

- **Contexte** : Format .xlsx imposé pour l'export.
- **Décision** : Apache POI XSSF.
- **Justification** : Référence Java pour la génération Excel.
- **Risque** : POI est lourd en mémoire pour de très gros fichiers. Les scénarios MiniAstra ne dépasseront pas quelques centaines d'objets.

### ADR-008 : PostgreSQL JSONB pour les waypoints

- **Contexte** : Un tronçon peut avoir 0 à N points intermédiaires.
- **Décision** : Colonne `waypoints JSONB` stockant `[[lat, lon], ...]`.
- **Justification** : Pas de table de jointure, lecture/écriture simple, toujours lus en bloc.
- **Alternative écartée** : Table `waypoint` (1:N) avec `sort_order` — sur-ingénierie pour ce cas.

---

## 9. Schéma base de données

### Table `scenario`

| Colonne | Type | Contraintes |
|---------|------|-------------|
| `id` | `UUID` | PK, DEFAULT gen_random_uuid() |
| `name` | `VARCHAR(100)` | NOT NULL |
| `description` | `TEXT` | NULLABLE |
| `duration_s` | `INTEGER` | NOT NULL, CHECK (duration_s > 0) |
| `start_time` | `TIME` | NOT NULL, DEFAULT '08:00' |
| `created_at` | `TIMESTAMP` | NOT NULL, DEFAULT NOW() |
| `updated_at` | `TIMESTAMP` | NOT NULL, DEFAULT NOW() |

### Table `track_segment`

| Colonne | Type | Contraintes |
|---------|------|-------------|
| `id` | `UUID` | PK, DEFAULT gen_random_uuid() |
| `scenario_id` | `UUID` | NOT NULL, FK → scenario(id) ON DELETE CASCADE |
| `name` | `VARCHAR(100)` | NOT NULL |
| `start_lat` | `DOUBLE PRECISION` | NOT NULL |
| `start_lon` | `DOUBLE PRECISION` | NOT NULL |
| `end_lat` | `DOUBLE PRECISION` | NOT NULL |
| `end_lon` | `DOUBLE PRECISION` | NOT NULL |
| `waypoints` | `JSONB` | NOT NULL, DEFAULT '[]' |
| `length_m` | `DOUBLE PRECISION` | NOT NULL, CHECK (length_m > 0) |
| `max_speed_kmh` | `INTEGER` | NOT NULL, CHECK (max_speed_kmh > 0) |
| `track_count` | `INTEGER` | NOT NULL, DEFAULT 1, CHECK (track_count IN (1, 2)) |
| `electrification` | `VARCHAR(20)` | NOT NULL, CHECK (electrification IN ('NONE', 'AC_25KV', 'DC_1500V')) |
| `grade_permil` | `DOUBLE PRECISION` | NOT NULL, DEFAULT 0.0 |

Index : `idx_track_segment_scenario_id ON track_segment(scenario_id)`

### Table `passenger_train`

| Colonne | Type | Contraintes |
|---------|------|-------------|
| `id` | `UUID` | PK, DEFAULT gen_random_uuid() |
| `scenario_id` | `UUID` | NOT NULL, FK → scenario(id) ON DELETE CASCADE |
| `name` | `VARCHAR(100)` | NOT NULL |
| `model_code` | `VARCHAR(30)` | NOT NULL |
| `track_id` | `UUID` | NOT NULL, FK → track_segment(id) ON DELETE CASCADE |
| `position_m` | `DOUBLE PRECISION` | NOT NULL, CHECK (position_m >= 0) |
| `direction` | `VARCHAR(10)` | NOT NULL, CHECK (direction IN ('PAIR', 'IMPAIR')) |
| `initial_speed_kmh` | `DOUBLE PRECISION` | NOT NULL, DEFAULT 0, CHECK (initial_speed_kmh >= 0) |
| `passenger_count` | `INTEGER` | NOT NULL, DEFAULT 0, CHECK (passenger_count >= 0) |
| `service_number` | `VARCHAR(20)` | NULLABLE |

Index : `idx_passenger_train_scenario_id`, `idx_passenger_train_track_id`

### Table `freight_train`

| Colonne | Type | Contraintes |
|---------|------|-------------|
| `id` | `UUID` | PK, DEFAULT gen_random_uuid() |
| `scenario_id` | `UUID` | NOT NULL, FK → scenario(id) ON DELETE CASCADE |
| `name` | `VARCHAR(100)` | NOT NULL |
| `model_code` | `VARCHAR(30)` | NOT NULL |
| `track_id` | `UUID` | NOT NULL, FK → track_segment(id) ON DELETE CASCADE |
| `position_m` | `DOUBLE PRECISION` | NOT NULL, CHECK (position_m >= 0) |
| `direction` | `VARCHAR(10)` | NOT NULL, CHECK (direction IN ('PAIR', 'IMPAIR')) |
| `initial_speed_kmh` | `DOUBLE PRECISION` | NOT NULL, DEFAULT 0, CHECK (initial_speed_kmh >= 0) |
| `load_t` | `DOUBLE PRECISION` | NOT NULL, DEFAULT 0, CHECK (load_t >= 0) |
| `cargo_type` | `VARCHAR(20)` | NOT NULL, CHECK (cargo_type IN ('VIDE', 'GENERAL', 'VRAC', 'CITERNE', 'DANGEREUX')) |

Index : `idx_freight_train_scenario_id`, `idx_freight_train_track_id`

### Table `obstacle`

| Colonne | Type | Contraintes |
|---------|------|-------------|
| `id` | `UUID` | PK, DEFAULT gen_random_uuid() |
| `scenario_id` | `UUID` | NOT NULL, FK → scenario(id) ON DELETE CASCADE |
| `name` | `VARCHAR(100)` | NOT NULL |
| `type` | `VARCHAR(20)` | NOT NULL, CHECK (type IN ('VEHICULE', 'CHANTIER', 'GLISSEMENT', 'ANIMAL', 'OBJET')) |
| `track_id` | `UUID` | NOT NULL, FK → track_segment(id) ON DELETE CASCADE |
| `position_m` | `DOUBLE PRECISION` | NOT NULL, CHECK (position_m >= 0) |
| `length_m` | `DOUBLE PRECISION` | NOT NULL, DEFAULT 1, CHECK (length_m > 0) |
| `blocking` | `BOOLEAN` | NOT NULL, DEFAULT false |
| `speed_limit_kmh` | `INTEGER` | NOT NULL, DEFAULT 0, CHECK (speed_limit_kmh >= 0) |
| `visibility_m` | `DOUBLE PRECISION` | NOT NULL, DEFAULT 300, CHECK (visibility_m > 0) |
| `appear_at_s` | `INTEGER` | NOT NULL, DEFAULT 0, CHECK (appear_at_s >= 0) |
| `disappear_at_s` | `INTEGER` | NULLABLE, CHECK (disappear_at_s IS NULL OR disappear_at_s > appear_at_s) |

Index : `idx_obstacle_scenario_id`, `idx_obstacle_track_id`

### Table `signal`

| Colonne | Type | Contraintes |
|---------|------|-------------|
| `id` | `UUID` | PK, DEFAULT gen_random_uuid() |
| `scenario_id` | `UUID` | NOT NULL, FK → scenario(id) ON DELETE CASCADE |
| `name` | `VARCHAR(100)` | NOT NULL |
| `type` | `VARCHAR(20)` | NOT NULL, CHECK (type IN ('CARRE', 'SEMAPHORE', 'AVERTISSEMENT', 'GUIDON', 'TGV_R')) |
| `track_id` | `UUID` | NOT NULL, FK → track_segment(id) ON DELETE CASCADE |
| `position_m` | `DOUBLE PRECISION` | NOT NULL, CHECK (position_m >= 0) |
| `direction` | `VARCHAR(10)` | NOT NULL, CHECK (direction IN ('PAIR', 'IMPAIR', 'BIDIR')) |
| `initial_state` | `VARCHAR(20)` | NOT NULL, CHECK (initial_state IN ('VOIE_LIBRE', 'ARRET', 'AVERTISSEMENT')) |

Index : `idx_signal_scenario_id`, `idx_signal_track_id`

---

## 10. Configuration

### 10.1 Prérequis

| Composant | Version | Installation (Arch Linux) |
|-----------|---------|--------------------------|
| JDK | 21+ | `sudo pacman -S jdk-openjdk` |
| Maven | 3.9+ | `sudo pacman -S maven` |
| Node.js | 20+ | `sudo pacman -S nodejs npm` |
| PostgreSQL | 16+ | `sudo pacman -S postgresql` |

### 10.2 Ports

| Service | Port |
|---------|------|
| Frontend (Vite dev) | `5173` |
| Backend (Spring Boot) | `8080` |
| PostgreSQL | `5432` |

### 10.3 application.yml (backend)

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/miniastra
    username: miniastra
    password: miniastra
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
  flyway:
    enabled: true
    locations: classpath:db/migration

logging:
  level:
    fr.miniastra: DEBUG
```

### 10.4 .env.local (frontend, non commité)

```
VITE_API_BASE_URL=http://localhost:8080/api
```

### 10.5 Configuration CORS (backend)

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
    }
}
```

### 10.6 Démarrage rapide

```bash
# 1. Base de données
createdb miniastra
psql -c "CREATE USER miniastra WITH PASSWORD 'miniastra';"
psql -c "GRANT ALL PRIVILEGES ON DATABASE miniastra TO miniastra;"

# 2. Backend
cd backend/
mvn spring-boot:run

# 3. Frontend
cd frontend/
npm install
npm run dev
```

### 10.7 Structure racine du projet

```
miniAstra/
├── backend/                     # Projet Maven / Spring Boot
│   ├── pom.xml
│   └── src/
├── frontend/                    # Projet Vite / React
│   ├── package.json
│   └── src/
├── docs/
│   ├── specs.md
│   └── architecture.md          # Ce fichier
└── scripts/
    └── init-db.sh
```
