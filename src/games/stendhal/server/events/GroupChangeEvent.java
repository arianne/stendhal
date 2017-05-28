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

import java.util.List;

import games.stendhal.common.constants.Events;
import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;

/**
 * The group has changed (players added, removed, etc)
 *
 * @author hendrik
 */
public class GroupChangeEvent extends RPEvent {

	/**
	 * Creates the rpclass.
	 */
	public static void generateRPClass() {
		final RPClass rpclass = new RPClass(Events.GROUP_CHANGE);
		rpclass.add(DefinitionClass.ATTRIBUTE, "leader", Type.STRING);
		rpclass.add(DefinitionClass.ATTRIBUTE, "members", Type.STRING);
		rpclass.add(DefinitionClass.ATTRIBUTE, "lootmode", Type.STRING);
	}

	/**
	 * Creates a new empty group change event for leaving a group.
	 */
	public GroupChangeEvent() {
		super(Events.GROUP_CHANGE);
	}

	/**
	 * Creates a new group change event.
	 *
	 * @param leader leader of the group
	 * @param members list of members
	 * @param lootmode lootmode "single" or "shared"
	 */
	public GroupChangeEvent(String leader, List<String> members, String lootmode) {
		super(Events.GROUP_CHANGE);
		put("leader", leader);
		put("members", members);
		put("lootmode", lootmode);
	}

}
