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
 * Not safe for players below level 5
 */
public class AnimalRaid extends CreateRaid {

	@Override
	protected Map<String, Integer> createArmy() {
		final Map<String, Integer> attackArmy = new HashMap<String, Integer>();
		attackArmy.put("monkey", 2);
		attackArmy.put("grass snake", 2);
		attackArmy.put("beaver", 2);
		attackArmy.put("tiger", 2);
		attackArmy.put("lion", 3);
		attackArmy.put("panda", 2);
		attackArmy.put("penguin", 4);
		attackArmy.put("caiman", 3);
		attackArmy.put("babybear", 2);
		attackArmy.put("black bear", 1);
		attackArmy.put("elephant", 3);
		attackArmy.put("crocodile", 2);

		return attackArmy;
	}

	@Override
	protected String getInfo() {
		return " * Not safe for players below level 5";
	}
}
