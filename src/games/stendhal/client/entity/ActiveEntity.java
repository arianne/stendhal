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

import games.stendhal.client.GameObjects;
import games.stendhal.common.Direction;
import marauroa.common.game.RPObject;

/**
 * An entity that has movement and direction.
 */
public abstract class ActiveEntity extends Entity {
	/**
	 * Direction property.
	 */
	public static final Property PROP_DIRECTION = new Property();

	/**
	 * Speed property.
	 */
	public static final Property PROP_SPEED = new Property();

	/**
	 * The current [facing] direction.
	 */
	private Direction direction;

	/** The current speed of this entity horizontally (tiles?/sec). */
	private double dx;

	/** The current speed of this entity vertically (tiles?/sec) . */
	private double dy;

	/**
	 * Create an active (moving) entity.
	 */
	ActiveEntity() {
		direction = Direction.DOWN;
		dx = 0.0;
		dy = 0.0;
	}

	//
	// ActiveEntity
	//

	/**
	 * Get the direction.
	 * 
	 * @return The direction.
	 */
	public Direction getDirection() {
		return direction;
	}

	/**
	 * The entity has stopped motion.
	 */
	protected void onStop() {
	}

	/**
	 * Set the direction.
	 * 
	 * @param direction
	 *            The direction.
	 */
	protected void setDirection(final Direction direction) {
		this.direction = direction;
	}

	/**
	 * Determine if this entity is not moving.
	 * 
	 * @return <code>true</code> if not moving.
	 */
	public boolean stopped() {
		return (dx == 0.0) && (dy == 0.0);
	}

	/**
	 * Compares to floating point values.
	 * 
	 * @param d1
	 *            first value
	 * @param d2
	 *            second value
	 * @param diff
	 *            acceptable diff
	 * @return true if they are within diff
	 */
	private static boolean compareDouble(final double d1, final double d2,
			final double diff) {
		return Math.abs(d1 - d2) < diff;
	}

	/**
	 * calculates the movement if the server an client are out of sync. for some
	 * milliseconds. (server turns are not exactly 300 ms) Most times this will
	 * slow down the client movement
	 * 
	 * @param clientPos
	 *            the position the client has calculated
	 * @param serverPos
	 *            the position the server has reported
	 * @param delta
	 *            the movement based on direction
	 * @return the new delta to correct the movement error
	 */
	public static double calcDeltaMovement(final double clientPos,
			final double serverPos, final double delta) {
		final double moveErr = clientPos - serverPos;
		final double moveCorrection = (delta - moveErr) / delta;
		return (delta + delta * moveCorrection) / 2;
	}

	// When rpentity moves, it will be called with the data.
	protected void onMove(final int x, final int y, final Direction direction,
			final double speed) {

		double oldx = this.x;
		double oldy = this.y;
		this.dx = direction.getdx() * speed;
		this.dy = direction.getdy() * speed;

		if ((Direction.LEFT.equals(direction))
				|| (Direction.RIGHT.equals(direction))) {
			this.y = y;
			if (compareDouble(this.x, x, 1.0)) {
				// make the movement look more nicely: + this.dx * 0.1
				this.dx = calcDeltaMovement(this.x + this.dx * 0.1, x,
						direction.getdx())
						* speed;
			} else {
				this.x = x;
			}
			this.dy = 0;
		} else if ((Direction.UP.equals(direction))
				|| (Direction.DOWN.equals(direction))) {
			this.x = x;
			this.dx = 0;
			if (compareDouble(this.y, y, 1.0)) {
				// make the movement look more nicely: + this.dy * 0.1
				this.dy = calcDeltaMovement(this.y + this.dy * 0.1, y,
						direction.getdy())
						* speed;
			} else {
				this.y = y;
			}
		} else {
			// placing entities
			this.x = x;
			this.y = y;
		}

		// Call onPosition only if the entity actually moved
		if (!compareDouble(this.x, oldx, 0.001) || !compareDouble(this.y, oldy, 0.001)) {
			onPosition(x, y);
		}
	}

	//
	// Entity
	//

	/**
	 * Initialize this entity for an object.
	 * 
	 * @param base
	 *            The object.
	 * 
	 * @see #release()
	 */
	@Override
	public void initialize(final RPObject base) {
		double speed;

		super.initialize(base);

		if (base.has("dir")) {
			setDirection(Direction.build(base.getInt("dir")));
		}

		if (base.has("speed")) {
			speed = base.getDouble("speed");
		} else {
			speed = 0.0;
		}

		dx = direction.getdx() * speed;
		dy = direction.getdy() * speed;
	}

	/**
	 * Update cycle.
	 * 
	 * @param delta
	 *            The time (in ms) since last call.
	 */
	@Override
	public void update(final int delta) {
		super.update(delta);

		if (!stopped()) {
			final double step = (delta / 300.0);

			final double oldX = x;
			final double oldY = y;

			// update the location of the entity based on speeds
			x += (dx * step);
			y += (dy * step);

			if (GameObjects.getInstance().collides(this)) {
				x = oldX;
				y = oldY;
			} else {
				onPosition(x, y);
			}
		}
	}

	/**
	 * Process attribute changes that may affect positioning. This is needed
	 * because different entities may want to process coordinate changes more
	 * gracefully.
	 * 
	 * @param base
	 *            The previous values.
	 * @param diff
	 *            The changes.
	 */
	@Override
	protected void processPositioning(final RPObject base, final RPObject diff) {
		// Real movement case
		final int oldx = base.getInt("x");
		final int oldy = base.getInt("y");

		int newX = oldx;
		int newY = oldy;

		if (diff.has("x")) {
			newX = diff.getInt("x");
		}
		if (diff.has("y")) {
			newY = diff.getInt("y");
		}

		Direction temp_direction;

		if (diff.has("dir")) {
			temp_direction = Direction.build(diff.getInt("dir"));
			setDirection(temp_direction);
			fireChange(PROP_DIRECTION);
		} else if (base.has("dir")) {
			temp_direction = Direction.build(base.getInt("dir"));
			setDirection(temp_direction);
		} else {
			temp_direction = Direction.STOP;
		}

		double speed;

		if (diff.has("speed")) {
			speed = diff.getDouble("speed");
			fireChange(PROP_SPEED);
		} else if (base.has("speed")) {
			speed = base.getDouble("speed");
		} else {
			speed = 0;
		}

		onMove(newX, newY, temp_direction, speed);

		if ((Direction.STOP.equals(temp_direction)) || (speed == 0)) {
			dx = 0.0;
			dy = 0.0;

			x = newX;
			y = newY;

			onStop();
		}

		/*
		 * Change in position?
		 */
		if ((oldx != newX) && (oldy != newY)) {
			onPosition(newX, newY);
		}
	}
}
