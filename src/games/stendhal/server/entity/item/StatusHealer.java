/***************************************************************************
 *                (C) Copyright 2003-2018 - Arianne                        *
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

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import games.stendhal.server.entity.status.StatusType;

/**
 * Consumable item that can be used to cure/prevent a single or multiple status effects.
 *
 * @author AntumDeluge
 */
public class StatusHealer extends ConsumableItem {
	private final Set<StatusType> immunizations = EnumSet.noneOf(StatusType.class);

	/**
	 * Constructor: Creates a status-healing item.
	 *
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public StatusHealer(final String name, final String clazz, final String subclass,
			final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);

		if (attributes.containsKey("immunization")) {
			setImmunization(attributes.get("immunization"));
		}
	}

	/**
	 * Copy constructor.
	 *
	 * @param item
	 * 			Item to copy.
	 */
	public StatusHealer(StatusHealer item) {
		super(item);

		immunizations.addAll(item.getImmunizations());
	}

	/**
	 * Initializes list of immunizations.
	 *
	 * @param i
	 * 			List of status effect names to be added.
	 */
	private void setImmunization(final String i) {
		// Comma-separated list
		final List<String> iNames = Arrays.asList(i.split(","));
		for (String name: iNames) {
			try {
				immunizations.add(StatusType.valueOf(name.toUpperCase()));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Set<StatusType> getImmunizations() {
		return immunizations;
	}
}
