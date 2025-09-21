# 🔧 Installation Guide - Sqrilizz-Reports

[![Version](https://img.shields.io/badge/version-7.2-brightgreen.svg)](https://modrinth.com/plugin/sqrilizz-report)
[![Minecraft](https://img.shields.io/badge/minecraft-1.8--1.21+-blue.svg)](https://www.minecraft.net/)

> **Complete installation guide for all platforms**

---

## 📋 Requirements

### System Requirements
- **Java**: 21+ (recommended)
- **Minecraft**: 1.8.8 - 1.21+
- **RAM**: 512MB+ available
- **Storage**: 50MB+ free space

### Supported Platforms
![Paper](https://img.shields.io/badge/Paper-✅-green.svg)
![Spigot](https://img.shields.io/badge/Spigot-✅-green.svg)
![Folia](https://img.shields.io/badge/Folia-✅-green.svg)

---

## 📦 Download

### Option 1: Modrinth (Recommended)
```bash
# Visit Modrinth page
https://modrinth.com/plugin/sqrilizz-report

# Download latest version
wget https://cdn.modrinth.com/data/sqrilizz-report/versions/7.2/Sqrilizz-Reports-7.2.jar
```

### Option 2: GitHub Releases
```bash
# Download from GitHub
wget https://github.com/Sqrilizz/Sqrilizz-Reports/releases/latest/download/Sqrilizz-Reports-7.2.jar
```

---

## ⚡ Quick Installation

### Step 1: Download Plugin
```bash
# Download to your computer
# Then upload to server via FTP/SFTP
```

### Step 2: Install Plugin
```bash
# Place in plugins folder
mv Sqrilizz-Reports-7.2.jar /path/to/server/plugins/

# Set permissions (if needed)
chmod 644 /path/to/server/plugins/Sqrilizz-Reports-7.2.jar
```

### Step 3: Restart Server
```bash
# Stop server
screen -S minecraft -X stuff "stop^M"

# Start server
screen -S minecraft -X stuff "./start.sh^M"
```

### Step 4: Verify Installation
```bash
# Check logs
tail -f logs/latest.log | grep "Sqrilizz"

# Should see: "SQRILIZZREPORTS has been enabled successfully!"
```

---

## 🔧 First Time Setup

### Basic Configuration
```yaml
# plugins/Sqrilizz-Reports/config.yml
language: en                    # en, ru, ar
cooldown: 60                   # seconds between reports
anonymous-reports: false        # enable anonymous reporting
```

### Test Installation
```bash
# In-game commands
/report TestPlayer Test reason
/reports
/report-stats
```

---

## 🐳 Docker Installation

### Docker Compose
```yaml
version: '3.8'
services:
  minecraft:
    image: itzg/minecraft-server
    environment:
      EULA: "TRUE"
      TYPE: "PAPER"
      VERSION: "1.20.4"
    volumes:
      - ./plugins:/data/plugins
      - ./Sqrilizz-Reports-7.2.jar:/data/plugins/Sqrilizz-Reports-7.2.jar
    ports:
      - "25565:25565"
```

---

## 🔄 Updating

### From Previous Version
```bash
# 1. Stop server
screen -S minecraft -X stuff "stop^M"

# 2. Backup old plugin
cp plugins/Sqrilizz-Reports-*.jar backups/

# 3. Replace with new version
rm plugins/Sqrilizz-Reports-*.jar
mv Sqrilizz-Reports-7.2.jar plugins/

# 4. Start server
screen -S minecraft -X stuff "./start.sh^M"
```

### Configuration Migration
- Config files are automatically updated
- Old settings are preserved
- New features use default values

---

## 🔍 Troubleshooting

### Common Issues

#### Plugin Not Loading
```bash
# Check Java version
java -version
# Should be 21+

# Check file permissions
ls -la plugins/Sqrilizz-Reports-7.2.jar
# Should be readable
```

#### Commands Not Working
```bash
# Check plugin status
/plugins
# Should show Sqrilizz-Reports in green

# Check permissions
# Make sure you have reports.admin
```

#### Performance Issues
```bash
# Check server resources
free -h
# Ensure enough RAM available

# Check plugin conflicts
# Disable other report plugins
```

---

## 📞 Support

Need help with installation?

[![Discord](https://img.shields.io/badge/Discord-Join_Server-7289da.svg?logo=discord&logoColor=white)](https://discord.gg/yourdiscord)
[![GitHub Issues](https://img.shields.io/badge/GitHub-Issues-black.svg?logo=github)](https://github.com/Sqrilizz/Sqrilizz-Reports/issues)
