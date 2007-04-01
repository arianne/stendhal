package games.stendhal.server.events;

import games.stendhal.server.entity.player.Player;

/**
 * manages the tutorial based on events created all over the game.
 *
 * @author hendrik
 */
public class TutorialNotifier {

	/**
	 * If the specified event is unknown, add it to the list and
	 * send the text to the player.
	 *
	 * @param player Player
	 * @param key    EventType
	 */
	private static void process(Player player, String key) {
		if (player.getKeyedSlot("!tutorial", key) == null) {
			player.setKeyedSlot("!tutorial", key, "1");
			// TODO: Use text instead of key
			player.sendPrivateText("Tutorial: " + key);
		}
	}

	/**
	 * Login
	 *
	 * @param player Player
	 */
	public static void login(Player player) {
		process(player, "FIRST_LOGIN");
	}

	/**
	 * moveing
	 *
	 * @param player Player
	 */
	public static void move(Player player) {
		process(player, "FIRST_WALK");
	}

	/**
	 * Zone changes
	 *
	 * @param player Player
	 * @param sourceZone source zone
	 * @param destinationZone destination zone
	 */
	public static void zoneChange(Player player, String sourceZone, String destinationZone) {
		if (sourceZone.equals("int_semos_townhall")) {
			process(player, "FIRST_VISIT_TO_SEMOS_CITY");
		}
	}
}
