# Changelog - Sqrilizz-Reports

## [8.0] - 2026-05-09

### Major Changes
- Replaced SQLite with JSON storage (18MB → 3.5MB)
- Updated to Paper API 26.1.2 and Java 25
- Added Kotlin 2.3.0 utilities
- Implemented full GUI pagination

### Added
- JSON storage driver with auto-save and backup
- Bug report system with 11 categories
- bStats metrics integration
- Thread-safe storage operations
- Kotlin utility classes:
  - ColorManager - Color management with hex support
  - CooldownManager - Cooldown system
  - LanguageManager - Multi-language support
  - NameUtils - Player name cleaning

### Changed
- Paper API: 1.21.11 → 26.1.2
- Java: 21 → 25
- Gradle: 8.14.2 → 9.5.0
- Shadow plugin: 8.1.1 → 9.3.1
- Gson: 2.10.1 → 2.11.0
- HikariCP: 5.1.0 → 6.2.1
- Caffeine: 3.1.8 → 3.2.0
- SQLite: now optional (3.45.3.0 → 3.47.2.0)
- Build system migrated to Kotlin DSL
- Default storage changed from SQLite to JSON

### Fixed
- GUI pagination navigation
- Kotlin-Java interop issues
- CooldownManager initialization

### Technical
- Improved JAR minimization
- Better dependency relocation
- Optimized build configuration
- Added version variables in build script

## [7.8] - 2026-03-27

### Added
- Interactive GUI system
- Punishment presets
- Pagination support

### Changed
- Paper API: 1.21.10 → 1.21.11
- Enhanced /reports command

## [7.6] - 2025-11-14

### Added
- Optional MySQL support
- Lightweight Telegram integration

### Changed
- Optimized dependencies (30MB → 17MB)

### Removed
- Unused commands
