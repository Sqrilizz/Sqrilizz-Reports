# Changelog

All notable changes to this project will be documented in this file.

## [7.3] - 2025-09-21

### 🛠️ Critical Dependency Fix
- **Fixed NoClassDefFoundError**: Resolved "org.telegram.telegrambots.meta.generics.LongPollingBot" error
- **Shadow JAR Implementation**: Added Shadow plugin to properly include all dependencies in JAR
- **Dependency Inclusion**: Telegram Bot API, JDA, and Gson libraries now properly bundled
- **Enhanced Error Handling**: Added graceful fallbacks for missing dependencies
- **Plugin Stability**: Plugin now loads successfully even if some features are unavailable

### 🛠️ Folia Compatibility Fix
- **Fixed Folia Support**: Resolved "plugin not marked as supporting Folia" error
- **Enhanced Scheduler**: Improved Folia scheduler implementation with proper region scheduling
- **Better Compatibility**: Added proper `folia-supported: true` flag in plugin.yml
- **Scheduler Refactoring**: Updated all scheduler usages to use Folia-compatible methods

### 🔧 Technical Improvements
- Added Shadow JAR plugin for proper dependency management
- Added `Main.runTaskAsync()` method for Folia-compatible async tasks
- Added `Main.runTaskTimerAsync()` method for Folia-compatible repeating async tasks  
- Added `Main.runTask()` method for Folia-compatible sync tasks
- Updated all scheduler calls across the codebase to use new methods
- Enhanced error handling with fallbacks to regular Bukkit scheduler
- Improved plugin initialization with optional feature loading

### 📝 Updated Files
- `plugin.yml` - Added `folia-supported: true` flag
- `Main.java` - Enhanced scheduler implementation with Folia support
- `DiscordBot.java` - Updated to use new scheduler methods
- `TelegramManager.java` - Updated to use new scheduler methods
- `DiscordWebhookManager.java` - Updated to use new scheduler methods
- `WebhookManager.java` - Updated to use new scheduler methods

### 🚀 Platform Support
- **Folia**: Now fully supported without errors
- **Paper**: Continues to work optimally
- **Spigot**: Continues to work with good performance

## [7.2] - 2025-09-21

### 🎉 Major Features Added
- **🤖 Discord Bot Integration**: Full Discord bot with slash commands (/ban, /kick, /mute, /warn, /reports)
- **📋 Tab Completion**: Complete tab completion for all commands with smart suggestions
- **🌍 Multi-Language Documentation**: Added Russian and Arabic README versions
- **📚 Modular Documentation**: Organized documentation into specialized guides

### ✨ New Features
- Discord Bot with moderation commands and embed notifications
- ReportsTabCompleter for enhanced user experience
- DiscordBotCommand for easy Discord bot configuration
- Language-specific README files (EN/RU/AR)
- Specialized documentation in docs/ folder

### 🔧 Technical Improvements
- Added JDA 5.0.0-beta.24 dependency for Discord integration
- Enhanced command registration system
- Improved plugin initialization order
- Better error handling for Discord operations

### 📝 Documentation
- **README.md** (English) - Main documentation
- **README-RU.md** (Russian) - Russian version
- **README-AR.md** (Arabic) - Arabic version with RTL support
- **docs/DISCORD_BOT.md** - Complete Discord Bot guide
- **docs/API.md** - Developer API documentation
- **docs/INSTALLATION.md** - Installation guide
- **docs/CONFIGURATION.md** - Configuration reference
- **docs/TELEGRAM.md** - Telegram integration
- **docs/ANTI_ABUSE.md** - Anti-abuse system guide
- **docs/DESIGN.md** - Design system documentation

### 🗑️ Cleanup
- Removed duplicate documentation files
- Cleaned up temporary build files
- Organized project structure

### 🔄 Updated Files
- `Main.java` - Added Discord bot initialization
- `plugin.yml` - Added /report-discord command
- `build.gradle` - Added JDA dependency
- All README files updated with new version badges

## [7.0] - 2025-08-18

### 🎯 Major Features
- **Multi-Version Support**: Full support for Minecraft versions 1.8.9 to 1.21.8
- **Folia Support**: Complete compatibility with Folia servers
- **UUID Support**: Modern player identification with fallbacks for older versions
- **Version Detection**: Automatic server version detection and compatibility adjustment
- **Multi-Release JAR**: Enhanced JAR structure for better version compatibility
- **Enhanced Performance**: Optimized for all supported versions

### 🔧 Technical Improvements
- **VersionUtils Class**: Comprehensive utility class for handling version-specific operations
- **Enhanced Main Class**: Advanced version detection and Folia support
- **Updated CooldownManager**: Modern UUID-based system with legacy fallbacks
- **Improved Scheduler**: Version-compatible scheduler with Folia support
- **Better Error Handling**: Enhanced error handling for different versions
- **Optimized Memory Usage**: Reduced memory footprint across all versions

### 📝 Code Changes

