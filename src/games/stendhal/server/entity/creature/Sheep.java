/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.creature;

import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.mapstuff.spawner.SheepFood;
import games.stendhal.server.entity.player.Player;

import org.apache.log4j.Logger;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.SyntaxException;
import marauroa.common.game.Definition.Type;

/**
 * A sheep is a domestic animal that can be owned by a player. It eats berries
 * from bushes and can be sold.
 */
/**
 * @author Daniel Herding
 * 
 */
public class Sheep extends DomesticAnimal {

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(Sheep.class);

	/**
	 * The amount of hunger that indicates hungry.
	 */
	protected static final int HUNGER_HUNGRY = 50;

	/**
	 * The amount of hunger that indicates extremely hungry.
	 */
	protected static final int HUNGER_EXTREMELY_HUNGRY = 500;

	/**
	 * The amount of hunger that indicates starvation.
	 */
	protected static final int HUNGER_STARVATION = 1000;

	/**
	 * The weight at which the sheep will stop eating.
	 */
	public final int MAX_WEIGHT = 100;

	private static final int HP = 30;

	private static final int ATK = 5;

	private static final int DEF = 15;

	private static final int XP = 0;

	/**
	 * Random timing offset to give sheep non-synchronized reactions.
	 */
	private int timingAdjust;

	private int hunger;

	public static void generateRPClass() {
		try {
			RPClass sheep = new RPClass("sheep");
			sheep.isA("creature");
			sheep.addAttribute("weight", Type.BYTE);
		} catch (SyntaxException e) {
			logger.error("cannot generate RPClass", e);
		}
	}

	/**
	 * Creates a new wild Sheep.
	 * 
	 * @throws AttributeNotFoundException
	 */
	public Sheep() {
		this(null);
	}

	/**
	 * Creates a new Sheep that is owned by a player.
	 * 
	 * @throws AttributeNotFoundException
	 */
	public Sheep(Player owner) {
		super(owner);
		setRPClass("sheep");
		put("type", "sheep");

		setATK(ATK);
		setDEF(DEF);
		setXP(XP);
		initHP(HP);
		baseSpeed = 0.25;
		hunger = 0;
		timingAdjust = Rand.rand(10);
		update();
		logger.debug("Created Sheep: " + this);
	}

	/**
	 * Creates a Sheep based on an existing sheep RPObject, and assigns it to a
	 * player.
	 * 
	 * @param object
	 * @param owner
	 *            The player who should own the sheep
	 * @throws AttributeNotFoundException
	 */
	public Sheep(RPObject object, Player owner) {
		super(object, owner);

		setRPClass("sheep");
		put("type", "sheep");
		baseSpeed = 0.25;
		hunger = 0;
		timingAdjust = Rand.rand(10);

		update();
		logger.debug("Created Sheep: " + this);
	}

	/**
	 * Is called when the sheep dies. Removes the dead sheep from the owner.
	 * 
	 * @param killer
	 *            The entity who caused the death
	 */
	@Override
	public void onDead(String killername) {
		cleanUpSheep();

		super.onDead(killername);
	}

	@Override
	public void onDead(Entity killer) {
		cleanUpSheep();

		super.onDead(killer);
	}

	private void cleanUpSheep() {
		if (owner != null) {
			if (owner.hasSheep()) {
				owner.removeSheep(this);
			} else {
				logger.warn("INCOHERENCE: Sheep " + this + " isn't owned by "
						+ owner);
			}
		}
	}

	/**
	 * Returns the SheepFood that is nearest to the sheep's current position. If
	 * there is no SheepFood within the given range, returns none.
	 * 
	 * @param range
	 *            The maximum distance to a SheepFood
	 * @return The nearest SheepFood, or null if there is none within the given
	 *         range
	 */
	private SheepFood getNearestFood(double range) {
		// This way we save several sqrt operations
		double squaredDistance = range * range;

		SheepFood chosen = null;

		for (SheepFood food : getZone().getSheepFoodList()) {
			if (food.getAmount() > 0) {
				double foodDistance = squaredDistance(food);

				if (foodDistance <= squaredDistance) {
					chosen = food;
					squaredDistance = foodDistance;
				}
			}
		}

		return chosen;
	}

