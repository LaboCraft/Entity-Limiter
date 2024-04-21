# Entity-Limiter

Entities (minecart, boat and armorstand) are a source of lag for your minecraft server.
This plugin limits the number of entity per chunk and protect you from players who want to crash your server with a large number of entity.

## Feature
* Set entity (minecart, boat and armorstand) limit in a Chunk
* Check the world name avoid unwanted removal
* Timer that checks every X minutes in the loaded chunks the number of entities inside the chunk
* Check TPS status and if too low check and remove the entity in a or chunk ( this check is also performed only on loaded chunks )
* Ability to limit players to placing a maximum of entity per chunk
* Discord and inGame notication of when entities are removed due to limit violation
* No Dependency!
* Open Source!

This project was inspired by the work on [ArmorStand-Limiter](https://github.com/xSavior-of-God/ArmorStand-Limiter) who allows you to limit armor stands.

## Commands
* **/ml reload** - *Reload the config file (Perms: `entitylimiter.use`)*
* **/ml check** - *Show how many minecart/boat/armorstand there are in your actual chunk (Perms: `entitylimiter.use`)*

# Support

[![support image](https://www.heroxwar.com/discordLogo.png)](https://discord.com/invite/3QGe3ts)

**[https://discord.com/invite/3QGe3ts](https://discord.com/invite/3QGe3ts)**


# Download
**[https://github.com/LaboCraft/Entity-Limiter/releases/tag/latest](https://github.com/LaboCraft/Entity-Limiter/releases/tag/latest)**
