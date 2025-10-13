# 🚀 Sqrilizz-Reports Lite

**Simple. Fast. Reliable.**

A lightweight report system for Minecraft servers with only the essential features you actually need. Based on the popular **Sqrilizz-Reports** plugin, but stripped down to the core functionality.

## ✨ Why Choose Lite?

- **🎯 Zero Bloat** - Only 8 classes, ~2MB JAR (vs 47MB full version)
- **⚡ Lightning Fast** - No complex systems slowing you down
- **🔧 Zero Setup** - Works out of the box, no configuration needed
- **💾 Simple Database** - SQLite only, no MySQL complexity
- **📱 Smart Notifications** - Telegram + Webhooks (no heavy Discord bot)

## 📋 Core Features

### Commands
- `/report <player> <reason>` - Create a report
- `/reports view <player>` - View reports for a player
- `/reports close <id>` - Close a report
- `/reports delete <id>` - Delete a report
- `/reports reload` - Reload configuration

### Notifications
- **Telegram Bot** - Instant notifications to your phone
- **Webhooks** - Integrate with Discord, Slack, or custom systems
- **In-Game Alerts** - Notify online admins immediately

### Database
- **SQLite** - Lightweight, no external database required
- **Smart Caching** - Fast report lookups with automatic cleanup
- **Data Integrity** - Reliable storage with proper error handling

## 🌍 Compatibility

- **Minecraft**: 1.8.9 - 1.21.10
- **Servers**: Bukkit, Spigot, Paper, Folia
- **Java**: 17+ (with fallbacks for older versions)

## ⚙️ Quick Setup

1. Download and place in `plugins/` folder
2. Restart server
3. **Optional**: Configure Telegram/Webhooks in `config.yml`
4. Done! ✅

### Telegram Setup (Optional)
```yaml
telegram:
  enabled: true
  bot-token: "YOUR_BOT_TOKEN"
  chat-id: "YOUR_CHAT_ID"
```

### Webhook Setup (Optional)
```yaml
webhook:
  enabled: true
  url: "https://discord.com/api/webhooks/..."
```

## 📊 Comparison with Full Version

| Feature | Lite | Full |
|---------|------|------|
| **JAR Size** | 2MB | 47MB |
| **Setup Time** | 30 seconds | 10+ minutes |
| **Commands** | 2 | 15+ |
| **Dependencies** | 1 | 10+ |
| **Performance** | ⚡ Blazing | 🐌 Heavy |
| **Complexity** | Simple | Complex |

## 🚫 What's NOT Included

- Discord Bot (use webhooks instead)
- REST API (keep it simple)
- Multi-language support (English only)
- Anti-abuse system (trust your admins)
- Performance monitoring (it's already fast)
- Complex permissions (basic admin/user only)

## 🔗 Webhook JSON Format

```json
{
  "event": "report_created",
  "report": {
    "id": 123,
    "timestamp": 1697123456789,
    "reporter": "PlayerName",
    "target": "ReportedPlayer",
    "reason": "Cheating",
    "location": "100.5, 64.0, -200.3",
    "resolved": false
  },
  "server": "MyServer"
}
```

## 🎯 Perfect For

- **Small-Medium Servers** - No need for enterprise features
- **Quick Setup** - Get reports working in under a minute
- **Performance Focused** - Every MB and millisecond matters
- **Simplicity Lovers** - Just reports, nothing else

## 🔗 Related Projects

- **[Sqrilizz-Reports](https://modrinth.com/plugin/sqrilizz-reports)** - Full-featured version with Discord bot, REST API, and advanced features
- **Based on the original Sqrilizz-Reports** - Same reliability, less complexity

## 🆘 Support

- **GitHub**: [Issues & Source](https://github.com/Sqrilizz/Sqrilizz-Reports)
- **Telegram**: [@Matve1mok1](https://t.me/Matve1mok1)
- **Discord**: Join our community server

## 📄 License

MIT License - Free to use, modify, and distribute!

---

**Made with ❤️ by Sqrilizz**

*Sometimes less is more. Sqrilizz-Reports Lite proves that you don't need 50 features to have a great report system.*
