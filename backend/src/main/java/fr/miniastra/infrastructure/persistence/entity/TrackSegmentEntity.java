package fr.miniastra.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "track_segments",
        uniqueConstraints = @UniqueConstraint(columnNames = {"scenario_id", "name"}))
@Getter
@Setter
@NoArgsConstructor
public class TrackSegmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "scenario_id", nullable = false)
    private UUID scenarioId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "start_lat", nullable = false)
    private double startLat;

    @Column(name = "start_lon", nullable = false)
    private double startLon;

    @Column(name = "end_lat", nullable = false)
    private double endLat;

    @Column(name = "end_lon", nullable = false)
    private double endLon;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "waypoints", nullable = false)
    private List<List<Double>> waypoints = new ArrayList<>();

    @Column(name = "length_m", nullable = false)
    private double lengthM;

    @Column(name = "max_speed_kmh", nullable = false)
    private int maxSpeedKmh;

    @Column(name = "track_count", nullable = false)
    private int trackCount = 1;

    @Column(name = "electrification", nullable = false, length = 20)
    private String electrification = "NONE";

    @Column(name = "grade_permil", nullable = false)
    private double gradePermil = 0.0;

    @Column(name = "start_station_id")
    private UUID startStationId;

    @Column(name = "end_station_id")
    private UUID endStationId;
}
