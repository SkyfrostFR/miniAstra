# Tickets Frontend — MiniAstra

Preparateur de scenarios train France — React 18 / TypeScript / MapLibre GL JS / AG Grid

---

## Epic 1 — Fondations projet

---

### FRONT-01 — Setup Vite + React 18 + TypeScript

**Epic :** Fondations projet
**Priorite :** Must
**Estimation :** S (2h)
**Dependances :** aucune

**Description :**
Initialiser le projet frontend avec Vite, React 18 et TypeScript strict. Le squelette doit etre pret a accueillir toutes les dependances de la stack sans reconfiguration ulterieure.

**Taches techniques :**
- [ ] Creer le projet avec `npm create vite@latest miniAstra-frontend -- --template react-ts`
- [ ] Configurer `tsconfig.json` en mode strict (`"strict": true`, `"noUncheckedIndexedAccess": true`)
- [ ] Configurer `vite.config.ts` avec alias `@/` pointant sur `src/`
- [ ] Ajouter les dependances principales : `react@18`, `react-dom@18`, `react-router-dom@6`
- [ ] Ajouter les dependances de dev : `vitest`, `@testing-library/react`, `@testing-library/user-event`, `@testing-library/jest-dom`, `jsdom`, `@playwright/test`
- [ ] Configurer `vitest.config.ts` avec environment `jsdom` et setup file `src/test/setup.ts`
- [ ] Creer `src/test/setup.ts` avec import `@testing-library/jest-dom`
- [ ] Verifier que `npm run build` produit un bundle sans erreur TypeScript

**Criteres d'acceptance :**
- CA-1 : `npm run build` se termine sans erreur
- CA-2 : `npm test` execute la suite Vitest (0 test, 0 echec)
- CA-3 : L'alias `@/` est resolu correctement par TypeScript et Vite

**Tests attendus :**
- Test unitaire : smoke test `src/App.test.tsx` — le composant `<App />` se monte sans erreur
- Test integration : N/A a ce stade

---

### FRONT-02 — Configuration Tailwind CSS + shadcn/ui

**Epic :** Fondations projet
**Priorite :** Must
**Estimation :** S (2h)
**Dependances :** FRONT-01

**Description :**
Integrer Tailwind CSS v3 et initialiser shadcn/ui pour disposer du systeme de design (tokens, composants de base). Les composants shadcn utilises dans le projet seront ajoutes au fur et a mesure des tickets.

**Taches techniques :**
- [ ] Installer Tailwind CSS v3 : `npm install -D tailwindcss postcss autoprefixer` et `npx tailwindcss init -p`
- [ ] Configurer `tailwind.config.ts` avec le contenu `["./index.html", "./src/**/*.{ts,tsx}"]` et les tokens de couleur custom (palette MiniAstra)
- [ ] Ajouter les directives Tailwind dans `src/index.css`
- [ ] Initialiser shadcn/ui : `npx shadcn@latest init` (style default, base color slate, CSS variables activees)
- [ ] Ajouter les composants de base utilises dans le projet : `button`, `dropdown-menu`, `dialog`, `badge`, `select`, `tooltip`, `separator`
- [ ] Creer `src/lib/utils.ts` avec la fonction `cn()` (merge clsx + tailwind-merge)
- [ ] Verifier le rendu visuel sur la page de demarrage

**Criteres d'acceptance :**
- CA-1 : Les classes Tailwind sont compilees dans le bundle de production
- CA-2 : Un `<Button variant="destructive">` shadcn s'affiche avec le bon style
- CA-3 : La fonction `cn()` est importable depuis `@/lib/utils`

**Tests attendus :**
- Test unitaire : le composant `<Button />` shadcn se rend avec la bonne classe CSS selon la `variant`
- Test integration : N/A a ce stade

---

### FRONT-03 — Zustand stores (scenarioStore, mapStore, selectionStore, objectsStore, validationStore)

**Epic :** Fondations projet
**Priorite :** Must
**Estimation :** M (4h)
**Dependances :** FRONT-01

**Description :**
Creer les cinq stores Zustand qui constituent la source de verite globale de l'application. Chaque store expose un etat type et des actions clairement nommees. Aucune logique metier complexe dans cette iteration — uniquement la structure et les actions CRUD basiques.

**Taches techniques :**
- [ ] Installer Zustand : `npm install zustand`
- [ ] Creer `src/stores/scenarioStore.ts` : etat `{ currentScenario, scenarios }`, actions `setCurrentScenario`, `setScenarios`, `clearScenario`
- [ ] Creer `src/stores/mapStore.ts` : etat `{ center, zoom, mode }` (mode = `'select' | 'draw-track' | 'place-object' | 'drag-move'`), actions `setCenter`, `setZoom`, `setMode`
- [ ] Creer `src/stores/selectionStore.ts` : etat `{ selectedId, selectedType }`, actions `select`, `clearSelection`
- [ ] Creer `src/stores/objectsStore.ts` : etat `{ trackSegments, passengerTrains, freightTrains, obstacles, signals }`, actions `setAll`, `upsert`, `remove` pour chaque type
- [ ] Creer `src/stores/validationStore.ts` : etat `{ errors, warnings, lastValidatedAt }`, actions `setResults`, `clearResults`
- [ ] Typer chaque store avec les interfaces definies dans `src/types/` (FRONT-05)
- [ ] Exposer un hook par store (ex: `useScenarioStore()`)

**Criteres d'acceptance :**
- CA-1 : Chaque store est instanciable et son etat initial correspond aux valeurs par defaut
- CA-2 : Les actions mutent l'etat de facon immutable (immer ou pattern spread)
- CA-3 : Deux composants distincts consomment le meme store et restent synchronises

**Tests attendus :**
- Test unitaire : pour chaque store, verifier que les actions produisent l'etat attendu (ex: `upsert` sur `objectsStore` ajoute l'objet si absent, le remplace s'il existe)
- Test integration : N/A a ce stade

---

### FRONT-04 — Couche API client (fetch wrapper + clients par type)

**Epic :** Fondations projet
**Priorite :** Must
**Estimation :** M (4h)
**Dependances :** FRONT-01, FRONT-05

**Description :**
Creer la couche d'acces au backend REST (localhost:8080/api). Un wrapper fetch centralise gere les en-tetes, le timeout, la serialisation JSON et la normalisation des erreurs. Chaque type d'objet dispose de son propre fichier client.

**Taches techniques :**
- [ ] Creer `src/api/http.ts` : wrapper `apiFetch<T>(path, options)` avec base URL depuis `import.meta.env.VITE_API_BASE_URL`, content-type JSON, throw `ApiError` si status >= 400
- [ ] Definir `ApiError` avec `status: number` et `message: string`
- [ ] Creer `src/api/scenarioApi.ts` : `getAll()`, `getById(id)`, `create(dto)`, `update(id, dto)`, `remove(id)`
- [ ] Creer `src/api/trackSegmentApi.ts` : meme interface CRUD + `getByScenario(scenarioId)`
- [ ] Creer `src/api/passengerTrainApi.ts` : meme interface CRUD + `getByScenario(scenarioId)`
- [ ] Creer `src/api/freightTrainApi.ts` : meme interface CRUD + `getByScenario(scenarioId)`
- [ ] Creer `src/api/obstacleApi.ts` : meme interface CRUD + `getByScenario(scenarioId)`
- [ ] Creer `src/api/signalApi.ts` : meme interface CRUD + `getByScenario(scenarioId)`
- [ ] Creer `src/api/catalogApi.ts` : `getPassengerModels()`, `getFreightModels()`
- [ ] Creer `src/api/validationApi.ts` : `validate(scenarioId)` → `ValidationResult`
- [ ] Creer `src/api/exportApi.ts` : `exportType(scenarioId, type)` → `Blob`
- [ ] Ajouter `.env.development` avec `VITE_API_BASE_URL=http://localhost:8080/api`

