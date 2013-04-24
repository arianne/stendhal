/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.rp.equipping.validator;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.rp.equipping.EquipmentActionData;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;

/**
 * checks that the sum of the source items is at least the requested quantity
 *
 * @author hendrik
 */
public class IsQuantityInRangeOfSourceItems implements Validator {

	@Override
	public boolean validate(EquipmentActionData data) {
		// -1 means everything
		int quantity = data.getQuantity();
		if (quantity <= 0) {
			return true;
		}

		int sum = getSumOfSourceItems(data);
		if (sum < quantity) {
			data.setErrorMessage("Sorry, you don't have enough " + Grammar.plural(data.getItemName()));
			return false;
		}

		return true;
	}

	/**
	 * gets the quantity sum of all source items
	 *
	 * @param data EquipmentActionData
	 * @return sum
	 */
	private int getSumOfSourceItems(EquipmentActionData data) {
		int sum = 0;
		for (Entity entity : data.getSourceItems()) {
			if (entity instanceof StackableItem) {
				sum = sum + ((StackableItem) entity).getQuantity();
			} else if (entity instanceof Item) {
				sum++;
			}
		}
		return sum;
	}

}
