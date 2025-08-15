# Sqrilizz-Reports

Плагин для Minecraft серверов, позволяющий отправлять жалобы на игроков через Telegram и Discord.

## Описание

Sqrilizz-Reports - это плагин для Minecraft, который позволяет игрокам отправлять жалобы на нарушителей через Telegram и Discord. Плагин поддерживает мультиязычность (русский и английский) и предоставляет удобный интерфейс для администраторов.

### Основные функции

- Отправка жалоб через команду `/report`
- Поддержка Telegram и Discord уведомлений
- Мультиязычность (русский/английский)
- Система управления вебхуками Discord
- Подробное логирование

## Установка

1. Скачайте последнюю версию плагина из раздела Releases
2. Поместите файл .jar в папку `plugins` вашего сервера
3. Перезапустите сервер
4. Настройте конфигурацию в файле `config.yml`

## Команды

- `/report <игрок> <причина>` - Отправить жалобу на игрока
- `/report reload` - Перезагрузить конфигурацию
- `/report language <ru/en>` - Изменить язык плагина
- `/report webhook <url>` - Установить Discord вебхук
- `/report webhook remove` - Удалить Discord вебхук

## Конфигурация

Основные настройки находятся в файле `config.yml`:

```yaml
# Настройки Telegram
telegram:
  enabled: true
  bot-token: "YOUR_BOT_TOKEN"
  chat-id: "YOUR_CHAT_ID"

# Настройки Discord
discord:
  enabled: true
  webhook-url: "YOUR_WEBHOOK_URL"

# Настройки языка
language: "ru" # ru или en
```
# Sqrilizz-Reports

A Minecraft plugin that allows sending player reports through Telegram and Discord.


### Key Features

- Report submission via `/report` command
- Telegram and Discord notifications
- Multilingual support (Russian/English)
- Discord webhook management system
- Detailed logging

## Commands

- `/report <player> <reason>` - Submit a report against a player
- `/report reload` - Reload configuration
- `/report language <ru/en>` - Change plugin language
- `/report webhook <url>` - Set Discord webhook
- `/report webhook remove` - Remove Discord webhook

## Configuration

Main settings are located in `config.yml`:

```yaml
# Telegram settings
telegram:
  enabled: true
  bot-token: "YOUR_BOT_TOKEN"
  chat-id: "YOUR_CHAT_ID"

# Discord settings
discord:
  enabled: true
  webhook-url: "YOUR_WEBHOOK_URL"

# Language settings
language: "ru" # ru or en
```
