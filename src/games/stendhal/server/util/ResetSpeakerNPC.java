/***************************************************************************
 *                     Copyright © 2023 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.util;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;


/**
 * Utility class to restoring SpeakerNPC to startup state.
 *
 * NOTE: Be mindful that resetting NPCs will erase ALL quest
 * transitions. If the NPC is associated with multiple quests
 * be sure to reload any quests that should remain active.
 */
public class ResetSpeakerNPC {

	private static final NPCList npcs = SingletonRepository.getNPCList();


	/**
	 * Resets multiple SpeakerNPC to state at server startup.
	 *
	 * @param zc
	 *     Zone configurator to be reloaded.
	 * @param z
	 *     Zone or name of zone passed to configurator.
	 * @param names
	 *     Names of NPCs that should be reset.
	 * @return
	 *     <code>true</code> if each NPC was removed & reloaded.
	 */
	public static boolean reload(final ZoneConfigurator zc, final Object z,
			final String... names) {
		StendhalRPZone zone = null;
		if (z instanceof StendhalRPZone) {
			zone = (StendhalRPZone) z;
		} else if (z instanceof String) {
			zone = SingletonRepository.getRPWorld().getZone((String) z);
		}
		boolean res = true;
		for (final String name: names) {
			removeNPC(name);
			res = res && npcs.get(name) == null;
		}
		// re-configure zone
		configureZone(zc, zone);
		for (final String name: names) {
			// was NPC reloaded successfully?
			res = res && npcs.get(name) != null;
		}
		return res;
	}

	/**
	 * Resets a SpeakerNPC to state at server startup. The zone to be
	 * passed to configurator is the zone where NPC is located.
	 *
	 * @param zc
	 *     Zone configurator to be reloaded.
	 * @param name
	 *     Names of NPC that should be reset.
	 * @return
	 *     <code>true</code> if the NPC was removed & reloaded.
	 */
	public static boolean reload(final ZoneConfigurator zc, final String name) {
		final StendhalRPZone zone = removeNPC(name);
		boolean res = npcs.get(name) == null;
		// re-configure zone
		configureZone(zc, zone);
		// was NPC reloaded successfully?
		res = res && npcs.get(name) != null;
		return res;
	}

	/**
	 * Initiates the zone configurator.
	 *
	 * @param zc
	 *     Zone configurator to be reloaded.
	 * @param zone
	 *     Name of zone passed to configurator.
	 */
	private static void configureZone(final ZoneConfigurator zc, final StendhalRPZone zone) {
		if (zone != null) {
			zc.configureZone(zone, zone.getAttributes().toMap());
		} else {
			zc.configureZone(null, null);
		}
	}

	/**
	 * Removes a SpeakerNPC from the world.
	 *
	 * @param name
	 *     Name of NPC.
	 * @return
	 *     The zone the NPC was removed from.
	 */
	private static StendhalRPZone removeNPC(final String name) {
		final SpeakerNPC npc = npcs.get(name);
		if (npc != null) {
			final StendhalRPZone zone = npc.getZone();
			if (zone != null) {
				zone.remove(npc);
				return zone;
			}
		}
		return null;
	}
}
