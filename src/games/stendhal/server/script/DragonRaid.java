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
public class DragonRaid extends CreateRaid {

	@Override
	protected Map<String, Integer> createArmy() {
		final Map<String, Integer> attackArmy = new HashMap<String, Integer>();
		attackArmy.put("red dragon", 2);
		attackArmy.put("green dragon", 2);
		attackArmy.put("bone dragon", 3);
		attackArmy.put("twin headed dragon", 2);
		attackArmy.put("blue dragon", 3);
		attackArmy.put("chaos red dragonrider", 2);
		attackArmy.put("chaos green dragonrider", 2);
		attackArmy.put("flying golden dragon", 2);
		attackArmy.put("black dragon", 1);
		return attackArmy;
	}
	@Override
	protected String getInfo() {
		return "Not safe for players below level 150.";
	}
}
