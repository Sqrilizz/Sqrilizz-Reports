# 🚨 Sqrilizz-Reports - النسخة العربية

[![Version](https://img.shields.io/badge/version-7.2-brightgreen.svg)](https://github.com/Sqrilizz/Sqrilizz-Reports/releases)
[![Minecraft](https://img.shields.io/badge/minecraft-1.8--1.21+-blue.svg)](https://www.minecraft.net/)
[![Java](https://img.shields.io/badge/java-21-orange.svg)](https://openjdk.java.net/)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)
[![Modrinth](https://img.shields.io/modrinth/dt/sqrilizz-report?color=00AF5C&logo=modrinth)](https://modrinth.com/plugin/sqrilizz-report)
[![Discord](https://img.shields.io/discord/123456789?color=7289da&logo=discord&logoColor=white)](https://discord.gg/yourdiscord)

> **أكثر إضافات البلاغات تقدماً وجمالاً لخوادم ماين كرافت**

نظام بلاغات شامل وغني بالميزات مع تصميم حديث وتكامل متعدد المنصات وحماية متقدمة ضد إساءة الاستخدام.

---

## 🌍 إصدارات اللغة

اختر لغتك المفضلة:

| اللغة | الملف | الوصف |
|-------|------|-------|
| 🇺🇸 **English** | [README.md](README.md) | النسخة الإنجليزية |
| 🇷🇺 **Русский** | [README-RU.md](README-RU.md) | النسخة الروسية |
| 🇸🇦 **العربية** | **[README-AR.md](#)** | **النسخة الحالية** |

## 📚 الوثائق

| المكون | الوثائق |
|--------|---------|
| 🤖 **بوت ديسكورد** | [دليل بوت ديسكورد](docs/DISCORD_BOT.md) |
| 📱 **تيليجرام** | [تكامل تيليجرام](docs/TELEGRAM.md) |
| 🛠️ **واجهة برمجة التطبيقات** | [API للمطورين](docs/API.md) |
| 🎨 **التصميم** | [نظام التصميم](docs/DESIGN.md) |
| 🛡️ **مكافحة الإساءة** | [نظام مكافحة الإساءة](docs/ANTI_ABUSE.md) |
| ⚙️ **الإعدادات** | [دليل الإعدادات](docs/CONFIGURATION.md) |
| 🔧 **التثبيت** | [دليل التثبيت](docs/INSTALLATION.md) |

---

## 📋 Table of Contents

- [🚀 Installation](#-installation)
- [⚙️ Configuration](#️-configuration)
- [📝 Commands Reference](#-commands-reference)
- [🔐 Permissions](#-permissions)
- [🤖 Discord Bot Setup](#-discord-bot-setup)
- [📱 Telegram Integration](#-telegram-integration)
- [🛠️ Developer API](#️-developer-api)
- [🎨 Design System](#-design-system)
- [🌍 Localization](#-localization)
- [🛡️ Anti-Abuse System](#️-anti-abuse-system)
- [📊 Statistics & Monitoring](#-statistics--monitoring)
- [🔧 Troubleshooting](#-troubleshooting)

---

## 🚀 Installation

### Requirements
![Java 21](https://img.shields.io/badge/Java-21+-orange.svg)
![Minecraft 1.8+](https://img.shields.io/badge/Minecraft-1.8+-blue.svg)
![Paper](https://img.shields.io/badge/Paper-Recommended-green.svg)

### Download Options

| Platform | Link | Notes |
|----------|------|-------|
| 🟢 **Modrinth** | [Download](https://modrinth.com/plugin/sqrilizz-report) | **Recommended** |
| 🔵 **GitHub** | [Releases](https://github.com/sqrilizz/Sqrilizz-Reports/releases) | Latest builds |
| 🟡 **SpigotMC** | Coming Soon | Under review |

### Installation Steps

```bash
# 1. Download the plugin
wget https://github.com/sqrilizz/Sqrilizz-Reports/releases/latest/download/Sqrilizz-Reports-7.1.jar

# 2. Place in plugins directory
mv Sqrilizz-Reports-7.1.jar /path/to/server/plugins/

# 3. Restart server
systemctl restart minecraft-server

# 4. Verify installation
tail -f /path/to/server/logs/latest.log | grep "Sqrilizz"
```

---

## ⚙️ Configuration

### Complete config.yml Reference

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

# Anti-abuse system
anti-abuse:
  enabled: true
  warning-threshold: 5        # Warn after X reports
  temp-mute-threshold: 8      # Temp mute after X reports  
  temp-mute-duration: 3600    # Mute duration (seconds)

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

# Telegram integration
telegram:
  enabled: false
  token: ""                   # Bot token from @BotFather
  chat_id: ""                 # Chat ID for notifications
  moderation:
    enabled: false            # Enable moderation commands
    admin_ids:                # Telegram user IDs
      - "123456789"
    commands:
      ban: true
      kick: true
      mute: true
      warn: true

# Discord webhook (legacy)
discord:
  enabled: false
  webhook_url: ""

# Discord Bot (new)
discord-bot:
  enabled: false
  token: ""                   # Bot token
  guild-id: ""                # Discord server ID
  channel-id: ""              # Channel for notifications
  mod-roles:                  # Moderator role IDs
    - "123456789012345678"
  status: "Watching reports 👀"
  moderation:
    enabled: false
    commands:
      ban: true
      kick: true
      mute: true
      warn: true
```

---

## 📝 Commands Reference

### Player Commands

#### `/report <player> <reason>`
**Description**: Report a player for rule violations  
**Permission**: `reports.report` (default: true)  
**Examples**:
```bash
/report Griefer123 Destroying builds at spawn
/report Cheater456 Flying hacks in PvP area
/report Spammer789 Advertising other servers
```

#### `/report-language <language>`
**Description**: Change server language  
**Permission**: `reports.language` (default: op)  
**Languages**: `en` (English), `ru` (Russian), `ar` (Arabic)  
**Examples**:
```bash
/report-language en    # Switch to English
/report-language ru    # Switch to Russian  
/report-language ar    # Switch to Arabic
```

### Admin Commands

#### `/reports`
**Description**: View all active reports  
**Permission**: `reports.admin`  
**Output**: Paginated list of all reports with counts

#### `/reports check <player>`
**Description**: View reports for specific player  
**Permission**: `reports.admin`  
**Example**: `/reports check Griefer123`

#### `/reports clear <player>`
**Description**: Clear all reports for a player  
**Permission**: `reports.admin`  
**Example**: `/reports clear Griefer123`

#### `/reports clearall`
**Description**: Clear all reports from all players  
**Permission**: `reports.admin`  
**Warning**: This action cannot be undone

#### `/reports false <player>`
**Description**: Mark player's reports as false (punishment)  
**Permission**: `reports.admin`  
**Effect**: Reduces reporter's credibility

### Configuration Commands

#### `/report-telegram <type> <value>`
**Description**: Configure Telegram integration  
**Permission**: `reports.admin`  
**Types**: `token`, `chat`  
**Examples**:
```bash
/report-telegram token 123456:ABC-DEF1234ghIkl-zyx57W2v1u123ew11
/report-telegram chat -1001234567890
```

#### `/report-discord <type> <value>`
**Description**: Configure Discord Bot  
**Permission**: `reports.admin`  
**Types**: `token`, `guild`, `channel`, `enable`, `disable`, `moderation`, `status`  
**Examples**:
```bash
/report-discord token MTIzNDU2Nzg5MDEyMzQ1Njc4.GhIjKl.example
/report-discord guild 123456789012345678
/report-discord channel 987654321098765432
/report-discord enable
/report-discord moderation true
```

#### `/report-webhook <action> [url]`
**Description**: Configure Discord webhooks (legacy)  
**Permission**: `reports.admin`  
**Actions**: `set`, `remove`  
**Example**: `/report-webhook set https://discord.com/api/webhooks/...`

#### `/report-stats`
**Description**: View detailed plugin statistics  
**Permission**: `reports.admin`  
**Shows**: Report counts, integrations status, system info

#### `/report-reload`
**Description**: Reload plugin configuration  
**Permission**: `reports.reload`  
**Effect**: Reloads config without restart

---

## 🔐 Permissions

### Permission Nodes

| Permission | Description | Default | Commands |
|------------|-------------|---------|----------|
| `reports.report` | Create reports | `true` | `/report` |
| `reports.admin` | Admin functions | `op` | `/reports *`, `/report-discord`, etc. |
| `reports.language` | Change language | `op` | `/report-language` |
| `reports.telegram` | Configure Telegram | `op` | `/report-telegram` |
| `reports.reload` | Reload config | `op` | `/report-reload` |

### Permission Groups Example

```yaml
# LuckPerms example
groups:
  moderator:
    permissions:
      - reports.admin
      - reports.language
      - reports.reload
  
  player:
    permissions:
      - reports.report
```

---

## 🤖 Discord Bot Setup

### Step 1: Create Discord Application

1. Go to [Discord Developer Portal](https://discord.com/developers/applications)
2. Click "New Application"
3. Name your application (e.g., "Server Reports Bot")
4. Go to "Bot" section
5. Click "Add Bot"
6. Copy the bot token

### Step 2: Configure Bot Permissions

Required permissions:
- `Send Messages`
- `Use Slash Commands`  
- `Embed Links`
- `Read Message History`

### Step 3: Invite Bot to Server

Use OAuth2 URL Generator:
- Scopes: `bot`, `applications.commands`
- Bot Permissions: (as listed above)

### Step 4: Configure in Game

```bash
# Set bot token
/report-discord token YOUR_BOT_TOKEN

# Set guild ID (enable Developer Mode in Discord)
/report-discord guild YOUR_GUILD_ID

# Set channel for notifications
/report-discord channel YOUR_CHANNEL_ID

# Enable the bot
/report-discord enable

# Enable moderation commands
/report-discord moderation true
```

### Discord Slash Commands

| Command | Description | Parameters |
|---------|-------------|------------|
| `/ban` | Ban a player | `player` (required), `reason` (optional) |
| `/kick` | Kick a player | `player` (required), `reason` (optional) |
| `/mute` | Mute a player | `player` (required), `duration` (minutes), `reason` (optional) |
| `/warn` | Warn a player | `player` (required), `reason` (optional) |
| `/reports` | View reports | `player` (optional) |

---

## 📱 Telegram Integration

### Setup Process

1. **Create Bot**: Message @BotFather on Telegram
2. **Get Token**: Use `/newbot` command
3. **Get Chat ID**: 
   - Add bot to group/channel
   - Send a message
   - Visit: `https://api.telegram.org/bot<TOKEN>/getUpdates`
   - Find chat ID in response

### Configuration

```bash
# Set bot token
/report-telegram token 123456:ABC-DEF1234ghIkl-zyx57W2v1u123ew11

# Set chat ID  
/report-telegram chat -1001234567890
```

### Message Format

```
🚨 *New Report*
👤 From: PlayerName
🎯 Target: ReportedPlayer
📝 Reason: Violation description
⏰ Time: 2025-09-21 18:25:49
📍 Location: world: 100, 64, -200
```

---

## 🛠️ Developer API

### Maven/Gradle Dependency

```xml
<!-- Maven -->
<dependency>
    <groupId>dev.sqrilizz</groupId>
    <artifactId>sqrilizz-reports</artifactId>
    <version>7.1</version>
    <scope>provided</scope>
</dependency>
```

```gradle
// Gradle
compileOnly 'dev.sqrilizz:sqrilizz-reports:7.1'
```

### API Usage Examples

#### Creating Reports

```java
import dev.sqrilizz.SQRILIZZREPORTS.api.ReportAPI;

// Create a report from another plugin
ReportAPI.createReport(reporter, target, "Automated detection: Flying");

// Create system report (no player reporter)
ReportAPI.createSystemReport(target, "AntiCheat", "Speed hacking detected");
```

#### Listening for Events

```java
import dev.sqrilizz.SQRILIZZREPORTS.api.ReportEvent;

// Listen for new reports
ReportAPI.onReportCreate(event -> {
    String reporter = event.getReporterName();
    String target = event.getTargetName();
    String reason = event.getReason();
    
    // Custom handling
    if (reason.contains("hack")) {
        // Alert staff immediately
        broadcastToStaff("Possible hacker: " + target);
    }
});
```

#### Managing Reports

```java
// Get reports for a player
List<Report> reports = ReportAPI.getReports(targetPlayer);

// Clear reports
ReportAPI.clearReports(targetPlayer);

// Check if player has reports
boolean hasReports = ReportAPI.hasReports(targetPlayer);
```

### Webhook Integration

#### JSON Format

```json
{
  "type": "report",
  "timestamp": 1695307200000,
  "reporter": "AdminUser",
  "target": "SuspiciousPlayer", 
  "reason": "Flying hacks detected",
  "is_system_report": true,
  "system_name": "AntiCheat",
  "reporter_location": "world: 100, 64, -200",
  "target_location": "world: 150, 80, -180"
}
```

#### Registering Webhooks

```java
import dev.sqrilizz.SQRILIZZREPORTS.api.WebhookManager;

// Register webhook for reports
WebhookManager.registerWebhook("report", "https://your-server.com/webhook");

// Register webhook for false reports
WebhookManager.registerWebhook("false_report", "https://your-server.com/false-reports");
```

---

## 🎨 Design System

### Color Palette

| Color | Hex Code | Usage | Legacy Fallback |
|-------|----------|-------|-----------------|
| Primary | `#FF6B6B` | Main elements | `§c` (Red) |
| Secondary | `#4ECDC4` | Secondary info | `§a` (Green) |
| Success | `#45B7D1` | Success messages | `§9` (Blue) |
| Warning | `#FFA726` | Warnings | `§6` (Gold) |
| Error | `#EF5350` | Errors | `§c` (Red) |
| Info | `#66BB6A` | Information | `§a` (Green) |
| Accent | `#AB47BC` | Highlights | `§d` (Light Purple) |

### Message Templates

```yaml
# Using color tags
report-success: "{success}✅ You reported {accent}[PLAYER]{success} for: {secondary}[REASON]"

# Using hex colors directly  
custom-message: "&#FF6B6BThis is red text &#4ECDC4and this is teal"
```

### Version Compatibility

- **Minecraft 1.16+**: Full hex color support
- **Minecraft 1.8-1.15**: Automatic fallback to legacy colors
- **Console**: Colors stripped for readability

---

## 🌍 Localization

### Supported Languages

| Language | Code | Status | Completeness |
|----------|------|--------|--------------|
| English | `en` | ✅ Complete | 100% |
| Russian | `ru` | ✅ Complete | 100% |
| Arabic | `ar` | ✅ Complete | 100% |

### Adding Custom Languages

1. Copy existing language section in `config.yml`
2. Translate all message keys
3. Change language code
4. Use `/report-language <code>` to activate

### RTL Support

Arabic language includes proper RTL (Right-to-Left) support:
- Text direction handled automatically
- Unicode compatibility
- Proper emoji positioning

---

## 🛡️ Anti-Abuse System

### Rate Limiting

```yaml
report-limits:
  per-player: 3      # Max reports against one player
  per-hour: 10       # Max total reports per hour
```

### Automatic Actions

1. **Warning** (5 reports): Player receives warning message
2. **Temporary Mute** (8 reports): Cannot use `/report` for 1 hour
3. **Low Priority** (false reports): Reports get lower priority

### False Report Detection

- Admins can mark reports as false using `/reports false <player>`
- False reporters get reduced credibility
- System tracks false report patterns

### Data Cleanup

- Old abuse data cleaned every 6 hours
- Configurable retention periods
- Automatic memory management

---

## 📊 Statistics & Monitoring

### Available Statistics

- Total reports count
- Reports per player
- Integration status (Discord, Telegram)
- System performance metrics
- Language usage statistics

### Monitoring Commands

```bash
/report-stats              # View all statistics
/report-discord status     # Discord bot status
/report-reload             # Reload and show status
```

### Log Files

```
plugins/Sqrilizz-Reports/
├── config.yml             # Main configuration
├── reports.yml            # Report storage
├── abuse_data.yml         # Anti-abuse data
└── logs/
    ├── reports.log         # Report activity
    ├── discord.log         # Discord bot logs
    └── telegram.log        # Telegram logs
```

---

## 🔧 Troubleshooting

### Common Issues

#### Reports Not Saving
```bash
# Check permissions
ls -la plugins/Sqrilizz-Reports/
# Should show read/write permissions

# Check logs
tail -f logs/latest.log | grep "Sqrilizz"
```

#### Discord Bot Not Responding
```bash
# Check bot status
/report-discord status

# Verify token
# Token should start with MTI... or similar

# Check permissions in Discord
# Bot needs Send Messages, Use Slash Commands
```

#### Colors Not Showing
```bash
# Check Minecraft version
/version

# For 1.8-1.15, disable hex colors
design:
  use-hex-colors: false
```

#### Language Not Changing
```bash
# Reload after language change
/report-reload

# Check config syntax
# YAML is sensitive to indentation
```

### Debug Mode

Enable debug logging in `config.yml`:
```yaml
debug: true
log-level: DEBUG
```

### Performance Issues

```bash
# Check report count
/report-stats

# Clear old reports if needed
/reports clearall

# Restart server if memory issues persist
```

---

## 📞 Support & Community

### Get Help

[![Discord](https://img.shields.io/badge/Discord-Join_Server-7289da.svg?logo=discord&logoColor=white)](https://discord.gg/yourdiscord)
[![GitHub Issues](https://img.shields.io/badge/GitHub-Issues-black.svg?logo=github)](https://github.com/sqrilizz/Sqrilizz-Reports/issues)
[![Modrinth](https://img.shields.io/badge/Modrinth-Comments-00AF5C.svg?logo=modrinth)](https://modrinth.com/plugin/sqrilizz-report)

### Contributing

1. **Fork** the repository
2. **Create** a feature branch
3. **Commit** your changes
4. **Push** to the branch
5. **Create** a Pull Request

### Reporting Bugs

Use our [Bug Report Template](https://github.com/sqrilizz/Sqrilizz-Reports/issues/new?template=bug_report.md)

Include:
- Minecraft version
- Server software (Paper/Spigot/Folia)
- Plugin version
- Error logs
- Steps to reproduce

---

<div align="center">

**Made with ❤️ by [Sqrilizz](https://modrinth.com/user/Sqrilizz)**

[![GitHub](https://img.shields.io/badge/GitHub-sqrilizz-black.svg?logo=github)](https://github.com/sqrilizz)
[![Modrinth](https://img.shields.io/badge/Modrinth-Sqrilizz-00AF5C.svg?logo=modrinth)](https://modrinth.com/user/Sqrilizz)
[![Website](https://img.shields.io/badge/Website-sqrilizz.xyz-blue.svg)](https://sqrilizz.xyz)

</div>
