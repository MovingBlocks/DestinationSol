Destination Sol
==========

<p align="center"><img src="/main/imgSrcs/ui/title.png" alt="Destination Sol"/></p>

This is the official open source home for the arcade space shooter Destination Sol, originally started by Milosh Petrov and a small team on [Steam](http://store.steampowered.com/app/342980/) and [SourceForge](http://sourceforge.net/projects/destinationsol)

After receiving highly positive reviews launching as an indie title on Steam Milosh and the remaining team members wanted to focus on different projects, having made Destination Sol primarily to try out the involved technology.

A call was put out to the player community for a new maintainer, and open source was an option praised by many and already somewhat in place on Sourceforge. The open source group MovingBlocks behind [Terasology](http://terasology.org) stepped in to offer infrastructure and maintenance.

Milosh accepted our offer and supported us in moving the game onwards to its new home here on GitHub where we'll set up to accept contributions from anybody willing to help improve Destination Sol and expand on its gameplay.

Destination Sol is now officially licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html) and available in source code form at [GitHub](https://github.com/MovingBlocks/DestinationSol).

You can download the game on [Steam](http://store.steampowered.com/app/342980/), get it in the [Google Play Store](https://play.google.com/store/apps/details?id=com.miloshpetrov.sol2.android&hl=en), or download the [very latest version from our build server](http://jenkins.terasology.org/job/DestinationSol/lastSuccessfulBuild/artifact/desktop/build/distributions/DestinationSol.zip) (warning: latest build may be unstable)
 
Feel free to fork the project and contribute pull requests! You can visit a [Destination Sol forum](http://forum.terasology.org/forum/destination-sol.57/) on the Terasology site if you have any questions or would like to discuss the game or contributing.

Gameplay
--------

You start at the edge of a solar system as a pilot in a small ship. You are free to explore space, land on planets, fight with enemies, upgrade your ship and equipment, hire mercenaries, mine asteroids, and so on.

Enemy ships are orange icons, allies are blue. Enemies can be marked with a skull icon - beware! They are likely stronger than you. Improve your ship and equipment and fight them later!
 
Your ship has a certain number of hit points (your armor), which will recover if you have consumable repair kits in your inventory and stay idle for a short while. You may also have a shield that takes damage first. Each is vulnerable to different weapons, both on your ship and others.

Weapons and special abilities often need consumables to function (like Bullets or Slo Mo Charges) and take time to rearm.

You can destroy asteroids for easy money, even with the starting ship's ammo-less but weak gun.

Warnings get posted if you get close to dangerous ships or may soon collide with something on your current course. Blue dots along the edge of the screen indicate a planet is nearby.

Controls
--------

Note: You can select either pure keyboard or keyboard + mouse (in the settings). Exact details may change over time. Below are the default key mappings (no mouse)

*Main screen*

* [Space] - Fire main gun
* [Ctrl] - Fire secondary gun (if equipped)
* [Shift] - Use ship ability
* [Left,Right] - Turn the ship
* [Up] - Thrust. There are no brakes! You have to turn and burn to change direction or slow down 
* [Tab] - Show the map
* [I] - Show inventory
* [T] - Talk (interact with a station)
* [ESC] - Menu / close screens

*With map up*

* [Up, Down] - Zoom in and out on the map

*With inventory up*

* [Left, Right] - change page
* [Up, Down] - scroll up and down
* [Space] - equip / unequip item *OR* buy / sell if talking to a station
* [D] - discard selected item


Building and running from source
--------

You only need Java installed to run Destination Sol from source. Use Java 7 or 8, the newer the better.

Run any commands in the project root directory

* Download / clone the [source from GitHub](https://github.com/MovingBlocks/DestinationSol)
* To run from the command line: `gradlew run`
* To prepare for IntelliJ run: `gradlew idea` then load the generated project files
* To create a game package for distribution (Windows, Linux, Mac): `gradlew distZip`

Credits
--------

Original creators: Milosh Petrov, Nika Burimenko, Kent C. Jensen, Julia Nikolaeva 

Contributors on GitHub: Cervator, PrivateAlpha, theotherjay, Linus ... and your name here? :-) More coming!

Apologies in advance for any omissions, contact [Cervator](http://forum.terasology.org/members/cervator.2/) if you believe you've been missed :-)
