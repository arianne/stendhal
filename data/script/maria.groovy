/* $Id$ */

import games.stendhal.server.entity.*
import games.stendhal.server.entity.item.*
import games.stendhal.server.scripting.*
import games.stendhal.server.entity.npc.*;
import games.stendhal.server.pathfinder.Path
import games.stendhal.common.Direction;

/**
 * Creates a portable NPC which sell foods&drinks at meetings.
 *
 * As admin use /script maria.groovy to sommon her right next to you.
 * Please put her back in int_admin_playground after use.
 */

	// Create NPC
	npc=new ScriptingNPC("Maria")
	npc.setClass("tavernbarmaidnpc")

	// Place NPC in int_admin_playground on server start
	myZone = "int_admin_playground";
	x = 11;
	y = 4;

    // if this script is executed by an admin, Maria will be placed next to him.
	if (player != null) {
		myZone = game.getZone(player);
		x = player.getx() + 1;
		y = player.gety();
	}
	game.setZone(myZone);
	npc.set(x, y);
	game.add(npc)

	// Create Dialog
	npc.behave("greet", "Hi, how can i help you?")
	npc.behave("job","I am the bar maid at Semos' #tavern and doing outside services. We sell fine beers and food.")
    npc.behave("tavern", "Please visit us in Semos. You can find the tavern on the left side of the temple.")
	npc.behave("help", "You can get an #offer of drinks and take a break to meet new people!")
	npc.behave("sell", ShopList.get().get("food&drinks"))
