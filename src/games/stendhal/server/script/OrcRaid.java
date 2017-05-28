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
 * Less safe for players below level 40
 */
public class OrcRaid extends CreateRaid {

	@Override
	protected Map<String, Integer> createArmy() {
		final Map<String, Integer> attackArmy = new HashMap<String, Integer>();
		attackArmy.put("orc warrior", 7);
		attackArmy.put("orc hunter", 5);
		attackArmy.put("orc chief", 3);
		attackArmy.put("orc", 6);
		attackArmy.put("mountain orc", 3);
		attackArmy.put("troll", 4);
		attackArmy.put("red troll", 7);
		attackArmy.put("cave troll", 2);
   		attackArmy.put("green dragon", 3);

		return attackArmy;
	}
}
