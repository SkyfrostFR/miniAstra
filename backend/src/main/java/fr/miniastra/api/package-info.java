/**
 * Couche API — Interface HTTP de MiniAstra.
 *
 * <p>Traduit les requêtes HTTP en appels vers la couche applicative.
 * N'accède jamais directement à l'infrastructure.
 *
 * <p>Contient :
 * <ul>
 *   <li>{@code controller/} — Contrôleurs REST ({@code @RestController})</li>
 *   <li>{@code dto/request/} — DTOs de requête (payload entrant)</li>
 *   <li>{@code dto/response/} — DTOs de réponse (payload sortant)</li>
 *   <li>{@code mapper/} — Mappers MapStruct API ↔ domaine/application</li>
 *   <li>{@code exception/} — Gestionnaire global des exceptions</li>
 * </ul>
 */
package fr.miniastra.api;
