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
package games.stendhal.server.entity.mapstuff.useable;

import games.stendhal.server.core.events.UseListener;
import games.stendhal.server.entity.Entity;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;

/**
 * an entity that can be placed on the map and will allow the client to "use"
 * it. It support animations and multiple states for the sprites.
 *
 * @author hendrik
 */
public abstract class UseableEntity extends Entity implements UseListener {

	/**
	 * creates a new UseableEntity
	 */
	public UseableEntity() {
		setRPClass("useable_entity");
		put("type", "useable_entity");
		put("cursor", "ACTIVITY");
		super.setSize(1, 1);
	}

	public static void generateRPClass() {
		final RPClass rpclass = new RPClass("useable_entity");
		rpclass.isA("entity");

		// class: source/switch
		rpclass.addAttribute("class", Type.STRING);

		// subclass: long sword/leather/armor/...
		rpclass.addAttribute("subclass", Type.STRING);

		// name of item: gold_source
		rpclass.addAttribute("name", Type.STRING);

		// state (row in sprite image)
		rpclass.addAttribute("state", Type.INT);
	}

	/**
	 * gets the current state
	 *
	 * @return current state
	 */
	public int getState() {
		return getInt("state");
	}

	/**
	 * sets the state
	 *
	 * @param state new state
	 */
	public void setState(int state) {
		put("state", state);
		notifyWorldAboutChanges();
	}
}
