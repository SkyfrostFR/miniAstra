/**
 * Couche domaine — Cœur métier de MiniAstra.
 *
 * <p><strong>Règle d'or :</strong> aucune dépendance vers les couches externes
 * ({@code infrastructure}, {@code application}, {@code api}).
 *
 * <p>Contient :
 * <ul>
 *   <li>{@code model/} — POJO domaine (pas d'annotation JPA)</li>
 *   <li>{@code repository/} — Interfaces repository (ports)</li>
 *   <li>{@code valueobject/} — Value Objects immutables</li>
 *   <li>{@code service/} — Services domaine (logique métier pure)</li>
 * </ul>
 */
package fr.miniastra.domain;
