# 🚨 Sqrilizz-Reports

[![Version](https://img.shields.io/badge/version-7.8-brightgreen.svg)](https://github.com/Sqrilizz/Sqrilizz-Reports/releases)
[![Minecraft](https://img.shields.io/badge/minecraft-1.8--1.21.11-blue.svg)](https://www.minecraft.net/)
[![Java](https://img.shields.io/badge/java-21-orange.svg)](https://openjdk.java.net/)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)
[![Modrinth](https://img.shields.io/modrinth/dt/sqrilizz-report?color=00AF5C&logo=modrinth)](https://modrinth.com/plugin/sqrilizz-report)

> **A lightweight, high-performance reports plugin for Minecraft servers with interactive GUI**

Modern report management system with intuitive GUI interface, multi-language support, and powerful admin tools for efficient server moderation.

---

## 🌍 Language Versions

| Language | File | Description |
|----------|------|-------------|
| 🇺🇸 **English** | **[README.md](#)** | **Default version** |
| 🇷🇺 **Русский** | **[README-RU.md](README-RU.md)** | Russian version |
| 🇸🇦 **العربية** | **[README-AR.md](README-AR.md)** | Arabic version |

---

## ✨ Key Features

### 🎮 **Interactive GUI Menu** (v7.6+)
- **Visual report management** - Browse reports with player heads
- **One-click actions** - Teleport, punish, resolve instantly
- **Punishment presets** - Warn, kick, mute (1h/1d), ban (1d/7d/perm)
- **Smart navigation** - Pagination, back buttons, smooth transitions
- **Multi-language** - All menus support EN/RU/AR

### 🎨 **Modern Design**
- **Hex colors** for Minecraft 1.16+ with legacy fallback
- **Emoji integration** for better UX
- **Customizable themes** with 7 color palettes
- **Clean interface** with intuitive controls

### 🌍 **Multi-Language**
- **3 Languages**: English, Russian, Arabic
- **RTL support** for Arabic
- **Separate language files** for easy customization
- **Hot reload** with `/report-reload`

### 🛡️ **Anti-Abuse System**
- **Rate limiting** (per-player & hourly)
- **False report detection**
- **Auto-punishment** for abuse
- **Smart cooldowns**

### 🔗 **Integrations**
- **Telegram** notifications
- **Discord** webhooks
- **REST API** for external tools
- **Developer API** for plugins

---

## 🚀 Quick Start

### Installation
1. Download latest version from [Modrinth](https://modrinth.com/plugin/sqrilizz-report)
2. Place JAR in `plugins/` folder
3. Restart server
4. Configure `config.yml` (optional)
5. Done! Use `/reports` to open GUI

### First Steps
```bash
# Report a player
/report PlayerName Reason for report

# Open reports GUI (admin)
/reports

# Check specific player
/reports check PlayerName
```

---

## 📋 Commands

### For Players
- `/report <player> <reason>` - Report a player

### For Admins
- `/reports` - Open interactive GUI menu
- `/reports list` - View reports in chat
- `/reports check <player>` - Check player's reports
- `/reports clear <player>` - Clear player's reports
- `/reports clearall` - Clear all reports
- `/report-reload` - Reload configuration

### Configuration
- `/report-language <en|ru|ar>` - Change language
- `/report-telegram <token|chat> <value>` - Setup Telegram
- `/report-webhook <set|remove> [url]` - Setup webhooks

---

## 🎮 GUI Features

### Main Menu (`/reports`)
- View all reported players with heads
- See report count for each player
- Left-click to view player's reports
- Right-click to clear all reports

### Player Reports Menu
- List all reports for specific player
- Teleport to reported player
- Open actions for each report
- Clear all reports button

### Report Actions Menu
- Teleport to reporter location
- Teleport to target location
- Open punishment menu
- Resolve report (deletes it)
- Delete report without action

### Punishment Menu
- **Warning** - Give warning
- **Kick** - Kick from server
- **Mute 1h/1d** - Temporary mute
- **Ban 1d/7d** - Temporary ban
- **Permanent ban** - Permanent ban
- After punishment: choose to close report or keep it

---

## ⚙️ Configuration

### Basic Settings
```yaml
language: en                    # en, ru, ar
anonymous-reports: false        # Hide reporter name
cooldown: 60                    # Seconds between reports

report-limits:
  per-player: 3                 # Max reports per player
  per-hour: 10                  # Max reports per hour
```

### Design
```yaml
design:
  use-hex-colors: true          # Modern colors (1.16+)
  colors:
    primary: "#FF6B6B"
    secondary: "#4ECDC4"
    success: "#45B7D1"
```

### Database
```yaml
database:
  type: sqlite                  # sqlite or mysql
  # MySQL optional - requires driver in lib/ folder
```

---

## 🔑 Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `reports.report` | Use `/report` | Everyone |
| `reports.admin` | Admin commands & GUI | OP |
| `reports.language` | Change language | OP |
| `reports.telegram` | Configure Telegram | OP |
| `reports.reload` | Reload config | OP |

---

## 🌐 Platform Support

**Minecraft**: 1.8.9 - 1.21.11 (all versions)  
**Server**: Paper (recommended), Spigot, Folia  
**Java**: 21+ required

---

## 📚 Documentation

- 🎮 [GUI Guide](GUI_GUIDE.md) - Complete GUI usage guide
- 📱 [Telegram Setup](docs/TELEGRAM.md) - Bot integration
- 🔗 [REST API](docs/REST_API.md) - API reference
- 🛡️ [Anti-Abuse](docs/ANTI_ABUSE.md) - Protection system
- ⚙️ [Configuration](docs/CONFIGURATION.md) - Full config guide
- 💾 [Database](docs/DATABASE.md) - Database setup

---

## 🆘 Support

**Found a bug?** [Report it](https://github.com/Sqrilizz/Sqrilizz-Reports/issues)  
**Need help?** Check [documentation](https://github.com/Sqrilizz/Sqrilizz-Reports/wiki)  
**Have an idea?** [Suggest a feature](https://github.com/Sqrilizz/Sqrilizz-Reports/issues/new)

---

## 📄 License

MIT License - see [LICENSE](LICENSE) file

---

<div align="center">

**Made with ❤️ by [Sqrilizz](https://modrinth.com/user/Sqrilizz)**

[![GitHub](https://img.shields.io/badge/GitHub-Sqrilizz-black.svg?logo=github)](https://github.com/Sqrilizz)
[![Modrinth](https://img.shields.io/badge/Modrinth-Sqrilizz-00AF5C.svg?logo=modrinth)](https://modrinth.com/user/Sqrilizz)
[![Website](https://img.shields.io/badge/Website-sqrilizz.xyz-blue.svg)](https://sqrilizz.xyz)

</div>
