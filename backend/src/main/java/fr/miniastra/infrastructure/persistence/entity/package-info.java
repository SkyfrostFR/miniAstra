/**
 * Entités JPA — seul endroit de l'application où {@code @Entity} est autorisé.
 *
 * <p>Ces classes sont la représentation persistée des modèles domaine.
 * Elles ne doivent pas contenir de logique métier.
 *
 * <p>La règle ArchUnit vérifie que {@code @Entity} n'apparaît nulle part ailleurs
 * dans le projet.
 */
package fr.miniastra.infrastructure.persistence.entity;
