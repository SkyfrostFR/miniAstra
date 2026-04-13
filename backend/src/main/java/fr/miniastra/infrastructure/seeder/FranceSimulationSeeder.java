package fr.miniastra.infrastructure.seeder;

import fr.miniastra.application.service.PassengerTrainService;
import fr.miniastra.application.service.ScenarioService;
import fr.miniastra.application.service.StationService;
import fr.miniastra.application.service.TrackSegmentService;
import fr.miniastra.domain.model.Electrification;
import fr.miniastra.domain.model.Scenario;
import fr.miniastra.domain.model.Station;
import fr.miniastra.domain.model.TrackSegment;
import fr.miniastra.domain.model.TrainDirection;
import fr.miniastra.domain.repository.ScenarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Initialise un scénario de démonstration "France - Grandes Villes" au démarrage
 * si aucun scénario de ce nom n'existe déjà.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FranceSimulationSeeder implements ApplicationRunner {

    private static final String SCENARIO_NAME = "France - Grandes Villes";

    private final ScenarioRepository scenarioRepository;
    private final ScenarioService scenarioService;
    private final StationService stationService;
    private final TrackSegmentService trackSegmentService;
    private final PassengerTrainService passengerTrainService;

    @Override
    public void run(ApplicationArguments args) {
        boolean alreadyExists = scenarioRepository.findAll().stream()
                .anyMatch(s -> SCENARIO_NAME.equals(s.name()));
        if (alreadyExists) {
            log.info("[Seeder] Scénario '{}' déjà présent — seed ignoré.", SCENARIO_NAME);
            return;
        }

        log.info("[Seeder] Création du scénario de démonstration '{}'...", SCENARIO_NAME);
        seed();
        log.info("[Seeder] Scénario '{}' créé avec succès.", SCENARIO_NAME);
    }

    private void seed() {
        Scenario scenario = scenarioService.create(
                SCENARIO_NAME,
                "Simulation de base sur les 10 plus grandes villes de France avec liaisons TGV et trains pré-placés.",
                28800,
                LocalTime.of(6, 0)
        );
        UUID sid = scenario.id();

        // ---- Gares (10 plus grandes villes de France) ----
        Station paris      = stationService.create(sid, "Paris-Gare-de-Lyon",        48.8448,  2.3735);
        Station marseille  = stationService.create(sid, "Marseille-Saint-Charles",   43.3038,  5.3814);
        Station lyon       = stationService.create(sid, "Lyon-Part-Dieu",            45.7606,  4.8592);
        Station toulouse   = stationService.create(sid, "Toulouse-Matabiau",         43.6109,  1.4540);
        Station nice       = stationService.create(sid, "Nice-Ville",                43.7042,  7.2619);
        Station nantes     = stationService.create(sid, "Nantes",                    47.2170, -1.5417);
        Station montpellier= stationService.create(sid, "Montpellier-Saint-Roch",    43.6054,  3.8797);
        Station strasbourg = stationService.create(sid, "Strasbourg",                48.5850,  7.7347);
        Station bordeaux   = stationService.create(sid, "Bordeaux-Saint-Jean",       44.8253, -0.5566);
        Station lille      = stationService.create(sid, "Lille-Europe",              50.6394,  3.0756);

        // ---- Tronçons (lignes TGV principales) ----

        // LGV Sud-Est : Paris — Lyon
        TrackSegment parisLyon = trackSegmentService.create(
                sid, "LGV Sud-Est : Paris — Lyon",
                paris.lat(), paris.lon(), lyon.lat(), lyon.lon(),
                List.of(
                        List.of(48.5218, 2.6533),   // Lieusaint
                        List.of(47.2625, 3.5253),   // Dijon (bypass)
                        List.of(46.0750, 4.1000)    // Mâcon
                ),
                320, 2, Electrification.AC_25KV, 0.0,
                paris.id(), lyon.id()
        );

        // Lyon — Marseille
        TrackSegment lyonMarseille = trackSegmentService.create(
                sid, "LGV Méditerranée : Lyon — Marseille",
                lyon.lat(), lyon.lon(), marseille.lat(), marseille.lon(),
                List.of(
                        List.of(44.9300, 4.8700),   // Valence
                        List.of(43.9500, 4.8100)    // Avignon
                ),
                300, 2, Electrification.AC_25KV, 0.0,
                lyon.id(), marseille.id()
        );

        // LGV Atlantique : Paris — Bordeaux
        TrackSegment parisBordeaux = trackSegmentService.create(
                sid, "LGV Atlantique : Paris — Bordeaux",
                paris.lat(), paris.lon(), bordeaux.lat(), bordeaux.lon(),
                List.of(
                        List.of(47.9900, 1.9600),   // Vendôme
                        List.of(46.6500, 0.3500),   // Poitiers
                        List.of(45.6200, -0.1600)   // Angoulême
                ),
                320, 2, Electrification.AC_25KV, 0.0,
                paris.id(), bordeaux.id()
        );

        // LGV Nord : Paris — Lille
        TrackSegment parisLille = trackSegmentService.create(
                sid, "LGV Nord : Paris — Lille",
                paris.lat(), paris.lon(), lille.lat(), lille.lon(),
                List.of(
                        List.of(49.1500, 2.7200),   // Roissy (CDG)
                        List.of(50.0200, 2.7600)    // Arras
                ),
                320, 2, Electrification.AC_25KV, 0.0,
                paris.id(), lille.id()
        );

        // LGV Est : Paris — Strasbourg
        TrackSegment parisStrasbourg = trackSegmentService.create(
                sid, "LGV Est : Paris — Strasbourg",
                paris.lat(), paris.lon(), strasbourg.lat(), strasbourg.lon(),
                List.of(
                        List.of(48.9400, 3.5700),   // Champagne-Ardenne
                        List.of(48.9700, 4.3600),   // Meuse
                        List.of(48.6900, 6.1800)    // Nancy
                ),
                320, 2, Electrification.AC_25KV, 0.0,
                paris.id(), strasbourg.id()
        );

        // Paris — Nantes (LGV Atlantique branche Bretagne)
        TrackSegment parisNantes = trackSegmentService.create(
                sid, "LGV Atlantique : Paris — Nantes",
                paris.lat(), paris.lon(), nantes.lat(), nantes.lon(),
                List.of(
                        List.of(48.0000, 1.7600),   // Le Mans
                        List.of(47.4700, -0.5500)   // Angers
                ),
                300, 2, Electrification.AC_25KV, 0.0,
                paris.id(), nantes.id()
        );

        // Bordeaux — Toulouse
        TrackSegment bordeauxToulouse = trackSegmentService.create(
                sid, "Bordeaux — Toulouse",
                bordeaux.lat(), bordeaux.lon(), toulouse.lat(), toulouse.lon(),
                List.of(
                        List.of(44.4000, 0.0700),   // Agen
                        List.of(43.9500, 1.1000)    // Montauban
                ),
                200, 2, Electrification.AC_25KV, 0.0,
                bordeaux.id(), toulouse.id()
        );

        // Marseille — Montpellier
        TrackSegment marseilleMontp = trackSegmentService.create(
                sid, "Marseille — Montpellier",
                marseille.lat(), marseille.lon(), montpellier.lat(), montpellier.lon(),
                List.of(
                        List.of(43.4400, 5.0500),   // Martigues
                        List.of(43.5300, 4.2800)    // Arles
                ),
                250, 2, Electrification.AC_25KV, 0.0,
                marseille.id(), montpellier.id()
        );

        // Nice — Marseille (Côte d'Azur)
        TrackSegment niceMarseille = trackSegmentService.create(
                sid, "Nice — Marseille (Côte d'Azur)",
                nice.lat(), nice.lon(), marseille.lat(), marseille.lon(),
                List.of(
                        List.of(43.6700, 7.0000),   // Antibes
                        List.of(43.5000, 6.6500),   // Fréjus
                        List.of(43.1200, 6.1700)    // Toulon
                ),
                200, 2, Electrification.DC_1500V, 0.0,
                nice.id(), marseille.id()
        );

        // ---- Trains pré-placés ----

        // TGV Duplex Paris → Lyon, départ gare (pos 0)
        passengerTrainService.create(sid, "TGV Duplex 6101", "TGV-DUPLEX",
                parisLyon.id(), 0.0, TrainDirection.PAIR, 0.0, 507, "6101");

        // TGV Duplex Lyon → Paris, en cours de route (mi-trajet ≈ 160 km)
        passengerTrainService.create(sid, "TGV Duplex 6202", "TGV-DUPLEX",
                parisLyon.id(), 160_000.0, TrainDirection.IMPAIR, 300.0, 389, "6202");

        // TGV Atlantique Paris → Bordeaux, en cours (80 km)
        passengerTrainService.create(sid, "TGV Atlantique 8501", "TGV-ATLANTIQUE",
                parisBordeaux.id(), 80_000.0, TrainDirection.PAIR, 320.0, 485, "8501");

        // Eurostar/TGV Paris → Lille (pos 0)
        passengerTrainService.create(sid, "TGV 7701", "TGV-RESEAU",
                parisLille.id(), 0.0, TrainDirection.PAIR, 0.0, 370, "7701");

        // TGV Est Paris → Strasbourg (50 km)
        passengerTrainService.create(sid, "TGV Est 6801", "TGV-POS",
                parisStrasbourg.id(), 50_000.0, TrainDirection.PAIR, 320.0, 320, "6801");

        // TGV Lyon → Marseille (départ gare)
        passengerTrainService.create(sid, "TGV 6657", "TGV-DUPLEX",
                lyonMarseille.id(), 0.0, TrainDirection.PAIR, 0.0, 200, "6657");

        // Intercités Bordeaux → Toulouse (en cours, 100 km)
        passengerTrainService.create(sid, "Intercités 3801", "IC-200",
                bordeauxToulouse.id(), 100_000.0, TrainDirection.PAIR, 160.0, 310, "3801");

        // TGV Paris → Nantes (départ)
        passengerTrainService.create(sid, "TGV 8011", "TGV-ATLANTIQUE",
                parisNantes.id(), 0.0, TrainDirection.PAIR, 0.0, 400, "8011");

        // TGV Marseille → Montpellier (mi-trajet ≈ 80 km)
        passengerTrainService.create(sid, "TGV 6181", "TGV-DUPLEX",
                marseilleMontp.id(), 80_000.0, TrainDirection.PAIR, 250.0, 180, "6181");

        // Téoz Nice → Marseille (en route, 100 km)
        passengerTrainService.create(sid, "Intercités 4761", "IC-200",
                niceMarseille.id(), 100_000.0, TrainDirection.PAIR, 160.0, 240, "4761");
    }
}
