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

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;

import java.util.Set;

import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.SyntaxException;
import marauroa.common.game.Definition.Type;

/**
 * A pet is a domestic animal that can be owned by a player. It eats chicken 
 * from the ground. They move faster than sheep.
 *
 * TODO: pets attack weak animals for you
 */
/**
 * @author kymara
 *
 */
public abstract class Pet extends DomesticAnimal {

	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(Pet.class);

	/**
	 * The weight at which the pet will stop eating.
	 */
	public final int MAX_WEIGHT = 100;

	protected static int HP = 100;

	protected static int incHP = 5;

	protected static int ATK = 10;

	protected static int DEF = 20;

	protected static int XP;

	protected int hunger;

	public static void generateRPClass() {
		try {
			RPClass pet = new RPClass("pet");
			pet.isA("creature");
			pet.addAttribute("weight", Type.BYTE);
			pet.addAttribute("eat", Type.FLAG);
		} catch (SyntaxException e) {
			logger.error("cannot generate RPClass", e);
		}
	}

	/**
	 * Creates a new wild Pet.
	 *
	 * @throws AttributeNotFoundException
	 */
	public Pet() {
		this(null);
	}

	/**
	 * Creates a new Pet that is owned by a player.
	 *
	 * @throws AttributeNotFoundException
	 */
	public Pet(Player owner) {
		super(owner);
		baseSpeed = 0.5;

		setATK(ATK);
		setDEF(DEF);
		setXP(XP);
		setBaseHP(HP);
		setHP(HP);

		hunger = 0;

	}

	/**
	 * Creates a Pet based on an existing pet RPObject, and assigns it to a
	 * player.
	 *
	 * @param object
	 * @param owner
	 *            The player who should own the pet
	 * @throws AttributeNotFoundException
	 */
	public Pet(RPObject object, Player owner) {
		super(object, owner);
		baseSpeed = 0.5;
		hunger = 0;
	}

	/**
	 * Is called when the pet dies. Removes the dead pet from the owner.
	 *
	 * @param killer
	 *            The entity who caused the death
	 */
	@Override
	public void onDead(Entity killer) {
		if (owner != null) {
			if (owner.hasPet()) {
				owner.removePet(this);
			} else {
				logger.warn("INCOHERENCE: Pet " + this + " isn't owned by "
						+ owner);
			}
		} else {
			StendhalRPRuleProcessor.get().removeNPC(this);
		}

		super.onDead(killer);
	}

	/**
	 * Returns the PetFood that is nearest to the pet's current position. If
	 * there is no PetFood within the given range, returns none.
	 *
	 * @param range
	 *            The maximum distance to a PetFood
	 * @return The nearest PetFood, or null if there is none within the given
	 *         range
	 */
	private Item getNearestFood(double range) {

		Set<Item> items = getZone().getItemsOnGround();
		double squaredDistance = range * range; // This way we save several sqrt
												// operations
		Item chosen = null;
		for (Item i : items) {
			if (canEat(i)) {
				if (this.squaredDistance(i) < squaredDistance) {
					chosen = i;
					squaredDistance = this.squaredDistance(i);
				}
			}

		}

		return chosen;
	}

	boolean canEat(Item i) {

		return "chicken".equals(i.getItemSubclass());

	}

	private void eat(Item food) {
		if (weight < MAX_WEIGHT) {
			setWeight(weight + 1);
		}
		food.removeOne();
		hunger = 0;
	}

	//
	// RPEntity
	//

	/**
	 * Determines what the pet shall do next.
	 */
	@Override
	public void logic() {

		if (!isEnemyNear(20) && (owner == null)) {
			// if there is no player near and none will see us...
			stop();
			notifyWorldAboutChanges();
			return;
		}

		hunger++;
		Item food = getNearestFood(6);

		if ((hunger > 50) && (food  != null)
				&& (weight < MAX_WEIGHT)) {
			if (nextTo(food)) {
				logger.debug("Pet eats");
				setIdea("eat");
				eat(food);
				clearPath();
				stop();
			} else {
				logger.debug("Pet moves to food");
				setIdea("food");
				setMovement(food, 0, 0, 20);
				// setAsynchonousMovement(food,0,0);
			}
		} else if (owner == null) {
			logger.debug("Pet (ownerless) moves randomly");
			moveRandomly();
		} else if ((owner != null) && !nextTo(owner)) {
			moveToOwner();
		} else {
			logger.debug("Pet has nothing to do");
			setIdea(null);
			stop();
			clearPath();
		}

		if ((owner != null) && owner.has("text")
				&& owner.get("text").contains("pet")) {
			clearPath();
			moveToOwner();
		}

		this.applyMovement();

		if (getHP() < getBaseHP()) {
			healSelf(incHP, 100);
		}

		notifyWorldAboutChanges();

	}

	// Should never be called
	@Override
	public String describe() {
		String text = "You see a pet; it looks like it weighs about " + weight
				+ ".";
		if (hasDescription()) {
			text = getDescription();
		}
		return (text);
	}

}
