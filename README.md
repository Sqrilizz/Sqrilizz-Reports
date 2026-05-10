# Sqrilizz-Reports

Modern report management system for Minecraft servers with interactive GUI interface.

Available in: [English](README.md) | [Russian](README-RU.md) | [Arabic](README-AR.md)

## Key Features

- Interactive GUI for report management with player heads and punishment presets
- Multi-language support (English, Russian, Arabic) with hot reload
- Bug report system with 11 predefined categories
- Lightweight JSON storage by default (optional SQLite/MySQL)
- Anti-abuse system with rate limiting and auto-punishment
- Integrations: Discord webhooks, Telegram bot, REST API

## Requirements

- Minecraft 1.8.9 - 26.1.2
- Paper, Purpur, Pufferfish, Folia, or Spigot
- Java 25 or higher

## Installation

1. Download the latest JAR from [Releases](https://github.com/Sqrilizz/Sqrilizz-Reports/releases).
2. Place it in your server's `plugins/` folder.
3. Restart the server — the plugin works out of the box.

## Quick Start

**For Players:**
```
/report <player> <reason>              - Report a player
/bugreport <category> <description>    - Report a bug
```

**For Admins:**
```
/reports                               - Open reports GUI
/report-reload                         - Reload configuration
/report-language <en|ru|ar>            - Change language
```

## Permissions

```yaml
reports.admin      # Access to reports management
reports.bypass     # Bypass cooldowns
reports.reload     # Reload configuration
reports.language   # Change language
```

## Bug Report Categories

Available categories for `/bugreport`:
`duplication`, `crash`, `exploit`, `performance`, `gameplay`, `world`,
`inventory`, `commands`, `permissions`, `economy`, `other`.

Example:
```
/bugreport dupe Players can duplicate diamonds using chest
```

## Configuration

Basic `config.yml`:

```yaml
# Language (en, ru, ar)
language: en

# Cooldown between reports (seconds)
cooldown: 60

# Storage type: json (default), sqlite, mysql
database:
  type: json
```

## Building from Source

Requires Java 25+ and Gradle 9.5+.

```bash
git clone https://github.com/Sqrilizz/Sqrilizz-Reports.git
cd Sqrilizz-Reports/Sqrilizz-Reports
./gradlew build
```

## License

Licensed under the Sqrilizz Custom License (SCL) — see [LICENSE](LICENSE).

## Support

- [GitHub Issues](https://github.com/Sqrilizz/Sqrilizz-Reports/issues)
- [Modrinth](https://modrinth.com/plugin/sqrilizz-report)
- Website: https://sqrilizz.xyz
