# Graph Report - .  (2026-04-13)

## Corpus Check
- 198 files · ~76,053 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 653 nodes · 923 edges · 58 communities detected
- Extraction: 99% EXTRACTED · 1% INFERRED · 0% AMBIGUOUS · INFERRED: 7 edges (avg confidence: 0.84)
- Token cost: 8,500 input · 3,200 output

## God Nodes (most connected - your core abstractions)
1. `ValidationServiceTest` - 17 edges
2. `ScenarioStatsServiceTest` - 12 edges
3. `cmd_write()` - 11 edges
4. `TestResolveInputs` - 9 edges
5. `TrackSegmentService` - 9 edges
6. `Cross-Pollination Technique` - 9 edges
7. `TestFindTargetModuleYaml` - 8 edges
8. `ScenarioController` - 8 edges
9. `SignalService` - 8 edges
10. `FreightTrainService` - 8 edges

## Surprising Connections (you probably didn't know these)
- `MiniAstra Favicon SVG (Purple Lightning Bolt)` --conceptually_related_to--> `MiniAstra Project Context`  [INFERRED]
  frontend/public/favicon.svg → _bmad-output/brainstorming/brainstorming-session-2026-04-06-1130-distillate.md
- `Vite Logo SVG` --references--> `React + TypeScript + Vite Template`  [INFERRED]
  frontend/src/assets/vite.svg → frontend/README.md
- `React Logo SVG` --references--> `React + TypeScript + Vite Template`  [INFERRED]
  frontend/src/assets/react.svg → frontend/README.md
- `Hero Image — Isometric Layered Shapes (Purple)` --semantically_similar_to--> `MiniAstra Favicon SVG (Purple Lightning Bolt)`  [INFERRED] [semantically similar]
  frontend/src/assets/hero.png → frontend/public/favicon.svg
- `Theme C — Data Quality` --references--> `GH#8 — Visual Completeness Indicators`  [EXTRACTED]
  _bmad-output/brainstorming/brainstorming-session-2026-04-06-1130.md → _bmad-output/brainstorming/brainstorming-session-2026-04-06-1130-distillate.md

## Hyperedges (group relationships)
- **Brainstorming Session — Full Technique Execution** — brainstorm_session_technique_cross_pollination, brainstorm_session_technique_scamper, brainstorm_session_technique_what_if, brainstorm_session_overview [EXTRACTED 1.00]
- **MVP Candidate Features (low complexity, immediate value)** — brainstorm_distillate_gh1_counters, brainstorm_distillate_gh2_zoom, brainstorm_distillate_gh3_filter, brainstorm_distillate_gh4_split [EXTRACTED 1.00]
- **Post-MVP Sprint 1 Features** — brainstorm_distillate_gh5_duplication, brainstorm_distillate_gh6_map_filter, brainstorm_distillate_gh7_bidirectional, brainstorm_distillate_gh8_completeness [EXTRACTED 1.00]
- **Post-MVP Sprint 2 Features** — brainstorm_distillate_gh9_validation, brainstorm_distillate_gh10_scenarios [EXTRACTED 1.00]
- **Frontend Brand and Visual Assets** — hero_png_isometric_layers, vite_svg_vite_logo, react_svg_react_logo, favicon_svg_miniastra_favicon [INFERRED 0.80]
- **Public SVG Icon Set** — icons_svg_bluesky_icon, icons_svg_discord_icon, icons_svg_documentation_icon, icons_svg_github_icon, icons_svg_social_icon, icons_svg_x_icon [EXTRACTED 1.00]

## Communities

### Community 0 - "Train & Cargo Domain"
Cohesion: 0.07
Nodes (7): ExcelExportService, FreightTrainSheetWriter, ObstacleSheetWriter, PassengerTrainSheetWriter, ScenarioStatsService, SignalSheetWriter, ValidationService

### Community 1 - "BMad Init Tests"
Cohesion: 0.04
Nodes (12): Skill assets module.yaml takes priority over _bmad/{module}/., First-run case: LLM provides path but _bmad doesn't exist yet., TestApplyResultTemplate, TestExpandTemplate, TestFindCoreModuleYaml, TestFindProjectRoot, TestFindTargetModuleYaml, TestLoadConfigFile (+4 more)

### Community 2 - "Station & Conflict Management"
Cohesion: 0.05
Nodes (8): ConflictException, StationController, StationEntity, StationEntityMapper, StationJpaRepository, StationRepository, StationRepositoryAdapter, StationService

### Community 3 - "Scenario & Excel Export"
Cohesion: 0.05
Nodes (7): ExcelExportController, ScenarioController, ScenarioEntity, ScenarioEntityMapper, ScenarioJpaRepository, ScenarioRepositoryAdapter, ScenarioService

