/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
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

import games.stendhal.common.constants.Events;
import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;

/**
 * Tell a player about being invited to join a group, or the invitation expiring.
 *
 * @author hendrik
 */
public class GroupInviteEvent extends RPEvent {

	/**
	 * Creates the rpclass.
	 */
	public static void generateRPClass() {
		final RPClass rpclass = new RPClass(Events.GROUP_INVITE);
		rpclass.add(DefinitionClass.ATTRIBUTE, "leader", Type.STRING);
		rpclass.add(DefinitionClass.ATTRIBUTE, "expire", Type.FLAG);
	}

	/**
	 * Creates a new group invite event.
	 *
	 * @param leader name of the group leader who invited this player
	 * @param expire true, if this invited has expired, false if it is still valid
	 */
	public GroupInviteEvent(String leader, boolean expire) {
		super(Events.GROUP_INVITE);
		put("leader", leader);
		if (expire) {
			put("expire", "");
		}
	}

}
