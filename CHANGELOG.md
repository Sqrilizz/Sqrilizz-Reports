# Changelog

All notable changes to this project will be documented in this file.

## [7.8] - 2026.27.03

### Added
- **Interactive GUI Menu**: Added comprehensive GUI system for managing reports
  - Main reports list with player heads and report counts
  - Detailed report view with all information
  - Quick action menu: teleport to players, punish, resolve, or delete reports
  - Punishment menu with preset options (warn, kick, mute, ban)
  - Full pagination support for large report lists
  - Right-click to quickly clear all reports for a player

### Changed
- **Updated Minecraft API**: Updated Paper API from 1.21.10 to 1.21.11 for latest compatibility. ([#5](https://github.com/Sqrilizz/Sqrilizz-Reports/issues/5))
- **Enhanced /reports Command**: Now opens GUI by default, use `/reports list` for text-based view

Note ``sorry, i was a busy with school and i forgot to update my plugins)``

## [7.6] - 2025-11-14

### Added
- **Optional MySQL Support**: The plugin now uses SQLite by default. MySQL support can be enabled by placing the MySQL JDBC driver in the `lib` folder.
- **Lightweight Telegram Integration**: Replaced the heavy Telegram Bot API with a lightweight HTTP client, significantly reducing the plugin's file size.

### Changed
- **Optimized Dependencies**: Removed and optimized heavy dependencies to reduce the final JAR size from ~30MB to ~17MB.
- **Updated Documentation**: All README files and the Modrinth description have been updated to reflect the latest changes.

### Removed
- **Unused Commands**: Removed the following unused commands to clean up the plugin:
  - `/report-discord`
  - `/report-health`
  - `/report-stats`

### Fixed
- **Build Compatibility**: Resolved build issues related to Java versions and dependency conflicts.
