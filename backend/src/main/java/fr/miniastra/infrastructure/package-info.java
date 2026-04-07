/**
 * Couche infrastructure — Adaptateurs techniques.
 *
 * <p>Implémente les ports définis dans le domaine ({@code domain.repository})
 * et fournit les détails techniques : accès base de données, catalogues en mémoire, etc.
 *
 * <p>Contient :
 * <ul>
 *   <li>{@code persistence/entity/} — Entités JPA ({@code @Entity} uniquement ici)</li>
 *   <li>{@code persistence/repository/} — Implémentations Spring Data JPA</li>
 *   <li>{@code persistence/mapper/} — Mappers MapStruct domaine ↔ entités JPA</li>
 *   <li>{@code catalog/} — Catalogues statiques en mémoire (ex. référentiel trains)</li>
 * </ul>
 */
package fr.miniastra.infrastructure;
