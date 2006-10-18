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
package games.stendhal.server.entity.item;

import games.stendhal.server.entity.Player;

import java.util.Map;

import org.apache.log4j.Logger;

public class Token extends Item {
	private static Logger logger = Logger.getLogger(Token.class);
	
	public Token(String name, String clazz, String subclass, Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	private void process(Player player) {
		logger.warn("Not implemented yet");
	}
	
	@Override
	public void onPutOnGround(Player player) {
		super.onPutOnGround(player);
		process(player);
	}

	@Override
	public boolean canBeEquiped() {
		return false;
	}
}
