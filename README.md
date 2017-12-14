# Arc

Arc is an anticheat plugin made for Spigot 1.7.10-1.8.8.
Arc was made with performance in mind to ensure your server won't ever drop TPS or be laggy.
Arc is currently nowhere near finished. If you wish to contribute or help me out see below :).

# Requirements
Arc requires your server to be running java 8 to ensure the best performance.
Arc uses [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/) for packet level checks. This allows more accurate and advanced checks. Please download the latest version of ProtocolLib.

# Contributing
To build with gradle:
```
Windows :: gradlew clean build
Linux/Mac :: ./gradlew clean build
```
Credits to https://github.com/Mishyy for implementing gradle, thank you.


Found an issue or a bug? Please open an issue and explain how to reproduce the bug and give the stacktrace log(if any), please also provide a log of the "/arc info" command.

Want to contribute with a change or new check? Open a pull request with your changes and show what you changed/fixed.

# Features
Arc currently features these checks

* Movement Checks
  * Flight - this check blocks all types of flight including glide, ascension, hover, ladder, and jesus.
  * MorePackets - this is an advanced check that monitors how many packets the player is sending to prevent Speed, Regeneration, other malicious stuff.
  * Speed - this check monitors the movement of a player to make sure they are not going faster than allowed.
  * NoFall - this check enforces fall damage when a player attempts to fake they're not taking fall damage.
* Inventory Checks
  * FastConsume - this check ensures the player isn't consuming an item (food, potions, etc) too fast.
* Combat Checks
  * Criticals - this check ensures the player isn't landing critical hits when its not possible.
  * Regeneration - this check ensures the player isn't regenerating their health too fast.
  * KillAura - this check checks for many things, including(but not limited to): MultiAura, Direction, AttackSpeed, Reach, NoSwing

Arc has many more checks coming in the future, this is NOT a complete list.

# Screenshots
![alerts](https://i.imgur.com/Hab1UZ5.png)

![help](https://i.imgur.com/r6G6CB7.png)
  
