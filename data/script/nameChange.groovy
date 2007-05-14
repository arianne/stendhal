 
import games.stendhal.server.entity.*
import games.stendhal.server.entity.item.*
import games.stendhal.server.scripting.*
import games.stendhal.server.entity.npc.*;
import games.stendhal.server.pathfinder.Path
import games.stendhal.common.Direction;
import marauroa.common.game.RPSlot;
import games.stendhal.common.*;

/**
* Makes client display a fake player name by changing the title attribute. If args[0] equals remove, the original name is reset. Can only be used to chage the name of the player running the script.

@author timothyb89
*/

if (player != null) {
	if (args[0].equals("remove")) {
		player.remove("title");
		player.sendPrivateText("Your original name has been restored. Please change zones for the changes to take effect.");
		player.update();
		player.notifyWorldAboutChanges();
	} else {
		player.put("title", args[0]);
		player.sendPrivateText("Your name has been changed to " + player.get("alternateTitle") + ".");
		player.update();
		player.notifyWorldAboutChanges();
	}
}
