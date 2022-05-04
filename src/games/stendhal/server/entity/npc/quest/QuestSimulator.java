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

/**
 * simulates the quest
 *
 * @author hendrik
 */
class QuestSimulator {

	public void playerSays(String text) {
		System.out.println("Player: " + text);
	}

	public void npcSays(String name, String text) {
		System.out.println(name + ": " + text);
	}

	public void history(String text) {
		System.out.println("History: " + text);
	}

	public void info(String text) {
		System.out.println(text);
	}
}
