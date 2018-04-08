# BedwarsX

![Logo](https://www.dein-plugin.de/img/mimg132.png)

Bedwars gamemode implementation for Sponge.

** Early alpha, still in development! The plugin can contain a lot of bugs **


## Installation
BedwarsX doesn't require any dependencies, just put BedwarsX jar file to your server's **mods** folder.

## Gameplay
Bedwars is a PVP-game, where players in teams have to defend their Bed and destroy enemy Beds. If team's bed gets destroyed - team players can no longer respawn. Players buy better weapons, armor and utilites by collecting in-game currency: iron, gold, diamonds, emeralds.

## Features
* Very similiar to Hypixel version of Bedwars
* Up to 8 teams in a game
* Compact Bedwars shop
* Resets your game arena after each game

## Configuration

BedwarsX config file is **config/bedwarsx.conf**. Usually, you do not need to edit this file manually, because you can edit it using in-game commands.

## Commands

* /playbedwars (/joinbw, /playbw) - join Bedwars game. Does not have permission required to run it. Players should use that command to join the Bedwars game

* /bedwars (/bwx, /bedwarsx) - bedwars configuration command. For admins.

## Setting up the Game

To setup the game you need to:

* Build Bedwars arena. After each game, arena will be reset to it's previous state.
* Set Bedwars Lobby location.
* Decide how many teams in game do you want to have.
* For each team, set **spawn point**, **resource location** and, of course, the **bed**.
* Create additional **generators**, which will spawn diamonds or emeralds.
* Add Bedwars shops
* **Save the configuration**

### Set Lobby

**/bwx setlobby** or **/bwx sl** will set Bedwars lobby to your current location.

### Set Team Spawn Point

**/bwx setspawn [team]** or **/bwx ss [team]** will set team spawn point.

Possible teams:

* red
* blue
* green
* yellow
* aqua
* light_purple (pink wool)
* gray
* white

You can use TAB for team name auto-completion while typing the command.

### Set team resource location
Resource location is the place where team resources such as iron and gold will spawn.

**/bwx setresource [team]** or **/bwx sr [team]** will set team resource location to your current position. [team] argument accepts the same team names as stated above.

### Set Team Bed

Run **/bwx setbed [team]** or **/bwx sb [team]**. Then place the bed. Placed bed will be chosen team's bed.

### Creating generators
Generators periodically spawn diamonds or emeralds.

**/bwx setgenerator [name] [type]** or **/bwx sg [name] [type]** sets generator [name] on your current location. It will spawn items of [type]. [type] can be **diamond** or **emerald**. [name] can be any string, for example: **/bwx sg emerald_gen_south emerald**

### Adding bedwars shop

You should put a Shop at each team's base. To put a shop, just spawn a Villager on a desired location. In game, player interacting with villager will open Bedwars Shop instead of trading menu.

### Saving configuration
After setting up the game, you **must** save the Bedwars configuration to make it persistent between server restarts.

Run **/bwx save** to save Bedwars configuration.

### Stopping the game

You can stop a Bedwars game by running **/bwx stop**.

## Contributing

This is large project, so any code suggestions / bug reports / pull requests are welcomed. Just create an issue or a pull request.

# Author: MrOnlineCoder
# Licensed under Apache 2.0, see LICENSE file
