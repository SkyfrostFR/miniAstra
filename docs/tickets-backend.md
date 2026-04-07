# Tickets Backend — MiniAstra

Préparateur de scénarios train France — Java 21 / Spring Boot 3.3 / PostgreSQL 16

---

## Epic 1 — Fondations projet

---

### BACK-01 — Initialisation du projet Maven + Spring Boot 3.3

**Epic :** Fondations projet
**Priorite :** Must
**Estimation :** S (2h)
**Dependances :** aucune

**Description :**
Mettre en place le projet Maven multi-module avec Spring Boot 3.3, Java 21, et toutes les dependances necessaires au backend MiniAstra. Le `pom.xml` doit refleter exactement la stack cible pour eviter tout ajout ulterieur en dehors des migrations.

**Taches techniques :**
- [ ] Creer le projet Spring Boot 3.3 via Spring Initializr (groupId `fr.miniastra`, artifactId `miniastra`)
- [ ] Configurer Java 21 (`maven.compiler.source` / `target`)
- [ ] Ajouter les dependances : `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-validation`, `flyway-core`, `flyway-database-postgresql`, `postgresql` (driver), `mapstruct`, `mapstruct-processor`, `lombok`, `lombok-mapstruct-binding`, `apache-poi`, `apache-poi-ooxml`
- [ ] Ajouter les dependances de test : `spring-boot-starter-test`, `testcontainers` (postgresql), `junit-jupiter`
- [ ] Configurer le plugin `maven-compiler-plugin` avec les `annotationProcessorPaths` pour Lombok + MapStruct (ordre obligatoire : Lombok avant MapStruct)
- [ ] Valider la compilation (`mvn compile` sans erreur)

**Criteres d'acceptance :**
- CA-1 : `mvn compile` produit un build SUCCESS sans warning de compatibilite de version
- CA-2 : Les annotations `@Getter`, `@Builder`, `@Mapper` sont reconnues par le compilateur
- CA-3 : La dependance Apache POI est resolvable en local

**Tests attendus :**
- Test unitaire : le contexte Spring Boot demarre sans `DataSource` configuree (`@SpringBootTest` avec `spring.autoconfigure.exclude=DataSourceAutoConfiguration`)
- Test integration : N/A a ce stade

---

### BACK-02 — Configuration PostgreSQL, Flyway et profils d'environnement

**Epic :** Fondations projet
**Priorite :** Must
**Estimation :** S (2h)
**Dependances :** BACK-01

**Description :**
Configurer la connexion PostgreSQL 16 via `application.yml`, activer Flyway pour la gestion des migrations, et definir les profils `dev` / `test` / `prod` afin que chaque environnement pointe sur la bonne base de donnees.

