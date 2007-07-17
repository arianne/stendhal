/*
 * @(#) src/games/stendhal/server/entity/ActiveEntity.java
 *
 * $Id$
 */

package games.stendhal.server.entity;

//
//

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPAction;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.portal.Portal;

import java.awt.geom.Rectangle2D;

import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.Definition.Type;

/**
 * An entity that hase speed and direction.
 */
public abstract class ActiveEntity extends Entity {
	/**
	 * The logger.
	 */
	private static final Logger logger = Log4J.getLogger(ActiveEntity.class);

	/*
	 * The facing direction
	 */
	private Direction direction;

	/**
	 * The current speed.
	 */
	private double speed;

	/**
	 * The amount of uncommited tile movement.
	 */
	private double movementOffset;

	/**
	 * Create an active entity.
	 */
	public ActiveEntity() {
		direction = Direction.STOP;
		speed = 0.0;
		movementOffset = 0.0;
	}

	/**
	 * Create an active entity.
	 *
	 * @param object
	 *            The source object.
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
	 * Apply movement and process it's reactions.
	 */
	public void applyMovement() {
		if (stopped()) {
			return;
		}

		setCollides(false);

		int x = getX();
		int y = getY();

		Direction dir = getDirection();
		int dx = dir.getdx();
		int dy = dir.getdy();

		int nx = x + dx;
		int ny = y + dy;

		StendhalRPZone zone = getZone();
		boolean collision = zone.collides(this, nx, ny);

		if (collision) {
			// TODO: Break the Player dependency
			// For now isZoneChangeAllowed() should return false
			// with non-Player entity's.
			if (isZoneChangeAllowed()) {
				if (zone.leavesZone(this, nx, ny)) {
					logger.debug("Leaving zone from (" + x + "," + y + ") to ("
							+ nx + "," + ny + ")");
					StendhalRPAction.decideChangeZone(this, nx, ny);
					stop();
					notifyWorldAboutChanges();
					return;
				}

				// TODO: If Player becomes 1x1, remove "+ 1"
				Portal portal = zone.getPortal(nx, ny + 1);

				if (portal != null) {
					logger.debug("Using portal " + portal);
					// TODO: Generalize parameter type
					portal.onUsed((RPEntity) this);
					return;
				}
			}
		}

		if (!collision || isGhost()) {
			if (!isMoveCompleted()) {
				logger.debug(get("type") + ") move not completed");
				return;
			}

			if (logger.isDebugEnabled()) {
				logger.debug("Moving from (" + x + "," + y + ") to (" + nx
						+ "," + ny + ")");
			}

			set(nx, ny);
			onMoved(x, y, nx, ny);
		} else {
			/* Collision */
			if (logger.isDebugEnabled()) {
				logger.debug("Collision at (" + nx + "," + ny + ")");
			}

			setCollides(true);
		}

		notifyWorldAboutChanges();
	}

	/**
	 * Define the RPClass.
	 *
	 * @return The configured RPClass.
	 */
	private static RPClass createRPClass() {
		RPClass rpclass = new RPClass("active_entity");

		rpclass.isA("entity");
		rpclass.addAttribute("dir", Type.BYTE, Definition.VOLATILE);
		rpclass.addAttribute("speed", Type.FLOAT, Definition.VOLATILE);

		return rpclass;
	}

	/**
	 * Face toward an entity.
	 *
	 * @param entity
	 *            The entity to face toward.
	 */
	final public void faceToward(final Entity entity) {
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
	 * @return The facing direction.
	 */
	public Direction getDirection() {
		return direction;
	}

	/**
	 * Get the direction toward an entity.
	 *
	 * @param entity
	 *            The target entity.
	 *
	 * @return A facing direction.
	 */
	final public Direction getDirectionToward(final Entity entity) {
		return getDirectionToward(entity.getArea());
	}

	final Direction getDirectionToward(Rectangle2D area) {

		double rx = getArea().getCenterX();
		double ry = getArea().getCenterY();
		double x = area.getCenterX();
		double y = area.getCenterY();
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

	//
	// /**
	// * Get the direction toward a point.
	// *
	// * @param x The target X coordinate.
	// * @param y The target Y coordinate.
	// *
	// * @return A facing direction.
	// */
	// Direction getDirectionToward(final double x, final double y) {
	// Rectangle2D area = getArea();
	//
	// double rx = area.getCenterX();
	// double ry = area.getCenterY();
	//
	// if (Math.abs(x - rx) > Math.abs(y - ry)) {
	// if (x - rx > 0) {
	// return Direction.RIGHT;
	// } else {
	// return Direction.LEFT;
	// }
	// } else {
	// if (y - ry > 0) {
	// return Direction.DOWN;
	// } else {
	// return Direction.UP;
	// }
	// }
	// }

	/**
	 * Get the current speed.
	 *
	 * @return The current speed, or <code>0.0</code> if stopped.
	 */
	public double getSpeed() {
		return speed;
	}

	/**
	 * Determine if this entity is facing toward another entity.
	 *
	 * @param entity
	 *            The target entity.
	 *
	 * @return <code>true</code> if facing the other entity.
	 */
	public boolean isFacingToward(final Entity entity) {
		return direction.equals(getDirectionToward(entity));
	}

	/**
	 * Determine if this entity has move at least a whole tile.
	 *
	 * @return <code>true</code> if moved a whole tile.
	 */
	protected boolean isMoveCompleted() {
		movementOffset += getSpeed();

		if (movementOffset >= 1.0) {
			movementOffset -= 1.0;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Determine if zone changes are currently allowed via normal means
	 * (non-portal teleportation doesn't count).
	 *
	 * @return <code>true</code> if the entity can change zones.
	 */
	protected boolean isZoneChangeAllowed() {
		return false;
	}

	/**
	 * Notify of intra-zone movement.
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
	protected void onMoved(int oldX, int oldY, int newX, int newY) {
		getZone().notifyMovement(this, oldX, oldY, newX, newY);
	}

	/**
	 * Set the facing direction.
	 *
	 * @param dir
	 *            The facing direction.
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
	 * @param speed
	 *            The new speed.
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
		movementOffset = 0.0;
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
		return (speed == 0.0);
	}

	final public void faceto(int x, int y) {
		int rndx = x - getX();
		int rndy = y - getY();

		if (Math.abs(rndx) > Math.abs(rndy)) {
			if (rndx < 0.0) {
				setDirection(Direction.LEFT);
			} else {
				setDirection(Direction.RIGHT);
			}
		} else {
			if (rndy < 0.0) {
				setDirection(Direction.UP);
			} else {
				setDirection(Direction.DOWN);
			}
		}
	}
}
