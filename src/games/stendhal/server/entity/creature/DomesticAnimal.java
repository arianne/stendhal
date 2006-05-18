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

import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.Food;
import java.awt.geom.Rectangle2D;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPObject;

/**
 * A domestic animal can be owned by a player; each player can't own more
 * than one domestic animal. It has a weight; when it dies, it leaves an
 * amount of meat, depending on its weight.
 */
public abstract class DomesticAnimal extends Creature {

	protected int weight;

	/**
	 * The player who owns the domestic animal, or null if the animal is wild.
	 */
	protected Player owner;
	
	/**
	 * Creates a new wild DomesticAnimal.
	 * @throws AttributeNotFoundException
	 */
	public DomesticAnimal() throws AttributeNotFoundException {
		this(null);
	}

	/**
	 * Creates a new DomesticAnimal that is owned by a player.
	 * @throws AttributeNotFoundException
	 */
	public DomesticAnimal(Player owner) throws AttributeNotFoundException {
		super();
		this.owner = owner;

		put("x", 0);
		put("y", 0);
	}

	/**
	 * Creates a wild DomesticAnimal based on an existing RPObject.
	 * @param object
	 * @param owner The player who should own the sheep
	 * @throws AttributeNotFoundException
	 */
	public DomesticAnimal(RPObject object) throws AttributeNotFoundException {
		super(object);
	}

	/**
	 * Creates a wild DomesticAnimal based on an existing RPObjec, and assigns
	 * it to a player.
	 * @param object
	 * @param owner The player who should own the sheep
	 * @throws AttributeNotFoundException
	 */
	public DomesticAnimal(RPObject object, Player owner) throws AttributeNotFoundException {
		super(object);
		this.owner = owner;
	}

	/**
	 * Returns a rectangle of size 1x1, located at the animal's current
	 * position. Override this if the animal is larger than 1x1.
	 */
	public void getArea(Rectangle2D rect, double x, double y) {
		rect.setRect(x, y, 1, 1);
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public Player getOwner() {
		return owner;
	}

	public void update() throws AttributeNotFoundException {
		super.update();
		if (has("weight")) {
			weight = getInt("weight");
		}
	}

	/**
	 * Is called when the sheep dies. Removes the dead sheep from the owner.
     * @param who The entity who caused the death
	 */
	
	// TODO: this method can't yet be abstracted because the method
	// owner.removeSheep() would first have to be generalized.
//	public void onDead(RPEntity who) {
//		if (owner != null) {
//			if (owner.hasSheep()) {
//				owner.removeSheep(this);
//			} else {
//				logger.warn("INCOHERENCE: Domestic animal " + this + " isn't owned by "
//						+ owner);
//			}
//		} else {
//			rp.removeNPC(this);
//		}
//
//		super.onDead(who);
//	}

	public abstract double getSpeed();

	public void setWeight(int weight) {
		this.weight = weight;
		put("weight", weight);
	}

	public int getWeight() {
		return weight;
	}

	/**
	 * Can be called when the sheep dies. Puts meat onto its corpse; the
	 * amount of meat depends on the sheep's weight.
	 * @param corpse The corpse on which to put the meat
	 */
	protected void dropItemsOn(Corpse corpse) {
		Food food = (Food) world.getRuleManager().getEntityManager().getItem("meat");
		food.setQuantity(getWeight() / 10 + 1);
		corpse.add(food);
	}

	/**
	 * Determines what the animal shall do next.
	 */
	public abstract void logic();
}