**Taches techniques :**
- [ ] Creer `src/main/resources/application.yml` avec proprietes datasource, JPA (DDL auto = `validate`) et Flyway (`enabled=true`, `locations=classpath:db/migration`)
- [ ] Creer `src/main/resources/application-dev.yml` (base `miniastra_dev`, credentials via variables d'environnement `DB_USER` / `DB_PASSWORD`)
- [ ] Creer `src/test/resources/application-test.yml` pointant sur Testcontainers PostgreSQL (`spring.datasource.url=` dynamique)
- [ ] Ajouter `FlywayConfig.java` dans `config/` si une personnalisation du DataSource est necessaire (schema cible, baseline-on-migrate)
- [ ] Verifier que Flyway refuse de demarrer si aucune migration n'existe (comportement attendu avant BACK-08)

**Criteres d'acceptance :**
- CA-1 : Avec une base vide et `SPRING_PROFILES_ACTIVE=dev`, l'application demarre et Flyway log "Successfully validated 0 migrations"
- CA-2 : Les credentials ne sont jamais ecrits en dur dans les fichiers de configuration
- CA-3 : Le profil `test` utilise bien Testcontainers (URL contient `tc:postgresql`)

**Tests attendus :**
- Test integration : `DataSourceConnectionTest` verifie la connexion via `DataSource.getConnection()` en profil `test`
- Test unitaire : `FlywayConfigTest` verifie que la location de migrations est correctement configuree

---

### BACK-03 — Configuration CORS et securite de base

**Epic :** Fondations projet
**Priorite :** Must
**Estimation :** XS (< 1h)
**Dependances :** BACK-01

**Description :**
Configurer CORS pour autoriser le frontend React (port 5173 en dev, domaine de production configurable) a appeler l'API. Aucune authentification n'est requise au MVP, mais les headers de securite basiques doivent etre presents.

**Taches techniques :**
- [ ] Creer `config/CorsConfig.java` implementant `WebMvcConfigurer`
- [ ] Autoriser les origines `http://localhost:5173` et `${app.cors.allowed-origins}` (variable d'environnement)
- [ ] Autoriser les methodes `GET`, `POST`, `PUT`, `DELETE`, `OPTIONS`
- [ ] Autoriser les headers `Content-Type`, `Accept`, `Authorization`
- [ ] Activer `allowCredentials(false)` (pas de session au MVP)
- [ ] Ajouter la propriete `app.cors.allowed-origins` dans `application.yml` avec valeur par defaut vide

**Criteres d'acceptance :**
- CA-1 : Une requete OPTIONS depuis `localhost:5173` recoit le header `Access-Control-Allow-Origin: http://localhost:5173`
- CA-2 : Une requete depuis une origine non autorisee recoit une reponse 403
- CA-3 : La configuration est driven par propriete, pas par valeur hardcodee

**Tests attendus :**
- Test unitaire : `CorsConfigTest` valide les origines et methodes autorisees avec `MockMvc`
- Test integration : requete preflight via `MockMvc` verifie la presence des headers CORS

---

### BACK-04 — Structure des packages DDD

**Epic :** Fondations projet
**Priorite :** Must
**Estimation :** XS (< 1h)
**Dependances :** BACK-01

**Description :**
Creer l'arborescence de packages conforme a l'architecture DDD de MiniAstra. Les packages doivent etre crees avec une classe marqueur ou une interface vide pour que Maven les inclue dans le build. La structure guide toutes les implementations ulterieures.

**Taches techniques :**
- [ ] Creer les packages sous `fr.miniastra` : `domain/model`, `domain/repository`, `domain/valueobject`, `domain/service`
- [ ] Creer les packages : `application/service`, `application/dto`
- [ ] Creer les packages : `infrastructure/persistence/entity`, `infrastructure/persistence/repository`, `infrastructure/persistence/mapper`, `infrastructure/catalog`
- [ ] Creer les packages : `api/controller`, `api/dto/request`, `api/dto/response`, `api/mapper`, `api/exception`
- [ ] Creer le package : `config`
- [ ] Ajouter un `package-info.java` dans chaque package racine documentant la responsabilite de la couche

**Criteres d'acceptance :**
- CA-1 : `mvn compile` reste SUCCESS apres creation des packages
- CA-2 : L'arborescence correspond exactement a la structure definie dans `architecture.md`
- CA-3 : Aucune dependance cyclique entre couches (verifie par ArchUnit dans BACK-05)

**Tests attendus :**
- Test unitaire : `ArchitectureTest` (ArchUnit) verifie que `api/` ne depend pas de `infrastructure/` directement et que `domain/` n'a aucune dependance externe

---

### BACK-05 — Tests d'architecture ArchUnit

**Epic :** Fondations projet
**Priorite :** Should
**Estimation :** S (2h)
**Dependances :** BACK-04

**Description :**
Ajouter ArchUnit comme garde-fou pour garantir le respect des regles d'architecture DDD tout au long du projet. Ces tests echouent immediatement si une couche introduit une dependance illegale.

**Taches techniques :**
- [ ] Ajouter la dependance `archunit-junit5` dans `pom.xml` (scope `test`)
- [ ] Creer `tests/architecture/ArchitectureTest.java`
- [ ] Regle 1 : `domain` n'importe rien de `infrastructure`, `application`, `api`
- [ ] Regle 2 : `application` n'importe rien de `infrastructure`, `api`
- [ ] Regle 3 : `api` n'importe rien de `infrastructure` (sauf via `application`)
- [ ] Regle 4 : les entites JPA (`@Entity`) n'existent que dans `infrastructure.persistence.entity`
- [ ] Regle 5 : les `@RestController` n'existent que dans `api.controller`

**Criteres d'acceptance :**
- CA-1 : `mvn test` execute `ArchitectureTest` et tous les checks passent sur la structure initiale
- CA-2 : L'ajout delibere d'un import illegal dans le domaine fait echouer le test correspondant

**Tests attendus :**
- Test unitaire : chaque regle ArchUnit est un test independant avec message d'erreur explicite

---

## Epic 2 — Migrations Flyway + Seed

---

### BACK-06 — Migration V1 : table `scenario`

**Epic :** Migrations Flyway + Seed
**Priorite :** Must
**Estimation :** XS (< 1h)
**Dependances :** BACK-02

**Description :**
Creer la premiere migration Flyway definissant la table `scenario`, socle de toutes les autres tables. Chaque scenario regroupe l'ensemble des objets ferroviaires d'une simulation.

**Taches techniques :**
- [ ] Creer `src/main/resources/db/migration/V1__create_scenario.sql`
- [ ] Colonnes : `id` (UUID, PK, default gen_random_uuid()), `name` (VARCHAR 255, NOT NULL), `description` (TEXT), `created_at` (TIMESTAMPTZ, NOT NULL, default now()), `updated_at` (TIMESTAMPTZ, NOT NULL, default now())
- [ ] Ajouter une contrainte UNIQUE sur `name`
- [ ] Creer un index sur `created_at DESC` pour le tri par defaut

**Criteres d'acceptance :**
- CA-1 : Flyway applique V1 sans erreur sur une base vide
- CA-2 : La table existe avec les bonnes colonnes et contraintes (`\d scenario` en psql)
- CA-3 : L'insertion d'un scenario avec le meme nom echoue avec violation de contrainte unique

**Tests attendus :**
- Test integration : `ScenarioMigrationTest` (Testcontainers) verifie la creation de la table et l'unicite du nom

---

### BACK-07 — Migration V2 : table `track_segment`

**Epic :** Migrations Flyway + Seed
**Priorite :** Must
**Estimation :** XS (< 1h)
**Dependances :** BACK-06

**Description :**
Creer la table `track_segment` representant les troncons de voie entre deux points geographiques. La table stocke les waypoints de depart et d'arrivee ainsi que les caracteristiques techniques de la voie.

**Taches techniques :**
- [ ] Creer `V2__create_track_segment.sql`
- [ ] Colonnes : `id` (UUID PK), `scenario_id` (UUID FK -> scenario.id ON DELETE CASCADE), `name` (VARCHAR 255 NOT NULL), `start_lat` (DOUBLE PRECISION NOT NULL), `start_lon` (DOUBLE PRECISION NOT NULL), `end_lat` (DOUBLE PRECISION NOT NULL), `end_lon` (DOUBLE PRECISION NOT NULL), `length_m` (DOUBLE PRECISION), `max_speed_kmh` (INTEGER NOT NULL), `electrification` (VARCHAR 50 NOT NULL), `track_count` (SMALLINT NOT NULL DEFAULT 1), `grade_per_mille` (DOUBLE PRECISION NOT NULL DEFAULT 0), `created_at` (TIMESTAMPTZ NOT NULL DEFAULT now())
- [ ] Ajouter un index sur `scenario_id`

**Criteres d'acceptance :**
- CA-1 : Flyway applique V1 + V2 en sequence sans erreur
- CA-2 : La FK `scenario_id` est bien contrainte (INSERT avec UUID inexistant echoue)
- CA-3 : La suppression d'un scenario supprime en cascade ses troncons

**Tests attendus :**
- Test integration : verifie la contrainte FK et le comportement CASCADE

---

### BACK-08 — Migration V3 : table `passenger_train`

**Epic :** Migrations Flyway + Seed
**Priorite :** Must
**Estimation :** XS (< 1h)
**Dependances :** BACK-06

**Description :**
Creer la table `passenger_train` pour les trains de voyageurs, avec leur composition, leur modele et leurs horaires de passage.

**Taches techniques :**
- [ ] Creer `V3__create_passenger_train.sql`
- [ ] Colonnes : `id` (UUID PK), `scenario_id` (UUID FK CASCADE), `name` (VARCHAR 255 NOT NULL), `model` (VARCHAR 100 NOT NULL), `composition` (VARCHAR 100), `capacity` (INTEGER), `max_speed_kmh` (INTEGER NOT NULL), `departure_time` (TIME), `arrival_time` (TIME), `track_segment_id` (UUID FK -> track_segment.id ON DELETE SET NULL), `direction` (VARCHAR 20 NOT NULL), `created_at` (TIMESTAMPTZ NOT NULL DEFAULT now())
- [ ] Index sur `scenario_id`, index sur `track_segment_id`

**Criteres d'acceptance :**
- CA-1 : Flyway applique V1-V3 en sequence sans erreur
- CA-2 : La FK vers `track_segment` accepte NULL (SET NULL au DELETE)

**Tests attendus :**
- Test integration : `PassengerTrainMigrationTest` verifie colonnes et contraintes

---

### BACK-09 — Migration V4 : table `freight_train`

**Epic :** Migrations Flyway + Seed
**Priorite :** Must
**Estimation :** XS (< 1h)
**Dependances :** BACK-06

**Description :**
Creer la table `freight_train` pour les trains de marchandises, avec leur type de cargaison et leur masse.

**Taches techniques :**
- [ ] Creer `V4__create_freight_train.sql`
- [ ] Colonnes : `id` (UUID PK), `scenario_id` (UUID FK CASCADE), `name` (VARCHAR 255 NOT NULL), `model` (VARCHAR 100 NOT NULL), `cargo_type` (VARCHAR 50 NOT NULL), `tonnage` (DOUBLE PRECISION), `max_speed_kmh` (INTEGER NOT NULL), `departure_time` (TIME), `arrival_time` (TIME), `track_segment_id` (UUID FK -> track_segment.id ON DELETE SET NULL), `direction` (VARCHAR 20 NOT NULL), `wagon_count` (INTEGER), `created_at` (TIMESTAMPTZ NOT NULL DEFAULT now())
- [ ] Index sur `scenario_id`

**Criteres d'acceptance :**
- CA-1 : Flyway applique V1-V4 sans erreur
- CA-2 : Les valeurs de `cargo_type` ne sont pas contraintes en base (validation en couche application)

**Tests attendus :**
- Test integration : verifie colonnes, FK et index

---

### BACK-10 — Migration V5 : table `obstacle`

**Epic :** Migrations Flyway + Seed
**Priorite :** Must
**Estimation :** XS (< 1h)
**Dependances :** BACK-07

**Description :**
Creer la table `obstacle` representant les obstacles temporels (chantiers, restrictions de vitesse, fermetures planifiees) sur un troncon.

**Taches techniques :**
- [ ] Creer `V5__create_obstacle.sql`
- [ ] Colonnes : `id` (UUID PK), `scenario_id` (UUID FK CASCADE), `name` (VARCHAR 255 NOT NULL), `track_segment_id` (UUID FK -> track_segment.id ON DELETE CASCADE), `obstacle_type` (VARCHAR 50 NOT NULL), `start_datetime` (TIMESTAMPTZ NOT NULL), `end_datetime` (TIMESTAMPTZ NOT NULL), `speed_limit_kmh` (INTEGER), `description` (TEXT), `position_m` (DOUBLE PRECISION), `created_at` (TIMESTAMPTZ NOT NULL DEFAULT now())
- [ ] Contrainte CHECK : `end_datetime > start_datetime`
- [ ] Index sur `scenario_id`, index sur `track_segment_id`

**Criteres d'acceptance :**
- CA-1 : Flyway applique V1-V5 sans erreur
- CA-2 : L'insertion avec `end_datetime <= start_datetime` echoue avec violation de CHECK

**Tests attendus :**
- Test integration : verifie la contrainte CHECK temporelle

---

### BACK-11 — Migration V6 : table `signal`

**Epic :** Migrations Flyway + Seed
**Priorite :** Must
**Estimation :** XS (< 1h)
**Dependances :** BACK-07

**Description :**
Creer la table `signal` representant les signaux ferroviaires (semaphore, carre, avertissement, etc.) positionnes sur les troncons.

**Taches techniques :**
- [ ] Creer `V6__create_signal.sql`
- [ ] Colonnes : `id` (UUID PK), `scenario_id` (UUID FK CASCADE), `name` (VARCHAR 255 NOT NULL), `track_segment_id` (UUID FK -> track_segment.id ON DELETE CASCADE), `signal_type` (VARCHAR 50 NOT NULL), `position_m` (DOUBLE PRECISION NOT NULL), `direction` (VARCHAR 20 NOT NULL), `is_active` (BOOLEAN NOT NULL DEFAULT true), `created_at` (TIMESTAMPTZ NOT NULL DEFAULT now())
- [ ] Index sur `scenario_id`, index sur `track_segment_id`
- [ ] Contrainte CHECK : `position_m >= 0`

**Criteres d'acceptance :**
- CA-1 : Flyway applique V1-V6 sans erreur
- CA-2 : Une position negative echoue avec violation de CHECK

**Tests attendus :**
- Test integration : verifie la contrainte de position et les FK

---

### BACK-12 — Migration V7 : seed "Corridor Paris-Lyon"

**Epic :** Migrations Flyway + Seed
**Priorite :** Must
**Estimation :** S (2h)
**Dependances :** BACK-07, BACK-08, BACK-09, BACK-10, BACK-11

**Description :**
Inserer un jeu de donnees de demonstration realiste representant le corridor Paris-Lyon avec 10 objets par type (10 troncons, 10 trains passagers, 10 trains marchandises, 10 obstacles, 10 signaux). Ces donnees servent de base pour les tests manuels et la demonstration produit.

**Taches techniques :**
- [ ] Creer `V7__seed_paris_lyon.sql`
- [ ] Inserer 1 scenario : `id` fixe (UUID deterministe pour la reproductibilite), `name = 'Corridor Paris-Lyon'`
- [ ] Inserer 10 troncons couvrant les segments Paris-Melun, Melun-Sens, Sens-Dijon, etc. avec coordonnees GPS realistes
- [ ] Inserer 10 trains passagers (TGV, Intercites) avec horaires distribues sur la journee
- [ ] Inserer 10 trains marchandises (fret intermodal, chimique) avec tonnages varies
- [ ] Inserer 10 obstacles (chantiers, ralentissements) sur des plages temporelles non chevauchantes
- [ ] Inserer 10 signaux repartis sur les troncons avec types varies (SEMAPHORE, CARRE, AVERTISSEMENT, DISQUE, FEU_VERT)
- [ ] Utiliser des UUID fixes (v4 generes une fois) pour la reproductibilite du seed

**Criteres d'acceptance :**
- CA-1 : Flyway applique V7 sans erreur apres V1-V6
- CA-2 : `SELECT count(*) FROM passenger_train WHERE scenario_id = '<seed-uuid>'` retourne 10
- CA-3 : Le seed est idempotent si on relance Flyway (baseline-on-migrate gere le cas)

**Tests attendus :**
- Test integration : `SeedDataTest` verifie les 6 comptages (1 scenario + 5 x 10 objets) via `JdbcTemplate`

---

## Epic 3 — Domaine (entites + repositories)

---

### BACK-13 — Enums du domaine

**Epic :** Domaine
**Priorite :** Must
**Estimation :** XS (< 1h)
**Dependances :** BACK-04

**Description :**
Definir tous les enums du domaine metier. Les enums centralisent les valeurs acceptees et sont reutilises par les entites JPA, les DTOs et la validation.

**Taches techniques :**
- [ ] `domain/model/Electrification.java` : `NONE`, `AC_25KV`, `DC_1500V`, `DC_3000V`
- [ ] `domain/model/Direction.java` : `PARIS_LYON`, `LYON_PARIS`, `BOTH`
- [ ] `domain/model/CargoType.java` : `INTERMODAL`, `BULK`, `CHEMICAL`, `AUTOMOTIVE`, `REFRIGERATED`, `GENERAL`
- [ ] `domain/model/SignalType.java` : `SEMAPHORE`, `CARRE`, `AVERTISSEMENT`, `DISQUE`, `FEU_VERT`, `PANNEAU_VITESSE`
- [ ] `domain/model/ObstacleType.java` : `CHANTIER`, `RALENTISSEMENT`, `FERMETURE`, `RESTRICTION_VITESSE`, `TRAVAUX_VOIE`
- [ ] `domain/model/TrainModel.java` : valeurs pour TGV (DUPLEX, RESEAU, EURODUPLEX), TER, Intercites, Class66 (fret), Prima (fret)
- [ ] Chaque enum implementera `toString()` retournant un libelle lisible

**Criteres d'acceptance :**
- CA-1 : Chaque enum compile sans erreur
- CA-2 : Aucun enum ne reference de classes hors du package `domain`

**Tests attendus :**
- Test unitaire : `EnumTest` verifie que `toString()` retourne une valeur non vide pour chaque constante de chaque enum

---

### BACK-14 — Value Objects du domaine

**Epic :** Domaine
**Priorite :** Must
**Estimation :** S (2h)
**Dependances :** BACK-13

**Description :**
Implementer les Value Objects qui encapsulent des concepts metier composes. Ces objets sont immuables, validables et independants de toute infrastructure.

**Taches techniques :**
- [ ] `domain/valueobject/Waypoint.java` : record avec `latitude` (double) et `longitude` (double), validation `-90 <= lat <= 90`, `-180 <= lon <= 180`
- [ ] `domain/valueobject/TimeWindow.java` : record avec `startDateTime` (LocalDateTime) et `endDateTime` (LocalDateTime), validation `end > start`
- [ ] `domain/valueobject/ValidationResult.java` : classe immuable avec `List<String> errors`, methodes `isValid()`, `merge(ValidationResult)`, factory `success()` et `failure(String...)`
- [ ] `domain/valueobject/DistanceMeters.java` : record wrappant un `double`, validation `>= 0`, methode `fromCoordinates(Waypoint, Waypoint)` calculant la distance Haversine
- [ ] Tous les records sont `@Value`-immutables (pas de setters)

**Criteres d'acceptance :**
- CA-1 : `Waypoint` avec lat=91 leve une `IllegalArgumentException`
- CA-2 : `TimeWindow` avec end <= start leve une `IllegalArgumentException`
- CA-3 : `DistanceMeters.fromCoordinates` retourne ~392km entre Paris (48.85, 2.35) et Lyon (45.75, 4.85) avec une tolerance de 5km
- CA-4 : `ValidationResult.merge` combine correctement deux resultats partiels

**Tests attendus :**
- Test unitaire : `WaypointTest`, `TimeWindowTest`, `DistanceMetersTest`, `ValidationResultTest` avec cas nominaux et cas limites

---

### BACK-15 — Entite JPA `Scenario`

**Epic :** Domaine
**Priorite :** Must
**Estimation :** XS (< 1h)
**Dependances :** BACK-04, BACK-06

**Description :**
Implementer l'entite JPA `Scenario` mappant la table cree en V1. C'est la racine d'agregat principale.

**Taches techniques :**
- [ ] `infrastructure/persistence/entity/ScenarioEntity.java` avec `@Entity`, `@Table(name="scenario")`
- [ ] Champs : `UUID id` (`@Id`, `@GeneratedValue`), `String name`, `String description`, `Instant createdAt`, `Instant updatedAt`
- [ ] `@PrePersist` / `@PreUpdate` pour gerer `createdAt` / `updatedAt` automatiquement
- [ ] Utiliser `@Column(nullable=false)` sur les champs NOT NULL
- [ ] Ajouter `@EqualsAndHashCode(of="id")` via Lombok

**Criteres d'acceptance :**
- CA-1 : `ScenarioEntity` persiste et se relit sans erreur via `ScenarioRepository`
- CA-2 : `updatedAt` est mis a jour automatiquement lors d'un `save` sur une entite existante

**Tests attendus :**
- Test integration : `ScenarioEntityTest` (Testcontainers + `@DataJpaTest`) verifie persist, update et fetch

---

### BACK-16 — Entite JPA `TrackSegmentEntity`

**Epic :** Domaine
**Priorite :** Must
**Estimation :** S (2h)
**Dependances :** BACK-15, BACK-07

**Description :**
Implementer l'entite JPA `TrackSegmentEntity` mappant la table `track_segment` avec la relation vers `ScenarioEntity`.

**Taches techniques :**
- [ ] `infrastructure/persistence/entity/TrackSegmentEntity.java`
- [ ] Champs : tous les champs de V2, `electrification` mappe vers l'enum `Electrification` via `@Enumerated(EnumType.STRING)`
- [ ] Relation `@ManyToOne(fetch=FetchType.LAZY)` vers `ScenarioEntity`, `@JoinColumn(name="scenario_id", nullable=false)`
- [ ] Utiliser Lombok `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`

**Criteres d'acceptance :**
- CA-1 : Persist + fetch via `@DataJpaTest`
- CA-2 : `FetchType.LAZY` verifie : acces a `scenario` hors transaction leve `LazyInitializationException`

**Tests attendus :**
- Test integration : `TrackSegmentEntityTest` verifie persist, relation lazy et suppression cascade depuis scenario

---

### BACK-17 — Entites JPA trains, obstacle, signal

**Epic :** Domaine
**Priorite :** Must
**Estimation :** M (4h)
**Dependances :** BACK-16

**Description :**
Implementer les 4 entites JPA restantes : `PassengerTrainEntity`, `FreightTrainEntity`, `ObstacleEntity`, `SignalEntity`. Chacune a une FK vers `scenario` et une FK optionnelle vers `track_segment`.

**Taches techniques :**
- [ ] `PassengerTrainEntity` : champs de V3, `direction` en `@Enumerated STRING`, FK lazy vers `ScenarioEntity` et `TrackSegmentEntity` (nullable)
- [ ] `FreightTrainEntity` : champs de V4, `cargoType` en `@Enumerated STRING`, `direction` en `@Enumerated STRING`, FKs lazy
- [ ] `ObstacleEntity` : champs de V5, `obstacleType` en `@Enumerated STRING`, FK non-null vers `TrackSegmentEntity`
- [ ] `SignalEntity` : champs de V6, `signalType` et `direction` en `@Enumerated STRING`, FK non-null vers `TrackSegmentEntity`
- [ ] Toutes les entites : `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@EqualsAndHashCode(of="id")`

**Criteres d'acceptance :**
- CA-1 : Chaque entite persiste et se relit via `@DataJpaTest`
- CA-2 : Suppression d'un `TrackSegmentEntity` met `track_segment_id` a NULL dans `PassengerTrainEntity` et `FreightTrainEntity` (SET NULL)
- CA-3 : Suppression d'un `TrackSegmentEntity` supprime en CASCADE les `ObstacleEntity` et `SignalEntity` associes

**Tests attendus :**
- Test integration : tests `@DataJpaTest` pour chaque entite, + test cascade sur `TrackSegmentEntity`

---

### BACK-18 — Interfaces Repository (ports DDD)

**Epic :** Domaine
**Priorite :** Must
**Estimation :** XS (< 1h)
**Dependances :** BACK-14, BACK-13

**Description :**
Definir les interfaces de repository dans la couche `domain` (ports DDD). Ces interfaces n'ont aucune dependance sur Spring Data et sont implementees dans `infrastructure`.

**Taches techniques :**
- [ ] `domain/repository/ScenarioRepository.java` : `findAll()`, `findById(UUID)`, `save(Scenario)`, `delete(UUID)`, `existsByName(String)`
- [ ] `domain/repository/TrackSegmentRepository.java` : CRUD + `findAllByScenarioId(UUID)`
- [ ] `domain/repository/PassengerTrainRepository.java` : CRUD + `findAllByScenarioId(UUID)`
- [ ] `domain/repository/FreightTrainRepository.java` : CRUD + `findAllByScenarioId(UUID)`
- [ ] `domain/repository/ObstacleRepository.java` : CRUD + `findAllByScenarioId(UUID)` + `findAllByTrackSegmentId(UUID)`
- [ ] `domain/repository/SignalRepository.java` : CRUD + `findAllByScenarioId(UUID)` + `findAllByTrackSegmentId(UUID)`
- [ ] Les interfaces travaillent avec des objets du package `domain/model`, pas avec les entites JPA

**Criteres d'acceptance :**
- CA-1 : Les interfaces ne contiennent aucune annotation Spring
- CA-2 : ArchUnit valide que `domain/repository` ne depend pas de `infrastructure` (BACK-05)

**Tests attendus :**
- Test unitaire : `RepositoryContractTest` verifie via reflection que chaque interface declare au minimum `findById`, `save`, `delete`

---

### BACK-19 — Modeles domaine (POJO)

**Epic :** Domaine
**Priorite :** Must
**Estimation :** M (4h)
**Dependances :** BACK-13, BACK-14

**Description :**
Implementer les POJO (Plain Old Java Objects) du domaine pour les 6 agregats. Ces objets sont immutables, distincts des entites JPA, et representent le modele riche du domaine.

**Taches techniques :**
- [ ] `domain/model/Scenario.java` : record avec `UUID id`, `String name`, `String description`, `Instant createdAt`, `Instant updatedAt`
- [ ] `domain/model/TrackSegment.java` : record avec tous les champs metier, `Waypoint start`, `Waypoint end`, `Electrification electrification`
- [ ] `domain/model/PassengerTrain.java` : record avec champs metier, `Direction direction`, `TrainModel model`, `UUID trackSegmentId` (nullable)
- [ ] `domain/model/FreightTrain.java` : record avec champs metier, `CargoType cargoType`, `Direction direction`, `UUID trackSegmentId` (nullable)
- [ ] `domain/model/Obstacle.java` : record avec `TimeWindow timeWindow`, `ObstacleType obstacleType`, `UUID trackSegmentId`
- [ ] `domain/model/Signal.java` : record avec `SignalType signalType`, `Direction direction`, `double positionM`, `UUID trackSegmentId`

**Criteres d'acceptance :**
- CA-1 : Aucune annotation Spring ou JPA dans ces classes
- CA-2 : Les records sont immutables (pas de setters, pas de champs mutables)
- CA-3 : Les tests ArchUnit (BACK-05) passent

**Tests attendus :**
- Test unitaire : `DomainModelTest` instancie chaque record et verifie equality, toString et immutabilite

---

### BACK-20 — Catalogue materiel roulant en memoire

**Epic :** Domaine
**Priorite :** Should
**Estimation :** S (2h)
**Dependances :** BACK-13

**Description :**
Implementer `InMemoryTrainCatalog` qui expose la liste des modeles de materiel roulant disponibles. Ce catalogue est charge une fois au demarrage et ne persiste pas en base.

**Taches techniques :**
- [ ] `infrastructure/catalog/TrainCatalogEntry.java` : record avec `String code`, `String label`, `int maxSpeedKmh`, `boolean isPassenger`, `String manufacturer`
- [ ] `infrastructure/catalog/InMemoryTrainCatalog.java` : classe `@Component` initialisant une `List<TrainCatalogEntry>` immuable au constructeur
- [ ] Entrees passagers : TGV Duplex (320 km/h), TGV Reseau (300 km/h), TGV Euroduplex (320 km/h), Intercites BB26000 (200 km/h), TER Regiolis (200 km/h)
- [ ] Entrees fret : Prima II BB27000 (140 km/h), Class 66 (120 km/h), G2000 BB (120 km/h)
- [ ] Methodes : `findAll()`, `findByCode(String)`, `findPassengerModels()`, `findFreightModels()`
- [ ] Interface `domain/service/TrainCatalog.java` dans le domaine (implementation dans `infrastructure`)

**Criteres d'acceptance :**
- CA-1 : `findAll()` retourne au minimum 8 entrees
- CA-2 : `findByCode("TGV_DUPLEX")` retourne le TGV Duplex avec maxSpeed=320
- CA-3 : La liste retournee par `findAll()` est non-modifiable (leve `UnsupportedOperationException` si on tente add)

**Tests attendus :**
- Test unitaire : `InMemoryTrainCatalogTest` couvre `findAll`, `findByCode` (trouve + non-trouve), `findPassengerModels`, immutabilite

---

### BACK-21 — Implementations Spring Data JPA des repositories

**Epic :** Domaine
**Priorite :** Must
**Estimation :** M (4h)
**Dependances :** BACK-17, BACK-18, BACK-19

**Description :**
Implementer les adaptateurs infrastructure qui implementent les interfaces de repository du domaine en s'appuyant sur Spring Data JPA.

**Taches techniques :**
- [ ] Creer les interfaces Spring Data : `JpaScenarioRepository extends JpaRepository<ScenarioEntity, UUID>`
- [ ] Idem pour les 5 autres entites avec les methodes de requete necesaires (`findAllByScenarioId`, etc.)
- [ ] Creer les adaptateurs (adapter pattern) : `ScenarioRepositoryAdapter implements ScenarioRepository` qui deleguent au JPA repository et utilisent les mappers d'entite
- [ ] Idem pour `TrackSegmentRepositoryAdapter`, `PassengerTrainRepositoryAdapter`, `FreightTrainRepositoryAdapter`, `ObstacleRepositoryAdapter`, `SignalRepositoryAdapter`
- [ ] Creer les mappers entite<->domaine : `ScenarioEntityMapper`, etc. (classes utilitaires simples, sans MapStruct a ce stade)
- [ ] Annoter les adaptateurs avec `@Repository`

**Criteres d'acceptance :**
- CA-1 : Chaque adaptateur passe un test `@DataJpaTest` verifiant save + findById + delete
- CA-2 : `findAllByScenarioId` retourne bien les objets appartenant au bon scenario
- CA-3 : Les adaptateurs ne levent pas d'exception sur `findById` d'un UUID inexistant (retournent `Optional.empty()`)

**Tests attendus :**
- Test integration : `@DataJpaTest` avec Testcontainers pour chaque adaptateur (6 tests)

---

## Epic 4 — Services applicatifs

---

### BACK-22 — `ScenarioAppService` (CRUD)

**Epic :** Services applicatifs
**Priorite :** Must
**Estimation :** S (2h)
**Dependances :** BACK-21

**Description :**
Implementer le service applicatif orchestrant les operations CRUD sur les scenarios. C'est le point d'entree unique pour toute manipulation de scenario depuis l'API.

**Taches techniques :**
- [ ] `application/service/ScenarioAppService.java` annote `@Service`, `@Transactional`
- [ ] Methode `createScenario(String name, String description)` : verifie unicite du nom, cree et persiste
- [ ] Methode `getScenario(UUID id)` : leve `ScenarioNotFoundException` si absent
- [ ] Methode `listScenarios()` : retourne `List<Scenario>` triee par `createdAt` DESC
- [ ] Methode `updateScenario(UUID id, String name, String description)` : verifie unicite du nouveau nom si change
- [ ] Methode `deleteScenario(UUID id)` : supprime en cascade (gerée par FK), leve exception si absent
- [ ] Creer `domain/exception/ScenarioNotFoundException.java` (extends `RuntimeException`)
- [ ] Creer `domain/exception/DuplicateNameException.java`

**Criteres d'acceptance :**
- CA-1 : Creation avec nom duplique leve `DuplicateNameException`
- CA-2 : `getScenario` sur UUID inconnu leve `ScenarioNotFoundException`
- CA-3 : `listScenarios` retourne les scenarios dans l'ordre `createdAt` DESC

**Tests attendus :**
- Test unitaire : `ScenarioAppServiceTest` avec mocks des repositories (Mockito)
- Test integration : `ScenarioAppServiceIntegrationTest` avec Testcontainers + vraie base

---

### BACK-23 — `TrackSegmentAppService` (CRUD + calcul `length_m`)

**Epic :** Services applicatifs
**Priorite :** Must
**Estimation :** S (2h)
**Dependances :** BACK-22, BACK-14

**Description :**
Implementer le service applicatif pour les troncons. La creation et la mise a jour calculent automatiquement `length_m` via Haversine a partir des coordonnees GPS des waypoints.

**Taches techniques :**
- [ ] `application/service/TrackSegmentAppService.java`
- [ ] Methode `createTrackSegment(UUID scenarioId, CreateTrackSegmentCommand)` : verifie existence scenario, calcule `length_m` via `DistanceMeters.fromCoordinates`, persiste
- [ ] Methode `getTrackSegment(UUID id)` : leve `TrackSegmentNotFoundException` si absent
- [ ] Methode `listByScenario(UUID scenarioId)` : verifie existence scenario, retourne liste
- [ ] Methode `updateTrackSegment(UUID id, UpdateTrackSegmentCommand)` : recalcule `length_m` si coordonnees changent
- [ ] Methode `deleteTrackSegment(UUID id)`
- [ ] Creer `domain/exception/TrackSegmentNotFoundException.java`

**Criteres d'acceptance :**
- CA-1 : Creation d'un troncon Paris-Lyon calcule `length_m` dans la plage [380000, 410000] metres
- CA-2 : Modification des coordonnees recalcule bien `length_m`
- CA-3 : `listByScenario` leve `ScenarioNotFoundException` si le scenario n'existe pas

**Tests attendus :**
- Test unitaire : `TrackSegmentAppServiceTest` avec Mockito, cas nominal + calcul Haversine
- Test integration : verification du `length_m` calcule en base

---

### BACK-24 — `PassengerTrainAppService` et `FreightTrainAppService`

**Epic :** Services applicatifs
**Priorite :** Must
**Estimation :** M (4h)
**Dependances :** BACK-23

**Description :**
Implementer les deux services applicatifs pour les trains passagers et marchandises. Les deux partagent la meme logique CRUD avec des specificites metier propres.

**Taches techniques :**
- [ ] `PassengerTrainAppService` : CRUD complet, verifie existence scenario et troncon (si fourni), leve `PassengerTrainNotFoundException`
- [ ] `FreightTrainAppService` : CRUD complet, idem, leve `FreightTrainNotFoundException`
- [ ] Validation dans chaque service : `maxSpeedKmh > 0`, `departureTime < arrivalTime` si les deux sont fournis
- [ ] Methode `listByScenario(UUID scenarioId)` dans chaque service
- [ ] Methode `listByTrackSegment(UUID trackSegmentId)` dans chaque service
- [ ] Creer les classes d'exception manquantes dans `domain/exception/`

**Criteres d'acceptance :**
- CA-1 : Creation d'un train avec `maxSpeedKmh=0` leve `IllegalArgumentException`
- CA-2 : Affectation a un troncon inexistant leve `TrackSegmentNotFoundException`
- CA-3 : `listByTrackSegment` retourne tous les trains affectes a ce troncon

**Tests attendus :**
- Test unitaire : `PassengerTrainAppServiceTest` et `FreightTrainAppServiceTest` avec Mockito (validation + cas nominaux)
- Test integration : test de persistance et de requete

---

### BACK-25 — `ObstacleAppService` et `SignalAppService`

**Epic :** Services applicatifs
**Priorite :** Must
**Estimation :** M (4h)
**Dependances :** BACK-23

**Description :**
Implementer les services applicatifs pour les obstacles et signaux. Un obstacle necessite une validation temporelle stricte, un signal necessite la validation de sa position par rapport a la longueur du troncon.

**Taches techniques :**
- [ ] `ObstacleAppService` : CRUD, validation `TimeWindow` (end > start), verifie troncon obligatoire, leve `ObstacleNotFoundException`
- [ ] `SignalAppService` : CRUD, validation `positionM >= 0` et `positionM <= troncon.lengthM` si `lengthM` est defini, leve `SignalNotFoundException`
- [ ] Methode `findActiveObstaclesAt(UUID scenarioId, LocalDateTime dateTime)` dans `ObstacleAppService`
- [ ] Methode `listByTrackSegment(UUID trackSegmentId)` dans chaque service

**Criteres d'acceptance :**
- CA-1 : Obstacle avec `end <= start` leve `IllegalArgumentException`
- CA-2 : Signal a une position depassant la longueur du troncon leve `IllegalArgumentException`
- CA-3 : `findActiveObstaclesAt` retourne uniquement les obstacles dont la fenetre temporelle couvre la date donnee

**Tests attendus :**
- Test unitaire : `ObstacleAppServiceTest` et `SignalAppServiceTest` avec Mockito
- Test integration : `findActiveObstaclesAt` avec des obstacles chevauchants et non chevauchants

---

### BACK-26 — `ScenarioValidationService` (regles RV + RW)

**Epic :** Services applicatifs
**Priorite :** Must
**Estimation :** L (1j)
**Dependances :** BACK-24, BACK-25

**Description :**
Implementer le service de validation d'un scenario complet contre les regles ferroviaires definies dans les specs. Chaque regle retourne un `ValidationResult` qui est merge en un resultat global.

**Taches techniques :**
- [ ] `application/service/ScenarioValidationService.java`
- [ ] Regles RV (voie) :
  - RV-001 : Chaque troncon doit avoir `max_speed_kmh > 0`
  - RV-002 : Pas de chevauchement temporel de deux obstacles sur le meme troncon
  - RV-003 : Les signaux doivent etre dans les bornes du troncon (`0 <= positionM <= lengthM`)
  - RV-004 : Chaque scenario doit avoir au moins 1 troncon
  - RV-005 : Les coordonnees des waypoints doivent etre dans les bornes France metropolitaine (lat 41-52, lon -6 a 10)
- [ ] Regles RW (materiel roulant) :
  - RW-001 : La vitesse max d'un train ne peut pas depasser la vitesse max du troncon affecte
  - RW-002 : Un train fret de type CHEMICAL doit avoir `tonnage > 0`
  - RW-003 : Les horaires de depart/arrivee doivent etre coherents (depart < arrivee)
- [ ] Regles supplementaires (RV-006 a RV-009 selon specs) a implementer selon `specs.md`
- [ ] Methode principale : `validate(UUID scenarioId)` retournant `ValidationResult`

**Criteres d'acceptance :**
- CA-1 : Un scenario vide (sans troncon) echoue RV-004
- CA-2 : Deux obstacles qui se chevauchent sur le meme troncon echouent RV-002
- CA-3 : Un train dont la vitesse max > vitesse du troncon echoue RW-001
- CA-4 : Un scenario "Corridor Paris-Lyon" seed (BACK-12) passe toutes les regles

**Tests attendus :**
- Test unitaire : `ScenarioValidationServiceTest` avec un scenario construit en memoire pour chaque regle (1 test par regle)
- Test integration : validation du scenario seed

---

## Epic 5 — API REST

---

### BACK-27 — DTOs request/response et mappers MapStruct pour `Scenario`

**Epic :** API REST
**Priorite :** Must
**Estimation :** S (2h)
**Dependances :** BACK-22

**Description :**
Creer les DTOs de requete et de reponse pour l'API Scenario ainsi que les mappers MapStruct correspondants.

**Taches techniques :**
- [ ] `api/dto/request/CreateScenarioRequest.java` : `@NotBlank String name`, `String description`
- [ ] `api/dto/request/UpdateScenarioRequest.java` : idem
- [ ] `api/dto/response/ScenarioResponse.java` : tous les champs + `createdAt` en ISO-8601
- [ ] `api/mapper/ScenarioApiMapper.java` : interface `@Mapper(componentModel="spring")` avec `toResponse(Scenario)` et `toResponseList(List<Scenario>)`
- [ ] Validation Bean Validation sur les requests (`@Valid`, `@NotBlank`, `@Size`)

**Criteres d'acceptance :**
- CA-1 : `ScenarioApiMapper` genere une implementation sans erreur de compilation MapStruct
- CA-2 : `CreateScenarioRequest` avec `name=null` echoue la validation Bean Validation
- CA-3 : `ScenarioResponse` serialise correctement `createdAt` en format ISO-8601

**Tests attendus :**
- Test unitaire : `ScenarioApiMapperTest` verifie le mapping complet (aucun champ null inattendu)

---

### BACK-28 — DTOs et mappers pour les 5 types d'objets

**Epic :** API REST
**Priorite :** Must
**Estimation :** L (1j)
**Dependances :** BACK-24, BACK-25

**Description :**
Creer l'ensemble des DTOs et mappers MapStruct pour les 5 types d'objets metier. Ce ticket suit le meme patron que BACK-27 applique aux 5 types.

**Taches techniques :**
- [ ] DTOs `CreateTrackSegmentRequest`, `UpdateTrackSegmentRequest`, `TrackSegmentResponse`
- [ ] DTOs `CreatePassengerTrainRequest`, `UpdatePassengerTrainRequest`, `PassengerTrainResponse`
- [ ] DTOs `CreateFreightTrainRequest`, `UpdateFreightTrainRequest`, `FreightTrainResponse`
- [ ] DTOs `CreateObstacleRequest`, `UpdateObstacleRequest`, `ObstacleResponse`
- [ ] DTOs `CreateSignalRequest`, `UpdateSignalRequest`, `SignalResponse`
- [ ] Mappers MapStruct pour chacun (interface `@Mapper(componentModel="spring")`)
- [ ] Validations Bean Validation sur tous les champs obligatoires

**Criteres d'acceptance :**
- CA-1 : `mvn compile` genere toutes les implementations MapStruct sans erreur
- CA-2 : Les enums sont correctement mappes de String (JSON) vers enum Java
- CA-3 : Les champs `@NotNull` valides refusent `null` avec message explicite

**Tests attendus :**
- Test unitaire : 1 test de mapping par type, verifiant les champs cles et les conversions d'enum

---

### BACK-29 — `ScenarioController`

**Epic :** API REST
**Priorite :** Must
**Estimation :** S (2h)
**Dependances :** BACK-27, BACK-22

**Description :**
Implementer le controller REST pour les scenarios. Il expose les 5 operations CRUD standard sur `/api/v1/scenarios`.

**Taches techniques :**
- [ ] `api/controller/ScenarioController.java` annote `@RestController`, `@RequestMapping("/api/v1/scenarios")`
- [ ] `GET /` -> `listScenarios()` -> 200 + `List<ScenarioResponse>`
- [ ] `GET /{id}` -> `getScenario(UUID)` -> 200 + `ScenarioResponse`
- [ ] `POST /` -> `createScenario(@Valid @RequestBody)` -> 201 + `ScenarioResponse`
- [ ] `PUT /{id}` -> `updateScenario(UUID, @Valid @RequestBody)` -> 200 + `ScenarioResponse`
- [ ] `DELETE /{id}` -> `deleteScenario(UUID)` -> 204
- [ ] Ajouter `@Tag(name="Scenarios")` pour la documentation OpenAPI

**Criteres d'acceptance :**
- CA-1 : `POST /api/v1/scenarios` avec body valide retourne HTTP 201 et le scenario cree
- CA-2 : `DELETE /api/v1/scenarios/{id}` retourne HTTP 204
- CA-3 : `GET /api/v1/scenarios/{id}` sur UUID inexistant retourne HTTP 404 (gere par ExceptionHandler)

**Tests attendus :**
- Test unitaire : `ScenarioControllerTest` avec `@WebMvcTest` et mock du service (5 scenarios nominaux)
- Test integration : `ScenarioControllerIntegrationTest` avec `@SpringBootTest` + Testcontainers

---

### BACK-30 — Controllers pour les 5 types d'objets

**Epic :** API REST
**Priorite :** Must
**Estimation :** L (1j)
**Dependances :** BACK-28, BACK-24, BACK-25, BACK-29

**Description :**
Implementer les 5 controllers REST pour les objets ferroviaires. Tous suivent le meme patron que `ScenarioController` avec une imbrication sous `/api/v1/scenarios/{scenarioId}/...`.

**Taches techniques :**
- [ ] `TrackSegmentController` : `/api/v1/scenarios/{scenarioId}/track-segments` (CRUD)
- [ ] `PassengerTrainController` : `/api/v1/scenarios/{scenarioId}/passenger-trains` (CRUD)
- [ ] `FreightTrainController` : `/api/v1/scenarios/{scenarioId}/freight-trains` (CRUD)
- [ ] `ObstacleController` : `/api/v1/scenarios/{scenarioId}/obstacles` (CRUD + `GET /active?at=datetime`)
- [ ] `SignalController` : `/api/v1/scenarios/{scenarioId}/signals` (CRUD)
- [ ] Chaque controller : GET list, GET by id, POST, PUT, DELETE
- [ ] `@Tag` OpenAPI sur chaque controller

**Criteres d'acceptance :**
- CA-1 : `GET /api/v1/scenarios/{id}/track-segments` retourne 404 si le scenario n'existe pas
- CA-2 : `POST /api/v1/scenarios/{id}/obstacles` cree l'obstacle lie au scenario
- CA-3 : `GET /api/v1/scenarios/{id}/obstacles/active?at=2024-06-01T10:00:00Z` retourne uniquement les obstacles actifs

**Tests attendus :**
- Test unitaire : `@WebMvcTest` pour chaque controller (operations nominales)
- Test integration : 1 test `@SpringBootTest` par controller verifiant le cas 404 scenario inexistant

---

### BACK-31 — `CatalogController` (lecture seule)

**Epic :** API REST
**Priorite :** Should
**Estimation :** XS (< 1h)
**Dependances :** BACK-20

**Description :**
Exposer le catalogue de materiel roulant en lecture seule via un endpoint REST. Ce controller ne necessite pas de service applicatif intermediaire.

**Taches techniques :**
- [ ] `api/controller/CatalogController.java`
- [ ] `GET /api/v1/catalog/trains` -> liste complete des `TrainCatalogEntry`
- [ ] `GET /api/v1/catalog/trains/{code}` -> entree specifique, 404 si absent
- [ ] `GET /api/v1/catalog/trains?passenger=true` -> filtre par type
- [ ] `api/dto/response/TrainCatalogResponse.java` (record)
- [ ] `@Tag(name="Catalog")` OpenAPI

**Criteres d'acceptance :**
- CA-1 : `GET /api/v1/catalog/trains` retourne au moins 8 entrees
- CA-2 : `GET /api/v1/catalog/trains?passenger=true` retourne uniquement les modeles voyageurs
- CA-3 : `GET /api/v1/catalog/trains/UNKNOWN` retourne 404

**Tests attendus :**
- Test unitaire : `CatalogControllerTest` avec `@WebMvcTest` (filtre, liste, not-found)

---

### BACK-32 — `ValidationController`

**Epic :** API REST
**Priorite :** Must
**Estimation :** S (2h)
**Dependances :** BACK-26, BACK-29

**Description :**
Exposer le service de validation d'un scenario via un endpoint REST dedie.

**Taches techniques :**
- [ ] `api/controller/ValidationController.java`
- [ ] `POST /api/v1/scenarios/{id}/validate` -> declenche `ScenarioValidationService.validate(id)` -> retourne `ValidationResponse`
- [ ] `api/dto/response/ValidationResponse.java` : `boolean valid`, `List<String> errors`, `int checkedRulesCount`
- [ ] Si le scenario n'existe pas : 404
- [ ] Si valide : 200 + `{ valid: true, errors: [], checkedRulesCount: 9 }`
- [ ] Si invalide : 200 + `{ valid: false, errors: ["RV-001: ...", ...] }` (pas un 4xx car c'est une reponse metier)

**Criteres d'acceptance :**
- CA-1 : Validation du scenario seed retourne `valid: true`
- CA-2 : Validation d'un scenario vide retourne `valid: false` avec l'erreur RV-004
- CA-3 : Le statut HTTP est toujours 200 pour une validation (meme si le scenario est invalide)

**Tests attendus :**
- Test unitaire : `ValidationControllerTest` avec `@WebMvcTest` + mock du service (cas valide + invalide)
- Test integration : validation complete du scenario seed

---

### BACK-33 — `GlobalExceptionHandler`

**Epic :** API REST
**Priorite :** Must
**Estimation :** S (2h)
**Dependances :** BACK-29

**Description :**
Centraliser la gestion des exceptions dans un `@ControllerAdvice`. Chaque type d'exception domaine est mappe sur un code HTTP et un corps d'erreur standardise.

**Taches techniques :**
- [ ] `api/exception/GlobalExceptionHandler.java` annote `@RestControllerAdvice`
- [ ] `api/exception/ErrorResponse.java` : record avec `String message`, `String errorCode`, `Instant timestamp`
- [ ] Mapping : `*NotFoundException` -> 404
- [ ] Mapping : `DuplicateNameException` -> 409
- [ ] Mapping : `IllegalArgumentException` (validation domaine) -> 400
- [ ] Mapping : `MethodArgumentNotValidException` (Bean Validation) -> 400 avec liste des erreurs de champ
- [ ] Mapping : `Exception` (catch-all) -> 500 sans exposer le stacktrace
- [ ] Log systematique des exceptions 5xx avec stacktrace complet

**Criteres d'acceptance :**
- CA-1 : `GET /api/v1/scenarios/{uuid-inexistant}` retourne `{ "errorCode": "NOT_FOUND", "message": "Scenario not found", "timestamp": "..." }` avec HTTP 404
- CA-2 : `POST /api/v1/scenarios` avec body invalide retourne 400 avec liste des champs en erreur
- CA-3 : Les erreurs 5xx ne leakent jamais de stacktrace dans la reponse JSON

**Tests attendus :**
- Test unitaire : `GlobalExceptionHandlerTest` avec `@WebMvcTest` testant chaque mapping d'exception

---

## Epic 6 — Export Excel

---

### BACK-34 — `ExcelExporter` (Apache POI)

**Epic :** Export Excel
**Priorite :** Must
**Estimation :** L (1j)
**Dependances :** BACK-19, BACK-01

**Description :**
Implementer le composant bas niveau `ExcelExporter` qui prend des donnees domaine et produit un `Workbook` Apache POI. Chaque feuille correspond a un type d'objet du scenario.

**Taches techniques :**
- [ ] `infrastructure/export/ExcelExporter.java` : classe `@Component`
- [ ] Methode `exportScenario(Scenario, List<TrackSegment>, List<PassengerTrain>, List<FreightTrain>, List<Obstacle>, List<Signal>)` retournant `XSSFWorkbook`
- [ ] Feuille "Scenario" : informations generales du scenario
- [ ] Feuille "Troncons" : liste des `TrackSegment` avec en-tetes en francais
- [ ] Feuille "Trains Passagers" : liste des `PassengerTrain`
- [ ] Feuille "Trains Marchandises" : liste des `FreightTrain`
- [ ] Feuille "Obstacles" : liste des `Obstacle`
- [ ] Feuille "Signaux" : liste des `Signal`
- [ ] Style : en-tetes en gras avec couleur de fond bleue SNCF (#003189), colonnes auto-dimensionnees (`autoSizeColumn`)
- [ ] Methode privee `createHeaderRow(Sheet, String[])` reutilisable

**Criteres d'acceptance :**
- CA-1 : Le workbook genere contient exactement 6 feuilles avec les noms corrects
- CA-2 : La feuille "Troncons" avec 10 troncons contient 11 lignes (1 en-tete + 10 donnees)
- CA-3 : L'en-tete de chaque feuille est en gras
- CA-4 : Le workbook est lisible par Apache POI sans exception apres generation

**Tests attendus :**
- Test unitaire : `ExcelExporterTest` avec donnees de test construites en memoire, verifie le nombre de feuilles, lignes et le style des en-tetes

---

### BACK-35 — `ExcelExportAppService`

**Epic :** Export Excel
**Priorite :** Must
**Estimation :** S (2h)
**Dependances :** BACK-34, BACK-24, BACK-25

**Description :**
Implementer le service applicatif qui orchestre la collecte des donnees depuis les repositories et deleguent a `ExcelExporter` pour la generation du fichier.

**Taches techniques :**
- [ ] `application/service/ExcelExportAppService.java`
- [ ] Methode `exportScenario(UUID scenarioId)` retournant `byte[]`
- [ ] Collecte le scenario + les 5 listes d'objets via les repositories
- [ ] Deleguent a `ExcelExporter.exportScenario`
- [ ] Convertit le `Workbook` en `byte[]` via `ByteArrayOutputStream`
- [ ] Ferme proprement le workbook apres export (try-with-resources)
- [ ] Leve `ScenarioNotFoundException` si le scenario n'existe pas

**Criteres d'acceptance :**
- CA-1 : `exportScenario` sur le scenario seed retourne un tableau de bytes non vide
- CA-2 : Le byte[] peut etre relu comme un `XSSFWorkbook` valide
- CA-3 : La methode ne laisse pas de ressource Workbook ouverte en cas d'exception

**Tests attendus :**
- Test unitaire : `ExcelExportAppServiceTest` avec Mockito sur les repositories
- Test integration : `ExcelExportAppServiceIntegrationTest` avec Testcontainers, verifie le workbook genere

---

### BACK-36 — `ExportController` (5 endpoints + endpoint global)

**Epic :** Export Excel
**Priorite :** Must
**Estimation :** S (2h)
**Dependances :** BACK-35, BACK-29

**Description :**
Exposer l'export Excel via des endpoints REST retournant le fichier en telechargement. Un endpoint global exporte tout le scenario, et 5 endpoints specifiques exportent chaque type d'objet separement.

**Taches techniques :**
- [ ] `api/controller/ExportController.java`
- [ ] `GET /api/v1/scenarios/{id}/export` -> export complet du scenario (toutes les feuilles)
- [ ] `GET /api/v1/scenarios/{id}/export/track-segments` -> export troncons uniquement
- [ ] `GET /api/v1/scenarios/{id}/export/passenger-trains` -> export trains passagers
- [ ] `GET /api/v1/scenarios/{id}/export/freight-trains` -> export trains marchandises
- [ ] `GET /api/v1/scenarios/{id}/export/obstacles` -> export obstacles
- [ ] `GET /api/v1/scenarios/{id}/export/signals` -> export signaux
- [ ] Content-Type : `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`
- [ ] Header : `Content-Disposition: attachment; filename="miniastra-<nom-scenario>-<type>.xlsx"`
- [ ] Retourner `ResponseEntity<byte[]>` avec HTTP 200

**Criteres d'acceptance :**
- CA-1 : `GET /api/v1/scenarios/{seed-id}/export` retourne HTTP 200 avec Content-Type Excel
- CA-2 : Le header `Content-Disposition` contient le nom du scenario dans le nom de fichier
- CA-3 : `GET /api/v1/scenarios/{inexistant}/export` retourne HTTP 404

**Tests attendus :**
- Test unitaire : `ExportControllerTest` avec `@WebMvcTest` + mock du service, verifie les headers HTTP
- Test integration : `ExportControllerIntegrationTest` telecharge le fichier et verifie qu'il est lisible par Apache POI

---

## Recapitulatif

| Ticket | Titre | Epic | Priorite | Estimation | Dependances |
|--------|-------|------|----------|------------|-------------|
| BACK-01 | Setup Maven + Spring Boot 3.3 | Fondations | Must | S | — |
| BACK-02 | Config PostgreSQL + Flyway | Fondations | Must | S | BACK-01 |
| BACK-03 | Config CORS | Fondations | Must | XS | BACK-01 |
| BACK-04 | Structure packages DDD | Fondations | Must | XS | BACK-01 |
| BACK-05 | Tests ArchUnit | Fondations | Should | S | BACK-04 |
| BACK-06 | V1 : table scenario | Migrations | Must | XS | BACK-02 |
| BACK-07 | V2 : table track_segment | Migrations | Must | XS | BACK-06 |
| BACK-08 | V3 : table passenger_train | Migrations | Must | XS | BACK-06 |
| BACK-09 | V4 : table freight_train | Migrations | Must | XS | BACK-06 |
| BACK-10 | V5 : table obstacle | Migrations | Must | XS | BACK-07 |
| BACK-11 | V6 : table signal | Migrations | Must | XS | BACK-07 |
| BACK-12 | V7 : seed Paris-Lyon | Migrations | Must | S | BACK-07 a BACK-11 |
| BACK-13 | Enums domaine | Domaine | Must | XS | BACK-04 |
| BACK-14 | Value Objects | Domaine | Must | S | BACK-13 |
| BACK-15 | Entite JPA Scenario | Domaine | Must | XS | BACK-04, BACK-06 |
| BACK-16 | Entite JPA TrackSegment | Domaine | Must | S | BACK-15, BACK-07 |
| BACK-17 | Entites JPA trains/obstacle/signal | Domaine | Must | M | BACK-16 |
| BACK-18 | Interfaces Repository (ports) | Domaine | Must | XS | BACK-14, BACK-13 |
| BACK-19 | Modeles domaine POJO | Domaine | Must | M | BACK-13, BACK-14 |
| BACK-20 | Catalogue materiel roulant | Domaine | Should | S | BACK-13 |
| BACK-21 | Implem Spring Data JPA | Domaine | Must | M | BACK-17, BACK-18, BACK-19 |
| BACK-22 | ScenarioAppService | Services | Must | S | BACK-21 |
| BACK-23 | TrackSegmentAppService | Services | Must | S | BACK-22, BACK-14 |
| BACK-24 | PassengerTrain + FreightTrain services | Services | Must | M | BACK-23 |
| BACK-25 | Obstacle + Signal services | Services | Must | M | BACK-23 |
| BACK-26 | ScenarioValidationService | Services | Must | L | BACK-24, BACK-25 |
| BACK-27 | DTOs + mappers Scenario | API REST | Must | S | BACK-22 |
| BACK-28 | DTOs + mappers 5 types | API REST | Must | L | BACK-24, BACK-25 |
| BACK-29 | ScenarioController | API REST | Must | S | BACK-27, BACK-22 |
| BACK-30 | Controllers 5 types | API REST | Must | L | BACK-28, BACK-24, BACK-25 |
| BACK-31 | CatalogController | API REST | Should | XS | BACK-20 |
| BACK-32 | ValidationController | API REST | Must | S | BACK-26, BACK-29 |
| BACK-33 | GlobalExceptionHandler | API REST | Must | S | BACK-29 |
| BACK-34 | ExcelExporter (Apache POI) | Export Excel | Must | L | BACK-19, BACK-01 |
| BACK-35 | ExcelExportAppService | Export Excel | Must | S | BACK-34, BACK-24, BACK-25 |
| BACK-36 | ExportController | Export Excel | Must | S | BACK-35, BACK-29 |

**Total : 36 tickets**
**Estimation totale : ~15-18 jours/homme**
**Chemin critique :** BACK-01 -> BACK-02 -> BACK-06 -> BACK-07 -> BACK-12 -> BACK-17 -> BACK-21 -> BACK-24/25 -> BACK-26 -> BACK-28 -> BACK-30 -> BACK-35 -> BACK-36
