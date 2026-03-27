# **🚀 Sqrilizz-Reports v7.8 - Interactive GUI Edition**
A professional, high-performance reports management system for Minecraft servers with intuitive GUI interface and powerful moderation tools.

[![Follow on Telegram](https://img.shields.io/badge/Telegram-Follow_for_Updates-0088cc.svg?logo=telegram)](https://t.me/Matve1mok1)
[![Documentation](https://img.shields.io/badge/Docs-GitHub-black.svg?logo=github)](https://github.com/Sqrilizz/Sqrilizz-Reports)

![Minecraft Version](https://img.shields.io/badge/Minecraft-1.8.9--1.21.11-green.svg)
[![Server Type](https://img.shields.io/badge/Server-Paper%20%7C%20Spigot%20%7C%20Folia-blue.svg)](https://modrinth.com/plugin/sqrilizz-report)
[![Java Version](https://img.shields.io/badge/Java-21+-orange.svg)](https://modrinth.com/plugin/sqrilizz-report)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](https://github.com/Sqrilizz/Sqrilizz-Reports/blob/main/LICENSE)

---

## 🎮 **NEW in v7.8: Interactive GUI System**

Manage reports visually with an intuitive interface:

✨ **Visual Report Browser** - Browse reports with player heads  
⚡ **One-Click Actions** - Teleport, punish, resolve instantly  
🎯 **Punishment Presets** - Warn, kick, mute, ban with one click  
🔄 **Smart Navigation** - Pagination, back buttons, smooth transitions  
🌍 **Multi-Language GUI** - Full support for EN/RU/AR in all menus  
📱 **Mobile-Friendly** - Works great on any screen size  

---

## 📋 **What This Plugin Does**

Sqrilizz-Reports provides server administrators with a complete player reporting system:

- **Interactive GUI** - Visual report management with player heads and quick actions
- **Player Reporting** - Players report violations with `/report <player> <reason>`
- **Admin Tools** - Review, punish, and resolve reports through GUI or commands
- **Multi-Platform Notifications** - Discord webhooks, Telegram integration
- **Anti-Abuse Protection** - Rate limiting and false report detection
- **REST API** - External integration for web panels and tools
- **Multi-Language** - English, Russian, and Arabic translations

---

## ✨ **Core Features**

### 🎮 **Interactive GUI System**
- **Main Menu** - View all reported players with heads
- **Player Reports** - See all reports for specific player
- **Actions Menu** - Teleport to reporter/target, punish, resolve
- **Punishment Menu** - Warn, kick, mute (1h/1d), ban (1d/7d/perm)
- **Confirmation** - Choose to close report after punishment
- **Smooth Transitions** - Fluid navigation between menus

### 🎨 **Beautiful Design**
- **Hex color support** for Minecraft 1.16+ with legacy fallbacks
- **Modern emoji integration** for enhanced UX
- **Customizable color palette** with 7 predefined themes
- **Clean interface** with intuitive controls

### �️ **Advanced Anti-Abuse**
- **Rate limiting**: Per-player and hourly limits
- **False report detection** with automatic credibility adjustment
- **Smart cooldowns** based on player behavior
- **Automatic cleanup** of old data

### 🌍 **Multi-Language Support**
- **3 Languages**: English, Russian, Arabic
- **RTL support** for Arabic text
- **Separate language files** for easy customization
- **Hot reload** with `/report-reload`

### � **Integrations**
- **Telegram** - Instant notifications to your bot
- **Discord** - Webhook support for channels
- **REST API** - Token auth, IP whitelist, HMAC
- **Developer API** - Events and methods for plugins

---

## 🔧 **Commands Overview**

### � **Player Commands**
```
/report <player> <reason>       — Report a player for violations
```

### �️ **Admin Commands**
```
/reports                        — Open interactive GUI menu
/reports list                   — View reports in chat
/reports check <player>         — Check reports for specific player
/reports clear <player>         — Clear reports for a player
/reports clearall               — Clear all reports
/report-reload                  — Reload plugin configuration
```

### 📱 **Integration Commands**
```
/report-language <en|ru|ar>     — Change server language
/report-telegram token <token>  — Set Telegram bot token
/report-telegram chat <id>      — Set Telegram chat ID
/report-webhook set <url>       — Set Discord webhook URL
/report-webhook remove          — Remove webhook
```

---

## 🎮 **GUI Usage Guide**

### Main Menu (`/reports`)
1. See all players with active reports (player heads)
2. **Left-click** player head → View their reports
3. **Right-click** player head → Clear all reports for player
4. Use pagination arrows for more players

### Player Reports Menu
1. View all reports for selected player
2. Click report item → Open actions menu
3. Teleport button → TP to reported player
4. Clear all button → Remove all reports

### Report Actions Menu
1. **TP to Reporter** → Teleport to who reported
2. **TP to Target** → Teleport to reported player
3. **Punish** → Open punishment menu
4. **Resolve** → Mark as resolved (deletes report)
5. **Delete** → Remove without action

### Punishment Menu
1. Select punishment type:
   - Warning, Kick, Mute (1h/1d), Ban (1d/7d/perm)
2. After punishment, choose:
   - **Close Report** → Resolve and delete
   - **Keep Report** → Return to reports
   - **Close Menu** → Exit without action

---

## 🖥️ **Compatibility**

**Minecraft Versions**: 1.8.9 - 1.21.11 (All versions supported)  
**Server Software**: Paper (recommended), Spigot, Folia  
**Java**: 21+ required

**Note**: The plugin automatically detects your server version and adapts features accordingly.

---

## ⚙️ **Configuration**

### 📄 **Basic config.yml**
```yaml
# Language (en, ru, ar)
language: en

# Anonymous reports
anonymous-reports: false

# Cooldown between reports (seconds)
cooldown: 60

# Anti-abuse limits
report-limits:
  per-player: 3    # Max reports per player
  per-hour: 10     # Max reports per hour

# Design settings
design:
  use-hex-colors: true
  colors:
    primary: "#FF6B6B"
    secondary: "#4ECDC4"
    success: "#45B7D1"
```

### � **Database**
```yaml
database:
  type: sqlite   # Default, works out-of-the-box
  # MySQL optional - requires driver in lib/ folder
```

---

## 🧪 **Permissions**

| Permission | Description | Default |
|------------|-------------|---------|
| `reports.report` | Use `/report` command | ✅ **Everyone** |
| `reports.admin` | Access admin commands & GUI | 👑 **OPs only** |
| `reports.language` | Change server language | 👑 **OPs only** |
| `reports.telegram` | Configure Telegram | 👑 **OPs only** |
| `reports.reload` | Reload configuration | 👑 **OPs only** |

---

## 🛠️ **Developer API**

### 📦 **Quick API Usage**
```java
// Create a report programmatically
ReportAPI.createReport(player, target, "Automated detection");

// Listen for report events
ReportAPI.onReportCreate(event -> {
    // Handle new report
});

// Get reports for a player
List<Report> reports = ReportAPI.getReports(player);
```

---

## 🔔 **Integrations**

### 📱 **Telegram Bot**
- Instant report notifications
- Moderation commands via Telegram
- Admin-only access control
- Rich message formatting

### 🔗 **Discord Webhooks**
- JSON webhook support
- Custom integrations
- Automated report processing

---

## 📚 **Documentation**

### 🌍 **Multi-Language READMEs**
- **🇺🇸 English**: [README.md](https://github.com/Sqrilizz/Sqrilizz-Reports/blob/main/README.md)
- **🇷🇺 Russian**: [README-RU.md](https://github.com/Sqrilizz/Sqrilizz-Reports/blob/main/README-RU.md)  
- **🇸🇦 Arabic**: [README-AR.md](https://github.com/Sqrilizz/Sqrilizz-Reports/blob/main/README-AR.md)

### 📖 **Specialized Guides**
- **� GUI Guide**: [Complete GUI Usage](https://github.com/Sqrilizz/Sqrilizz-Reports/blob/main/GUI_GUIDE.md)  
- **� Telegram**: [Telegram Setup](https://github.com/Sqrilizz/Sqrilizz-Reports/blob/main/docs/TELEGRAM.md)
- **🛠️ API**: [Developer API](https://github.com/Sqrilizz/Sqrilizz-Reports/blob/main/docs/API.md)
- **🌐 REST API**: [REST API Reference](https://github.com/Sqrilizz/Sqrilizz-Reports/blob/main/docs/REST_API.md)
- **💾 Database**: [Database Guide](https://github.com/Sqrilizz/Sqrilizz-Reports/blob/main/docs/DATABASE.md)
- **⚙️ Configuration**: [Config Reference](https://github.com/Sqrilizz/Sqrilizz-Reports/blob/main/docs/CONFIGURATION.md)
- **�️ Anti-Abuse**: [Protection System](https://github.com/Sqrilizz/Sqrilizz-Reports/blob/main/docs/ANTI_ABUSE.md)

---

## 📞 **Support & Community**

### 🆘 **Get Help**
- **� Telegram**: [Follow for updates](https://t.me/Matve1mok1)
- **📚 Documentation**: [GitHub Wiki](https://github.com/Sqrilizz/Sqrilizz-Reports)
- **🐛 Bug Reports**: [GitHub Issues](https://github.com/Sqrilizz/Sqrilizz-Reports/issues)
- **💡 Feature Requests**: [GitHub Discussions](https://github.com/Sqrilizz/Sqrilizz-Reports/discussions)

---

## 🎯 **Key Benefits**

✅ **Complete Solution** - Everything you need for player reporting  
✅ **Easy Setup** - Works out of the box with sensible defaults  
✅ **Intuitive GUI** - Visual interface for efficient moderation  
✅ **Scalable** - Handles small servers to large networks  
✅ **Reliable** - Tested across multiple Minecraft versions  
✅ **High Performance** - Optimized for minimal server impact  
✅ **Extensible** - REST API and webhook support  
✅ **Well Maintained** - Regular updates and active support  

**A professional reporting system that grows with your server.**
