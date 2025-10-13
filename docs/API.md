# 🛠️ Developer API - Sqrilizz-Reports

[![Version](https://img.shields.io/badge/version-7.2-brightgreen.svg)](https://modrinth.com/plugin/sqrilizz-report)
[![Java](https://img.shields.io/badge/java-21-orange.svg)](https://openjdk.java.net/)

> **Complete API documentation for developers**

---

## 📦 Maven/Gradle Dependency

### Maven
```xml
<dependency>
    <groupId>dev.sqrilizz</groupId>
    <artifactId>sqrilizz-reports</artifactId>
    <version>7.2</version>
    <scope>provided</scope>
</dependency>
```

### Gradle
```gradle
compileOnly 'dev.sqrilizz:sqrilizz-reports:7.2'
```

---

## 🚀 Quick Start

### Creating Reports
```java
import dev.sqrilizz.SQRILIZZREPORTS.api.ReportAPI;

// Create a report from another plugin
ReportAPI.createReport(reporter, target, "Automated detection: Flying");

// Create system report (no player reporter)
ReportAPI.createSystemReport(target, "AntiCheat", "Speed hacking detected");
```

### Event Listening
```java
import dev.sqrilizz.SQRILIZZREPORTS.api.ReportEvent;

// Listen for new reports
ReportAPI.onReportCreate(event -> {
    String reporter = event.getReporterName();
    String target = event.getTargetName();
    String reason = event.getReason();
    
    // Custom handling
    if (reason.contains("hack")) {
        broadcastToStaff("Possible hacker: " + target);
    }
});
```

---

## 📚 API Reference

### ReportAPI Class

#### Methods

| Method | Description | Parameters | Returns |
|--------|-------------|------------|---------|
| `createReport()` | Create a new report | `Player reporter, Player target, String reason` | `boolean` |
| `createSystemReport()` | Create system report | `Player target, String systemName, String reason` | `boolean` |
| `getReports()` | Get reports for player | `Player target` | `List<Report>` |
| `clearReports()` | Clear player reports | `Player target` | `void` |
| `hasReports()` | Check if player has reports | `Player target` | `boolean` |
| `onReportCreate()` | Register event listener | `Consumer<ReportEvent> listener` | `void` |

---

## 🔗 Webhook Integration

### JSON Format
```json
{
  "type": "report",
  "timestamp": 1695307200000,
  "reporter": "AdminUser",
  "target": "SuspiciousPlayer", 
  "reason": "Flying hacks detected",
  "is_system_report": true,
  "system_name": "AntiCheat"
}
```

### Registering Webhooks
```java
import dev.sqrilizz.SQRILIZZREPORTS.api.WebhookManager;

// Register webhook for reports
WebhookManager.registerWebhook("report", "https://your-server.com/webhook");
```

---

## 📞 Support

[![Discord](https://img.shields.io/badge/Discord-Join_Server-7289da.svg?logo=discord&logoColor=white)](https://discord.gg/yourdiscord)
[![GitHub Issues](https://img.shields.io/badge/GitHub-Issues-black.svg?logo=github)](https://github.com/Sqrilizz/Sqrilizz-Reports/issues)
