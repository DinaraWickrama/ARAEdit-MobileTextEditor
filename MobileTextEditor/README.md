markdown
# ARAEdit — Mobile Text Editor (IS2205 Mini-Project)

A native Android text editor with live Kotlin and Markdown syntax highlighting,
crash-recovery autosave, and a delta-based (non-duplicating) version control
system.

## Tech stack
- Kotlin, Jetpack Compose (Material 3)
- Room (SQLite) for file/version metadata
- [java-diff-utils](https://github.com/java-diff-utils/java-diff-utils) for diff/patch generation and structured diff rows
- Kotlin Coroutines for autosave + async I/O
- Android Storage Access Framework (SAF) for Open/Save/Save As

## App flow
The app opens to a **Home screen** with two entry points:
- **+ New File** — prompts for a file type (Kotlin, Markdown, HTML, CSS, or plain text) up front, so highlighting is active from the very first keystroke, not only after saving.
- **Open File...** — opens the system file picker (any file type, no MIME filtering that could hide `.md`/`.kt` files).

Previously opened/saved files appear in **Recent Files** below, and tapping a name reopens that file directly (no picker needed) with its version history intact. Files can also be removed from Recent Files (with confirmation) without deleting the actual file from device storage.

## Project structure
```
app/src/main/java/com/example/mobiletexteditor/
|-- MainActivity.kt                 (navigation between Home / Editor / Version History)
|-- ui/
|   |-- HomeScreen.kt                (landing page: New File, Open File, Recent Files)
|   |-- EditorViewModel.kt           (central state holder wiring all subsystems)
|   |-- EditorScreen.kt              (editor UI: toolbar, search/replace, save dialogs)
|-- editor/
|   |-- FileManager.kt               (Open/New/Save/Save As via SAF)
|   |-- UndoRedoManager.kt           (in-memory, session-level undo/redo stack)
|-- autosave/
|   |-- AutosaveManager.kt           (10s periodic crash-recovery cache)
|-- highlighting/
|   |-- Keywords.kt                  (Kotlin keyword list, the "keyword file")
|   |-- KotlinHighlighter.kt         (tokenizer -> AnnotatedString spans)
|   |-- MarkdownHighlighter.kt       (headers, bold/italic, lists, code, links, blockquotes)
|-- data/
|   |-- FileEntity.kt / FileDao.kt          (Room: file metadata + recents)
|   |-- VersionEntity.kt / VersionDao.kt    (Room: version/delta history)
|   |-- AppDatabase.kt
|-- versioncontrol/
    |-- DeltaManager.kt              (core delta/version-control engine)
    |-- DiffModels.kt                (DiffLine / DiffLineType for structured comparison)
    |-- DiffViewScreen.kt            (version history + side-by-side diff UI)
```

## Live syntax highlighting
Highlighting is applied via a Jetpack Compose `VisualTransformation` attached
directly to the editor's text field — it re-runs on every keystroke and only
affects how text is *displayed*, never what's written to disk. This means
highlighting appears immediately as you type, before any save, and the saved
`.kt`/`.md` file is always plain text with no styling embedded.

- **Kotlin**: keywords (bold blue), function calls/declarations — identifiers followed by `(` (gold), string literals, comments, annotations.
- **Markdown**: headings, bold, italic, list items, inline code, fenced code blocks, links, blockquotes.

## How the delta version control works
1. The first time a file is opened/created, its full text is stored once as
   **version 0** (`DeltaManager.createBaseVersion`).
2. Every subsequent save (optionally named via a prompt dialog) diffs the
   current buffer against the reconstructed previous version using
   `java-diff-utils`, storing **only the unified diff patch** — not the full
   file (`DeltaManager.saveNewVersion`).
3. To view or restore any version, the engine starts at v0 and replays
   patches forward (`DeltaManager.reconstructVersion`).
4. `DiffViewScreen` lets the user pick two versions and see a **structured,
   two-column diff** (via `java-diff-utils`' `DiffRowGenerator`) — equal
   lines shown plainly, added lines shaded green, deleted lines shaded red,
   changed lines shown red/green side-by-side on the same row — and restore
   ("rollback") to any prior state without deleting later history.

Files can also be explicitly locked **read-only** to prevent accidental edits.

Undo/redo (`UndoRedoManager`) is intentionally separate: an in-memory,
per-session stack for granular edits, cleared when the file closes — not
part of the persisted version history.

## Room database schema
`VersionEntity` matches the assignment's specified structure directly:

| Column | Purpose |
|---|---|
| `id` | Primary key, auto-generated |
| `fileId` | Foreign key to the file (cascade delete) |
| `versionNumber` | 0 = base snapshot, increments per save |
| `delta` | Full text (v0) or unified diff patch (v>0) |
| `versionName` | Optional user-supplied label |
| `timestamp` | When the version was created |

Viewable live during development via Android Studio's **Database Inspector**
(View → Tool Windows → App Inspection).

## Local storage
Version history, crash-recovery data, and app metadata are always stored
locally (Room database + app-private internal storage) with no network
calls anywhere in the app. The edited file's own location is chosen by the
user via the system file picker (SAF) — local by default (internal storage,
Downloads), unless the user actively selects a cloud-backed provider.

## Setup
1. Open the project root in Android Studio (Koala/2024.1+ recommended).
2. Let Gradle sync — dependencies (Compose, Room, java-diff-utils) are
   declared in `app/build.gradle.kts`.
3. Run on an emulator or device (minSdk 24).

## Known limitations / next steps
- Markdown *preview* panel (rendering to styled HTML) is not implemented —
  only in-editor Markdown highlighting is.
- Kotlin code auto-formatting (optional requirement) is not included.