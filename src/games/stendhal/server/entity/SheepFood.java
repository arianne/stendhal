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

import java.awt.geom.Rectangle2D;

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;

public class SheepFood extends PlantGrower {
	private int amount;
	
	protected int turnsForRegrow = 2000;

	public SheepFood(RPObject object) throws AttributeNotFoundException {
		super(object);
		update();
	}

	public SheepFood() throws AttributeNotFoundException {
		super();
	}

	@Override
	public static void generateRPClass() {
		RPClass food = new RPClass("food");
		food.isA("entity");
		food.add("class", RPClass.STRING);
		food.add("amount", RPClass.BYTE);
	}

	@Override
	public void getArea(Rectangle2D rect, double x, double y) {
		rect.setRect(x, y, 1, 1);
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

	@Override
	protected boolean canGrowNewFruit() {
		return amount < 5;
	}
	
	@Override
	protected void growNewFruit() {
		setAmount(amount + 1);
		world.modify(this);
	}
	
	@Override
	public String describe() {
		String text = "You see a bush with " + getAmount()
				+ " fruits. Only sheeps eat from this bush.";
		return (text);
	}

}
