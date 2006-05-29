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

public class PlantGrower extends Entity {

	/**
	 * How much of the next fruit is ready, as a value between 0 and 1.
	 */
	protected double ripeness;
	
	// TODO: make variable
	private String growingItemName = "arandula";
	
	// TODO: make variable
	protected int turnsForRegrow = 250;
	
	public PlantGrower(RPObject object) throws AttributeNotFoundException {
		super(object);
		setCollides(false);
		// TODO: consider making growth persistent
		put("type", "plant_grower");
		//update();
	}

	public PlantGrower() throws AttributeNotFoundException {
		super();
		// TODO: This doesn't work. Still can't walk over the plant grower.
		setCollides(false);
		put("type", "plant_grower");
	}

	@Override
	public static void generateRPClass() {
		RPClass grower = new RPClass("plant_grower");
		grower.isA("entity");
		grower.add("class", RPClass.STRING);
	}

	public void getArea(Rectangle2D rect, double x, double y) {
		rect.setRect(x, y, 1, 1);
	}

	protected boolean canGrowNewFruit() {
		StendhalRPZone zone = (StendhalRPZone) world.getRPZone(this.getID());
		for (Item item: zone.getItemsOnGround().keySet()) {
			if (item.getName().equals(growingItemName) && item.getx() == this.getx() && 	item.gety() == this.gety()) {
				// don't regrow until someone picks the last grown item up.
				return true;
			}
		}
		return false;
	}
	
	protected void growNewFruit() {
		StendhalRPZone zone = (StendhalRPZone) world.getRPZone(this.getID());
		// create a new grown item
		Item grownItem = world.getRuleManager().getEntityManager().getItem(growingItemName);
		grownItem.setx(this.getx());
		grownItem.sety(this.gety());
		//logger.warn("entity set to " + x + "x" + y);
		
		zone.assignRPObjectID(grownItem);
		//logger.warn("entity has valid id: " + entity.getID());
		
		zone.add(grownItem);
		//world.modify(this);
		
	}
	
	public void regrow() {
		if (canGrowNewFruit()) {
			ripeness += 1.0f / turnsForRegrow;
			
			// TODO: add some randomization
			if (ripeness > 1.0f) {
				ripeness = 0.0f;
				growNewFruit();
			}
		}
	}

	@Override
	public String describe() {
		String text = "You see a place where " + growingItemName
				+ " grows.";
		return (text);
	}

}
