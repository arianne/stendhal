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
import games.stendhal.server.entity.mapstuff.portal.Portal;

import java.awt.geom.Rectangle2D;


import org.apache.log4j.Logger;
import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.Definition.Type;

/**
 * An entity that has speed and direction.
 */
public abstract class ActiveEntity extends Entity {
	/**
	 * The logger.
	 */
	private static final Logger logger = Logger.getLogger(ActiveEntity.class);

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
		//even if we could we would not move;
		if (speed == 0) {
			return;
		}
		if (direction == Direction.STOP) {
			return;
		}

		setCollides(false);

		int x = getX();
		int y = getY();
		int nx = x + direction.getdx();
		int ny = y + direction.getdy();


		StendhalRPZone zone = getZone();
		if (zone.simpleCollides(this, nx, ny)) {
			handleSimpleCollision(nx, ny);
			return;
		}
		Portal p = zone.getPortal(nx, ny);
		if (p != null) {
		 if (handlePortal(p)) {
			 return;
		 }
		}


		if (isGhost()) {
			if (isMoveCompleted()) {
			move(x, y, nx, ny);
			return;
			}
		}
		boolean collision = zone.collidesObjects(this, this.getArea(nx, ny));

		if (!collision) {
			if (!isMoveCompleted()) {
				if (logger.isDebugEnabled()) {
					logger.debug(get("type") + ") move not completed");
				}
				return;
			}

			if (logger.isDebugEnabled()) {
				logger.debug("Moving from (" + x + "," + y + ") to (" + nx
						+ "," + ny + ")");
			}

			move(x, y, nx, ny);
		} else {
			/* Collision */
			if (logger.isDebugEnabled()) {
				logger.debug("Collision at (" + nx + "," + ny + ")");
			}
			handleObjectCollision();
		}

		notifyWorldAboutChanges();
	}

	protected void handleObjectCollision() {


		setCollides(true);
	}

	private void move(int x, int y, int nx, int ny) {
		setPosition(nx, ny);
	}


	private boolean handlePortal(Portal portal) {
		if (isZoneChangeAllowed()) {
			logger.debug("Using portal " + portal);
		 return portal.onUsed((RPEntity) this);
		}
		return false;
	}

	/**
	 * a simple collision is from tiled collision layer
	 * or the edge of the map.
	 * @param ny
	 * @param nx
	 *
	 */
	protected void handleSimpleCollision(int nx, int ny) {
		if (isZoneChangeAllowed()) {
			if (getZone().leavesZone(this, nx, ny)) {
				handleLeaveZone(nx, ny);
				return;
			}
		}
		if (isGhost()) {
		    move(getX(), getY(), nx, ny);
		} else {
		    setCollides(true);
		}
	}

	protected void handleLeaveZone(int nx, int ny) {
		logger.debug("Leaving zone from (" + getX() + "," + getY() + ") to ("
				+ nx + "," + ny + ")");
		StendhalRPAction.decideChangeZone(this, nx, ny);
		stop();
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
	public final void faceToward(final Entity entity) {
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
	public final Direction getDirectionToward(final Entity entity) {
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
	@Override
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

	/**
	 * Called when this object is added to a zone.
	 *
	 * @param zone
	 *		The zone this was added to.
	 */
	@Override
	public void onAdded(StendhalRPZone zone) {
		super.onAdded(zone);

		zone.notifyEntered(this, getX(), getY());
	}

	/**
	 * Called when this object is removed from a zone.
	 *
	 * @param zone
	 *		The zone this was removed from.
	 */
	@Override
	public void onRemoved(StendhalRPZone zone) {
		zone.notifyExited(this, getX(), getY());

		super.onRemoved(zone);
	}


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

	public final void faceto(int x, int y) {
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
