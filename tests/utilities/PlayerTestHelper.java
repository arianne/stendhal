package utilities;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.game.RPObject;

public class PlayerTestHelper  {

	/**
	 * create an unnamed player object
	 * @return player
	 */
	public static Player createPlayer() {
		PlayerHelper.generatePlayerRPClasses();
		Player pl = new Player(new RPObject());
		PlayerHelper.addEmptySlots(pl);
		pl.put("outfit", "01010101");
		return pl;
	}

	/**
	 * create a named player
	 * @param name
	 * @return player
	 */
	public static Player createPlayer(String name) {
		Player pl = createPlayer();
		pl.setName(name);
		return pl;
	}

	/**
	 * register a player in rule processor, world and zone
	 * @param player
	 * @param zoneName
	 */
	public static void registerPlayer(Player player, String zoneName) {
		registerPlayer(player, StendhalRPWorld.get().getZone(zoneName));
	}

	/**
	 * register a player in rule processor, world and zone
	 * @param player
	 * @param zone
	 */
	public static void registerPlayer(Player player, StendhalRPZone zone) {
		MockStendhalRPRuleProcessor.get().addPlayer(player);
		MockStendlRPWorld.get().add(player);
		zone.add(player);
	}

	/**
	 * remove a player from rule processor, world and zone
	 * @param player
	 * @param zone
	 */
	public static void unregisterPlayer(Player player, StendhalRPZone zone) {
		zone.remove(player);
		removePlayer(player);
	}

	/**
	 * remove a player from rule processor, world and zone
	 * @param player
	 * @param zone
	 */
	public static void removePlayer(String playerName, StendhalRPZone zone) {
		Player player = MockStendhalRPRuleProcessor.get().getPlayer(playerName);

		if (player != null) {
			unregisterPlayer(player, zone);
		}
    }

	/**
	 * remove a player from rule processor, world and zone
	 * @param playerName
	 * @param zoneName
	 */
	public static void removePlayer(String playerName, String zoneName)
    {
		removePlayer(playerName, MockStendlRPWorld.get().getZone(zoneName));
    }

	/**
	 * remove a player from world and rule processor
	 * @param playerName
	 */
	public static void removePlayer(String playerName)
    {
		Player player = MockStendhalRPRuleProcessor.get().getPlayer(playerName);

		if (player != null) {
			removePlayer(player);
		}
    }

	/**
	 * remove a player from world and rule processor
	 * @param player
	 */
	public static void removePlayer(Player player)
    {
		if (player != null) {
			MockStendlRPWorld.get().remove(player.getID());
			MockStendhalRPRuleProcessor.get().removePlayer(player);
		}
    }

	/**
	 * equip the player with the given amount of money
	 * @param player
	 * @param amount
	 * @return success flag
	 */
	public static boolean equipWithMoney(Player player, int amount) {
		return equipWithStackableItem(player, "money", amount);
	}

	/**
	 * equip the player with the given amount of items
	 * @param player
	 * @param clazz
	 * @param amount
	 * @return success flag
	 */
	public static boolean equipWithStackableItem(Player player, String clazz, int amount) {
		StendhalRPWorld world = StendhalRPWorld.get();

		StackableItem item = (StackableItem) world.getRuleManager().getEntityManager().getItem(clazz);
		item.setQuantity(amount);

		return player.equip(item);
	}

	/**
	 * reset the conversation state of the named NPC
	 * @param string
	 */
	public static void resetNPC(String npcName)
    {
		SpeakerNPC npc = NPCList.get().get(npcName);

		if (npc != null) {
			npc.setCurrentState(ConversationStates.IDLE);
		}
    }

	/**
	 * remove the named NPC
	 * @param npcName
	 */
	public static void removeNPC(String npcName)
    {
		NPCList.get().remove(npcName);
    }

	/**
	 * remove a zone from the world
	 * @param name
	 */
	public static void removeZone(String zoneName)
    {
		//TODO
    }
}
