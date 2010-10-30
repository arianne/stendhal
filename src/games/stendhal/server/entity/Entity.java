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

import games.stendhal.common.Grammar;
import games.stendhal.common.ItemTools;
import games.stendhal.common.constants.Events;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.slot.EntitySlot;
import games.stendhal.server.entity.slot.Slot;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import marauroa.common.game.Definition;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import org.apache.log4j.Logger;

public abstract class Entity extends RPObject {
	/**
	 * The logger.
	 */
	private static final Logger logger = Logger.getLogger(Entity.class);

	
	protected Rectangle2D area = new Rectangle.Double();
	
	private int x;

	private int y;

	/**
	 * The width (in world units). Using double instead of int to avoid type
	 * conversion in getArea().
	 */
	private double width;

	/**
	 * The height (in world units). Using double instead of int to avoid type
	 * conversion in getArea().
	 */
	private double height;

	/**
	 * Amount of resistance this has with other entities (0-100).
	 */
	private int resistance;

	private StendhalRPZone zone;
	private StendhalRPZone lastZone;

	public Entity(final RPObject object) {
		super(object);

		if (!has("x")) {
			put("x", 0);
		}

		if (!has("y")) {
			put("y", 0);
		}

		if (!has("width")) {
			put("width", 1);
		}

		if (!has("height")) {
			put("height", 1);
		}

		if (!has("resistance")) {
			put("resistance", 100);
		}

		if (!has("visibility")) {
			put("visibility", 100);
		}

		update();
	}

	public Entity() {
		put("x", 0);
		put("y", 0);

		x = 0;
		y = 0;

		setSize(1, 1);

		setResistance(100);
		setVisibility(100);
	}

	public static void generateRPClass() {
		final RPClass entity = new RPClass("entity");

		// Some things may have a textual description
		entity.addAttribute("description", Type.LONG_STRING, Definition.HIDDEN);

		// TODO: Try to remove this attribute later (at DB reset?)
		entity.addAttribute("type", Type.STRING);

		/**
		 * Resistance to other entities (0-100). 0=Phantom, 100=Obstacle.
		 */
		entity.addAttribute("resistance", Type.BYTE, Definition.VOLATILE);

		entity.addAttribute("x", Type.SHORT);
		entity.addAttribute("y", Type.SHORT);

		/*
		 * The size of the entity (in world units).
		 */
		entity.addAttribute("width", Type.SHORT, Definition.VOLATILE);
		entity.addAttribute("height", Type.SHORT, Definition.VOLATILE);

		/*
		 * Obsolete and ignored by the client. Do not use.
		 */
		entity.addAttribute("server-only", Type.FLAG, Definition.VOLATILE);

		/*
		 * The current overlayed client effect.
		 */
		entity.addAttribute("effect", Type.STRING, Definition.VOLATILE);

		/*
		 * The visibility of the entity drawn on client (0-100). 0=Invisible,
		 * 100=Solid. Useful when mixed with effect.
		 */
		entity.addAttribute("visibility", Type.INT, Definition.VOLATILE);

		// cursor
		entity.addAttribute("cursor", Type.STRING);

		// sound events
		entity.addRPEvent(Events.SOUND, Definition.VOLATILE);
	}


	public void update() {
		final int oldX = x;
		final int oldY = y;
		boolean moved = false;

		if (has("x")) {
			x = getInt("x");

			if (x != oldX) {
				moved = true;
			}
		}

		if (has("y")) {
			y = getInt("y");

			if (y != oldY) {
				moved = true;
			}
		}

		if (moved && (getZone() != null)) {
			onMoved(oldX, oldY, x, y);
		}

		if (has("height")) {
			height = getInt("height");
		}

		if (has("width")) {
			width = getInt("width");
		}

		if (has("resistance")) {
			resistance = getInt("resistance");
		}

	}

	public boolean hasDescription() {
		if (has("description")) {
			return ((getDescription() != null) && (getDescription().length() > 0));
		}
		return (false);
	}

	public void setDescription(final String text) {
		if (text == null) {
			put("description", "");
		} else {
			put("description", text);
		}
		
	}

	public String getDescription() {
		String description = "";
		if (has("description")) {
			description = get("description");
		}
		return description;
	}

