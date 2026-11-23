# Sqrilizz-Reports

Сетевая система обработки репортов игроков и багСетевая система обработки репортов игроков и баг-репортов для современных серверов Paper, Purpur и Foliaрепортов для современных серверов Paper, Purpur и Folia.

[![Minecraft](https://img.shields.io/badge/minecraft-26.3-blue.svg)](https://papermc.io/)
[![Java](https://img.shields.io/badge/java-25-orange.svg)](https://openjdk.java.net/)
[![License](https://img.shields.io/badge/license-SCL-purple.svg)](LICENSE)[![Minecraft](https://img.shields.io/badge/minecraft-26.3-blue.svg)](https://papermc.io/)
[![Java](https://img.shields.io/badge/java-25-orange.svg)](https://openjdk.java.net/)
[![License](https://img.shields.io/badge/license-SCL-purple.svg)](LICENSE)

##> [!IMPORTANT]
> **Статус проекта — Только авто-обслуживание:**
> Данный проект больше активно не поддерживается и не разрабатывается. Не стоит ожидать новых функций, правок или обновлений. Репозиторий работает в автономном режиме: раз в месяц запускается только автоматический бамп зависимостей под новые сборки PaperMC. Если вам нужны доработки или новый функционал — вы можете свободно форкнуть репозиторий под свои нужды.

- Полная асинхронность с нативной поддержкой регион-шедулеров Folia
- Интерактивное GUI-меню с головами игроков и быстрыми действиями (решить, отклонить, телепорт, наказать)
- Встроенная система баг-репортов (`/bugreport`) с настраиваемыми категориями
- Хранение в SQLite (по умолчанию) и MySQL с пулом соединений HikariCP
- Отправка уведомлений в Discord (вебхуки) и Telegram
- Поддержка сетей серверов: прокси-мосты для Velocity и BungeeCord
- Мультиязычность: русский, английский, арабский
- Опциональный легковесный REST API для внешних панелей
## Возможности
- Полная асинхронность с нативной поддержкой регион-шедулеров Folia
- Интерактивное GUI-меню с головами игроков и быстрыми действиями (решить, отклонить, телепорт, наказать)
- Встроенная система баг-репортов (`/bugreport`) с настраиваемыми категориями
- Хранение в SQLite (по умолчанию) и MySQL с пулом соединений HikariCP
- Отправка уведомлений в Discord (вебхуки) и Telegram
- Поддержка сетей серверов: прокси-мосты для Velocity и BungeeCord
- Мультиязычность: русский, английский, арабский
- Опциональный легковесный REST API для внешних панелей

##Требования

## Модули и сборки- Minecraft: 26.3+
- Серверное ядро: Paper, Purpur или Folia
- Java: 25 или выше

| Файл | Платформа | Описание |
|---|---|---|
| `Sqrilizz-Reports-Standard-Edition-11.0.0.jar` | Paper / Folia | Легковесная сборка (~2 МБ) с использованием системных драйверов JDBC |
| `Sqrilizz-Reports-Bot-Edition-11.0.0.jar` | Paper / Folia | Автономная сборка со встроенными драйверами SQLite и MySQL |
| `Sqrilizz-Reports-Velocity-11.0.0.jar` | Velocity | Прокси-модуль для перенаправления действий модераторов |
| `Sqrilizz-Reports-Bungee-11.0.0.jar` | BungeeCord | Прокси-модуль для перенаправления действий модераторов |
Модулиисборки
## Команды
| Файл | Платформа | Описание |
|---|---|---|
| `Sqrilizz-Reports-Standard-Edition-11.0.0.jar` | Paper / Folia | Легковесная сборка (~2 МБ) с использованием системных драйверов JDBC |
| `Sqrilizz-Reports-Bot-Edition-11.0.0.jar` | Paper / Folia | Автономная сборка со встроенными драйверами SQLite и MySQL |
| `Sqrilizz-Reports-Velocity-11.0.0.jar` | Velocity | Прокси-модуль для перенаправления действий модераторов |
| `Sqrilizz-Reports-Bungee-11.0.0.jar` | BungeeCord | Прокси-модуль для перенаправления действий модераторов |

## ИгрокиКоманды

### Администраторы
- `/reports` — Открыть визуальную очередь жалоб
- `/reports open <id>` — Открыть подробности конкретного репорта
- `/reports resolve <id>` — Отметить жалобу как решенную
- `/reports dismiss <id>` — Отклонить жалобу
- `/reports tp <игрок>` — Телепортироваться к игроку
- `/reports punish <игрок> <warn|kick|mute|ban>` — Применить пресет наказания
- `/reports stats` — Показать статистику очереди
- `/reports reload` — Перезагрузить конфигурацию и языковые файлы
Игроки <игрок> <причина> — Отправить жалобу на игрока онлайн
- `/bugreport <категория> <описание>` — Отправить отчет об ошибке
##### Администраторы
- `/reports` — Открыть визуальную очередь жалоб
- `/reports open <id>` — Открыть подробности конкретного репорта
- `/reports resolve <id>` — Отметить жалобу как решенную
- `/reports dismiss <id>` — Отклонить жалобу
- `/reports tp <игрок>` — Телепортироваться к игроку
- `/reports punish <игрок> <warn|kick|mute|ban>` — Применить пресет наказания
- `/reports stats` — Показать статистику очереди
- `/reports reload` — Перезагрузить конфигурацию и языковые файлы

| Право | По умолчанию | Описание |
|---|---|---|
| `sqrilizzreports.report` | true | Доступ к команде `/report` |
| `sqrilizzreports.bugreport` | true | Доступ к команде `/bugreport` |
| `sqrilizzreports.admin` | op | Доступ к `/reports` и уведомлениям о новых жалобах |
| `sqrilizzreports.punish` | op | Доступ к выполнению наказаний из GUI |
| `sqrilizzreports.transfer` | op | Доступ к межсерверным перемещениям через прокси |
##Права(Permissions)
## Сборка из исходников| Право | По умолчанию | Описание |
|---|---|---|
| `sqrilizzreports.report` | true | Доступ к команде `/report` |
| `sqrilizzreports.bugreport` | true | Доступ к команде `/bugreport` |
| `sqrilizzreports.admin` | op | Доступ к `/reports` и уведомлениям о новых жалобах |
| `sqrilizzreports.punish` | op | Доступ к выполнению наказаний из GUI |
| `sqrilizzreports.transfer` | op | Доступ к межсерверным перемещениям через прокси |

Сборкаизисходников```bash
gitgit cloneclone https://github.comhttps://github.com/SqrilizzSqrilizz/Sqrilizz-Reports.gitSqrilizz-Reports.git
cd Sqrilizz-Reports
./gradlewgradlew buildbuild
```
