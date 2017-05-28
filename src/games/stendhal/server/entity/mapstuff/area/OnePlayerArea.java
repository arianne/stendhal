/*
 * @(#) src/games/stendhal/server/entity/OnePlayerArea.java
 *
 * $Id$
 */

package games.stendhal.server.entity.mapstuff.area;

import java.lang.ref.WeakReference;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.MovementListener;
import games.stendhal.server.entity.ActiveEntity;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;

/**
 * An area that only allows one play at a time to enter.
 *
 */
public class OnePlayerArea extends AreaEntity implements MovementListener {
	/**
	 * The logger instance.
	 */
	private static final Logger LOGGER = Logger.getLogger(OnePlayerArea.class);

	/**
	 * The reference to the entity currently in the area.
	 */
	protected WeakReference<Player> occupantRef;

	/**
	 * Create a one player area.
	 *
	 * @param width
	 *            The area width.
	 * @param height
	 *            The area height.
	 */
	public OnePlayerArea(final int width, final int height) {
		super(width, height);

		hide();
		occupantRef = null;
	}

	//
	// OnePlayerArea
	//

	/**
	 * Clear the occupant.
	 */
	protected void clearOccupant() {
		occupantRef = null;
	}

	/**
	 * Check if an entity is in this area.
	 *
	 * @param entity
	 *            The entity to check.
	 *
	 * @return <code>true</code> if the entity is in this area.
	 *
	 */
	protected boolean contains(final Entity entity) {
		return ((getZone() == entity.getZone()) && getArea().intersects(
				entity.getArea()));
	}

	/**
	 * Get the occupant.
	 *
	 * @return The area occupant, or <code>null</code> in none.
	 */
	protected Player getOccupant() {
		if (occupantRef != null) {
			return occupantRef.get();
		} else {
			return null;
		}
	}

	/**
	 * Set the occupant.
	 *
	 * @param player
	 *            The occupant to set.
	 */
	protected void setOccupant(final Player player) {
		occupantRef = new WeakReference<Player>(player);
	}

	//
	// Entity
	//

	/**
	 * Checks whether players, NPC's, etc. can walk over this entity.
	 *
	 * @param entity
	 *            The entity trying to enter.
	 *
	 * @return <code>true</code> if a Player is given and it is occupied by
	 *         someone else.
	 */
	@Override
	public boolean isObstacle(final Entity entity) {
		/*
		 * Only applies to Players
		 */
		if (!(entity instanceof Player)) {
			return super.isObstacle(entity);
		}
		final Player player = (Player) entity;

		/*
		 * Ghosts shouldn't give away their presence
		 */
		if (player.isGhost()) {
			return false;
		}

		final Player occupant = getOccupant();

		if (occupant != null) {
			/*
			 * Verify the occupant (just incase)
			 */
			if (contains(occupant)) {
				/*
				 * Allow if it's the occupant (quick check).
				 *
				 * But don't block entities that got in the area (somehow) from
				 * leaving.
				 */
				return ((entity != occupant) && !contains(entity));
			}

			LOGGER.warn("Occupant vanished: " + occupant.getName());
			clearOccupant();
		}

		return false;
	}

	/**
	 * Called when this object is added to a zone.
	 *
	 * @param zone
	 *            The zone this was added to.
	 */
	@Override
	public void onAdded(final StendhalRPZone zone) {
		super.onAdded(zone);
		zone.addMovementListener(this);
	}

	/**
	 * Called when this object is being removed from a zone.
	 *
	 * @param zone
	 *            The zone this will be removed from.
	 */
	@Override
	public void onRemoved(final StendhalRPZone zone) {
		zone.removeMovementListener(this);
		super.onRemoved(zone);
	}

	/**
	 * Handle object attribute change(s).
	 */
	@Override
	public void update() {
		/*
		 * Reregister incase coordinates/size changed (could be smarter)
		 */
		final StendhalRPZone zone = getZone();

		if (zone != null) {
			zone.removeMovementListener(this);
		}

		super.update();

		if (zone != null) {
			zone.addMovementListener(this);
		}
	}

	//
	// MovementListener
	//

	/**
	 * Invoked when an entity enters the object area.
	 *
	 * @param entity
	 *            The entity that moved.
	 * @param zone
	 *            The new zone.
	 * @param newX
	 *            The new X coordinate.
	 * @param newY
	 *            The new Y coordinate.
	 */
	@Override
	public void onEntered(final ActiveEntity entity, final StendhalRPZone zone, final int newX,
			final int newY) {
		/*
		 * Just players
		 */
		if (entity instanceof Player) {
			/*
			 * Ghosts don't occupy normal space
			 */
			if (entity.isGhost()) {
				return;
			}

			final Player occupant = getOccupant();

			/*
			 * Check to make sure things aren't buggy
			 */
			if ((occupant != null) && (occupant != entity)) {
				if (occupant.isGhost()) {
					clearOccupant();
				}
				LOGGER.error("Existing occupant: " + occupant.getName(), new Throwable());
			}

			setOccupant((Player) entity);
		}
	}

	/**
	 * Invoked when an entity leaves the object area.
	 *
	 * @param entity
	 *            The entity that entered.
	 * @param zone
	 *            The old zone.
	 * @param oldX
	 *            The old X coordinate.
	 * @param oldY
	 *            The old Y coordinate.
	 *
	 */
	@Override
	public void onExited(final ActiveEntity entity, final StendhalRPZone zone, final int oldX,
			final int oldY) {
		/*
		 * Check against occupant incase something else is existing
		 */
		if (entity == getOccupant()) {
			clearOccupant();
		}
	}

	/**
	 * Invoked when an entity moves while over the object area.
	 *
	 * @param entity
	 *            The entity that left.
	 * @param zone
	 *            The zone.
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
	public void onMoved(final ActiveEntity entity, final StendhalRPZone zone, final int oldX,
			final int oldY, final int newX, final int newY) {
		// does nothing, but is specified in the implemented interface
	}

	@Override
	public void beforeMove(ActiveEntity entity, StendhalRPZone zone, int oldX,
			int oldY, int newX, int newY) {
		// does nothing, but is specified in the implemented interface
	}
}
