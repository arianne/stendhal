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
 * Not safe for players below level 150
 */
public class ImperialRaid extends CreateRaid {

	@Override
	protected Map<String, Integer> createArmy() {
		final Map<String, Integer> attackArmy = new HashMap<String, Integer>();
		attackArmy.put("imperial chief", 2);
		attackArmy.put("imperial knight", 2);
		attackArmy.put("imperial general", 1);
		attackArmy.put("imperial commander", 2);
		attackArmy.put("imperial scientist", 3);
		attackArmy.put("imperial priest", 1);
		attackArmy.put("imperial defender", 5);
		attackArmy.put("imperial experiment", 2);
		attackArmy.put("imperial mutant", 2);
		attackArmy.put("imperial general giant", 2);
		return attackArmy;
	}

	@Override
	protected String getInfo() {
		return "Not safe for players below level 150";
	}
}
