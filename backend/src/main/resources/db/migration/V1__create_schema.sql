-- V1 : Schéma initial miniAstra
-- Toutes les tables du MVP : scenarios, track_segments, passenger_trains,
-- freight_trains, obstacles, signals.

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ─── Scénarios ────────────────────────────────────────────────────────────────
CREATE TABLE scenarios (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    duration_s  INTEGER      NOT NULL DEFAULT 3600,
    start_time  TIME         NOT NULL DEFAULT '08:00:00',
    created_at  TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT now()
);

-- ─── Tronçons de voie ─────────────────────────────────────────────────────────
CREATE TABLE track_segments (
    id              UUID             PRIMARY KEY DEFAULT gen_random_uuid(),
    scenario_id     UUID             NOT NULL REFERENCES scenarios(id) ON DELETE CASCADE,
    name            VARCHAR(100)     NOT NULL,
    start_lat       DOUBLE PRECISION NOT NULL,
    start_lon       DOUBLE PRECISION NOT NULL,
    end_lat         DOUBLE PRECISION NOT NULL,
    end_lon         DOUBLE PRECISION NOT NULL,
    waypoints       JSONB            NOT NULL DEFAULT '[]'::jsonb,
    length_m        DOUBLE PRECISION NOT NULL,
    max_speed_kmh   INTEGER          NOT NULL,
    track_count     INTEGER          NOT NULL DEFAULT 1,
    electrification VARCHAR(20)      NOT NULL DEFAULT 'NONE',
    grade_permil    DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    UNIQUE (scenario_id, name)
);

-- ─── Trains passagers ─────────────────────────────────────────────────────────
CREATE TABLE passenger_trains (
    id                UUID             PRIMARY KEY DEFAULT gen_random_uuid(),
    scenario_id       UUID             NOT NULL REFERENCES scenarios(id) ON DELETE CASCADE,
    name              VARCHAR(100)     NOT NULL,
    model_code        VARCHAR(30)      NOT NULL,
    track_id          UUID             NOT NULL REFERENCES track_segments(id),
    position_m        DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    direction         VARCHAR(10)      NOT NULL DEFAULT 'PAIR',
    initial_speed_kmh DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    passenger_count   INTEGER          NOT NULL DEFAULT 0,
    service_number    VARCHAR(20)
);

-- ─── Trains de marchandises ───────────────────────────────────────────────────
CREATE TABLE freight_trains (
    id                UUID             PRIMARY KEY DEFAULT gen_random_uuid(),
    scenario_id       UUID             NOT NULL REFERENCES scenarios(id) ON DELETE CASCADE,
    name              VARCHAR(100)     NOT NULL,
    model_code        VARCHAR(30)      NOT NULL,
    track_id          UUID             NOT NULL REFERENCES track_segments(id),
    position_m        DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    direction         VARCHAR(10)      NOT NULL DEFAULT 'PAIR',
    initial_speed_kmh DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    load_t            DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    cargo_type        VARCHAR(20)      NOT NULL DEFAULT 'VIDE'
);

-- ─── Obstacles ────────────────────────────────────────────────────────────────
CREATE TABLE obstacles (
    id               UUID             PRIMARY KEY DEFAULT gen_random_uuid(),
    scenario_id      UUID             NOT NULL REFERENCES scenarios(id) ON DELETE CASCADE,
    name             VARCHAR(100)     NOT NULL,
    type             VARCHAR(20)      NOT NULL,
    track_id         UUID             NOT NULL REFERENCES track_segments(id),
    position_m       DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    length_m         DOUBLE PRECISION NOT NULL DEFAULT 10.0,
    blocking         BOOLEAN          NOT NULL DEFAULT false,
    speed_limit_kmh  INTEGER          NOT NULL DEFAULT 30,
    visibility_m     DOUBLE PRECISION NOT NULL DEFAULT 200.0,
    appear_at_s      INTEGER          NOT NULL DEFAULT 0,
    disappear_at_s   INTEGER
);

-- ─── Signaux ──────────────────────────────────────────────────────────────────
CREATE TABLE signals (
    id            UUID             PRIMARY KEY DEFAULT gen_random_uuid(),
    scenario_id   UUID             NOT NULL REFERENCES scenarios(id) ON DELETE CASCADE,
    name          VARCHAR(100)     NOT NULL,
    type          VARCHAR(20)      NOT NULL,
    track_id      UUID             NOT NULL REFERENCES track_segments(id),
    position_m    DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    direction     VARCHAR(10)      NOT NULL DEFAULT 'PAIR',
    initial_state VARCHAR(20)      NOT NULL DEFAULT 'VOIE_LIBRE'
);

-- ─── Index ────────────────────────────────────────────────────────────────────
CREATE INDEX idx_track_segments_scenario_id    ON track_segments(scenario_id);
CREATE INDEX idx_passenger_trains_scenario_id  ON passenger_trains(scenario_id);
CREATE INDEX idx_passenger_trains_track_id     ON passenger_trains(track_id);
CREATE INDEX idx_freight_trains_scenario_id    ON freight_trains(scenario_id);
CREATE INDEX idx_freight_trains_track_id       ON freight_trains(track_id);
CREATE INDEX idx_obstacles_scenario_id         ON obstacles(scenario_id);
CREATE INDEX idx_obstacles_track_id            ON obstacles(track_id);
CREATE INDEX idx_signals_scenario_id           ON signals(scenario_id);
CREATE INDEX idx_signals_track_id              ON signals(track_id);
