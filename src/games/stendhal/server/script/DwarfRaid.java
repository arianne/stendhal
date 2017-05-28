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
 * Less safe for players below level 30
 */
public class DwarfRaid extends CreateRaid {

	@Override
	protected Map<String, Integer> createArmy() {
		final Map<String, Integer> attackArmy = new HashMap<String, Integer>();
		attackArmy.put("dwarf", 7);
		attackArmy.put("dwarf guardian", 6);
		attackArmy.put("elder dwarf", 6);
		attackArmy.put("leader dwarf", 4);
		attackArmy.put("hero dwarf", 5);
		attackArmy.put("duergar", 3);
		attackArmy.put("elder duergar", 3);
		attackArmy.put("duergar axeman", 3);

		return attackArmy;
	}
	@Override
	protected String getInfo() {
		return "Not safe for players below level 30.";
	}
}
