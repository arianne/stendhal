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
 * A raid safe for lowest level players
 */
public class FarmRaid extends CreateRaid {

	@Override
	protected Map<String, Integer> createArmy() {
		final Map<String, Integer> attackArmy = new HashMap<String, Integer>();
		attackArmy.put("piglet", 4);
		attackArmy.put("cow", 3);
		attackArmy.put("mother hen", 4);
		attackArmy.put("goat", 3);
		attackArmy.put("horse", 2);
		attackArmy.put("chick", 5);
		attackArmy.put("bull", 2);
		attackArmy.put("ram", 5);
		attackArmy.put("mouse", 5);
		attackArmy.put("white horse", 2);
		return attackArmy;
	}

	@Override
	protected String getInfo() {
		return "A raid safe for lowest level Player.";
	}
}
