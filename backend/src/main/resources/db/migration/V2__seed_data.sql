-- V2 : Données d'exemple — Scénario "Corridor Paris-Lyon"
-- 10 objets par type. Zone : bbox Paris–Lyon, AC_25KV, pentes ∈ [-15, 15]‰.

DO $$
DECLARE
    s_id UUID := gen_random_uuid();
    t01  UUID := gen_random_uuid();
    t02  UUID := gen_random_uuid();
    t03  UUID := gen_random_uuid();
    t04  UUID := gen_random_uuid();
    t05  UUID := gen_random_uuid();
    t06  UUID := gen_random_uuid();
    t07  UUID := gen_random_uuid();
    t08  UUID := gen_random_uuid();
    t09  UUID := gen_random_uuid();
    t10  UUID := gen_random_uuid();
BEGIN

-- ─── Scénario ─────────────────────────────────────────────────────────────────
INSERT INTO scenarios (id, name, description, duration_s, start_time)
VALUES (
    s_id,
    'Corridor Paris-Lyon',
    'Scénario de démonstration — ligne fictive Paris → Lyon (10 tronçons, AC_25kV)',
    7200,
    '06:00:00'
);

-- ─── Tronçons de voie ─────────────────────────────────────────────────────────
-- 10 segments couvrant approximativement Paris (48.85°N,2.35°E) → Lyon (45.75°N,4.83°E)
-- Longueurs calculées par Haversine approximative — précision ±5 %

INSERT INTO track_segments
    (id, scenario_id, name, start_lat, start_lon, end_lat, end_lon,
     waypoints, length_m, max_speed_kmh, track_count, electrification, grade_permil)
VALUES
    (t01, s_id, 'Tronçon T01', 48.850, 2.350, 48.540, 2.630, '[]', 43800, 320, 2, 'AC_25KV',  2.0),
    (t02, s_id, 'Tronçon T02', 48.540, 2.630, 48.230, 2.900, '[]', 42600, 300, 2, 'AC_25KV',  5.0),
    (t03, s_id, 'Tronçon T03', 48.230, 2.900, 47.920, 3.160, '[]', 43200, 320, 2, 'AC_25KV', -3.0),
    (t04, s_id, 'Tronçon T04', 47.920, 3.160, 47.610, 3.420, '[]', 42900, 300, 2, 'AC_25KV',  8.0),
    (t05, s_id, 'Tronçon T05', 47.610, 3.420, 47.300, 3.680, '[]', 43500, 320, 2, 'AC_25KV', -5.0),
    (t06, s_id, 'Tronçon T06', 47.300, 3.680, 46.990, 3.940, '[]', 43100, 300, 2, 'AC_25KV', 12.0),
    (t07, s_id, 'Tronçon T07', 46.990, 3.940, 46.680, 4.200, '[]', 43400, 320, 2, 'AC_25KV', -8.0),
    (t08, s_id, 'Tronçon T08', 46.680, 4.200, 46.370, 4.460, '[]', 43200, 300, 2, 'AC_25KV',  3.0),
    (t09, s_id, 'Tronçon T09', 46.370, 4.460, 46.060, 4.660, '[]', 40700, 320, 2, 'AC_25KV', 15.0),
    (t10, s_id, 'Tronçon T10', 46.060, 4.660, 45.750, 4.830, '[]', 38900, 300, 2, 'AC_25KV', -4.0);

-- ─── Trains passagers ─────────────────────────────────────────────────────────
-- 5 TGV Duplex + 3 TER Régiolis + 2 Intercités Corail

INSERT INTO passenger_trains
    (scenario_id, name, model_code, track_id, position_m,
     direction, initial_speed_kmh, passenger_count, service_number)
VALUES
    (s_id, 'TGV 6201', 'TGV-DUPLEX',   t01, 5000,  'PAIR',   280, 320, '6201'),
    (s_id, 'TGV 6203', 'TGV-DUPLEX',   t02, 8000,  'PAIR',   300, 350, '6203'),
    (s_id, 'TGV 6205', 'TGV-DUPLEX',   t04, 3000,  'PAIR',   270, 305, '6205'),
    (s_id, 'TGV 6207', 'TGV-DUPLEX',   t06, 12000, 'PAIR',   290, 340, '6207'),
    (s_id, 'TGV 6209', 'TGV-DUPLEX',   t08, 6000,  'PAIR',   285, 315, '6209'),
    (s_id, 'TER 87001', 'TER-REGIOLIS', t03, 20000, 'IMPAIR', 140, 180, '87001'),
    (s_id, 'TER 87003', 'TER-REGIOLIS', t05, 15000, 'PAIR',   130, 210, '87003'),
    (s_id, 'TER 87005', 'TER-REGIOLIS', t09, 10000, 'IMPAIR', 120, 165, '87005'),
    (s_id, 'IC 3701',  'IC-CORAIL',    t07, 18000, 'PAIR',   180, 420, '3701'),
    (s_id, 'IC 3703',  'IC-CORAIL',    t10, 5000,  'IMPAIR', 170, 395, '3703');