	/**
	 * Get the nicely formatted entity title/name.
	 * 
	 * @return The title, or <code>null</code> if unknown.
	 */
	public String getTitle() {
		if (has("subclass")) {
			return ItemTools.itemNameToDisplayName(get("subclass"));
		} else if (has("class")) {
			return ItemTools.itemNameToDisplayName(get("class"));
		} else if (has("type")) {
			return ItemTools.itemNameToDisplayName(get("type"));
		} else {
			return null;
		}
	}

	/**
	 * Get the entity X coordinate.
	 * 
	 * @return The X coordinate.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Get the entity Y coordinate.
	 * 
	 * @return The Y coordinate.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Get the zone this entity is in.
	 * 
	 * @return A zone, or <code>null</code> if not in one.
	 */
	public StendhalRPZone getZone() {
		// Use onAdded()/onRemoved() to grab a reference to the zone and save
		// as a attribute.
		// During zone transfer zone is set to null for a short period of time
		// which causes lots of problems, so we use the old zone until the new
		// one is set.
		return lastZone;
	}

	/**
	 * Is this entity not moving?
	 * 
	 * @return true, if it stopped, false if it is moving
	 */
	public boolean stopped() {
		return true;
	}

	/**
	 * Get the resistance this has on other entities (0-100).
	 * 
	 * @return The amount of resistance, or 0 if in ghostmode.
	 */
	public int getResistance() {
		return resistance;
	}

	/**
	 * Get the resistance between this and another entity (0-100).
	 * @param entity other entity to be evaluated
	 * 
	 * @return The amount of combined resistance.
	 */
	public int getResistance(final Entity entity) {
		return ((getResistance() * entity.getResistance()) / 100);
	}


	/**
	 * Determine if this is an obstacle for another entity.
	 * 
	 * @param entity
	 *            The entity to check against.
	 * 
	 * @return <code>true</code> if very high resistance.
	 */
	public boolean isObstacle(final Entity entity) {
		// > 95% combined resistance = obstacle
		return (getResistance(entity) > 95);
	}

	/**
	 * This returns square of the distance between this entity and the given
	 * one. We're calculating the square because the square root operation would
	 * be expensive. As long as we only need to compare distances, it doesn't
	 * matter if we compare the distances or the squares of the distances (the
	 * square operation is strictly monotonous for positive numbers).
	 * 
	 * @param other
	 *            the entity to which the distance should be calculated
	 * @return double representing the squared distance
	 */
	public final double squaredDistance(final Entity other) {
		final Rectangle2D otherArea = other.getArea();
		final double otherMiddleX = otherArea.getCenterX();
		final double otherMiddleY = otherArea.getCenterY();

		final Rectangle2D thisArea = getArea();
		final double thisMiddleX = thisArea.getCenterX();
		final double thisMiddleY = thisArea.getCenterY();
		
		double xDistance = Math.abs(otherMiddleX - thisMiddleX) - (width + other.width) / 2;
		double yDistance = Math.abs(otherMiddleY - thisMiddleY) - (height + other.height) / 2;

		if (xDistance < 0) {
			xDistance = 0;
		}
		if (yDistance < 0) {
			yDistance = 0;
		}
		return xDistance * xDistance + yDistance * yDistance;
	}

	/**
	 * This returns square of the distance from this entity to a specific point.
	 * We're calculating the square because the square root operation would be
	 * expensive. As long as we only need to compare distances, it doesn't
	 * matter if we compare the distances or the squares of the distances (the
	 * square operation is strictly monotonous for positive numbers).
	 * 
	 * @param x
	 *            The horizontal coordinate of the point
	 * @param y
	 *            The vertical coordinate of the point
	 * @return double representing the squared distance
	 */
	public final double squaredDistance(final int x, final int y) {
		
		
		final double otherMiddleX = x + 0.5;
		final double otherMiddleY = y + 0.5;


		final Rectangle2D thisArea = getArea();
		
		final double thisMiddleX = thisArea.getCenterX();
		final double thisMiddleY = thisArea.getCenterY();
		
		
		double xDistance = Math.abs(otherMiddleX - thisMiddleX) - (width + 1) / 2;
		double yDistance = Math.abs(otherMiddleY - thisMiddleY) - (height + 1) / 2;

		if (xDistance < 0) {
			xDistance = 0;
		}
		if (yDistance < 0) {
			yDistance = 0;
		}
		return xDistance * xDistance + yDistance * yDistance;
	}

