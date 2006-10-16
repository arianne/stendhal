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
package games.stendhal.server.entity.portal;

import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.RPEntity;

/**
 * A portal which allows only certain levels of player to use it.
 *
 * @author hendrik
 */
public class LevelCheckingPortal extends Portal {
	private int minLevel = 0;
	private int maxLevel = 99;

	public LevelCheckingPortal() {
		super();
	}

	/**
	 * creates a level checking portal
	 *
	 * @param minLevel
	 * @param maxLevel
	 */
	public LevelCheckingPortal(int minLevel, int maxLevel) {
		super();
		this.minLevel = minLevel;
		this.maxLevel = maxLevel;
	}

	public void onUsed(RPEntity user) {
		Player player = (Player) user;
		if (player.getLevel() < minLevel) {
			player.sendPrivateText("I am to weak to use this portal.");
		} else if (player.getLevel() > maxLevel) {
			player.sendPrivateText("I am to strong to use this portal.");
		} else {
			super.onUsed(player);
		}
	}
}
