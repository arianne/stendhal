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

import games.stendhal.common.Direction;
import games.stendhal.server.entity.RPEntity;

import java.awt.geom.Rectangle2D;

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPClass;

/**
 * A door is a special kind of portal which can be open or closed.
 *
 * Note that you can link a door with a portal; that way, people only
 * require the key when walking in one direction and can walk in the
 * other direction without any key.
 */
public class Door extends Portal {

	/** Whether or not the door is currently open */
	private boolean open;

	public static void generateRPClass() {
		RPClass door = new RPClass("door");
		door.isA("entity");
		door.add("class", RPClass.STRING);
		door.add("locked", RPClass.STRING, RPClass.PRIVATE);
		door.add("open", RPClass.FLAG);
	}

	/**
	 * Creates a new door.
	 * @param clazz The class. Responsible for how this door looks like.
	 * @param dir The direction in which one has to walk in order to pass
	 *            through this door
	 * @throws AttributeNotFoundException
	 */
	public Door(String clazz, Direction dir) {
		super();
		put("type", "door");
		put("class", clazz);

		setDirection(dir);

		open = false;
	}

	@Override
	public void getArea(Rectangle2D rect, double x, double y) {
		rect.setRect(x, y, 1, 1);
	}

	@Override
	public void update() {
		super.update();
		open = has("open");
	}

	/**
	 * Opens the door. 
	 */
	protected void open() {
        open = true;
		put("open", "");
		notifyWorldAboutChanges();
	}

	/**
	 * Closes the door. 
	 */
	protected void close() {
		this.open = false;
		if (has("open")) {
			remove("open");
		}
		notifyWorldAboutChanges();
	}

	protected boolean isOpen() {
		return open;
	}


	/**
	 * teleport (if the door is now open)
	 */
	public void onUsed(RPEntity user) {
		if (isOpen()) {
			super.onUsed(user);
		}
	}

	@Override
	public void onUsedBackwards(RPEntity user) {
		open();
		notifyWorldAboutChanges();
	}

	@Override
	public String describe() {
		String text = "You see a door.";
		if (hasDescription()) {
			text = getDescription();
		}
		text += " It is " + (isOpen() ? "open" : "closed") + ".";
		return (text);
	}

}
