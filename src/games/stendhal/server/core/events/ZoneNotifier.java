/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.events;

import games.stendhal.server.entity.player.Player;

/**
 * Manages messages based on entering a new zone.
 *
 * @author kymara (based on Tutorial Notifier by hendrik)
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

			// We must delay this for 1 turn for technical reasons (like zone change).
			// But we delay it for 2 seconds so that the player has some time to recognize the event.
			new DelayedPlayerTextSender(player, type.getMessage(), 2);
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
        } else if (destinationZone.equals("-7_kanmararn_prison")) {
            process(player, ZoneEventType.VISIT_KANMARARN_PRISON);
        } else if (destinationZone.equals("-1_fado_great_cave_w2")) {
            process(player, ZoneEventType.VISIT_IMPERIAL_CAVES);
        } else if (destinationZone.equals("-1_fado_great_cave_n_e2")) {
            process(player, ZoneEventType.VISIT_MAGIC_CITY_N);
        } else if (destinationZone.equals("-1_fado_great_cave_e2")) {
            process(player, ZoneEventType.VISIT_MAGIC_CITY);
        } else if (destinationZone.equals("-1_semos_caves")) {
            process(player, ZoneEventType.VISIT_SEMOS_CAVES);
        } else if (destinationZone.equals("int_ados_castle_entrance")) {
            process(player, ZoneEventType.VISIT_ADOS_CASTLE);
}
    }

}
