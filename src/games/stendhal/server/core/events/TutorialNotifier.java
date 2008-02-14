package games.stendhal.server.core.events;

import games.stendhal.common.NotificationType;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.player.Player;

/**
 * manages the tutorial based on events created all over the game.
 * 
 * @author hendrik
 */
public class TutorialNotifier {

	/**
	 * If the specified event is unknown, add it to the list and send the text
	 * to the player.
	 * 
	 * @param player
	 *            Player
	 * @param type
	 *            EventType
	 */
	private static void process(Player player, TutorialEventType type) {
		String key = type.name().toLowerCase();
		if (player.getKeyedSlot("!tutorial", key) == null) {
			player.setKeyedSlot("!tutorial", key, "1");

			// we must delay this for 1 turn for technical reasons (like zone
			// change)
			// but we delay it for 2 seconds so that the player has some time to
			// recognize the event
			DelayedPlayerTextSender dpts = new DelayedPlayerTextSender(player, "Tutorial: " + type.getMessage(), NotificationType.TUTORIAL);
			SingletonRepository.getTurnNotifier().notifyInSeconds(2, dpts);
		}
	}

	/**
	 * Login.
	 * 
	 * @param player
	 *            Player
	 */
	public static void login(Player player) {
		process(player, TutorialEventType.FIRST_LOGIN);
	}

	/**
	 * moving.
	 * 
	 * @param player
	 *            Player
	 */
	public static void move(Player player) {
		StendhalRPZone zone = player.getZone();
		if (zone != null) {
			if (zone.getName().equals("int_semos_guard_house")) {
				process(player, TutorialEventType.FIRST_MOVE);
			}
		}
	}

	/**
	 * Zone changes.
	 * 
	 * @param player
	 *            Player
	 * @param sourceZone
	 *            source zone
	 * @param destinationZone
	 *            destination zone
	 */
	public static void zoneChange(Player player, String sourceZone,
			String destinationZone) {
		if (sourceZone.equals("0_semos_village_w") && destinationZone.equals("int_semos_guard_house")) {
			process(player, TutorialEventType.RETURN_GUARDHOUSE);
		} else if (destinationZone.equals("0_semos_city")) {
			process(player, TutorialEventType.VISIT_SEMOS_CITY);
		} else if (destinationZone.equals("int_semos_tavern_0")) {
			process(player, TutorialEventType.VISIT_SEMOS_TAVERN);
		} else if (destinationZone.equals("-1_semos_dungeon")) {
			process(player, TutorialEventType.VISIT_SEMOS_DUNGEON);
		} else if (destinationZone.equals("-2_semos_dungeon")) {
			process(player, TutorialEventType.VISIT_SEMOS_DUNGEON_2);
		} else if (destinationZone.equals("int_afterlife")) {
			process(player, TutorialEventType.FIRST_DEATH);
		}
	}

	/**
	 * player got attacked.
	 * 
	 * @param player
	 *            Player
	 */
	public static void attacked(Player player) {
		process(player, TutorialEventType.FIRST_ATTACKED);
	}

	/**
	 * player killed something.
	 * 
	 * @param player
	 *            Player
	 */
	public static void killedSomething(Player player) {
		process(player, TutorialEventType.FIRST_KILL);
	}

	/**
	 * player got poisoned.
	 * 
	 * @param player
	 *            Player
	 */
	public static void poisoned(Player player) {
		process(player, TutorialEventType.FIRST_POISONED);
	}

	/**
	 * a player who stayed another minute in game.
	 * 
	 * @param player
	 *            Player
	 * @param age
	 *            playing time
	 */
	public static void aged(Player player, int age) {
		if (age >= 15) {
			if (player.getOutfit().isNaked()) {
				process(player, TutorialEventType.TIMED_NAKED);
			}
		} else if (age >= 10) {
			// TODO: activate this after password change is implemented
			// process(player, TutorialEventType.TIMED_PASSWORD);
		} else if (age >= 5) {
			process(player, TutorialEventType.TIMED_HELP);
		}
	}
	/**
	 * player > level 2 logged in for new release
	 * 
	 * @param player
	 *            Player
	 */
	public static void newrelease(Player player) {
		process(player, TutorialEventType.NEW_RELEASE);
	}
}
