package fr.miniastra.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "passenger_trains")
@Getter
@Setter
@NoArgsConstructor
public class PassengerTrainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "scenario_id", nullable = false)
    private UUID scenarioId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "model_code", nullable = false, length = 30)
    private String modelCode;

    @Column(name = "track_id", nullable = false)
    private UUID trackId;

    @Column(name = "position_m", nullable = false)
    private double positionM = 0.0;

    @Column(nullable = false, length = 10)
    private String direction = "PAIR";

    @Column(name = "initial_speed_kmh", nullable = false)
    private double initialSpeedKmh = 0.0;

    @Column(name = "passenger_count", nullable = false)
    private int passengerCount = 0;

    @Column(name = "service_number", length = 20)
    private String serviceNumber;
}
