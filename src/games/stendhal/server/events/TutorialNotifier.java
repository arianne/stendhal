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
	 * @param type    EventType
	 */
	private static void process(Player player, TutorialEventType type) {
		String key = type.name().toLowerCase();
		if (player.getKeyedSlot("!tutorial", key) == null) {
			player.setKeyedSlot("!tutorial", key, "1");
			player.sendPrivateText("Tutorial: " + type.getMessage());
		}
	}

	/**
	 * Login
	 *
	 * @param player Player
	 */
	public static void login(Player player) {
		process(player, TutorialEventType.FIRST_LOGIN);
	}

	/**
	 * moveing
	 *
	 * @param player Player
	 */
	public static void move(Player player) {
		process(player, TutorialEventType.FIRST_MOVE);
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
			process(player, TutorialEventType.VISIT_SEMOS_CITY);
		}
	}
}
