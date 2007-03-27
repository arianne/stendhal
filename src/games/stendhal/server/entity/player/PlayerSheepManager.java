package games.stendhal.server.entity.player;

import games.stendhal.server.entity.creature.Sheep;
import marauroa.common.Log4J;
import marauroa.common.game.RPSlot;

import org.apache.log4j.Logger;

public class PlayerSheepManager {
	private static Logger logger = Logger.getLogger(PlayerSheepManager.class);
	private Player player = null;
	
	PlayerSheepManager(Player player) {
		this.player = player;
	}
	
	void storeSheep(Sheep sheep) {
		Log4J.startMethod(logger, "storeSheep");
		if (!player.hasSlot("#flock")) {
			player.addSlot(new RPSlot("#flock"));
		}

		RPSlot slot = player.getSlot("#flock");
		slot.clear();
		slot.add(sheep);
		player.put("sheep", sheep.getID().getObjectID());
		Log4J.finishMethod(logger, "storeSheep");
	}


}
