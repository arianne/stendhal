-------------------------------------------------------------------------------
| This program is free software; you can redistribute it and/or modify
| it under the terms of the GNU General Public License as published by
| the Free Software Foundation.
|
| This program is distributed in the hope that it will be useful,
| but WITHOUT ANY WARRANTY; without even the implied warranty of
| MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
| doc/GPL.license file for more details.
-------------------------------------------------------------------------------


Stendhal
--------

Are you looking for adventure? Want to fight for riches?
Develop yourself and your social standing? Meet new people?
Do you want to be part of a brave new world?

Stendhal is a fully fledged multiplayer online adventures game (MMORPG) developed using the Arianne game development system.

Stendhal features a new, rich and expanding world in which you can explore towns, buildings, plains, caves and dungeons.
You will meet NPCs and acquire tasks and quests for valuable experience and cold hard cash.

Your character will develop and grow and with each new level up become stronger and better. With the money you acquire you can buy new items and improve your
armour and weapons. And for the blood thirsty of you; satisfy your killing desires by roaming the world in search of evil monsters!

Stendhal is totally platform independent, written using Java 1.5 and the Java2D environment.

So what are you waiting for?! A whole new world awaits...


Current Version
---------------

Version 0.69


How to play
-----------

You need arianne's Stendhal client in order to be able to play Stendhal.
You also need Sun Java JRE 1.5 or higher ( http://www.java.com/en/download )

Please download Stendhal from http://arianne.sourceforge.net
Then unzip the client zip into a new folder. 
You can double click on stendhal-X.XX.jar and the client will run.

If you prefer to run from command line, then just do:

  java -jar path-to-folder/stendhal-X.XX.jar

Alternatively you can run it using Java Webstart technology at:

  http://stendhal.game-host.org/stendhal.jnlp

To play Stendhal you have to use a keyboard and a mouse.

There is a manual at
http://arianne.sourceforge.net/wiki/index.php/StendhalManual, and there is
Help from the client main menu too.


How to create server
--------------------

You need to create the server.ini file by running 

  java -cp marauroa.jar:stendhal-server-X.XX.jar games.stendhal.server.core.engine.GenerateINI

(Please use ; instead of : on Microsoft Windows).
  
It will create server.ini file that will work correctly with Marauroa 2.0 and above  

If you have run previously a Stendhal server, you will need to dump your database
 as it is incompatible with it. The fastest way would be:
 
   drop database stendhal;
   create database stendhal;

For more details please see http://arianne.sourceforge.net/wiki/index.php?title=Host_a_Stendhal_Server

How to compile Stendhal
-----------------------

http://arianne.sourceforge.net/wiki/index.php/HowToBuildStendhal

No warranties. But let us know if it doesn't work.


Stendhal in Eclipse
-------------------

For a description on how to setup the Eclipse project for Stendhal please see:

http://arianne.sourceforge.net/wiki/index.php?title=StendhalonEclipse


Legal
-----

Stendhal(c) is copyright of Miguel Angel Blanch Lardin, 2008
arianne_rpg at users dot sourceforge dot net
