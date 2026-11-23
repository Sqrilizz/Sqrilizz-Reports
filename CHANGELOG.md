# Changelog

All notable changes to this project will be documented in this file.

## [11.0.0] - 2026-11-23

### Added
- Complete rewrite in Kotlin with modular architecture: `core`, `server`, `proxy-velocity`, and `proxy-bungee`.
- Native Folia region scheduler support for thread-safe asynchronous operations.
- HikariCP connection pooling for both SQLite and MySQL storage backends.
- Interactive paginated GUI queue with player heads, actions (resolve, dismiss, teleport, punish), and confirmation handling.
- Integrated `/bugreport` system with category routing and staff alerts.
- Velocity and BungeeCord proxy message handlers for cross-server moderator teleportation and report review.
- Lightweight Discord Webhooks and Telegram notifications without heavy third-party bot gateway daemons.

### Changed
- Promoted modular build to repository root and simplified project hierarchy.
- Updated target environment to Paper 26.3 and Java 25.
- Replaced custom JSON file storage driver with SQLite default.
- Removed legacy unmaintained JDA bot runtime and monolithic static managers.

## [9.5.1] - 2026-08-01

### Fixed
- Fixed invalid YAML quoting in the Arabic language file, allowing `/report-reload` to load `messages_ar.yml` successfully.

### Changed
- Standard Edition and Bot Edition artifacts are published as version 9.5.1.

## [9.5.0] - 2026-07-24

### Fixed
- bStats metrics not displaying: added `metrics: true` to plugin.yml (main cause)
- bStats config missing `globalSwitch: true`; enabled `logFailedRequests` for error visibility
- Fixed bstats badge typo in README

### Changed
- Bump version to 9.5.0

## [9.4.0] - 2026-07-18

### Added
- A separate Bot Edition with moderator buttons for resolving reports or marking them as not a bug / false report
- Discord report cards show Minecraft head thumbnails, a status field, the acting moderator, and disabled final controls
- Offline-player reports with the target location recorded as offline
- Persistent report statuses: open, in progress, resolved, not a bug, not a violation, false report, and closed
- Localized status labels and reports-menu statistics in English, Russian, and Arabic
- Reporter notifications for every final moderation outcome
- Telegram moderation notifications for report status changes
- Audit webhook events for report creation, status changes, and report cleanup
- Duplicate protection for recent open complaints from the same reporter to the same target
- Automatic migration of renamed configuration keys from older releases

### Fixed
- Reports menu now previews the newest report for each player
- Discord bot buttons acknowledge interactions before the server-side status change
- Discord report cards now show the moderator's final decision and disable the action controls
- JSON, SQLite, and MySQL stores now persist the final status, moderator, and resolution time consistently

### Changed
- The `gui.player-heads` setting now works for all report GUI cards
- The main build now targets Paper 26.2 and Java 25; support for Java 21 and Minecraft 1.21.x has been dropped
- Standard Edition and Bot Edition artifacts are both published as version 9.4.0

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