	/**
	 * Checks whether a certain point is near this entity.
	 * 
	 * @param x
	 *            The point's x coordinate
	 * @param y
	 *            The point's y coordinate
	 * @param step
	 *            The maximum distance
	 * @return true iff the point is at most <i>step</i> steps away
	 */
	public boolean nextTo(final int x, final int y, final double step) {
		final Rectangle2D thisArea = getArea();
		thisArea.setRect(thisArea.getX() - step, thisArea.getY() - step,
				thisArea.getWidth() + 2 * step, thisArea.getHeight() + 2 * step);
		return thisArea.contains(x, y);
	}

	/**
	 * Checks whether the given entity is directly next to this entity. This
	 * method may be optimized over using nextTo(entity, 0.25).
	 * 
	 * @param entity
	 *            The entity
	 * 
	 * @return <code>true</code> if the entity is next to this.
	 */
	public boolean nextTo(final Entity entity) {
		// For now call old code (just a convenience function)
		return nextTo(entity, 0.25);
	}

	/**
	 * Checks whether the given entity is near this entity.
	 * 
	 * @param entity
	 *            the entity
	 * @param step
	 *            The maximum distance
	 * @return true iff the entity is at most <i>step</i> steps away
	 */
	public boolean nextTo(final Entity entity, final double step) {
		final Rectangle2D thisArea = getArea();
		// To check the overlapping between 'this' and the other 'entity'
		// we create two temporary rectangle objects and initialise them
		// with the position of the two entities.
		// The size is calculated from the original objects with the additional
		// 'step' distance on both sides of the two rectangles.
		// As the absolute position is not important, 'step' need not be
		// subtracted from the values of getX() and getY().
		thisArea.setRect(x - step, y - step, width
				+ 2 * step, height + 2 * step);
		
		return thisArea.intersects(entity.getArea());
	}

	/**
	 * Get the area this object currently occupies.
	 * 
	 * @return A rectangular area.
	 */
	public Rectangle2D getArea() {
		return getArea(getX(), getY());
	}

	/**
	 * Returns the area used by this entity.
	 * 
	 * @param ex
	 *            x
	 * @param ey
	 *            y
	 * @return rectangle for the used area
	 */
	public Rectangle2D getArea(final double ex, final double ey) {
		final Rectangle2D tempRect = new Rectangle.Double();
		getArea(tempRect, ex, ey);
		return tempRect;
	}

	/**
	 * returns the area used by this entity. Note for performance reasons the
	 * Rectangle is not returned as new object but the one supplied as first
	 * parameter is modified.
	 * 
	 * @param rect
	 *            the area is stored into this parameter
	 * @param x
	 *            x
	 * @param y
	 *            y
	 */
	public void getArea(final Rectangle2D rect, final double x, final double y) {
		rect.setRect(x, y, getWidth(), getHeight());
	}

	/**
	 * Called when this object is added to a zone.
	 * 
	 * @param zone
	 *            The zone this was added to.
	 */
	public void onAdded(final StendhalRPZone zone) {
		if (this.zone != null) {
			logger.error("Entity added while in another " + zone + ": " + this, new Throwable());
		}

		this.zone = zone;
		this.lastZone = zone;
	}

	/**
	 * Notification of intra-zone position change.
	 * 
	 * @param oldX
	 *            The old X coordinate.
	 * @param oldY
	 *            The old Y coordinate.
	 * @param newX
	 *            The new X coordinate.
	 * @param newY
	 *            The new Y coordinate.
	 */
	protected void onMoved(final int oldX, final int oldY, final int newX, final int newY) {
		// sub classes can implement this method
	}

	/**
	 * Called when this object is being removed from a zone.
	 * 
	 * @param zone
	 *            The zone this will be removed from.
	 */
	public void onRemoved(final StendhalRPZone zone) {
		if (this.zone != zone) {
			logger.error("Entity removed from wrong zone: " + this);
		}

		this.zone = null;
	}

	/**
	 * Notifies the StendhalRPWorld that this entity's attributes have changed.
	 * 
	 */
	public void notifyWorldAboutChanges() {
		// Only possible if in a zone
		if (getZone() != null) {
			getZone().modify(this);
		}
	}

	/**
	 * Describes the entity (if a players looks at it).
	 * 
	 * @return description from the players point of view
	 */
	public String describe() {
		if (hasDescription()) {
			return getDescription();
		}

		return "You see " + getDescriptionName(false) + ".";
	}

