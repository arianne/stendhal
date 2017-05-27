/*
 * @(#) src/games/stendhal/server/entity/area/OccupantArea.java
 *
 * $Id$
 */

package games.stendhal.server.entity.mapstuff.area;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.MovementListener;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.ActiveEntity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.IRPZone;

/**
 * An base area that performs actions on RPEntity's that are entering, leaving,
 * moving in, or standing in it's space.
 */
public class OccupantArea extends AreaEntity implements MovementListener,
		TurnListener {

	/**
	 * How often an action is done while stationary (in turns).
	 */
	protected int interval;

	/**
	 * Applies only to players.
	 */
	protected boolean playersOnly;

	/**
	 * A list of entities [potentially] occupying this area.
	 */
	protected List<RPEntity.ID> targets;

	/**
	 * Create an occupant area.
	 *
	 * @param width
	 *            Width of this area
	 * @param height
	 *            Height of this area
	 * @param interval
	 *            Standing action interval.
	 */
	public OccupantArea(final int width, final int height, final int interval) {
		super(width, height);

		this.interval = interval;

		playersOnly = false;
		targets = new LinkedList<RPEntity.ID>();
	}

	//
	// OccupantArea
	//

	/**
	 * Add an entity to the target list.
	 *
	 * @param entity
	 *            The RPEntity to add.
	 */
	protected void addTarget(final RPEntity entity) {
		targets.add(entity.getID());

		if (targets.size() == 1) {
			SingletonRepository.getTurnNotifier().notifyInTurns(interval, this);
		}
	}

	/**
	 * Check if an entity is an [acknowledged] occupant of this area.
	 * @param entity to be tested
	 * @return true if is occupant
	 *
	 *
	 */
	public boolean isOccupant(final RPEntity entity) {
		return targets.contains(entity.getID());
	}

	/**
	 * An entity has entered the area. This should not apply any actions that
	 * <code>handleMovement()</code> does.
	 *
	 * @param entity
	 *            The RPEntity that was added.
	 *
	 * @return <code>false</code> if this entity should not be processed,
	 *         <code>true</code> otherwise.
	 */
	protected boolean handleAdded(final RPEntity entity) {
		return true;
	}

	/**
	 * Apply actions done at regular intervals.
	 *
	 * @param entity
	 *            The RPEntity occupant.
	 *
	 * @return <code>false</code> if this entity should be removed from
	 *         further processing, <code>true</code> otherwise.
	 */
	protected boolean handleInterval(final RPEntity entity) {
		return true;
	}

	/**
	 * Apply actions done while moving.
	 *
	 * @param entity
	 *            The RPEntity that moved.
	 *
	 * @return <code>false</code> if this entity should be removed from
	 *         further processing, <code>true</code> otherwise.
	 */
	protected boolean handleMovement(final RPEntity entity) {
		return true;
	}

	/**
	 * An entity has left the area. This should not apply any actions that
	 * <code>handleMovement()</code> does.
	 *
	 * @param entity
	 *            The RPEntity that was added.
	 */
	protected void handleRemoved(final RPEntity entity) {
		// can be implemented by sub classes.
	}

	/**
	 * Remove an entity from the target list.
	 *
	 * @param entity
	 *            The RPEntity to remove.
	 */
	protected void removeTarget(final RPEntity entity) {
		targets.remove(entity.getID());

		if (targets.isEmpty()) {
			SingletonRepository.getTurnNotifier().dontNotify(this);
		}
	}

	/**
	 * Set whether only players get affected.
	 *
	 * @param playersOnly
	 *            Whether to only affect players.
	 */
	public void setPlayersOnly(final boolean playersOnly) {
		this.playersOnly = playersOnly;
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
		StendhalRPZone zone;

		/*
		 * Reregister incase coordinates/size changed (could be smarter)
		 */
		zone = getZone();

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
		 * Ignore non-RPEntity's
		 */
		if (!(entity instanceof RPEntity)) {
			return;
		}

		final RPEntity rpentity = (RPEntity) entity;

		/*
		 * Only effect players?
		 */
		if (playersOnly && !(rpentity instanceof Player)) {
			return;
		}

		if (handleAdded(rpentity)) {
			handleMovement(rpentity);
			addTarget(rpentity);
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
		 * Ignore non-RPEntity's
		 */
		if (!(entity instanceof RPEntity)) {
			return;
		}

		final RPEntity rpentity = (RPEntity) entity;

		/*
		 * Only effect players?
		 */
		if (playersOnly && !(rpentity instanceof Player)) {
			return;
		}

		if (targets.contains(rpentity.getID())) {
			handleMovement(rpentity);
			removeTarget(rpentity);
			handleRemoved(rpentity);
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
		/*
		 * Ignore non-RPEntity's
		 */
		if (!(entity instanceof RPEntity)) {
			return;
		}

		final RPEntity rpentity = (RPEntity) entity;

		if (targets.contains(rpentity.getID())) {
			handleMovement(rpentity);
		}
	}

	//
	// TurnListener
	//

	/**
	 * This method is called when the turn number is reached.
	 *
	 * @param currentTurn
	 *            Current turn number.
	 */
	@Override
	public void onTurnReached(final int currentTurn) {
		IRPZone zone;
		Rectangle2D area;

		zone = getZone();
		area = getArea();

		/*
		 * Perform action on entities still in the area. Remove those that have
		 * gone missing.
		 */
		final Iterator<RPEntity.ID> iter = targets.iterator();

		while (iter.hasNext()) {
			final RPEntity.ID id = iter.next();

			if (zone.has(id)) {
				final RPEntity entity = (RPEntity) zone.get(id);

				if (area.intersects(entity.getArea())) {
					if (!handleInterval(entity)) {
						handleRemoved(entity);
						iter.remove();
					}
				} else {
					handleRemoved(entity);
					iter.remove();
				}
			} else {
				iter.remove();
			}
		}

		if (!targets.isEmpty()) {
			SingletonRepository.getTurnNotifier().notifyInTurns(interval, this);
		}
	}

	@Override
	public void beforeMove(ActiveEntity entity, StendhalRPZone zone, int oldX,
			int oldY, int newX, int newY) {
		// nothing to do before a move
	}
}
