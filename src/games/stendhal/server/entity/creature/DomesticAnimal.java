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

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.Food;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPObject;

/**
 * A domestic animal can be owned by a player;
 * <p>
 * each player can't own more than one domestic animal.
 * <p>
 * It has a weight; when it dies, it leaves an amount of meat, depending on its
 * weight.
 */
public abstract class DomesticAnimal extends Creature {

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(DomesticAnimal.class);

	protected int weight;
	int incHP = 2;

	protected boolean wasOwned = false;

	/**
	 * The player who owns the domestic animal, or null if the animal is wild.
	 */
	protected Player owner;

	/**
	 * Creates a new wild DomesticAnimal.
	 */
	public DomesticAnimal() {
		put("title_type", "friend");
		setPosition(0, 0);
		setSize(1, 1);
	}

	/**
	 * Creates a wild DomesticAnimal based on an existing RPObject.
	 *
	 * @param object object containing the data for the animal
	 */
	public DomesticAnimal(final RPObject object) {
		super(object);
		put("title_type", "friend");

		if (object.has("title_type")) {
			put("title_type", object.get("title_type"));
		}

		// set the default range for movements
		setPerceptionRange(20);

		// make pets passable so players don't get trapped
		setResistance(75);
	}

	/**
	 * Creates a wild DomesticAnimal based on an existing RPObject, and assigns
	 * it to a player.
	 *
	 * @param object owning player, or <code>null</code>
	 * @param owner
	 *            The player who should own the sheep
	 */
	public DomesticAnimal(final RPObject object, final Player owner) {
		this(object);
		this.owner = owner;
		if (owner != null) {
			wasOwned = true;
		}
		int storedHP = getInt("hp");
		// fetch the speed etc values...
		setUp();
		// ...but don't heal the animal
		setHP(storedHP);
	}

	public void setOwner(final Player owner) {
		this.owner = owner;
		if (owner != null) {
			wasOwned = true;
			if (takesPartInCombat() && getZone() != null) {
				getZone().addToPlayersAndFriends(this);
			}
			// make pets passable so players don't get trapped
			setResistance(75);
		} else {
			setResistance(100);
		}
	}

	/**
	 * Set the default stats of the animal. This should include HP, incHP (the
	 * amount of healing when eating), ATK, DEF, XP and base speed.
	 */
	abstract void setUp();

	/**
	 * Does this domestic animal take part in combat?
	 *
	 * @return true, if it can be attacked by creatures, false otherwise
	 */
	protected boolean takesPartInCombat() {
		return true;
	}

	public Player getOwner() {
		return owner;
	}

	/**
	 * Checks if this domestic animal was owned by a player,
	 * regardless of whether it is owned at the moment.
	 *
	 * @return <code>true</code>, if the creature had been owned, otherwise
	 * 	<code>false</code>
	 */
	public boolean wasOwned() {
		return wasOwned;
	}

	@Override
	public void update() {
		super.update();
		if (has("weight")) {
			weight = getInt("weight");
		}
	}

	public void setWeight(final int weight) {
		this.weight = weight;
		put("weight", weight);
	}

	public int getWeight() {
		return weight;
	}

	protected void moveToOwner() {
		logger.debug("Domestic animal (owner) moves to owner");
		setIdea("follow");
		setMovement(owner, 0, 0, getMovementRange());
		// setAsynchonousMovement(owner,0,0);
	}

	/**
	 * Can be called when the sheep dies. Puts meat onto its corpse; the amount
	 * of meat depends on the domestic animal's weight.
	 *
	 * @param corpse
	 *            The corpse on which to put the meat
	 */
	@Override
	protected void dropItemsOn(final Corpse corpse) {
		final Food food = (Food) SingletonRepository.getEntityManager().getItem("meat");
		food.setQuantity(getWeight() / 10 + 1);
		corpse.add(food);
	}

	@Override
	protected void handleObjectCollision() {
		stop();
		clearPath();
	}

	@Override
	protected void handleSimpleCollision(final int nx, final int ny) {
		stop();
		clearPath();
	}

	/**
	 * Is the owner of this pet calling its name or the type name like "pet"?
	 *
	 * @return boolean flag
	 */
	protected boolean isOwnerCallingMe() {
    	if (owner != null) {
    		String text = owner.get("text");

    		if (text != null) {
    			text = text.trim().toLowerCase();

    			// react on calling the pet's name
        		final String title = getTitle();
    			if ((title != null) && text.startsWith(title.trim().toLowerCase())) {
    				return true;
    			}

    			// react on calling the pet type ("cat", "sheep", ...)
        		final String type = get("type");
    			if ((type != null) && text.startsWith(type)) {
    				return true;
    			}
    		}
    	}

    	return false;
    }

}
