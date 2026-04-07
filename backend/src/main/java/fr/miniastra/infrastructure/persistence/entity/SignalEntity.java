package fr.miniastra.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "signals")
@Getter
@Setter
@NoArgsConstructor
public class SignalEntity {

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

    @Column(nullable = false, length = 10)
    private String direction = "PAIR";

    @Column(name = "initial_state", nullable = false, length = 20)
    private String initialState = "VOIE_LIBRE";
}
