# 🤖 Discord Bot Guide - Sqrilizz-Reports

## 📋 Обзор

Discord Bot для Sqrilizz-Reports предоставляет полную интеграцию с Discord сервером, включая:
- 📢 Автоматические уведомления о новых жалобах
- 🛡️ Команды модерации через Discord
- 📊 Просмотр статистики жалоб
- ⚙️ Настройка через игру

## 🚀 Быстрый старт

### 1. Создание Discord Bot

1. Перейдите на [Discord Developer Portal](https://discord.com/developers/applications)
2. Создайте новое приложение
3. Перейдите в раздел "Bot"
4. Создайте бота и скопируйте токен
5. Включите необходимые Intents:
   - `GUILD_MESSAGES`
   - `MESSAGE_CONTENT`

### 2. Приглашение бота на сервер

Используйте OAuth2 URL Generator с правами:
- `bot`
- `applications.commands`
- Permissions: `Send Messages`, `Use Slash Commands`, `Embed Links`

### 3. Настройка в игре

```bash
# Установка токена
/report-discord token YOUR_BOT_TOKEN

# Установка Guild ID (ID вашего Discord сервера)
/report-discord guild 123456789012345678

# Установка Channel ID (ID канала для уведомлений)
/report-discord channel 123456789012345678

# Включение бота
/report-discord enable

# Включение модерации
/report-discord moderation true
```

## 📊 Команды администратора

### `/report-discord <параметр> <значение>`

| Параметр | Описание | Пример |
|----------|----------|---------|
| `token` | Установить токен бота | `/report-discord token YOUR_TOKEN` |
| `guild` | ID Discord сервера | `/report-discord guild 123456789012345678` |
| `channel` | ID канала для уведомлений | `/report-discord channel 123456789012345678` |
| `enable` | Включить бота | `/report-discord enable` |
| `disable` | Выключить бота | `/report-discord disable` |
| `moderation` | Включить/выключить модерацию | `/report-discord moderation true` |
| `status` | Показать статус бота | `/report-discord status` |

## 🛡️ Slash команды Discord

### Модерационные команды

#### `/ban <player> [reason]`
- **Описание**: Банит игрока на сервере
- **Параметры**:
  - `player` (обязательно) - Имя игрока
  - `reason` (опционально) - Причина бана
- **Пример**: `/ban Griefer123 Читерство`

#### `/kick <player> [reason]`
- **Описание**: Кикает игрока с сервера
- **Параметры**:
  - `player` (обязательно) - Имя игрока
  - `reason` (опционально) - Причина кика
- **Пример**: `/kick Spammer Спам в чате`

#### `/mute <player> [duration] [reason]`
- **Описание**: Мутит игрока
- **Параметры**:
  - `player` (обязательно) - Имя игрока
  - `duration` (опционально) - Длительность в минутах (по умолчанию 60)
  - `reason` (опционально) - Причина мута
- **Пример**: `/mute Toxic_Player 120 Токсичное поведение`

#### `/warn <player> [reason]`
- **Описание**: Предупреждает игрока
- **Параметры**:
  - `player` (обязательно) - Имя игрока
  - `reason` (опционально) - Причина предупреждения
- **Пример**: `/warn NewPlayer Нарушение правил`

### Информационные команды

#### `/reports [player]`
- **Описание**: Показывает жалобы
- **Параметры**:
  - `player` (опционально) - Имя игрока для просмотра его жалоб
- **Примеры**: 
  - `/reports` - общая статистика
  - `/reports Cheater123` - жалобы на конкретного игрока

## ⚙️ Конфигурация

### config.yml

```yaml
# Discord Bot settings (built-in bot)
discord-bot:
  enabled: true
  token: "YOUR_BOT_TOKEN_HERE"
  guild-id: "123456789012345678"
  channel-id: "123456789012345678"
  mod-roles: # List of moderator role IDs
    - "123456789012345678"
    - "987654321098765432"
  status: "Watching reports 👀"
  moderation:
    enabled: true
    commands:
      ban: true
      kick: true
      mute: true
      warn: true
```

## 🔐 Права доступа

### Роли модераторов

Добавьте ID ролей в `mod-roles`:
```yaml
mod-roles:
  - "123456789012345678"  # Moderator role
  - "987654321098765432"  # Admin role
```

### Автоматические права

- Пользователи с правом `Administrator` автоматически могут использовать все команды
- Пользователи с ролями из `mod-roles` могут использовать команды модерации

## 📢 Уведомления

### Формат уведомлений о жалобах

```
🚨 Новая жалоба
👤 Жалобщик: PlayerName (или *Анонимно*)
🎯 Цель: TargetPlayer
📝 Причина: Описание нарушения
📍 Локация жалобщика: world: 100, 64, -200
📍 Локация цели: world: 150, 80, -180
⏰ Время: 2025-09-21 18:15:30
```

### Анонимные жалобы

Если включены анонимные жалобы (`anonymous-reports: true`), имя жалобщика будет показано как "*Анонимно*".

## 🛠️ Устранение неполадок

### Бот не подключается

1. Проверьте токен: `/report-discord status`
2. Убедитесь что бот приглашен на сервер
3. Проверьте права бота в Discord

### Команды не работают

1. Убедитесь что модерация включена: `/report-discord moderation true`
2. Проверьте Guild ID: `/report-discord status`
3. Убедитесь что у пользователя есть нужная роль

### Уведомления не приходят

1. Проверьте Channel ID: `/report-discord status`
2. Убедитесь что бот имеет права на отправку сообщений в канал
3. Проверьте что бот включен: `/report-discord enable`

## 📝 Примеры использования

### Настройка с нуля

```bash
# 1. Установка токена
/report-discord token MTIzNDU2Nzg5MDEyMzQ1Njc4.GhIjKl.MnOpQrStUvWxYzAbCdEfGhIjKlMnOpQrStUvWxYz

# 2. Получение Guild ID (включите режим разработчика в Discord)
/report-discord guild 123456789012345678

# 3. Получение Channel ID
/report-discord channel 987654321098765432

# 4. Включение бота
/report-discord enable

# 5. Включение модерации
/report-discord moderation true

# 6. Проверка статуса
/report-discord status
```

### Модерация через Discord

```bash
# Бан читера
/ban Cheater123 Использование читов

# Кик за спам
/kick Spammer Спам в чате

# Мут на 2 часа
/mute ToxicPlayer 120 Токсичное поведение

# Предупреждение новичку
/warn NewPlayer Прочитайте правила сервера

# Просмотр жалоб
/reports Cheater123
```

## 🎨 Кастомизация

### Изменение статуса бота

В `config.yml`:
```yaml
discord-bot:
  status: "Ваш кастомный статус"
```

### Отключение определенных команд

```yaml
discord-bot:
  moderation:
    commands:
      ban: false    # Отключить команду бана
      kick: true
      mute: true
      warn: true
```

---

**Discord Bot делает модерацию сервера еще более удобной! 🚀**
