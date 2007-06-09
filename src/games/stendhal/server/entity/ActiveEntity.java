/*
 * @(#) src/games/stendhal/server/entity/ActiveEntity.java
 *
 * $Id$
 */

package games.stendhal.server.entity;

//
//

import java.awt.geom.Rectangle2D;

import games.stendhal.common.Direction;
import games.stendhal.server.entity.Entity;

import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;

/**
 * An entity that hase speed and direction.
 */
public abstract class ActiveEntity extends Entity {
	/*
	 * The facing direction
	 */
	private Direction	direction;

	/**
	 * The current speed.
	 */
	private double		speed;

	/**
	 * The number of turns to complete a move.
	 */
	private int		turnsToCompleteMove;


	/**
	 * Create an active entity.
	 */
	public ActiveEntity() {
		direction = Direction.STOP;
		speed = 0.0;
	}


	/**
	 * Create an active entity.
	 *
	 * @param	object		The source object.
	 */
	public ActiveEntity(final RPObject object) {
		super(object);

		direction = Direction.STOP;
		speed = 0.0;

		update();
	}


	//
	// ActiveEntity
	//

	/**
	 * Define the RPClass.
	 *
	 * @return	The configured RPClass.
	 */
	private static RPClass createRPClass() {
		RPClass rpclass = new RPClass("active_entity");

		rpclass.isA("entity");
		rpclass.add("dir", RPClass.BYTE, RPClass.VOLATILE);
		rpclass.add("speed", RPClass.FLOAT, RPClass.VOLATILE);

		return rpclass;
	}


	/**
	 * Face toward an entity.
	 *
	 * @param	entity		The entity to face toward.
	 */
	public void faceToward(final Entity entity) {
		setDirection(getDirectionToward(entity));
	}


	/**
	 * Generate the RPClass (compatible with manual init/order).
	 *
	 * NOTE: This MUST be called during environment initialization.
	 */
	public static void generateRPClass() {
		createRPClass();
	}


	/**
	 * Get the current facing direction.
	 *
	 * @return	The facing direction.
	 */
	public Direction getDirection() {
		return direction;
	}


	/**
	 * Get the direction toward an entity.
	 *
	 * @param	entity		The target entity.
	 *
	 * @return	A facing direction.
	 */
	public Direction getDirectionToward(final Entity entity) {
		Rectangle2D area = entity.getArea();

		return getDirectionToward(area.getCenterX(), area.getCenterY());
	}


	/**
	 * Get the direction toward a point.
	 *
	 * @param	x		The target X coordinate.
	 * @param	y		The target Y coordinate.
	 *
	 * @return	A facing direction.
	 */
	private Direction getDirectionToward(final double x, final double y) {
		Rectangle2D area = getArea();

		double rx = area.getCenterX();
		double ry = area.getCenterY();

		if (Math.abs(x - rx) > Math.abs(y - ry)) {
			if (x - rx > 0) {
				return Direction.RIGHT;
			} else {
				return Direction.LEFT;
			}
		} else {
			if (y - ry > 0) {
				return Direction.DOWN;
			} else {
				return Direction.UP;
			}
		}
	}


	/**
	 * Get the current speed.
	 *
	 * @return	The current speed, or <code>0.0</code> if stopped.
	 */
	public double getSpeed() {
		return speed;
	}


	/**
	 * Determine if this entity is facing toward another entity.
	 *
	 * @param	entity		The target entity.
	 *
	 * @return	<code>true</code> if facing the other entity.
	 */
	public boolean isFacingToward(final Entity entity) {
		return direction.equals(getDirectionToward(entity));
	}


	public boolean isMoveCompleted() {
		++turnsToCompleteMove;

		if (turnsToCompleteMove >= (1.0 / speed)) {
			turnsToCompleteMove = 0;
			return true;
		}

		return false;
	}


	/**
	 * Determine if this entity is not moving.
	 *
	 * @return	<code>true</code> if it is stopped.
	 */
	public boolean isStopped() {
		return (speed == 0.0);
	}


	/**
	 * Set the facing direction.
	 *
	 * @param	dir		The facing direction.
	 */
	public void setDirection(final Direction dir) {
		if (dir == this.direction) {
			return;
		}

		this.direction = dir;
		put("dir", direction.get());
	}


	/**
	 * Set the movement speed.
	 *
	 * @param	speed		The new speed.
	 */
	public void setSpeed(final double speed) {
		if (speed == this.speed) {
			return;
		}

		this.speed = speed;
		put("speed", speed);
	}


	/**
	 * Stops entity movement.
	 */
	public void stop() {
		setSpeed(0.0);
	}


	//
	// Entity
	//

	@Override
	public void update() {
		super.update();

		if (has("dir")) {
			direction = Direction.build(getInt("dir"));
		}

		if (has("speed")) {
			speed = getDouble("speed");
		}
	}


	//
	// <Compat>
	//

	/**
	 * is this entity not moving
	 *
	 * @return true, if it stopped, false if it is moving
	 */
	@Override
	public boolean stopped() {
		return isStopped();
	}
}
