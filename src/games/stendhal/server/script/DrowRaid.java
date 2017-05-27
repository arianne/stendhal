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
 * @author miguel
 *
 * Not safe for players below level 150
 */
public class DrowRaid extends CreateRaid {

	@Override
	protected Map<String, Integer> createArmy() {
		final Map<String, Integer> attackArmy = new HashMap<String, Integer>();
		attackArmy.put("dark elf", 10);
		attackArmy.put("dark elf archer", 2);
		attackArmy.put("dark elf elite archer", 2);
		attackArmy.put("dark elf captain", 5);
		attackArmy.put("dark elf knight", 3);
		attackArmy.put("dark elf general", 1);
		attackArmy.put("dark elf wizard", 2);
		attackArmy.put("dark elf viceroy", 1);
		attackArmy.put("dark elf sacerdotist", 3);
		attackArmy.put("dark elf matronmother", 1);
		attackArmy.put("dark elf master", 1);
		attackArmy.put("dark elf ranger", 3);
		attackArmy.put("dark elf admiral", 3);
		return attackArmy;
	}
	@Override
	protected String getInfo() {
		return "Not safe for players below level 150.";
	}
}
