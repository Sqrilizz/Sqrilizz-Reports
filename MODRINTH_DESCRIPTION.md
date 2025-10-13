# **🚨 Sqrilizz-Reports**
A professional reports management system for Minecraft servers with advanced features, multi-platform integration, and comprehensive anti-abuse protection.

[![Follow on Telegram](https://img.shields.io/badge/Telegram-Follow_for_Updates-0088cc.svg?logo=telegram)](https://t.me/Matve1mok1)
[![Documentation](https://img.shields.io/badge/Docs-GitHub-black.svg?logo=github)](https://github.com/Sqrilizz/Sqrilizz-Reports)

![Minecraft Version](https://img.shields.io/badge/Minecraft-1.8.9--1.21.10-green.svg)
[![Server Type](https://img.shields.io/badge/Server-Paper%20%7C%20Spigot%20%7C%20Folia-blue.svg)](https://modrinth.com/plugin/sqrilizz-report)
[![Java Version](https://img.shields.io/badge/Java-21+-orange.svg)](https://modrinth.com/plugin/sqrilizz-report)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](https://github.com/Sqrilizz/Sqrilizz-Reports/blob/main/LICENSE)

## 📋 **What This Plugin Does**

Sqrilizz-Reports provides server administrators with a complete player reporting system that includes:

- **Player Reporting System** - Players can report rule violations with `/report <player> <reason>`
- **Admin Management Tools** - Review, resolve, and manage reports through intuitive commands
- **Multi-Platform Notifications** - Discord bot, Telegram integration, and webhook support
- **Anti-Abuse Protection** - Rate limiting and false report detection to prevent system abuse
- **REST API** - External integration capabilities for web panels and other tools
- **Performance Monitoring** - Built-in health checks and system diagnostics
- **Multi-Language Support** - English, Russian, and Arabic translations

---

## ✨ **Core Features**

### 🎨 **Beautiful Design System**
- **Hex color support** for Minecraft 1.16+ with legacy fallbacks
- **Modern emoji integration** for enhanced UX
- **Customizable color palette** with 7 predefined themes
- **Gradient text effects** for premium feel

### 🛡️ **Advanced Anti-Abuse Protection**
- **Rate limiting**: Per-player and hourly limits
- **False report detection** with automatic credibility adjustment
- **Smart cooldowns** based on player behavior
- **Automatic cleanup** of old data

### 🙈 **Privacy & Security**
- **Anonymous reports** option for sensitive situations
- **Data protection** with configurable visibility
- **UUID-based tracking** with legacy support
- **Secure API** for external integrations

### 🌍 **Multi-Language Support**
- **3 Languages**: English, Russian, Arabic
- **RTL support** for Arabic text
- **Easy language switching** with `/report-language`
- **Localized messages** and error handling

---

## 🔧 **Commands Overview**

### 👥 **Player Commands**
```
/report <player> <reason>       — Report a player for violations
/report-language <en|ru|ar>     — Change server language (admin)
```

### 🛠️ **Admin Commands**
```
/reports                        — View all active reports
/reports check <player>         — Check reports for specific player
/reports clear <player>         — Clear reports for a player
/reports clearall               — Clear all reports
/reports false <player>         — Mark player's reports as false
/report-stats                   — View detailed plugin statistics
/report-reload                  — Reload plugin configuration
```

### 🤖 **Discord Bot Commands**
```
/report-discord token <token>   — Set Discord bot token
/report-discord guild <id>      — Set Discord server ID
/report-discord channel <id>    — Set notification channel
/report-discord enable          — Enable Discord bot
/report-discord moderation true — Enable moderation commands
/report-resolve <id>            — Resolve a report by ID
/report-reply <id> <message>    — Add a moderator reply
```

### 📱 **Telegram Integration**
```
/report-telegram token <token>  — Set Telegram bot token
/report-telegram chat <id>      — Set Telegram chat ID
```

### 🔗 **Webhook Support**
```
/report-webhook set <url>       — Set Discord webhook URL
/report-webhook remove          — Remove webhook
```

---

## 🖥️ **Compatibility**

**Minecraft Versions**: 1.8.9 - 1.21.x (All versions supported)
**Server Software**: Paper (recommended), Spigot, Folia
**Java**: 21+ (with fallbacks for older versions)

**Note**: The plugin automatically detects your server version and adapts its features accordingly.

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

# Auto-ban threshold
auto-ban-threshold: 8

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

### 🌐 **REST API (config excerpt)**
```yaml
rest-api:
  enabled: true
  port: 8971
  token: "REPLACE_ME"
  ip-whitelist:
    enabled: false
    list: []
  webhook:
    secret: ""    # optional HMAC secret
  rate-limit:
    rps: 10
```

### 💾 **Database (config excerpt)**
```yaml
database:
  type: sqlite   # or mysql
  mysql:
    host: "localhost"
    port: 3306
    database: "sqrilizz_reports"
    user: "root"
    password: ""
    pool-size: 10
    params: "useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=utf8&serverTimezone=UTC"
```

### 🤖 **Discord Bot Setup**
```yaml
discord-bot:
  enabled: true
  token: "YOUR_BOT_TOKEN"
  guild-id: "YOUR_GUILD_ID"
  channel-id: "YOUR_CHANNEL_ID"
  moderation:
    enabled: true
```

---

## 🧪 **Permissions**

| Permission | Description | Default |
|------------|-------------|---------|
| `reports.report` | Use `/report` command | ✅ **Everyone** |
| `reports.admin` | Access admin commands | 👑 **OPs only** |
| `reports.language` | Change server language | 👑 **OPs only** |
| `reports.telegram` | Configure Telegram | 👑 **OPs only** |
| `reports.reload` | Reload configuration | 👑 **OPs only** |
| `reports.bypass` | Immunity from reports | 👑 **OPs only** |

---

## 🛠️ **Developer API**

### 📦 **Maven Dependency**
```xml
<dependency>
    <groupId>dev.sqrilizz</groupId>
    <artifactId>sqrilizz-reports</artifactId>
    <version>7.5</version>
    <scope>provided</scope>
</dependency>
```

### 🚀 **Quick API Usage**
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

### 🤖 **Discord Bot** *(NEW!)*
- Full Discord bot with slash commands
- Beautiful embed notifications
- Role-based moderation system
- Real-time report alerts

### 🔗 **Webhooks**
- JSON webhook support for external systems
- Custom integrations with other tools
- Automated report processing
- Third-party service integration

---

## 📚 **Documentation**

### 🌍 **Multi-Language READMEs**
- **🇺🇸 English**: [README.md](https://github.com/Sqrilizz/Sqrilizz-Reports/blob/main/README.md)
- **🇷🇺 Russian**: [README-RU.md](https://github.com/Sqrilizz/Sqrilizz-Reports/blob/main/README-RU.md)  
- **🇸🇦 Arabic**: [README-AR.md](https://github.com/Sqrilizz/Sqrilizz-Reports/blob/main/README-AR.md)

### 📖 **Specialized Guides**
- **🤖 Discord Bot**: [Discord Integration Guide](https://github.com/Sqrilizz/Sqrilizz-Reports/blob/main/docs/DISCORD_BOT.md)
- **📱 Telegram**: [Telegram Setup Guide](https://github.com/Sqrilizz/Sqrilizz-Reports/blob/main/docs/TELEGRAM.md)
- **🛠️ API**: [Developer API Documentation](https://github.com/Sqrilizz/Sqrilizz-Reports/blob/main/docs/API.md)
- **🌐 REST API**: [REST API Reference](https://github.com/Sqrilizz/Sqrilizz-Reports/blob/main/docs/REST_API.md)
- **💾 Database**: [Database & Swapping Guide](https://github.com/Sqrilizz/Sqrilizz-Reports/blob/main/docs/DATABASE.md)
- **🔧 Installation**: [Complete Installation Guide](https://github.com/Sqrilizz/Sqrilizz-Reports/blob/main/docs/INSTALLATION.md)
- **⚙️ Configuration**: [Configuration Reference](https://github.com/Sqrilizz/Sqrilizz-Reports/blob/main/docs/CONFIGURATION.md)
- **🛡️ Anti-Abuse**: [Anti-Abuse System Guide](https://github.com/Sqrilizz/Sqrilizz-Reports/blob/main/docs/ANTI_ABUSE.md)
- **🎨 Design**: [Design System Documentation](https://github.com/Sqrilizz/Sqrilizz-Reports/blob/main/docs/DESIGN.md)

---

## 📞 **Support & Community**

### 🆘 **Get Help**
- **📱 Telegram**: [Follow for updates](https://t.me/Matve1mok1)
- **📚 Documentation**: [GitHub Wiki](https://github.com/Sqrilizz/Sqrilizz-Reports)
- **🐛 Bug Reports**: [GitHub Issues](https://github.com/Sqrilizz/Sqrilizz-Reports/issues)
- **💡 Feature Requests**: [GitHub Discussions](https://github.com/Sqrilizz/Sqrilizz-Reports/discussions)

### 🤝 **Contributing**
- **🌍 Translations**: Help translate to more languages
- **🐛 Bug Fixes**: Submit pull requests
- **📝 Documentation**: Improve guides and examples
- **💡 Features**: Suggest and implement new features

---

## 🎯 **Key Benefits**

✅ **Complete Solution**: Everything you need for player reporting in one plugin  
✅ **Easy Setup**: Works out of the box with sensible defaults  
✅ **Scalable**: Handles small servers to large networks efficiently  
✅ **Reliable**: Tested across multiple Minecraft versions and server types  
✅ **Extensible**: REST API and webhook support for custom integrations  
✅ **Well Maintained**: Regular updates and active community support  

**A professional reporting system that grows with your server.**
