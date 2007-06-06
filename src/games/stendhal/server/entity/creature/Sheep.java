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

import games.stendhal.server.StendhalRPAction;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.spawner.PassiveEntityRespawnPoint;
import games.stendhal.server.entity.spawner.SheepFood;
import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import org.apache.log4j.Logger;

/**
 * A sheep is a domestic animal that can be owned by a player.
 * It eats berries from bushes and can be sold. 
 */
/**
 * @author Daniel Herding
 *
 */
public class Sheep extends DomesticAnimal {

	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(Sheep.class);

	final private double SPEED = 0.25;

	/**
	 * The weight at which the sheep will stop eating.
	 */
	final public int MAX_WEIGHT = 100;

	final private static int HP = 30;

	final private static int ATK = 5;

	final private static int DEF = 15;

	final private static int XP = 0;

	private int hunger;

	public static void generateRPClass() {
		try {
			RPClass sheep = new RPClass("sheep");
			sheep.isA("creature");
			sheep.add("weight", RPClass.BYTE);
			sheep.add("eat", RPClass.FLAG);
		} catch (RPClass.SyntaxException e) {
			logger.error("cannot generate RPClass", e);
		}
	}

	/**
	 * Creates a new wild Sheep.
	 * @throws AttributeNotFoundException
	 */
	public Sheep() throws AttributeNotFoundException {
		this(null);
	}

	/**
	 * Creates a new Sheep that is owned by a player.
	 * @throws AttributeNotFoundException
	 */
	public Sheep(Player owner) throws AttributeNotFoundException {
		super(owner);
		put("type", "sheep");

		setATK(ATK);
		setDEF(DEF);
		setXP(XP);
		setBaseHP(HP);
		setHP(HP);

		hunger = 0;
		update();
		logger.debug("Created Sheep: " + this);
	}

	/**
	 * Creates a Sheep based on an existing sheep RPObject, and assigns it to
	 * a player.
	 * @param object
	 * @param owner The player who should own the sheep
	 * @throws AttributeNotFoundException
	 */
	public Sheep(RPObject object, Player owner) throws AttributeNotFoundException {
		super(object, owner);

		put("type", "sheep");
		hunger = 0;

		update();
		logger.debug("Created Sheep: " + this);
	}

	/**
	 * Is called when the sheep dies. Removes the dead sheep from the owner.
	 * @param killer The entity who caused the death
	 */
	@Override
	public void onDead(Entity killer) {
		if (owner != null) {
			if (owner.hasSheep()) {
				owner.removeSheep(this);
			} else {
				logger.warn("INCOHERENCE: Sheep " + this + " isn't owned by " + owner);
			}
		} else {
			StendhalRPRuleProcessor.get().removeNPC(this);
		}

		super.onDead(killer);
	}

	@Override
	public double getSpeed() {
		return SPEED;
	}

	/**
	 * Returns the SheepFood that is nearest to the sheep's current position.
	 * If there is no SheepFood within the given range, returns none.
	 * @param range The maximum distance to a SheepFood
	 * @return The nearest SheepFood, or null if there is none within the
	 *         given range
	 */
	private SheepFood getNearestFood(double range) {
		int x = getX();
		int y = getY();

		double squaredDistance = range * range; // This way we save several sqrt operations
		SheepFood chosen = null;

		for (SheepFood food : getZone().getSheepFoodList()) {
			if (food.getAmount() > 0) {
				int fx = food.getX();
				int fy = food.getY();

				double foodDistance = squaredDistance(food);

				if (foodDistance < squaredDistance) {
					chosen = food;
					squaredDistance = foodDistance;
				}
			}
		}

		return chosen;
	}


	/**
	 * Called when the sheep is starving.
	 */
	protected void onStarve() {
		if(weight > 0) {
			setWeight(weight - 1);
			hunger = 0;
		} else {
			damage(1, "starvation");
			hunger -= 50;
		}
	}


	/**
	 * Let the sheep eat some food.
	 *
	 * @param	food		The food to eat.
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

	/**
	 * Determines what the sheep shall do next.
	 */
	@Override
	public void logic() {
		Log4J.startMethod(logger, "logic");

		hunger++;

		/*
		 * Allow owner to call sheep
		 */
		if ((owner != null) && owner.has("text") && owner.get("text").contains("sheep")) {
			clearPath();
			moveToOwner();
		}


		/*
		 * Hunting if moving and food is on the mind
		 */
		boolean hunting = !stopped() && "food".equals(getIdea());

		/*
		 * If not already hunting for food, try to find some when
		 * hungry (and hunger pains surface [every 10]).
		 */
		if(!hunting && (hunger >= 50) && ((hunger % 10) == 0)) {
			SheepFood food = getNearestFood(6);

			if(food != null) {
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
					//setAsynchonousMovement(food,0,0);
					moveto(SPEED);
					hunting = true;
				}
			} else {
				setIdea(null);
				hunting = false;
			}
		}

		/*
		 * Starving?
		 */
		if (hunger > 200) {
			onStarve();
		}

		/*
		 * If not hunting for food, do other things
		 */
		if(!hunting) {
			if (owner == null) {
				if (!isEnemyNear(20)) {
					/*
					 * If there is no player near then
					 * no one will see us...
					 */
					stop();
					notifyWorldAboutChanges();
					return;
				} else {
					logger.debug("Sheep (ownerless) moves randomly");
					moveRandomly();
				}
			} else if (!nextTo(owner)) {
				moveToOwner();
			} else {
				// TODO: Add bordem threshold to
				//       wander [slightly] from owner
				logger.debug("Sheep has nothing to do");
				setIdea(null);
				stop();
				clearPath();
			}
		}

		if (!stopped()) {
			StendhalRPAction.move(this);
			// /* if we collided with something we stop and clear the path */
			if (collides()) {
				stop();
				clearPath();

				// move randomly, hoping we find a way by chance
				moveRandomly();
			}
		}

		notifyWorldAboutChanges();
		Log4J.finishMethod(logger, "logic");
	}

	@Override
	public String describe() {
		String text = "You see a sheep; it looks like it weighs about " + weight + ".";
		if (hasDescription()) {
			text = getDescription();
		}
		return (text);
	}

}