**Criteres d'acceptance :**
- CA-1 : `apiFetch` leve une `ApiError` typee si le backend repond 4xx ou 5xx
- CA-2 : Tous les clients acceptent des generiques TypeScript et retournent des types corrects
- CA-3 : La base URL est configurable via variable d'environnement sans modifier le code

**Tests attendus :**
- Test unitaire : `apiFetch` avec un mock `fetch` retournant 404 → leve `ApiError` avec `status=404`
- Test unitaire : `trackSegmentApi.getByScenario()` appelle le bon endpoint `/track-segments?scenarioId=...`
- Test integration : N/A (backend requis)

---

### FRONT-05 — Types TypeScript + enums

**Epic :** Fondations projet
**Priorite :** Must
**Estimation :** S (2h)
**Dependances :** FRONT-01

**Description :**
Definir les types TypeScript et enums correspondant exactement au modele de donnees backend. Ces types sont la reference partagee par les stores, les schemas Zod et les clients API.

**Taches techniques :**
- [ ] Creer `src/types/scenario.ts` : interface `Scenario`
- [ ] Creer `src/types/trackSegment.ts` : interface `TrackSegment`, enum `Electrification` (`NONE | AC_25KV | DC_1500V`)
- [ ] Creer `src/types/passengerTrain.ts` : interface `PassengerTrain`, enum `Direction` (`PAIR | IMPAIR`)
- [ ] Creer `src/types/freightTrain.ts` : interface `FreightTrain`, enums `Direction`, `CargoType` (`VIDE | GENERAL | VRAC | CITERNE | DANGEREUX`)
- [ ] Creer `src/types/obstacle.ts` : interface `Obstacle`, enum `ObstacleType` (`VEHICULE | CHANTIER | GLISSEMENT | ANIMAL | OBJET`)
- [ ] Creer `src/types/signal.ts` : interface `Signal`, enum `SignalType` (`CARRE | SEMAPHORE | AVERTISSEMENT | GUIDON | TGV_R`), enum `SignalState` (`VOIE_LIBRE | ARRET | AVERTISSEMENT`), enum `SignalDirection` (`PAIR | IMPAIR | BIDIR`)
- [ ] Creer `src/types/catalog.ts` : interfaces `PassengerModel`, `FreightModel`
- [ ] Creer `src/types/validation.ts` : interfaces `ValidationError`, `ValidationWarning`, `ValidationResult`
- [ ] Creer `src/types/index.ts` re-exportant tous les types
- [ ] Typer le champ `selectedType` du selectionStore avec un union des 5 types d'objet + `null`

**Criteres d'acceptance :**
- CA-1 : Tous les types compilent sans erreur en mode strict
- CA-2 : Les enums sont utilises dans les interfaces (pas de `string` generique pour les champs a valeurs fixes)
- CA-3 : Un objet `TrackSegment` avec un champ `electrification: 'INCONNU'` produit une erreur TypeScript

**Tests attendus :**
- Test unitaire : N/A (types seulement — la compilation TypeScript fait office de test)
- Test integration : N/A

---

### FRONT-06 — Zod schemas pour les 5 types d'objets

**Epic :** Fondations projet
**Priorite :** Must
**Estimation :** M (4h)
**Dependances :** FRONT-05

