MySQL Support Setup Instructions
================================

To enable MySQL support for Sqrilizz-Reports:

1. Download mysql-connector-j.jar from:
   https://dev.mysql.com/downloads/connector/j/

2. Place the JAR file in this folder (plugins/Sqrilizz-Reports/lib/)

3. Restart the server

The plugin will automatically detect and load the MySQL driver.
If MySQL driver is not found, the plugin will use SQLite instead.
