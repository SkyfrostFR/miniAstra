-- V3 : Ajout des gares et liaison gare↔tronçon

-- ─── Gares ────────────────────────────────────────────────────────────────────
CREATE TABLE stations (
    id          UUID             PRIMARY KEY DEFAULT gen_random_uuid(),
    scenario_id UUID             NOT NULL REFERENCES scenarios(id) ON DELETE CASCADE,
    name        VARCHAR(100)     NOT NULL,
    lat         DOUBLE PRECISION NOT NULL,
    lon         DOUBLE PRECISION NOT NULL,
    UNIQUE (scenario_id, name)
);

CREATE INDEX idx_stations_scenario_id ON stations(scenario_id);

-- ─── Liaisons gare ↔ tronçon ──────────────────────────────────────────────────
ALTER TABLE track_segments
    ADD COLUMN start_station_id UUID REFERENCES stations(id) ON DELETE SET NULL,
    ADD COLUMN end_station_id   UUID REFERENCES stations(id) ON DELETE SET NULL;

CREATE INDEX idx_track_segments_start_station ON track_segments(start_station_id);
CREATE INDEX idx_track_segments_end_station   ON track_segments(end_station_id);
