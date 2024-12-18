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

import games.stendhal.common.MathHelper;
import games.stendhal.common.Rand;
import games.stendhal.server.entity.npc.behaviour.impl.idle.IdleBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.idle.WanderIdleBehaviour;

public class IdleBehaviourFactory {
	private static final IdleBehaviour nothing = new StandOnIdle();

	public static IdleBehaviour get(final Map<String, String> aiProfiles) {
		IdleBehaviour behaviour = nothing;
		if (aiProfiles.containsKey("wander")) {
			final int wanderRadius = MathHelper.parseIntDefault(aiProfiles.get("wander"), 0);
			if (wanderRadius > 0) {
				behaviour = new WanderIdleBehaviour(wanderRadius);
			} else {
				behaviour = new WanderIdleBehaviour();
			}
			if (aiProfiles.containsKey("patrolling")) {
				// randomly select between "patrolling" & "wander" for individual entity instance
				behaviour = Rand.flipCoin() ? new Patroller() : behaviour;
			}
		} else if (aiProfiles.containsKey("patrolling")) {
			behaviour = new Patroller();
		} else if (aiProfiles.containsKey("camouflage")) {
			behaviour = new CamouflagedIdleBehaviour();
		}
		return behaviour;
	}
}
