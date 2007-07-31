/*
 * @(#) src/games/stendhal/server/entity/area/DamagingArea.java
 *
 * $Id$
 */

package games.stendhal.server.entity.area;

//
//

import games.stendhal.common.Level;
import games.stendhal.server.entity.RPEntity;

import java.util.Random;

import marauroa.common.Log4J;
import marauroa.common.Logger;

/**
 * An area that damages an RPEntity while over it.
 *
 */
public class DamagingArea extends OccupantArea {

	/**
	 * The logger instance.
	 */
	private static final Logger logger = Log4J.getLogger(DamagingArea.class);

	/**
	 * The damage inflicted each hit.
	 */
	protected int damage;

	/**
	 * The chance of damage while walking (0.0 - 1.0).
	 */
	protected double probability;

	/**
	 * Random number generator.
	 */
	protected Random rand;

	/**
	 * Create a damaging area.
	 *
	 * @param	damage		The amount of damage to inflict.
	 * @param   width       Width of  this area
	 * @param   height      Height of this area
	 * @param	interval	How often damage is given while
	 *				stationary (in turns).
	 * @param	probability	The chance of damage while walking
	 *				(0.0 - 1.0).
	 */
	public DamagingArea(String name, int width, int height, int damage, int interval, double probability) {
		super(name, width, height, interval);

		this.damage = damage;
		this.probability = probability;

		rand = new Random();

		setResistance(50);
	}

	//
	// DamagingArea
	//

	/**
	 * Calculate the entity's final defense value.
	 * Taken from new (potential replacement) combat code.
	 */
	protected float calculateDefense(RPEntity entity) {
		float potential;
		float min;
		float score;

		float armor = entity.getItemDef() + 1.0f;
		int def = entity.getDEF();

		if (logger.isDebugEnabled()) {
			logger.debug("defender has " + def + " and uses a armor of " + armor);
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
		score += ((float) entity.useKarma(0.1) * potential);

		if (logger.isDebugEnabled()) {
			logger.debug("DEF MAX: " + potential + "  DEF SCORE: " + score);
		}

		return score;
	}

	/**
	 * Inflict damage on an entity.
	 *
	 * @param	entity		The entity to damage.
	 */
	protected boolean doDamage(RPEntity entity) {
		float attack;
		float defense;
		int actualDamage;

		/*
		 * Don't beat a dead horse!
		 */
		if (entity.getHP() == 0) {
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

		if (actualDamage <= 0) {
			return true;
		}

		entity.onDamaged(this, Math.min(actualDamage, entity.getHP()));
		return true;
	}


	//
	// OccupantArea
	//

	/**
	 * An entity has entered the area. This should not apply any actions
	 * that <code>handleMovement()</code> does.
	 *
	 * @param	entity		The RPEntity that was added.
	 *
	 * @return	<code>false</code> if this entity should not be
	 *		processed, <code>true</code> otherwise.
	 */
	@Override
	protected boolean handleAdded(RPEntity entity) {
		if(!super.handleAdded(entity)) {
			return false;
		}

		entity.onAttacked(this, true);
		doDamage(entity);
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
	@Override
	protected boolean handleInterval(RPEntity entity) {
		doDamage(entity);
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
	@Override
	protected boolean handleMovement(RPEntity entity) {
		if (rand.nextDouble() < probability) {
			doDamage(entity);
		}

		return true;
	}

	/**
	 * An entity has left the area. This should not apply any actions
	 * that <code>handleMovement()</code> does.
	 *
	 * @param	entity		The RPEntity that was added.
	 */
	@Override
	protected void handleRemoved(RPEntity entity) {
		entity.onAttacked(this, false);
		super.handleRemoved(entity);
	}
}
