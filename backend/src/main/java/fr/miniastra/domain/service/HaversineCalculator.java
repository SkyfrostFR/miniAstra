package fr.miniastra.domain.service;

import java.util.List;

/**
 * Calcul de distance Haversine.
 *
 * <p>Utilisé pour calculer {@code length_m} d'un tronçon : distance totale
 * de {@code start} → {@code waypoints[0..n]} → {@code end}.
 */
public final class HaversineCalculator {

    private static final double EARTH_RADIUS_M = 6_371_000.0;

    private HaversineCalculator() {}

    /** Distance en mètres entre deux points (lat/lon en degrés décimaux). */
    public static double distanceM(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return EARTH_RADIUS_M * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    /**
     * Longueur totale d'un tronçon avec waypoints intermédiaires (en mètres).
     *
     * @param startLat latitude du point de départ
     * @param startLon longitude du point de départ
     * @param waypoints liste ordonnée [[lat,lon], ...] — peut être vide
     * @param endLat   latitude du point d'arrivée
     * @param endLon   longitude du point d'arrivée
     * @return longueur totale en mètres
     */
    public static double trackLengthM(
            double startLat, double startLon,
            List<List<Double>> waypoints,
            double endLat, double endLon) {

        double total = 0.0;
        double prevLat = startLat;
        double prevLon = startLon;

        for (List<Double> wp : waypoints) {
            double wpLat = wp.get(0);
            double wpLon = wp.get(1);
            total += distanceM(prevLat, prevLon, wpLat, wpLon);
            prevLat = wpLat;
            prevLon = wpLon;
        }

        total += distanceM(prevLat, prevLon, endLat, endLon);
        return total;
    }
}