**Description :**
Creer les schemas Zod correspondant aux 5 types d'objets MVP. Ces schemas servent a la validation cote client (formulaires DataGrid, creation d'objets) et doivent etre derives des types TypeScript (ou l'inverse via `z.infer`).

**Taches techniques :**
- [ ] Installer Zod : `npm install zod`
- [ ] Creer `src/schemas/scenarioSchema.ts` : schema avec validation `name` non vide, `duration_s` entier positif
- [ ] Creer `src/schemas/trackSegmentSchema.ts` : coordonnees dans plages valides (lat -90/90, lon -180/180), `max_speed_kmh` > 0, `track_count` in [1, 2], `grade_permil` in [-60, 60]
- [ ] Creer `src/schemas/passengerTrainSchema.ts` : `model_code` non vide, `position_m` >= 0, `initial_speed_kmh` >= 0, `passenger_count` >= 0
- [ ] Creer `src/schemas/freightTrainSchema.ts` : memes contraintes + `load_t` >= 0
- [ ] Creer `src/schemas/obstacleSchema.ts` : `appear_at_s` >= 0, `disappear_at_s` > `appear_at_s` si defini (RV-005), `speed_limit_kmh` >= 0
- [ ] Creer `src/schemas/signalSchema.ts` : `position_m` >= 0
- [ ] Creer `src/schemas/index.ts` re-exportant tous les schemas
- [ ] Verifier que `z.infer<typeof trackSegmentSchema>` est compatible avec `TrackSegment`

**Criteres d'acceptance :**
- CA-1 : Un obstacle avec `disappear_at_s <= appear_at_s` est rejete par le schema Zod
- CA-2 : Un troncon avec `start_lat = 200` est rejete (hors plage)
- CA-3 : Les messages d'erreur Zod sont en francais (`.refine()` avec messages explicites)

**Tests attendus :**
- Test unitaire : pour chaque schema, tester 1 cas valide + au moins 2 cas invalides couvrant les regles critiques (RV-005 pour obstacle, plage coordonnees pour troncon)
- Test integration : N/A

---

## Epic 2 — Layout et navigation

---

### FRONT-07 — ScenarioSelector (ecran d'accueil)

**Epic :** Layout et navigation
**Priorite :** Must
**Estimation :** M (4h)
**Dependances :** FRONT-03, FRONT-04, FRONT-05

**Description :**
Creer l'ecran d'accueil qui s'affiche quand aucun scenario n'est charge. L'utilisateur peut lister les scenarios existants, en ouvrir un ou en creer un nouveau. Une fois un scenario selectionne, l'application bascule sur le layout principal.

**Taches techniques :**
- [ ] Creer `src/components/shared/ScenarioSelector.tsx`
- [ ] Au montage, appeler `scenarioApi.getAll()` et afficher la liste dans un tableau simple (nom, description, date de creation)
- [ ] Bouton "Nouveau scenario" → ouvre un dialog (shadcn `Dialog`) avec formulaire `name` + `description` + `duration_s` + `start_time`, validation Zod avant soumission
- [ ] Clic sur un scenario → appelle `scenarioApi.getById(id)`, hydrate le `scenarioStore` et les `objectsStore` (appels paralleles `getByScenario` pour les 5 types), puis navigue vers `AppLayout`
- [ ] Gerer les etats de chargement (spinner) et d'erreur (message utilisateur)
- [ ] Bouton "Supprimer" sur chaque ligne avec confirmation `ConfirmDialog`

**Criteres d'acceptance :**
- CA-1 : La liste s'affiche au chargement et se rafraichit apres creation/suppression
- CA-2 : Le formulaire de creation bloque la soumission si `duration_s` n'est pas un entier positif
- CA-3 : Apres selection, `scenarioStore.currentScenario` est hydrate et l'utilisateur voit le layout principal

**Tests attendus :**
- Test unitaire : le formulaire de creation affiche une erreur si `duration_s` est negatif
- Test integration : mock `scenarioApi.getAll()` retourne 3 scenarios → les 3 noms sont visibles dans le DOM

---

### FRONT-08 — AppLayout (toolbar / carte / datagrid / statusbar)

**Epic :** Layout et navigation
**Priorite :** Must
**Estimation :** M (4h)
**Dependances :** FRONT-07

**Description :**
Creer le layout principal de l'application avec les quatre zones : toolbar en haut, carte a gauche, datagrid a droite, statusbar en bas. Le layout est responsive horizontalement (la carte et le datagrid se partagent la largeur) mais n'a pas besoin d'etre mobile-friendly.

**Taches techniques :**
- [ ] Creer `src/components/layout/AppLayout.tsx` avec CSS Grid ou Flexbox
- [ ] Zone carte : occupe ~60% de la largeur, hauteur = viewport - toolbar - statusbar
- [ ] Zone datagrid : occupe ~40% de la largeur, meme hauteur que la carte
- [ ] Barre de separateur redimensionnable (drag horizontal) entre carte et datagrid
- [ ] Passer les refs de dimensions aux composants enfants via context ou props
- [ ] Creer `src/components/layout/index.ts` re-exportant `AppLayout`

**Criteres d'acceptance :**
- CA-1 : Les quatre zones sont visibles et occupent la totalite de la fenetre
- CA-2 : Le separateur peut etre deplace entre 30% et 70% de la largeur
- CA-3 : Aucun scrollbar parasite ne s'affiche sur le body

**Tests attendus :**
- Test unitaire : `AppLayout` se rend sans erreur avec des enfants stub
- Test integration : N/A (visuel)

---

### FRONT-09 — Toolbar avec actions principales

**Epic :** Layout et navigation
**Priorite :** Must
**Estimation :** M (4h)
**Dependances :** FRONT-03, FRONT-08

**Description :**
Creer la barre d'outils avec les actions : mode selection, mode trace, menu "Ajouter objet" (5 types), bouton Valider, menu "Exporter", bouton Sauvegarder. Les actions mettent a jour le `mapStore` ou declenchent les operations correspondantes.

**Taches techniques :**
- [ ] Creer `src/components/layout/Toolbar.tsx`
- [ ] Bouton "Selection" → `mapStore.setMode('select')`, icone + tooltip
- [ ] Bouton "Tracer voie" → `mapStore.setMode('draw-track')`, icone + tooltip
- [ ] Dropdown "Ajouter" → 5 items (Troncon, Train passagers, Train marchandises, Obstacle, Signal) → `mapStore.setMode('place-object')` avec le type cible stocke dans `mapStore`
- [ ] Bouton "Valider" → appel `validationApi.validate(scenarioId)` → hydrate `validationStore`
- [ ] Composant `ExportMenu` (dropdown, voir FRONT-30)
- [ ] Bouton "Sauvegarder" → appel `scenarioApi.update(id, dto)` + feedback toast
- [ ] Le bouton actif (mode courant) est mis en evidence visuellement
- [ ] Desactiver "Valider" et "Exporter" si aucun scenario charge

**Criteres d'acceptance :**
- CA-1 : Cliquer "Tracer voie" passe le `mapStore.mode` a `'draw-track'` et le bouton est visuellement actif
- CA-2 : Le dropdown "Ajouter" liste exactement les 5 types d'objets
- CA-3 : "Sauvegarder" est desactive si `scenarioStore.currentScenario` est null

**Tests attendus :**
- Test unitaire : clic sur "Selection" → `mapStore.mode === 'select'`
- Test unitaire : clic sur "Valider" appelle `validationApi.validate` avec le bon `scenarioId`

---

### FRONT-10 — StatusBar (coordonnees, zoom, compteurs)

**Epic :** Layout et navigation
**Priorite :** Must
**Estimation :** S (2h)
**Dependances :** FRONT-03, FRONT-08

**Description :**
Creer la barre de statut inferieure affichant les coordonnees du curseur sur la carte, le niveau de zoom, et le nombre d'erreurs/warnings issus du `validationStore`.

**Taches techniques :**
- [ ] Creer `src/components/layout/StatusBar.tsx`
- [ ] Afficher lat/lon du curseur (format `48.8566° N, 2.3522° E`) mis a jour via `mapStore`
- [ ] Afficher le zoom courant (ex: `Zoom : 12.4`)
- [ ] Afficher `ValidationBadge` (voir FRONT-28) pour les erreurs et les warnings
- [ ] Si `currentScenario` est null, afficher "Aucun scenario charge" a la place des coordonnees
- [ ] Les coordonnees affichees s'arretent de se mettre a jour quand le curseur quitte la carte

**Criteres d'acceptance :**
- CA-1 : Les coordonnees s'actualisent quand le curseur se deplace sur la carte
- CA-2 : Le compteur d'erreurs affiche 0 si `validationStore.errors` est vide
- CA-3 : Aucun re-render inutile (les mises a jour de lat/lon ne declenchent pas un re-render du DataGrid)

**Tests attendus :**
- Test unitaire : avec `validationStore` contenant 2 erreurs et 1 warning, `StatusBar` affiche "2 erreurs" et "1 warning"
- Test integration : N/A

---

## Epic 3 — Carte MapLibre

---

### FRONT-11 — Initialisation MapLibre GL JS avec fond OSM

**Epic :** Carte MapLibre
**Priorite :** Must
**Estimation :** M (4h)
**Dependances :** FRONT-03, FRONT-08

**Description :**
Initialiser la carte MapLibre GL JS avec un fond de carte OpenStreetMap. La carte se monte dans le container fourni par `AppLayout` et synchronise center/zoom avec le `mapStore`.

**Taches techniques :**
- [ ] Installer `maplibre-gl` : `npm install maplibre-gl` + import CSS
- [ ] Creer `src/components/map/MapContainer.tsx` avec ref sur le div et initialisation `new maplibregl.Map(...)`
- [ ] Style OSM gratuit (ex: `https://demotiles.maplibre.org/style.json` en dev, configurable via env)
- [ ] Synchroniser `center` et `zoom` depuis `mapStore` a l'initialisation
- [ ] Emmettre `mapStore.setCenter` et `mapStore.setZoom` sur les evenements `move` et `zoom` de la carte
- [ ] Emettre `mapStore.setMouseCoords` sur l'evenement `mousemove`
- [ ] Creer un context `MapContext` exposant l'instance `maplibregl.Map`
- [ ] Cleanup `map.remove()` sur le unmount du composant

**Criteres d'acceptance :**
- CA-1 : La carte s'affiche correctement avec le fond OSM
- CA-2 : `mapStore.center` et `mapStore.zoom` restent synchronises quand l'utilisateur pan/zoom
- CA-3 : Aucune erreur de fuite memoire au remontage du composant

**Tests attendus :**
- Test unitaire : mock `maplibregl.Map`, verifier que `MapContainer` appelle `map.remove()` au unmount
- Test integration : N/A (necessite environnement WebGL)

---

### FRONT-12 — TrackLayer : rendu des troncons de voie

**Epic :** Carte MapLibre
**Priorite :** Must
**Estimation :** M (4h)
**Dependances :** FRONT-11, FRONT-03

**Description :**
Afficher les troncons de voie sur la carte comme des lignes GeoJSON colorees selon le type d'electrification. La couche se met a jour quand `objectsStore.trackSegments` change.

**Taches techniques :**
- [ ] Creer `src/components/map/layers/TrackLayer.tsx`
- [ ] Construire une `FeatureCollection` GeoJSON a partir de `objectsStore.trackSegments` (LineString avec start_lat/lon, waypoints, end_lat/lon)
- [ ] Couleur selon `electrification` : `NONE` = gris `#6B7280`, `AC_25KV` = rouge `#EF4444`, `DC_1500V` = bleu `#3B82F6`
- [ ] Epaisseur de ligne : 3px par defaut, 5px si selectionne (`selectionStore.selectedId`)
- [ ] Ajouter source `track-segments` et layer `track-segments-line` via `map.addSource` / `map.addLayer`
- [ ] Mettre a jour la source avec `map.getSource('track-segments').setData(...)` quand les donnees changent
- [ ] Nettoyer les sources et layers au unmount

**Criteres d'acceptance :**
- CA-1 : Un troncon AC_25KV s'affiche en rouge
- CA-2 : Le troncon selectionne est plus epais que les autres
- CA-3 : Ajouter un troncon dans le store met a jour la carte sans rechargement

**Tests attendus :**
- Test unitaire : la fonction `buildTrackGeoJson(segments)` produit une FeatureCollection valide avec le bon `color` par electrification
- Test integration : N/A

---

### FRONT-13 — TrainLayer : icones trains sur troncons

**Epic :** Carte MapLibre
**Priorite :** Must
**Estimation :** M (4h)
**Dependances :** FRONT-12

**Description :**
Afficher les trains (passagers et marchandises) comme des icones positionnees sur les troncons avec une fleche directionnelle indiquant le sens PAIR/IMPAIR.

**Taches techniques :**
- [ ] Creer `src/components/map/layers/TrainLayer.tsx`
- [ ] Calculer les coordonnees GPS du train a partir de `track_id`, `position_m` et la geometrie du troncon (interpolation lineaire sur la polyligne)
- [ ] Creer `src/components/map/utils/geoUtils.ts` : fonction `interpolatePosition(segment, position_m)` → `[lon, lat]` + `bearing` (angle de la ligne)
- [ ] Charger des images SVG pour icone passager et icone marchandises via `map.loadImage` + `map.addImage`
- [ ] Layer `symbol` MapLibre avec rotation selon bearing + sens PAIR/IMPAIR (IMPAIR = bearing + 180)
- [ ] Couleur d'icone differente pour passager vs marchandises
- [ ] Afficher le `name` du train comme label sous l'icone (layer text)

**Criteres d'acceptance :**
- CA-1 : Un train positionne a 50% d'un troncon apparait au bon endroit visuel
- CA-2 : La fleche directionnelle est correcte pour PAIR et IMPAIR
- CA-3 : Le nom du train est lisible sans chevaucher l'icone

**Tests attendus :**
- Test unitaire : `interpolatePosition` avec position_m = 0 retourne les coordonnees start du segment, position_m = length_m retourne les coordonnees end
- Test unitaire : `interpolatePosition` avec position_m hors plage leve une erreur explicite

---

### FRONT-14 — ObstacleLayer : icones obstacles avec opacite temporelle

**Epic :** Carte MapLibre
**Priorite :** Must
**Estimation :** M (4h)
**Dependances :** FRONT-12, FRONT-03

**Description :**
Afficher les obstacles sur les troncons. L'opacite de l'icone depend de la fenetre temporelle (`appear_at_s` / `disappear_at_s`) par rapport au temps courant du `TimeSlider`.

**Taches techniques :**
- [ ] Creer `src/components/map/layers/ObstacleLayer.tsx`
- [ ] Utiliser `interpolatePosition` (FRONT-13) pour placer chaque obstacle sur son troncon
- [ ] Ajouter le temps courant `currentTime` dans le `mapStore`
- [ ] Calculer l'opacite pour chaque obstacle : 0 si hors fenetre temporelle, 1 si dans la fenetre (`appear_at_s <= currentTime < disappear_at_s` ou `disappear_at_s` null)
- [ ] Icone differente selon `ObstacleType` (5 icones SVG distinctes)
- [ ] Afficher un symbole rouge si `blocking === true`
- [ ] Mettre a jour les opacites quand `currentTime` change (sans recreer les features)

**Criteres d'acceptance :**
- CA-1 : Un obstacle avec `appear_at_s=60` est invisible a `currentTime=30` et visible a `currentTime=90`
- CA-2 : Un obstacle permanent (`disappear_at_s=null`) est toujours visible
- CA-3 : Changer `currentTime` met a jour les opacites en moins de 16ms (pas de recreaction de source)

**Tests attendus :**
- Test unitaire : fonction `computeObstacleOpacity(obstacle, currentTime)` → 0 ou 1 selon la fenetre
- Test unitaire : un obstacle `blocking=true` a la bonne propriete dans les features GeoJSON

---

### FRONT-15 — SignalLayer : icones signaux selon etat initial

**Epic :** Carte MapLibre
**Priorite :** Must
**Estimation :** S (2h)
**Dependances :** FRONT-12

**Description :**
Afficher les signaux comme des icones colorees selon `initial_state`. L'icone varie selon le `type` du signal (carre, semaphore, etc.).

**Taches techniques :**
- [ ] Creer `src/components/map/layers/SignalLayer.tsx`
- [ ] Couleur selon `initial_state` : `VOIE_LIBRE` = vert, `ARRET` = rouge, `AVERTISSEMENT` = jaune
- [ ] Icone selon `type` : 5 icones SVG (`CARRE`, `SEMAPHORE`, `AVERTISSEMENT`, `GUIDON`, `TGV_R`)
- [ ] Orientation selon `direction` (bearing du troncon, IMPAIR = +180)
- [ ] Afficher le `name` du signal comme label

**Criteres d'acceptance :**
- CA-1 : Un signal `ARRET` s'affiche en rouge
- CA-2 : Un signal `SEMAPHORE` utilise l'icone semaphore et non l'icone carre
- CA-3 : Le label est visible au zoom >= 12

**Tests attendus :**
- Test unitaire : fonction `getSignalColor(state)` retourne le bon code couleur pour chaque etat
- Test integration : N/A

---

### FRONT-16 — SelectMode : clic sur objet → selection

**Epic :** Carte MapLibre
**Priorite :** Must
**Estimation :** S (2h)
**Dependances :** FRONT-12, FRONT-13, FRONT-14, FRONT-15, FRONT-03

**Description :**
En mode selection, un clic sur un objet de la carte le selectionne dans le `selectionStore`. Le DataGrid se synchronise automatiquement (FRONT-22).

**Taches techniques :**
- [ ] Creer `src/components/map/interactions/SelectMode.tsx`
- [ ] Enregistrer un listener `map.on('click', layerId, handler)` pour chacun des layers cliquables (tracks, trains, obstacles, signals)
- [ ] Le handler appelle `selectionStore.select(id, type)` avec l'id et le type de l'objet clique
- [ ] Propager l'event `stopPropagation` pour eviter les conflits entre layers superposes
- [ ] En mode selection uniquement (verifier `mapStore.mode === 'select'`)
- [ ] Clic dans le vide → `selectionStore.clearSelection()`
- [ ] Nettoyer les listeners au changement de mode ou au unmount

**Criteres d'acceptance :**
- CA-1 : Cliquer sur un troncon met `selectionStore.selectedId` a l'id du troncon
- CA-2 : Cliquer dans le vide remet `selectedId` a null
- CA-3 : En mode `draw-track`, les clics ne declenchent pas de selection

**Tests attendus :**
- Test unitaire : le handler appelle `selectionStore.select` avec les bons parametres quand la feature est cliquee
- Test integration : N/A

---

### FRONT-17 — DrawTrackMode : trace d'un troncon par clics

**Epic :** Carte MapLibre
**Priorite :** Must
**Estimation :** L (1j)
**Dependances :** FRONT-11, FRONT-03, FRONT-04

**Description :**
Implémenter le mode de trace de troncon. L'utilisateur clique pour ajouter des points intermediaires ; un double-clic termine le trace et cree le troncon via l'API.

**Taches techniques :**
- [ ] Creer `src/components/map/interactions/DrawTrackMode.tsx`
- [ ] A chaque clic simple : ajouter le point clique a la liste de waypoints locaux, afficher un marker temporaire
- [ ] Afficher une ligne provisoire reliant tous les waypoints
- [ ] Double-clic : dernier point = end point, appel `trackSegmentApi.create(dto)` avec `start_lat/lon`, `end_lat/lon`, `waypoints`, `length_m` calcule
- [ ] Calcul de `length_m` : somme des distances Haversine entre points consecutifs (fonction dans `geoUtils.ts`)
- [ ] Apres creation : hydrate `objectsStore`, passe en mode `select`, selectionne le nouveau troncon
- [ ] Echap annule le trace en cours et efface les markers temporaires
- [ ] Nettoyer les markers et la ligne provisoire au unmount ou au changement de mode

**Criteres d'acceptance :**
- CA-1 : Apres 3 clics et un double-clic, le troncon est cree avec les bons waypoints
- CA-2 : Echap annule sans creer d'objet
- CA-3 : La longueur calculee correspond a la distance Haversine relle (ecart < 1%)

**Tests attendus :**
- Test unitaire : `calculateHaversineDistance([48.8, 2.3], [48.9, 2.4])` retourne une valeur proche de la reference (~13.4 km)
- Test unitaire : `buildWaypoints(clicks)` retourne le premier point comme start, dernier comme end, et les intermediaires comme waypoints

---

### FRONT-18 — PlaceObjectMode : drag depuis palette → snap sur troncon

**Epic :** Carte MapLibre
**Priorite :** Must
**Estimation :** L (1j)
**Dependances :** FRONT-11, FRONT-12, FRONT-03, FRONT-04

**Description :**
Implementer le placement d'objet par drag depuis la palette de la toolbar. L'objet se snape sur le troncon le plus proche et cree l'entite via l'API si un troncon est trouve.

**Taches techniques :**
- [ ] Creer `src/components/map/interactions/PlaceObjectMode.tsx`
- [ ] Afficher un "ghost" visuel sous le curseur pendant le drag
- [ ] Sur drop : trouver le troncon le plus proche de la position droppee via `findNearestTrack(lngLat, segments)` (distance point-polyligne, max 50m)
- [ ] Si aucun troncon a moins de 50m : afficher un toast d'erreur "Placez l'objet sur un troncon"
- [ ] Si troncon trouve : calculer `position_m` (projection du point sur la polyligne), appeler l'API correspondante
- [ ] Implementer `findNearestTrack` et `projectPointOnPolyline` dans `geoUtils.ts`
- [ ] Apres creation : hydrate le store, selectionne le nouvel objet

**Criteres d'acceptance :**
- CA-1 : Dropper un train a moins de 50m d'un troncon le cree avec un `track_id` et un `position_m` corrects
- CA-2 : Dropper dans le vide affiche un message d'erreur et ne cree pas d'objet
- CA-3 : `position_m` est dans `[0, length_m]` du troncon

**Tests attendus :**
- Test unitaire : `projectPointOnPolyline(point, segment)` retourne `position_m` correct sur un segment droit
- Test unitaire : `findNearestTrack(lngLat, [])` retourne null si aucun segment

---

### FRONT-19 — DragMoveMode : deplacement d'objet et recalcul position_m

**Epic :** Carte MapLibre
**Priorite :** Should
**Estimation :** M (4h)
**Dependances :** FRONT-16, FRONT-18

**Description :**
Permettre de deplacer un objet deja place sur la carte par drag and drop. La nouvelle position est calculee en projetant la position droppee sur le troncon d'origine.

**Taches techniques :**
- [ ] Creer `src/components/map/interactions/DragMoveMode.tsx`
- [ ] En mode `select`, activer le drag sur les features des layers objet (trains, obstacles, signaux)
- [ ] Pendant le drag : mettre a jour la position visuelle du marker sans appeler l'API
- [ ] Au drop : recalculer `position_m` via `projectPointOnPolyline`, appeler l'API `update`, mettre a jour le store (mise a jour optimiste)
- [ ] Si le drop est trop loin du troncon d'origine (> 100m) : annuler le deplacement et remettre l'objet a sa position initiale
- [ ] Empecher le drag quand `mapStore.mode !== 'select'`

**Criteres d'acceptance :**
- CA-1 : Apres deplacement, `position_m` est mis a jour dans le store et dans l'API
- CA-2 : Dropper trop loin du troncon remet l'objet a sa position initiale avec un toast
- CA-3 : Le drag est fluide (pas de latence perceptible pendant le deplacement)

**Tests attendus :**
- Test unitaire : la mise a jour optimiste met a jour le store avant la reponse API
- Test unitaire : si l'appel API echoue, le store est revenu a l'etat anterieur (rollback)

---

### FRONT-20 — TimeSlider : curseur temporel et masquage obstacles

**Epic :** Carte MapLibre
**Priorite :** Must
**Estimation :** S (2h)
**Dependances :** FRONT-14, FRONT-03

**Description :**
Creer le curseur temporel (0 → `duration_s`) qui met a jour `mapStore.currentTime`. L'`ObstacleLayer` reagit a ce changement pour masquer/afficher les obstacles hors fenetre.

**Taches techniques :**
- [ ] Creer `src/components/layout/TimeSlider.tsx`
- [ ] Input `range` HTML natif de 0 a `scenario.duration_s`, pas de 1 seconde
- [ ] Afficher le temps courant formatte (ex: `00:01:30` pour 90s)
- [ ] Mettre a jour `mapStore.currentTime` a chaque changement
- [ ] Positionner le `TimeSlider` dans la zone carte (overlay bas de carte ou dans la toolbar)
- [ ] Desactiver si aucun scenario charge

**Criteres d'acceptance :**
- CA-1 : Bouger le curseur a 60s masque les obstacles avec `appear_at_s > 60`
- CA-2 : Le format d'affichage est `HH:MM:SS`
- CA-3 : Le curseur revient a 0 quand on change de scenario

**Tests attendus :**
- Test unitaire : `formatTime(90)` retourne `"00:01:30"`
- Test unitaire : `formatTime(3661)` retourne `"01:01:01"`

---

## Epic 4 — DataGrid AG Grid

---

### FRONT-21 — DataGridPanel avec filtre par type d'objet

**Epic :** DataGrid AG Grid
**Priorite :** Must
**Estimation :** M (4h)
**Dependances :** FRONT-03, FRONT-08

**Description :**
Creer le panel DataGrid avec AG Grid Community. Un dropdown permet de filtrer l'affichage par type d'objet. Les colonnes et donnees sont chargees dynamiquement selon le type selectionne.

**Taches techniques :**
- [ ] Installer `ag-grid-community` et `ag-grid-react` : `npm install ag-grid-community ag-grid-react`
- [ ] Creer `src/components/grid/DataGridPanel.tsx`
- [ ] Dropdown de filtre (shadcn `Select`) : Troncons, Trains passagers, Trains marchandises, Obstacles, Signaux
- [ ] Charger les colonnes selon le type selectionne depuis `src/components/grid/columns/`
- [ ] Charger les donnees depuis le store correspondant (`objectsStore`)
- [ ] Configurer AG Grid : `rowSelection='single'`, `animateRows`, theme `ag-theme-alpine`
- [ ] Ajouter `src/components/grid/columns/index.ts` re-exportant toutes les definitions de colonnes

**Criteres d'acceptance :**
- CA-1 : Changer le filtre de type affiche les bonnes colonnes et les bonnes donnees
- CA-2 : La grille affiche toutes les entites du store pour le type selectionne
- CA-3 : Aucune erreur de console en changeant de type

**Tests attendus :**
- Test unitaire : `DataGridPanel` avec `objectsStore` contenant 3 troncons → la grille affiche 3 lignes quand le filtre est sur "Troncons"
- Test integration : N/A

---

### FRONT-22 — Synchronisation bidirectionnelle selection carte <-> DataGrid

**Epic :** DataGrid AG Grid
**Priorite :** Must
**Estimation :** M (4h)
**Dependances :** FRONT-21, FRONT-16

**Description :**
Lorsqu'un objet est selectionne sur la carte, la ligne correspondante est selectionnee dans le DataGrid (et le type filtre est mis a jour). Inversement, selectionner une ligne dans le DataGrid selectionne l'objet sur la carte.

**Taches techniques :**
- [ ] Dans `DataGridPanel`, souscrire a `selectionStore` et appeler `gridApi.getRowNode(id).setSelected(true)` quand `selectedId` change
- [ ] Si le type selectionne ne correspond pas au filtre courant, changer le filtre automatiquement
- [ ] Faire defiler la grille pour rendre la ligne selectionnee visible (`gridApi.ensureNodeVisible`)
- [ ] Sur `onSelectionChanged` d'AG Grid : appeler `selectionStore.select(id, type)` si la selection vient de l'utilisateur (flag pour eviter la boucle)
- [ ] Creer `src/hooks/useSelection.ts` encapsulant la logique de synchronisation

**Criteres d'acceptance :**
- CA-1 : Cliquer sur un obstacle sur la carte selectionne la ligne obstacle dans la grille et passe le filtre sur "Obstacles"
- CA-2 : Selectionner une ligne dans la grille met a jour `selectionStore` sans boucle infinie
- CA-3 : La grille scrolle pour rendre la ligne selectionnee visible

**Tests attendus :**
- Test unitaire : `useSelection` avec `selectedId` changeant dans le store → `gridApi.getRowNode(id).setSelected` est appele
- Test unitaire : la boucle de synchronisation ne se declenche pas plus d'une fois par selection

---

### FRONT-23 — Colonnes et editeurs pour TrackSegment

**Epic :** DataGrid AG Grid
**Priorite :** Must
**Estimation :** M (4h)
**Dependances :** FRONT-21, FRONT-06

**Description :**
Definir les colonnes AG Grid pour les troncons de voie avec editeurs inline adaptes au type de chaque champ.

**Taches techniques :**
- [ ] Creer `src/components/grid/columns/trackSegmentColumns.ts`
- [ ] Colonnes : `name` (editable, texte), `length_m` (lecture seule, formatte en km), `max_speed_kmh` (editable, numerique), `track_count` (editable, dropdown 1/2), `electrification` (editable, `EnumCellEditor`), `grade_permil` (editable, numerique avec unite ‰)
- [ ] Creer `src/components/grid/editors/EnumCellEditor.tsx` : dropdown AG Grid avec les valeurs de l'enum passe en parametre
- [ ] Creer `src/components/grid/editors/NumberCellEditor.tsx` : input numerique avec validation Zod inline (affiche erreur rouge si invalide)
- [ ] Renderer `src/components/grid/renderers/ElectrificationRenderer.tsx` : badge colore selon la valeur

**Criteres d'acceptance :**
- CA-1 : Modifier `max_speed_kmh` avec une valeur negative affiche une erreur rouge sans sauvegarder
- CA-2 : Modifier `electrification` via dropdown met a jour le store et appelle l'API
- CA-3 : `length_m` s'affiche en km avec 2 decimales (ex: "12.34 km") et n'est pas editable

**Tests attendus :**
- Test unitaire : `NumberCellEditor` avec schema Zod `z.number().positive()` → la valeur `-5` est rejetee
- Test unitaire : `EnumCellEditor` affiche les bonnes options pour `Electrification`

---

### FRONT-24 — Colonnes et editeurs pour PassengerTrain et FreightTrain

**Epic :** DataGrid AG Grid
**Priorite :** Must
**Estimation :** M (4h)
**Dependances :** FRONT-23

**Description :**
Definir les colonnes AG Grid pour les trains passagers et marchandises, incluant un selecteur de modele catalogue.

**Taches techniques :**
- [ ] Creer `src/components/grid/columns/passengerTrainColumns.ts` : `name`, `model_code` (dropdown catalogue), `track_id` (lookup nom troncon), `position_m`, `direction` (enum), `initial_speed_kmh`, `passenger_count`, `service_number`
- [ ] Creer `src/components/grid/columns/freightTrainColumns.ts` : memes champs communs + `load_t`, `cargo_type` (enum)
- [ ] Creer `src/components/grid/editors/CatalogCellEditor.tsx` : dropdown affichant `code — modele (longueur, vitesse_max)` depuis `catalogApi`
- [ ] Renderer `src/components/grid/renderers/TrackNameRenderer.tsx` : affiche le nom du troncon au lieu de l'UUID
- [ ] Valider `position_m` contre `length_m` du troncon selectionne (via `objectsStore`)

**Criteres d'acceptance :**
- CA-1 : Le dropdown de modele affiche les 7 modeles passagers (ou les 4 fret selon le type)
- CA-2 : `position_m` superieur a `length_m` du troncon affiche une erreur inline
- CA-3 : `track_id` affiche le nom du troncon et non l'UUID

**Tests attendus :**
- Test unitaire : `CatalogCellEditor` avec 7 modeles → 7 options dans le dropdown
- Test unitaire : `position_m = 1000` sur un troncon de `length_m = 500` est invalide

---

### FRONT-25 — Colonnes et editeurs pour Obstacle

**Epic :** DataGrid AG Grid
**Priorite :** Must
**Estimation :** M (4h)
**Dependances :** FRONT-23, FRONT-06

**Description :**
Definir les colonnes AG Grid pour les obstacles avec gestion de la fenetre temporelle.

**Taches techniques :**
- [ ] Creer `src/components/grid/columns/obstacleColumns.ts` : `name`, `type` (enum), `track_id` (lookup), `position_m`, `length_m`, `blocking` (checkbox), `speed_limit_kmh`, `visibility_m`, `appear_at_s`, `disappear_at_s`
- [ ] Renderer `src/components/grid/renderers/TimeWindowRenderer.tsx` : affiche `appear_at_s` et `disappear_at_s` formates en `HH:MM:SS`
- [ ] Valider que `disappear_at_s > appear_at_s` si les deux sont renseignes (RV-005)
- [ ] Griser `speed_limit_kmh` si `blocking === true`

**Criteres d'acceptance :**
- CA-1 : `disappear_at_s <= appear_at_s` produit une erreur inline
- CA-2 : `speed_limit_kmh` est non editable quand `blocking = true`
- CA-3 : Les temps s'affichent en `HH:MM:SS`

**Tests attendus :**
- Test unitaire : validation `disappear_at_s > appear_at_s` avec schema Zod dans l'editeur
- Test unitaire : `TimeWindowRenderer` avec `appear_at_s = 3661` affiche `"01:01:01"`

---

### FRONT-26 — Colonnes et editeurs pour Signal

**Epic :** DataGrid AG Grid
**Priorite :** Must
**Estimation :** S (2h)
**Dependances :** FRONT-23

**Description :**
Definir les colonnes AG Grid pour les signaux.

**Taches techniques :**
- [ ] Creer `src/components/grid/columns/signalColumns.ts` : `name`, `type` (enum), `track_id` (lookup), `position_m`, `direction` (enum), `initial_state` (enum)
- [ ] Renderer `src/components/grid/renderers/SignalStateRenderer.tsx` : badge colore selon `initial_state` (vert/rouge/jaune)
- [ ] Renderer `src/components/grid/renderers/SignalTypeRenderer.tsx` : icone selon `type`

**Criteres d'acceptance :**
- CA-1 : Un signal `ARRET` affiche un badge rouge dans la colonne `initial_state`
- CA-2 : Les enums `type` et `direction` sont editables via `EnumCellEditor`
- CA-3 : `position_m` est valide contre `length_m` du troncon

**Tests attendus :**
- Test unitaire : `SignalStateRenderer` avec `initial_state = 'ARRET'` affiche le bon badge

---

### FRONT-27 — Mise a jour optimiste et appel API sur modification DataGrid

**Epic :** DataGrid AG Grid
**Priorite :** Must
**Estimation :** M (4h)
**Dependances :** FRONT-21, FRONT-04, FRONT-03

**Description :**
Creer le hook `useObjects` qui gere la mise a jour optimiste des objets : le store est mis a jour immediatement, l'appel API est lance en arriere-plan, et le store est revenu en arriere si l'API echoue.

**Taches techniques :**
- [ ] Creer `src/hooks/useObjects.ts`
- [ ] Fonction `updateObject(type, id, patch)` : 1) snapshot de l'etat actuel, 2) `objectsStore.upsert(type, updated)`, 3) appel API `update(id, patch)`, 4) si echec → `objectsStore.upsert(type, snapshot)` + toast d'erreur
- [ ] Fonction `deleteObject(type, id)` : confirmation (`ConfirmDialog`) → appel API → `objectsStore.remove(type, id)` ou rollback
- [ ] Brancher `onCellValueChanged` d'AG Grid sur `updateObject`
- [ ] Debounce de 300ms pour les modifications de champs texte/numerique (eviter les appels API a chaque frappe)

