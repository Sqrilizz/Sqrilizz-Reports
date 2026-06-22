# Changelog

All notable changes to this project will be documented in this file.

## [9.2.1] - 2026-06-22

### Fixed
- Bug reports no longer blocked by the per-player complaint limit (3 of 3) — the limit applies only to player reports, not bug reports
- Corrected config key mismatch in limit messages: `report-limits.*` → `anti-abuse.*`, so the displayed limit now matches the actual value from `config.yml`

## [9.2] - 2026-06-19

### Added
- New action buttons in GUI: "Notify Player", "Mark Resolved", "Not a Bug / Not a Violation"
- Confirmation dialog before Resolved / Not a Bug actions
- Player notifications for all report actions
- Discord webhooks for notify, resolve, and not-a-bug actions
- Persist resolved reports (status + resolvedBy + resolvedAt) instead of deleting
- History display in GUI (resolved reports shown greyed out with ✓ badge)
- GitHub Actions build & release workflow
- Explicit server software support notes: Paper, Purpur, Pufferfish, Spigot, and Folia
- Compatibility note for most Paper-compatible forks such as Leaves, Gale, and Canvas

### Fixed
- Bug reports now properly populate reportsById index on load
- Fixed `openReportActionsGUI` showing "no-reports" instead of "report-not-found" when report is null
- Fixed confirmation GUI not being recognized as plugin GUI (missing isOurGUI check)
- Fixed double Discord webhook on resolve actions
- Updated version badges in README files
- Fixed Gradle/plugin metadata to publish as version 9.2
- Fixed GUI compatibility on legacy and modern versions by resolving renamed materials through `VersionUtils`
- Fixed player head owner handling on old servers by falling back from `setOwningPlayer` to legacy `setOwner`

### Changed
- Unified color scheme for action buttons using config colors
- Resolved reports no longer deleted, kept in database with status
- Release target clarified: Java 21+, Minecraft compatibility target 1.8.9–9.2

## [9.1] - 2026-05-15

### Fixed
- bStats plugin ID corrected from 24619 to 31222

## [9.0] - 2026-05-14

### Added
- DebugManager with toggleable debug mode (default: off)
- `/report-debug` command to toggle debug logging
- Debug logging in GUI listener (click events, menu detection, button matching)
- Debug logging in PunishmentManager (punishment actions, system detection)
- Separate bug report format for Telegram and Discord (category instead of target)

### Fixed
- GUI button matching now case-insensitive (was breaking buttons like "TP to Reporter" vs "reporter")
- Punishment menu title parsing (extract player name between `] ` and ` #` instead of splitting by `-`)
- Player reports target name extraction (strips trailing `]` from title format)
- Hardcoded Russian "Назад" button in punishment handler (now uses isButton with all language variants)

### Changed
- Bug report notifications now use dedicated format (Reporter, Category, Description, Time, Location)
- Admin bug report notification format simplified (shorter field names)
- Bug report Discord embed color changed to orange (0xFFA500) to distinguish from regular reports

## [9.2] - 2026-05-14 | Test

### Added
- Discord webhook notification when a report is resolved (green embed with resolver info)

### Changed
- Plugin version updated to 9.2
- Universal version detection: supports any Minecraft version from 1.8 to 26.x+ (and future versions)
- Version parser now uses regex instead of hardcoded checks, with Bukkit fallback

### Fixed
- Discord webhook config path bug (`discord.webhook_url` → `discord.webhook.url` matching config.yml)
- Discord webhook now respects `discord.webhook.enabled` config flag
- Discord webhook ignores placeholder `YOUR_WEBHOOK_URL` values
- `setWebhookUrl()` now saves to both nested and legacy config paths for compatibility
- Version detection no longer falls to "unknown" for versions beyond 1.21

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
- Updated Paper API from 1.21.11 to 9.2
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
