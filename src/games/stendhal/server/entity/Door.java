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
package games.stendhal.server.entity;

import java.awt.geom.Rectangle2D;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPClass;
import games.stendhal.common.Direction;

public class Door extends Portal {
	private boolean open;

	//private int width;

	public static void generateRPClass() {
		RPClass door = new RPClass("door");
		door.isA("entity");
		door.add("class", RPClass.STRING);
		door.add("locked", RPClass.STRING, RPClass.PRIVATE);
		door.add("open", RPClass.FLAG);
	}

	public Door(String key, String clazz, Direction dir)
			throws AttributeNotFoundException {
		super();
		put("type", "door");
		put("class", clazz);
		put("locked", key);

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
		open = false;
		if (has("open"))
			open = true;
	}

	public void open() {
		this.open = true;
		put("open", "");
	}

	public void close() {
		this.open = false;
		remove("open");
	}

	public boolean isOpen() {
		return open;
	}

	@Override
	public void onUsed(RPEntity user) {
		if (has("locked") && user.isEquipped(get("locked"))) {
			if (!isOpen()) {
				open();
				world.modify(this);
			}
		} else {
			if (isOpen()) {
				close();
				world.modify(this);
			}
		}

		if (isOpen()) {
			super.onUsed(user);
		}
	}

	@Override
	public String describe() {
		String text = "You see a door.";
		if (hasDescription())
			text = getDescription();
		text += " It is " + (isOpen() ? "open." : "closed.");
		return (text);
	}
}