**Criteres d'acceptance :**
- CA-1 : Modifier une valeur met a jour le store immediatement, avant la reponse API
- CA-2 : Si l'API retourne 400, le store revient a la valeur precedente et un toast d'erreur est affiche
- CA-3 : Le debounce empeche plus d'un appel API par champ tant que l'utilisateur tape

**Tests attendus :**
- Test unitaire : si `trackSegmentApi.update` rejette → `objectsStore` revient a l'etat precedent
- Test unitaire : deux modifications rapides → un seul appel API apres debounce

---

## Epic 5 — Validation

---

### FRONT-28 — ValidationBadge dans la toolbar

**Epic :** Validation
**Priorite :** Must
**Estimation :** XS (< 1h)
**Dependances :** FRONT-03, FRONT-09

**Description :**
Afficher dans la toolbar un badge indiquant le nombre d'erreurs et de warnings issus du `validationStore`. Ce composant est reutilise dans la `StatusBar`.

**Taches techniques :**
- [ ] Creer `src/components/validation/ValidationBadge.tsx`
- [ ] Afficher `N erreur(s)` en rouge si `errors.length > 0`
- [ ] Afficher `N warning(s)` en orange si `warnings.length > 0`
- [ ] Afficher `OK` en vert si les deux sont vides
- [ ] Le badge est cliquable et ouvre le `ValidationPanel` (FRONT-29)
- [ ] Afficher un spinner pendant la validation en cours (etat `isValidating` dans `validationStore`)

