/**
 * Couche applicative — Orchestration des cas d'usage.
 *
 * <p>Dépend uniquement du domaine ({@code fr.miniastra.domain}).
 * N'importe rien de {@code infrastructure} ni de {@code api}.
 *
 * <p>Contient :
 * <ul>
 *   <li>{@code service/} — Services applicatifs : transactions, orchestration</li>
 *   <li>{@code dto/} — Projections en lecture (ex. données agrégées pour l'UI)</li>
 * </ul>
 */
package fr.miniastra.application;
