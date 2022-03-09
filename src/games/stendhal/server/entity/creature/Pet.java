/***************************************************************************
 *                   (C) Copyright 2003-2022 - Marauroa                    *
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

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.common.ItemTools;
import games.stendhal.common.Rand;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.Killer;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.SyntaxException;

/**
 * A pet is a domestic animal that can be owned by a player. It eats chicken
 * from the ground. They move faster than sheep.

 * Pets starve if they are not fed. They can die.
 *
 * @author kymara
 *
 */
public abstract class Pet extends DomesticAnimal {
	/**
	 * The amount of hunger that indicates hungry.
	 */
	protected static final int HUNGER_HUNGRY = 300;

	/**
	 * The amount of hunger that indicates starvation.
	 */
	protected static final int HUNGER_STARVATION = 750;


	private static final int START_HUNGER_VALUE = 0;

	/** the logger instance. */
	private static final Logger LOGGER = Logger.getLogger(Pet.class);


	/**
	 * The weight at which the pet will stop eating.
	 */
	public final int MAX_WEIGHT = 100;

	/**
	 * Approximately how much slower he gets hungry if he's full weight.
	 */
	public final int FAT_FACTOR = 5;

	protected List<String> foodName = getFoodNames();

	protected List<String> medicineName = getMedicineNames();

	protected int HP = 100;

	protected int ATK = 10;

	protected int DEF = 20;

	protected int XP;

	protected int hunger;

	RPEntity myTarget = null;


	/**
	 * Creates a new wild Pet.
	 */
	public Pet() {
		super();
		baseSpeed = 0.5;
		setUp();

		// set the default perception range
		setPerceptionRange(20);

		// set the default movement range
		setMovementRange(20);

		hunger = START_HUNGER_VALUE;
	}

	/**
	 * Creates a Pet based on an existing pet RPObject, and assigns it to a
	 * player.
	 *
	 * @param object object containing the data for the Pet
	 * @param owner
	 *            The player who should own the pet
	 */
	public Pet(final RPObject object, final Player owner) {
		super(object, owner);
		hunger = START_HUNGER_VALUE;
	}

	protected abstract List<String> getFoodNames();

	protected List<String> getMedicineNames() {
		return Arrays.asList("minor potion", "potion", "greater potion", "mega potion");
	}

	public static void generateRPClass() {
		try {
			final RPClass pet = new RPClass("pet");
			pet.isA("creature");
			pet.addAttribute("weight", Type.BYTE);
			pet.addAttribute("eat", Type.FLAG);
			pet.addAttribute("drink", Type.FLAG);
		} catch (final SyntaxException e) {
			LOGGER.error("cannot generate RPClass", e);
		}
	}

	/**
	 * Is called when the pet dies. Removes the dead pet from the owner.
	 *
	 */
	@Override
	public void onDead(final Killer killer, final boolean remove) {
		cleanUpPet();
		super.onDead(killer, remove);
	}

