/***************************************************************************
 *                   (C) Copyright 2022 - Faiumoni e.V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc.quest;

import java.util.HashMap;
import java.util.Map;

import games.stendhal.server.util.StringUtils;

/**
 * simulates the quest
 *
 * @author hendrik
 */
class QuestSimulator {
	private Map<String, Object> params = new HashMap<>();

	public void setParam(String key, String value) {
		params.put(key, value);
	}

	public void playerSays(String text) {
		System.out.println("Player: " + StringUtils.substitute(text, params));
	}

	public void npcSays(String name, String text) {
		System.out.println(name + ": " + StringUtils.substitute(text, params));
	}

	public void history(String text) {
		System.out.println("History: " + StringUtils.substitute(text, params));
	}

	public void info(String text) {
		System.out.println(text);
	}
}
