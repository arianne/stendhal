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
package games.stendhal.server.entity.slot;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.entity.Entity;

/**
 * Keeps track of "personal chests" which are an interface to the bank slots.
 *
 * @author hendrik
 */
public class BankAccessorManager {

	/** The singleton instance. */
	private static BankAccessorManager instance;

	private final HashMap<Banks, List<Entity>> accessors;


	/**
	 * Gets the BankAccessorManager.
	 *
	 * @return BankAccessorManager
	 */
	public static BankAccessorManager get() {
		if (instance == null) {
			instance = new BankAccessorManager();
		}

		return instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private BankAccessorManager() {
		accessors = new HashMap<Banks, List<Entity>>();
	}

	/**
	 * Adds an accessor for this bank.
	 *
	 * @param bank
	 *            Banks
	 * @param entity
	 *            Accessor
	 */
	public void add(final Banks bank, final Entity entity) {
		final List<Entity> bankAccess = getListAddingUnknownBanks(bank);
		if (!bankAccess.contains(entity)) {
			bankAccess.add(entity);
		}
	}

	/**
	 * Gets the list of accessors for the specified bank.
	 * <p> In case the bank is unknown, an empty list is automatically created
	 *
	 * @param bank
	 *            Banks
	 * @return list of accessors
	 */
	private List<Entity> getListAddingUnknownBanks(final Banks bank) {
		List<Entity> bankAccess = accessors.get(bank);
		if (bankAccess == null) {
			bankAccess = new LinkedList<Entity>();
			accessors.put(bank, bankAccess);
		}
		return bankAccess;
	}

	/**
	 * gets the list of accessors for think bank.
	 *
	 * @param bank
	 *            Banks
	 * @return list of accessors or an empty list if this bank is unknown
	 */
	protected List<Entity> get(final Banks bank) {

		// If the visibility of this method should be raised,
		// please return only a copy of this list
		return getListAddingUnknownBanks(bank);
	}
}
