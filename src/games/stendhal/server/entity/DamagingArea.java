/*
 * @(#) src/games/stendhal/server/entity/DamagingArea.java
 *
 *$Id$
 */

package games.stendhal.server.entity;

//
//

import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.apache.log4j.Logger;

import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.IRPZone;

import games.stendhal.common.Level;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.MovementListener;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;

/**
 * An area that damages an RPEntity while over it.
 *
 */
public class DamagingArea extends PassiveEntity
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
	 * How often damage is given while stationary (in turns).
	 */
	protected int			interval;

	/**
	 * The inflict damage only on players.
	 */
	protected boolean		playersOnly;

	/**
	 * The chance of damage while walking (0.0 - 1.0).
	 */
	protected double		probability;

	/**
	 * A list of entities [potentially] in range.
	 */
	protected List<RPEntity.ID>	targets;

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
	public DamagingArea(String name, int damage, int interval,
	 double probability) throws AttributeNotFoundException {
		put("name", name);
		put("type", "damaging_area");

		this.damage = damage;
		this.interval = interval;
		this.probability = probability;

		playersOnly = false;
		rand = new Random();
		targets = new LinkedList<RPEntity.ID>();
	}


	//
	// DamagingArea
	//

	/**
	 * Add an entity to the target list.
	 *
	 * @param	entity		The RPEntity to add.
	 */
	protected void addTarget(RPEntity entity) {
		targets.add(entity.getID());
		entity.onAttack(this, true);

		if(targets.size() == 1) {
			TurnNotifier.get().notifyInTurns(interval, this, null);
		}
	}


	/**
	 * Calculate the entity's final defense value.
	 * Taken from new (potential replacement) combat code.
	 */
	protected float calculateDefense(RPEntity entity) {
		float	potential;
		float	min;
		float	score;


		float armor = entity.getItemDef() + 1.0f;
		int def = entity.getDEF();

		if (logger.isDebugEnabled()) {
			logger.debug("defender has " + def
				+ " and uses a armor of " + armor);
		}

		/*
		 * The maximum defense skill can double armor effectiveness
		 */
		potential = ((float) Level.getWisdom(def)) * 2.0f * armor;

		/*
		 * Wisdom allows a certain amount of skill to always be used
		 */
		min = (float) Level.getWisdom(entity.getLevel()) * 0.60f;

		score = ((rand.nextFloat() * (1.0f - min)) + min) * potential;

		/*
		 * Account for karma (+/-10%) potential
		 */
		score += ((float) entity.getKarma(0.1) * potential);

		if (logger.isDebugEnabled()) {
			logger.debug(
				"DEF MAX: " + potential
					+ "  DEF SCORE: " + score);
		}

		return score;
	}


	/**
	 * Inflict damage on an entity.
	 *
	 * @param	entity		The entity to damage.
	 */
	protected boolean doDamage(RPEntity entity) {
		float	attack;
		float	defense;
		int	actualDamage;


		/*
		 * Don't beat a dead horse!
		 */
		if(entity.getHP() == 0) {
			return false;
		}

		/*
		 * TEMP HACK - Emulate some of user's def.
		 */
		attack = damage;
		defense = calculateDefense(entity);
		actualDamage = Math.round(attack - defense);

//logger.info("attack: " + attack);
//logger.info("defense: " + defense);
//logger.info("actualDamage: " + actualDamage);

		if(actualDamage <= 0) {
			return true;
		}

		entity.onDamage(this, Math.min(actualDamage, entity.getHP()));
		return true;
	}


	/**
	 * Apply any damage done while moving.
	 *
	 * @param	entity		The RPEntity to [possibly] damage.
	 */
	protected void handleMovement(RPEntity entity) {
		if(rand.nextDouble() < probability) {
			doDamage(entity);
		}
	}



	/**
	 * Remove an entity from the target list.
	 *
	 * @param	entity		The RPEntity to remove.
	 */
	protected void removeTarget(RPEntity entity) {
		entity.onAttack(this, false);
		targets.remove(entity.getID());

		if(targets.isEmpty()) {
			TurnNotifier.get().dontNotify(this, null);
		}
	}


	/**
	 * Set whether only players get damage.
	 *
	 * @param	playersOnly	Whether to only attack players.
	 */
	public void setPlayersOnly(boolean playersOnly) {
		this.playersOnly = playersOnly;
	}

	//
	// Entity
	//

	/**
	 * Get the damage area.
	 *
	 *
	 */
	@Override
	public void getArea(Rectangle2D rect, double x, double y) {
		rect.setRect(x, y, 1, 1);
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
		StendhalRPZone	zone;


		super.update();

		/*
		 * Reregister incase coordinates changed (could be smarter)
		 */
		zone = getZone();
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
	 int newX, int newY) {
		/*
		 * Only effect players?
		 */
		if(playersOnly && !(entity instanceof Player)) {
			return;
		}

		handleMovement(entity);
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
		/*
		 * Only effect players?
		 */
		if(playersOnly && !(entity instanceof Player)) {
			return;
		}

		handleMovement(entity);
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
		/*
		 * Only effect players?
		 */
		if(playersOnly && !(entity instanceof Player)) {
			return;
		}

		handleMovement(entity);
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
		IRPZone		zone;
		Rectangle2D	area;


		zone = getZone();
		area = getArea();

		/*
		 * Damage entities still in the area, remove those not here
		 */
		Iterator<RPEntity.ID> iter = targets.iterator();

		while(iter.hasNext()) {
			RPEntity.ID id = iter.next();

			if(!zone.has(id)) {
				iter.remove();
			} else {
				RPEntity entity = (RPEntity) zone.get(id);

				if(area.intersects(entity.getArea())) {
					if(!doDamage(entity)) {
						entity.onAttack(this, false);
						iter.remove();
					}
				} else {
					entity.onAttack(this, false);
					iter.remove();
				}
			}
		}

		if(!targets.isEmpty()) {
			TurnNotifier.get().notifyInTurns(interval, this, null);
		}
	}
}
