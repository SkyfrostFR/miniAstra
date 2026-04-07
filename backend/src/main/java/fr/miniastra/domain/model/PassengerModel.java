package fr.miniastra.domain.model;

import java.util.List;

public record PassengerModel(
        String code,
        String name,
        int lengthM,
        int massT,
        int maxSpeedKmh,
        String traction
) {
    public static final List<PassengerModel> ALL = List.of(
            new PassengerModel("TGV-DUPLEX",    "TGV Duplex",               200, 380, 320, "25kV AC"),
            new PassengerModel("TGV-INOUISEE",  "TGV inoui (Euroduplex)",   200, 410, 320, "25kV AC"),
            new PassengerModel("TER-REGIOLIS",  "TER Régiolis",              82, 118, 160, "25kV AC / Diesel"),
            new PassengerModel("TER-REGIO2N",   "TER Régio 2N",             110, 200, 160, "25kV AC"),
            new PassengerModel("IC-CORAIL",     "Intercités Corail (BB 26000)", 300, 450, 200, "25kV AC / 1500V DC"),
            new PassengerModel("RER-MI09",      "RER A MI09",               225, 310, 140, "1500V DC"),
            new PassengerModel("THALYS",        "Thalys PBKA",              200, 385, 300, "Multi-tension")
    );

    public static boolean isValid(String code) {
        return ALL.stream().anyMatch(m -> m.code().equals(code));
    }

    public static int maxSpeedFor(String code) {
        return ALL.stream()
                .filter(m -> m.code().equals(code))
                .mapToInt(PassengerModel::maxSpeedKmh)
                .findFirst()
                .orElse(0);
    }
}
