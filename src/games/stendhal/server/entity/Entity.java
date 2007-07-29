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
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.Definition.Type;

public abstract class Entity extends RPObject {

	private int x;

	private int y;

	private boolean collides;

	/**
	 * Whether this will collide with other entities.
	 */
	private boolean obstacle;

	public static void generateRPClass() {
		RPClass entity = new RPClass("entity");

		// Some things may have a textual description
		entity.addAttribute("description", Type.LONG_STRING, Definition.HIDDEN);

		// TODO: Try to remove this attribute later
		entity.addAttribute("type", Type.STRING);

		/*
		 * Entity is an obstacle.
		 */
		entity.addAttribute("obstacle", Type.FLAG, Definition.VOLATILE);

		entity.addAttribute("x", Type.SHORT);
		entity.addAttribute("y", Type.SHORT);

		/*
		 * If this is set, the client will discard/ignore entity
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
	}

	public Entity(RPObject object) {
		super(object);

		setObstacle(true);

		if (!has("visibility")) {
			setVisibility(100);
		}

		update();
	}

	public Entity() {
		setObstacle(true);
		setVisibility(100);
	}

	public void update() {
		if (has("x")) {
			x = getInt("x");
		}
		if (has("y")) {
			y = getInt("y");
		}

		obstacle = has("obstacle");
	}

	public boolean hasDescription() {
		if (has("description")) {
			return ((getDescription() != null) && (getDescription().length() > 0));
		}
		return (false);
	}

	public void setDescription(String text) {
		if (text == null) {
			text = "";
		}
		put("description", text);
	}

	public String getDescription() {
		String description = "";
		if (has("description")) {
			description = get("description");
		}
		return description;
	}

	/**
	 * Set the entity coordinates.
	 *
	 * @param x
	 *            The X coordinate.
	 * @param y
	 *            The Y coordinate.
	 */
	public void set(final int x, final int y) {
		setX(x);
		setY(y);
	}

	/**
	 * Set the entity X coordinate.
	 *
	 * @param x
	 *            The X coordinate.
	 */
	public void setX(final int x) {
		if ((x == this.x) && (x != 0)) {
			return;
		}

		this.x = x;
		put("x", x);
	}

	/**
	 * Get the entity name.
	 *
	 * @return The entity's name, or <code>null</code> if undefined.
	 */
	public String getName() {
		if (has("name")) {
			return get("name").replace("_", " ");
		} else {
			return null;
		}
	}

	/**
	 * Get the nicely formatted entity title/name.
	 *
	 * @return The title, or <code>null</code> if unknown.
	 */
	public String getTitle() {
		if (has("title")) {
			return get("title");
		} else if (has("name")) {
			return get("name").replace('_', ' ');
		} else if (has("subclass")) {
			return get("subclass").replace('_', ' ');
		} else if (has("class")) {
			return get("class").replace('_', ' ');
		} else if (has("type")) {
			return get("type").replace('_', ' ');
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
	 * Set the entity Y coordinate.
	 *
	 * @param y
	 *            The Y coordinate.
	 */
	public void setY(final int y) {
		if ((y == this.y) && (y != 0)) {
			return;
		}

		this.y = y;
		put("y", y);
	}

	/**
	 * Get the entity Y coordinate.
	 *
	 * @return The Y coordinate.
	 */
	public int getY() {
		return y;
	}

	private StendhalRPZone zone;

	/**
	 * Get the zone this entity is in.
	 *
	 * @return A zone, or <code>null</code> if not in one.
	 */
	public StendhalRPZone getZone() {
		// Use onAdded()/onRemoved() to grab a copy
		// of the zone and save as a local variable.
		return zone;

	}

	/**
	 * is this entity not moving
	 *
	 * TODO: Remove after the StendhalNavigableEntities dependancy is gone
	 *
	 * @return true, if it stopped, false if it is moving
	 */
	public boolean stopped() {
		return true;
	}

	protected void setCollides(boolean collides) {
		this.collides = collides;
	}

	/**
	 * TODO: docu
	 *
	 * @return ???
	 */
	public boolean collides() {
		return collides;
	}

	/**
	 * Checks whether an entity is a ghost (non physically interactive).
	 *
	 * @return <code>true</code> if in ghost mode.
	 */
	public boolean isGhost() {
		return has("ghostmode");
	}

	/**
	 * Determine if this is an obstacle for other entities in general.
	 *
	 * @param entity
	 *            The entity to check against.
	 *
	 * @return <code>true</code> if an obstacle.
	 */
	protected boolean isObstacle() {
		return obstacle;
	}

	/**
	 * Determine if this is an obstacle for another entity.
	 *
	 * @param entity
	 *            The entity to check against.
	 *
	 * @return <code>true</code> if not a ghost.
	 */
	public boolean isObstacle(Entity entity) {
		return isObstacle() && !isGhost();
	}


	/**
	 * Set this entity as an obstacle.
	 *
	 * @param	obstacle	<code>true</code> if an obstacle.
	 */
	public void setObstacle(boolean obstacle) {
		this.obstacle = obstacle;

		if(obstacle) {
			put("obstacle", "");
		} else if(has("obstacle")) {
			remove("obstacle");
		}
	}

	/**
	 * Calculates the squared distance between the two given rectangles, i.e.
	 * the square of the minimal distance between a point in rect1 and a point
	 * in rect2.
	 *
	 * We're calculating the square because the square root operation would be
	 * expensive. As long as we only need to compare distances, it doesn't
	 * matter if we compare the distances or the squares of the distances (the
	 * square operation is strictly monotonous for positive numbers).
	 *
	 * TODO: consider moving this to a class in games.stendhal.common
	 *
	 * @param rect1
	 *            The first rectangle.
	 * @param rect2
	 *            The second rectangle.
	 * @return The squared distance between the two rectangles.
	 */
	private static int squaredDistanceBetween(Rectangle2D rect1,
			Rectangle2D rect2) {
		int left1 = (int) rect1.getMinX();
		// minus one because we want the distance of two 1x1 entities standing
		// directly next to each other to be 1, not 0.
		int right1 = (int) rect1.getMaxX() - 1;
		int top1 = (int) rect1.getMinY();
		int bottom1 = (int) rect1.getMaxY() - 1;

		int left2 = (int) rect2.getMinX();
		int right2 = (int) rect2.getMaxX() - 1;
		int top2 = (int) rect2.getMinY();
		int bottom2 = (int) rect2.getMaxY() - 1;

		int xDist = 0;
		int yDist = 0;

		if (right1 < right2) {
			if (right1 > left2) {
				xDist = 0;
			} else {
				// rect1 is left of rect2
				xDist = left2 - right1;
			}
		} else {
			if (right2 > left1) {
				xDist = 0;
			} else {
				// rect1 is right of rect2
				xDist = left1 - right2;
			}
		}
		if (bottom1 < bottom2) {
			if (bottom1 > top2) {
				yDist = 0;
			} else {
				// rect1 is over rect2
				yDist = top2 - bottom1;
			}
		} else {
			if (bottom2 > top1) {
				yDist = 0;
			} else {
				// rect1 is under rect2
				yDist = top1 - bottom2;
			}
		}
		return xDist * xDist + yDist * yDist;
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
	 */
	public double squaredDistance(Entity other) {
		if ((!has("width") || getInt("width") == 1)
				&& (!has("height") || getInt("height") == 1)) {
			// This doesn't work properly if the other entity is larger
			// than 1x1, but it is faster.
			return squaredDistance(other.x, other.y);
		}
		return squaredDistanceBetween(getArea(), other.getArea());
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
	 */
	public double squaredDistance(int x, int y) {
		if ((!has("width") || getInt("width") == 1)
				&& (!has("height") || getInt("height") == 1)) {
			// This doesn't work properly if this entity is larger
			// than 1x1, but it is faster.
			return (x - this.x) * (x - this.x) + (y - this.y) * (y - this.y);
		}
		Rectangle2D thisArea = getArea();
		// for 1x2 size creatures the destArea, needs bo be one up (this sucks
		// badly)
		thisArea = new Rectangle2D.Double(thisArea.getX(), this.getY(),
				thisArea.getWidth(), thisArea.getHeight());

		return squaredDistanceBetween(thisArea, new Rectangle(x, y, 1, 1));
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
	public boolean nextTo(int x, int y, double step) {
		Rectangle2D thisArea = getArea();
		thisArea
				.setRect(thisArea.getX() - step, thisArea.getY() - step,
						thisArea.getWidth() + 2 * step, thisArea.getHeight()
								+ 2 * step);
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
	public boolean nextTo(Entity entity) {
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
	public boolean nextTo(Entity entity, double step) {
		Rectangle2D thisArea = getArea();
		Rectangle2D otherArea = entity.getArea();
		thisArea.setRect(thisArea.getX() - step, thisArea.getY() - step,
				thisArea.getWidth() + step, thisArea.getHeight() + step);
		otherArea.setRect(otherArea.getX() - step, otherArea.getY() - step,
				otherArea.getWidth() + step, otherArea.getHeight() + step);

		return thisArea.intersects(otherArea);
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
	 * returns the area used by this entity
	 *
	 * @param ex
	 *            x
	 * @param ey
	 *            y
	 * @return rectangle for the used area
	 */
	public Rectangle2D getArea(double ex, double ey) {
		Rectangle2D rect = new Rectangle.Double();
		getArea(rect, ex, ey);
		return rect;
	}

	/**
	 * returns the area used by this entity. Note for performance reasons the
	 * Rectangle is not returned as new object but the one supplied as first
	 * parameter is modified.
	 *
	 * @param rect
	 *            the area is stored into this paramter
	 * @param x
	 *            x
	 * @param y
	 *            y
	 */
	public void getArea(final Rectangle2D rect, final double x, final double y) {
		rect.setRect(x, y, 1.0, 1.0);
	}

	/**
	 * Called when this object is added to a zone.
	 *
	 * @param zone
	 *            The zone this was added to.
	 */
	public void onAdded(StendhalRPZone zone) {
		this.zone = zone;
	}

	/**
	 * Called when this object is being removed from a zone.
	 *
	 * @param zone
	 *            The zone this will be removed from.
	 */
	public void onRemoved(StendhalRPZone zone) {
	}

	/**
	 * Notifies the StendhalRPWorld that this entity's attributes have changed.
	 *
	 * TODO: Find a way to move this up to RPObject.
	 */
	public void notifyWorldAboutChanges() {
		StendhalRPWorld.get().modify(this);
	}

	/**
	 * describes the entity (if a players looks at it)
	 *
	 * @return description from the players point of view
	 */
	public String describe() {
		String ret = "You see ";
		if (hasDescription()) {
			return (getDescription());
		}

		ret += getDescriptionName(false);
		return (ret + ".");
	}

	/**
	 * returns the name or something that can be used to identify the entity for
	 * the player
	 *
	 * @param definite
	 *            true for "the" and false for "a/an" in case the entity has no
	 *            name
	 * @return name
	 */
	public String getDescriptionName(boolean definite) {
		String name = null;
		if ((name = getName()) != null) {
			return name;
		} else if (has("subclass")) {
			return Grammar.article_noun(get("subclass"), definite);
		} else if (has("class")) {
			return Grammar.article_noun(get("class"), definite);
		} else {
			String ret = "something indescribably strange";
			if (has("type")) {
				ret += " of type " + get("type");
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
	 * Set the entity's visibility.
	 *
	 * @param visibility
	 *            The visibility (0-100).
	 */
	public void setVisibility(final int visibility) {
		put("visibility", visibility);
	}
}