	/**
	 * Called when the sheep is hungry.
	 * 
	 * @return <code>true</code> if the sheep is hunting for food.
	 */
	protected boolean onHungry() {
		boolean hunting = "food".equals(getIdea());

		/*
		 * Will try to eat if one of... - Food already on the mind and not
		 * moving (collision?) - Food not on the mind and hunger pains (every
		 * 10)
		 */
		if (hunting) {
			if (!stopped()) {
				return true;
			}
		} else {
			/*
			 * Only do something on occasional hunger pains
			 */
			if ((hunger % 10) != 0) {
				return false;
			}
		}

		/*
		 * Search for food
		 */
		SheepFood food = getNearestFood(6);

		if (food != null) {
			hunting = true;

			if (nextTo(food)) {
				logger.debug("Sheep eats");
				setIdea("eat");
				eat(food);
				clearPath();
				stop();
			} else {
				logger.debug("Sheep moves to food");
				setIdea("food");
				setMovement(food, 0, 0, 20);
				// setAsynchonousMovement(food,0,0);
			}
		} else if (hunting) {
			setIdea(null);
			hunting = false;
		}

		return hunting;
	}

	/**
	 * Called when the sheep is idle.
	 */
	protected void onIdle() {
		int turn = StendhalRPRuleProcessor.get().getTurn() + timingAdjust;

		if (owner == null) {
			/*
			 * Check if player near (creature's enemy)
			 */
			if (((turn % 15) == 0) && isEnemyNear(20)) {
				logger.debug("Sheep (ownerless) moves randomly");
				moveRandomly();
			} else {
				logger.debug("Sheep sleeping");

				// TODO: Add 'sleep' idea?
				// setIdea("sleep");
				setIdea(null);
			}
		} else if (((turn % 10) == 0) && (hunger >= HUNGER_EXTREMELY_HUNGRY)) {
			/*
			 * An extremely hungry sheep becomes agitated
			 */
			setIdea("food");
			setRandomPathFrom(owner.getX(), owner.getY(), 20);
			setSpeed(getBaseSpeed());
		} else if (!nextTo(owner)) {
			moveToOwner();
		} else {
			if ((turn % 100) == 0) {
				logger.debug("Sheep is bored");

				// TODO: Add 'curious' idea?
				// setIdea("curious");
				setRandomPathFrom(owner.getX(), owner.getY(), 10);
				setSpeed(getBaseSpeed());
			} else {
				logger.debug("Sheep has nothing to do");
				setIdea(null);
			}
		}
	}

	/**
	 * Called when the sheep is starving.
	 */
	protected void onStarve() {
		if (weight > 0) {
			setWeight(weight - 1);
		} else {
			damage(1, "starvation");
		}

		hunger /= 2;
	}

	/**
	 * Let the sheep eat some food.
	 * 
	 * @param food
	 *            The food to eat.
	 */
	private void eat(SheepFood food) {
		int amount = food.getAmount();

		if (amount > 0) {
			food.onFruitPicked(null);

			if (weight < MAX_WEIGHT) {
				setWeight(weight + 1);
			}

			heal(5);
			hunger = 0;
		}
	}

	//
	// RPEntity
	//

	/**
	 * Determines what the sheep shall do next.
	 */
	@Override
	public void logic() {
		hunger++;

		/*
		 * Allow owner to call sheep (will override other reactions)
		 */
		if ((owner != null) && owner.has("text")
				&& owner.get("text").contains("sheep")) {
			moveToOwner();
		} else if (stopped()) {
			/*
			 * Hungry?
			 */
			if ((hunger < HUNGER_HUNGRY) || !onHungry()) {
				/*
				 * If not hunting for food, do other things
				 */
				onIdle();
			}
		} else if (hunger >= HUNGER_EXTREMELY_HUNGRY) {
			onHungry();
		}

		/*
		 * Starving?
		 */
		if (hunger >= HUNGER_STARVATION) {
			onStarve();

		}

		// TODO: Move to upper level logic()?, as it really seems to
		// apply to all RPEntity's.
		if (HP > 0) {
			applyMovement();
		}
		notifyWorldAboutChanges();

	}

	//
	// Entity
	//

	@Override
	public String describe() {
		String text = "You see a sheep; it looks like it weighs about "
				+ weight + ".";
		if (hasDescription()) {
			text = getDescription();
		}
		return (text);
	}
}
