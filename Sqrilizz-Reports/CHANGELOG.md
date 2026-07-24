# Changelog - Sqrilizz-Reports

## [9.5.0] - 2026-07-24

### Fixed
- bStats metrics not displaying: added `metrics: true` to plugin.yml (main cause)
- bStats config missing `globalSwitch: true`; enabled `logFailedRequests` for error visibility

## [9.4.0] - 2026-07-18

### Added
- Discord bot embeds now include a Minecraft head thumbnail: the reported player for a complaint and the reporter for a bug report
- Added localized labels for all report statuses and localized statistics in the reports menu
- Duplicate protection blocks a new complaint from the same reporter against the same player while a recent open report exists
- Telegram sends a moderation notification whenever a report status changes

### Fixed
- Player cards in the reports menu now show the newest report instead of an older entry
- Discord button interactions are acknowledged immediately, preventing timeout errors while the report is updated on the server thread
- Discord report cards are updated with the final status and moderator, then their controls are disabled

### Changed
- `gui.player-heads` now controls player heads in every GUI; when disabled, the menu uses paper cards without breaking their actions
- The main build now targets Paper 26.2 and Java 25; support for Java 21 and Minecraft 1.21.x has been dropped

## [9.3.0] - 2026-07-01

### Changed
- Verified and declared support for Minecraft 26.2 (new date-based versioning); sources compile cleanly against Paper API `26.2.build.40-alpha`
- Still compiled against Paper API 1.21.11 with Java 21 bytecode, so the same jar runs on 1.21+ servers (Java 21) and 26.x servers (Java 25) — Paper API 26.x itself requires Java 25 and would break 1.21 compatibility
- Updated dependencies: Gson 2.14.0, HikariCP 6.3.0, sqlite-jdbc 3.49.1.0
- `runServer` test target updated from 1.21.4 to 1.21.11

## [9.2] - 2026-06-19

### Added
- New action buttons in GUI: "Notify Player", "Mark Resolved", "Not a Bug / Not a Violation"
- Confirmation dialog before Resolved / Not a Bug actions
- Player notifications for all report actions
- Discord webhooks for notify, resolve, and not-a-bug actions
- Persist resolved reports (`status`, `resolvedBy`, `resolvedAt`) instead of deleting them
- History display in GUI: resolved reports are shown greyed out with a ✓ badge
- Explicit server software support notes: Paper, Purpur, Pufferfish, Spigot, and Folia
- Compatibility note for most Paper-compatible forks such as Leaves, Gale, and Canvas

### Fixed
- Bug reports now properly populate the `reportsById` index on load
- Fixed `openReportActionsGUI` showing `no-reports` instead of `report-not-found` when a report is missing
- Fixed confirmation GUI not being recognized as plugin GUI because of the missing `isOurGUI` check
- Fixed double Discord webhook send on resolve actions
- Fixed Gradle/plugin metadata to publish as version `9.2`
- Fixed GUI compatibility on legacy and modern versions by resolving renamed materials through `VersionUtils`
- Fixed player head owner handling on old servers by falling back from `setOwningPlayer` to legacy `setOwner`

### Changed
- Unified action button color scheme using configured GUI colors
- Resolved reports are no longer deleted; they are kept in the database with status
- Release target clarified: Java 21+, Minecraft compatibility target `1.8.9–9.2`
- Supported server software clarified: Paper, Purpur, Pufferfish, Spigot, and Folia
- Paper-compatible forks are documented as likely compatible, not officially tested

## [8.0] - 2026-05-09

### Major Changes
- Replaced SQLite with JSON storage (18MB → 3.5MB)
- Plugin version set to 9.2 with Java 21 target for compatibility
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
- Plugin version: 8.0 → 9.2
- Paper API kept on real available API: 1.21.11-R0.1-SNAPSHOT
- Java target kept at 21
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
