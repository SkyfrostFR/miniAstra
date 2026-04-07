/**
 * Interfaces repository (ports) du domaine.
 *
 * <p>Définit les contrats d'accès aux données du point de vue métier.
 * Les implémentations concrètes se trouvent dans {@code infrastructure.persistence.repository}.
 *
 * <p>Ces interfaces ne doivent pas étendre {@code JpaRepository} ou tout autre
 * artefact Spring Data — elles expriment uniquement le besoin du domaine.
 */
package fr.miniastra.domain.repository;
