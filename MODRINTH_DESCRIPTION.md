# Sqrilizz-Reports
[![Documentation](https://img.shields.io/badge/Docs-GitHub-black.svg?logo=github)](https://github.com/Sqrilizz/Sqrilizz-Reports) 

Modern report management system for Minecraft servers with GUI interface and multi-language support.

## Requirements

- **Minecraft:** 1.8.9 - 26.1.2
- **Server:** Paper, Purpur, Pufferfish, Folia, or Spigot
- **Java:** 25+

## Features

- Interactive GUI for managing reports
- Multi-language support (English, Russian, Arabic)
- Bug report system with categories
- Anti-abuse protection with rate limiting
- Optional MySQL/SQLite support
- Discord and Telegram integration
- REST API for external tools
- Punishment presets (warn, kick, mute, ban)
- Full pagination support


## Quick Start

1. Download and place JAR in `plugins/` folder
2. Restart server
3. Use `/report <player> <reason>` to create reports
4. Use `/reports` to open GUI (requires `reports.admin` permission)

## Commands

**Players:**
- `/report <player> <reason>` - Report a player
- `/bugreport <category> <description>` - Report a bug

**Admins:**
- `/reports` - Open reports GUI
- `/report-reload` - Reload configuration
- `/report-language <en|ru|ar>` - Change language

## Configuration

```yaml
language: en
cooldown: 60
database:
  type: json
```

## Permissions

- `reports.admin` - Access to reports management
- `reports.bypass` - Bypass cooldowns
- `reports.reload` - Reload configuration
- `reports.language` - Change language

## Links

- [GitHub Repository](https://github.com/Sqrilizz/Sqrilizz-Reports)
- [Full Documentation](https://github.com/Sqrilizz/Sqrilizz-Reports#readme)
- [Issue Tracker](https://github.com/Sqrilizz/Sqrilizz-Reports/issues)

## Support

For detailed documentation, configuration examples, and troubleshooting, visit the GitHub repository.
