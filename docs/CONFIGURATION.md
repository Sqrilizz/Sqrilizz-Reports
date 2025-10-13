# ⚙️ Configuration Guide - Sqrilizz-Reports

[![Version](https://img.shields.io/badge/version-7.2-brightgreen.svg)](https://modrinth.com/plugin/sqrilizz-report)

> **Complete configuration reference**

---

## 📋 config.yml Reference

```yaml
# Language settings (en, ru, ar)
language: en

# Cooldown between reports (seconds)
cooldown: 60

# Auto-ban threshold (number of reports)
auto-ban-threshold: 8

# Anonymous reports
anonymous-reports: false

# Report limits (anti-abuse)
report-limits:
  per-player: 3      # Max reports per player
  per-hour: 10       # Max reports per hour

# Design settings
design:
  use-hex-colors: true        # Hex colors for 1.16+
  colors:
    primary: "#FF6B6B"        # Main color (red)
    secondary: "#4ECDC4"      # Secondary (teal)
    success: "#45B7D1"        # Success (blue)
    warning: "#FFA726"        # Warning (orange)
    error: "#EF5350"          # Error (red)
    info: "#66BB6A"           # Info (green)
    accent: "#AB47BC"         # Accent (purple)

# Discord Bot
discord-bot:
  enabled: false
  token: ""                   # Bot token
  guild-id: ""                # Discord server ID
  channel-id: ""              # Channel for notifications

# Telegram
telegram:
  enabled: false
  token: ""                   # Bot token
  chat_id: ""                 # Chat ID
```

---

## 📞 Support

[![Discord](https://img.shields.io/badge/Discord-Join_Server-7289da.svg?logo=discord&logoColor=white)](https://discord.gg/yourdiscord)
[![GitHub Issues](https://img.shields.io/badge/GitHub-Issues-black.svg?logo=github)](https://github.com/Sqrilizz/Sqrilizz-Reports/issues)
