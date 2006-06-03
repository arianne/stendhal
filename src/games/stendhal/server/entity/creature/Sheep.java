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
import games.stendhal.server.entity.PlantGrower;
import games.stendhal.server.entity.SheepFood;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.RPEntity;
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
     * @param who The entity who caused the death
	 */
	@Override
	public void onDead(RPEntity who) {
		if (owner != null) {
			if (owner.hasSheep()) {
				owner.removeSheep(this);
			} else {
				logger.warn("INCOHERENCE: Sheep " + this + " isn't owned by "
						+ owner);
			}
		} else {
			rp.removeNPC(this);
		}

		super.onDead(who);
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
		int x = getx();
		int y = gety();

		double distance = range * range; // We save this way several sqrt operations
		SheepFood chosen = null;

		for (PlantGrower grower : rp.getPlantGrowers()) {
			if (grower instanceof SheepFood && grower.get("zoneid").equals(get("zoneid"))) {
				SheepFood food = (SheepFood) grower;
				int fx = food.getx();
				int fy = food.gety();

				if (Math.abs(fx - x) < range && Math.abs(fy - y) < range
						&& food.getAmount() > 0) {
					if (this.distance(food) < distance) {
						chosen = food;
						distance = this.distance(food);
					}
				}
			}
		}

		return chosen;
	}

	private void eat(SheepFood food) {
		int amount = food.getAmount();
		if (amount > 0) {
			food.setAmount(amount - 1);
			world.modify(food);
			hunger = 0;

			if (weight < MAX_WEIGHT) {
				setWeight(weight + 1);
			}
		}
	}

	/**
	 * Determines what the sheep shall do next.
	 */
	@Override
	public void logic() {
		Log4J.startMethod(logger, "logic");

		if (getNearestPlayer(20) == null && owner == null) // if there is no player near and none will see us... 
		{
			stop();

			world.modify(this);
			return;
		}

		hunger++;
		SheepFood food = null;

		if (hunger > 50 && (food = getNearestFood(6)) != null
				&& weight < MAX_WEIGHT) {
			if (nextto(food, 0.25)) {
				logger.debug("Sheep eats");
				setIdea("eat");
				eat(food);
				clearPath();
				stop();
			} else {
				logger.debug("Sheep moves to food");
				setIdea("food");
				setMovement(food, 0, 0, 20);
				//        setAsynchonousMovement(food,0,0);
				moveto(SPEED);
			}
		} else if (owner == null) {
			logger.debug("Sheep (ownerless) moves randomly");
			setIdea("walk");
			moveRandomly(SPEED);
		} else if (owner != null && !nextto(owner, 0.25)) {
			logger.debug("Sheep (owner) moves to owner");
			setIdea("follow");
			setMovement(owner, 0, 0, 20);
			//      setAsynchonousMovement(owner,0,0);
			moveto(SPEED);
		} else {
			logger.debug("Sheep has nothing to do");
			setIdea("stop");
			stop();
			clearPath();
		}

		if (owner != null && owner.has("text")
				&& owner.get("text").contains("sheep")) {
			logger.debug("Sheep(owner) moves to Owner");
			setIdea("follow");
			clearPath();
			setMovement(owner, 0, 0, 20);
			//      setAsynchonousMovement(owner,0,0);
			moveto(SPEED);
		}

		if (!stopped()) {
			StendhalRPAction.move(this);
			// if we collided with something we stop and clear the path
			if (collides()) {
				stop();
				clearPath();
			}
		}

		if (rp.getTurn() % 100 == 0 && getHP() < getBaseHP()) {
			if (getHP() + 5 <= getBaseHP()) {
				setHP(getHP() + 5);
			} else {
				setHP(getBaseHP());
			}
		}

		world.modify(this);
		Log4J.finishMethod(logger, "logic");
	}

	@Override
	public String describe() {
		String text = "You see a sheep that weighs " + weight + ".";
		if (hasDescription())
			text = getDescription();
		return (text);
	}

}
