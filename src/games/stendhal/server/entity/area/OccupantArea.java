/*
 * @(#) src/games/stendhal/server/entity/area/OccupantArea.java
 *
 * $Id$
 */

package games.stendhal.server.entity.area;

//
//

import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;

import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.IRPZone;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.PassiveEntity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.MovementListener;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;

/**
 * An base area that performs actions on RPEntity's that are entering, leaving,
 * moving in, or standing in it's space.
 */
public class OccupantArea extends PassiveEntity implements MovementListener, TurnListener {
	/**
	 * The logger instance.
	 */
	private static final Logger logger = Log4J.getLogger(OccupantArea.class);

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
	 * The height.
	 */
	protected int height;

	/**
	 * The width
	 */
	protected int width;


	/**
	 * Create an occupant area.
	 *
	 * @param	type		The entity type.
	 * @param	name		The entity name.
	 * @param	width		Width of this area
	 * @param	height		Height of this area
	 * @param	interval	Standing action interval.
	 */
	public OccupantArea(String type, String name, int width, int height, int interval) throws AttributeNotFoundException {
		put("type", type);
		put("name", name);

		this.height = height;
		this.width = width;
		this.interval = interval;

		put("width", width);
		put("height", height);
		
		playersOnly = false;
		targets = new LinkedList<RPEntity.ID>();
	}


	//
	// OccupantArea
	//

	/**
	 * Add an entity to the target list.
	 *
	 * @param	entity		The RPEntity to add.
	 */
	protected void addTarget(RPEntity entity) {
		targets.add(entity.getID());

		if (targets.size() == 1) {
			TurnNotifier.get().notifyInTurns(interval, this, null);
		}
	}


	/**
	 * Check if an entity is an [acknowledged] occupant of this area.
	 *
	 *
	 */
	public boolean isOccupant(RPEntity entity) {
		return targets.contains(entity);
	}


	/**
	 * An entity has entered the area. This should not apply any actions
	 * that <code>handleMovement()</code> does.
	 *
	 * @param	entity		The RPEntity that was added.
	 *
	 * @return	<code>false</code> if this entity should not be
	 *		processed, <code>true</code> otherwise.
	 */
	protected boolean handleAdded(RPEntity entity) {
		return true;
	}


	/**
	 * Apply actions done at regular intervals.
	 *
	 * @param	entity		The RPEntity occupant.
	 *
	 * @return	<code>false</code> if this entity should be removed
	 *		from further processing, <code>true</code> otherwise.
	 */
	protected boolean handleInterval(RPEntity entity) {
		return true;
	}


	/**
	 * Apply actions done while moving.
	 *
	 * @param	entity		The RPEntity that moved.
	 *
	 * @return	<code>false</code> if this entity should be removed
	 *		from further processing, <code>true</code> otherwise.
	 */
	protected boolean handleMovement(RPEntity entity) {
		return true;
	}


	/**
	 * An entity has left the area. This should not apply any actions
	 * that <code>handleMovement()</code> does.
	 *
	 * @param	entity		The RPEntity that was added.
	 */
	protected void handleRemoved(RPEntity entity) {
	}


	/**
	 * Remove an entity from the target list.
	 *
	 * @param	entity		The RPEntity to remove.
	 */
	protected void removeTarget(RPEntity entity) {
		targets.remove(entity.getID());

		if (targets.isEmpty()) {
			TurnNotifier.get().dontNotify(this, null);
		}
	}


	/**
	 * Set whether only players get affected.
	 *
	 * @param	playersOnly	Whether to only affect players.
	 */
	public void setPlayersOnly(boolean playersOnly) {
		this.playersOnly = playersOnly;
	}


	//
	// Entity
	//

	/**
	 * Get the area.
	 *
	 * @param	rect		The rectangle to fill in.
	 * @param	x		The X coordinate.
	 * @param	y		The Y coordinate.
	 */
	@Override
	public void getArea(Rectangle2D rect, double x, double y) {
		rect.setRect(x, y, width, height);
	}


	/**
	 * Called when this object is added to a zone.
	 *
	 * @param	zone		The zone this was added to.
	 */
	@Override
	public void onAdded(StendhalRPZone zone) {
		super.onAdded(zone);
		zone.addMovementListener(this);
	}


	/**
	 * Called when this object is being removed from a zone.
	 *
	 * @param	zone		The zone this will be removed from.
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
	public void update() throws AttributeNotFoundException {
		StendhalRPZone zone;

		super.update();

		/*
		 * Reregister incase coordinates/size changed (could be smarter)
		 */
		zone = getZone();
		zone.removeMovementListener(this);

		if(has("height")) {
			height = getInt("height");
		}

		if(has("width")) {
			width = getInt("width");
		}

		zone.addMovementListener(this);
	}


	//
	// MovementListener
	//

	/**
	 * Invoked when an entity enters the object area.
	 *
	 * @param	entity		The RPEntity who moved.
	 * @param	zone		The new zone.
	 * @param	newX		The new X coordinate.
	 * @param	newY		The new Y coordinate.
	 */
	public void onEntered(RPEntity entity, StendhalRPZone zone, int newX, int newY) {
		/*
		 * Only effect players?
		 */
		if (playersOnly && !(entity instanceof Player)) {
			return;
		}

		if(handleAdded(entity)) {
			handleMovement(entity);
			addTarget(entity);
		}
	}


	/**
	 * Invoked when an entity leaves the object area.
	 *
	 * @param	entity		The RPEntity who entered.
	 * @param	zone		The old zone.
	 * @param	oldX		The old X coordinate.
	 * @param	oldY		The old Y coordinate.
	 *
	 */
	public void onExited(RPEntity entity, StendhalRPZone zone, int oldX, int oldY) {
		/*
		 * Only effect players?
		 */
		if (playersOnly && !(entity instanceof Player)) {
			return;
		}

		if(targets.contains(entity.getID())) {
			handleMovement(entity);
			removeTarget(entity);
			handleRemoved(entity);
		}
	}


	/**
	 * Invoked when an entity moves while over the object area.
	 *
	 * @param	entity		The RPEntity who left.
	 * @param	zone		The zone.
	 * @param	oldX		The old X coordinate.
	 * @param	oldY		The old Y coordinate.
	 * @param	newX		The new X coordinate.
	 * @param	newY		The new Y coordinate.
	 */
	public void onMoved(RPEntity entity, StendhalRPZone zone, int oldX, int oldY, int newX, int newY) {
		if(targets.contains(entity.getID())) {
			handleMovement(entity);
		}
	}


	//
	// TurnListener
	//

	/**
	 * This method is called when the turn number is reached.
	 *
	 * @param	currentTurn	Current turn number.
	 * @param	message		The string that was used.
	 */
	public void onTurnReached(int currentTurn, String message) {
		IRPZone zone;
		Rectangle2D area;

		zone = getZone();
		area = getArea();

		/*
		 * Perform action on entities still in the area.
		 * Remove those that have gone missing.
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
			TurnNotifier.get().notifyInTurns(interval, this, null);
		}
	}
}
