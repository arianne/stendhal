/***************************************************************************
 *                  Copyright (C) 2003-2022 - Arianne                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests.tutorial;

import java.util.HashMap;
import java.util.Map;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import marauroa.common.game.RPObject.ID;


public class ActiveTutors {

	private static ActiveTutors instance;

	private static Map<String, SpeakerNPC> tutors;


	public static ActiveTutors get() {
		if (instance == null) {
			instance = new ActiveTutors();
		}

		return instance;
	}

	/**
	 * Singleton constructor.
	 */
	private ActiveTutors() {
		tutors = new HashMap<>();
	}

	public void put(final String key, final SpeakerNPC npc) {
		tutors.put(key, npc);
	}

	public SpeakerNPC get(final String key) {
		return tutors.get(key);
	}

	public boolean removeFromWorld(final String key) {
		if (!tutors.containsKey(key)) {
			return false;
		}

		final SpeakerNPC npc = tutors.get(key);
		final String npcName = npc.getName();
		final ID id = npc.getID();

		tutors.remove(key);

		final StendhalRPZone zone = npc.getZone();
		if (zone != null) {
			zone.remove(npc);
		}

		SingletonRepository.getRPWorld().remove(id);

		return SingletonRepository.getNPCList().get(npcName) == null;
	}
}
