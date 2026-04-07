## [2026-04-06] - Variables inutilisées bloquant le build TypeScript

**Fichier(s) concerné(s)** : `frontend/src/components/DataGrid.tsx`, `frontend/src/components/Toolbar.tsx`  
**Sévérité** : Moyenne  
**Catégorie** : Dette technique

**Observation** :
`selectedObjectId` dans `DataGrid.tsx` et `stats` dans `Toolbar.tsx` étaient destructurés depuis le store mais jamais consommés dans leur fichier. `tsc` levait `TS6133` sur ces deux variables, bloquant entièrement `npm run build`.

**Impact potentiel** :
Build de production bloqué. Si ce pattern se répète, les développeurs finissent par ignorer les erreurs TS ce qui dégrade la valeur du type-checking.

**Suggestion** :
Activer `"noUnusedLocals": true` dans `tsconfig.json` pour détecter ce genre de régression dès l'édition, pas uniquement au build. Les variables concernées ont été supprimées des destructurations dans ce ticket ; à implémenter correctement lorsque les fonctionnalités associées seront développées.

---

## [2026-04-06] - Absence de feedback utilisateur sur les erreurs de sauvegarde inline

**Fichier(s) concerné(s)** : `frontend/src/components/DataGrid.tsx`  
**Sévérité** : Moyenne  
**Catégorie** : Maintenabilité

**Observation** :
Les handlers `onCellValueChanged` et `handleDelete` catchent les erreurs et font un rollback visuel (`setDataValue(field, oldValue)`) en cas d'échec API, mais ne notifient l'utilisateur que via `console.error`. En production, l'utilisateur ne sait pas que sa modification n'a pas été persistée.

**Impact potentiel** :
L'utilisateur pense avoir sauvegardé une modification (la cellule revient à l'ancienne valeur sans explication), ce qui peut générer de la confusion ou des données silencieusement perdues s'il ne remarque pas le rollback.

**Suggestion** :
Intégrer un système de notification (toast) dans le store ou via un contexte React. Un `set({ error: 'Sauvegarde échouée : ...' })` dans le store Zustand suffirait à court terme si un composant d'affichage des erreurs existe déjà dans l'UI.

---
