# Sqrilizz-Reports 9.5.0

[![Documentation](https://img.shields.io/badge/Docs-GitHub-black.svg?logo=github)](https://github.com/Sqrilizz/Sqrilizz-Reports)

Modern report management for Paper servers: GUI moderation, report history, Discord and Telegram notifications, anti-abuse protection, and multilingual messages.

## Which File Should I Download?

### `Sqrilizz-Reports-Standard-Edition-9.5.0.jar` — Standard Edition

Choose this if you need the report system, GUI actions, Discord webhooks, Telegram notifications, REST API, and databases. It is the recommended build for most servers.

- Smaller file: about 4.6 MB
- No Discord bot account or token required
- Use Discord webhooks if you only need report notifications in Discord

### `Sqrilizz-Reports-Bot-Edition-9.5.0.jar` — Bot Edition

Choose this only if moderators must manage reports from Discord with buttons.

- Includes everything in Standard Edition
- New report cards have `Resolved` and `Not a Bug` / `False Report` buttons
- Shows Minecraft head thumbnails and updates the card with the final status and moderator
- Requires a Discord bot token, channel ID, and moderator role IDs in `config.yml`
- Larger file: about 21 MB

> Install **only one** of these files. Do not put both JARs in the `plugins/` folder.

## Requirements

- **Minecraft:** 26.2+
- **Server:** Paper, Purpur, Pufferfish, or Folia
- **Java:** 25+

## Features

- Interactive reports GUI with player heads, pagination, statuses, and moderation actions
- Offline-player reports and persistent report history
- Reporter notifications for every final moderation outcome
- Discord webhooks, optional Discord bot controls, and Telegram notifications
- Discord bot cards with Minecraft avatars and disabled final controls
- Duplicate report protection, cooldowns, and rate limits
- Bug reports with categories
- JSON, MySQL, and SQLite storage options
- REST API and audit webhook events
- English, Russian, and Arabic messages
- Punishment presets: warn, kick, mute, and ban

## Quick Start

1. Download one JAR from the section above and place it in `plugins/`.
2. Start the server once to generate `config.yml`.
3. Configure a Discord webhook, Telegram, or the Discord bot only if needed.
4. Use `/report <player> <reason>` to create a report.
5. Use `/reports` to open the moderation GUI with `reports.admin`.

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
reports:
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
