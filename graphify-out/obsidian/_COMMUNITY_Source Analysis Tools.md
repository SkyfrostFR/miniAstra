---
type: community
cohesion: 0.06
members: 38
---

# Source Analysis Tools

**Cohesion:** 0.06 - loosely connected
**Members:** 38 nodes

## Members
- [[.test_basic_analysis()]] - code - _bmad/core/bmad-distillator/scripts/tests/test_analyze_sources.py
- [[.test_deduplication()]] - code - _bmad/core/bmad-distillator/scripts/tests/test_analyze_sources.py
- [[.test_folder_analysis()]] - code - _bmad/core/bmad-distillator/scripts/tests/test_analyze_sources.py
- [[.test_folder_recursion()]] - code - _bmad/core/bmad-distillator/scripts/tests/test_analyze_sources.py
- [[.test_folder_skips_excluded_dirs()]] - code - _bmad/core/bmad-distillator/scripts/tests/test_analyze_sources.py
- [[.test_folder_skips_non_text_files()]] - code - _bmad/core/bmad-distillator/scripts/tests/test_analyze_sources.py
- [[.test_glob_pattern()]] - code - _bmad/core/bmad-distillator/scripts/tests/test_analyze_sources.py
- [[.test_groups_brief_with_discovery_notes()]] - code - _bmad/core/bmad-distillator/scripts/tests/test_analyze_sources.py
- [[.test_mixed_grouped_and_standalone()]] - code - _bmad/core/bmad-distillator/scripts/tests/test_analyze_sources.py
- [[.test_mixed_inputs()]] - code - _bmad/core/bmad-distillator/scripts/tests/test_analyze_sources.py
- [[.test_no_files_found()]] - code - _bmad/core/bmad-distillator/scripts/tests/test_analyze_sources.py
- [[.test_nonexistent_path()]] - code - _bmad/core/bmad-distillator/scripts/tests/test_analyze_sources.py
- [[.test_routing_fanout_many_files()]] - code - _bmad/core/bmad-distillator/scripts/tests/test_analyze_sources.py
- [[.test_routing_single_small_input()]] - code - _bmad/core/bmad-distillator/scripts/tests/test_analyze_sources.py
- [[.test_single_file()]] - code - _bmad/core/bmad-distillator/scripts/tests/test_analyze_sources.py
- [[.test_standalone_files()]] - code - _bmad/core/bmad-distillator/scripts/tests/test_analyze_sources.py
- [[.test_stdout_output()]] - code - _bmad/core/bmad-distillator/scripts/tests/test_analyze_sources.py
- [[Create a temp directory with sample files.]] - rationale - _bmad/core/bmad-distillator/scripts/tests/test_analyze_sources.py
- [[Detect document type from filename.]] - rationale - _bmad/core/bmad-distillator/scripts/analyze_sources.py
- [[Main analysis function.]] - rationale - _bmad/core/bmad-distillator/scripts/analyze_sources.py
- [[Resolve input arguments to a flat list of file paths.]] - rationale - _bmad/core/bmad-distillator/scripts/analyze_sources.py
- [[Suggest document groupings based on naming conventions.]] - rationale - _bmad/core/bmad-distillator/scripts/analyze_sources.py
- [[TestAnalyze]] - code - _bmad/core/bmad-distillator/scripts/tests/test_analyze_sources.py
- [[TestDetectDocType]] - code - _bmad/core/bmad-distillator/scripts/tests/test_analyze_sources.py
- [[TestResolveInputs]] - code - _bmad/core/bmad-distillator/scripts/tests/test_analyze_sources.py
- [[TestSuggestGroups]] - code - _bmad/core/bmad-distillator/scripts/tests/test_analyze_sources.py
- [[Tests for analyze_sources.py]] - rationale - _bmad/core/bmad-distillator/scripts/tests/test_analyze_sources.py
- [[Write JSON to file or stdout.]] - rationale - _bmad/core/bmad-distillator/scripts/analyze_sources.py
- [[analyze()]] - code - _bmad/core/bmad-distillator/scripts/analyze_sources.py
- [[analyze_sources.py]] - code - _bmad/core/bmad-distillator/scripts/analyze_sources.py
- [[detect_doc_type()]] - code - _bmad/core/bmad-distillator/scripts/analyze_sources.py
- [[main()]] - code - _bmad/core/bmad-distillator/scripts/analyze_sources.py
- [[output_json()]] - code - _bmad/core/bmad-distillator/scripts/analyze_sources.py
- [[resolve_inputs()]] - code - _bmad/core/bmad-distillator/scripts/analyze_sources.py
- [[suggest_groups()]] - code - _bmad/core/bmad-distillator/scripts/analyze_sources.py
- [[temp_dir()]] - code - _bmad/core/bmad-distillator/scripts/tests/test_analyze_sources.py
- [[test_analyze_sources.py]] - code - _bmad/core/bmad-distillator/scripts/tests/test_analyze_sources.py
- [[test_detection()]] - code - _bmad/core/bmad-distillator/scripts/tests/test_analyze_sources.py

## Live Query (requires Dataview plugin)

```dataview
TABLE source_file, type FROM #community/Source_Analysis_Tools
SORT file.name ASC
```
