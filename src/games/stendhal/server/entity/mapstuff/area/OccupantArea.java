/*
 * @(#) src/games/stendhal/server/entity/area/OccupantArea.java
 *
 * $Id$
 */

package games.stendhal.server.entity.mapstuff.area;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.ActiveEntity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.MovementListener;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
	 * @param name
	 *            The entity name.
	 * @param width
	 *            Width of this area
	 * @param height
	 *            Height of this area
	 * @param interval
	 *            Standing action interval.
	 */
	public OccupantArea(int width, int height, int interval) {
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
	protected void addTarget(RPEntity entity) {
		targets.add(entity.getID());

		if (targets.size() == 1) {
			TurnNotifier.get().notifyInTurns(interval, this);
		}
	}

	/**
	 * Check if an entity is an [acknowledged] occupant of this area.
	 * 
	 * 
	 */
	public boolean isOccupant(RPEntity entity) {
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
	protected boolean handleAdded(RPEntity entity) {
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
	protected boolean handleInterval(RPEntity entity) {
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
	protected boolean handleMovement(RPEntity entity) {
		return true;
	}

	/**
	 * An entity has left the area. This should not apply any actions that
	 * <code>handleMovement()</code> does.
	 * 
	 * @param entity
	 *            The RPEntity that was added.
	 */
	protected void handleRemoved(RPEntity entity) {
		// can be implemented by sub classes.
	}

	/**
	 * Remove an entity from the target list.
	 * 
	 * @param entity
	 *            The RPEntity to remove.
	 */
	protected void removeTarget(RPEntity entity) {
		targets.remove(entity.getID());

		if (targets.isEmpty()) {
			TurnNotifier.get().dontNotify(this);
		}
	}

	/**
	 * Set whether only players get affected.
	 * 
	 * @param playersOnly
	 *            Whether to only affect players.
	 */
	public void setPlayersOnly(boolean playersOnly) {
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
	public void onAdded(StendhalRPZone zone) {
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
	public void onRemoved(StendhalRPZone zone) {
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
	public void onEntered(ActiveEntity entity, StendhalRPZone zone, int newX,
			int newY) {
		/*
		 * Ignore non-RPEntity's
		 */
		if (!(entity instanceof RPEntity)) {
			return;
		}

		RPEntity rpentity = (RPEntity) entity;

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
	public void onExited(ActiveEntity entity, StendhalRPZone zone, int oldX,
			int oldY) {
		/*
		 * Ignore non-RPEntity's
		 */
		if (!(entity instanceof RPEntity)) {
			return;
		}

		RPEntity rpentity = (RPEntity) entity;

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
	public void onMoved(ActiveEntity entity, StendhalRPZone zone, int oldX,
			int oldY, int newX, int newY) {
		/*
		 * Ignore non-RPEntity's
		 */
		if (!(entity instanceof RPEntity)) {
			return;
		}

		RPEntity rpentity = (RPEntity) entity;

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
	public void onTurnReached(int currentTurn) {
		IRPZone zone;
		Rectangle2D area;

		zone = getZone();
		area = getArea();

		/*
		 * Perform action on entities still in the area. Remove those that have
		 * gone missing.
		 */
		Iterator<RPEntity.ID> iter = targets.iterator();

		while (iter.hasNext()) {
			RPEntity.ID id = iter.next();

			if (!zone.has(id)) {
				iter.remove();
			} else {
				RPEntity entity = (RPEntity) zone.get(id);

				if (area.intersects(entity.getArea())) {
					if (!handleInterval(entity)) {
						handleRemoved(entity);
						iter.remove();
					}
				} else {
					handleRemoved(entity);
					iter.remove();
				}
			}
		}

		if (!targets.isEmpty()) {
			TurnNotifier.get().notifyInTurns(interval, this);
		}
	}
}
