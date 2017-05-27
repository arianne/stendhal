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
package games.stendhal.client.entity;

import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPObject.ID;
import marauroa.common.game.RPSlot;

public class Gate implements IEntity {

	private double x;
	private double y;
	private double width;
	private double height;
	private RPObject rpObject;
	private double radius;

	public Gate() {
		radius = 6;
	}

	@Override
	public void addChangeListener(final EntityChangeListener<?> listener) {
		// unused
	}

	@Override
	public Rectangle2D getArea() {
		return new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight());
	}

	public Rectangle2D getAudibleArea() {

		return new Rectangle2D.Double(getX() - radius / 2, getY() - radius / 2, radius, radius);
	}

	@Override
	public String getEntityClass() {
		if (rpObject == null) {
			return "";
		}
		return this.rpObject.get("class");
	}

	@Override
	public String getEntitySubclass() {
		if (rpObject == null) {
			return "";
		}
		return this.rpObject.get("subclass");
	}

	@Override
	public double getHeight() {
		return height;
	}

	@Override
	public ID getID() {
		if (rpObject == null) {
			return null;
		} else {
			return rpObject.getID();
		}

	}

	@Override
	public String getName() {
		return "";
	}

	@Override
	public RPObject getRPObject() {
		return rpObject;
	}

	@Override
	public int getResistance() {
		if (rpObject == null) {
			return 0;
		}

		return rpObject.getInt("resistance");
	}

	@Override
	public int getResistance(final IEntity entity) {
		return getResistance();
	}

	@Override
	public RPSlot getSlot(final String name) {
		return null;
	}

	@Override
	public String getTitle() {
		if (rpObject == null) {
			return null;
		}
		return rpObject.get("title");
	}

	@Override
	public String getType() {
		if (rpObject == null) {
			return null;
		}
		return rpObject.getRPClass().getName();
	}

	@Override
	public int getVisibility() {
		return 100;
	}

	@Override
	public double getWidth() {
		return width;
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public void initialize(final RPObject object) {
		x = object.getInt("x");
		y = object.getInt("y");
		width = object.getInt("width");
		height = object.getInt("height");
		this.rpObject = object;
	}

	@Override
	public boolean isObstacle(final IEntity entity) {
		return getResistance() > 0;
	}

	@Override
	public boolean isOnGround() {
		return true;
	}

	@Override
	public boolean isUser() {
		return false;
	}

	@Override
	public void release() {
		// do nothing
	}

	@Override
	public void removeChangeListener(final EntityChangeListener<?> listener) {
		// unused
	}

	public void setAudibleRange(final double range) {
		radius = range;
	}

	@Override
	public void update(final int delta) {
		// unused
	}

	@Override
	public String getCursor() {
		return "ACTIVITY";
	}

	/**
	 * Get identifier path for the entity.
	 *
	 * @return List of object identifiers and slot names that make up the
	 * 	complete path to the entity
	 */
	@Override
	public List<String> getPath() {
		LinkedList<String> path = new LinkedList<String>();
		RPObject object = getRPObject();
		while (object != null) {
			// Prepend the items; They'll be in a nice container-slot-content
			// order for the server
			path.add(0, Integer.toString(object.getID().getObjectID()));
			RPSlot slot = object.getContainerSlot();
			if (slot != null) {
				path.add(0, slot.getName());
			}
			object = object.getContainer();
		}
		return path;
	}

	@Override
	public void addContentChangeListener(ContentChangeListener listener) {
		// unused
	}

	@Override
	public void removeContentChangeListener(ContentChangeListener listener) {
		// unused
	}
}
