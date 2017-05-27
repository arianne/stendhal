/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.creature.impl.attack;

import java.util.HashMap;
import java.util.Map;

public class PositioningStrategyFactory {
	private static final PositioningStrategy dual = new DualAttackPositioningStrategy();

	/**
	 * Get positioning strategy.
	 *
	 * @param name name of the strategy
	 * @param param parameters to the strategy, or empty string if none provided
	 *
	 * @return positioning strategy
	 */
	static final PositioningStrategy get(final String name, final String param) {
		if ("dual attack".equals(name)) {
			return dual;
		}

		// Fall back to complete strategy implementations
		Map<String, String> aiProfiles = new HashMap<String, String>();
		aiProfiles.put(name, param);
		return AttackStrategyFactory.get(aiProfiles);
	}
}
