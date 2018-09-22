/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package utilities.RPClass;

import java.util.HashMap;
import java.util.Map;

import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.item.StatusHealer;

public class ConsumableTestHelper {

	public static ConsumableItem createEater(final String name) {
		ItemTestHelper.generateRPClasses();
		Map<String, String> attributes = new HashMap<>();
		attributes.put("amount", "1");
		attributes.put("regen", "1");
		attributes.put("frequency", "1");
		return new ConsumableItem(name, "class", "subclass", attributes);
	}

	public static ConsumableItem createImmunizer(final String name) {
		ItemTestHelper.generateRPClasses();
		Map<String, String> attributes = new HashMap<>();
		attributes.put("amount", "1");
		attributes.put("regen", "0");
		attributes.put("frequency", "1");
		attributes.put("immunization", "poisoned");
		return new StatusHealer(name, "class", "subclass", attributes);
	}
}