**Criteres d'acceptance :**
- CA-1 : `validationStore` avec 2 erreurs → badge rouge "2 erreur(s)"
- CA-2 : `validationStore` vide → badge vert "OK"
- CA-3 : Le spinner est visible pendant l'appel `validationApi.validate`

**Tests attendus :**
- Test unitaire : `ValidationBadge` avec `{ errors: [{...}, {...}], warnings: [] }` → affiche "2 erreur(s)" en rouge
- Test unitaire : `ValidationBadge` avec `{ errors: [], warnings: [] }` → affiche "OK"

---

### FRONT-29 — ValidationPanel : liste des erreurs cliquables

**Epic :** Validation
**Priorite :** Must
**Estimation :** M (4h)
**Dependances :** FRONT-28, FRONT-22

**Description :**
Panel lateral ou modal listant toutes les erreurs et warnings. Un clic sur une erreur selectionne l'objet concerne dans la carte et le DataGrid.

**Taches techniques :**
- [ ] Creer `src/components/validation/ValidationPanel.tsx`
- [ ] Afficher la liste des erreurs (groupe "Erreurs bloquantes") et warnings (groupe "Avertissements")
- [ ] Chaque item : icone severite + code regle (ex: RV-002) + message + nom de l'objet concerne
- [ ] Clic sur un item → `selectionStore.select(objectId, objectType)` → la carte et la grille se synchronisent (FRONT-22)
- [ ] Badge de comptage en en-tete de chaque groupe
- [ ] Bouton "Relancer la validation" qui appelle `validationApi.validate` et rafraichit le panel
- [ ] Le panel peut etre ferme (toggle depuis `ValidationBadge`)

