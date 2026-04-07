# ADR-0001 : Stack technique — Spring Boot 3.3 + React 18 + PostgreSQL 16

**Date**: 2026-04-06
**Statut**: accepted
**Décideurs**: Guillaume

## Contexte

miniAstra est un outil local de préparation de données pour la simulation ferroviaire. L'application tourne en local (pas de déploiement web), sans authentification, et manipule 5 types d'objets (tronçons, trains passagers, trains fret, obstacles, signaux). Les tronçons utilisent des waypoints géospatiaux (JSONB), et les migrations de schéma sont gérées via Flyway. L'interface nécessite une grille de données avancée (AG Grid) et une carte interactive (MapLibre).

## Décision

Nous utilisons Spring Boot 3.3 (Java 21) pour le backend, React 18 pour le frontend, et PostgreSQL 16 comme base de données.

## Alternatives considérées

### Alternative 1 : FastAPI + React
- **Pour** : Python plus concis, démarrage rapide
- **Contre** : Compétences Java déjà disponibles, écosystème Spring mature
- **Rejetée car** : Les compétences Java de l'équipe rendent Spring Boot plus productif

### Alternative 2 : Spring Boot + Vue.js
- **Pour** : Vue.js plus léger que React
- **Contre** : Les intégrations AG Grid et MapLibre sont mieux documentées sur React
- **Rejetée car** : Écosystème React plus favorable pour les bibliothèques cibles

### Alternative 3 : Spring Boot + SQLite
- **Pour** : Pas besoin d'instance PostgreSQL
- **Contre** : Pas de support natif JSONB pour les waypoints des tronçons
- **Rejetée car** : JSONB est requis pour le stockage des waypoints géospatiaux

## Conséquences

### Positives
- Schéma strict avec migrations Flyway reproductibles
- Support natif JSONB pour les waypoints (tronçons)
- AG Grid et MapLibre GL JS en intégration React first-class
- Ecosystème Spring Boot mature (validation, gestion d'erreurs, configuration)

### Négatives
- Verbosité Java comparée à Python ou TypeScript
- Nécessite une instance PostgreSQL locale (vs SQLite embarqué)

### Risques
- Temps de démarrage Spring Boot plus long (~5-10s) — acceptable pour un usage local
