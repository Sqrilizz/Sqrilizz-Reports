# 🚀 Sqrilizz-Reports Lite v9.4.0

**Simple. Fast. Reliable.**

A lightweight report system for Minecraft servers with only the essential features you actually need.

## ✨ Features

- 📝 **Simple Commands** - `/report` and `/reports` - that's it!
- 💾 **SQLite Database** - No complex setup, just works
- ⚡ **Fast Cache** - In-memory caching for better performance  
- 📱 **Telegram Notifications** - Get notified instantly
- 🔗 **Webhook Support** - Integrate with your systems
- 🌍 **Folia Compatible** - Works on all server types
- 🎯 **Zero Bloat** - Only 8 classes, ~2MB JAR

## 📋 Commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/report <player> <reason>` | `reports.use` | Create a report |
| `/reports view <player>` | `reports.admin` | View reports for player |
| `/reports close <id>` | `reports.admin` | Close a report |
| `/reports delete <id>` | `reports.admin` | Delete a report |
| `/reports reload` | `reports.admin` | Reload configuration |

## ⚙️ Configuration

```yaml
# Telegram Notifications
telegram:
  enabled: false
  bot-token: "YOUR_BOT_TOKEN_HERE"
  chat-id: "YOUR_CHAT_ID_HERE"

# Webhook Notifications  
webhook:
  enabled: false
  url: "https://your-webhook-url.com/reports"
```

## 🚀 Installation

1. Download `Sqrilizz-Reports-Lite-9.4.0.jar`
2. Place in your `plugins/` folder
3. Restart server
4. Configure `config.yml` if needed
5. Done! ✅

## 📊 Comparison with Full Version

| Feature | Lite v9.4.0 | Full v9.4.0 |
|---------|-----------|-----------|
| **JAR Size** | ~2MB | ~47MB |
| **Classes** | 8 | 50+ |
| **Commands** | 2 | 15+ |
| **Dependencies** | 1 | 10+ |
| **Setup Time** | 30 seconds | 10+ minutes |
| **Performance** | ⚡ Blazing | 🐌 Heavy |

## 🎯 Why Lite?

- **No Discord Bot** - Use webhooks instead
- **No REST API** - Keep it simple
- **No Multi-language** - English only
- **No Complex Features** - Just reports
- **No Performance Monitoring** - It's already fast
- **No Anti-Abuse** - Trust your admins

## 📱 Telegram Setup

1. Create bot with [@BotFather](https://t.me/BotFather)
2. Get your chat ID from [@userinfobot](https://t.me/userinfobot)
3. Add to config.yml
4. Reload plugin

## 🔗 Webhook Format

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

## 🆘 Support

- **GitHub**: [Issues](https://github.com/Sqrilizz/Sqrilizz-Reports/issues)
- **Telegram**: [@Matve1mok1](https://t.me/Matve1mok1)

## 📄 License

MIT License - Use it however you want!

---

**Made with ❤️ by Sqrilizz**
