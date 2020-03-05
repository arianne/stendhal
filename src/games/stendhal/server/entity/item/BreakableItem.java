/***************************************************************************
 *                   (C) Copyright 2018 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.item;

import java.util.LinkedHashMap;
import java.util.Map;

import games.stendhal.common.Rand;

/**
 * An item that wears & breaks.
 */
public class BreakableItem extends Item {

	private static final Map<String, Double> conditions = new LinkedHashMap<String, Double>() {{
		put("new", 1.0);
		put("like new", 0.75);
		put("used", 0.5);
		put("well used", 0.25);
		put("very worn", 0.0);
	}};


	public BreakableItem(String name, String clazz, String subclass, Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	public BreakableItem(final BreakableItem item) {
		super(item);
	}

	@Override
	public String getDescription() {
		return super.getDescription() + " It is " + getConditionName() + ".";
	}

	/**
	 * Increment number of times used.
	 */
	@Override
	public void deteriorate() {
		put("uses", getUses() + 1);
	}

	public String getConditionName() {
		final Double condition = getCondition();
		for (final String conditionName: conditions.keySet()) {
			if (condition >= conditions.get(conditionName)) {
				return conditionName;
			}
		}

		return "about to break";
	}

	private double getCondition() {
		return 1 - (getUses() / (double) getDurability());
	}

	/**
	 * Checks if the item has no uses remaining.
	 *
	 * @return
	 * 		<code>true</code> if uses are as much or more than base_uses.
	 */
	public boolean isBroken() {
		final double condition = getCondition();
		if (condition >= 0) {
			return false;
		}

		final int chanceOfBreak;
		if (condition < -0.75) {
			chanceOfBreak = 25;
		} else if (condition < -0.5) {
			chanceOfBreak = 10;
		} else if (condition < -0.25) {
			chanceOfBreak = 5;
		} else if (condition < -0.15) {
			chanceOfBreak = 2;
		} else {
			chanceOfBreak = 1;
		}

		return Rand.randUniform(1, 100) <= chanceOfBreak;
	}

	public int getDurability() {
		return getInt("durability");
	}

	public int getUses() {
		return getInt("uses");
	}
}
