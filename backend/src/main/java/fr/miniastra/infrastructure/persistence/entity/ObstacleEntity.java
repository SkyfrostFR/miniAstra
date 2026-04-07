package fr.miniastra.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "obstacles")
@Getter
@Setter
@NoArgsConstructor
public class ObstacleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "scenario_id", nullable = false)
    private UUID scenarioId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 20)
    private String type;

    @Column(name = "track_id", nullable = false)
    private UUID trackId;

    @Column(name = "position_m", nullable = false)
    private double positionM = 0.0;

    @Column(name = "length_m", nullable = false)
    private double lengthM = 10.0;

    @Column(nullable = false)
    private boolean blocking = false;

    @Column(name = "speed_limit_kmh", nullable = false)
    private int speedLimitKmh = 30;

    @Column(name = "visibility_m", nullable = false)
    private double visibilityM = 200.0;

    @Column(name = "appear_at_s", nullable = false)
    private int appearAtS = 0;

    @Column(name = "disappear_at_s")
    private Integer disappearAtS;
}
