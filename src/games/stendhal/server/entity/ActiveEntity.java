/*
 * @(#) src/games/stendhal/server/entity/ActiveEntity.java
 *
 * $Id$
 */

package games.stendhal.server.entity;

import static games.stendhal.common.constants.Actions.MOVE_CONTINUOUS;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import org.apache.log4j.Logger;

import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.entity.mapstuff.portal.Portal;
import marauroa.common.game.Definition;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;

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
	 * The amount of uncommitted tile movement.
	 */
	private double movementOffset;

	private int stepsTaken;

	/** Allows entity to walk through collision areas */

	/**
	 * Create an active entity.
	 */
	public ActiveEntity() {
		direction = Direction.STOP;
		speed = 0.0;
		movementOffset = 0.0;
		stepsTaken = 0;
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
		stepsTaken = 0;

		update();
	}

	protected void move(final int x, final int y, final int nx, final int ny) {
		setPosition(nx, ny);
		notifyWorldAboutChanges();
	}

	protected boolean handlePortal(final Portal portal) {

		return false;
	}

	protected void handleLeaveZone(final int nx, final int ny) {
		logger.debug("Leaving zone from (" + getX() + "," + getY() + ") to ("
				+ nx + "," + ny + ")");
		StendhalRPAction.decideChangeZone(this, nx, ny);

		/* Allow player to continue movement after teleport after map change
		 * without the need to release and press direction again.
		 */
		if (!has(MOVE_CONTINUOUS)) {
			stop();
		}

		notifyWorldAboutChanges();
	}

	/**
	 * Define the RPClass.
	 *
	 * @return The configured RPClass.
	 */
	private static RPClass createRPClass() {
		final RPClass rpclass = new RPClass("active_entity");

		rpclass.isA("entity");
		rpclass.addAttribute("dir", Type.BYTE, Definition.VOLATILE);
		rpclass.addAttribute("speed", Type.FLOAT, Definition.VOLATILE);

		return rpclass;
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
	 * Determine if zone changes are currently allowed via normal means
	 * (non-portal teleportation doesn't count).
	 *
	 * @return <code>true</code> if the entity can change zones.
	 */
	protected boolean isZoneChangeAllowed() {
		return false;
	}

	//
	// Entity
	//

	/**
	 * Called when this object is added to a zone.
	 *
	 * @param zone
	 *            The zone this was added to.
	 */
	@Override
	public void onAdded(final StendhalRPZone zone) {
		super.onAdded(zone);

		zone.notifyEntered(this, getX(), getY());
	}

	/**
	 * Called when this object is removed from a zone.
	 *
	 * @param zone
	 *            The zone this was removed from.
	 */
	@Override
	public void onRemoved(final StendhalRPZone zone) {
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

	/**
	 * Checks whether an entity is a ghost (non physically interactive).
	 *
	 * @return <code>true</code> if in ghost mode.
	 */
	public boolean isGhost() {
		// 'ghostmode' attribute is at player level
		return false;
	}

	/**
	 * Get the resistance this has on other entities (0-100).
	 *
	 * @return The amount of resistance, or 0 if in ghostmode.
	 */
	@Override
	public int getResistance() {
		if (isGhost()) {
			return 0;
		}
		return super.getResistance();
	}


/* XXX --- DIRECTION --- XXX */


	/**
	 * Face toward a specified point on the map.
	 *
	 * @param x
	 * 		Horizontal coordinate of position
	 * @param y
	 * 		Vertical coordinate of position
	 */
	public final void faceto(final int x, final int y) {
		final int rndx = x - getX();
		final int rndy = y - getY();

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

	/**
	 * Face toward an entity.
	 *
	 * @param entity
	 * 		The entity to face toward
	 */
	public final void faceToward(final Entity entity) {
		setDirection(getDirectionToward(entity));
	}

	/**
	 * Get the current facing direction.
	 *
	 * @return
	 * 		The facing direction
	 */
	public Direction getDirection() {
		return direction;
	}

	/**
	 * Get the direction toward an entity.
	 *
	 * @param entity
	 * 		The target entity
	 *
	 * @return
	 * 		A facing direction
	 */
	public final Direction getDirectionToward(final Entity entity) {
		return getDirectionToward(entity.getArea());
	}

	public final Direction getDirectionToward(final Rectangle2D area) {
		return Direction.getAreaDirectionTowardsArea(getArea(), area);
	}

	/**
	 * Determine if this entity is facing toward another entity.
	 *
	 * @param entity
	 * 		The target entity
	 *
	 * @return
	 * 		<code>true</code> if facing other entity
	 */
	public boolean isFacingToward(final Entity entity) {
		return direction.equals(getDirectionToward(entity));
	}

	/**
	 * Set the facing direction.
	 *
	 * @param dir
	 * 		Direction to face toward
	 */
	public void setDirection(final Direction dir) {
		if (dir == this.direction) {
			return;
		}

		this.direction = dir;
		put("dir", direction.get());
	}


/* XXX --- MOVEMENT --- XXX */

	/**
	 * Apply movement and process it's reactions.
	 */
	public void applyMovement() {
		// even if we could we would not move;
		if (speed == 0) {
			stepsTaken = 0;
			return;
		}
		/* XXX: Can this.stopped() be called here instead and set direction
		 *      to Direction.DOWN at construction? Issue found with WalkAction:
		 *      When player logs in setSpeed() will not cause movement as
		 *      direction is set to STOP. WalkAction bypasses this by setting
		 *      default to DOWN if direction is STOP or null.
		 */
		if (direction == Direction.STOP) {
			stepsTaken = 0;
			return;
		}


		final int x = getX();
		final int y = getY();
		final int nx = x + direction.getdx();
		final int ny = y + direction.getdy();

		final StendhalRPZone zone = getZone();

		zone.notifyBeforeMovement(this, x, y, nx, ny);

		if (!ignoresCollision()) {
			if (zone.simpleCollides(this, nx, ny, this.getWidth(), this.getHeight())) {
				handleSimpleCollision(nx, ny);
				return;
			}
		}
		final Portal p = zone.getPortal(nx, ny);
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

		final boolean collision = zone.collidesObjects(this, this.getArea(nx, ny));

		if (collision) {
			/* Collision */
			if (logger.isDebugEnabled()) {
				logger.debug("Collision at (" + nx + "," + ny + ")");
			}
			handleObjectCollision();
		} else {
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
			stepsTaken += 1;
		}
	}

	/**
	 * Get the current speed.
	 *
	 * @return
	 * 		The current speed, or <code>0.0</code> if stopped.
	 */
	public double getSpeed() {
		return speed;
	}

	/**
	 * Retrieves the amount of steps the entity has taken during the current
	 * session.
	 *
	 * @return
	 * 		Steps taken
	 */
	public int getStepsTaken() {
	    return stepsTaken;
	}

	/**
	 * Determine if this entity has move at least a whole tile.
	 *
	 * @return
	 * 		<code>true</code> if moved a whole tile
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
	 * Notification of intra-zone position change.
	 *
	 * @param oldX
	 * 		The old X coordinate.
	 * @param oldY
	 * 		The old Y coordinate.
	 * @param newX
	 * 		The new X coordinate.
	 * @param newY
	 * 		The new Y coordinate.
	 */
	@Override
	protected void onMoved(final int oldX, final int oldY, final int newX, final int newY) {
		getZone().notifyMovement(this, oldX, oldY, newX, newY);
	}

	/**
	 * Set the movement speed.
	 *
	 * @param speed
	 * 		New speed.
	 */
	public void setSpeed(final double speed) {
		if (speed == this.speed) {
			return;
		}

		this.speed = speed;
		put("speed", speed);
		notifyWorldAboutChanges();
	}

	/**
	 * Stops entity's movement.
	 */
	public void stop() {
		setSpeed(0.0);
		movementOffset = 0.0;
	}

	/**
	 * Checks if the entity is not moving.
	 *
	 * @return
	 * 		<b>true</b> if stopped, <b>false</b> if moving
	 */
	@Override
	public boolean stopped() {
		return (speed == 0.0);
	}


/* XXX --- COLLISION --- XXX */

	protected void handleObjectCollision() {
		// implemented by sub classes
	}

	/**
	 * a simple collision is from tiled collision layer or the edge of the map.
	 *
	 * @param ny
	 * @param nx
	 *
	 */
	protected void handleSimpleCollision(final int nx, final int ny) {
		if (isZoneChangeAllowed()) {
			if (getZone().leavesZone(this, nx, ny)) {
				handleLeaveZone(nx, ny);
				return;
			}
		}
		if (isGhost()) {
			move(getX(), getY(), nx, ny);
		}
	}

	/**
	 * Tells if entity can pass through collision tiles
	 *
	 * @return ignoreCollision
	 */
	public boolean ignoresCollision() {
		return has("ignore_collision");
	}

	/**
	 * Set entity to ignore collision tiles
	 *
	 * @param ignore
	 */
	public void setIgnoresCollision(boolean ignore) {
		if (ignore) {
			put("ignore_collision", "");
		} else {
			remove("ignore_collision");
		}
	}

	/**
	 * Predict if entity can move to a position.
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean canMoveTo(final int x, final int y) {
		if (ignoresCollision()) {
			return true;
		}

		final StendhalRPZone zone = getZone();
		final Rectangle2D area = new Rectangle.Double();
		area.setRect(x, y, getWidth(), getHeight());

		final boolean collidesObjects = zone.collidesObjects(this, area) && getResistance() > 95;

		return !zone.collides(area) && !collidesObjects;
	}

	/**
	 * Predict if entity can move to a position.
	 *
	 * @param pos
	 * @return
	 */
	public boolean canMoveTo(final Point pos) {
		if (pos == null) {
			return false;
		}

		return canMoveTo(pos.x, pos.y);
	}
}