**Criteres d'acceptance :**
- CA-1 : Cliquer sur une erreur RV-002 sur un signal selectionne ce signal dans la carte et affiche "Signaux" dans le DataGrid
- CA-2 : Apres correction et re-validation, les erreurs resolues disparaissent de la liste
- CA-3 : Le panel est scrollable si la liste est longue

**Tests attendus :**
- Test unitaire : clic sur un item d'erreur → `selectionStore.select` est appele avec le bon `objectId`
- Test integration : mock `validationApi.validate` retournant 1 erreur → le panel affiche 1 item dans le groupe "Erreurs bloquantes"

---

## Epic 6 — Export Excel

---

### FRONT-30 — ExportMenu dropdown (5 types)

**Epic :** Export Excel
**Priorite :** Must
**Estimation :** S (2h)
**Dependances :** FRONT-09, FRONT-28

**Description :**
Creer le dropdown d'export permettant de telecharger un fichier Excel pour chacun des 5 types d'objets. L'export est bloque si des erreurs bloquantes sont presentes.

**Taches techniques :**
- [ ] Creer `src/components/shared/ExportMenu.tsx`
- [ ] Dropdown shadcn avec 5 items : Troncons, Trains passagers, Trains marchandises, Obstacles, Signaux
- [ ] Chaque item est desactive si `validationStore.errors.length > 0` avec tooltip "Corrigez les erreurs avant d'exporter"
- [ ] Clic sur un item actif declenche `useExport.exportType(scenarioId, type)` (FRONT-31)
- [ ] Afficher un spinner dans l'item pendant le telechargement
- [ ] L'ensemble du menu est desactive si aucun scenario n'est charge

