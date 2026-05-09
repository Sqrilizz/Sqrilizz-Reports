# Changelog

All notable changes to this project will be documented in this file.

## [8.0] - 2026-05-09

### Added
- JSON storage system as default (lightweight, no dependencies)
- Bug report system with 11 predefined categories
- Full GUI pagination for reports list and player reports
- bStats metrics integration
- Kotlin 2.3.0 utility classes (ColorManager, CooldownManager, LanguageManager, NameUtils)
- Auto-save and backup system for JSON storage
- Thread-safe storage operations with ReadWriteLock

### Changed
- Updated Paper API from 1.21.11 to 26.1.2
- Updated Java requirement from 21 to 25
- Updated Gradle from 8.14.2 to 9.5.0
- Updated Shadow plugin from 8.1.1 to 9.3.1
- Migrated build system to Kotlin DSL (build.gradle.kts)
- Replaced SQLite with JSON as default storage (18MB → 3.5MB plugin size)
- Updated dependencies:
  - Gson: 2.10.1 → 2.11.0
  - SQLite: 3.45.3.0 → 3.47.2.0 (optional)
  - HikariCP: 5.1.0 → 6.2.1
  - Caffeine: 3.1.8 → 3.2.0
- Improved JAR optimization with minimize and better relocate
- SQLite is now optional (requires manual dependency addition)

### Fixed
- GUI pagination navigation (Next/Previous buttons)
- Kotlin-Java interop compilation issues
- CooldownManager initialization

### Removed
- SQLite JDBC as default dependency (now optional)

## [7.8] - 2026-03-27

### Added
- Interactive GUI Menu for managing reports
  - Main reports list with player heads and report counts
  - Detailed report view with all information
  - Quick action menu: teleport to players, punish, resolve, or delete reports
  - Punishment menu with preset options (warn, kick, mute, ban)
  - Full pagination support for large report lists
  - Right-click to quickly clear all reports for a player

### Changed
- Updated Paper API from 1.21.10 to 1.21.11
- Enhanced /reports command to open GUI by default

## [7.6] - 2025-11-14

### Added
- Optional MySQL support (SQLite by default)
- Lightweight Telegram integration with HTTP client

### Changed
- Optimized dependencies (30MB → 17MB)
- Updated documentation

### Removed
- Unused commands: /report-discord, /report-health, /report-stats

### Fixed
- Build compatibility issues with Java versions
