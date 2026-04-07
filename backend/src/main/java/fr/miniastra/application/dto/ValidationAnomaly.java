package fr.miniastra.application.dto;

import java.util.UUID;

/**
 * Représente une anomalie de validation détectée dans un scénario.
 *
 * @param objectType  type d'objet concerné (ex. "obstacle", "tronçon")
 * @param objectId    identifiant de l'objet concerné
 * @param objectName  nom lisible de l'objet concerné
 * @param rule        code de la règle violée
 * @param description description lisible de l'anomalie
 */
public record ValidationAnomaly(
        String objectType,
        UUID objectId,
        String objectName,
        String rule,
        String description
) {}
