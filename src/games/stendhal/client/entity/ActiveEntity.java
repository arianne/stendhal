/*
 * @(#) src/games/stendhal/client/entity/ActiveEntity.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import games.stendhal.client.GameObjects;
import games.stendhal.common.Direction;
import marauroa.common.game.RPObject;

/**
 * An entity that has movement.
 */
public abstract class ActiveEntity extends AnimatedStateEntity {
	public static final String	STATE_DOWN	= "move_down";
	public static final String	STATE_UP	= "move_up";
	public static final String	STATE_LEFT	= "move_left";
	public static final String	STATE_RIGHT	= "move_right";

	/**
	 * The current [facing] direction.
	 */
	private Direction	direction;

	/** The current speed of this entity horizontally (tiles?/sec) */
	protected double	dx;

	/** The current speed of this entity vertically (tiles?/sec) */
	protected double	dy;


	/**
	 * Create an active (moving) entity.
	 */
	ActiveEntity()  {
		direction = Direction.DOWN;
		animation = STATE_DOWN;
		dx = 0.0;
		dy = 0.0;
	}


	//
	// ActiveEntity
	//

	/**
	 * Get the direction.
	 *
	 * @return	The direction.
	 */
	public Direction getDirection() {
		return direction;
	}


	/**
	 * Get the appropriete named state for a direction.
	 *
	 * @param	direction	The direction.
	 *
	 * @return	A named state.
	 */
	protected String getDirectionState(final Direction direction) {
		switch (direction) {
			case LEFT:
				return STATE_LEFT;

			case RIGHT:
				return STATE_RIGHT;

			case UP:
				return STATE_UP;

			case DOWN:
				return STATE_DOWN;

			default:
				return STATE_DOWN;
		}
	}

	/**
	 * The entity has started motion.
	 *
	 *
	 */
	protected void onMotion(double dx, double dy) {
	}


	/**
	 * The entity has stopped motion.
	 */
	protected void onStop() {
	}


	/**
	 * Set the direction.
	 *
	 *
	 */
	protected void setDirection(Direction direction) {
		this.direction = direction;

		animation = getDirectionState(direction);
	}


	public boolean stopped() {
		return (dx == 0.0) && (dy == 0.0);
	}


	/**
	 * compares to floating point values
	 * 
	 * @param d1
	 *            first value
	 * @param d2
	 *            second value
	 * @param diff
	 *            acceptable diff
	 * @return true if they are within diff
	 */
	private static boolean compareDouble(final double d1, final double d2, final double diff) {
		return Math.abs(d1 - d2) < diff;
	}

	/**
	 * calculates the movement if the server an client are out of sync. for some
	 * miliseconds. (server turns are not exactly 300 ms) Most times this will
	 * slow down the client movement
	 * 
	 * @param clientPos
	 *            the postion the client has calculated
	 * @param serverPos
	 *            the postion the server has reported
	 * @param delta
	 *            the movement based on direction
	 * @return the new delta to correct the movement error
	 */
	public static double calcDeltaMovement(final double clientPos, final double serverPos, final double delta) {
		double moveErr = clientPos - serverPos;
		double moveCorrection = (delta - moveErr) / delta;
		return (delta + delta * moveCorrection) / 2;
	}

	// When rpentity moves, it will be called with the data.
	protected void onMove(final int x,final  int y,final  Direction direction,final  double speed) {

		this.dx = direction.getdx() * speed;
		this.dy = direction.getdy() * speed;
		

		if ((Direction.LEFT.equals(direction)) || (Direction.RIGHT.equals(direction))) {
			this.y = y;
			if (compareDouble(this.x, x, 1.0)) {
				// make the movement look more nicely: + this.dx * 0.1
				this.dx = calcDeltaMovement(this.x + this.dx * 0.1, x, direction.getdx()) * speed;
			} else {
				this.x = x;
			}
			this.dy = 0;
		} else if ((Direction.UP.equals(direction)) || (Direction.DOWN.equals(direction))) {
			this.x = x;
			this.dx = 0;
			if (compareDouble(this.y, y, 1.0)) {
				// make the movement look more nicely: + this.dy * 0.1
				this.dy = calcDeltaMovement(this.y + this.dy * 0.1, y, direction.getdy()) * speed;
			} else {
				this.y = y;
			}
		} else {
			// placing entities
			this.x = x;
			this.y = y;
		}

		onPosition(x, y);
		onMotion(dx, dy);
	}


	//
	// Entity
	//

	/**
	 * Initialize this entity for an object.
	 *
	 * @param	object		The object.
	 *
	 * @see-also	#release()
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

		/*
		 * KLUDGE - Ensure that view animation is set right
		 */
		changed();
	}


	/**
	 * Update cycle.
	 *
	 * @param	delta		The time (in ms) since last call.
	 */
	@Override
	public void update(final long delta) {
		if(!stopped()) {
			double step = (delta / 300.0);

			double oldX = x;
			double oldY = y;

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
	 * Process attribute changes that may affect positioning. This is
	 * needed because different entities may want to process coordinate
	 * changes more gracefully.
	 *
	 * @param	base		The previous values.
	 * @param	diff		The changes.
	 */
	protected void processPositioning(final RPObject base, final RPObject diff) {
		// Real movement case
		int oldx = base.getInt("x");
		int oldy = base.getInt("y");

		int newX=oldx;
		int newY=oldy;

		if (diff.has("x")) {
			newX = diff.getInt("x");
		}
		if (diff.has("y")) {
			newY = diff.getInt("y");
		}

		Direction direction;

		if (diff.has("dir")) {
			direction = Direction.build(diff.getInt("dir"));
			setDirection(direction);
			changed();
		} else if (base.has("dir")) {
			direction = Direction.build(base.getInt("dir"));
			setDirection(direction);
		} else {
			direction = Direction.STOP;
		}

		double speed;

		if (diff.has("speed")) {
			speed = diff.getDouble("speed");
			changed();
		} else if (base.has("speed")) {
			speed = base.getDouble("speed");
		} else {
			speed = 0;
		}

		onMove(newX, newY, direction, speed);

		if ((Direction.STOP.equals(direction)) || (speed == 0)) {
			dx = 0.0;
			dy = 0.0;

			x = newX;
			y = newY;

			onStop();
		}

		if ((oldx != newX) && (oldy != newY)) {
			onPosition(newX, newY);
		}
	}
}
