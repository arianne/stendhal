package games.stendhal.server.core.events;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;

/**
 * manages messages based on entering a new zone.
 * 
 * @author kymara (based on Tutorial Notifer by hendrik)
 */
public class ZoneNotifier {

	/**
	 * If the specified event is unknown, add it to the list and send the text
	 * to the player.
	 * 
	 * @param player
	 *            Player
	 * @param type
	 *            EventType
	 */
	private static void process(final Player player, final ZoneEventType type) {
		final String key = type.name().toLowerCase();
		// Use tutorial slot
		if (player.getKeyedSlot("!tutorial", key) == null) {
			player.setKeyedSlot("!tutorial", key, "1");

			// we must delay this for 1 turn for technical reasons (like zone
			// change)
			// but we delay it for 2 seconds so that the player has some time to
			// recognize the event
			final DelayedPlayerTextSender dpts = new DelayedPlayerTextSender(player,
					type.getMessage());
			SingletonRepository.getTurnNotifier().notifyInSeconds(2, dpts);
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
	public static void zoneChange(final Player player, final String sourceZone,
			final String destinationZone) {
		if (destinationZone.equals("-1_semos_catacombs_se")) {
			process(player, ZoneEventType.VISIT_SUB1_SEMOS_CATACOMBS);
		} else if (destinationZone.equals("-2_semos_catacombs")) {
			process(player, ZoneEventType.VISIT_SUB2_SEMOS_CATACOMBS);
		} else if (destinationZone.equals("1_kikareukin_cave")) {
			process(player, ZoneEventType.VISIT_KIKAREUKIN_CAVE);
		} else if (destinationZone.equals("6_kikareukin_islands")) {
			process(player, ZoneEventType.VISIT_KIKAREUKIN_ISLANDS);
		} else if (destinationZone.equals("-7_kanmararn_prison")) {
			process(player, ZoneEventType.VISIT_KANMARARN_PRISON);
		} else if (destinationZone.equals("-1_fado_great_cave_w2")) {
			process(player, ZoneEventType.VISIT_IMPERIAL_CAVES);
		} else if (destinationZone.equals("-1_fado_great_cave_n_e2")) {
			process(player, ZoneEventType.VISIT_MAGIC_CITY_N);
		} else if (destinationZone.equals("-1_fado_great_cave_e2")) {
			process(player, ZoneEventType.VISIT_MAGIC_CITY);
		} else if (destinationZone.equals("1_dreamscape")) {
			process(player, ZoneEventType.VISIT_DREAMSCAPE);
		} else if (destinationZone.equals("-1_semos_caves")) {
			process(player, ZoneEventType.VISIT_SEMOS_CAVES);
		} else if (destinationZone.equals("int_ados_castle_entrance")) {
			process(player, ZoneEventType.VISIT_ADOS_CASTLE);
		} else if (destinationZone.equals("hell")) {
			process(player, ZoneEventType.VISIT_HELL);
	    } else if (destinationZone.equals("7_kikareukin_clouds")) {
            process(player, ZoneEventType.VISIT_KIKAREUKIN_CLOUDS);
        }
        
	}

}
