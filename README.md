# RusherHack PlayerLog Plugin

Rusherhack plugin that log all player encounters in a log file and display them in the chat

# Features

#### Player Logging
- Log everyone that enter the render distance

#### Optional Chat Logging
- Logs can be displayed directly in Minecraft chat

#### Optional File Logging
- Logs can be saved in a local text file (.minecraft/rusherhack/logs/player_log.txt)

#### Optional Notification Logging
- Logs can be send as an in-game rusherhack notification

#### Optional Sound Alert
- A sound alert can be played when a player is encountered

#### In-Game Log Viewer Window
- A rusherhack window module to read the log file directly in game

#### Log all important information
- Log the date, server IP, name, coordinate and dimension

#### Optional NPC / Fake Player Filter
- Fake players (e.g. on mini-game servers) can be ignored to avoid false positives.

# Settings
The Filter NPC option work by not logging player that has a ping of 0. Unfortunately 2b2t doesn't give the ping of other players connected and send a ping of 0 for everybody instead. So activating this option will disable the plugin on 2b2t and servers that send a ping of 0 for everybody

# Screenshots

#### Exemple of log in the chat
![PlayerLogChat](img/PlayerLogChat.png)

#### Exemple of notification
![PlayerLogNotification](img/PlayerLogNotification.png)

#### Windows module that show the log file
![PlayerLogWindow](img/PlayerLogWindows.png)
