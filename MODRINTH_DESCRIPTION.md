# Sqrilizz-Reports 11.0.0

Network-ready player and bug reporting system for modern Paper, Purpur, and Folia servers.

> **Project Status — Maintenance Only:**
> This project is no longer under active development. No new features, requests, or manual updates are planned. The project runs in an autonomous state with automated monthly dependency bumps to newer PaperMC builds. If you need new functionality or custom modifications, feel free to fork the source code on GitHub.

---

## Downloads

| File | Environment | Description |
|---|---|---|
| `Sqrilizz-Reports-Standard-Edition-11.0.0.jar` | Paper / Folia | Recommended lightweight build (~2 MB) using server-provided or runtime JDBC drivers |
| `Sqrilizz-Reports-Bot-Edition-11.0.0.jar` | Paper / Folia | Bundled build containing offline SQLite and MySQL JDBC drivers |
| `Sqrilizz-Reports-Velocity-11.0.0.jar` | Velocity | Proxy bridge forwarding staff cross-server actions |
| `Sqrilizz-Reports-Bungee-11.0.0.jar` | BungeeCord | Proxy bridge forwarding staff cross-server actions |

---

## Requirements

- **Minecraft:** 26.3+
- **Server:** Paper, Purpur, or Folia
- **Java:** 25 or higher

---

## Key Features

- **Folia Native:** Full async safety using region and player schedulers.
- **Interactive GUI:** Visual moderation queue (`/reports`) with player heads, pagination, and quick actions (resolve, dismiss, teleport, punish).
- **Bug Reporting:** Separate bug report queue (`/bugreport`) with 11 categorized queues.
- **HikariCP Storage:** High-performance connection pooling for SQLite (default) and MySQL.
- **Lightweight Notifications:** Direct Discord Webhook and Telegram HTTP alerts without heavy third-party bot gateway daemons.
- **Multi-Server Networks:** Cross-server moderator transfer via Velocity or BungeeCord.
- **Localization:** English, Russian, and Arabic built-in.

---

## Commands & Permissions

### Players
- `/report <player> <reason>` — Report an online player (`sqrilizzreports.report`)
- `/bugreport <category> <description>` — Submit a bug report (`sqrilizzreports.bugreport`)

### Staff
- `/reports` — Open the moderation queue GUI (`sqrilizzreports.admin`)
- `/reports open <id>` — View details for a report
- `/reports resolve <id>` — Mark a report as resolved
- `/reports dismiss <id>` — Mark a report as invalid
- `/reports tp <player>` — Teleport to reported player
- `/reports punish <player> <warn|kick|mute|ban>` — Execute configured punishment preset (`sqrilizzreports.punish`)
- `/reports stats` — Display queue statistics
- `/reports reload` — Reload config and language files

---

## Source & Links

- **GitHub Repository:** [https://github.com/Sqrilizz/Sqrilizz-Reports](https://github.com/Sqrilizz/Sqrilizz-Reports)
- **Issue Tracker:** [GitHub Issues](https://github.com/Sqrilizz/Sqrilizz-Reports/issues)
- **License:** SCL
