package games.stendhal.server.entity.player;

import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.player.Player.NoSheepException;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

public class PlayerSheepManager {
	private Player player;

	PlayerSheepManager(Player player) {
		this.player = player;
	}

	void storeSheep(Sheep sheep) {

		if (!player.hasSlot("#flock")) {
			player.addSlot(new RPSlot("#flock"));
		}

		RPSlot slot = player.getSlot("#flock");
		slot.clear();
		slot.add(sheep);
		player.put("sheep", sheep.getID().getObjectID());

	}

	public Sheep retrieveSheep() throws NoSheepException {

		try {
			if (player.hasSlot("#flock")) {
				RPSlot slot = player.getSlot("#flock");
				if (slot.size() > 0) {
					RPObject object = slot.getFirst();
					slot.remove(object.getID());

					Sheep sheep = new Sheep(object, player);

					player.removeSlot("#flock");
					return sheep;
				}
			}

			throw new NoSheepException();
		} finally {

		}
	}

}