-- ─── Trains de marchandises ───────────────────────────────────────────────────
-- 4 BB 75000 + 3 BB 60000 + 3 BB 27000

INSERT INTO freight_trains
    (scenario_id, name, model_code, track_id, position_m,
     direction, initial_speed_kmh, load_t, cargo_type)
VALUES
    (s_id, 'Fret BB75-001', 'FRET-BB75000', t01, 30000, 'IMPAIR', 80,  650, 'GENERAL'),
    (s_id, 'Fret BB75-002', 'FRET-BB75000', t03, 25000, 'PAIR',   75,  700, 'VRAC'),
    (s_id, 'Fret BB75-003', 'FRET-BB75000', t06, 20000, 'IMPAIR', 85,  620, 'GENERAL'),
    (s_id, 'Fret BB75-004', 'FRET-BB75000', t09, 15000, 'PAIR',   70,  680, 'VRAC'),
    (s_id, 'Fret BB60-001', 'FRET-BB60000', t02, 10000, 'IMPAIR', 90,  950, 'CITERNE'),
    (s_id, 'Fret BB60-002', 'FRET-BB60000', t05, 22000, 'PAIR',   85, 1000, 'CITERNE'),
    (s_id, 'Fret BB60-003', 'FRET-BB60000', t08, 35000, 'IMPAIR', 80,  980, 'DANGEREUX'),
    (s_id, 'Fret BB27-001', 'FRET-BB27000', t04, 18000, 'PAIR',   90, 1200, 'VRAC'),
    (s_id, 'Fret BB27-002', 'FRET-BB27000', t07, 28000, 'IMPAIR', 85, 1300, 'GENERAL'),
    (s_id, 'Fret BB27-003', 'FRET-BB27000', t10, 12000, 'PAIR',   95, 1150, 'VRAC');

-- ─── Obstacles ────────────────────────────────────────────────────────────────
-- Mix de types : chantier permanent, véhicule temporaire, animal, glissement, objet

INSERT INTO obstacles
    (scenario_id, name, type, track_id, position_m,
     length_m, blocking, speed_limit_kmh, visibility_m,
     appear_at_s, disappear_at_s)
VALUES
    (s_id, 'Chantier km 23',     'CHANTIER',   t01, 23000, 500, false, 60,  500, 0,    NULL),
    (s_id, 'Véhicule voie T02',  'VEHICULE',   t02, 15000, 15,  true,  0,   200, 1800, 3600),
    (s_id, 'Glissement T03',     'GLISSEMENT', t03, 38000, 200, false, 30,  300, 0,    NULL),
    (s_id, 'Animal sur voie T04','ANIMAL',      t04, 5000,  5,   false, 80,  400, 2700, 2800),
    (s_id, 'Objet T05',          'OBJET',       t05, 31000, 2,   false, 100, 250, 3600, 5400),
    (s_id, 'Chantier km 15 T06', 'CHANTIER',   t06, 15000, 300, false, 50,  400, 0,    NULL),
    (s_id, 'Véhicule T07',       'VEHICULE',   t07, 22000, 10,  false, 60,  300, 4500, 5400),
    (s_id, 'Glissement T08',     'GLISSEMENT', t08, 10000, 150, true,  0,   200, 0,    NULL),
    (s_id, 'Animal T09',         'ANIMAL',      t09, 28000, 3,   false, 90,  350, 1200, 1500),
    (s_id, 'Objet T10',          'OBJET',       t10, 8000,  1,   false, 120, 200, 5400, 6300);

-- ─── Signaux ──────────────────────────────────────────────────────────────────
-- Alternance carrés et sémaphores sur les tronçons

INSERT INTO signals
    (scenario_id, name, type, track_id, position_m, direction, initial_state)
VALUES
    (s_id, 'S101 Carré T01',        'CARRE',        t01,  1000, 'PAIR',   'VOIE_LIBRE'),
    (s_id, 'S102 Sémaphore T01',    'SEMAPHORE',    t01, 42000, 'PAIR',   'VOIE_LIBRE'),
    (s_id, 'S201 Carré T02',        'CARRE',        t02,  1000, 'PAIR',   'ARRET'),
    (s_id, 'S202 Avertissement T03','AVERTISSEMENT', t03, 20000, 'PAIR',   'VOIE_LIBRE'),
    (s_id, 'S301 Sémaphore T04',    'SEMAPHORE',    t04, 35000, 'IMPAIR', 'VOIE_LIBRE'),
    (s_id, 'S401 Carré T05',        'CARRE',        t05,  2000, 'PAIR',   'VOIE_LIBRE'),
    (s_id, 'S501 Sémaphore T06',    'SEMAPHORE',    t06, 40000, 'PAIR',   'VOIE_LIBRE'),
    (s_id, 'S601 TGV-R T07',        'TGV_R',        t07, 10000, 'BIDIR',  'VOIE_LIBRE'),
    (s_id, 'S701 Carré T08',        'CARRE',        t08, 38000, 'IMPAIR', 'ARRET'),
    (s_id, 'S801 Guidon T09',       'GUIDON',       t09, 15000, 'PAIR',   'VOIE_LIBRE');

END $$;
