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
package utilities;

import java.util.ArrayList;
import java.util.List;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.game.IRPZone;
import marauroa.common.game.RPObject;


public class NPCTestHelper {

	private static final StendhalRPWorld world = MockStendlRPWorld.get();


	/**
	 * Retrieves all NPCs currently in world.
	 */
	public static List<NPC> getAllNPCs() {
		final List<NPC> npcs = new ArrayList<>();
		for (final IRPZone zone: world) {
			for (final RPObject obj: zone) {
				if (obj instanceof NPC) {
					npcs.add((NPC) obj);
				}
			}
		}
		return npcs;
	}

	/**
	 * Removes an NPC from world.
	 *
	 * @param npc
	 *     NPC to be removed.
	 * @return
	 *     <code>true</code> if NPC was removed.
	 */
	public static boolean removeNPC(final NPC npc) {
		if (npc == null) {
			return true;
		}
		final StendhalRPZone npczone = npc.getZone();
		if (npczone == null) {
			return true;
		}
		npczone.remove(npc);
		return !npczone.getNPCList().contains(npc);
	}

	/**
	 * Removes all NPCs from world;
	 *
	 * @return
	 *     <code>true</code> if world contains no NPCs.
	 */
	public static boolean removeAllNPCs() {
		final List<NPC> npcs = getAllNPCs();
		boolean removed = true;
		for (final NPC npc: npcs) {
			removed = removed && removeNPC(npc);
		}
		return removed && getAllNPCs().isEmpty();
	}
}
