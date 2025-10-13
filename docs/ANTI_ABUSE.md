# 🛡️ Anti-Abuse System - Sqrilizz-Reports

[![Version](https://img.shields.io/badge/version-7.2-brightgreen.svg)](https://modrinth.com/plugin/sqrilizz-report)

> **Advanced protection against report spam and abuse**

---

## 🎯 Features

### Rate Limiting
- **Per-player limits**: Max reports against one player
- **Hourly limits**: Max total reports per hour
- **Smart cooldowns**: Dynamic cooldown based on behavior

### Automatic Actions
1. **Warning** (5 reports): Player receives warning
2. **Temporary Mute** (8 reports): Cannot use `/report`
3. **Low Priority** (false reports): Reports get lower priority

### False Report Detection
- Admins can mark reports as false
- System tracks patterns
- Automatic credibility adjustment

---

## ⚙️ Configuration

```yaml
# Anti-abuse settings
anti-abuse:
  enabled: true
  warning-threshold: 5        # Warn after X reports
  temp-mute-threshold: 8      # Temp mute after X reports  
  temp-mute-duration: 3600    # Mute duration (seconds)

report-limits:
  per-player: 3      # Max reports per player
  per-hour: 10       # Max reports per hour
```

---

## 📊 Monitoring

### Admin Commands
```bash
/report-stats              # View abuse statistics
/reports false <player>    # Mark reports as false
```

### Data Storage
- `abuse_data.yml` - Stores abuse tracking data
- Automatic cleanup every 6 hours
- Configurable retention periods

---

## 📞 Support

[![Discord](https://img.shields.io/badge/Discord-Join_Server-7289da.svg?logo=discord&logoColor=white)](https://discord.gg/yourdiscord)
[![GitHub Issues](https://img.shields.io/badge/GitHub-Issues-black.svg?logo=github)](https://github.com/Sqrilizz/Sqrilizz-Reports/issues)
