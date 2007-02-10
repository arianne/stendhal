/*
 * @(#) src/games/stendhal/server/entity/DamagingArea.java
 *
 *$Id$
 */

package games.stendhal.server.entity;

//
//

import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.apache.log4j.Logger;

import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.IRPZone;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.events.MovementListener;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;

/**
 * An area that damages an RPEntity while over it.
 *
 */
public class DamagingArea extends Entity
 implements MovementListener, TurnListener {
	/**
	 * The logger instance.
	 */
	private static final Logger	logger =
					Log4J.getLogger(DamagingArea.class);

	/**
	 * The damage inflicted each hit.
	 */
	protected int			damage;

	/**
	 * How often damage is given while stationary.
	 */
	protected int			interval;

	/**
	 * The chance of damage while walking (0.0 - 1.0).
	 */
	protected double		probability;

	/**
	 * A list of entities [potentially] in range.
	 */
	protected List<RPEntity>	targets;

	/**
	 * Random number generator.
	 */
	protected Random		rand;


	/**
	 * Create a damaging area.
	 *
	 * @param	damage		The amount of damage to inflict.
	 * @param	interval	How often damage is given while
	 *				stationary (in turns).
	 * @param	probability	The chance of damage while walking
	 *				(0.0 - 1.0).
	 */
	public DamagingArea(int damage, int interval, double probability)
	 throws AttributeNotFoundException {
		put("type", "damaging_area");

		this.damage = damage;
		this.interval = interval;
		this.probability = probability;

		rand = new Random();
		targets = new LinkedList<RPEntity>();
	}


	//
	// DamagingArea
	//

	/**
	 * Inflict damage on an entity.
	 *
	 * @param	entity		The entity to damage.
	 */
	protected void doDamage(RPEntity entity) {
		// XXX - TODO - entity.onDamage() requires a valid attacker
logger.info("Would damage " + entity.getName() + " by " + damage);
	}


	/**
	 * Add an entity to the target list.
	 *
	 * @param	entity		The RPEntity to add.
	 */
	protected void addTarget(RPEntity entity) {
		if(targets.isEmpty())
			TurnNotifier.get().notifyInTurns(interval, this, null);

		targets.add(entity);
	}


	/**
	 * Remove an entity from the target list.
	 *
	 * @param	entity		The RPEntity to remove.
	 */
	protected void removeTarget(RPEntity entity) {
		targets.remove(entity);

		if(targets.isEmpty())
			TurnNotifier.get().dontNotify(this, null);
	}


	//
	// Entity
	//

	@Override
	public void getArea(Rectangle2D rect, double x, double y) {
		rect.setRect(x, y, 1, 1);
	}


	/**
	 * Checks whether players, NPCs etc. can walk over this entity.
	 *
	 * @return	<code>false</code>.
	 */
	public boolean isObstacle() {
		return false;
	}


	/**
	 * Called when this object is added to a zone.
	 *
	 * @param	zone		The zone this was added to.
	 */
	public void onAdded(StendhalRPZone zone) {
		zone.addMovementListener(this);
	}


	/**
	 * Called when this object is being removed from a zone.
	 *
	 * @param	zone		The zone this will be removed from.
	 */
	public void onRemoved(StendhalRPZone zone) {
		zone.removeMovementListener(this);
	}


	public void update() throws AttributeNotFoundException {
		StendhalRPZone	zone;


		super.update();

		/*
		 * Reregister incase coordinates changed (could be smarter)
		 */
		zone = (StendhalRPZone)
			StendhalRPWorld.get().getRPZone(getID());

		zone.removeMovementListener(this);
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
	public void onEntered(RPEntity entity, StendhalRPZone zone,
	 int newX, int newY)
	{
		addTarget(entity);
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
	public void onExited(RPEntity entity, StendhalRPZone zone,
	 int oldX, int oldY) {
		removeTarget(entity);
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
	public void onMoved(RPEntity entity, StendhalRPZone zone,
	 int oldX, int oldY, int newX, int newY) {
		if(rand.nextDouble() < probability)
			doDamage(entity);
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
	public void onTurnReached(int currentTurn, String message)
	{
		IRPZone		zone;
		Rectangle2D	area;


		zone = StendhalRPWorld.get().getRPZone(getID());
		area = getArea();

		/*
		 * Damage entities still in the area
		 */
		for(RPEntity entity : targets) {
			if(zone.has(entity.getID())
			 && area.intersects(entity.getArea())) {
				doDamage(entity);
			} else {
				removeTarget(entity);
			}
		}

		if(!targets.isEmpty())
			TurnNotifier.get().notifyInTurns(interval, this, null);
	}
}
