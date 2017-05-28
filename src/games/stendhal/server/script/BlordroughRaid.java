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
 * Not safe for players below level 150
 */
public class BlordroughRaid extends CreateRaid {

	@Override
	protected Map<String, Integer> createArmy() {
		final Map<String, Integer> attackArmy = new HashMap<String, Integer>();
		attackArmy.put("elder giant", 5);
		attackArmy.put("imperial general giant", 5);
		attackArmy.put("blordrough quartermaster", 9);
		attackArmy.put("blordrough corporal", 6);
		attackArmy.put("blordrough storm trooper", 8);
		attackArmy.put("master giant", 2);
		return attackArmy;
	}
	@Override
	protected String getInfo() {
		return "Not safe for players below level 150.";
	}
}
