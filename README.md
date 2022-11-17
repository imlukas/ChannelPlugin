# ChannelPlugin
A Chat Channel management plugin, made for Devnics.
Data is stored both locally (channels) and on database (player-channel info) (SQL).
Changes might come in the future

## To add
  - More functions on the GUI such as: delete channels, set other player's channel, see other player's channels and so on.
  - Permission based channels
  - Member-limited channels


## Permissions:

   channelplugin.admin -> Makes so that player can use admin commands (/channel <create, delete, join, set, reset, uuid>)

## Commands:

- /channeladmin toggleprefix / toggleactionbar - Toggles actionbar/chat messages and prefix on messages

- /channel -> opens up a GUI for players to select their channel, based on available channels on the server. I dind't implement permission based channels but that could be an option. This GUI is paginable and dinamically updated.

- /channel create (name) <prefix> <range> <worlds...> - creates a channel

**The auto-complete shows you all the available UUIDs and a name besides it so you know what channel you're refering to.**

- /channel delete (channel-uuid) - deletes a channel

- /channel join (channel-uuid) - join a specific channel, without using the gui
 
- /channel set (player) (channel-uuid) - set a player's channel

- /channel reset - reset your channel to the default one (Global)

- /channel uuid - sends you a message with the UUID of your  current channel that you copy by clicking the message.