**Criteres d'acceptance :**
- CA-1 : Avec des erreurs bloquantes, tous les items sont grises et le tooltip est visible
- CA-2 : Sans erreur, cliquer "Troncons" declenche le telechargement du fichier Excel
- CA-3 : Le spinner est visible pendant la generation du fichier cote serveur

**Tests attendus :**
- Test unitaire : avec `validationStore.errors = [{ ... }]`, tous les items du menu ont l'attribut `disabled`
- Test unitaire : clic sur un item actif appelle `exportApi.exportType` avec les bons parametres

---

### FRONT-31 — Hook useExport : appel API → blob → download

**Epic :** Export Excel
**Priorite :** Must
**Estimation :** S (2h)
**Dependances :** FRONT-04

**Description :**
Creer le hook `useExport` qui encapsule l'appel a `exportApi`, recupere le blob Excel retourne par le backend, et declenche le telechargement automatique dans le navigateur.

**Taches techniques :**
- [ ] Creer `src/hooks/useExport.ts`
- [ ] Fonction `exportType(scenarioId, type)` : appel `exportApi.exportType(scenarioId, type)` → blob
- [ ] Creer un `<a>` element avec `URL.createObjectURL(blob)` et le `download` attribute, cliquer programmatiquement, puis revoquer l'URL
- [ ] Nom du fichier : `{scenarioName}_{type}_{YYYY-MM-DD}.xlsx`
- [ ] Gerer l'etat `isExporting` (boolean) et `exportError` (string | null)
- [ ] Si erreur API : afficher un toast d'erreur

**Criteres d'acceptance :**
- CA-1 : Appeler `exportType` declenche un telechargement de fichier `.xlsx` dans le navigateur
- CA-2 : Le nom du fichier suit le format `{scenarioName}_{type}_{date}.xlsx`
- CA-3 : Une erreur API est communiquee a l'utilisateur par toast

