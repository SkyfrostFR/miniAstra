/**
 * Services applicatifs — orchestration des cas d'usage.
 *
 * <p>Un service applicatif coordonne les appels aux repositories du domaine,
 * gère les transactions ({@code @Transactional}) et publie des événements si nécessaire.
 * Il ne contient pas de logique métier — celle-ci est dans le domaine.
 */
package fr.miniastra.application.service;
