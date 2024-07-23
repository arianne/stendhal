/***************************************************************************
 *                    Copyright © 2018-2024 - Stendhal                     *
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
import games.stendhal.server.entity.RPEntity;


/**
 * An item that wears & breaks.
 *
 * TODO:
 *   - create unit test
 *   - convert to "broken" item when "breaks"
 */
public class BreakableWeapon extends Weapon {

	/** Item condition descriptions based on number of uses. */
	private static final Map<String, Double> conditions = new LinkedHashMap<String, Double>() {{
		put("new", 1.0);
		put("like new", 0.75);
		put("used", 0.5);
		put("well used", 0.25);
		put("very worn", 0.0);
	}};

	/** Property denoting whether player has been notified that item is about to break. */
	private boolean notified = false;


	/**
	 * Creates a breakable weapon item.
	 *
	 * @param name
	 *   Item name.
	 * @param clazz
	 *   Item class.
	 * @param subclass
	 *   Item subclass.
	 * @param attributes
	 *   Item attributes.
	 */
	public BreakableWeapon(String name, String clazz, String subclass, Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * Copy constructor.
	 *
	 * @param item
	 *   Item to be copied.
	 */
	public BreakableWeapon(final BreakableWeapon item) {
		super(item);
	}

	@Override
	public String getDescription() {
		return super.getDescription() + " It is " + getConditionName() + ".";
	}

	/**
	 * Sets the item's condition back to new (0 uses).
	 */
	@Override
	public void repair() {
		put("uses", 0);
		// reset so player will be notified when item is about to break again
		notified = false;
	}

	/**
	 * Checks the used state of the item.
	 *
	 * @return
	 * {@code true} if the item has been used and is deteriorated.
	 */
	public boolean isUsed() {
		return getUses() > 0;
	}

	/**
	 * Increments number of times item has been used.
	 */
	@Override
	public void deteriorate() {
		put("uses", getUses() + 1);
	}

	/**
	 * Increments number of times item has been used and notifies user if about to break.
	 *
	 * @param user
	 *   Entity using item when deterioration takes place.
	 */
	@Override
	public void deteriorate(final RPEntity user) {
		deteriorate();

		if (getCondition() <= 0) {
			onWeakened(user);
		}
	}

	/**
	 * Notifies user if item is about to break.
	 *
	 * @param user
	 *   Entity using item.
	 */
	private void onWeakened(final RPEntity user) {
		if (!notified) {
			user.sendPrivateText("Your " + getName() + " is about to break.");
			notified = true;
		}
	}

	/**
	 * Retrieves condition description based on item's current state.
	 *
	 * @return
	 *   Condition description.
	 */
	public String getConditionName() {
		final Double condition = getCondition();
		for (final String conditionName: conditions.keySet()) {
			if (condition >= conditions.get(conditionName)) {
				return conditionName;
			}
		}

		return "about to break";
	}

	/**
	 * Retrieves numeric value representing item's current condition state.
	 *
	 * @return
	 *   Condition state.
	 */
	private double getCondition() {
		return 1 - (getUses() / (double) getDurability());
	}

	/**
	 * Checks if the item has no uses remaining.
	 *
	 * @return
	 *   {@code true} if item's condition state is 0 or less.
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

	/**
	 * Retrieves item's durability.
	 *
	 * @return
	 *   Number of uses before chance of break.
	 */
	public int getDurability() {
		return getInt("durability");
	}

	/**
	 * Retrieves number of times item has been used.
	 *
	 * @return
	 *   Number of times used.
	 */
	public int getUses() {
		return getInt("uses");
	}
}
