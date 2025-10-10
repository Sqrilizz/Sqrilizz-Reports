# 🚨 Sqrilizz-Reports - Русская версия

[![Version](https://img.shields.io/badge/version-7.5-brightgreen.svg)](https://modrinth.com/plugin/sqrilizz-report)
[![Minecraft](https://img.shields.io/badge/minecraft-1.8--1.21+-blue.svg)](https://www.minecraft.net/)
[![Modrinth](https://img.shields.io/modrinth/dt/sqrilizz-report?color=00AF5C&logo=modrinth)](https://modrinth.com/plugin/sqrilizz-report)

> **Надежный и функциональный плагин жалоб для серверов Minecraft**

Продуманная система жалоб с современными функциями, мультиплатформенной интеграцией и эффективной защитой от злоупотреблений для администраторов серверов.

---

## 🌍 Языковые версии

Выберите предпочитаемый язык:

| Язык | Файл | Описание |
|------|------|----------|
| 🇺🇸 **English** | [README.md](README.md) | Английская версия |
| 🇷🇺 **Русский** | **[README-RU.md](#)** | **Текущая версия** |
| 🇸🇦 **العربية** | [README-AR.md](README-AR.md) | Арабская версия |

## 📚 Документация

| Компонент | Документация |
|-----------|--------------|
| 🤖 **Discord Bot** | [Руководство по Discord Bot](docs/DISCORD_BOT.md) |
| 📱 **Telegram** | [Интеграция Telegram](docs/TELEGRAM.md) |
| 🛠️ **API** | [API для разработчиков](docs/API.md) |
| 🌐 **REST API** | [Справочник REST API](docs/REST_API.md) |
| 🎨 **Дизайн** | [Система дизайна](docs/DESIGN.md) |
| 🛡️ **Защита от злоупотреблений** | [Система защиты](docs/ANTI_ABUSE.md) |
| ⚙️ **Конфигурация** | [Руководство по настройке](docs/CONFIGURATION.md) |
| 💾 **База данных** | [База данных и свитчинг](docs/DATABASE.md) |
| 🔧 **Установка** | [Руководство по установке](docs/INSTALLATION.md) |

---

## ⚡ Quick Setup (5 minutes)

### 1. Download & Install
```bash
# Download from Modrinth (recommended)
https://modrinth.com/plugin/sqrilizz-report

# Or from GitHub
https://github.com/sqrilizz/Sqrilizz-Reports/releases/latest

# Put in plugins folder
/plugins/Sqrilizz-Reports-7.1.jar

# Restart server
```

### 2. Basic Commands
```bash
# Report a player
/report PlayerName Reason for report

# Check reports (admin)
/reports

# Change language
/report-language en    # English
/report-language ru    # Russian  
/report-language ar    # Arabic
```

### 3. Done! ✅
Your reports system is ready to use.

---

## 🎯 Key Features

|---------|--------|-------------|
| 📝 **Basic Reports** | ✅ | Players can report others |
| 🛡️ **Anti-Spam** | ✅ | Prevents report spam |
| 🌍 **3 Languages** | ✅ | English, Russian, Arabic |
| 🎨 ** Beautiful Design** | ✅ | Modern colors & emojis |
| 🤖 **Discord Bot** | ✅ | Slash commands & notifications |
| 📱 **Telegram** | ✅ | Report notifications |
| 🙈 **Anonymous Reports** | ✅ | Optional privacy mode |

### 💾 База данных и производительность
- **SQLite / MySQL** с пулом HikariCP
- **Горячая смена БД** через `/report-reload` (миграция данных из памяти)
- **Кэш Caffeine** (30 сек) для быстрых чтений
- **Ограничение запросов** на REST API

---

## 🔧 Optional Setup

{{ ... }}
```bash
# Get Discord bot token from Discord Developer Portal
/report-discord token YOUR_BOT_TOKEN
/report-discord guild YOUR_GUILD_ID  
/report-discord channel YOUR_CHANNEL_ID
/report-discord enable
```

### Telegram (Optional)
```bash
# Get bot token from @BotFather
/report-telegram token YOUR_BOT_TOKEN
/report-telegram chat YOUR_CHAT_ID
```

### Anonymous Reports (Optional)
```yaml
# config.yml
anonymous-reports: true  # Hide reporter names
```

---

## 📋 Essential Commands

### For Players
- `/report <player> <reason>` - Report someone

### For Admins  
- `/reports` - View all reports
- `/reports check <player>` - Check specific player
- `/reports clear <player>` - Clear reports
- `/report-stats` - View statistics

---

## 🆘 Need Help?

### Common Issues

**❓ Commands not working?**
- Check permissions: `reports.admin` for admin commands

**❓ Colors not showing?**
- Update to Minecraft 1.16+ for hex colors

**❓ Discord bot not working?**
- Make sure bot has proper permissions in Discord

**❓ Language not changing?**
- Use `/report-reload` after config changes

### Get Support
[![Discord](https://img.shields.io/badge/Discord-Join_Server-7289da.svg?logo=discord&logoColor=white)](https://discord.gg/yourdiscord)
[![GitHub Issues](https://img.shields.io/badge/GitHub-Issues-black.svg?logo=github)](https://github.com/sqrilizz/Sqrilizz-Reports/issues)

---

## 🎮 Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `reports.report` | Use /report command | ✅ Everyone |
| `reports.admin` | Admin commands | 👑 OPs only |
| `reports.language` | Change language | 👑 OPs only |

---

## ⚙️ Basic Config

```yaml
# config.yml - Only change if needed
language: en                    # en, ru, ar
anonymous-reports: false        # true = hide reporter names
cooldown: 60                   # seconds between reports

design:
  use-hex-colors: true         # false for old MC versions

# Optional integrations
discord-bot:
  enabled: false               # true to enable Discord bot
  
telegram:
  enabled: false               # true to enable Telegram
```

---

## 🚀 That's It!

Your server now has a professional reports system. 

**Want more features?** Check the [Detailed Guide](README-DETAILED.md)

---

<div align="center">

[![GitHub](https://img.shields.io/badge/GitHub-Sqrilizz-black.svg?logo=github)](https://github.com/Sqrilizz)
[![Modrinth](https://img.shields.io/badge/Modrinth-Sqrilizz-00AF5C.svg?logo=modrinth)](https://modrinth.com/user/Sqrilizz)
[![Website](https://img.shields.io/badge/Website-sqrilizz.xyz-blue.svg)](https://sqrilizz.xyz)

**Made with ❤️ by Sqrilizz**

</div>
