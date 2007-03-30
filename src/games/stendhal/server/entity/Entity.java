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
import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;

public abstract class Entity extends RPObject {

	private int x;

	private int y;

	private Direction direction;

	private double speed;

	private boolean collides;

	public static void generateRPClass() {
		RPClass entity = new RPClass("entity");
		entity.add("description", RPClass.LONG_STRING, RPClass.HIDDEN); // Some things may have a textual description
		entity.add("x", RPClass.SHORT);
		entity.add("y", RPClass.SHORT);
		entity.add("dir", RPClass.BYTE, RPClass.VOLATILE);
		entity.add("speed", RPClass.FLOAT, RPClass.VOLATILE);

		/*
		 * If this is set, the client will discard/ignore entity
		 */
		entity.add("server-only", RPClass.FLAG, RPClass.VOLATILE);
	}

	public Entity(RPObject object) throws AttributeNotFoundException {
		super(object);
		direction = Direction.STOP;
		speed = 0;
		update();
	}

	public Entity() throws AttributeNotFoundException {
		super();
	}

	public void update() throws AttributeNotFoundException {
		if (has("x")) {
			x = getInt("x");
		}
		if (has("y")) {
			y = getInt("y");
		}
		if (has("speed")) {
			speed = getDouble("speed");
		}
		if (has("dir")) {
			direction = Direction.build(getInt("dir"));
		}
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

	public void set(int x, int y) {
		setX(x);
		setY(y);
	}

	public void setX(int x) {
		if ((x == this.x) && (x != 0)) {
			return;
		}
		this.x = x;
		put("x", x);
	}

	/**
	 * Get the entity name.
	 *
	 * @return	The entity's name, or <code>null</code> if undefined.
	 */
	public String getName() {
		String name;

		if (has("name")) {
			if ((name = get("name")) != null) {
				return name.replace("_", " ");
			}
		}

		return null;
	}

	public int getX() {
		return x;
	}

	public void setY(int y) {
		if ((y == this.y) && (y != 0)) {
			return;
		}
		this.y = y;
		put("y", y);
	}

	public int getY() {
		return y;
	}

	public void setDirection(Direction dir) {
		if (dir == this.direction) {
			return;
		}
		this.direction = dir;
		put("dir", direction.get());
	}

	public Direction getDirection() {
		return direction;
	}

	/**
	 * Get the zone this entity is in.
	 *
	 * @return	A zone, or <code>null</code> if not in one.
	 */
	public StendhalRPZone getZone() {
		//
		// POSSIBLE TODO: Use onAdded()/onRemoved() to grab a copy
		// of the zone and save as a local variable.
		//
		return (StendhalRPZone) StendhalRPWorld.get().getRPZone(getID());
	}

	public void setSpeed(double speed) {
		if (speed == this.speed) {
			return;
		}
		this.speed = speed;
		put("speed", speed);
	}

	public double getSpeed() {
		return speed;
	}

	private int turnsToCompleteMove;

	public boolean isMoveCompleted() {
		++turnsToCompleteMove;
		if (turnsToCompleteMove >= 1.0 / speed) {
			turnsToCompleteMove = 0;
			return true;
		}
		return false;
	}

	public void stop() {
		setSpeed(0);
	}

	public boolean stopped() {
		return speed == 0;
	}

	public void setCollides(boolean collides) {
		this.collides = collides;
	}

	/**
	 * TODO: docu
	 * @return ???
	 */
	public boolean collides() {
		return collides;
	}

	/**
	 * Checks whether an entity is a ghost (non physically interactive).
	 *
	 * @return	<code>true</code> if in ghost mode.
	 */
	public boolean isGhost() {
		return has("ghostmode");
	}

	/**
	 * Determine if this is an obstacle for another entity.
	 *
	 * @param	entity		The entity to check against.
	 *
	 * @return	<code>true</code> if not a ghost.
	 */
	public boolean isObstacle(Entity entity) {
		return !isGhost();
	}

	/**
	 * Calculates the squared distance between the two given rectangles,
	 * i.e. the square of the minimal distance between a point in rect1 
	 * and a point in rect2.
	 *
	 * We're calculating the square because the square root operation
	 * would be expensive. As long as we only need to compare distances,
	 * it doesn't matter if we compare the distances or the squares of
	 * the distances (the square operation is strictly monotonous for positive
	 * numbers).
	 * 
	 * TODO: consider moving this to a class in games.stendhal.common
	 * 
	 * @param rect1 The first rectangle.
	 * @param rect2 The second rectangle.
	 * @return The squared distance between the two rectangles.
	 */
	private static int squaredDistanceBetween(Rectangle2D rect1, Rectangle2D rect2) {
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
	 * This returns square of the distance between this entity and the
	 * given one.
	 * We're calculating the square because the square root operation
	 * would be expensive. As long as we only need to compare distances,
	 * it doesn't matter if we compare the distances or the squares of
	 * the distances (the square operation is strictly monotonous for positive
	 * numbers).
	 * @param other the entity to which the distance should be calculated 
	 */
	public double squaredDistance(Entity other) {
		if (other.getInt("width") == 1 && other.getInt("height") == 1) {
			// This doesn't work properly if the other entity is larger
			// than 1x1, but it is faster.
			return squaredDistance(other.x, other.y);
		}
		return squaredDistanceBetween(getArea(), other.getArea());
	}

	/**
	 * This returns square of the distance from this entity to a specific
	 * point.
	 * We're calculating the square because the square root operation
	 * would be expensive. As long as we only need to compare distances,
	 * it doesn't matter if we compare the distances or the squares of
	 * the distances (the square operation is strictly monotonous for positive
	 * numbers).
	 * @param x The horizontal coordinate of the point
	 * @param y The vertical coordinate of the point
	 */
	public double squaredDistance(int x, int y) {
		if (getInt("width") == 1 && getInt("height") == 1) {
			// This doesn't work properly if this entity is larger
			// than 1x1, but it is faster.
			return (x - this.x) * (x - this.x) + (y - this.y) * (y - this.y);
		}
		return squaredDistanceBetween(getArea(), new Rectangle(x, y, 1, 1));
	}

	/**
	 * Checks whether a certain point is near this entity.
	 * @param x The point's x coordinate
	 * @param y The point's y coordinate
	 * @param step The maximum distance
	 * @return true iff the point is at most <i>step</i> steps away
	 */
	public boolean nextTo(int x, int y, double step) {
		Rectangle2D thisArea = getArea(this.x, this.y);
		thisArea.setRect(thisArea.getX() - step, thisArea.getY() - step, thisArea.getWidth() + step, thisArea
		        .getHeight()
		        + step);
		return thisArea.contains(x, y);
	}

	/**
	 * Checks whether the given entity is directly next to this entity.
	 * This method may be optimized over using nextTo(entity, 0.25).
	 *
	 * @param	entity		The entity
	 *
	 * @return	<code>true</code> if the entity is next to this.
	 */
	public boolean nextTo(Entity entity) {
		// For now call old code (just a convenience function)
		return nextTo(entity, 0.25);
	}

	/**
	 * Checks whether the given entity is near this entity.
	 * @param entity the entity
	 * @param step The maximum distance
	 * @return true iff the entity is at most <i>step</i> steps away
	 */
	public boolean nextTo(Entity entity, double step) {
		Rectangle2D thisArea = getArea(x, y);
		Rectangle2D otherArea = entity.getArea(entity.x, entity.y);
		thisArea.setRect(thisArea.getX() - step, thisArea.getY() - step, thisArea.getWidth() + step, thisArea
		        .getHeight()
		        + step);
		otherArea.setRect(otherArea.getX() - step, otherArea.getY() - step, otherArea.getWidth() + step, otherArea
		        .getHeight()
		        + step);
		return thisArea.intersects(otherArea);
	}

	public boolean facingTo(Entity entity) {
		Rectangle2D thisArea = getArea(x, y);
		Rectangle2D otherArea = entity.getArea(entity.x, entity.y);
		if ((direction == Direction.UP) && (thisArea.getX() == otherArea.getX())
		        && (thisArea.getY() - 1 == otherArea.getY())) {
			return true;
		}
		if ((direction == Direction.DOWN) && (thisArea.getX() == otherArea.getX())
		        && (thisArea.getY() + 1 == otherArea.getY())) {
			return true;
		}
		if ((direction == Direction.LEFT) && (thisArea.getY() == otherArea.getY())
		        && (thisArea.getX() - 1 == otherArea.getX())) {
			return true;
		}
		if ((direction == Direction.RIGHT) && (thisArea.getY() == otherArea.getY())
		        && (thisArea.getX() + 1 == otherArea.getX())) {
			return true;
		}
		return false;
	}

	public void faceTo(Entity entity) {
		Rectangle2D otherArea = entity.getArea(entity.getX(), entity.getY());
		setDirection(directionTo((int) otherArea.getX(), (int) otherArea.getY()));
	}

	private Direction directionTo(int px, int py) {
		Rectangle2D area = getArea(x, y);
		int rx = (int) area.getX();
		int ry = (int) area.getY();
		if (Math.abs(px - rx) > Math.abs(py - ry)) {
			if (px - rx > 0) {
				return Direction.RIGHT;
			} else {
				return Direction.LEFT;
			}
		} else {
			if (py - ry > 0) {
				return Direction.DOWN;
			} else {
				return Direction.UP;
			}
		}
	}

	/**
	 * Get the area this object currently occupies.
	 *
	 * @return	A rectangular area.
	 */
	public Rectangle2D getArea() {
		return getArea(getX(), getY());
	}

	public Rectangle2D getArea(double ex, double ey) {
		Rectangle2D rect = new Rectangle.Double();
		getArea(rect, ex, ey);
		return rect;
	}

	abstract public void getArea(Rectangle2D rect, double x, double y);

	/**
	 * Called when this object is added to a zone.
	 *
	 * @param	zone		The zone this was added to.
	 */
	public void onAdded(StendhalRPZone zone) {
	}

	/**
	 * Called when this object is being removed from a zone.
	 *
	 * @param	zone		The zone this will be removed from.
	 */
	public void onRemoved(StendhalRPZone zone) {
	}

	/**
	 * Notifies the StendhalRPWorld that this entity's attributes have
	 * changed.
	 * 
	 * TODO: Find a way to move this up to RPObject.
	 */
	public void notifyWorldAboutChanges() {
		StendhalRPWorld.get().modify(this);
	}

	public String describe() {
		String name;

		String ret = "You see ";
		if (hasDescription()) {
			return (getDescription());
		}

		if ((name = getName()) != null) {
			ret += name;
		} else if (has("subclass")) {
			ret += Grammar.a_noun(get("subclass"));
		} else if (has("class")) {
			ret += Grammar.a_noun(get("class"));
		} else {
			ret += "something indescribably strange";
			if (has("type")) {
				ret += " of type " + get("type");
			}
			if (has("id")) {
				ret += " with id " + get("id");
			}
			if (has("zone")) {
				ret += " in zone " + get("zone");
			}
		}
		return (ret + ".");
	}
}