	private void cleanUpPet() {
		if (owner != null) {
			if (owner.hasPet()) {
				owner.removePet(this);
			} else {
				LOGGER.warn("INCOHERENCE: Pet " + this + " isn't owned by " + owner);
			}
		}
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
	private Item getNearestFood(final double range) {
		// This way we save several sqrt operations
		double squaredDistance = range * range;

		Item chosen = null;

		for (final Item item : getZone().getItemsOnGround()) {
			if (canEat(item) && (this.squaredDistance(item) < squaredDistance)) {
				chosen = item;
				squaredDistance = this.squaredDistance(item);
			}
		}

		return chosen;
	}

	private Item getNearestHealItem(final double range) {
		// This way we save several sqrt operations
		double squaredDistance = range * range;

		Item chosen = null;
		for (final Item item : getZone().getItemsOnGround()) {
			if (canHeal(item) && (this.squaredDistance(item) < squaredDistance)) {
				chosen = item;
				squaredDistance = this.squaredDistance(item);
			}
		}
		return chosen;
	}

	boolean canEat(final Item i) {
		return foodName.contains(i.getName());
	}

	boolean canHeal(final Item i) {
		return medicineName.contains(i.getName());
	}

	private void eat(final Item food) {
		if (weight < MAX_WEIGHT) {
			setWeight(weight + 1);
		}
		food.removeOne();
		hunger = START_HUNGER_VALUE;
		if (getHP() < getBaseHP()) {
			// directly increase the pet's health points
			heal(incHP);
		}
	}

	private void drink(final ConsumableItem medicine) {
		if (getHP() < getBaseHP()) {
			// directly increase the pet's health points
			heal(((ConsumableItem) medicine.splitOff(1)).getAmount(), true);
			medicine.removeOne();
		}
	}

	//
	// RPEntity
	//


	/**
	 * Determines what the pet shall do next.
	 */
	@Override
	public void logic() {
		// call super class to perform common tasks like attacking targets
		super.logic();

		if (!isEnemyNear(getPerceptionRange()) && (owner == null)) {
			// if no one near and no one owns us ....
			stop();
			notifyWorldAboutChanges();
			return;
		}
		setPath(null);
		setIdea(null);
		if (weight < MAX_WEIGHT) {
			increaseHunger();
		} else if (Rand.rand(FAT_FACTOR) == 1) {
			increaseHunger();
		}

		//fight whatever owner is targeting
		if (System.getProperty("stendhal.petleveling", "false").equals("true")
				&& takesPartInCombat() && (owner != null)
				&& (owner.getAttackTarget() != null)) {
			myTarget = owner.getAttackTarget();
			this.setTarget(myTarget);
			this.setIdea("agressive");

			if (!nextTo(myTarget)) {
				clearPath();
				this.setMovement(myTarget, 0, 0, this.getMovementRange());
			}
		}


		if ((this.getLevel() >= this.getLVCap()) && canGrow()) {

			// Postpone growing to the next turn because it may involve
			// removing this NPC-entity from the zone and adding a
			// different one.
			// But we are called from within a for-loop over all NPC-entity
			// in StendhalRPZone.logic, so we may not modify that list
			TurnNotifier.get().notifyInTurns(1, new TurnListener() {

				@Override
				public void onTurnReached(int currentTurn) {
					grow();
				}
			});
		}

		//drinking logic
		boolean busyWithHealing = false;
		if (getHP() < getBaseHP()) {
			busyWithHealing = logicHealing();
		}

		if (!busyWithHealing) {
			if (hunger > HUNGER_HUNGRY) {
				logicEating();
			} else if (this.getIdea() == "agressive") {
				if (myTarget == null)
				{
					this.setIdea(null);
				}
				this.setMovement(myTarget, 0, 0, this.getMovementRange());
			} else {
				logicStandard(null);
			}
		}

		// bring the pet to the owner if he/she calls it
		if (isOwnerCallingMe()) {
			clearPath();
			moveToOwner();
		}

		this.applyMovement();
		notifyWorldAboutChanges();
	}

	private void logicStandard(String idleIdea) {
		if (owner == null) {
			LOGGER.debug("Pet (ownerless) moves randomly");
			moveRandomly();
		} else if (!nextTo(owner)) {
			moveToOwner();
		} else {
			LOGGER.debug("Pet has nothing to do");
			setIdea(idleIdea);
			stop();
			clearPath();
		}
	}

	private void logicEating() {
		// Show 'food' idea whenever hungry
		setIdea("food");

		final Item food = getNearestFood(6);

		if ((food != null)) {
			if (nextTo(food)) {
				LOGGER.debug("Pet eats");
				setIdea("eat");
				eat(food);
				clearPath();
				stop();
			} else {
				LOGGER.debug("Pet moves to food");
				setIdea("food");
				setMovement(food, 0, 0, getMovementRange());
				// setAsynchonousMovement(food,0,0);
			}
		} else if (hunger > HUNGER_STARVATION) {
			// move crazy if starving
			moveRandomly();

			hunger /= 2;
			 if (owner != null) {
				 owner.sendPrivateText("Your pet is starving!");
			 }
			LOGGER.debug("Pet starves");
			if (weight > 0) {
				setWeight(weight - 1);
			} else {
				// apply starvation damage at a safe moment
				delayedDamage(2, "starvation");
			}
		} else {
			// here, (hunger_hungry < hunger < starvation) && not near food
			// so, here, we follow owner, if we have one and if we don't,
			// we do the other stuff
			logicStandard("food");
		}
	}

	/**
	 * handles logic regarding healing
	 *
	 * @return busy with healing
	 */
	private boolean logicHealing() {
		final Item medicine = getNearestHealItem(6);
		if ((medicine != null)) {
			if (nextTo(medicine)) {
				LOGGER.debug("Pet heals");
				drink((ConsumableItem) medicine);
				clearPath();
				stop();
			} else {
				LOGGER.debug("Pet moves to medicine");
				setMovement(medicine, 0, 0, getMovementRange());
			}
			return true;
		}
		return false;
	}

	private void increaseHunger() {
		if (!getZone().getPlayers().isEmpty()) {
			hunger++;
		}
	}
	// provide a nice string, describing the pet's hunger, to add to the
	// Look description.
	private String getHungerType(final int hunger) {
		if (hunger < HUNGER_HUNGRY) {
			return " It seems quite content.";
		} else if (hunger < 2 * HUNGER_STARVATION / 3) {
			return " It appears to be getting a bit peckish.";
		} else if (hunger < 9 * HUNGER_STARVATION / 10) {
			return " It seems really hungry.";
		} else {
			return " It's on the brink of starvation!";
		}
	}

	@Override
	public String describe() {
		String text = "You see a cute " + ItemTools.itemNameToDisplayName(get("type")) + "; it looks like it weighs about " + weight + " and is level " + getLevel() + ".";
		if (hasDescription()) {
			text = getDescription();
		}
		return (text + getHungerType(hunger));
	}

	/**
	 * does this pet even have another form?
	 */
	public boolean canGrow() {
		return false;
	}

	/**
	 * grow this pet into a new form
	 */
	public void grow() {
		LOGGER.error("Subclass of Pet claimed to be growable, but did not implement grow()");
	}

}
