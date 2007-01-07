/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.player;

import marauroa.common.game.RPClass;

/**
 * Handles the RPClass registration and updating old Player objects
 * created by an older version of Stendhal.
 */
class PlayerRPClass {

	/**
	 * Generates the RPClass and specifies slots and attributes.
	 */
	static void generateRPClass() {
		RPClass player = new RPClass("player");
		player.isA("rpentity");
		player.add("text", RPClass.LONG_STRING, RPClass.VOLATILE);
		player.add("private_text", RPClass.LONG_STRING,
				(byte) (RPClass.PRIVATE | RPClass.VOLATILE));

		player.add("poisoned", RPClass.SHORT, RPClass.VOLATILE);
		player.add("eating", RPClass.SHORT, RPClass.VOLATILE);

		player.add("dead", RPClass.FLAG, RPClass.PRIVATE);

		player.add("outfit", RPClass.INT);
		player.add("outfit_org", RPClass.INT);

		// Use this for admin menus and usage.
		player.add("admin", RPClass.FLAG);
		player.add("adminlevel", RPClass.INT);
		player.add("invisible", RPClass.FLAG, RPClass.HIDDEN);
		player.add("ghostmode", RPClass.FLAG, RPClass.HIDDEN);

		player.add("release", RPClass.STRING, RPClass.HIDDEN);

		player.add("age", RPClass.INT);

		// Store sheep at DB
		player.addRPSlot("#flock", 1, RPClass.HIDDEN);
		player.add("sheep", RPClass.INT);

		// Bank system
		player.addRPSlot("bank", 20, RPClass.HIDDEN);

		// Kills recorder - needed for quest
		player.addRPSlot("!kills", 1, RPClass.HIDDEN);

		// We use this for the buddy system
		player.addRPSlot("!buddy", 1, RPClass.PRIVATE);
		player.addRPSlot("!ignore", 1, RPClass.HIDDEN);
		player.add("online", RPClass.LONG_STRING,
				(byte) (RPClass.PRIVATE | RPClass.VOLATILE));
		player.add("offline", RPClass.LONG_STRING,
				(byte) (RPClass.PRIVATE | RPClass.VOLATILE));

		player.addRPSlot("!quests", 1, RPClass.HIDDEN);
	}

	
}