### Community 4 - "Track Segment & Seeder"
Cohesion: 0.06
Nodes (7): FranceSimulationSeeder, HaversineCalculator, TrackSegmentController, TrackSegmentEntity, TrackSegmentEntityMapper, TrackSegmentJpaRepository, TrackSegmentRepositoryAdapter

### Community 5 - "Source Analysis Tools"
Cohesion: 0.06
Nodes (18): analyze(), detect_doc_type(), main(), output_json(), Detect document type from filename., Suggest document groupings based on naming conventions., Main analysis function., Write JSON to file or stdout. (+10 more)

### Community 6 - "BMad Init Core"
Cohesion: 0.11
Nodes (30): apply_result_template(), cmd_check(), cmd_load(), cmd_resolve_defaults(), cmd_write(), expand_template(), find_core_module_yaml(), find_project_root() (+22 more)

### Community 7 - "React Frontend UI"
Cohesion: 0.12
Nodes (2): f(), handleSubmit()

### Community 8 - "Brainstorm Distillate"
Cohesion: 0.14
Nodes (27): GH#10 — Local Session Save / Scenarios, GH#1 — Real-Time Object Counters, GH#2 — Map Zoom on Selected Object, GH#3 — Global Datagrid Filter, GH#4 — Resizable Map/Datagrid Split, GH#5 — Object Duplication, GH#6 — Map Filter by Object Type, GH#7 — Bidirectional Map/Datagrid Selection (+19 more)

### Community 9 - "Passenger Train & GeoJSON"
Cohesion: 0.1
Nodes (4): GeoJsonController, PassengerTrainController, ResourceNotFoundException, ValidationController

### Community 10 - "Validation Service Tests"
Cohesion: 0.22
Nodes (1): ValidationServiceTest

### Community 11 - "Application Context & Catalog"
Cohesion: 0.15
Nodes (2): ApplicationContextTest, CatalogController

### Community 12 - "Scenario Stats Tests"
Cohesion: 0.27
Nodes (1): ScenarioStatsServiceTest

### Community 13 - "Track Segment Service"
Cohesion: 0.42
Nodes (1): TrackSegmentService

### Community 14 - "Frontend Config"
Cohesion: 0.22
Nodes (9): ESLint Type-Aware Configuration, eslint-plugin-react-dom, eslint-plugin-react-x, React Compiler, React + TypeScript + Vite Template, @vitejs/plugin-react (Oxc), @vitejs/plugin-react-swc (SWC), React Logo SVG (+1 more)

### Community 15 - "Community 15"
Cohesion: 0.43
Nodes (1): SignalService

### Community 16 - "Community 16"
Cohesion: 0.43
Nodes (1): FreightTrainService

### Community 17 - "Community 17"
Cohesion: 0.43
Nodes (1): PassengerTrainService

### Community 18 - "Community 18"
Cohesion: 0.43
Nodes (1): ObstacleService

### Community 19 - "Community 19"
Cohesion: 0.25
Nodes (1): ArchitectureTest

### Community 20 - "Community 20"
Cohesion: 0.29
Nodes (1): ScenarioRepository

### Community 21 - "Community 21"
Cohesion: 0.29
Nodes (1): TrackSegmentRepository

### Community 22 - "Community 22"
Cohesion: 0.29
Nodes (1): GlobalExceptionHandler

### Community 23 - "Community 23"
Cohesion: 0.33
Nodes (1): ObstacleRepository

### Community 24 - "Community 24"
Cohesion: 0.33
Nodes (1): FreightTrainRepository

### Community 25 - "Community 25"
Cohesion: 0.33
Nodes (1): SignalRepository

### Community 26 - "Community 26"
Cohesion: 0.33
Nodes (1): PassengerTrainRepository

### Community 27 - "Community 27"
Cohesion: 0.33
Nodes (1): SignalController

### Community 28 - "Community 28"
Cohesion: 0.33
Nodes (1): ObstacleController

### Community 29 - "Community 29"
Cohesion: 0.33
Nodes (1): FreightTrainController

### Community 30 - "Community 30"
Cohesion: 0.33
Nodes (1): ObstacleRepositoryAdapter

### Community 31 - "Community 31"
Cohesion: 0.33
Nodes (1): PassengerTrainRepositoryAdapter

### Community 32 - "Community 32"
Cohesion: 0.33
Nodes (1): FreightTrainRepositoryAdapter

### Community 33 - "Community 33"
Cohesion: 0.33
Nodes (1): SignalRepositoryAdapter

