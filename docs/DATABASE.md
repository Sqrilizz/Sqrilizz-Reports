# 💾 Database Guide - Sqrilizz-Reports

This guide explains how storage works and how to switch between SQLite and MySQL without losing data.

---

## 📦 Drivers

- SQLite (default)
  - Zero configuration, stores data in `plugins/Sqrilizz-Reports/reports.db`
- MySQL
  - Use for production and multi-server setups
  - Connection pooling by HikariCP

---

## ⚙️ Configuration (`config.yml`)

```yaml
database:
  type: sqlite   # or mysql
  mysql:
    host: "localhost"
    port: 3306
    database: "sqrilizz_reports"
    user: "root"
    password: ""
    pool-size: 10
    params: "useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=utf8&serverTimezone=UTC"
```

- Set `type` to `sqlite` or `mysql`
- Adjust MySQL credentials and optional pool size

---

## 🔁 Hot Swapping (SQLite ⇄ MySQL)

You can switch the database type at runtime and migrate the current data:

1. Edit `config.yml` and set `database.type`
2. Run in-game: `/report-reload`
3. The plugin will:
   - Close the old datasource
   - Initialize the new datasource
   - Migrate current in-memory reports to the new backend

No server restart required.

---

## 🧰 Performance Tips

- Use MySQL for larger servers or multi-instance setups
- Tune `pool-size` if you have heavy REST/API usage
- Monitor slow queries and ensure indexes exist (default schema includes the essentials)

---

## 🧪 Schema Overview

Tables:

- `reports`
  - `id` (PK), `reporter`, `target`, `reason`, `ts`, `status`, locations, anonymity, resolver, `resolved_at`
- `replies`
  - `id` (PK), `report_id` (FK), `author`, `message`, `ts`

---

## 🛠️ Troubleshooting

- Ensure network access for MySQL (security groups / firewall)
- Verify credentials in `config.yml`
- Check logs for `MySQL/SQLite ... failed` messages
- If swapping fails, data is also stored as YAML backup `reports.yml` and `reports_backup.json` for recovery