#### New Files
- `VersionUtils.java` - Version compatibility utility class
- `VERSION_COMPATIBILITY.md` - Detailed version compatibility guide
- `BUILD_INSTRUCTIONS.md` - Build instructions for different versions

#### Updated Files
- `Main.java` - Added version detection and Folia support
- `build.gradle` - Updated for multi-version support
- `plugin.yml` - Updated API version and added platform support
- `config.yml` - Enhanced configuration with version compatibility settings
- `CooldownManager.java` - Updated to use UUIDs with legacy support
- `ReportCommand.java` - Updated to use VersionUtils
- `AdminReportsCommand.java` - Updated to use VersionUtils
- `LanguageCommand.java` - Updated to use VersionUtils
- `TelegramCommand.java` - Updated to use VersionUtils
- `WebhookCommand.java` - Updated to use VersionUtils
- `ReportManager.java` - Updated to use version-compatible scheduler
- `TelegramManager.java` - Updated configuration paths and scheduler
- `DiscordWebhookManager.java` - Updated configuration paths and scheduler

### 🌐 Configuration Changes
- Updated Telegram configuration paths (`bot-token` → `token`, `chat-id` → `chat_id`)
- Updated Discord configuration paths (`webhook-url` → `discord.webhook_url`)
- Added version compatibility settings
- Enhanced permission system configuration
- Added new message keys for webhook commands
- Improved configuration structure and organization

### 🔄 Backward Compatibility
- **Legacy Support**: Maintains compatibility with older versions using fallbacks
- **UUID Fallbacks**: Uses name-based identification for very old versions
- **API Fallbacks**: Provides reflection-based fallbacks for deprecated APIs
- **Permission Fallbacks**: Graceful degradation for older permission systems
- **Automatic Migration**: Seamless upgrade from previous versions

### 🚀 Performance Improvements
- **Optimized Scheduler**: Better performance with Folia's regional scheduler
- **Reduced Memory Usage**: More efficient UUID handling
- **Faster Version Detection**: Optimized version checking algorithms
- **Better Async Operations**: Improved asynchronous task handling
- **Enhanced Caching**: Better resource management

### 🛠️ Build System
- **Multi-Release JAR**: Support for different Java versions
- **Enhanced Dependencies**: Updated dependency management
- **Better Compilation**: Improved compilation settings for compatibility
- **Gradle Optimization**: Streamlined build process
- **Cross-Platform Support**: Works on Windows, Linux, and macOS

### 📚 Documentation
- **Enhanced README**: Comprehensive documentation with version support
- **Version Guide**: Detailed compatibility information
- **Build Instructions**: Step-by-step build guide
- **Migration Guide**: Instructions for upgrading from older versions
- **API Documentation**: Complete API reference

### 🔧 Bug Fixes
- Fixed permission checking for older versions
- Resolved scheduler compatibility issues
- Fixed configuration loading problems
- Corrected message handling for different languages
- Improved error reporting and debugging

### 🎮 Command Updates
- All commands now use VersionUtils for compatibility
- Enhanced error messages and user feedback
- Better permission handling
- Improved command validation
- Added command suggestions and help

### 🔗 Integration Updates
- **Telegram**: Updated configuration and error handling
- **Discord**: Enhanced webhook management
- **Language System**: Improved multi-language support
- **Permission System**: Better permission management
- **Plugin Hooks**: Better integration with other plugins

### 📋 Version Support Matrix

| Version Range | Status | Key Features |
|---------------|--------|--------------|
| 1.8.9 - 1.11.x | ✅ Supported | Legacy fallbacks, basic features |
| 1.12.x - 1.15.x | ✅ Supported | Full compatibility, enhanced features |
| 1.16.x | ✅ Supported | Modern API, optimal performance |
| 1.17.x - 1.18.x | ✅ Supported | Latest features, Java 16+ |
| 1.19.x | ✅ Supported | Enhanced security, modern Java |
| 1.20.x | ✅ Supported | Latest updates, best performance |
| 1.21.x | ✅ Supported | Cutting-edge features, Folia support |

### 🚀 Platform Support
- **Paper**: Full support with optimal performance
- **Spigot**: Full support with good performance
- **Folia**: Full support with regional scheduling
- **Bukkit**: Basic support for legacy servers

### 🔄 Migration Notes
- **Automatic Migration**: Most settings migrate automatically
- **Configuration Updates**: New config options available
- **Permission Updates**: Enhanced permission system
- **Backup Recommended**: Always backup before updating
- **Zero-Downtime**: Seamless upgrade process

### 📞 Support
- Enhanced error reporting
- Better debugging information
- Comprehensive logging
- Version-specific troubleshooting
- Community support channels

---

## [6.0] - 2025-08-18

### 🎯 Major Features
- **Multi-Version Support**: Added support for Minecraft versions 1.8.9 to 1.21.8
- **Folia Support**: Full compatibility with Folia servers
- **UUID Support**: Modern player identification with fallbacks for older versions
- **Version Detection**: Automatic server version detection and compatibility adjustment
- **Multi-Release JAR**: Enhanced JAR structure for better version compatibility

