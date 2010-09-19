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
package games.stendhal.server.script;

import java.util.HashMap;
import java.util.Map;

public class BarbarianRaid extends CreateRaid {

	@Override
	protected Map<String, Integer> createArmy() {
		final Map<String, Integer> attackArmy = new HashMap<String, Integer>();
		attackArmy.put("barbarian", 30);
		attackArmy.put("barbarian wolf", 15);
		attackArmy.put("barbarian elite", 12);
		attackArmy.put("barbarian priest", 7);
		attackArmy.put("barbarian chaman", 5);
		attackArmy.put("barbarian leader", 3);
		attackArmy.put("barbarian king", 1);

		return attackArmy;
	}

}
