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
 * Less safe for players below level 50
 */
public class ElementalRaid extends CreateRaid {

	@Override
	protected Map<String, Integer> createArmy() {
		final Map<String, Integer> attackArmy = new HashMap<String, Integer>();
		attackArmy.put("fire elemental", 7);
		attackArmy.put("water elemental", 7);
		attackArmy.put("ice elemental", 7);
		attackArmy.put("earth elemental", 7);
		attackArmy.put("djinn", 5);
		attackArmy.put("air elemental", 7);
		return attackArmy;
	}

	@Override
	protected String getInfo() {
		return "Less safe for players below level 50";
	}
}
