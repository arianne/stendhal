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
 * Not safe for players below level 10
 */
public class BeholderRaid extends CreateRaid {

	@Override
	protected Map<String, Integer> createArmy() {
		final Map<String, Integer> attackArmy = new HashMap<String, Integer>();
		attackArmy.put("young beholder", 7);
		attackArmy.put("green slime", 4);
		attackArmy.put("beholder", 5);
		attackArmy.put("elder beholder", 1);
		attackArmy.put("snake", 3);
		attackArmy.put("grass snake", 4);
		return attackArmy;
	}

	@Override
	protected String getInfo() {
		return "Not safe for players below level 12";
	}
}
