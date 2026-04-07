package fr.miniastra.domain.model;

import java.util.List;

public record FreightModel(
        String code,
        String name,
        int lengthM,
        int massEmptyT,
        int maxSpeedKmh,
        String traction
) {
    public static final List<FreightModel> ALL = List.of(
            new FreightModel("FRET-BB75000", "BB 75000 + wagons plats",       400,  800, 120, "25kV AC"),
            new FreightModel("FRET-BB60000", "BB 60000 + wagons citernes",    350, 1100, 100, "1500V DC"),
            new FreightModel("FRET-BB27000", "BB 27000 + wagons tombereaux",  500, 1400, 100, "25kV AC"),
            new FreightModel("FRET-G1000",   "Gravibus G1000 (Diesel)",       300,  600,  80, "Diesel")
    );

    public static boolean isValid(String code) {
        return ALL.stream().anyMatch(m -> m.code().equals(code));
    }

    public static int maxSpeedFor(String code) {
        return ALL.stream()
                .filter(m -> m.code().equals(code))
                .mapToInt(FreightModel::maxSpeedKmh)
                .findFirst()
                .orElse(0);
    }
}
