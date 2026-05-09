# GUI Guide - Sqrilizz-Reports

## Overview

The GUI system provides a visual interface for managing reports with player heads, pagination, and quick actions.

## Opening the GUI

Command: `/reports`

Permission: `reports.admin`

## Main Reports List

### Layout
- Player heads showing reported players
- Report count displayed on each head
- Navigation arrows for pagination
- Close button (red block)

### Actions
- **Left Click** on player head - View reports for that player
- **Arrow buttons** - Navigate between pages
- **Red block** - Close menu

### Pagination
- Shows up to 45 players per page
- Automatic page calculation
- Next/Previous buttons appear when needed

## Player Reports View

### Layout
- List of all reports for selected player
- Report details on each item
- Action buttons at bottom
- Navigation controls

### Report Information
- Report ID
- Reporter name
- Reason
- Timestamp
- Status (open/resolved)

### Actions
- **Click report** - Open action menu
- **Teleport button** - Teleport to reported player
- **Clear all button** - Remove all reports for player
- **Back button** - Return to main list
- **Navigation arrows** - Browse pages

## Report Actions Menu

### Available Actions

**Teleport to Reporter**
- Teleports you to the player who made the report
- Only works if player is online

**Teleport to Target**
- Teleports you to the reported player
- Only works if player is online

**Punish Player**
- Opens punishment menu
- Various punishment options available

**Resolve Report**
- Marks report as resolved
- Keeps report in history

**Delete Report**
- Permanently removes report
- Cannot be undone

**Back**
- Return to player reports list

## Punishment Menu

### Preset Punishments

**Warn**
- Issues warning to player
- Logged in report system

**Kick**
- Kicks player from server
- Reason shown to player

**Mute 1 Hour**
- Mutes player for 1 hour
- Requires compatible punishment plugin

**Mute 1 Day**
- Mutes player for 24 hours
- Requires compatible punishment plugin

**Ban 1 Day**
- Bans player for 24 hours
- Requires compatible punishment plugin

**Ban 7 Days**
- Bans player for 7 days
- Requires compatible punishment plugin

**Ban Permanent**
- Permanently bans player
- Requires compatible punishment plugin

**Back**
- Return to actions menu

### After Punishment
- Confirmation screen appears
- Options to resolve or keep report open
- Automatic return to reports list

## Keyboard Shortcuts

- **ESC** - Close current menu
- **Click outside** - Close menu (if enabled)

## Multi-Language Support

All GUI elements support multiple languages:
- English (en)
- Russian (ru)
- Arabic (ar)

Language is set in config.yml or per-player with `/report-language`

## Tips

1. Use pagination for servers with many reports
2. Right-click features may be added in future updates
3. GUI updates automatically when reports change
4. All actions are logged for audit trail
5. Punishment commands require compatible plugin

## Troubleshooting

**GUI not opening:**
- Check permission: `reports.admin`
- Verify language files are loaded
- Try `/report-reload`

**Player heads not showing:**
- Check server version (1.8.9+)
- Verify player names are valid
- Check console for errors

**Actions not working:**
- Verify target player is online (for teleport)
- Check punishment plugin is installed
- Review console logs

**Pagination issues:**
- Ensure reports exist
- Check page calculation in logs
- Try reloading plugin

## Configuration

GUI behavior can be customized in config.yml:

```yaml
# GUI settings (if available)
gui:
  items-per-page: 45
  auto-refresh: true
  close-on-action: false
```

## Permissions

- `reports.admin` - Access to GUI
- `reports.teleport` - Use teleport features (if separate)
- `reports.punish` - Use punishment menu (if separate)

## Future Features

Planned improvements:
- Custom punishment commands
- Report filtering
- Search functionality
- Bulk actions
- Export reports