**Tests attendus :**
- Test unitaire : mock `exportApi.exportType` retournant un `Blob` → un element `<a>` est cree avec `download` contenant `.xlsx`
- Test unitaire : mock retournant une erreur → `exportError` est defini et `isExporting` repasse a false

---

## Tickets transversaux

---

### FRONT-32 — ConfirmDialog : composant de confirmation generique

**Epic :** Fondations projet
**Priorite :** Must
**Estimation :** XS (< 1h)
**Dependances :** FRONT-02

**Description :**
Creer un composant de confirmation reutilisable base sur le `Dialog` shadcn. Utilise pour la suppression d'objets et l'annulation d'operations destructives.

**Taches techniques :**
- [ ] Creer `src/components/shared/ConfirmDialog.tsx`
- [ ] Props : `open`, `onOpenChange`, `title`, `description`, `onConfirm`, `onCancel`, `confirmLabel`, `variant` (`'default' | 'destructive'`)
- [ ] Utiliser le composant `Dialog` shadcn
- [ ] Focus automatique sur le bouton "Annuler" (action par defaut safe)
- [ ] Fermeture par Echap ou clic en dehors = annulation

**Criteres d'acceptance :**
- CA-1 : Appuyer sur Echap appelle `onCancel`
- CA-2 : Clic sur "Confirmer" appelle `onConfirm`
- CA-3 : Le bouton "Confirmer" est rouge si `variant='destructive'`

**Tests attendus :**
- Test unitaire : `ConfirmDialog` avec `open=true` → le dialog est visible dans le DOM
- Test unitaire : clic sur "Confirmer" → `onConfirm` est appele une seule fois

---

### FRONT-33 — useScenario : hook de chargement de scenario

**Epic :** Layout et navigation
**Priorite :** Must
**Estimation :** S (2h)
**Dependances :** FRONT-03, FRONT-04

**Description :**
Creer le hook `useScenario` qui centralise le chargement et le dechargement d'un scenario : appels API paralleles pour les 5 types d'objets, hydratation des stores, gestion des erreurs.

**Taches techniques :**
- [ ] Creer `src/hooks/useScenario.ts`
- [ ] Fonction `loadScenario(id)` : appels `Promise.all` sur les 5 `getByScenario(id)` + `getById(id)`, hydrate les stores, retourne `{ scenario, error }`
- [ ] Fonction `unloadScenario()` : `clearScenario()` dans tous les stores
- [ ] Etat `isLoading` pendant le chargement
- [ ] Si une ressource echoue a charger : afficher les autres et signaler l'erreur partielle

**Criteres d'acceptance :**
- CA-1 : `loadScenario` avec mock retournant 5 listes de donnees hydrate les 5 stores
- CA-2 : `isLoading` est vrai pendant le `Promise.all` et faux apres
- CA-3 : Si `obstacleApi.getByScenario` echoue, les 4 autres types sont quand meme charges

**Tests attendus :**
- Test unitaire : mock `Promise.all` resolving → les 5 stores sont remplis
- Test unitaire : mock avec un rejet partiel → `isLoading = false`, erreur partielle signalee

---

### FRONT-34 — useMapInteraction : hook de gestion du mode carte

**Epic :** Carte MapLibre
**Priorite :** Must
**Estimation :** S (2h)
**Dependances :** FRONT-11, FRONT-03

**Description :**
Creer le hook `useMapInteraction` qui monte/demonte les interactions carte selon le mode courant du `mapStore`. Chaque mode active ses propres handlers et desactive les autres.

**Taches techniques :**
- [ ] Creer `src/hooks/useMapInteraction.ts`
- [ ] Lire `mapStore.mode` et activer/desactiver les composants d'interaction selon le mode
- [ ] Retourner le composant d'interaction actif a rendre dans `MapContainer`
- [ ] Assurer le cleanup des event listeners au changement de mode

**Criteres d'acceptance :**
- CA-1 : Passer de `select` a `draw-track` desactive les listeners `SelectMode` et active ceux de `DrawTrackMode`
- CA-2 : Aucune fuite de listener au changement de mode (verifiable avec les listeners MapLibre)

**Tests attendus :**
- Test unitaire : mock `mapStore.mode = 'draw-track'` → le hook retourne `DrawTrackMode`
- Test unitaire : changement de mode → cleanup de l'ancien mode appele

---

### FRONT-35 — useValidation : hook de validation

**Epic :** Validation
**Priorite :** Must
**Estimation :** XS (< 1h)
**Dependances :** FRONT-03, FRONT-04

**Description :**
Creer le hook `useValidation` qui encapsule l'appel a `validationApi` et met a jour le `validationStore`.

**Taches techniques :**
- [ ] Creer `src/hooks/useValidation.ts`
- [ ] Fonction `validate()` : passe `isValidating` a true, appelle `validationApi.validate(scenarioId)`, hydrate `validationStore.setResults(result)`, repasse `isValidating` a false
- [ ] Gerer les erreurs API avec toast

**Criteres d'acceptance :**
- CA-1 : Apres `validate()`, `validationStore.errors` et `validationStore.warnings` sont mis a jour
- CA-2 : `isValidating` est true pendant l'appel et false apres (succes ou echec)

**Tests attendus :**
- Test unitaire : mock `validationApi.validate` retournant 1 erreur → `validationStore.errors.length === 1`

---

## Recapitulatif

| Ticket | Epic | Priorite | Estimation |
|--------|------|----------|------------|
| FRONT-01 | Fondations | Must | S (2h) |
| FRONT-02 | Fondations | Must | S (2h) |
| FRONT-03 | Fondations | Must | M (4h) |
| FRONT-04 | Fondations | Must | M (4h) |
| FRONT-05 | Fondations | Must | S (2h) |
| FRONT-06 | Fondations | Must | M (4h) |
| FRONT-07 | Layout | Must | M (4h) |
| FRONT-08 | Layout | Must | M (4h) |
| FRONT-09 | Layout | Must | M (4h) |
| FRONT-10 | Layout | Must | S (2h) |
| FRONT-11 | Carte | Must | M (4h) |
| FRONT-12 | Carte | Must | M (4h) |
| FRONT-13 | Carte | Must | M (4h) |
| FRONT-14 | Carte | Must | M (4h) |
| FRONT-15 | Carte | Must | S (2h) |
| FRONT-16 | Carte | Must | S (2h) |
| FRONT-17 | Carte | Must | L (1j) |
| FRONT-18 | Carte | Must | L (1j) |
| FRONT-19 | Carte | Should | M (4h) |
| FRONT-20 | Carte | Must | S (2h) |
| FRONT-21 | DataGrid | Must | M (4h) |
| FRONT-22 | DataGrid | Must | M (4h) |
| FRONT-23 | DataGrid | Must | M (4h) |
| FRONT-24 | DataGrid | Must | M (4h) |
| FRONT-25 | DataGrid | Must | M (4h) |
| FRONT-26 | DataGrid | Must | S (2h) |
| FRONT-27 | DataGrid | Must | M (4h) |
| FRONT-28 | Validation | Must | XS (< 1h) |
| FRONT-29 | Validation | Must | M (4h) |
| FRONT-30 | Export | Must | S (2h) |
| FRONT-31 | Export | Must | S (2h) |
| FRONT-32 | Transversal | Must | XS (< 1h) |
| FRONT-33 | Transversal | Must | S (2h) |
| FRONT-34 | Transversal | Must | S (2h) |
| FRONT-35 | Transversal | Must | XS (< 1h) |

**Total estime :** ~17.5 jours de developpement

**Chemin critique :**
FRONT-01 → FRONT-05 → FRONT-06 → FRONT-03 → FRONT-04 → FRONT-07 → FRONT-08 → FRONT-11 → FRONT-12 → FRONT-17/FRONT-18 → FRONT-21 → FRONT-22 → FRONT-27 → FRONT-29
