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

/**
 * @author gummipferd
 *
 * Less safe for players below level 50
 */
public class ZombieRaid extends CreateRaid {

	@Override
	protected Map<String, Integer> createArmy() {
		final Map<String, Integer> attackArmy = new HashMap<String, Integer>();
		attackArmy.put("zombie rat", 4);
		attackArmy.put("bloody zombie", 5);
		attackArmy.put("zombie", 10);
		attackArmy.put("headless monster", 5);
		attackArmy.put("rotten zombie", 5);
		attackArmy.put("vampirette", 5);
		attackArmy.put("werewolf", 3);
		attackArmy.put("death knight", 3);
		attackArmy.put("vampire bride", 1);
		attackArmy.put("vampire lord", 1);

		return attackArmy;
	}
}
