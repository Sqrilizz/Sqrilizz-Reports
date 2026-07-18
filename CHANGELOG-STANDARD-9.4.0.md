# Sqrilizz-Reports 9.4.0 - Standard Build

## Requirements

- Minecraft/Paper 26.2+
- Java 25+

## Added

- Offline-player reports
- Persistent report statuses and moderation history
- Reporter notifications for resolved, not-a-bug, not-a-violation, false-report, and closed outcomes
- Duplicate protection for recent open reports from the same reporter to the same target
- Telegram moderation notifications when a report status changes
- Audit webhook events for report creation, status changes, and report cleanup
- Automatic migration for renamed configuration keys
- Localized status labels and report-menu statistics in English, Russian, and Arabic

## Improved

- Reports menu previews the newest report for each player
- Player-head cards are controlled by the `gui.player-heads` setting in every GUI
- JSON, SQLite, and MySQL persist the status, moderator, and resolution time consistently

## Artifact

Use `Sqrilizz-Reports-9.4.0.jar`.
