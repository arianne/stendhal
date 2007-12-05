package utilities;


import games.stendhal.server.entity.ActiveEntity;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.slot.EntitySlot;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

public class PlayerHelper {
	public static void addEmptySlots(Player player) {
		player.addSlot(new EntitySlot("bag"));
		player.addSlot(new EntitySlot("lhand"));
		player.addSlot(new EntitySlot("rhand"));
		player.addSlot(new EntitySlot("armor"));
		player.addSlot(new EntitySlot("head"));
		player.addSlot(new EntitySlot("legs"));
		player.addSlot(new EntitySlot("feet"));
		player.addSlot(new EntitySlot("finger"));
		player.addSlot(new EntitySlot("cloak"));
		player.addSlot(new EntitySlot("keyring"));
		player.addSlot(new RPSlot("!buddy"));
		player.getSlot("!buddy").add(new RPObject());
		player.addSlot(new RPSlot("!quests"));
		player.getSlot("!quests").add(new RPObject());
		player.addSlot(new RPSlot("!kills"));
		player.getSlot("!kills").add(new RPObject());
		player.addSlot(new RPSlot("!tutorial"));
		player.getSlot("!tutorial").add(new RPObject());
		player.addSlot(new RPSlot("!visited"));
		player.getSlot("!visited").add(new RPObject());

	}

	public static void generatePlayerRPClasses() {
		Entity.generateRPClass();
		ActiveEntity.generateRPClass();
		RPEntity.generateRPClass();
		Player.generateRPClass();

	}
	public static void generateNPCRPClasses() {
		Entity.generateRPClass();
		ActiveEntity.generateRPClass();
		RPEntity.generateRPClass();
		NPC.generateRPClass();

	}
	public static void generateItemRPClasses() {
		Entity.generateRPClass();
		Item.generateRPClass();


	}
}
