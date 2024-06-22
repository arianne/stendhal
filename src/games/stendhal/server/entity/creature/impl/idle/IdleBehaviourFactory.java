/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2024 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.creature.impl.idle;


import java.util.Map;

import games.stendhal.common.Rand;
import games.stendhal.server.entity.npc.behaviour.impl.idle.IdleBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.idle.WanderIdleBehaviour;

public class IdleBehaviourFactory {
	private static final IdleBehaviour nothing = new StandOnIdle();

	public static IdleBehaviour get(final Map<String, String> aiProfiles) {
		// randomly select between "patrolling" & "wander" for individual entity instance
		if (aiProfiles.containsKey("patrolling") && aiProfiles.containsKey("wander")) {
			return Rand.rand(2) == 0 ? new Patroller() : new WanderIdleBehaviour();
		}
		if (aiProfiles.containsKey("patrolling")) {
			return new Patroller();
		} else if (aiProfiles.containsKey("camouflage")) {
			return new CamouflagedIdleBehaviour();
		} else if (aiProfiles.containsKey("wander")) {
			return new WanderIdleBehaviour();
		}
		return nothing;
	}
}