	/**
	 * Returns the name or something that can be used to identify the entity for
	 * the player.
	 * 
	 * @param definite
	 *            true for "the" and false for "a/an" in case the entity has no
	 *            name
	 * @return name 
	 * 
	 */
	public String getDescriptionName(final boolean definite) {
		if (has("subclass")) {
			return Grammar.article_noun(ItemTools.itemNameToDisplayName(get("subclass")), definite);
		} else if (has("class")) {
			return Grammar.article_noun(ItemTools.itemNameToDisplayName(get("class")), definite);
		} else {
			String ret = "something indescribably strange";
			if (has("type")) {
				ret += " of type " + ItemTools.itemNameToDisplayName(get("type"));
			}
			if (has("id")) {
				ret += " with id " + get("id");
			}
			if (has("zone")) {
				ret += " in zone " + get("zone");
			}
			return ret;
		}
	}

	/**
	 * Get the entity height.
	 * 
	 * @return The height (in world units).
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * Get the entity width.
	 * 
	 * @return The width (in world units).
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * Set the entity class.
	 * 
	 * @param clazz
	 *            The class name.
	 */
	public final void setEntityClass(final String clazz) {
		put("class", clazz);
	}

	/**
	 * Set the entity sub-class.
	 * 
	 * @param subclazz
	 *            The sub-class name.
	 */
	public final void setEntitySubClass(final String subclazz) {
		put("subclass", subclazz);
	}

	/**
	 * Sets the entity position.
	 * 
	 * 
	 * <p>
	 * This calls <code>onMoved()</code>. <strong>Note: When placing during a
	 * zone change, this call should be done after being removed from the old
	 * zone, but before adding to the zone to prevent an erroneous position jump
	 * in the zone.</strong>
	 * 
	 * @param x
	 *            The x position (in world units).
	 * @param y
	 *            The y position (in world units).
	 */
	public final void setPosition(final int x, final int y) {
		final int oldX = this.x;
		final int oldY = this.y;
		boolean moved = false;

		if (x != oldX) {
			this.x = x;
			put("x", x);
			moved = true;
		}

		if (y != oldY) {
			this.y = y;
			put("y", y);
			moved = true;
		}

		if (moved && (getZone() != null)) {
			onMoved(oldX, oldY, x, y);
		}
	}

	/**
	 * Set resistance this has with other entities.
	 * 
	 * @param resistance
	 *            The amount of resistance (0-100).
	 */
	public final void setResistance(final int resistance) {
		this.resistance = resistance;
		put("resistance", resistance);
	}

	/**
	 * Set the entity size.
	 * 
	 * @param width
	 *            The width (in world units).
	 * @param height
	 *            The height (in world units).
	 */
	public final void setSize(final int width, final int height) {
		this.width = width;
		put("width", width);

		this.height = height;
		put("height", height);
	}

	/**
	 * gets the name of the mouse cursor or <code>null</code>.
	 *
	 * @return name of the mouse cursor or <code>null</code>.
	 */
	public String getCursor() {
		if (has("cursor")) {
			return get("cursor");
		}
		return null;
	}

	/**
	 * sets the name of the mouse cursor
	 *
	 * @param cursor name of cursor
	 */
	public void setCursor(String cursor) {
		if (cursor == null) {
			remove("cursor");
		} else {
			put("cursor", cursor);
		}
	}

	/**
	 * Set the entity's visibility.
	 * 
	 * @param visibility
	 *            The visibility (0-100).
	 */
	public final void setVisibility(final int visibility) {
		put("visibility", visibility);
	}

	/**
	 * Check if the other Entity is near enough to be in sight on the client screen.
	 *
	 * @param other
	 * @return true if near enough
	 */
	public boolean isInSight(final Entity other) {
		if (other != null) {
			if (other.getZone() == getZone()) {
				// check distance: 640x480 client screen size for 32x32 pixel tiles
				// -> makes 20x15 tiles screen size
				if ((Math.abs(other.getX() - x) <= 20) 
						&& (Math.abs(other.getY() - y) <= 15)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * gets the named entity slot
	 *
	 * @param name name of entity slot
	 * @return EntitySlot or <code>null</code>
	 */
	public Slot getEntitySlot(String name) {
		if (!super.hasSlot(name)) {
			return null;
		}
		RPSlot slot = super.getSlot(name);
		if (!(slot instanceof EntitySlot)) {
			return null;
		}
		return (Slot) slot;
	}
}
