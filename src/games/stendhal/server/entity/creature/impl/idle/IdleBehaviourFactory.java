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
package games.stendhal.server.entity.creature.impl.idle;


import java.util.Map;

public class IdleBehaviourFactory {
	private static final IdleBehaviour nothing = new StandOnIdle();

	public static IdleBehaviour get(final Map<String, String> aiProfiles) {
		if (aiProfiles.containsKey("patrolling")) {
			return new Patroller();
        } else if (aiProfiles.containsKey("camouflage")) {
            return new CamouflagedIdleBehaviour();
        }
        return nothing;
	}
}
