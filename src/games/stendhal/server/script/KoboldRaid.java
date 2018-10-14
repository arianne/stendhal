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
 * Less safe for players below level 10
 */
public class KoboldRaid extends CreateRaid {

	@Override
	protected Map<String, Integer> createArmy() {
		final Map<String, Integer> attackArmy = new HashMap<String, Integer>();
		attackArmy.put("kobold", 7);
		attackArmy.put("archer kobold", 3);
		attackArmy.put("leader kobold", 7);
		attackArmy.put("soldier kobold", 7);
		attackArmy.put("giant kobold", 2);
		attackArmy.put("veteran kobold", 7);
		return attackArmy;
	}
	@Override
	protected String getInfo() {
		return "Less safe for players below level 10.";
	}
}
