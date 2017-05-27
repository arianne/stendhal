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
 * @author kymara
 *
 * Not safe for players below level 30
 */
public class FrogmanSheepmanRaid extends CreateRaid {

	@Override
	protected Map<String, Integer> createArmy() {
		final Map<String, Integer> attackArmy = new HashMap<String, Integer>();
		attackArmy.put("frogman", 7);
		attackArmy.put("elite frogman", 4);
		attackArmy.put("wizard frogman", 2);
		attackArmy.put("sheepman", 6);
		attackArmy.put("armored sheepman", 3);
		attackArmy.put("elder sheepman", 2);
		attackArmy.put("elite sheepman", 1);
		return attackArmy;
	}

	@Override
	protected String getInfo() {
		return "Not safe for players below level 30";
	}
}
