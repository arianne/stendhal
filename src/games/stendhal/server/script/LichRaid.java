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
 * Not safe for players below level 80
 */
public class LichRaid extends CreateRaid {

	@Override
	protected Map<String, Integer> createArmy() {
		final Map<String, Integer> attackArmy = new HashMap<String, Integer>();
		attackArmy.put("tiny skelly", 5);
		attackArmy.put("warrior skeleton", 3);
		attackArmy.put("elder skeleton", 4);
		attackArmy.put("demon skeleton", 3);
		attackArmy.put("bone dragon", 3);
		attackArmy.put("fallen warrior", 5);
		attackArmy.put("fallen priest", 3);
		attackArmy.put("fallen high priest", 2);
		attackArmy.put("lich", 8);
		attackArmy.put("dead lich", 3);
		attackArmy.put("high lich", 3);

		return attackArmy;
	}
	@Override
	protected String getInfo() {
		return "Not safe for players below level 80.";
	}
}
