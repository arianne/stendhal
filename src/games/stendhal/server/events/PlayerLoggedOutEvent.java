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
package games.stendhal.server.events;

import org.apache.log4j.Logger;

import games.stendhal.common.constants.Events;
import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;

public class PlayerLoggedOutEvent extends RPEvent {

	private static final String NAME_ATTRIBUTE = "name";
	private static final Logger logger = Logger.getLogger(PlayerLoggedOutEvent.class);

	public static void generateRPClass() {
		try {
			RPClass clazz = new RPClass(Events.PLAYER_LOGGED_OUT);
			clazz.add(DefinitionClass.ATTRIBUTE, NAME_ATTRIBUTE, Type.STRING);
		} catch (Exception e) {
			logger.error("cannot generate RPClass", e);
		}
	}

	public PlayerLoggedOutEvent(String name) {
		super(Events.PLAYER_LOGGED_OUT);
		put(NAME_ATTRIBUTE, name);
	}

}