### 🔧 Technical Improvements
- **VersionUtils Class**: New utility class for handling version-specific operations
- **Enhanced Main Class**: Added version detection and Folia support
- **Updated CooldownManager**: Now uses UUIDs with legacy fallbacks
- **Improved Scheduler**: Version-compatible scheduler with Folia support
- **Better Error Handling**: Enhanced error handling for different versions

### 📝 Code Changes

#### New Files
- `VersionUtils.java` - Version compatibility utility class
- `VERSION_COMPATIBILITY.md` - Detailed version compatibility guide
- `BUILD_INSTRUCTIONS.md` - Build instructions for different versions

#### Updated Files
- `Main.java` - Added version detection and Folia support
- `build.gradle` - Updated for multi-version support
- `plugin.yml` - Updated API version and added platform support
- `config.yml` - Enhanced configuration with version compatibility settings
- `CooldownManager.java` - Updated to use UUIDs with legacy support
- `ReportCommand.java` - Updated to use VersionUtils
- `AdminReportsCommand.java` - Updated to use VersionUtils
- `LanguageCommand.java` - Updated to use VersionUtils
- `TelegramCommand.java` - Updated to use VersionUtils
- `WebhookCommand.java` - Updated to use VersionUtils
- `ReportManager.java` - Updated to use version-compatible scheduler
- `TelegramManager.java` - Updated configuration paths and scheduler
- `DiscordWebhookManager.java` - Updated configuration paths and scheduler

### 🌐 Configuration Changes
- Updated Telegram configuration paths (`bot-token` → `token`, `chat-id` → `chat_id`)
- Updated Discord configuration paths (`webhook-url` → `discord.webhook_url`)
- Added version compatibility settings
- Enhanced permission system configuration
- Added new message keys for webhook commands

### 🔄 Backward Compatibility
- **Legacy Support**: Maintains compatibility with older versions using fallbacks
- **UUID Fallbacks**: Uses name-based identification for very old versions
- **API Fallbacks**: Provides reflection-based fallbacks for deprecated APIs
- **Permission Fallbacks**: Graceful degradation for older permission systems

### 🚀 Performance Improvements
- **Optimized Scheduler**: Better performance with Folia's regional scheduler
- **Reduced Memory Usage**: More efficient UUID handling
- **Faster Version Detection**: Optimized version checking algorithms
- **Better Async Operations**: Improved asynchronous task handling

### 🛠️ Build System
- **Multi-Release JAR**: Support for different Java versions
- **Enhanced Dependencies**: Updated dependency management
- **Better Compilation**: Improved compilation settings for compatibility
- **Gradle Optimization**: Streamlined build process

### 📚 Documentation
- **Enhanced README**: Comprehensive documentation with version support
- **Version Guide**: Detailed compatibility information
- **Build Instructions**: Step-by-step build guide
- **Migration Guide**: Instructions for upgrading from older versions

### 🔧 Bug Fixes
- Fixed permission checking for older versions
- Resolved scheduler compatibility issues
- Fixed configuration loading problems
- Corrected message handling for different languages

### 🎮 Command Updates
- All commands now use VersionUtils for compatibility
- Enhanced error messages and user feedback
- Better permission handling
- Improved command validation

### 🔗 Integration Updates
- **Telegram**: Updated configuration and error handling
- **Discord**: Enhanced webhook management
- **Language System**: Improved multi-language support
- **Permission System**: Better permission management

### 📋 Version Support Matrix

| Version Range | Status | Key Features |
|---------------|--------|--------------|
| 1.8.9 - 1.11.x | ✅ Supported | Legacy fallbacks, basic features |
| 1.12.x - 1.15.x | ✅ Supported | Full compatibility, enhanced features |
| 1.16.x | ✅ Supported | Modern API, optimal performance |
| 1.17.x - 1.18.x | ✅ Supported | Latest features, Java 16+ |
| 1.19.x | ✅ Supported | Enhanced security, modern Java |
| 1.20.x | ✅ Supported | Latest updates, best performance |
| 1.21.x | ✅ Supported | Cutting-edge features, Folia support |

### 🚀 Platform Support
- **Paper**: Full support with optimal performance
- **Spigot**: Full support with good performance
- **Folia**: Full support with regional scheduling

### 🔄 Migration Notes
- **Automatic Migration**: Most settings migrate automatically
- **Configuration Updates**: New config options available
- **Permission Updates**: Enhanced permission system
- **Backup Recommended**: Always backup before updating

### 📞 Support
- Enhanced error reporting
- Better debugging information
- Comprehensive logging
- Version-specific troubleshooting

---

## Previous Versions

### [5.0] - Previous Release
- Basic reports functionality
- Telegram integration
- Discord webhook support
- Multi-language support
- Basic admin commands

---

**Note**: Version 7.0 represents a major milestone with comprehensive multi-version support and enhanced performance. All existing functionality has been preserved while adding significant new capabilities for version compatibility and Folia support.
