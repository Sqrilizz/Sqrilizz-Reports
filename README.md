# Sqrilizz-Reports

[![Version](https://img.shields.io/badge/version-9.2-brightgreen.svg)](https://github.com/Sqrilizz/Sqrilizz-Reports/releases)
[![Minecraft](https://img.shields.io/badge/minecraft-1.8--9.2-blue.svg)](https://www.minecraft.net/)
[![Java](https://img.shields.io/badge/java-21-orange.svg)](https://openjdk.java.net/)
[![License](https://img.shields.io/badge/license-Custom-red.svg)](LICENSE)
[![Modrinth](https://img.shields.io/modrinth/dt/sqrilizz-report?color=00AF5C&logo=modrinth)](https://modrinth.com/plugin/sqrilizz-report)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/1a1d82daee3f49ed9677fcbb5fa594df)](https://app.codacy.com/gh/Sqrilizz/Sqrilizz-Reports/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)

Modern report management system for Minecraft servers with interactive GUI interface.

---

## Language Versions

| Language | File | Description |
|----------|------|-------------|
| English | [README.md](#) | Default version |
| Russian | [README-RU.md](README-RU.md) | Russian version |
| Arabic | [README-AR.md](README-AR.md) | Arabic version |

---

## Key Features

### Interactive GUI Menu
- Visual report management with player heads
- One-click actions: teleport, punish, resolve
- Punishment presets: warn, kick, mute, ban
- Smart pagination and navigation
- Multi-language support (EN/RU/AR)

### Modern Design
- Hex colors for Minecraft 1.16+ with legacy fallback
- Customizable color themes
- Clean and intuitive interface

### Multi-Language Support
- 3 languages: English, Russian, Arabic
- RTL support for Arabic
- Separate language files for easy customization
- Hot reload with `/report-reload`

### Lightweight Storage
- JSON by default - only 3.5MB plugin size
- No native dependencies - pure Java
- Auto-save and backup system
- Optional SQLite/MySQL support

### Anti-Abuse System
- Rate limiting per player and hourly
- False report detection
- Auto-punishment for abuse
- Smart cooldown system

### Bug Report System
- 11 predefined categories
- Category validation and suggestions
- Multi-language category names
- Integration with notifications

### Integration Support
- Discord webhooks
- Telegram bot
- REST API for external tools
- Custom webhook system

---

## Requirements

- **Minecraft**: 1.8.9 - 9.2
- **Server**: Paper, Purpur, Pufferfish, Folia, or Spigot
- **Java**: 21 or higher

**Recommended:** Paper or Purpur for best performance and features

---

## Installation

1. Download the latest JAR from [Releases](https://github.com/Sqrilizz/Sqrilizz-Reports/releases)
2. Place in `plugins/` folder
3. Restart server
4. Configure `config.yml` if needed
5. Done! Plugin works out of the box

---

## Quick Start

### Basic Commands

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

### Permissions

```yaml
reports.admin      # Access to reports management
reports.bypass     # Bypass cooldowns
reports.reload     # Reload configuration
reports.language   # Change language
```

---

## Configuration

### Basic Setup

```yaml
# Language (en, ru, ar)
language: en

# Cooldown between reports (seconds)
cooldown: 60

# Storage type
database:
  type: json
```

### Storage Options

**JSON (default)** - Recommended for most servers
- Lightweight (3.5MB plugin size)
- No dependencies required
- Auto-save and backup
- Easy to read and edit

**MySQL** - For large servers
```yaml
database:
  type: mysql
  mysql:
    host: "localhost"
    port: 3306
    database: "sqrilizz_reports"
    user: "root"
    password: "password"
```

**SQLite** - Optional (requires dependency)
```yaml
database:
  type: sqlite
```
Note: Requires adding `sqlite-jdbc` dependency (+8MB)

### Color Customization

```yaml
design:
  use-hex-colors: true
  colors:
    primary: "#FF6B6B"
    secondary: "#4ECDC4"
    success: "#45B7D1"
    warning: "#FFA726"
    error: "#EF5350"
    info: "#66BB6A"
    accent: "#AB47BC"
```

---

## Bug Report Categories

Available categories for `/bugreport`:

- `duplication` / `dupe` - Item duplication
- `crash` / `server-crash` - Server crashes
- `exploit` / `glitch` - Exploits and glitches
- `performance` / `lag` - Performance issues
- `gameplay` / `mechanic` - Gameplay mechanics
- `world` / `generation` - World generation
- `inventory` / `items` - Inventory issues
- `commands` / `cmd` - Command problems
- `permissions` / `perms` - Permission issues
- `economy` / `money` - Economy problems
- `other` / `misc` - Other issues

Example:
```
/bugreport dupe Players can duplicate diamonds using chest
```

---

## Integration

### Discord Webhook

```yaml
discord:
  enabled: true
  webhook_url: "https://discord.com/api/webhooks/..."
```

### Telegram Bot

```yaml
telegram:
  enabled: true
  token: "YOUR_BOT_TOKEN"
  chat_id: "YOUR_CHAT_ID"
```

### REST API

```yaml
rest-api:
  enabled: true
  port: 8971
  token: "your_secret_token"
```

Endpoints:
- `GET /api/reports` - List all reports
- `GET /api/stats` - Get statistics
- `POST /api/webhook` - Webhook for external systems

---

## Anti-Abuse Configuration

```yaml
anti-abuse:
  enabled: true
  warning-threshold: 5
  temp-mute-threshold: 8
  temp-mute-duration: 300

report-limits:
  per-player: 3
  per-hour: 10
```

---

## Documentation

Detailed documentation available in `/docs` folder:

- [Installation Guide](docs/INSTALLATION.md)
- [Configuration](docs/CONFIGURATION.md)
- [Database Setup](docs/DATABASE.md)
- [Anti-Abuse System](docs/ANTI_ABUSE.md)
- [REST API](docs/REST_API.md)
- [Discord Bot](docs/DISCORD_BOT.md)
- [Telegram Integration](docs/TELEGRAM.md)
- [Design Customization](docs/DESIGN.md)
- [API for Developers](docs/API.md)

---

## Building from Source

Requirements:
- Java 21+
- Gradle 9.5.0+

```bash
git clone https://github.com/Sqrilizz/Sqrilizz-Reports.git
cd Sqrilizz-Reports/Sqrilizz-Reports
./gradlew build
```

JAR will be in `build/libs/Sqrilizz-Reports-9.2.jar`

---

## Changelog

### Version 9.2
- Plugin version set to 9.2
- Build targets Java 21 for server compatibility
- Added Kotlin 2.3.0 utilities
- Replaced SQLite with lightweight JSON storage (18MB → 3.5MB)
- Implemented full GUI pagination
- Added bug report system with categories
- Updated all dependencies
- Added bStats integration
- Improved performance and stability

See [CHANGELOG.md](CHANGELOG.md) for full history.

---
![https://bstats.org/plugin/bukkit/Sqrilizz%20Reports/31222.svg](https://bstats.org/plugin/bukkit/Sqrilizz%20Reports/31222.svg)

## Support

- **GitHub Issues**: [Report bugs or request features](https://github.com/Sqrilizz/Sqrilizz-Reports/issues)
- **Modrinth**: [Plugin page](https://modrinth.com/plugin/sqrilizz-report)
- **Discord**: [Support server](#)

---

## License

This project is licensed under the Sqrilizz Custom License (SCL) - see [LICENSE](LICENSE) file for details.

### Key Points:
- Free to use on personal and public servers (including monetized)
- Free to modify for private use
- Cannot sell or redistribute without permission
- Attribution required

For commercial use or redistribution, contact: https://sqrilizz.xyz

---

## Credits

Developed by Sqrilizz

Sqrilizz Entertainment / AuryxStudio

Website: https://sqrilizz.xyz

Special thanks to all contributors and users who provided feedback and suggestions.
