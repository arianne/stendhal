package games.stendhal.server.maps;

import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.slot.EntitySlot;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

public class PlayerHelper {
	public static void addEmptySlots(Player player) {
		player.addSlot(new EntitySlot("bag"));

		// BUG: Capacity can only be set at the RPClass.
		// player.getSlot("bag").setCapacity(20);
		player.addSlot(new EntitySlot("lhand"));
		player.addSlot(new EntitySlot("rhand"));
		player.addSlot(new EntitySlot("armor"));
		player.addSlot(new EntitySlot("head"));
		player.addSlot(new EntitySlot("legs"));
		player.addSlot(new EntitySlot("feet"));
		player.addSlot(new EntitySlot("finger"));
		player.addSlot(new EntitySlot("cloak"));
		player.addSlot(new EntitySlot("keyring"));
		player.addSlot(new RPSlot("!quests"));
		player.getSlot("!quests").add(new RPObject());
		player.addSlot(new RPSlot("!kills"));
		player.getSlot("!kills").add(new RPObject());

	}
}
