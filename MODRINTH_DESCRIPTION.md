# **🚨 Sqrilizz-Reports v7.2**
The most advanced and beautiful reports plugin for Minecraft servers with modern design, multi-platform integration, and advanced anti-abuse protection!

### 🌟 **NEW in v7.2**: Discord Bot Integration, Tab Completion, Multi-Language Documentation!

[![Follow on Telegram](https://img.shields.io/badge/Telegram-Follow_for_Updates-0088cc.svg?logo=telegram)](https://t.me/Matve1mok1)
[![Documentation](https://img.shields.io/badge/Docs-GitHub-black.svg?logo=github)](https://github.com/Sqrilizz/Sqrilizz-Reports)

![Minecraft Version](https://img.shields.io/badge/Minecraft-1.8.9--1.21.8-green.svg)
[![Server Type](https://img.shields.io/badge/Server-Paper%20%7C%20Spigot%20%7C%20Folia-blue.svg)](https://modrinth.com/plugin/sqrilizz-report)
[![Java Version](https://img.shields.io/badge/Java-21+-orange.svg)](https://modrinth.com/plugin/sqrilizz-report)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](https://github.com/Sqrilizz/Sqrilizz-Reports/blob/main/LICENSE)

---

## 🎉 **What's New in v7.2**

### 🤖 **Discord Bot Integration**
- **Full Discord Bot** with slash commands: `/ban`, `/kick`, `/mute`, `/warn`, `/reports`
- **Beautiful embed notifications** for new reports
- **Role-based permissions** for moderation
- **Easy setup** with `/report-discord` command

### 📋 **Tab Completion**
- **Smart auto-completion** for all commands
- **Player name suggestions** 
- **Reason examples** in multiple languages
- **Enhanced user experience**

### 🌍 **Multi-Language Documentation**
- **3 Language versions**: English, Russian, Arabic (العربية)
- **Specialized guides** for each component
- **Complete API documentation** for developers

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

## 📋 **Platform Support Matrix**

| Version Range | Status | Key Features |
|---------------|--------|--------------|
| **1.8.9 - 1.11.x** | ✅ **Supported** | Legacy fallbacks, core features |
| **1.12.x - 1.15.x** | ✅ **Supported** | Full compatibility, enhanced features |
| **1.16.x** | ✅ **Supported** | Hex colors, modern API |
| **1.17.x - 1.18.x** | ✅ **Supported** | Latest features, Java 17+ |
| **1.19.x** | ✅ **Supported** | Enhanced security, performance |
| **1.20.x** | ✅ **Supported** | Latest updates, best performance |
| **1.21.x** | ✅ **Supported** | All features, **Folia support** |

### 🖥️ **Server Software**
- **Paper** (Recommended) - Full feature support
- **Spigot** - Complete compatibility  
- **Folia** - Optimized for regional threading

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
    <version>7.2</version>
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

## 🏆 **Why Choose Sqrilizz-Reports?**

✅ **Most Advanced**: Discord Bot, API, Anti-Abuse, Anonymous Reports  
✅ **Beautiful Design**: Modern hex colors with legacy fallbacks  
✅ **Multi-Platform**: Works on Paper, Spigot, and Folia  
✅ **Multi-Language**: English, Russian, Arabic support  
✅ **Developer Friendly**: Complete API with webhook integration  
✅ **Well Documented**: Comprehensive guides in multiple languages  
✅ **Actively Maintained**: Regular updates and community support  

**Transform your server's moderation with the most powerful reports plugin available!** 🚀
