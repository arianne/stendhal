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

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;

public class SheepFood extends PlantGrower {
	private int amount;

	@Override
	public static void generateRPClass() {
		RPClass food = new RPClass("food");
		food.isA("plant_grower");
		food.add("amount", RPClass.BYTE);
	}

	public SheepFood(RPObject object) throws AttributeNotFoundException {
		super(object);
		put("type", "food");
		update();
	}

	public SheepFood() throws AttributeNotFoundException {
		super();
		put("type", "food");
	}

	@Override
	public void update() {
		super.update();
		if (has("amount"))
			amount = getInt("amount");
	}

	public void setAmount(int amount) {
		this.amount = amount;
		put("amount", amount);
	}

	public int getAmount() {
		return amount;
	}

	protected boolean canGrowNewFruit() {
		return amount < 5;
	}
	
	protected int getTurnsForRegrow() {
		return 2000;
	}

	protected void growNewFruit() {
		setAmount(amount + 1);
		world.modify(this);
	}

	@Override
	public String describe() {
		String text = "You see a bush with " + getAmount()
				+ " fruits. Only sheep eat from this bush.";
		return (text);
	}
	
	@Override
	public boolean isCollisionable() {
		return true;
	}

}
