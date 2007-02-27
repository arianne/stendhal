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
package games.stendhal.server.entity.spawner;

import games.stendhal.server.entity.item.Item;
import games.stendhal.server.events.TurnNotifier;
import java.awt.geom.Rectangle2D;

import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;

/**
 * This respwan point has to be "used" to get the item. After that,
 * it will slowly regrow; there are several regrowing
 * steps in which the graphics will change to show the progress.
 * 
 * @author daniel, hendrik
 */
public abstract class GrowingPassiveEntityRespawnPoint extends PassiveEntityRespawnPoint {
	
	/** How long it takes for one regrowing step */
	private static final int GROWING_RATE = 3000;
	
	private int ripeness;
	private int maxRipeness;

	public static void generateRPClass() {
		RPClass grainFieldClass = new RPClass("growing_entity_spawner");
		grainFieldClass.isA("plant_grower");
		grainFieldClass.add("action_name", RPClass.STRING);
		grainFieldClass.add("max_ripeness", RPClass.BYTE);
		grainFieldClass.add("width", RPClass.BYTE);
		grainFieldClass.add("height", RPClass.BYTE);
		grainFieldClass.add("ripeness", RPClass.BYTE);
	}

	private void init(String clazz, String actionName, int maxRipeness, int width, int height) {
		this.maxRipeness = maxRipeness;
		put("type", "growing_entity_spawner");
		put("class", clazz);
		put("action_name", actionName);
		put("max_ripeness", maxRipeness);
		put("width", width);
		put("height", height);
	}
	
	public GrowingPassiveEntityRespawnPoint(RPObject object, String type, String actionName, int maxRipeness, int width, int height) {
		super(object, null, GROWING_RATE);
		init(type, actionName, maxRipeness, width, height);
		update();
	}

	public GrowingPassiveEntityRespawnPoint(String type, String actionName, int maxRipeness, int width, int height) {
		super(null, GROWING_RATE);
		init(type, actionName, maxRipeness, width, height);
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

	protected int getRipeness() {
		return ripeness;
	}

	@Override
	protected void growNewFruit() {
		setRipeness(ripeness + 1);
		if (ripeness < maxRipeness) {
			TurnNotifier.get().notifyInTurns(getRandomTurnsForRegrow(), this, null);
		}
		notifyWorldAboutChanges();
	}

	@Override
	public void getArea(Rectangle2D rect, double x, double y) {
		rect.setRect(x, y + getInt("height") - 1, 1, 1);
	}

	@Override
	public void onFruitPicked(Item picked) {
		super.onFruitPicked(picked);
		setRipeness(0);
		notifyWorldAboutChanges();
	}

	@Override
	public void setToFullGrowth() {
		setRipeness(maxRipeness);
		// don't grow anything new until someone harvests
		TurnNotifier.get().dontNotify(this, null);
	}

}
