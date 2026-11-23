# Sqrilizz-Reports

Network-ready player and bug reporting system for modern Paper, Purpur, and Folia servers.

[![Minecraft](https://img.shields.io/badge/minecraft-26.3-blue.svg)](https://papermc.io/)
[![Java](https://img.shields.io/badge/java-25-orange.svg)](https://openjdk.java.net/)
[![License](https://img.shields.io/badge/license-SCL-purple.svg)](LICENSE)

> [!IMPORTANT]
> **Project Status — Maintenance Only:**
> This project is no longer actively maintained or developed. Do not expect new features, pull request reviews, or major updates. The repository only runs automated monthly dependency bumps to newer PaperMC releases. If you need modifications or additional functionality, feel free to fork the repository.

## Features

- Fully asynchronous with native Folia region scheduler support
- Paginated moderation GUI with player heads and one-click actions (resolve, dismiss, teleport, punish)
- Integrated bug report system (`/bugreport`) with customizable categories
- SQLite (default) and MySQL storage backends powered by HikariCP connection pooling
- Discord Webhook and Telegram notification dispatch
- Cross-server moderation transfers with Velocity and BungeeCord proxy bridges
- Multilingual localization (English, Russian, Arabic)
- Optional lightweight REST API for external dashboards

## Requirements

- Minecraft: 26.3+
- Server software: Paper, Purpur, or Folia
- Java: 25 or higher

## Modules & Downloads

| Artifact | Platform | Description |
|---|---|---|
| `Sqrilizz-Reports-Standard-Edition-11.0.0.jar` | Paper / Folia | Lightweight build (~2 MB) using server-provided or runtime JDBC drivers |
| `Sqrilizz-Reports-Bot-Edition-11.0.0.jar` | Paper / Folia | Self-contained build bundling SQLite and MySQL JDBC drivers |
| `Sqrilizz-Reports-Velocity-11.0.0.jar` | Velocity | Proxy bridge forwarding staff cross-server actions |
| `Sqrilizz-Reports-Bungee-11.0.0.jar` | BungeeCord | Proxy bridge forwarding staff cross-server actions |

## Commands

### Players
- `/report <player> <reason>` — File a report against an online player
- `/bugreport <category> <description>` — Submit a bug report

### Staff
- `/reports` — Open the visual moderation queue
- `/reports open <id>` — Open details for a specific report
- `/reports resolve <id>` — Mark a report as resolved
- `/reports dismiss <id>` — Mark a report as invalid
- `/reports tp <player>` — Teleport to reported player
- `/reports punish <player> <warn|kick|mute|ban>` — Execute configured punishment preset
- `/reports stats` — Display queue statistics
- `/reports reload` — Reload configuration and language files

## Permissions

| Permission | Default | Description |
|---|---|---|
| `sqrilizzreports.report` | true | Allows players to use `/report` |
| `sqrilizzreports.bugreport` | true | Allows players to use `/bugreport` |
| `sqrilizzreports.admin` | op | Access to `/reports` and moderation notifications |
| `sqrilizzreports.punish` | op | Allows executing punishment actions from GUI |
| `sqrilizzreports.transfer` | op | Allows cross-server staff transfers via proxy |

## Configuration

See [`server/src/main/resources/config.yml`](server/src/main/resources/config.yml) for full configuration options.

```yaml
storage:
  type: sqlite
  sqlite-file: reports.db

reports:
  cooldown-seconds: 45
  duplicate-window-seconds: 900
  max-per-hour: 8
  max-per-target: 2

notifications:
  discord-webhook:
    enabled: false
    url: ""
  telegram:
    enabled: false
    token: ""
    chat-id: ""
```

## Building from Source

```bash
git clone https://github.com/Sqrilizz/Sqrilizz-Reports.git
cd Sqrilizz-Reports
./gradlew build
```

Compiled artifacts will be located in:
- `server/build/libs/`
- `proxy-velocity/build/libs/`
- `proxy-bungee/build/libs/`
