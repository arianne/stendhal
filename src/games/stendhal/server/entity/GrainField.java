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

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.events.TurnNotifier;
import games.stendhal.server.events.UseListener;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;

/**
 * A grain field can be harvested by players who have a scythe.
 * After that, it will slowly regrow; there are several regrowing
 * steps in which the graphics will change to show the progress.
 * 
 * @author daniel
 */
public class GrainField extends PlantGrower implements UseListener {
	
	/** How long it takes for one regrowing step */
	private static final int GROWING_RATE = 3000;
	
	/** How many regrowing steps are needed before one can harvest again */
	public static final int RIPE = 5;
	
	private int ripeness;

	public static void generateRPClass() {
		RPClass grainFieldClass = new RPClass("grain_field");
		grainFieldClass.isA("plant_grower");
		grainFieldClass.add("ripeness", RPClass.BYTE);
	}

	public GrainField(RPObject object) throws AttributeNotFoundException {
		super(object, null, GROWING_RATE);
		put("type", "grain_field");
		update();
	}

	public GrainField() throws AttributeNotFoundException {
		super(null, GROWING_RATE);
		put("type", "grain_field");
	}

	@Override
	public void update() {
		super.update();
		if (has("ripeness")) {
			ripeness = getInt("ripeness");
		}
	}

	private void setRipeness(int ripeness) {
		this.ripeness = ripeness;
		put("ripeness", ripeness);
	}

	private int getRipeness() {
		return ripeness;
	}

	@Override
	protected void growNewFruit() {
		setRipeness(ripeness + 1);
		if (ripeness < RIPE) {
			TurnNotifier.get().notifyInTurns(getRandomTurnsForRegrow(), this, null);
		}
		notifyWorldAboutChanges();
	}

	@Override
	public String describe() {
		String text;
		switch (getRipeness()) {
			case 0:
				text = "You see a grain field that has just been harvested.";
				break;
			case RIPE:
				text = "You see a ripe grain field.";
				break;
			default:
				text = "You see an unripe grain field.";
				break;
		}
		return text;
	}
	
	@Override
	public void getArea(Rectangle2D rect, double x, double y) {
		rect.setRect(x, y + 1, 1, 1);
	}

	/**
	 * Is called when a player tries to harvest this grain field.
	 */
	public void onUsed(RPEntity entity) {
		if (entity.nextTo(this, 0.25)) {
			if (getRipeness() == RIPE) {
				if (entity.isEquipped("scythe")) {
					onFruitPicked(null);
					Item grain = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("grain");
					entity.equip(grain, true);
				} else if (entity instanceof Player) {
					((Player) entity).sendPrivateText("You need a scythe to harvest grain fields.");
				}
			} else if (entity instanceof Player) {
				((Player) entity).sendPrivateText("This grain is not yet ripe enough to harvest.");
			}
		}
	}
	
	@Override
	public void onFruitPicked(Item picked) {
		super.onFruitPicked(picked);
		setRipeness(0);
		notifyWorldAboutChanges();
	}

	@Override
	public void setToFullGrowth() {
		setRipeness(RIPE);
		// don't grow anything new until someone harvests
		TurnNotifier.get().dontNotify(this, null);
	}

}
