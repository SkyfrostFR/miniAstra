/**
 * Contrôleurs REST — seul endroit où {@code @RestController} est autorisé.
 *
 * <p>Un contrôleur est mince : il valide l'entrée ({@code @Valid}), délègue
 * au service applicatif, puis formate la réponse. Pas de logique métier ici.
 *
 * <p>La règle ArchUnit vérifie que {@code @RestController} n'apparaît nulle part ailleurs.
 */
package fr.miniastra.api.controller;
