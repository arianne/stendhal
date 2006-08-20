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
package games.stendhal.server.entity;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import java.awt.geom.Rectangle2D;

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;

/**
 * A PlantGrower basically is a 1x1 area where a plant, a fruit or another
 * non-moving thing grows. This growing thing is a pickable Item
 * (e.g. a mushroom, an apple); by extending this class, it can also grow
 * something special (e.g. SheepFood).
 * 
 * PlantGrowers are currently invisible (fully transparent) on the client side.
 * 
 * @author Daniel Herding
 *
 */
public class PlantGrower extends Entity {

	/**
	 * How much of the next fruit is ready, as a value between 0 and 1.
	 */
	private double ripeness;
	
	/**
	 * The name of the fruit (Item) that is grown by the PlantGrower.
	 */
	private String growingItemName;
	
    /**
     * Remember which turn we were called last to compute the ripeness
     */
	private int lastTurn = 0;
    
	/**
	 * Tells how many turns it takes for a new fruit to become ripe.
	 */
	private int turnsForRegrow;
	
	public PlantGrower(RPObject object, String growingItemName, int turnsForRegrow) throws AttributeNotFoundException {
		super(object);
		this.growingItemName = growingItemName;
		this.turnsForRegrow = turnsForRegrow;
		setDescription("You see a place where a " + growingItemName	+ " can grow.");
		
		put("type", "plant_grower");
		//update();
	}

	public PlantGrower(String growingItemName, int turnsForRegrow) throws AttributeNotFoundException {
		super();
		this.growingItemName = growingItemName;
		this.turnsForRegrow = turnsForRegrow;
		setDescription("You see a place where a " + growingItemName	+ " can grow.");

		put("type", "plant_grower");
	}

	public static void generateRPClass() {
		RPClass grower = new RPClass("plant_grower");
		grower.isA("entity");
		grower.add("class", RPClass.STRING);
	}

	@Override
	public void getArea(Rectangle2D rect, double x, double y) {
		rect.setRect(x, y, 1, 1);
	}

	/**
	 * Tells if a new fruit can start to grow.
	 * @return true iff a new fruit can start to grow
	 */
	protected boolean canGrowNewFruit() {
		StendhalRPZone zone = (StendhalRPZone) world.getRPZone(this.getID());
		for (Item item: zone.getItemsOnGround().keySet()) {
			if (item.getName().equals(growingItemName) && item.getx() == this.getx() && 	item.gety() == this.gety()) {
				// don't regrow until someone picks the last grown item up.
				return false;
			}
		}
		return true;
	}

	/**
	 * Creates a new fruit.
	 */
	protected void growNewFruit() {
		StendhalRPZone zone = (StendhalRPZone) world.getRPZone(this.getID());
		// create a new grown item
		Item grownItem = world.getRuleManager().getEntityManager().getItem(growingItemName);
		grownItem.setx(this.getx());
		grownItem.sety(this.gety());
		
		zone.assignRPObjectID(grownItem);
		
		zone.add(grownItem);
		//world.modify(this);
	}
	
	/**
	 * Is invoked every few turns. Checks if a new fruit can be grown, and if the
	 * new fruit is ripe, creates it.
	 *
	 * @param aktTurn current turn
	 */
	public void regrow(int aktTurn) {
        if(lastTurn == 0) {
            lastTurn = aktTurn - 1;
        }
		if (canGrowNewFruit()) {
			ripeness += (aktTurn - lastTurn) * (1.0f / turnsForRegrow);
			// TODO: add some randomization
			if (ripeness > 1.0f) {
				ripeness = 0.0f;
				growNewFruit();
			}
		}
        lastTurn = aktTurn;
	}

	@Override
	public boolean isObstacle() {
		// The player can walk over the PlantGrower.
		return false;
	}

}
