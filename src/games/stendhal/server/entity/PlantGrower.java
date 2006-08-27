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

import games.stendhal.common.Rand;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;

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
public class PlantGrower extends Entity implements TurnListener {

	/**
	 * Is there still a fruit that has not yet been picked up? 
	 */
	private boolean hasPickableFruit; 
	
	/**
	 * The name of the fruit (Item) that is grown by the PlantGrower.
	 */
	private String growingItemName;
	
    /**
     * Remember which turn we were called last to compute the ripeness
     */
	//private int lastTurn = 0;
    
	/**
	 * Tells how many turns it takes in average for a new fruit to become ripe.
	 */
	protected int meanTurnsForRegrow;
	
	public PlantGrower(RPObject object, String growingItemName, int meanTurnsForRegrow) throws AttributeNotFoundException {
		super(object);
		this.growingItemName = growingItemName;
		this.meanTurnsForRegrow = meanTurnsForRegrow;
		setDescription("You see a place where a " + growingItemName	+ " can grow.");
		
		put("type", "plant_grower");
		//update();
	}

	public PlantGrower(String growingItemName, int meanTurnsForRegrow) throws AttributeNotFoundException {
		super();
		this.growingItemName = growingItemName;
		this.meanTurnsForRegrow = meanTurnsForRegrow;
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

	public void onFruitPicked() {
		hasPickableFruit = false;
		
		TurnNotifier.get().notifyInTurns(getRandomTurnsForRegrow(), this, null);
	}
	
	protected int getRandomTurnsForRegrow() {
		return Rand.rand(meanTurnsForRegrow, (int) (0.1 * meanTurnsForRegrow));
	}
	
	/**
	 * Creates a new fruit.
	 */
	protected void growNewFruit() {
		StendhalRPWorld world = StendhalRPWorld.get();
		StendhalRPZone zone = (StendhalRPZone) world.getRPZone(this.getID());
		// create a new grown item
		Item grownItem = world.getRuleManager().getEntityManager().getItem(growingItemName);
		grownItem.setPlantGrower(this);
		grownItem.setx(this.getx());
		grownItem.sety(this.gety());
		
		zone.assignRPObjectID(grownItem);
		zone.add(grownItem);
		hasPickableFruit = true;
	}
	
	public void setToFullGrowth() {
		if (!hasPickableFruit) {
			growNewFruit();
		}
	}

	@Override
	public boolean isObstacle() {
		// The player can walk over the PlantGrower.
		return false;
	}
	
	public void onTurnReached(int currentTurn, String message) {
		growNewFruit();
	}

}
