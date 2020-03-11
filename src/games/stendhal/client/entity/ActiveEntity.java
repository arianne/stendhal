/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
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
	/** Epsilon value used for coordinate change checks. */
	private static final double EPSILON = 0.001;
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

	/** If <code>true</code>, this entity is not blocked by FlyOverArea */
	private boolean flying = false;

	/**
	 * Create an active (moving) entity.
	 */
	ActiveEntity() {
		direction = Direction.DOWN;
		setSpeed(0.0, 0.0);
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
	 * Set the direction.
	 *
	 * @param direction
	 *            The direction.
	 */
	void setDirection(final Direction direction) {
		boolean changed = this.direction != direction;
		this.direction = direction;
		if (changed) {
			/*
			 * Movement prediction can result in the client entities (User)
			 * sometimes having the wrong direction, so we do the changed check
			 * here instead of firing the property only when the RPObject
			 * changes warrant so.
			 */
			fireChange(PROP_DIRECTION);
		}
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
	private static double calcDeltaMovement(final double clientPos,
			final double serverPos, final double delta) {
		final double moveErr = clientPos - serverPos;
		final double moveCorrection = (delta - moveErr) / delta;
		return (delta + delta * moveCorrection) / 2;
	}

	/**
	 * When entity moves, it will be called with the data.
	 *
	 * @param x new x coordinate
	 * @param y new y coordinate
	 * @param direction new direction
	 * @param speed new speed
	 */
	private void onMove(final int x, final int y, final Direction direction,
			final double speed) {

		double oldx = this.x;
		double oldy = this.y;
		setSpeed(direction.getdx() * speed, direction.getdy() * speed);

		if ((Direction.LEFT == direction)
				|| (Direction.RIGHT == direction)) {
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
		} else if ((Direction.UP == direction)
				|| (Direction.DOWN == direction)) {
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

		// Call onPosition only if the entity actually moved. Also always call
		// on partial coordinates - those are always predicted rather than real
		// and thus should always be a result of prediction. However, the
		// client collision detection does not always agree with that of the
		// server, so relying on just the coordinate change checks can miss
		// entities stopping when they collide with each other.
		if (!compareDouble(this.x, oldx, EPSILON) || !compareDouble(this.y, oldy, EPSILON)
				|| !compareDouble(oldx, (int) oldx, EPSILON)
				|| !compareDouble(oldy, (int) oldy, EPSILON)) {
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

		if (base.has("flying")) {
			flying = true;
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

		Direction tempDirection;

		if (diff.has("dir")) {
			tempDirection = Direction.build(diff.getInt("dir"));
			setDirection(tempDirection);
		} else if (base.has("dir")) {
			tempDirection = Direction.build(base.getInt("dir"));
			setDirection(tempDirection);
		} else {
			tempDirection = Direction.STOP;
		}

		double speed;

		/*
		 * Speed change must be fired only after the new speed has been stored
		 * (done in onMove())
		 */
		boolean speedChanged = false;

		if (diff.has("speed")) {
			speed = diff.getDouble("speed");
			speedChanged = true;
		} else if (base.has("speed")) {
			speed = base.getDouble("speed");
		} else {
			speed = 0;
		}

		onMove(newX, newY, tempDirection, speed);

		if (speedChanged) {
			fireChange(PROP_SPEED);
		}

		boolean positionChanged = false;
		if ((Direction.STOP == tempDirection) || (speed == 0)) {
			setSpeed(0.0, 0.0);

			/*
			 * Try to ensure relocation in the case the client and server were
			 * in disagreement about the position at the moment of stopping.
			 */
			if (!(compareDouble(y, newY, EPSILON) && compareDouble(x, newX, EPSILON))) {
				positionChanged = true;
			}

			// Store the new position before signaling it with onPosition().
			x = newX;
			y = newY;
		}

		/*
		 * Change in position?
		 */
		if (positionChanged || ((oldx != newX) && (oldy != newY))) {
			onPosition(newX, newY);
		}
	}

	/**
	 * Set the current client side speed. This is used by the movement
	 * prediction at key press. <b>Do not call unless you know what you're
	 * doing. </b>
	 *
	 * @param dx horizontal speed
	 * @param dy vertical speed
	 */
	final void setSpeed(double dx, double dy) {
		this.dx = dx;
		this.dy = dy;
	}

	/**
	 * Checks if the entity is a flying entity.
	 * @return
	 */
	public boolean isFlying() {
		return flying;
	}
}