### Community 34 - "Community 34"
Cohesion: 0.6
Nodes (1): GeoJsonService

### Community 35 - "Community 35"
Cohesion: 0.4
Nodes (2): ObstacleEntity, ObstacleJpaRepository

### Community 36 - "Community 36"
Cohesion: 0.4
Nodes (2): FreightTrainEntity, FreightTrainJpaRepository

### Community 37 - "Community 37"
Cohesion: 0.4
Nodes (2): PassengerTrainEntity, PassengerTrainJpaRepository

### Community 38 - "Community 38"
Cohesion: 0.4
Nodes (2): SignalEntity, SignalJpaRepository

### Community 39 - "Community 39"
Cohesion: 0.67
Nodes (1): CorsConfig

### Community 40 - "Community 40"
Cohesion: 0.83
Nodes (1): TrackSegmentSheetWriter

### Community 41 - "Community 41"
Cohesion: 0.67
Nodes (1): MiniAstraApplication

### Community 42 - "Community 42"
Cohesion: 0.67
Nodes (1): FreightTrainEntityMapper

### Community 43 - "Community 43"
Cohesion: 0.67
Nodes (1): SignalEntityMapper

### Community 44 - "Community 44"
Cohesion: 0.67
Nodes (1): ObstacleEntityMapper

### Community 45 - "Community 45"
Cohesion: 0.67
Nodes (1): PassengerTrainEntityMapper

### Community 46 - "Community 46"
Cohesion: 0.67
Nodes (1): SheetWriter

### Community 47 - "Community 47"
Cohesion: 1.0
Nodes (0): 

### Community 48 - "Community 48"
Cohesion: 1.0
Nodes (0): 

### Community 49 - "Community 49"
Cohesion: 1.0
Nodes (0): 

### Community 50 - "Community 50"
Cohesion: 1.0
Nodes (0): 

### Community 51 - "Community 51"
Cohesion: 1.0
Nodes (0): 

### Community 52 - "Community 52"
Cohesion: 1.0
Nodes (1): Bluesky Social Icon

### Community 53 - "Community 53"
Cohesion: 1.0
Nodes (1): Discord Icon

### Community 54 - "Community 54"
Cohesion: 1.0
Nodes (1): Documentation Icon (Purple)

### Community 55 - "Community 55"
Cohesion: 1.0
Nodes (1): GitHub Icon

### Community 56 - "Community 56"
Cohesion: 1.0
Nodes (1): Social/Profile Icon (Purple)

### Community 57 - "Community 57"
Cohesion: 1.0
Nodes (1): X (Twitter) Icon

## Knowledge Gaps
- **47 isolated node(s):** `Resolve input arguments to a flat list of file paths.`, `Detect document type from filename.`, `Suggest document groupings based on naming conventions.`, `Main analysis function.`, `Write JSON to file or stdout.` (+42 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **Thin community `Community 47`** (2 nodes): `ErrorResponse.java`, `of()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 48`** (1 nodes): `package-info.java`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 49`** (1 nodes): `eslint.config.js`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 50`** (1 nodes): `vite.config.ts`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 51`** (1 nodes): `main.tsx`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 52`** (1 nodes): `Bluesky Social Icon`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 53`** (1 nodes): `Discord Icon`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 54`** (1 nodes): `Documentation Icon (Purple)`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 55`** (1 nodes): `GitHub Icon`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 56`** (1 nodes): `Social/Profile Icon (Purple)`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 57`** (1 nodes): `X (Twitter) Icon`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `ValidationServiceTest` connect `Validation Service Tests` to `Train & Cargo Domain`?**
  _High betweenness centrality (0.030) - this node is a cross-community bridge._
- **Why does `ScenarioStatsServiceTest` connect `Scenario Stats Tests` to `Train & Cargo Domain`?**
  _High betweenness centrality (0.021) - this node is a cross-community bridge._
- **Why does `TrackSegmentService` connect `Track Segment Service` to `Track Segment & Seeder`?**
  _High betweenness centrality (0.015) - this node is a cross-community bridge._
- **What connects `Resolve input arguments to a flat list of file paths.`, `Detect document type from filename.`, `Suggest document groupings based on naming conventions.` to the rest of the system?**
  _47 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Train & Cargo Domain` be split into smaller, more focused modules?**
  _Cohesion score 0.07 - nodes in this community are weakly interconnected._
- **Should `BMad Init Tests` be split into smaller, more focused modules?**
  _Cohesion score 0.04 - nodes in this community are weakly interconnected._
- **Should `Station & Conflict Management` be split into smaller, more focused modules?**
  _Cohesion score 0.05 - nodes in this community are weakly interconnected._