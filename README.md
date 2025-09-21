# 🚨 Sqrilizz-Reports

[![Version](https://img.shields.io/badge/version-7.2-brightgreen.svg)](https://github.com/Sqrilizz/Sqrilizz-Reports/releases)
[![Minecraft](https://img.shields.io/badge/minecraft-1.8--1.21+-blue.svg)](https://www.minecraft.net/)
[![Java](https://img.shields.io/badge/java-21-orange.svg)](https://openjdk.java.net/)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)
[![Modrinth](https://img.shields.io/modrinth/dt/sqrilizz-report?color=00AF5C&logo=modrinth)](https://modrinth.com/plugin/sqrilizz-report)

> **The most advanced and beautiful reports plugin for Minecraft servers**

A comprehensive, feature-rich reports system with modern design, multi-platform integration, and advanced anti-abuse protection.

---

## 🌍 Language Versions

Choose your preferred language:

| Language | File | Description |
|----------|------|-------------|
| 🇺🇸 **English** | **[README.md](#)** | **Default version** |
| 🇷🇺 **Русский** | **[README-RU.md](README-RU.md)** | Russian version |
| 🇸🇦 **العربية** | **[README-AR.md](README-AR.md)** | Arabic version |

## 📚 Documentation

| Component | Documentation |
|-----------|---------------|
| 🤖 **Discord Bot** | [Discord Bot Guide](docs/DISCORD_BOT.md) |
| 📱 **Telegram** | [Telegram Integration](docs/TELEGRAM.md) |
| 🛠️ **API** | [Developer API](docs/API.md) |
| 🎨 **Design** | [Design System](docs/DESIGN.md) |
| 🛡️ **Anti-Abuse** | [Anti-Abuse System](docs/ANTI_ABUSE.md) |
| ⚙️ **Configuration** | [Configuration Guide](docs/CONFIGURATION.md) |
| 🔧 **Installation** | [Installation Guide](docs/INSTALLATION.md) |

---

## ✨ Key Features

### 🎨 **Beautiful Design**
- **Hex color support** for Minecraft 1.16+
- **Legacy fallback** for older versions
- **Emoji integration** for modern UX
- **Customizable color palette**

### 🌍 **Multi-Language Support**
- **3 Languages**: English, Russian, Arabic
- **RTL support** for Arabic
- **Easy language switching**
- **Localized messages**

### 🤖 **Advanced Integrations**
- **Discord Bot** with slash commands
- **Telegram notifications**
- **Webhook support** for external systems
- **Public API** for developers

### 🛡️ **Anti-Abuse System**
- **Rate limiting** (per-player & hourly)
- **False report detection**
- **Automatic punishments**
- **Smart cooldowns**

### 🙈 **Privacy Features**
- **Anonymous reports** option
- **Data protection**
- **Configurable visibility**

---

## 🚀 Quick Start

### 1️⃣ Installation
```bash
# Download the latest release
wget https://github.com/Sqrilizz/Sqrilizz-Reports/releases/latest/download/Sqrilizz-Reports-7.2.jar

# Place in plugins folder
mv Sqrilizz-Reports-7.2.jar /path/to/server/plugins/

# Restart server
systemctl restart minecraft
```

### 2️⃣ Basic Configuration
```yaml
# config.yml - Essential settings
language: en                    # en, ru, ar
anonymous-reports: false        # Enable anonymous reporting
design:
  use-hex-colors: true         # Modern colors for 1.16+
```

### 3️⃣ First Report
```bash
/report Griefer123 Destroying builds at spawn
```

---

## 📋 Commands Overview

| Command | Description | Permission |
|---------|-------------|------------|
| `/report <player> <reason>` | 📝 Report a player | `reports.report` |
| `/reports` | 📊 View all reports | `reports.admin` |
| `/reports check <player>` | 🔍 Check specific player | `reports.admin` |
| `/report-language <lang>` | 🌍 Change language | `reports.language` |
| `/report-discord <config>` | 🤖 Setup Discord bot | `reports.admin` |
| `/report-stats` | 📈 View statistics | `reports.admin` |

<details>
<summary>📚 View all commands</summary>

### Player Commands
- `/report <player> <reason>` - Report a player for violations

### Admin Commands
- `/reports` - View all active reports
- `/reports check <player>` - Check reports for specific player
- `/reports clear <player>` - Clear reports for a player
- `/reports clearall` - Clear all reports
- `/reports false <player>` - Mark player's reports as false

### Configuration Commands
- `/report-language <en|ru|ar>` - Change server language
- `/report-telegram <token|chat> <value>` - Configure Telegram
- `/report-discord <token|guild|channel> <value>` - Configure Discord
- `/report-webhook <set|remove> [url]` - Configure webhooks
- `/report-reload` - Reload configuration
- `/report-stats` - View plugin statistics

</details>

---

## 🎯 Platform Support

### ✅ **Minecraft Versions**
![Minecraft 1.8](https://img.shields.io/badge/1.8-✅-green.svg)
![Minecraft 1.12](https://img.shields.io/badge/1.12-✅-green.svg)
![Minecraft 1.16](https://img.shields.io/badge/1.16-✅-green.svg)
![Minecraft 1.19](https://img.shields.io/badge/1.19-✅-green.svg)
![Minecraft 1.20](https://img.shields.io/badge/1.20-✅-green.svg)

### ✅ **Server Software**
![Paper](https://img.shields.io/badge/Paper-✅-green.svg)
![Spigot](https://img.shields.io/badge/Spigot-✅-green.svg)
![Folia](https://img.shields.io/badge/Folia-✅-green.svg)

### ✅ **Integrations**
![Discord](https://img.shields.io/badge/Discord_Bot-✅-7289da.svg)
![Telegram](https://img.shields.io/badge/Telegram-✅-0088cc.svg)
![Webhooks](https://img.shields.io/badge/Webhooks-✅-orange.svg)

---

## 🛠️ For Developers

### 📦 **Public API**
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

### 🔗 **Webhook Integration**
```json
{
  "type": "report",
  "reporter": "AdminUser",
  "target": "Cheater123",
  "reason": "Flying hacks detected",
  "timestamp": 1695307200000,
  "is_system_report": true
}
```

---

## 📊 Statistics

![GitHub stars](https://img.shields.io/github/stars/Sqrilizz/Sqrilizz-Reports?style=social)
![GitHub forks](https://img.shields.io/github/forks/Sqrilizz/Sqrilizz-Reports?style=social)
![GitHub issues](https://img.shields.io/github/issues/Sqrilizz/Sqrilizz-Reports)
![GitHub pull requests](https://img.shields.io/github/issues-pr/Sqrilizz/Sqrilizz-Reports)

---

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guidelines](CONTRIBUTING.md) for details.

### 🐛 **Bug Reports**
Found a bug? [Create an issue](https://github.com/Sqrilizz/Sqrilizz-Reports/issues/new?template=bug_report.md)

### 💡 **Feature Requests**
Have an idea? [Suggest a feature](https://github.com/Sqrilizz/Sqrilizz-Reports/issues/new?template=feature_request.md)

### 🌍 **Translations**
Help us translate! Check our [Translation Guide](TRANSLATIONS.md)

---

## 📞 Support

[![Discord](https://img.shields.io/badge/Discord-Join_Server-7289da.svg?logo=discord&logoColor=white)](https://discord.gg/yourdiscord)
[![GitHub Issues](https://img.shields.io/badge/GitHub-Issues-black.svg?logo=github)](https://github.com/Sqrilizz/Sqrilizz-Reports/issues)
[![Documentation](https://img.shields.io/badge/Docs-Wiki-blue.svg)](https://github.com/Sqrilizz/Sqrilizz-Reports/wiki)

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

<div align="center">

**Made with ❤️ by [Sqrilizz](https://modrinth.com/user/Sqrilizz)**

[![GitHub](https://img.shields.io/badge/GitHub-Sqrilizz-black.svg?logo=github)](https://github.com/Sqrilizz)
[![Modrinth](https://img.shields.io/badge/Modrinth-Sqrilizz-00AF5C.svg?logo=modrinth)](https://modrinth.com/user/Sqrilizz)
[![Website](https://img.shields.io/badge/Website-sqrilizz.xyz-blue.svg)](https://sqrilizz.xyz)

</div>
