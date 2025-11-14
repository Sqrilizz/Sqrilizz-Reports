# Changelog

All notable changes to this project will be documented in this file.

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
