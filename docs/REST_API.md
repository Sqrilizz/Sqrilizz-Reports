# 🌐 REST API - Sqrilizz-Reports

This document describes the built-in HTTP API exposed by the plugin to integrate with external tools and Discord bots.

- Base URL: `http://<host>:<port>` (default port: `8971`)
- All endpoints require Bearer token in `Authorization` header
- Optionally verify HMAC signature for POST requests
- Rate limit: configurable (default 10 req/s per IP)

---

## 🔐 Authentication

- Header: `Authorization: Bearer <token>`
- Configure token in `config.yml` under `rest-api.token`
- Optional IP whitelist under `rest-api.ip-whitelist`
- Optional HMAC for POST requests:
  - Header: `X-Signature: <base64(HMAC_SHA256(body, secret))>`
  - Secret configured at `rest-api.webhook.secret`

---

## 📋 Endpoints

### GET `/api/reports`
Return all active reports grouped by target.

Response example:
```json
[
  {
    "player": "Cheater123",
    "reports": [
      {
        "id": 17,
        "reporter": "AdminUser",
        "reason": "Читы",
        "timestamp": 1695307200000,
        "status": "open",
        "target": "Cheater123"
      }
    ]
  }
]
```

### GET `/api/reports/{player}`
Return reports for a specific player.

Response example:
```json
{
  "player": "Cheater123",
  "reports": [
    {
      "id": 17,
      "reporter": "AdminUser",
      "reason": "Читы",
      "timestamp": 1695307200000,
      "status": "open",
      "target": "Cheater123"
    }
  ]
}
```

### POST `/api/reports/{id}/resolve`
Mark a report as resolved.

Headers:
- `Authorization: Bearer <token>`
- If HMAC enabled: `X-Signature: <base64(HMAC_SHA256(body, secret))>` (body can be empty)

Response:
```json
{"message":"resolved"}
```

### POST `/api/reports/{id}/reply`
Add a moderator reply to a report.

Headers:
- `Authorization: Bearer <token>`
- If HMAC enabled: `X-Signature: <base64(HMAC_SHA256(body, secret))>`

Body:
```json
{
  "author": "AdminUser",
  "message": "Handled, user warned"
}
```

Response:
```json
{"message":"replied"}
```

### GET `/api/stats`
Lightweight stats for dashboards.

Response:
```json
{"players": 12, "reports": 42}
```

---

## ⚙️ Configuration (`config.yml`)

```yaml
rest-api:
  enabled: true
  port: 8971
  token: "REPLACE_ME"            # Bearer token
  ip-whitelist:
    enabled: false
    list: ["127.0.0.1", "10.0.0.15"]
  webhook:
    secret: ""                  # HMAC secret (optional)
  rate-limit:
    rps: 10
```

---

## 🚦 Rate Limiting
- Default: 10 requests/second per IP
- Returns HTTP `429` on limit exceeded

---

## 🔒 Security Best Practices
- Use a long random token (32+ chars)
- Enable IP whitelist if you call API from a fixed address
- If you expose POST endpoints to third parties, enable HMAC verification
- Prefer HTTPS via reverse proxy (e.g. Nginx) if API is public
