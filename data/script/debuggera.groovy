/* $Id$ */

import games.stendhal.server.entity.*
import games.stendhal.server.entity.item.*
import games.stendhal.server.scripting.*
import games.stendhal.server.entity.npc.*;
import games.stendhal.server.pathfinder.Path
import games.stendhal.common.Direction;

/**
 * Creates a NPC to help testers.
 */

if (player != null) {

	// Create NPC
	npc=new ScriptingNPC("Debuggera")
	npc.setClass("tavernbarmaidnpc")

	// Place NPC in int_admin_playground 
    // if this script is executed by an admin
	myZone = "int_admin_playground";
	game.setZone(myZone);
	npc.set(4, 11);
	game.add(npc)

	// Create Dialog
	npc.behave("greet", "My mom said, i am not allowed to talk to strangers.");
}